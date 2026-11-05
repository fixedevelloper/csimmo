package com.immogest.app.finance;

import com.immogest.app.bail.api.BailResume;
import com.immogest.app.bail.api.LecteurBaux;
import com.immogest.app.finance.domain.DocumentPaiement;
import com.immogest.app.finance.domain.Echeance;
import com.immogest.app.finance.domain.Imputation;
import com.immogest.app.finance.domain.Paiement;
import com.immogest.app.finance.infra.AccesLocataire;
import com.immogest.app.finance.infra.DocumentRepository;
import com.immogest.app.finance.infra.DonneesDocument;
import com.immogest.app.finance.infra.EcheanceRepository;
import com.immogest.app.finance.infra.GenerateurDocumentPdf;
import com.immogest.app.finance.infra.ImputationRepository;
import com.immogest.app.finance.infra.PaiementRepository;
import com.immogest.app.patrimoine.api.LecteurLots;
import com.immogest.app.patrimoine.api.LotResume;
import com.immogest.app.shared.ActeurCourant;
import com.immogest.app.shared.ErreurMetier;
import com.immogest.app.shared.Montants;
import com.immogest.app.stockage.FichierDto;
import com.immogest.app.stockage.StockageService;
import com.immogest.app.tiers.api.LecteurTiers;
import jakarta.persistence.criteria.Predicate;
import java.io.ByteArrayInputStream;
import java.time.Clock;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * Quittances (une par échéance entièrement payée) et reçus (un par encaissement qui ne solde pas tout ce qu'il touche).
 * Numérotation continue par année et par type (QUI-2026-000001, REC-2026-000001) : le compteur est incrémenté dans la transaction
 * qui enregistre le document, donc un échec ne laisse aucun trou. Les documents ne sont jamais supprimés : annulés si le paiement
 * est contre-passé, et une nouvelle quittance (nouveau numéro) est émise quand l'échéance est de nouveau soldée.
 */
@Service
public class DocumentService {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentService.class);
    private static final ZoneId DOUALA = ZoneId.of("Africa/Douala");

    private final DocumentRepository documents;
    private final EcheanceRepository echeances;
    private final PaiementRepository paiements;
    private final ImputationRepository imputations;
    private final EcheanceService moteur;
    private final LecteurBaux baux;
    private final LecteurLots lots;
    private final LecteurTiers tiers;
    private final GenerateurDocumentPdf generateur;
    private final StockageService stockage;
    private final AccesLocataire acces;
    private final ActeurCourant acteurCourant;
    private final ApplicationEventPublisher evenements;
    private final JdbcTemplate jdbc;
    private final Clock horloge;

    DocumentService(DocumentRepository documents, EcheanceRepository echeances, PaiementRepository paiements,
            ImputationRepository imputations, EcheanceService moteur, LecteurBaux baux, LecteurLots lots, LecteurTiers tiers,
            GenerateurDocumentPdf generateur, StockageService stockage, AccesLocataire acces, ActeurCourant acteurCourant,
            ApplicationEventPublisher evenements, JdbcTemplate jdbc, Clock horloge) {
        this.documents = documents;
        this.echeances = echeances;
        this.paiements = paiements;
        this.imputations = imputations;
        this.moteur = moteur;
        this.baux = baux;
        this.lots = lots;
        this.tiers = tiers;
        this.generateur = generateur;
        this.stockage = stockage;
        this.acces = acces;
        this.acteurCourant = acteurCourant;
        this.evenements = evenements;
        this.jdbc = jdbc;
        this.horloge = horloge;
    }

    // ================================================================== émission

    /** Idempotent : sans effet si l'échéance a déjà une quittance en vigueur, ou si elle n'est plus entièrement payée. */
    @Transactional
    public void emettreQuittance(UUID echeanceId) {
        Echeance echeance = echeances.findById(echeanceId).orElse(null);
        if (echeance == null) {
            return;
        }
        moteur.verrouiller(echeance.getBailId());
        echeances.flush();
        Echeance courante = echeances.findById(echeanceId).orElseThrow();
        if (courante.getStatut() != StatutEcheance.PAYEE
                || documents.findByTypeAndEcheanceIdAndStatut(DocumentPaiement.QUITTANCE, echeanceId, DocumentPaiement.EMIS).isPresent()) {
            return;
        }
        Contexte c = contexte(courante.getBailId());
        long montant = courante.getMontant();
        long loyer = c.bail.loyer() + c.bail.charges() == 0 ? montant : montant * c.bail.loyer() / (c.bail.loyer() + c.bail.charges());
        List<DonneesDocument.Ligne> lignes = new ArrayList<>();
        lignes.add(new DonneesDocument.Ligne("Loyer", Montants.fcfa(loyer)));
        lignes.add(new DonneesDocument.Ligne("Charges", Montants.fcfa(montant - loyer)));
        lignes.add(new DonneesDocument.Ligne("Total réglé", Montants.fcfa(montant)));
        lignes.add(new DonneesDocument.Ligne("Échéance du", Montants.date(courante.getDateEcheance())));
        for (Object[] ligne : imputations.netParPaiement(echeanceId)) {
            Paiement p = paiements.findById((UUID) ligne[0]).orElseThrow();
            lignes.add(new DonneesDocument.Ligne("Payé le " + Montants.date(p.getDatePaiement()) + " (" + libelleMode(p) + ")",
                    Montants.fcfa(((Number) ligne[1]).longValue())));
        }
        String numero = prochainNumero("QUI", "QUITTANCE");
        String corps = "Je soussigné(e) %s, bailleur, reconnais avoir reçu de %s la somme de %s au titre du loyer et des charges pour la période du %s au %s, "
                .formatted(c.bailleur, c.locataireNom, Montants.fcfa(montant), Montants.date(courante.getPeriodeDebut()),
                        Montants.date(courante.getPeriodeFin()))
                + "concernant le logement %s, et lui en donne quittance, sous réserve de tous mes droits.".formatted(c.logement);
        DonneesDocument donnees = nettoyer(new DonneesDocument("QUITTANCE DE LOYER", numero, List.of(corps), "Détail du règlement", lignes,
                "Bail n° " + c.bail.numero() + ". Cette quittance annule tout reçu partiel délivré pour la même période.", emission()));
        DocumentPaiement doc = enregistrer(new DocumentPaiement(DocumentPaiement.QUITTANCE, numero, c.bail.id(), echeanceId, null, montant,
                aujourdhui()), donnees, "quittance-" + numero + ".pdf");
        publierEmission(doc, c.bail);
    }

    /** Idempotent : un seul reçu par paiement ; rien pour un paiement contre-passé entre-temps ou pour l'avance. */
    @Transactional
    public void emettreRecu(UUID paiementId) {
        Paiement paiement = paiements.findById(paiementId).orElse(null);
        if (paiement == null || !Paiement.ENCAISSEMENT.equals(paiement.getType())) {
            return;
        }
        moteur.verrouiller(paiement.getBailId());
        if (documents.findByTypeAndPaiementId(DocumentPaiement.RECU, paiementId).isPresent()
                || paiements.existsByContrePassationDe(paiementId)) {
            return;
        }
        Contexte c = contexte(paiement.getBailId());
        List<DonneesDocument.Ligne> lignes = new ArrayList<>();
        long impute = 0;
        for (Imputation i : imputations.findByPaiementId(paiementId)) {
            if (i.getMontant() <= 0) {
                continue;
            }
            Echeance e = echeances.findById(i.getEcheanceId()).orElseThrow();
            impute += i.getMontant();
            lignes.add(new DonneesDocument.Ligne("Échéance du %s (période du %s au %s) : imputé %s".formatted(
                    Montants.date(e.getDateEcheance()), Montants.date(e.getPeriodeDebut()), Montants.date(e.getPeriodeFin()),
                    Montants.fcfa(i.getMontant())), "reste dû " + Montants.fcfa(e.reste())));
        }
        if (impute < paiement.getMontant()) {
            lignes.add(new DonneesDocument.Ligne("Crédit non encore imputé", Montants.fcfa(paiement.getMontant() - impute)));
        }
        String numero = prochainNumero("REC", "RECU");
        String reference = paiement.getReference() == null ? "" : ", référence " + paiement.getReference();
        String corps = "Reçu de %s la somme de %s, le %s, par %s%s, au titre du bail %s (logement %s). Bailleur : %s."
                .formatted(c.locataireNom, Montants.fcfa(paiement.getMontant()), Montants.date(paiement.getDatePaiement()),
                        libelleMode(paiement), reference, c.bail.numero(), c.logement, c.bailleur);
        DonneesDocument donnees = nettoyer(new DonneesDocument("REÇU DE PAIEMENT", numero, List.of(corps), "Imputation du paiement", lignes,
                "Ce reçu n'est pas une quittance de loyer : la quittance sera délivrée quand l'échéance sera entièrement payée.", emission()));
        DocumentPaiement doc = enregistrer(new DocumentPaiement(DocumentPaiement.RECU, numero, c.bail.id(), null, paiementId,
                paiement.getMontant(), aujourdhui()), donnees, "recu-" + numero + ".pdf");
        publierEmission(doc, c.bail);
    }

    // ================================================================== annulation

    /**
     * À appeler dans la transaction de la contre-passation, après réimputation : annule le reçu du paiement et les quittances
     * des échéances qui ne sont plus entièrement payées. Les PDF restent consultables par les gestionnaires.
     */
    void annulerPourContrePassation(Paiement origine) {
        String motif = "Contre-passation du paiement du " + Montants.date(origine.getDatePaiement());
        documents.findByTypeAndPaiementId(DocumentPaiement.RECU, origine.getId()).filter(d -> DocumentPaiement.EMIS.equals(d.getStatut()))
                .ifPresent(d -> annuler(d, motif));
        List<UUID> touchees = imputations.findByPaiementId(origine.getId()).stream().map(Imputation::getEcheanceId).distinct().toList();
        if (touchees.isEmpty()) {
            return;
        }
        Set<UUID> payees = new java.util.HashSet<>();
        echeances.findAllById(touchees).forEach(e -> {
            if (e.getStatut() == StatutEcheance.PAYEE) {
                payees.add(e.getId());
            }
        });
        documents.findByTypeAndEcheanceIdInAndStatut(DocumentPaiement.QUITTANCE, touchees, DocumentPaiement.EMIS).stream()
                .filter(d -> !payees.contains(d.getEcheanceId())).forEach(d -> annuler(d, motif));
    }

    private void annuler(DocumentPaiement d, String motif) {
        d.annuler(motif, OffsetDateTime.now(horloge));
        String auteur = acteurCourant.acteur().map(a -> a.id().toString()).orElse("systeme");
        evenements.publishEvent(new EvenementsPaiement.DocumentAnnule(d.getId(), d.getNumero(), motif, auteur));
    }

    // ================================================================== lecture

    @Transactional(readOnly = true)
    public Page<DocumentDto> lister(UUID bailId, UUID locataireId, String type, Pageable pageable) {
        UUID moi = acces.locataireImpose();
        UUID locataire = locataireId;
        if (moi != null) {
            if ((locataireId != null && !moi.equals(locataireId)) || (bailId != null && !acces.bailDe(bailId, moi))) {
                throw new ErreurMetier("BAIL_INTROUVABLE", "Introuvable", HttpStatus.NOT_FOUND);
            }
            locataire = moi;
        }
        List<UUID> bailIds = locataire == null ? null : baux.bauxDuLocataire(locataire).stream().map(BailResume::id).toList();
        if (bailIds != null && bailIds.isEmpty()) {
            return Page.empty(pageable);
        }
        boolean parLocataire = moi != null;
        Specification<DocumentPaiement> spec = (r, q, cb) -> {
            List<Predicate> c = new ArrayList<>();
            if (bailId != null) {
                c.add(cb.equal(r.get("bailId"), bailId));
            }
            if (bailIds != null) {
                c.add(r.get("bailId").in(bailIds));
            }
            if (type != null) {
                c.add(cb.equal(r.get("type"), type));
            }
            if (parLocataire) { // un locataire ne voit pas les documents annulés
                c.add(cb.equal(r.get("statut"), DocumentPaiement.EMIS));
            }
            return cb.and(c.toArray(Predicate[]::new));
        };
        Page<DocumentPaiement> page = documents.findAll(spec, pageable);
        Map<UUID, BailResume> cacheBaux = new HashMap<>();
        Map<UUID, String> cacheNoms = new HashMap<>();
        List<DocumentDto> dtos = page.getContent().stream().map(d -> {
            BailResume b = cacheBaux.computeIfAbsent(d.getBailId(), id -> baux.bail(id).orElse(null));
            String nom = b == null ? null : cacheNoms.computeIfAbsent(b.locataireId(), id -> tiers.locataire(id).map(l -> l.nom()).orElse(null));
            Echeance e = d.getEcheanceId() == null ? null : echeances.findById(d.getEcheanceId()).orElse(null);
            return new DocumentDto(d.getId(), d.getType(), d.getNumero(), d.getStatut(), d.getBailId(), b == null ? null : b.numero(),
                    b == null ? null : b.locataireId(), nom, d.getEcheanceId(), d.getPaiementId(), e == null ? null : e.getPeriodeDebut(),
                    e == null ? null : e.getPeriodeFin(), d.getMontant(), d.getDateEmission(), d.getFichierId(), d.getMotifAnnulation());
        }).toList();
        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }

    // ================================================================== interne

    private record Contexte(BailResume bail, String locataireNom, String logement, String bailleur) {
    }

    private Contexte contexte(UUID bailId) {
        BailResume bail = baux.bail(bailId).orElseThrow(() -> new ErreurMetier("BAIL_INTROUVABLE", "Bail introuvable", HttpStatus.NOT_FOUND));
        String nom = tiers.locataire(bail.locataireId()).map(l -> l.nom()).orElse("le locataire");
        LotResume lot = lots.lot(bail.lotId()).orElse(null);
        String logement = lot == null ? "" : "%s, immeuble %s, %s".formatted(lot.reference(), lot.immeubleNom(), lot.adresse());
        return new Contexte(bail, nom, logement, baux.nomBailleur());
    }

    private DocumentPaiement enregistrer(DocumentPaiement doc, DonneesDocument donnees, String nomFichier) {
        byte[] pdf = generateur.generer(donnees);
        FichierDto fichier = stockage.enregistrer("finance", "documents", doc.getId(), nomFichier, new ByteArrayInputStream(pdf), true);
        try {
            doc.attacherFichier(fichier.id());
            return documents.saveAndFlush(doc);
        } catch (RuntimeException e) {
            stockage.supprimer(fichier.id()); // le numéro réservé est annulé avec la transaction : aucun trou
            throw e;
        }
    }

    private void publierEmission(DocumentPaiement doc, BailResume bail) {
        evenements.publishEvent(new EvenementsPaiement.DocumentEmis(doc.getId(), doc.getType(), doc.getNumero(), bail.id(),
                bail.locataireId(), doc.getMontant(), doc.getFichierId()));
        LOG.info("{} {} émis", doc.getType(), doc.getNumero());
    }

    /** Numéro suivant de la série (type, année) : le verrou de ligne tient jusqu'à la fin de la transaction, d'où l'absence de trou. */
    private String prochainNumero(String prefixe, String type) {
        int annee = aujourdhui().getYear();
        Long n = jdbc.queryForObject("""
                INSERT INTO fin_compteur_numero (type, annee, dernier) VALUES (?, ?, 1)
                ON CONFLICT (type, annee) DO UPDATE SET dernier = fin_compteur_numero.dernier + 1
                RETURNING dernier
                """, Long.class, type, annee);
        return "%s-%d-%06d".formatted(prefixe, annee, n);
    }

    private static String libelleMode(Paiement p) {
        if (p.getMode() == null) {
            return "avance versée à la signature";
        }
        return switch (p.getMode()) {
            case "ESPECES" -> "espèces";
            case "VIREMENT" -> "virement";
            case "MTN_MOMO" -> "MTN MoMo";
            case "ORANGE_MONEY" -> "Orange Money";
            default -> p.getMode();
        };
    }

    private static DonneesDocument nettoyer(DonneesDocument d) {
        return new DonneesDocument(Montants.pourPdf(d.titre()), d.numero(), d.corps().stream().map(Montants::pourPdf).toList(),
                Montants.pourPdf(d.titreTableau()),
                d.lignes().stream().map(l -> new DonneesDocument.Ligne(Montants.pourPdf(l.libelle()), Montants.pourPdf(l.valeur()))).toList(),
                Montants.pourPdf(d.note()), Montants.pourPdf(d.emission()));
    }

    private String emission() {
        return "Émis le " + Montants.date(aujourdhui()) + ".";
    }

    private LocalDate aujourdhui() {
        return LocalDate.now(horloge.withZone(DOUALA));
    }
}
