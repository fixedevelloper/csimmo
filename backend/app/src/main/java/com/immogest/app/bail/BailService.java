package com.immogest.app.bail;

import com.immogest.app.bail.domain.Bail;
import com.immogest.app.bail.domain.BailDocument;
import com.immogest.app.bail.domain.ModeleBail;
import com.immogest.app.bail.infra.BailDocumentRepository;
import com.immogest.app.bail.infra.BailRepository;
import com.immogest.app.bail.infra.DonneesBail;
import com.immogest.app.bail.infra.GenerateurBailPdf;
import com.immogest.app.shared.Montants;
import com.immogest.app.patrimoine.api.LecteurLots;
import com.immogest.app.patrimoine.api.LotResume;
import com.immogest.app.shared.ActeurCourant;
import com.immogest.app.shared.BailActive;
import com.immogest.app.shared.BailTermine;
import com.immogest.app.shared.ErreurMetier;
import com.immogest.app.stockage.FichierDto;
import com.immogest.app.stockage.StockageService;
import com.immogest.app.tiers.api.GarantContrat;
import com.immogest.app.tiers.api.LecteurTiers;
import com.immogest.app.tiers.api.LocataireContrat;
import jakarta.persistence.criteria.Predicate;
import java.io.ByteArrayInputStream;
import java.time.Clock;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Cycle de vie d'un bail : BROUILLON → ACTIF → RESILIE → TERMINE.
 * Règle n°1 : un lot a au plus un bail en vigueur à une date donnée ; vérifié ici (message clair) ET par une contrainte
 * d'exclusion PostgreSQL (jamais de course entre deux activations). Le statut du lot suit par événements
 * ({@code BailActive} / {@code BailTermine}), jamais par appel direct.
 */
@Service
public class BailService {

    private static final Logger LOG = LoggerFactory.getLogger(BailService.class);
    private static final ZoneId DOUALA = ZoneId.of("Africa/Douala");
    private static final LocalDate SANS_LIMITE = LocalDate.of(9999, 12, 31);
    private static final Set<StatutBail> OCCUPENT = EnumSet.of(StatutBail.ACTIF, StatutBail.RESILIE);
    private static final UUID AUCUN_LOCATAIRE = new UUID(0, 0);

    private final BailRepository baux;
    private final BailDocumentRepository documents;
    private final LecteurLots lots;
    private final LecteurTiers tiers;
    private final ModeleBailService modeles;
    private final GenerateurBailPdf generateur;
    private final StockageService stockage;
    private final ApplicationEventPublisher evenements;
    private final ActeurCourant acteurCourant;
    private final Clock horloge;

    BailService(BailRepository baux, BailDocumentRepository documents, LecteurLots lots, LecteurTiers tiers, ModeleBailService modeles,
            GenerateurBailPdf generateur, StockageService stockage, ApplicationEventPublisher evenements, ActeurCourant acteurCourant,
            Clock horloge) {
        this.baux = baux;
        this.documents = documents;
        this.lots = lots;
        this.tiers = tiers;
        this.modeles = modeles;
        this.generateur = generateur;
        this.stockage = stockage;
        this.evenements = evenements;
        this.acteurCourant = acteurCourant;
        this.horloge = horloge;
    }

    // ================================================================== lecture

    @Transactional(readOnly = true)
    public Page<BailDto> lister(FiltreBaux filtre, Pageable pageable) {
        UUID imposeLocataire = locataireImpose();
        Specification<Bail> spec = (racine, requete, cb) -> {
            List<Predicate> criteres = new ArrayList<>();
            if (filtre.statut() != null) {
                criteres.add(cb.equal(racine.get("statut"), filtre.statut()));
            }
            if (filtre.lotId() != null) {
                criteres.add(cb.equal(racine.get("lotId"), filtre.lotId()));
            }
            UUID locataire = imposeLocataire != null ? imposeLocataire : filtre.locataireId();
            if (locataire != null) {
                criteres.add(cb.equal(racine.get("locataireId"), locataire));
            }
            return cb.and(criteres.toArray(Predicate[]::new));
        };
        return baux.findAll(spec, pageable).map(this::versDto);
    }

