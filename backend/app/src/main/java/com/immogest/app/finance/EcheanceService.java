package com.immogest.app.finance;

import com.immogest.app.bail.api.BailResume;
import com.immogest.app.bail.api.LecteurBaux;
import com.immogest.app.finance.domain.Echeance;
import com.immogest.app.finance.domain.Imputation;
import com.immogest.app.finance.domain.Paiement;
import com.immogest.app.finance.infra.EcheanceRepository;
import com.immogest.app.finance.infra.ImputationRepository;
import com.immogest.app.finance.infra.PaiementRepository;
import com.immogest.app.shared.ActeurCourant;
import com.immogest.app.shared.ErreurMetier;
import com.immogest.app.tiers.api.LecteurTiers;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.transaction.annotation.Transactional;

/**
 * Moteur d'échéances. Idempotent : clé unique (bail, début de période) en base, verrou par bail, et chaque passage ne fait que
 * rattraper l'écart entre ce qui devrait exister et ce qui existe. Appelé chaque nuit et à l'activation d'un bail (événement).
 */
@Service
@EnableConfigurationProperties(FinanceProperties.class)
public class EcheanceService {

    private static final Logger LOG = LoggerFactory.getLogger(EcheanceService.class);
    private static final ZoneId DOUALA = ZoneId.of("Africa/Douala");
    private static final java.util.Set<StatutEcheance> OUVERTES =
            EnumSet.of(StatutEcheance.A_VENIR, StatutEcheance.DUE, StatutEcheance.PARTIELLE, StatutEcheance.EN_RETARD);

    private final LecteurBaux baux;
    private final LecteurTiers tiers;
    private final EcheanceRepository echeances;
    private final PaiementRepository paiements;
    private final ImputationRepository imputations;
    private final JdbcTemplate jdbc;
    private final TransactionTemplate transaction;
    private final ActeurCourant acteurCourant;
    private final FinanceProperties proprietes;
    private final Clock horloge;
    private final org.springframework.context.ApplicationEventPublisher evenements;

    EcheanceService(LecteurBaux baux, LecteurTiers tiers, EcheanceRepository echeances, PaiementRepository paiements,
            ImputationRepository imputations, JdbcTemplate jdbc, TransactionTemplate transaction, ActeurCourant acteurCourant,
            FinanceProperties proprietes, Clock horloge,
            org.springframework.context.ApplicationEventPublisher evenements) {
        this.evenements = evenements;
        this.baux = baux;
        this.tiers = tiers;
        this.echeances = echeances;
        this.paiements = paiements;
        this.imputations = imputations;
        this.jdbc = jdbc;
        this.transaction = transaction;
        this.acteurCourant = acteurCourant;
        this.proprietes = proprietes;
        this.horloge = horloge;
    }

    // ================================================================== moteur

    /** Passage complet : échéances de tous les baux signés, avance, ajustements, puis statuts de toutes les échéances ouvertes. */
    public BilanGeneration executer() {
        LocalDate aujourdhui = aujourdhui();
        List<BailResume> liste = baux.bauxSignesCommencesAvant(horizon(aujourdhui));
        int creees = 0;
        int ajustees = 0;
        int avances = 0;
        for (BailResume bail : liste) {
            try {
                BilanGeneration b = transaction.execute(s -> traiter(bail, aujourdhui));
                creees += b.echeancesCreees();
                ajustees += b.echeancesAjustees();
                avances += b.avancesImputees();
            } catch (RuntimeException e) {
                LOG.error("Échéances du bail {} : échec ({})", bail.numero(), e.getClass().getSimpleName());
            }
        }
        Integer statuts = transaction.execute(s -> rafraichirStatuts(aujourdhui));
        LOG.info("Échéances : {} bail(aux), {} créée(s), {} ajustée(s), {} statut(s) mis à jour", liste.size(), creees, ajustees, statuts);
        return new BilanGeneration(liste.size(), creees, ajustees, avances, statuts == null ? 0 : statuts);
    }

