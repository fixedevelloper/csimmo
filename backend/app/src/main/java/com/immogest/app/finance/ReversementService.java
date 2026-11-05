package com.immogest.app.finance;

import com.immogest.app.bail.api.BailResume;
import com.immogest.app.bail.api.LecteurBaux;
import com.immogest.app.finance.ReversementDtos.DonneesReleve;
import com.immogest.app.finance.ReversementDtos.LigneDepense;
import com.immogest.app.finance.ReversementDtos.LigneEncaissement;
import com.immogest.app.finance.ReversementDtos.LigneRecap;
import com.immogest.app.finance.ReversementDtos.PaiementReversementSaisie;
import com.immogest.app.finance.ReversementDtos.ReversementDto;
import com.immogest.app.finance.domain.Depense;
import com.immogest.app.finance.domain.Reversement;
import com.immogest.app.finance.infra.DepenseRepository;
import com.immogest.app.finance.infra.ReversementRepository;
import com.immogest.app.patrimoine.api.LecteurLots;
import com.immogest.app.patrimoine.api.LotResume;
import com.immogest.app.shared.ActeurCourant;
import com.immogest.app.shared.ErreurMetier;
import com.immogest.app.shared.Montants;
import com.immogest.app.shared.RenduPdf;
import com.immogest.app.stockage.FichierDto;
import com.immogest.app.stockage.StockageService;
import com.immogest.app.tiers.api.LecteurTiers;
import com.immogest.app.tiers.api.MandatResume;
import jakarta.persistence.criteria.Predicate;
import java.io.ByteArrayInputStream;
import java.time.Clock;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Reversements mensuels aux propriétaires tiers (règles 8 et 9) : encaissé − honoraires − dépenses imputées.
 *
 * <p>« Encaissé » = ce qui a été imputé à des échéances pendant le mois (date d'imputation, fuseau Africa/Douala), net des contre-passations.
 * Cette date ne change jamais après coup : un mois clos reste stable, et les corrections tombent dans le mois où elles ont lieu.
 * Loyers et charges sont distingués au prorata des conditions du bail ; les honoraires portent sur la part « loyers » seulement, au taux du
 * mandat en vigueur le jour de l'encaissement.
 */
@Service
public class ReversementService {

    private static final String ZONE = "Africa/Douala";
    private static final Set<String> MODES = Set.of("VIREMENT", "MOBILE_MONEY", "ESPECES", "CHEQUE");

    private final ReversementRepository reversements;
    private final DepenseRepository depenses;
    private final EcheanceService moteur;
    private final LecteurBaux baux;
    private final LecteurLots lots;
    private final LecteurTiers tiers;
    private final RenduPdf pdf;
    private final StockageService stockage;
    private final ActeurCourant acteurCourant;
    private final ApplicationEventPublisher evenements;
    private final JdbcTemplate jdbc;
    private final Clock horloge;

    ReversementService(ReversementRepository reversements, DepenseRepository depenses, EcheanceService moteur, LecteurBaux baux, LecteurLots lots,
            LecteurTiers tiers, RenduPdf pdf, StockageService stockage, ActeurCourant acteurCourant, ApplicationEventPublisher evenements,
            JdbcTemplate jdbc, Clock horloge) {
        this.reversements = reversements;
        this.depenses = depenses;
        this.moteur = moteur;
        this.baux = baux;
        this.lots = lots;
        this.tiers = tiers;
        this.pdf = pdf;
        this.stockage = stockage;
        this.acteurCourant = acteurCourant;
        this.evenements = evenements;
        this.jdbc = jdbc;
        this.horloge = horloge;
    }

    // ================================================================== calcul

    record LigneBail(UUID bailId, String libelle, long loyers, long charges) {
    }

    record Calcul(long loyers, long charges, long honoraires, long depenses, boolean sansMandat, List<LigneBail> lignes, List<Depense> listeDepenses) {
        static final Calcul VIDE = new Calcul(0, 0, 0, 0, false, List.of(), List.of());

        Integer tauxMoyenBp() {
            return loyers <= 0 ? null : (int) arrondi(honoraires * 10_000, loyers);
        }
    }

    /** Calcule le mois pour tous les propriétaires qui ont eu de l'activité (encaissements ou dépenses). */
    Map<UUID, Calcul> calculerTous(LocalDate mois) {
        LocalDate premier = mois.withDayOfMonth(1);
        LocalDate dernier = premier.plusMonths(1).minusDays(1);
        record Groupe(UUID bailId, LocalDate jour, long total) {
        }
        List<Groupe> groupes = jdbc.query("""
                SELECT e.bail_id, (i.created_at AT TIME ZONE '%s')::date AS jour, SUM(i.montant) AS total
                FROM fin_imputation i JOIN fin_echeance e ON e.id = i.echeance_id
                WHERE (i.created_at AT TIME ZONE '%s')::date BETWEEN ? AND ?
                GROUP BY e.bail_id, jour HAVING SUM(i.montant) <> 0
                """.formatted(ZONE, ZONE), (rs, n) -> new Groupe(rs.getObject("bail_id", UUID.class), rs.getDate("jour").toLocalDate(), rs.getLong("total")),
                java.sql.Date.valueOf(premier), java.sql.Date.valueOf(dernier));

        class Cumul {
            long loyers;
            long charges;
            long honoraires;
            boolean sansMandat;
            final Map<UUID, long[]> parBail = new LinkedHashMap<>();
        }
        Map<UUID, Cumul> parProprietaire = new LinkedHashMap<>();
        Map<UUID, BailResume> cacheBaux = new HashMap<>();
        Map<UUID, LotResume> cacheLots = new HashMap<>();
        Map<String, Optional<MandatResume>> cacheMandats = new HashMap<>();
        Map<UUID, String> libelles = new HashMap<>();
        for (Groupe g : groupes) {
            BailResume bail = cacheBaux.computeIfAbsent(g.bailId(), id -> baux.bail(id).orElse(null));
            if (bail == null) {
                continue;
            }
            LotResume lot = cacheLots.computeIfAbsent(bail.lotId(), id -> lots.lot(id).orElse(null));
            if (lot == null || lot.proprietaireTiersId() == null) {
                continue; // immeuble géré en direct : pas de reversement
            }
            long total = bail.loyer() + bail.charges();
            long loyer = total == 0 ? g.total() : arrondi(g.total() * bail.loyer(), total);
            Cumul c = parProprietaire.computeIfAbsent(lot.proprietaireTiersId(), k -> new Cumul());
            c.loyers += loyer;
            c.charges += g.total() - loyer;
            Optional<MandatResume> mandat = cacheMandats.computeIfAbsent(lot.proprietaireTiersId() + "|" + g.jour(),
                    k -> tiers.mandatEnVigueur(lot.proprietaireTiersId(), g.jour()));
            if (mandat.isPresent()) {
                c.honoraires += arrondi(loyer * mandat.get().tauxHonorairesBp(), 10_000);
            } else {
                c.sansMandat = true;
            }
            long[] v = c.parBail.computeIfAbsent(bail.id(), k -> new long[2]);
            v[0] += loyer;
            v[1] += g.total() - loyer;
            libelles.computeIfAbsent(bail.id(), k -> "%s — %s".formatted(bail.numero(), lot.immeubleNom() + " " + lot.reference()
                    + tiers.locataire(bail.locataireId()).map(l -> " — " + l.nom()).orElse("")));
        }
        Map<UUID, List<Depense>> deps = new HashMap<>();
        for (Depense d : depenses.activesDuMois(premier, dernier)) {
            deps.computeIfAbsent(d.getProprietaireId(), k -> new ArrayList<>()).add(d);
        }
        Set<UUID> proprietaires = new java.util.LinkedHashSet<>(parProprietaire.keySet());
        proprietaires.addAll(deps.keySet());
        Map<UUID, Calcul> resultat = new LinkedHashMap<>();
        for (UUID p : proprietaires) {
            Cumul c = parProprietaire.getOrDefault(p, new Cumul());
            List<Depense> liste = deps.getOrDefault(p, List.of());
            List<LigneBail> lignes = c.parBail.entrySet().stream().map(e -> new LigneBail(e.getKey(), libelles.get(e.getKey()), e.getValue()[0], e.getValue()[1])).toList();
            resultat.put(p, new Calcul(c.loyers, c.charges, c.honoraires, liste.stream().mapToLong(Depense::getMontant).sum(), c.sansMandat, lignes, liste));
        }
        return resultat;
    }

    /** Arrondi à l'entier le plus proche (moitié vers l'infini), symétrique pour les montants négatifs (contre-passations). */
    static long arrondi(long numerateur, long denominateur) {
        long signe = Long.signum(numerateur) * Long.signum(denominateur);
        long n = Math.abs(numerateur);
        long d = Math.abs(denominateur);
        return signe * ((n * 2 + d) / (d * 2));
    }

    // ================================================================== cycle de vie

    /**
     * Calcule (ou recalcule) les reversements CALCULE du mois. Un reversement VALIDE ou PAYE n'est jamais touché. Idempotent.
     *
     * @param mois n'importe quel jour du mois, qui ne peut pas être dans le futur
     */
    @Transactional
    public List<ReversementDto> calculer(LocalDate mois) {
        LocalDate premier = mois.withDayOfMonth(1);
        if (premier.isAfter(moteur.aujourdhui().withDayOfMonth(1))) {
            throw new ErreurMetier("REVERSEMENT_MOIS_FUTUR", "Ce mois n'a pas encore commencé", HttpStatus.BAD_REQUEST);
        }
        jdbc.queryForObject("select pg_advisory_xact_lock(?)", Object.class, 8_000_000L + premier.toEpochDay());
        Map<UUID, Calcul> calculs = calculerTous(premier);
        OffsetDateTime maintenant = OffsetDateTime.now(horloge);
        for (Map.Entry<UUID, Calcul> e : calculs.entrySet()) {
            Reversement r = reversements.findByProprietaireIdAndPeriode(e.getKey(), premier).orElseGet(() -> new Reversement(e.getKey(), premier));
            if (!Reversement.CALCULE.equals(r.getStatut())) {
                continue;
            }
            appliquer(r, e.getValue(), maintenant);
            reversements.save(r);
        }
        // un propriétaire qui n'a plus d'activité (paiement contre-passé…) repasse à zéro plutôt que de rester périmé
        for (Reversement r : reversements.findByPeriode(premier)) {
            if (Reversement.CALCULE.equals(r.getStatut()) && !calculs.containsKey(r.getProprietaireId())) {
                appliquer(r, Calcul.VIDE, maintenant);
            }
        }
        reversements.flush();
        return reversements.findByPeriode(premier).stream().map(this::dto).toList();
    }

    private static void appliquer(Reversement r, Calcul c, OffsetDateTime quand) {
        r.recalculer(c.loyers(), c.charges(), c.honoraires(), c.tauxMoyenBp(), c.depenses(), c.sansMandat(), quand);
    }

    /** Fige le reversement : recalcul final, numéro continu par année, relevé de gérance PDF stocké. Le mois doit être terminé. */
    @Transactional
    public ReversementDto valider(UUID id) {
        Reversement r = charger(id);
        jdbc.queryForObject("select pg_advisory_xact_lock(?)", Object.class, 8_000_000L + r.getPeriode().toEpochDay());
        if (!Reversement.CALCULE.equals(r.getStatut())) {
            throw new ErreurMetier("REVERSEMENT_DEJA_VALIDE", "Ce reversement est déjà validé", HttpStatus.CONFLICT);
        }
        LocalDate aujourdhui = moteur.aujourdhui();
        if (r.getPeriode().plusMonths(1).isAfter(aujourdhui)) {
            throw new ErreurMetier("REVERSEMENT_MOIS_EN_COURS", "Le mois n'est pas terminé : validez à partir du %s".formatted(r.getPeriode().plusMonths(1)), HttpStatus.CONFLICT);
        }
        Calcul calcul = calculerTous(r.getPeriode()).getOrDefault(r.getProprietaireId(), Calcul.VIDE);
        if (calcul.sansMandat()) {
            throw new ErreurMetier("REVERSEMENT_SANS_MANDAT", "Aucun mandat en vigueur à la date de certains encaissements : les honoraires sont indéfinis", HttpStatus.CONFLICT);
        }
        OffsetDateTime maintenant = OffsetDateTime.now(horloge);
        appliquer(r, calcul, maintenant);
        String numero = prochainNumero(aujourdhui.getYear());
        byte[] contenu = pdf.rendre("finance/releve", releve(r, calcul, numero, false));
        FichierDto fichier = stockage.enregistrer("finance", "reversements", r.getId(), "releve-" + numero + ".pdf", new ByteArrayInputStream(contenu), true);
        try {
            UUID moi = acteurCourant.acteur().map(a -> a.id()).orElse(null);
            r.valider(numero, fichier.id(), moi, maintenant);
            reversements.saveAndFlush(r);
            evenements.publishEvent(new EvenementsReversement.ReversementValide(r.getId(), r.getProprietaireId(), r.getPeriode().toString(), numero, r.getNet(),
                    moi == null ? "systeme" : moi.toString()));
        } catch (RuntimeException e) {
            stockage.supprimer(fichier.id());
            throw e;
        }
        return dto(r);
    }

    @Transactional
    public ReversementDto payer(UUID id, PaiementReversementSaisie s) {
        Reversement r = charger(id);
        String mode = s.mode() == null ? "" : s.mode().strip().toUpperCase(Locale.ROOT);
        if (!MODES.contains(mode)) {
            throw new ErreurMetier("REVERSEMENT_MODE_INVALIDE", "Mode de paiement : VIREMENT, MOBILE_MONEY, ESPECES ou CHEQUE", HttpStatus.BAD_REQUEST);
        }
        String reference = s.reference() == null || s.reference().isBlank() ? null : s.reference().strip();
        if (reference != null && reference.length() > 100) {
            throw new ErreurMetier("CHAMP_TROP_LONG", "Référence trop longue (100 caractères au plus)", HttpStatus.BAD_REQUEST);
        }
        if (!mode.equals("ESPECES") && reference == null) {
            throw new ErreurMetier("REVERSEMENT_REFERENCE_REQUISE", "La référence du paiement est obligatoire hors espèces", HttpStatus.BAD_REQUEST);
        }
        LocalDate aujourdhui = moteur.aujourdhui();
        LocalDate date = s.date() == null ? aujourdhui : s.date();
        if (date.isAfter(aujourdhui)) {
            throw new ErreurMetier("REVERSEMENT_DATE_FUTURE", "La date de paiement ne peut pas être dans le futur", HttpStatus.BAD_REQUEST);
        }
        if (!Reversement.VALIDE.equals(r.getStatut())) {
            throw new ErreurMetier("REVERSEMENT_NON_PAYABLE", Reversement.PAYE.equals(r.getStatut()) ? "Ce reversement est déjà payé" : "Validez le reversement avant de le payer",
                    HttpStatus.CONFLICT);
        }
        r.payer(mode, reference, date);
        reversements.saveAndFlush(r);
        evenements.publishEvent(new EvenementsReversement.ReversementPaye(id, mode, acteurCourant.acteur().map(a -> a.id().toString()).orElse("systeme")));
        return dto(r);
    }

    /** Relevé PDF en direct (projet tant que le reversement n'est pas validé). */
    @Transactional(readOnly = true)
    public byte[] apercu(UUID id) {
        Reversement r = charger(id);
        Calcul calcul = Reversement.CALCULE.equals(r.getStatut()) ? calculerTous(r.getPeriode()).getOrDefault(r.getProprietaireId(), Calcul.VIDE)
                : calculerTous(r.getPeriode()).getOrDefault(r.getProprietaireId(), Calcul.VIDE);
        return pdf.rendre("finance/releve", releve(r, calcul, r.getNumero(), Reversement.CALCULE.equals(r.getStatut())));
    }

    // ================================================================== lecture

    @Transactional(readOnly = true)
    public Page<ReversementDto> lister(UUID proprietaireId, String statut, LocalDate mois, Pageable pageable) {
        UUID proprietaireImpose = proprietaireActeur();
        if (proprietaireImpose != null && proprietaireId != null && !proprietaireImpose.equals(proprietaireId)) {
            throw new ErreurMetier("PROPRIETAIRE_INTROUVABLE", "Introuvable", HttpStatus.NOT_FOUND);
        }
        UUID filtre = proprietaireImpose != null ? proprietaireImpose : proprietaireId;
        Specification<Reversement> spec = (r, q, cb) -> {
            List<Predicate> c = new ArrayList<>();
            if (filtre != null) {
                c.add(cb.equal(r.get("proprietaireId"), filtre));
            }
            if (statut != null) {
                c.add(cb.equal(r.get("statut"), statut));
            }
            if (mois != null) {
                c.add(cb.equal(r.get("periode"), mois.withDayOfMonth(1)));
            }
            if (proprietaireImpose != null) { // un propriétaire ne voit que les reversements validés (pas les calculs provisoires)
                c.add(r.get("statut").in(Reversement.VALIDE, Reversement.PAYE));
            }
            return cb.and(c.toArray(Predicate[]::new));
        };
        Page<Reversement> page = reversements.findAll(spec, pageable);
        return new PageImpl<>(page.getContent().stream().map(this::dto).toList(), pageable, page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public ReversementDto obtenir(UUID id) {
        Reversement r = charger(id);
        UUID proprietaire = proprietaireActeur();
        if (proprietaire != null && (!proprietaire.equals(r.getProprietaireId()) || Reversement.CALCULE.equals(r.getStatut()))) {
            throw new ErreurMetier("REVERSEMENT_INTROUVABLE", "Reversement introuvable", HttpStatus.NOT_FOUND);
        }
        return dto(r);
    }

    // ================================================================== interne

    private Reversement charger(UUID id) {
        return reversements.findById(id).orElseThrow(() -> new ErreurMetier("REVERSEMENT_INTROUVABLE", "Reversement introuvable", HttpStatus.NOT_FOUND));
    }

    private UUID proprietaireActeur() {
        return acteurCourant.acteur().filter(a -> a.aLeRole("PROPRIETAIRE_TIERS"))
                .map(a -> tiers.proprietaireDeUtilisateur(a.id()).orElse(new UUID(0, 0))).orElse(null);
    }

    private ReversementDto dto(Reversement r) {
        return new ReversementDto(r.getId(), r.getProprietaireId(), tiers.nomProprietaire(r.getProprietaireId()).orElse(null), r.getPeriode(), r.getStatut(),
                r.getNumero(), r.getLoyersEncaisses(), r.getChargesEncaissees(), r.getBrut(), r.getHonoraires(), r.getTauxMoyenBp(), r.getDepenses(), r.getNet(),
                r.isSansMandat(), r.getFichierId(), r.getCalculeLe(), r.getValideLe(), r.getPayeLe(), r.getModePaiement(), r.getReferencePaiement());
    }

    private DonneesReleve releve(Reversement r, Calcul c, String numero, boolean projet) {
        String proprietaire = tiers.nomProprietaire(r.getProprietaireId()).orElse("");
        String periode = r.getPeriode().format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.FRENCH));
        long brut = c.loyers() + c.charges();
        long net = brut - c.honoraires() - c.depenses();
        List<LigneEncaissement> enc = c.lignes().stream().map(l -> new LigneEncaissement(Montants.pourPdf(l.libelle()), Montants.fcfa(l.loyers()),
                Montants.fcfa(l.charges()), Montants.fcfa(l.loyers() + l.charges()))).toList();
        List<LigneDepense> deps = c.listeDepenses().stream().map(d -> new LigneDepense(Montants.date(d.getDateDepense()), Montants.pourPdf(d.getNature()),
                Montants.fcfa(d.getMontant()))).toList();
        Integer taux = c.tauxMoyenBp();
        List<LigneRecap> recap = List.of(
                new LigneRecap("Loyers encaissés", Montants.fcfa(c.loyers()), false),
                new LigneRecap("Charges encaissées", Montants.fcfa(c.charges()), false),
                new LigneRecap("Total encaissé", Montants.fcfa(brut), false),
                new LigneRecap("Honoraires de gestion" + (taux == null ? "" : " (%s %% des loyers)".formatted(String.format(Locale.FRANCE, "%.2f", taux / 100.0))),
                        "- " + Montants.fcfa(c.honoraires()), false),
                new LigneRecap("Dépenses imputées", "- " + Montants.fcfa(c.depenses()), false),
                new LigneRecap(net >= 0 ? "NET À REVERSER" : "NET À VOTRE CHARGE (solde négatif)", Montants.fcfa(net), true));
        return new DonneesReleve("RELEVÉ DE GÉRANCE", projet ? "Projet — " + periode : numero, projet, List.of(
                Montants.pourPdf("Propriétaire : %s. Période : %s. Gestionnaire : %s.".formatted(proprietaire, periode, baux.nomBailleur()))),
                enc, deps, recap, Montants.pourPdf("Les encaissements sont ceux imputés aux loyers pendant le mois, nets des annulations. Les honoraires portent sur la part « loyers » au taux du mandat en vigueur le jour de l'encaissement."),
                "Émis le " + Montants.date(moteur.aujourdhui()) + ".");
    }

    private String prochainNumero(int annee) {
        Long n = jdbc.queryForObject("""
                INSERT INTO fin_compteur_numero (type, annee, dernier) VALUES ('REVERSEMENT', ?, 1)
                ON CONFLICT (type, annee) DO UPDATE SET dernier = fin_compteur_numero.dernier + 1
                RETURNING dernier
                """, Long.class, annee);
        return "REV-%d-%06d".formatted(annee, n);
    }
}
