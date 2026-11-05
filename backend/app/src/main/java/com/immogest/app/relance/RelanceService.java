package com.immogest.app.relance;

import com.immogest.app.bail.api.BailResume;
import com.immogest.app.bail.api.LecteurBaux;
import com.immogest.app.finance.EcheanceOuverte;
import com.immogest.app.finance.EcheanceService;
import com.immogest.app.notification.EtatMessage;
import com.immogest.app.notification.NotificationService;
import com.immogest.app.notification.StrategieCanaux;
import com.immogest.app.notification.TypeMessage;
import com.immogest.app.patrimoine.api.LecteurLots;
import com.immogest.app.patrimoine.api.LotResume;
import com.immogest.app.relance.domain.MiseEnDemeure;
import com.immogest.app.relance.domain.ParametresRelance;
import com.immogest.app.relance.domain.Relance;
import com.immogest.app.relance.infra.MiseEnDemeureRepository;
import com.immogest.app.relance.infra.ParametresRelanceRepository;
import com.immogest.app.relance.infra.RelanceRepository;
import com.immogest.app.shared.ActeurCourant;
import com.immogest.app.shared.ErreurMetier;
import com.immogest.app.shared.Montants;
import com.immogest.app.shared.RenduPdf;
import com.immogest.app.stockage.FichierDto;
import com.immogest.app.stockage.StockageService;
import com.immogest.app.tiers.api.LecteurTiers;
import java.io.ByteArrayInputStream;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import java.time.LocalDate;

/**
 * Relances d'impayés. Le job quotidien est idempotent (un niveau par échéance, contrainte unique) et rattrape un arrêt sans
 * inonder le locataire : seul le niveau le plus élevé atteint part, les niveaux inférieurs manqués sont historisés « ignorés ».
 * Une échéance dont le niveau atteint date de plus de 30 jours n'est plus relancée automatiquement (arriéré ancien ou repris
 * d'un autre outil) : le gestionnaire décide, avec la relance manuelle.
 */
@Service
public class RelanceService {

    private static final Logger LOG = LoggerFactory.getLogger(RelanceService.class);
    private static final int ANCIENNETE_MAX_JOURS = 30;

    private final ParametresRelanceRepository parametres;
    private final RelanceRepository relances;
    private final MiseEnDemeureRepository miseEnDemeures;
    private final EcheanceService finance;
    private final LecteurBaux baux;
    private final LecteurLots lots;
    private final LecteurTiers tiers;
    private final NotificationService notifications;
    private final StockageService stockage;
    private final RenduPdf pdf;
    private final ActeurCourant acteurCourant;
    private final ApplicationEventPublisher evenements;
    private final TransactionTemplate transaction;
    private final JdbcTemplate jdbc;

    RelanceService(ParametresRelanceRepository parametres, RelanceRepository relances, MiseEnDemeureRepository miseEnDemeures,
            EcheanceService finance, LecteurBaux baux, LecteurLots lots, LecteurTiers tiers, NotificationService notifications,
            StockageService stockage, RenduPdf pdf, ActeurCourant acteurCourant, ApplicationEventPublisher evenements,
            TransactionTemplate transaction, JdbcTemplate jdbc) {
        this.parametres = parametres;
        this.relances = relances;
        this.miseEnDemeures = miseEnDemeures;
        this.finance = finance;
        this.baux = baux;
        this.lots = lots;
        this.tiers = tiers;
        this.notifications = notifications;
        this.stockage = stockage;
        this.pdf = pdf;
        this.acteurCourant = acteurCourant;
        this.evenements = evenements;
        this.transaction = transaction;
        this.jdbc = jdbc;
    }

    // ================================================================== paramètres

    @Transactional(readOnly = true)
    public ParametresRelanceDto parametres() {
        return versDto(parametres.findBySingletonTrue().orElseGet(ParametresRelance::new));
    }