    /** Pour un bail (à son activation). Sans effet pour un brouillon ou un bail inconnu. */
    public BilanGeneration genererPourBail(UUID bailId) {
        LocalDate aujourdhui = aujourdhui();
        return baux.bail(bailId).filter(b -> !"BROUILLON".equals(b.statut()))
                .map(b -> transaction.execute(s -> traiter(b, aujourdhui)))
                .orElse(new BilanGeneration(0, 0, 0, 0, 0));
    }

    private BilanGeneration traiter(BailResume bail, LocalDate aujourdhui) {
        verrouiller(bail.id());
        int mois = Periodicite.mois(bail.periodicite());
        long mensuel = bail.loyer() + bail.charges();
        List<CalculEcheances.Periode> attendues = CalculEcheances.periodes(bail.dateDebut(), bail.dateFinEffective(), mois, mensuel,
                bail.jourEcheance(), horizon(aujourdhui));

        Map<LocalDate, Echeance> existantes = new HashMap<>();
        for (Echeance e : echeances.findByBailIdOrderByPeriodeDebutAsc(bail.id())) {
            existantes.put(e.getPeriodeDebut(), e);
        }
        int creees = 0;
        int ajustees = 0;
        for (CalculEcheances.Periode p : attendues) {
            Echeance e = existantes.get(p.debut());
            if (e == null) {
                echeances.save(new Echeance(bail.id(), p.debut(), p.fin(), p.dateEcheance(), p.montant(),
                        CalculEcheances.statut(p.montant(), 0, p.dateEcheance(), aujourdhui, proprietes.delaiGraceJours())));
                creees++;
            } else if (e.getStatut() != StatutEcheance.ANNULEE && e.getMontantPaye() == 0
                    && (e.getMontant() != p.montant() || !e.getPeriodeFin().equals(p.fin()))) {
                e.ajuster(p.fin(), p.montant());
                ajustees++;
            }
        }
        // Périodes entièrement postérieures à la sortie (bail résilié) et sans aucun paiement : annulées, jamais supprimées
        if (bail.dateFinEffective() != null) {
            for (Echeance e : existantes.values()) {
                if (e.getPeriodeDebut().isAfter(bail.dateFinEffective()) && e.getMontantPaye() == 0 && e.getStatut() != StatutEcheance.ANNULEE) {
                    e.setStatut(StatutEcheance.ANNULEE);
                    ajustees++;
                }
            }
        }
        echeances.flush();
        int avances = imputerAvance(bail, mensuel, aujourdhui);
        recalculerStatuts(bail.id(), aujourdhui);
        return new BilanGeneration(1, creees, ajustees, avances, 0);
    }

    /**
     * Crédits à imputer : l'avance versée à la signature (mois d'avance × loyer+charges) puis les encaissements, du plus ancien
     * au plus récent, sur les échéances les plus anciennes encore ouvertes. Un paiement supérieur au dû reste en crédit et
     * s'applique aux échéances créées plus tard. Les paiements contre-passés n'imputent plus rien.
     */
    private int imputerAvance(BailResume bail, long mensuel, LocalDate aujourdhui) {
        if (bail.moisAvance() > 0 && paiements.findByBailIdAndType(bail.id(), Paiement.AVANCE).isEmpty()) {
            paiements.saveAndFlush(new Paiement(bail.id(), Paiement.AVANCE, bail.moisAvance() * mensuel, aujourdhui));
        }
        int n = 0;
        for (Paiement paiement : paiements.creditables(bail.id())) {
            long credit = paiement.getMontant() - imputations.totalImpute(paiement.getId());
            for (Echeance e : echeances.ouvertes(bail.id())) {
                if (credit <= 0) {
                    break;
                }
                long part = Math.min(credit, e.reste());
                imputations.save(new Imputation(paiement.getId(), e.getId(), part));
                e.imputer(part);
                credit -= part;
                n++;
            }
        }
        return n;
    }