    @Transactional(readOnly = true)
    public BailDto obtenir(UUID id) {
        return versDto(chargerVisible(id));
    }

    // ================================================================== création et modification (brouillon)

    @Transactional
    public BailDto creer(BailSaisie saisie) {
        Valide v = valider(saisie);
        Bail bail = new Bail(prochainNumero(), null);
        bail.definir(v.lotId, v.locataireId, v.garants, v.debut, v.fin, v.loyer, v.charges, v.periodicite, v.jour, v.caution, v.avance, v.preavis);
        baux.saveAndFlush(bail);
        publier("BAIL.CREE", bail.getId(), null, resume(bail));
        return versDto(bail);
    }

    @Transactional
    public BailDto modifier(UUID id, BailSaisie saisie) {
        Bail bail = charger(id);
        Valide v = valider(saisie);
        Map<String, Object> avant = resume(bail);
        bail.definir(v.lotId, v.locataireId, v.garants, v.debut, v.fin, v.loyer, v.charges, v.periodicite, v.jour, v.caution, v.avance, v.preavis);
        baux.flush();
        publier("BAIL.MODIFIE", id, avant, resume(bail));
        return versDto(bail);
    }

    /** Supprime un brouillon (rien de financier n'y est encore rattaché). Un bail activé ne se supprime jamais. */
    @Transactional
    public void supprimer(UUID id) {
        Bail bail = charger(id);
        if (bail.getStatut() != StatutBail.BROUILLON) {
            throw new ErreurMetier("BAIL_NON_SUPPRIMABLE", "Seul un brouillon peut être supprimé", HttpStatus.CONFLICT);
        }
        Map<String, Object> avant = resume(bail);
        baux.delete(bail);
        publier("BAIL.SUPPRIME", id, avant, null);
    }

    // ================================================================== activation

    /**
     * Signe le bail : le lot passe en OCCUPE (événement), et un exemplaire PDF figé est généré. Refusé si un autre bail
     * est en vigueur sur tout ou partie de la période (409 {@code BAIL_LOT_DEJA_OCCUPE}).
     */
    @Transactional
    public BailDto activer(UUID id) {
        Bail bail = charger(id);
        if (bail.getStatut() != StatutBail.BROUILLON) {
            throw new ErreurMetier("BAIL_NON_ACTIVABLE", "Seul un bail en brouillon peut être activé", HttpStatus.CONFLICT);
        }
        LotResume lot = lotActif(bail.getLotId());
        if ("EN_TRAVAUX".equals(lot.statut())) {
            throw new ErreurMetier("BAIL_LOT_EN_TRAVAUX", "Ce lot est en travaux : il ne peut pas être loué", HttpStatus.CONFLICT);
        }
        tiers.locatairePourContrat(bail.getLocataireId()).orElseThrow(() ->
                new ErreurMetier("LOCATAIRE_INTROUVABLE", "Locataire introuvable ou archivé", HttpStatus.CONFLICT));
        verifierLibre(bail.getLotId(), bail.getDateDebut(), bail.getDateFin(), bail.getId());

        Map<String, Object> avant = resume(bail);
        bail.activer();
        try {
            baux.flush();
        } catch (DataIntegrityViolationException e) {
            throw dejaOccupe(); // course entre deux activations : la contrainte d'exclusion tranche
        }
        publier("BAIL.ACTIVE", id, avant, resume(bail));
        evenements.publishEvent(new BailActive(id, bail.getLotId()));
        enregistrerDocument(bail);
        return versDto(bail);
    }

    // ================================================================== résiliation avec préavis