    @Transactional
    public ParametresRelanceDto modifier(ParametresRelanceDto saisie) {
        TreeSet<Integer> niveaux = new TreeSet<>(saisie.niveaux() == null ? List.of() : saisie.niveaux());
        if (niveaux.isEmpty() || niveaux.size() > 8 || niveaux.first() < -30 || niveaux.last() > 365) {
            throw new ErreurMetier("RELANCE_NIVEAUX_INVALIDES", "Indiquez de 1 à 8 niveaux distincts, entre -30 et 365 jours", HttpStatus.BAD_REQUEST);
        }
        if (saisie.niveaux().size() != niveaux.size()) {
            throw new ErreurMetier("RELANCE_NIVEAUX_INVALIDES", "Les niveaux doivent être distincts", HttpStatus.BAD_REQUEST);
        }
        if (niveaux.last() < 1) {
            throw new ErreurMetier("RELANCE_NIVEAUX_INVALIDES", "Il faut au moins un niveau après l'échéance (la mise en demeure en dépend)", HttpStatus.BAD_REQUEST);
        }
        if (saisie.delaiMiseEnDemeureJours() < 1 || saisie.delaiMiseEnDemeureJours() > 90) {
            throw new ErreurMetier("RELANCE_DELAI_INVALIDE", "Le délai de la mise en demeure est de 1 à 90 jours", HttpStatus.BAD_REQUEST);
        }
        ParametresRelance p = parametres.findBySingletonTrue().orElseGet(() -> parametres.saveAndFlush(new ParametresRelance()));
        String texte = niveaux.stream().map(String::valueOf).collect(Collectors.joining(","));
        p.modifier(saisie.actif(), texte, saisie.delaiMiseEnDemeureJours());
        evenements.publishEvent(new EvenementsRelance.ParametresModifies(texte, saisie.delaiMiseEnDemeureJours(), saisie.actif(), auteur()));
        return versDto(p);
    }

    // ================================================================== job

    /** Passage quotidien. Chaque échéance est traitée dans sa propre transaction : un échec n'arrête pas les autres. */
    public BilanRelances executer() {
        ParametresRelanceDto p = parametres();
        LocalDate aujourdhui = finance.aujourdhui();
        if (!p.actif()) {
            return new BilanRelances(0, 0, 0, 0);
        }
        List<Integer> niveaux = p.niveaux().stream().sorted().toList();
        List<EcheanceOuverte> candidates = finance.ouvertesJusquA(aujourdhui.minusDays(niveaux.get(0)));
        int[] c = new int[3]; // envoyées, sans canal, ignorées
        for (EcheanceOuverte e : candidates) {
            try {
                int[] r = transaction.execute(s -> traiter(e, niveaux, aujourdhui));
                c[0] += r[0];
                c[1] += r[1];
                c[2] += r[2];
            } catch (DataIntegrityViolationException ex) {
                LOG.debug("Relance déjà traitée pour l'échéance {}", e.echeanceId());
            } catch (RuntimeException ex) {
                LOG.error("Relance de l'échéance {} : échec ({})", e.echeanceId(), ex.getClass().getSimpleName());
            }
        }
        LOG.info("Relances : {} échéance(s) examinée(s), {} envoyée(s), {} sans canal, {} ignorée(s)", candidates.size(), c[0], c[1], c[2]);
        return new BilanRelances(candidates.size(), c[0], c[1], c[2]);
    }

    private int[] traiter(EcheanceOuverte e, List<Integer> niveaux, LocalDate aujourdhui) {
        Set<Integer> deja = new java.util.HashSet<>(relances.niveauxAuto(e.echeanceId()));
        List<Integer> manquants = niveaux.stream().filter(n -> !e.dateEcheance().plusDays(n).isAfter(aujourdhui) && !deja.contains(n)).toList();
        if (manquants.isEmpty()) {
            return new int[] {0, 0, 0};
        }
        BailResume bail = baux.bail(e.bailId()).orElse(null);
        if (bail == null) {
            return new int[] {0, 0, 0};
        }
        int plusEleve = manquants.get(manquants.size() - 1);
        int ignorees = 0;
        for (int n : manquants) {
            if (n != plusEleve) {
                historiser(e, bail, n, aujourdhui, ResultatRelance.IGNOREE, TypeRelance.AUTO, null);
                ignorees++;
            }
        }
        long ancien = ChronoUnit.DAYS.between(e.dateEcheance().plusDays(plusEleve), aujourdhui);
        if (ancien > ANCIENNETE_MAX_JOURS) {
            historiser(e, bail, plusEleve, aujourdhui, ResultatRelance.IGNOREE, TypeRelance.AUTO, null);
            return new int[] {0, 0, ignorees + 1};
        }
        ResultatRelance resultat = envoyer(e, bail, plusEleve, aujourdhui, TypeRelance.AUTO, null).getResultat();
        return new int[] {resultat == ResultatRelance.EN_FILE ? 1 : 0, resultat == ResultatRelance.AUCUN_CANAL ? 1 : 0, ignorees};
    }