    /** Imputation des crédits puis recalcul des statuts du bail ; l'appelant tient la transaction et le verrou du bail. */
    void appliquerCredits(BailResume bail, LocalDate aujourdhui) {
        echeances.flush();
        imputerAvance(bail, bail.loyer() + bail.charges(), aujourdhui);
        recalculerStatuts(bail.id(), aujourdhui);
    }

    void recalculerStatuts(UUID bailId, LocalDate aujourdhui) {
        echeances.flush();
        for (Echeance e : echeances.findByBailIdOrderByPeriodeDebutAsc(bailId)) {
            if (e.getStatut() == StatutEcheance.ANNULEE) {
                continue;
            }
            StatutEcheance statut = CalculEcheances.statut(e.getMontant(), e.getMontantPaye(), e.getDateEcheance(), aujourdhui,
                    proprietes.delaiGraceJours());
            if (statut == StatutEcheance.PAYEE && e.getStatut() != StatutEcheance.PAYEE) {
                evenements.publishEvent(new EvenementsPaiement.EcheancePayee(e.getId(), bailId)); // → quittance
            }
            e.setStatut(statut);
        }
    }

    void verrouiller(UUID bailId) {
        jdbc.queryForObject("select pg_advisory_xact_lock(?)", Object.class, bailId.getMostSignificantBits() ^ bailId.getLeastSignificantBits());
    }

    private int rafraichirStatuts(LocalDate aujourdhui) {
        int n = 0;
        for (Echeance e : echeances.findByStatutIn(OUVERTES)) {
            StatutEcheance statut = CalculEcheances.statut(e.getMontant(), e.getMontantPaye(), e.getDateEcheance(), aujourdhui,
                    proprietes.delaiGraceJours());
            if (statut != e.getStatut()) {
                e.setStatut(statut);
                n++;
            }
        }
        return n;
    }

    // ================================================================== lecture

    @Transactional(readOnly = true)
    public Page<EcheanceDto> lister(UUID bailId, StatutEcheance statut, Pageable pageable) {
        if (acteurCourant.acteur().filter(a -> a.aLeRole("LOCATAIRE")).isPresent()) {
            // Un locataire ne lit que les échéances d'un de ses baux : tout autre cas est « introuvable »
            UUID moi = acteurCourant.acteur().flatMap(a -> tiers.locataireDeUtilisateur(a.id())).orElse(null);
            boolean sien = bailId != null && moi != null && baux.bail(bailId).map(b -> moi.equals(b.locataireId())).orElse(false);
            if (!sien) {
                throw new ErreurMetier("BAIL_INTROUVABLE", "Bail introuvable", HttpStatus.NOT_FOUND);
            }
        }
        return echeances.rechercher(bailId, statut, pageable).map(EcheanceService::versDto);
    }

    // ---- lecture pour le tableau de bord

    /**
     * Attendu et encaissé par mois d'échéance, du mois de {@code premier} à celui de {@code dernier} inclus (mois sans échéance : zéros).
     * Les échéances annulées (après la sortie) ne comptent pas.
     */
    @Transactional(readOnly = true)
    public List<MoisSynthese> syntheseMensuelle(LocalDate premier, LocalDate dernier) {
        LocalDate debut = premier.withDayOfMonth(1);
        LocalDate fin = dernier.withDayOfMonth(1).plusMonths(1).minusDays(1);
        Map<LocalDate, long[]> parMois = new java.util.HashMap<>();
        jdbc.query("""
                SELECT date_trunc('month', date_echeance)::date AS mois, SUM(montant) AS attendu, SUM(montant_paye) AS encaisse
                FROM fin_echeance WHERE statut <> 'ANNULEE' AND date_echeance BETWEEN ? AND ? GROUP BY 1
                """, rs -> {
                    parMois.put(rs.getDate("mois").toLocalDate(), new long[] {rs.getLong("attendu"), rs.getLong("encaisse")});
                }, java.sql.Date.valueOf(debut), java.sql.Date.valueOf(fin));
        List<MoisSynthese> resultat = new java.util.ArrayList<>();
        for (LocalDate m = debut; !m.isAfter(dernier.withDayOfMonth(1)); m = m.plusMonths(1)) {
            long[] v = parMois.getOrDefault(m, new long[] {0, 0});
            resultat.add(new MoisSynthese(m, v[0], v[1]));
        }
        return resultat;
    }