    @Transactional
    public BailDto resilier(UUID id, ResiliationSaisie saisie) {
        Bail bail = charger(id);
        if (bail.getStatut() != StatutBail.ACTIF) {
            throw new ErreurMetier("BAIL_NON_RESILIABLE", "Seul un bail actif peut être résilié", HttpStatus.CONFLICT);
        }
        LocalDate notification = saisie.dateNotification() != null ? saisie.dateNotification() : aujourdhui();
        LocalDate sortie = saisie.dateSortie() != null ? saisie.dateSortie() : notification.plusMonths(bail.getPreavisMois());
        String motif = texte(saisie.motif(), 1000);
        if (motif == null) {
            throw new ErreurMetier("BAIL_MOTIF_REQUIS", "Le motif de la résiliation est obligatoire", HttpStatus.BAD_REQUEST);
        }
        if (sortie.isBefore(notification) || sortie.isBefore(bail.getDateDebut())) {
            throw new ErreurMetier("BAIL_SORTIE_INVALIDE", "La date de sortie précède la notification ou le début du bail", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (bail.getDateFin() != null && sortie.isAfter(bail.getDateFin())) {
            throw new ErreurMetier("BAIL_SORTIE_APRES_FIN", "La sortie ne peut pas dépasser la fin prévue du bail", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (sortie.isBefore(notification.plusMonths(bail.getPreavisMois())) && !saisie.preavisReduitAccepte()) {
            throw new ErreurMetier("BAIL_PREAVIS_NON_RESPECTE",
                    "Le préavis de %d mois n'est pas respecté (sortie au plus tôt le %s) ; il faut l'accord des parties"
                            .formatted(bail.getPreavisMois(), notification.plusMonths(bail.getPreavisMois())), HttpStatus.UNPROCESSABLE_ENTITY);
        }
        Map<String, Object> avant = resume(bail);
        bail.resilier(notification, sortie, motif);
        baux.flush();
        publier("BAIL.RESILIE", id, avant, resume(bail));
        return versDto(bail);
    }

    // ================================================================== renouvellement

    /**
     * Crée, en brouillon, le bail suivant : il commence le lendemain de la fin du bail courant (donc sans chevauchement)
     * et reprend ses conditions, sauf celles précisées. Il s'active comme un bail ordinaire.
     */
    @Transactional
    public BailDto renouveler(UUID id, RenouvellementSaisie saisie) {
        Bail ancien = charger(id);
        if (ancien.getStatut() != StatutBail.ACTIF) {
            throw new ErreurMetier("BAIL_NON_RENOUVELABLE", "Seul un bail actif peut être renouvelé", HttpStatus.CONFLICT);
        }
        if (ancien.getDateFin() == null) {
            throw new ErreurMetier("BAIL_SANS_DATE_FIN", "Un bail sans date de fin n'a pas à être renouvelé", HttpStatus.CONFLICT);
        }
        if (baux.existsByBailPrecedentId(id)) {
            throw new ErreurMetier("BAIL_DEJA_RENOUVELE", "Ce bail a déjà un renouvellement", HttpStatus.CONFLICT);
        }
        LocalDate debut = ancien.getDateFin().plusDays(1);
        LocalDate fin = saisie.dateFin();
        if (fin != null && !fin.isAfter(debut)) {
            throw new ErreurMetier("BAIL_DATES_INVALIDES", "La fin du bail doit suivre son début (%s)".formatted(debut), HttpStatus.BAD_REQUEST);
        }
        Set<UUID> garantsActifs = new LinkedHashSet<>(tiers.garantsPourContrat(ancien.getLocataireId(), ancien.getGarantIds()).stream()
                .map(GarantContrat::id).toList());
        Bail nouveau = new Bail(prochainNumero(), ancien.getId());
        nouveau.definir(ancien.getLotId(), ancien.getLocataireId(), garantsActifs, debut, fin,
                valeur(saisie.loyer(), ancien.getLoyer()), valeur(saisie.charges(), ancien.getCharges()), ancien.getPeriodicite(),
                ancien.getJourEcheance(), valeur(saisie.caution(), ancien.getCaution()),
                saisie.moisAvance() != null ? saisie.moisAvance() : ancien.getMoisAvance(), ancien.getPreavisMois());
        baux.saveAndFlush(nouveau);
        publier("BAIL.RENOUVELE", nouveau.getId(), Map.of("bailPrecedentId", id.toString()), resume(nouveau));
        return versDto(nouveau);
    }

    // ================================================================== PDF

    /** PDF de prévisualisation (non enregistré) ; filigrane « projet » tant que le bail est en brouillon. */
    @Transactional(readOnly = true)
    public byte[] apercuPdf(UUID id) {
        return generateur.generer(donnees(chargerVisible(id)));
    }

    /** Génère et enregistre un nouvel exemplaire PDF (chiffré) du bail signé ; les précédents sont conservés. */
    @Transactional
    public DocumentBailDto genererDocument(UUID id) {
        Bail bail = charger(id);
        if (bail.getStatut() == StatutBail.BROUILLON) {
            throw new ErreurMetier("BAIL_BROUILLON_SANS_DOCUMENT", "Activez le bail avant d'en générer l'exemplaire signé", HttpStatus.CONFLICT);
        }
        return versDto(enregistrerDocument(bail));
    }

    @Transactional(readOnly = true)
    public List<DocumentBailDto> listerDocuments(UUID id) {
        chargerVisible(id);
        return documents.findByBailIdOrderByNumeroVersionDesc(id).stream().map(BailService::versDto).toList();
    }

    private BailDocument enregistrerDocument(Bail bail) {
        byte[] pdf = generateur.generer(donnees(bail));
        int version = documents.countByBailId(bail.getId()) + 1;
        FichierDto fichier = stockage.enregistrer("bail", "baux", bail.getId(), "bail-%s-v%d.pdf".formatted(bail.getNumero(), version),
                new ByteArrayInputStream(pdf), true);
        try {
            BailDocument document = documents.saveAndFlush(new BailDocument(bail.getId(), fichier.id(), version, OffsetDateTime.now(horloge)));
            publier("BAIL.DOCUMENT_GENERE", bail.getId(), null, Map.of("version", version));
            return document;
        } catch (RuntimeException e) {
            stockage.supprimer(fichier.id());
            throw e;
        }
    }

    // ================================================================== fin de bail (tâche quotidienne)

    /**
     * Termine les baux dont le dernier jour d'effet est passé (fin prévue ou date de sortie). Le lot ne redevient libre
     * que si aucun autre bail n'est en vigueur ou à venir sur ce lot (cas du renouvellement). Idempotent.
     *
     * @return nombre de baux terminés
     */
    @Transactional
    public int mettreAJourStatuts() {
        LocalDate aujourdhui = aujourdhui();
        List<Bail> echus = baux.findByStatutInAndDateFinEffectiveBefore(OCCUPENT, aujourdhui);
        for (Bail bail : echus) {
            Map<String, Object> avant = resume(bail);
            bail.terminer();
            publier("BAIL.TERMINE", bail.getId(), avant, resume(bail));
            if (!baux.existeAutreEnVigueurOuAVenir(bail.getLotId(), OCCUPENT, bail.getId(), aujourdhui)) {
                evenements.publishEvent(new BailTermine(bail.getId(), bail.getLotId()));
            }
        }
        if (!echus.isEmpty()) {
            LOG.info("{} bail(aux) terminé(s)", echus.size());
        }
        return echus.size();
    }

    // ================================================================== interne : validation

    private record Valide(UUID lotId, UUID locataireId, Set<UUID> garants, LocalDate debut, LocalDate fin, long loyer, long charges,
            Periodicite periodicite, int jour, long caution, int avance, int preavis) {
    }

    private Valide valider(BailSaisie s) {
        if (s.lotId() == null || s.locataireId() == null) {
            throw new ErreurMetier("BAIL_LOT_LOCATAIRE_REQUIS", "Le lot et le locataire sont obligatoires", HttpStatus.BAD_REQUEST);
        }
        lotActif(s.lotId());
        tiers.locatairePourContrat(s.locataireId()).orElseThrow(() ->
                new ErreurMetier("LOCATAIRE_INTROUVABLE", "Locataire introuvable ou archivé", HttpStatus.NOT_FOUND));
        Set<UUID> garants = new LinkedHashSet<>(s.garantIds() == null ? List.of() : s.garantIds());
        if (tiers.garantsPourContrat(s.locataireId(), garants).size() != garants.size()) {
            throw new ErreurMetier("BAIL_GARANT_INVALIDE", "Un garant n'existe pas ou n'est pas un garant actif de ce locataire", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (s.dateDebut() == null) {
            throw new ErreurMetier("BAIL_DATE_DEBUT_REQUISE", "La date de début est obligatoire", HttpStatus.BAD_REQUEST);
        }
        if (s.dateFin() != null && !s.dateFin().isAfter(s.dateDebut())) {
            throw new ErreurMetier("BAIL_DATES_INVALIDES", "La date de fin doit suivre la date de début", HttpStatus.BAD_REQUEST);
        }
        if (s.loyer() == null || s.loyer() <= 0) {
            throw new ErreurMetier("BAIL_LOYER_INVALIDE", "Le loyer doit être strictement positif (FCFA)", HttpStatus.BAD_REQUEST);
        }
        long charges = valeur(s.charges(), 0);
        long caution = valeur(s.caution(), 0);
        if (charges < 0 || caution < 0) {
            throw new ErreurMetier("BAIL_MONTANT_INVALIDE", "Charges et caution ne peuvent pas être négatives", HttpStatus.BAD_REQUEST);
        }
        if (s.periodicite() == null) {
            throw new ErreurMetier("BAIL_PERIODICITE_REQUISE", "La périodicité est obligatoire", HttpStatus.BAD_REQUEST);
        }
        if (s.jourEcheance() == null || s.jourEcheance() < 1 || s.jourEcheance() > 31) {
            throw new ErreurMetier("BAIL_JOUR_ECHEANCE_INVALIDE", "Le jour d'échéance est compris entre 1 et 31", HttpStatus.BAD_REQUEST);
        }
        int avance = s.moisAvance() == null ? 0 : s.moisAvance();
        if (avance < 0 || avance > 24) {
            throw new ErreurMetier("BAIL_AVANCE_INVALIDE", "L'avance est de 0 à 24 mois", HttpStatus.BAD_REQUEST);
        }
        int preavis = s.preavisMois() == null ? 3 : s.preavisMois();
        if (preavis < 0 || preavis > 12) {
            throw new ErreurMetier("BAIL_PREAVIS_INVALIDE", "Le préavis est de 0 à 12 mois", HttpStatus.BAD_REQUEST);
        }
        return new Valide(s.lotId(), s.locataireId(), garants, s.dateDebut(), s.dateFin(), s.loyer(), charges, s.periodicite(),
                s.jourEcheance(), caution, avance, preavis);
    }

    private LotResume lotActif(UUID lotId) {
        LotResume lot = lots.lot(lotId).orElseThrow(() -> new ErreurMetier("LOT_INTROUVABLE", "Lot introuvable", HttpStatus.NOT_FOUND));
        if (lot.archive()) {
            throw new ErreurMetier("LOT_ARCHIVE", "Ce lot est archivé", HttpStatus.CONFLICT);
        }
        return lot;
    }

    private void verifierLibre(UUID lotId, LocalDate debut, LocalDate fin, UUID exclu) {
        if (baux.chevauche(lotId, OCCUPENT, debut, fin == null ? SANS_LIMITE : fin, exclu)) {
            throw dejaOccupe();
        }
    }

    private static ErreurMetier dejaOccupe() {
        return new ErreurMetier("BAIL_LOT_DEJA_OCCUPE", "Ce lot a déjà un bail en vigueur sur tout ou partie de cette période", HttpStatus.CONFLICT);
    }

    // ================================================================== interne : accès, DTO, PDF

    private Bail charger(UUID id) {
        return baux.findById(id).orElseThrow(() -> new ErreurMetier("BAIL_INTROUVABLE", "Bail introuvable", HttpStatus.NOT_FOUND));
    }

    /** Un locataire ne voit que ses baux : les autres sont « introuvables ». */
    private Bail chargerVisible(UUID id) {
        Bail bail = charger(id);
        UUID impose = locataireImpose();
        if (impose != null && !impose.equals(bail.getLocataireId())) {
            throw new ErreurMetier("BAIL_INTROUVABLE", "Bail introuvable", HttpStatus.NOT_FOUND);
        }
        return bail;
    }

    /** Pour un LOCATAIRE : sa fiche (ou un identifiant impossible s'il n'en a pas). Vide pour les autres rôles. */
    private UUID locataireImpose() {
        return acteurCourant.acteur().filter(a -> a.aLeRole("LOCATAIRE"))
                .map(a -> tiers.locataireDeUtilisateur(a.id()).orElse(AUCUN_LOCATAIRE)).orElse(null);
    }

    private BailDto versDto(Bail b) {
        LotResume lot = lots.lot(b.getLotId()).orElse(null);
        String locataire = tiers.locataire(b.getLocataireId()).map(l -> l.nom()).orElse(null);
        List<BailDto.GarantResume> garants = tiers.garantsPourContrat(b.getLocataireId(), b.getGarantIds()).stream()
                .map(g -> new BailDto.GarantResume(g.id(), g.nom(), g.telephone())).toList();
        return new BailDto(b.getId(), b.getNumero(), b.getLotId(), lot == null ? null : lot.reference(), lot == null ? null : lot.immeubleNom(),
                b.getLocataireId(), locataire, b.getBailPrecedentId(), b.getStatut(), b.getDateDebut(), b.getDateFin(), b.getLoyer(),
                b.getCharges(), b.getPeriodicite(), b.getJourEcheance(), b.getCaution(), b.getMoisAvance(), b.getPreavisMois(),
                b.getDateNotificationPreavis(), b.getDateSortie(), b.getMotifResiliation(), b.getDateFinEffective(), garants);
    }

    private static DocumentBailDto versDto(BailDocument d) {
        return new DocumentBailDto(d.getId(), d.getBailId(), d.getFichierId(), d.getNumeroVersion(), d.getGenereLe());
    }

    private DonneesBail donnees(Bail b) {
        LotResume lot = lots.lot(b.getLotId()).orElseThrow(() -> new ErreurMetier("LOT_INTROUVABLE", "Lot introuvable", HttpStatus.NOT_FOUND));
        LocataireContrat loc = tiers.locatairePourContrat(b.getLocataireId()).orElseThrow(() ->
                new ErreurMetier("LOCATAIRE_INTROUVABLE", "Locataire introuvable ou archivé", HttpStatus.CONFLICT));
        List<GarantContrat> garants = tiers.garantsPourContrat(b.getLocataireId(), b.getGarantIds());
        ModeleBail modele = modeles.charger();

        String libelleLot = "%s %s%s".formatted(typeLibelle(lot.type()), lot.reference(),
                lot.surfaceM2() == null ? "" : ", " + lot.surfaceM2().stripTrailingZeros().toPlainString() + " m²");
        String adresse = String.join(", ", java.util.stream.Stream.of(lot.adresse(), lot.quartier(), lot.ville())
                .filter(x -> x != null && !x.isBlank()).toList());
        Map<String, String> valeurs = new LinkedHashMap<>();
        valeurs.put("NUMERO", b.getNumero());
        valeurs.put("LOCATAIRE", loc.nom());
        valeurs.put("LOT", libelleLot);
        valeurs.put("IMMEUBLE", lot.immeubleNom());
        valeurs.put("ADRESSE", adresse);
        valeurs.put("LOYER", Montants.fcfa(b.getLoyer()));
        valeurs.put("CHARGES", Montants.fcfa(b.getCharges()));
        valeurs.put("TOTAL_PERIODE", Montants.fcfa(b.getLoyer() + b.getCharges()));
        valeurs.put("CAUTION", Montants.fcfa(b.getCaution()));
        valeurs.put("DATE_DEBUT", Montants.date(b.getDateDebut()));
        valeurs.put("DATE_FIN", b.getDateFin() == null ? "durée indéterminée" : Montants.date(b.getDateFin()));
        valeurs.put("JOUR_ECHEANCE", String.valueOf(b.getJourEcheance()));
        valeurs.put("PERIODICITE", periodiciteLibelle(b.getPeriodicite()));
        valeurs.put("PREAVIS_MOIS", String.valueOf(b.getPreavisMois()));
        valeurs.put("MOIS_AVANCE", String.valueOf(b.getMoisAvance()));

        String contact = java.util.stream.Stream.of(loc.telephone(), loc.email()).filter(x -> x != null && !x.isBlank())
                .reduce((x, y) -> x + " / " + y).orElse("");
        List<DonneesBail.Garant> lignesGarants = garants.stream().map(g -> new DonneesBail.Garant(
                pdf(g.lien() == null ? "Garant" : g.lien()), pdf(g.nom()),
                pdf(java.util.stream.Stream.of(g.profession(), g.telephone(), g.adresse(), g.numeroCni() == null ? null : "CNI n° " + g.numeroCni())
                        .filter(x -> x != null && !x.isBlank()).reduce("", (x, y) -> x + ", " + y)))).toList();

        return new DonneesBail(b.getNumero(), b.getStatut() == StatutBail.BROUILLON, pdf(modele.getBailleurNom()), pdf(modele.getBailleurAdresse()),
                paragraphes(modele.getPreambule(), valeurs), pdf(loc.nom()), pdf(loc.profession()),
                loc.dateNaissance() == null ? null : Montants.date(loc.dateNaissance()), pdf(loc.numeroCni()), pdf(loc.adresse()), pdf(contact),
                pdf(lot.immeubleNom()), pdf(adresse), pdf(libelleLot), pdf(Montants.date(b.getDateDebut())), pdf(valeurs.get("DATE_FIN")),
                pdf(valeurs.get("LOYER")), pdf(valeurs.get("CHARGES")), pdf(valeurs.get("PERIODICITE")),
                "le " + b.getJourEcheance() + " de chaque période", pdf(valeurs.get("CAUTION")), b.getMoisAvance() + " mois de loyer",
                b.getPreavisMois() + " mois", lignesGarants, paragraphes(modele.getClauses(), valeurs),
                pdf(ModeleBailService.appliquer(modele.getPiedDePage(), valeurs)));
    }

    private static List<String> paragraphes(String texte, Map<String, String> valeurs) {
        return ModeleBailService.paragraphes(ModeleBailService.appliquer(texte, valeurs)).stream().map(BailService::pdf).toList();
    }

    private static String pdf(String texte) {
        return Montants.pourPdf(texte);
    }

    private static String typeLibelle(String type) {
        return switch (type == null ? "" : type) {
            case "APPARTEMENT" -> "Appartement";
            case "STUDIO" -> "Studio";
            case "BOUTIQUE" -> "Boutique";
            case "BUREAU" -> "Bureau";
            case "TERRAIN" -> "Terrain";
            default -> "Lot";
        };
    }

    private static String periodiciteLibelle(Periodicite p) {
        return switch (p) {
            case MENSUELLE -> "mensuelle";
            case TRIMESTRIELLE -> "trimestrielle";
            case ANNUELLE -> "annuelle";
        };
    }

    // ------------------------------------------------------------------ divers

    private String prochainNumero() {
        return "BAIL-%d-%04d".formatted(aujourdhui().getYear(), baux.prochainNumero());
    }

    private LocalDate aujourdhui() {
        return LocalDate.now(horloge.withZone(DOUALA));
    }

    private static long valeur(Long valeur, long defaut) {
        return valeur == null ? defaut : valeur;
    }

    private static String texte(String valeur, int max) {
        if (valeur == null || valeur.isBlank()) {
            return null;
        }
        String t = valeur.strip();
        if (t.length() > max) {
            throw new ErreurMetier("CHAMP_TROP_LONG", "Texte trop long (max %d caractères)".formatted(max), HttpStatus.BAD_REQUEST);
        }
        return t;
    }

    private Map<String, Object> resume(Bail b) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("numero", b.getNumero());
        m.put("lotId", b.getLotId().toString());
        m.put("locataireId", b.getLocataireId().toString());
        m.put("statut", b.getStatut().name());
        m.put("dateDebut", b.getDateDebut().toString());
        m.put("dateFin", b.getDateFin() == null ? null : b.getDateFin().toString());
        m.put("dateSortie", b.getDateSortie() == null ? null : b.getDateSortie().toString());
        m.put("loyer", b.getLoyer());
        m.put("charges", b.getCharges());
        return m;
    }

    private void publier(String action, UUID id, Map<String, Object> avant, Map<String, Object> apres) {
        String auteur = acteurCourant.acteur().map(a -> a.id().toString()).orElse("systeme");
        evenements.publishEvent(new EvenementBail(action, id, avant, apres, auteur));
    }
}