    private Relance historiser(EcheanceOuverte e, BailResume bail, Integer niveau, LocalDate aujourdhui, ResultatRelance resultat,
            TypeRelance type, UUID auteur) {
        int retard = (int) Math.max(0, ChronoUnit.DAYS.between(e.dateEcheance(), aujourdhui));
        Relance r = new Relance(e.echeanceId(), e.bailId(), bail.locataireId(), type, niveau, aujourdhui, e.reste(), retard, auteur);
        r.resultat(resultat, null);
        relances.saveAndFlush(r);
        return r;
    }

    private Relance envoyer(EcheanceOuverte e, BailResume bail, Integer niveau, LocalDate aujourdhui, TypeRelance type, UUID auteur) {
        int retard = (int) Math.max(0, ChronoUnit.DAYS.between(e.dateEcheance(), aujourdhui));
        Relance r = relances.saveAndFlush(new Relance(e.echeanceId(), e.bailId(), bail.locataireId(), type, niveau, aujourdhui, e.reste(), retard, auteur));
        TypeMessage message = e.dateEcheance().isBefore(aujourdhui) ? TypeMessage.RELANCE_IMPAYE : TypeMessage.RAPPEL_ECHEANCE;
        List<UUID> ids = notifications.notifierLocataire(message, bail.locataireId(), r.getId(), Map.of(
                "montant", Montants.fcfa(e.reste()).replace(' ', ' '), "date", Montants.date(e.dateEcheance()), "jours", String.valueOf(retard),
                "bail", bail.numero()), StrategieCanaux.PREFERENCE);
        ResultatRelance resultat = ids.isEmpty() ? ResultatRelance.AUCUN_CANAL : ResultatRelance.EN_FILE;
        r.resultat(resultat, ids.isEmpty() ? null : ids.get(0));
        evenements.publishEvent(new EvenementsRelance.RelanceTraitee(r.getId(), e.echeanceId(), e.bailId(), type.name(), niveau, resultat.name(),
                auteur == null ? "systeme" : auteur.toString()));
        return r;
    }

    /** Relance à la demande d'un gestionnaire (hors des niveaux automatiques). */
    @Transactional
    public RelanceDto relancer(UUID echeanceId) {
        EcheanceOuverte e = finance.echeance(echeanceId).orElseThrow(() -> new ErreurMetier("ECHEANCE_INTROUVABLE", "Échéance introuvable", HttpStatus.NOT_FOUND));
        if (e.reste() <= 0 || e.statut() == com.immogest.app.finance.StatutEcheance.ANNULEE) {
            throw new ErreurMetier("RELANCE_ECHEANCE_SOLDEE", "Cette échéance n'a rien à relancer", HttpStatus.CONFLICT);
        }
        BailResume bail = baux.bail(e.bailId()).orElseThrow(() -> new ErreurMetier("BAIL_INTROUVABLE", "Bail introuvable", HttpStatus.NOT_FOUND));
        UUID moi = acteurCourant.acteur().map(a -> a.id()).orElse(null);
        Relance r = envoyer(e, bail, null, finance.aujourdhui(), TypeRelance.MANUELLE, moi);
        return versDto(List.of(r)).get(0);
    }

    // ================================================================== lecture