    /** Arriérés (échues, non soldées) regroupés par bail, du plus gros au plus petit. */
    @Transactional(readOnly = true)
    public List<ArriereBail> arrieresParBail() {
        return jdbc.query("""
                SELECT bail_id, SUM(montant - montant_paye) AS total, COUNT(*) AS n, MIN(date_echeance) AS plus_ancienne
                FROM fin_echeance WHERE statut <> 'ANNULEE' AND montant_paye < montant AND date_echeance < ?
                GROUP BY bail_id ORDER BY total DESC
                """, (rs, i) -> new ArriereBail(rs.getObject("bail_id", UUID.class), rs.getLong("total"), rs.getInt("n"),
                rs.getDate("plus_ancienne").toLocalDate()), java.sql.Date.valueOf(aujourdhui()));
    }

    // ---- lecture pour le module relance

    /** Échéances ouvertes dont la date d'échéance est au plus tard {@code date} (rappels avant échéance compris). */
    @Transactional(readOnly = true)
    public List<EcheanceOuverte> ouvertesJusquA(LocalDate date) {
        return echeances.ouvertesJusquA(date).stream().map(EcheanceService::ouverte).toList();
    }

    /**
     * Arriérés : échéances échues avant aujourd'hui et pas entièrement payées, paginées.
     *
     * @param bailIds restreint à ces baux ({@code null} = tous)
     */
    @Transactional(readOnly = true)
    public Page<EcheanceOuverte> arrieres(Collection<UUID> bailIds, Pageable pageable) {
        if (bailIds != null && bailIds.isEmpty()) {
            return Page.empty(pageable);
        }
        return echeances.arrieres(aujourdhui(), bailIds != null, bailIds == null ? List.of(new UUID(0, 0)) : bailIds, pageable)
                .map(EcheanceService::ouverte);
    }

    @Transactional(readOnly = true)
    public List<EcheanceOuverte> arrieresDuBail(UUID bailId) {
        return echeances.arrieresDuBail(bailId, aujourdhui()).stream().map(EcheanceService::ouverte).toList();
    }

    @Transactional(readOnly = true)
    public java.util.Optional<EcheanceOuverte> echeance(UUID echeanceId) {
        return echeances.findById(echeanceId).map(EcheanceService::ouverte);
    }

    private static EcheanceOuverte ouverte(Echeance e) {
        return new EcheanceOuverte(e.getId(), e.getBailId(), e.getPeriodeDebut(), e.getPeriodeFin(), e.getDateEcheance(), e.getMontant(),
                e.reste(), e.getStatut());
    }

    private static EcheanceDto versDto(Echeance e) {
        return new EcheanceDto(e.getId(), e.getBailId(), e.getPeriodeDebut(), e.getPeriodeFin(), e.getDateEcheance(), e.getMontant(),
                e.getMontantPaye(), e.reste(), e.getStatut());
    }

    // ================================================================== divers

    private LocalDate horizon(LocalDate aujourdhui) {
        return aujourdhui.plusDays(proprietes.horizonJours());
    }

    public LocalDate aujourdhui() {
        return LocalDate.now(horloge.withZone(DOUALA));
    }

    /** Mois couverts par une échéance selon la périodicité du bail. */
    static final class Periodicite {
        static int mois(String periodicite) {
            return switch (periodicite) {
                case "MENSUELLE" -> 1;
                case "TRIMESTRIELLE" -> 3;
                case "ANNUELLE" -> 12;
                default -> throw new IllegalStateException("Périodicité inconnue : " + periodicite);
            };
        }
    }
}