    @Transactional(readOnly = true)
    public Page<RelanceDto> historique(UUID echeanceId, UUID bailId, Pageable pageable) {
        Page<Relance> page = relances.historique(echeanceId, bailId, pageable);
        return new PageImpl<>(versDto(page.getContent()), pageable, page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public Page<ArriereDto> arrieres(String q, Pageable pageable) {
        LocalDate aujourdhui = finance.aujourdhui();
        List<UUID> bailIds = null;
        if (q != null && !q.isBlank()) { // recherche par nom ou téléphone du locataire
            bailIds = tiers.rechercherLocataires(q.strip()).stream().flatMap(l -> baux.bauxDuLocataire(l).stream()).map(BailResume::id).toList();
        }
        int dernierNiveau = parametres().niveaux().stream().max(Integer::compare).orElse(30);
        Page<EcheanceOuverte> page = finance.arrieres(bailIds, pageable);
        Map<UUID, MiseEnDemeure> meds = miseEnDemeures.findByEcheanceIdIn(page.getContent().stream().map(EcheanceOuverte::echeanceId).toList())
                .stream().collect(Collectors.toMap(MiseEnDemeure::getEcheanceId, m -> m));
        Map<UUID, BailResume> cacheBaux = new HashMap<>();
        Map<UUID, String> noms = new HashMap<>();
        List<ArriereDto> dtos = page.getContent().stream().map(e -> {
            BailResume b = cacheBaux.computeIfAbsent(e.bailId(), id -> baux.bail(id).orElse(null));
            String nom = b == null ? null : noms.computeIfAbsent(b.locataireId(), id -> tiers.locataire(id).map(l -> l.nom()).orElse(null));
            LotResume lot = b == null ? null : lots.lot(b.lotId()).orElse(null);
            int retard = (int) Math.max(0, ChronoUnit.DAYS.between(e.dateEcheance(), aujourdhui));
            RelanceDto derniere = relances.findFirstByEcheanceIdAndResultatNotOrderByCreatedAtDesc(e.echeanceId(), ResultatRelance.IGNOREE)
                    .map(r -> versDto(List.of(r)).get(0)).orElse(null);
            MiseEnDemeure med = meds.get(e.echeanceId());
            return new ArriereDto(e.echeanceId(), e.bailId(), b == null ? null : b.numero(), b == null ? null : b.locataireId(), nom,
                    lot == null ? null : lot.immeubleNom() + " — " + lot.reference(), e.periodeDebut(), e.periodeFin(), e.dateEcheance(),
                    e.montant(), e.reste(), retard, derniere, med == null && retard >= dernierNiveau, med == null ? null : med.getId());
        }).toList();
        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }

    private List<RelanceDto> versDto(List<Relance> liste) {
        Map<UUID, EtatMessage> etats = notifications.etats(liste.stream().map(Relance::getMessageId).filter(java.util.Objects::nonNull).toList());
        List<RelanceDto> r = new ArrayList<>();
        for (Relance x : liste) {
            EtatMessage etat = x.getMessageId() == null ? null : etats.get(x.getMessageId());
            r.add(new RelanceDto(x.getId(), x.getEcheanceId(), x.getBailId(), x.getLocataireId(), x.getType(), x.getNiveau(), x.getDateRelance(),
                    x.getResultat(), etat == null ? null : etat.canal(), etat == null ? null : etat.statut(), x.getResteDu(), x.getJoursRetard(),
                    x.getCreatedAt()));
        }
        return r;
    }

    private static ParametresRelanceDto versDto(ParametresRelance p) {
        return new ParametresRelanceDto(p.isActif(), java.util.Arrays.stream(p.getNiveaux().split(",")).map(String::strip).map(Integer::valueOf).toList(),
                p.getDelaiMiseEnDemeureJours());
    }

    // ================================================================== mise en demeure

    /**
     * Émet la mise en demeure de payer pour le bail de cette échéance, quand le dernier niveau de relance est atteint. Elle récapitule
     * tous les arriérés du bail, fixe un délai de régularisation et reste consultable (PDF chiffré). Une seule par échéance.
     */
    @Transactional
    public MiseEnDemeureDto miseEnDemeure(UUID echeanceId) {
        EcheanceOuverte e = finance.echeance(echeanceId).orElseThrow(() -> new ErreurMetier("ECHEANCE_INTROUVABLE", "Échéance introuvable", HttpStatus.NOT_FOUND));
        if (e.reste() <= 0 || e.statut() == com.immogest.app.finance.StatutEcheance.ANNULEE) {
            throw new ErreurMetier("RELANCE_ECHEANCE_SOLDEE", "Cette échéance est soldée : pas de mise en demeure", HttpStatus.CONFLICT);
        }
        jdbc.queryForObject("select pg_advisory_xact_lock(?)", Object.class, e.bailId().getMostSignificantBits() ^ e.bailId().getLeastSignificantBits());
        if (miseEnDemeures.findByEcheanceId(echeanceId).isPresent()) {
            throw new ErreurMetier("RELANCE_MED_EXISTANTE", "Une mise en demeure existe déjà pour cette échéance", HttpStatus.CONFLICT);
        }
        ParametresRelanceDto p = parametres();
        LocalDate aujourdhui = finance.aujourdhui();
        int dernier = p.niveaux().stream().max(Integer::compare).orElse(30);
        long retard = ChronoUnit.DAYS.between(e.dateEcheance(), aujourdhui);
        if (retard < dernier) {
            throw new ErreurMetier("RELANCE_MED_TROP_TOT", "La mise en demeure n'est possible qu'à partir de %d jours de retard (dernier niveau de relance)".formatted(dernier),
                    HttpStatus.CONFLICT);
        }
        BailResume bail = baux.bail(e.bailId()).orElseThrow(() -> new ErreurMetier("BAIL_INTROUVABLE", "Bail introuvable", HttpStatus.NOT_FOUND));
        var locataire = tiers.locatairePourContrat(bail.locataireId())
                .orElseThrow(() -> new ErreurMetier("LOCATAIRE_INTROUVABLE", "Locataire introuvable ou archivé", HttpStatus.CONFLICT));
        LotResume lot = lots.lot(bail.lotId()).orElse(null);
        List<EcheanceOuverte> arrieres = finance.arrieresDuBail(e.bailId());
        long total = arrieres.stream().mapToLong(EcheanceOuverte::reste).sum();
        String numero = prochainNumero(aujourdhui.getYear());
        String bailleur = baux.nomBailleur();
        String logement = lot == null ? bail.numero() : "%s, immeuble %s, %s".formatted(lot.reference(), lot.immeubleNom(), lot.adresse());

        List<DonneesMiseEnDemeure.Ligne> lignes = arrieres.stream().map(a -> new DonneesMiseEnDemeure.Ligne(
                "Échéance du %s (période du %s au %s), %d jours de retard".formatted(Montants.date(a.dateEcheance()), Montants.date(a.periodeDebut()),
                        Montants.date(a.periodeFin()), ChronoUnit.DAYS.between(a.dateEcheance(), aujourdhui)), Montants.fcfa(a.reste()))).collect(Collectors.toList());
        lignes.add(new DonneesMiseEnDemeure.Ligne("TOTAL À RÉGLER", Montants.fcfa(total)));
        List<String> corps = List.of(
                "À l'attention de %s%s.".formatted(locataire.nom(), locataire.adresse() == null ? "" : ", " + locataire.adresse()),
                "Objet : mise en demeure de payer les loyers échus, bail n° %s, logement %s.".formatted(bail.numero(), logement),
                "Malgré nos rappels, les loyers et charges ci-dessous, échus et exigibles, demeurent impayés. Par la présente, %s, bailleur, vous met en demeure de lui régler la somme de %s dans un délai de %d jours à compter de la réception de ce courrier."
                        .formatted(bailleur, Montants.fcfa(total), p.delaiMiseEnDemeureJours()),
                "À défaut de règlement dans ce délai, le bailleur se réserve le droit d'engager toute action utile, y compris la résiliation du bail et la saisine des juridictions compétentes, sans autre avis et aux frais du locataire dans la mesure permise par la loi.");
        DonneesMiseEnDemeure donnees = nettoyer(new DonneesMiseEnDemeure("MISE EN DEMEURE DE PAYER", numero, corps, "Détail des sommes dues", lignes,
                "Si vous avez réglé ces sommes depuis l'émission de ce document, veuillez ne pas en tenir compte et nous en transmettre la preuve.",
                "Émise le " + Montants.date(aujourdhui) + ".", bailleur));

        UUID moi = acteurCourant.acteur().map(a -> a.id()).orElse(null);
        MiseEnDemeure med = new MiseEnDemeure(numero, e.bailId(), echeanceId, total, p.delaiMiseEnDemeureJours(), aujourdhui, moi);
        byte[] contenu = pdf.rendre("relance/mise-en-demeure", donnees);
        FichierDto fichier = stockage.enregistrer("relance", "mises-en-demeure", med.getId(), "mise-en-demeure-" + numero + ".pdf",
                new ByteArrayInputStream(contenu), true);
        try {
            med.attacherFichier(fichier.id());
            miseEnDemeures.saveAndFlush(med);
        } catch (RuntimeException ex) {
            stockage.supprimer(fichier.id());
            throw ex;
        }
        evenements.publishEvent(new EvenementsRelance.MiseEnDemeureEmise(med.getId(), numero, e.bailId(), echeanceId, total,
                moi == null ? "systeme" : moi.toString()));
        notifications.notifierLocataire(TypeMessage.MISE_EN_DEMEURE, bail.locataireId(), med.getId(), Map.of("numero", numero,
                "montant", Montants.fcfa(total).replace(' ', ' '), "delai", String.valueOf(p.delaiMiseEnDemeureJours())), StrategieCanaux.PREFERENCE);
        return dto(med, bail.numero(), locataire.nom());
    }

    @Transactional(readOnly = true)
    public Page<MiseEnDemeureDto> miseEnDemeures(UUID bailId, UUID locataireImpose, Pageable pageable) {
        Page<MiseEnDemeure> page;
        if (locataireImpose != null) {
            List<UUID> mes = baux.bauxDuLocataire(locataireImpose).stream().map(BailResume::id).filter(id -> bailId == null || id.equals(bailId)).toList();
            if (mes.isEmpty()) {
                return Page.empty(pageable);
            }
            page = miseEnDemeures.listerDesBaux(mes, pageable);
        } else {
            page = miseEnDemeures.lister(bailId, pageable);
        }
        Map<UUID, BailResume> cache = new HashMap<>();
        List<MiseEnDemeureDto> dtos = page.getContent().stream().map(m -> {
            BailResume b = cache.computeIfAbsent(m.getBailId(), id -> baux.bail(id).orElse(null));
            String nom = b == null ? null : tiers.locataire(b.locataireId()).map(l -> l.nom()).orElse(null);
            return dto(m, b == null ? null : b.numero(), nom);
        }).toList();
        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }

    private static MiseEnDemeureDto dto(MiseEnDemeure m, String bailNumero, String nom) {
        return new MiseEnDemeureDto(m.getId(), m.getNumero(), m.getBailId(), bailNumero, m.getEcheanceId(), nom, m.getMontantArrieres(),
                m.getDelaiJours(), m.getDateEmission(), m.getFichierId());
    }

    private String prochainNumero(int annee) {
        Long n = jdbc.queryForObject("""
                INSERT INTO rel_compteur_numero (annee, dernier) VALUES (?, 1)
                ON CONFLICT (annee) DO UPDATE SET dernier = rel_compteur_numero.dernier + 1
                RETURNING dernier
                """, Long.class, annee);
        return "MED-%d-%06d".formatted(annee, n);
    }

    private static DonneesMiseEnDemeure nettoyer(DonneesMiseEnDemeure d) {
        return new DonneesMiseEnDemeure(Montants.pourPdf(d.titre()), d.numero(), d.corps().stream().map(Montants::pourPdf).toList(),
                Montants.pourPdf(d.titreTableau()),
                d.lignes().stream().map(l -> new DonneesMiseEnDemeure.Ligne(Montants.pourPdf(l.libelle()), Montants.pourPdf(l.valeur()))).toList(),
                Montants.pourPdf(d.note()), Montants.pourPdf(d.emission()), Montants.pourPdf(d.signature()));
    }

    private String auteur() {
        return acteurCourant.acteur().map(a -> a.id().toString()).orElse("systeme");
    }

}
