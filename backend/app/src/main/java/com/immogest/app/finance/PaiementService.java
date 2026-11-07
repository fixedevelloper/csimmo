package com.immogest.app.finance;

import com.immogest.app.bail.api.BailResume;
import com.immogest.app.bail.api.LecteurBaux;
import com.immogest.app.finance.domain.Echeance;
import com.immogest.app.finance.domain.Imputation;
import com.immogest.app.finance.domain.Paiement;
import com.immogest.app.finance.infra.EcheanceRepository;
import com.immogest.app.finance.infra.ImputationRepository;
import com.immogest.app.finance.infra.PaiementRepository;
import com.immogest.app.securite.UtilisateurService;
import com.immogest.app.shared.ActeurCourant;
import com.immogest.app.shared.ErreurMetier;
import com.immogest.app.tiers.api.LecteurTiers;
import jakarta.persistence.criteria.Predicate;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Clock;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Encaissements manuels. Un paiement est imputé sur l'échéance la plus ancienne d'abord (règle n°6) ; le surplus reste en crédit.
 * Rien n'est jamais supprimé ni modifié : l'annulation est une contre-passation (paiement inverse + imputations négatives).
 */
@Service
public class PaiementService {

    private static final ZoneId DOUALA = ZoneId.of("Africa/Douala");

    private final LecteurBaux baux;
    private final LecteurTiers tiers;
    private final PaiementRepository paiements;
    private final ImputationRepository imputations;
    private final EcheanceRepository echeances;
    private final EcheanceService moteur;
    private final UtilisateurService utilisateurs;
    private final ActeurCourant acteurCourant;
    private final ApplicationEventPublisher evenements;
    private final JdbcTemplate jdbc;
    private final Clock horloge;
    private final DocumentService documents;

    PaiementService(LecteurBaux baux, LecteurTiers tiers, PaiementRepository paiements, ImputationRepository imputations,
            EcheanceRepository echeances, EcheanceService moteur, UtilisateurService utilisateurs, ActeurCourant acteurCourant,
            ApplicationEventPublisher evenements, JdbcTemplate jdbc, Clock horloge, DocumentService documents) {
        this.documents = documents;
        this.baux = baux;
        this.tiers = tiers;
        this.paiements = paiements;
        this.imputations = imputations;
        this.echeances = echeances;
        this.moteur = moteur;
        this.utilisateurs = utilisateurs;
        this.acteurCourant = acteurCourant;
        this.evenements = evenements;
        this.jdbc = jdbc;
        this.horloge = horloge;
    }

    // ================================================================== saisie

    @Transactional
    public ResultatPaiement enregistrer(PaiementSaisie s, String cleIdempotence) {
        String cle = cleIdempotence == null || cleIdempotence.isBlank() ? null : cleIdempotence.strip();
        if (cle != null && cle.length() > 100) {
            throw new ErreurMetier("IDEMPOTENCE_CLE_INVALIDE", "Idempotency-Key trop longue (100 caractères max)", HttpStatus.BAD_REQUEST);
        }
        if (s.bailId() == null || s.mode() == null || s.montant() == null) {
            throw new ErreurMetier("PAIEMENT_CHAMPS_REQUIS", "Bail, montant et mode sont obligatoires", HttpStatus.BAD_REQUEST);
        }
        verifierBailVisible(s.bailId()); // employé restreint : hors périmètre = introuvable, avant tout rejeu d'idempotence
        // Verrou du bail d'abord : deux requêtes simultanées de même clé se suivent, la seconde voit la première
        moteur.verrouiller(s.bailId());
        String reference = texte(s.reference(), 100);
        String empreinte = empreinte(s, reference);
        if (cle != null) {
            Optional<Paiement> existant = paiements.findByCleIdempotence(cle);
            if (existant.isPresent()) {
                if (!empreinte.equals(existant.get().getEmpreinte())) {
                    throw new ErreurMetier("IDEMPOTENCE_CONFLIT", "Cette Idempotency-Key a déjà servi pour une autre requête", HttpStatus.CONFLICT);
                }
                return new ResultatPaiement(versDto(List.of(existant.get())).get(0), true);
            }
        }
        BailResume bail = baux.bail(s.bailId()).filter(b -> !"BROUILLON".equals(b.statut()))
                .orElseThrow(() -> new ErreurMetier("BAIL_INTROUVABLE", "Bail introuvable", HttpStatus.NOT_FOUND));
        if (s.montant() <= 0) {
            throw new ErreurMetier("PAIEMENT_MONTANT_INVALIDE", "Le montant doit être strictement positif (FCFA)", HttpStatus.BAD_REQUEST);
        }
        if (s.mode().referenceObligatoire() && reference == null) {
            throw new ErreurMetier("PAIEMENT_REFERENCE_REQUISE", "La référence de l'opération est obligatoire pour ce mode", HttpStatus.BAD_REQUEST);
        }
        LocalDate aujourdhui = aujourdhui();
        LocalDate date = s.datePaiement() == null ? aujourdhui : s.datePaiement();
        if (date.isAfter(aujourdhui)) {
            throw new ErreurMetier("PAIEMENT_DATE_FUTURE", "La date du paiement ne peut pas être dans le futur", HttpStatus.BAD_REQUEST);
        }
        UUID agent = acteurCourant.acteur().map(a -> a.id()).orElse(null);
        Paiement paiement = paiements.saveAndFlush(
                Paiement.encaissement(bail.id(), s.montant(), date, s.mode().name(), reference, agent, cle, empreinte));
        moteur.appliquerCredits(bail, aujourdhui);
        if (soldeToutCeQuilTouche(paiement)) {
            // rien à publier : la quittance de chaque échéance soldée est émise par EcheancePayee
        } else {
            evenements.publishEvent(new EvenementsPaiement.RecuAEmettre(paiement.getId()));
        }
        evenements.publishEvent(new EvenementsPaiement.PaiementEnregistre(paiement.getId(), bail.id(), bail.locataireId(), s.montant(),
                s.mode(), agent == null ? "systeme" : agent.toString()));
        return new ResultatPaiement(versDto(List.of(paiement)).get(0), false);
    }

    // ================================================================== contre-passation

    @Transactional
    public PaiementDto contrePasser(UUID paiementId, String motifSaisi) {
        Paiement origine = paiements.findById(paiementId)
                .orElseThrow(() -> new ErreurMetier("PAIEMENT_INTROUVABLE", "Paiement introuvable", HttpStatus.NOT_FOUND));
        moteur.verrouiller(origine.getBailId());
        String motif = texte(motifSaisi, 500);
        if (motif == null) {
            throw new ErreurMetier("PAIEMENT_MOTIF_REQUIS", "Le motif de la contre-passation est obligatoire", HttpStatus.BAD_REQUEST);
        }
        if (!Paiement.ENCAISSEMENT.equals(origine.getType())) {
            throw new ErreurMetier("PAIEMENT_NON_CONTRE_PASSABLE", "Seul un encaissement peut être contre-passé", HttpStatus.CONFLICT);
        }
        if (paiements.existsByContrePassationDe(origine.getId())) {
            throw new ErreurMetier("PAIEMENT_DEJA_CONTRE_PASSE", "Ce paiement a déjà été contre-passé", HttpStatus.CONFLICT);
        }
        BailResume bail = baux.bail(origine.getBailId())
                .orElseThrow(() -> new ErreurMetier("BAIL_INTROUVABLE", "Bail introuvable", HttpStatus.NOT_FOUND));
        LocalDate aujourdhui = aujourdhui();
        UUID agent = acteurCourant.acteur().map(a -> a.id()).orElse(null);
        Paiement inverse = paiements.saveAndFlush(Paiement.contrePassation(origine, aujourdhui, motif, agent));
        for (Imputation i : imputations.findByPaiementId(origine.getId())) {
            Echeance e = echeances.findById(i.getEcheanceId()).orElseThrow();
            imputations.save(new Imputation(inverse.getId(), e.getId(), -i.getMontant()));
            e.imputer(-i.getMontant());
        }
        // L'échéance rouverte repasse DUE / EN_RETARD ; un éventuel crédit d'un autre paiement peut la reprendre
        moteur.appliquerCredits(bail, aujourdhui);
        documents.annulerPourContrePassation(origine);
        evenements.publishEvent(new EvenementsPaiement.PaiementContrePasse(inverse.getId(), origine.getId(), bail.id(), origine.getMontant(),
                motif, agent == null ? "systeme" : agent.toString()));
        return versDto(List.of(inverse)).get(0);
    }

    /** Vrai si tout le paiement est imputé à des échéances désormais entièrement payées : il ne donne alors que des quittances. */
    private boolean soldeToutCeQuilTouche(Paiement paiement) {
        List<Imputation> liste = imputations.findByPaiementId(paiement.getId());
        long impute = liste.stream().mapToLong(Imputation::getMontant).sum();
        return !liste.isEmpty() && impute >= paiement.getMontant() && liste.stream()
                .allMatch(i -> echeances.findById(i.getEcheanceId()).map(e -> e.getStatut() == StatutEcheance.PAYEE).orElse(false));
    }

    // ================================================================== lecture

    @Transactional(readOnly = true)
    public Page<PaiementDto> lister(UUID bailId, UUID locataireId, ModePaiement mode, LocalDate du, LocalDate au, Pageable pageable) {
        UUID moi = locataireActeur();
        UUID locataire = locataireId;
        if (moi != null) { // un locataire ne voit que ses propres paiements
            if ((locataireId != null && !moi.equals(locataireId)) || (bailId != null && !baileDe(bailId, moi))) {
                throw new ErreurMetier("BAIL_INTROUVABLE", "Introuvable", HttpStatus.NOT_FOUND);
            }
            locataire = moi;
        }
        List<UUID> bailIds = locataire == null ? null : baux.bauxDuLocataire(locataire).stream().map(BailResume::id).toList();
        if (bailIds != null && bailIds.isEmpty()) {
            return Page.empty(pageable);
        }
        Optional<Set<UUID>> visibles = baux.bailIdsVisibles(); // employé restreint : seulement les baux de son périmètre
        if (visibles.isPresent() && bailId != null && !visibles.get().contains(bailId)) {
            throw new ErreurMetier("BAIL_INTROUVABLE", "Introuvable", HttpStatus.NOT_FOUND);
        }
        Specification<Paiement> spec = (racine, requete, cb) -> {
            List<Predicate> c = new ArrayList<>();
            if (visibles.isPresent()) {
                c.add(visibles.get().isEmpty() ? cb.disjunction() : racine.get("bailId").in(visibles.get()));
            }
            if (bailId != null) {
                c.add(cb.equal(racine.get("bailId"), bailId));
            }
            if (bailIds != null) {
                c.add(racine.get("bailId").in(bailIds));
            }
            if (mode != null) {
                c.add(cb.equal(racine.get("mode"), mode.name()));
            }
            if (du != null) {
                c.add(cb.greaterThanOrEqualTo(racine.get("datePaiement"), du));
            }
            if (au != null) {
                c.add(cb.lessThanOrEqualTo(racine.get("datePaiement"), au));
            }
            return cb.and(c.toArray(Predicate[]::new));
        };
        Page<Paiement> page = paiements.findAll(spec, pageable);
        List<PaiementDto> dtos = versDto(page.getContent());
        return new org.springframework.data.domain.PageImpl<>(dtos, pageable, page.getTotalElements());
    }

    /** Espèces saisies un jour donné, par agent, nettes des contre-passations. Un gestionnaire ne voit que sa propre ligne. */
    @Transactional(readOnly = true)
    public List<CaisseLigne> caisse(LocalDate jour) {
        LocalDate date = jour == null ? aujourdhui() : jour;
        Set<String> roles = acteurCourant.acteur().map(a -> a.roles()).orElse(Set.of());
        UUID seulement = roles.contains("ADMIN") ? null : acteurCourant.acteur().map(a -> a.id()).orElse(null);
        return jdbc.query("""
                SELECT saisi_par, SUM(CASE WHEN type = 'ENCAISSEMENT' THEN montant ELSE -montant END) AS total, COUNT(*) AS n
                FROM fin_paiement
                WHERE mode = 'ESPECES' AND type IN ('ENCAISSEMENT', 'CONTRE_PASSATION')
                  AND (created_at AT TIME ZONE 'Africa/Douala')::date = ?
                  AND (?::uuid IS NULL OR saisi_par = ?::uuid)
                GROUP BY saisi_par ORDER BY total DESC
                """, (rs, i) -> new CaisseLigne(rs.getObject("saisi_par", UUID.class), null, rs.getLong("total"), rs.getInt("n")),
                date, seulement, seulement).stream().map(l -> new CaisseLigne(l.agentId(), nomUtilisateur(l.agentId()), l.total(), l.operations())).toList();
    }

    /** Compte du locataire connecté (pour l'application mobile, qui ne connaît pas l'identifiant de sa fiche). */
    @Transactional(readOnly = true)
    public CompteLocataireDto monCompte() {
        UUID moi = locataireActeur();
        if (moi == null || new UUID(0, 0).equals(moi)) {
            throw new ErreurMetier("LOCATAIRE_INTROUVABLE", "Aucune fiche locataire n'est rattachée à ce compte", HttpStatus.NOT_FOUND);
        }
        return compte(moi);
    }

    @Transactional(readOnly = true)
    public CompteLocataireDto compte(UUID locataireId) {
        UUID moi = locataireActeur();
        if (moi != null && !moi.equals(locataireId)) {
            throw new ErreurMetier("LOCATAIRE_INTROUVABLE", "Locataire introuvable", HttpStatus.NOT_FOUND);
        }
        var locataire = tiers.locataire(locataireId)
                .orElseThrow(() -> new ErreurMetier("LOCATAIRE_INTROUVABLE", "Locataire introuvable", HttpStatus.NOT_FOUND));
        List<BailResume> liste = baux.bauxDuLocataire(locataireId);
        Optional<Set<UUID>> visibles = baux.bailIdsVisibles();
        if (visibles.isPresent()) { // employé restreint : seuls les baux de son périmètre comptent (jamais ceux d'un autre immeuble)
            liste = liste.stream().filter(b -> visibles.get().contains(b.id())).toList();
            if (liste.isEmpty()) {
                throw new ErreurMetier("LOCATAIRE_INTROUVABLE", "Locataire introuvable", HttpStatus.NOT_FOUND);
            }
        }
        List<UUID> ids = liste.stream().map(BailResume::id).toList();
        long arrieres = 0;
        long aVenir = 0;
        long credit = 0;
        if (!ids.isEmpty()) {
            LocalDate aujourdhui = aujourdhui();
            arrieres = echeances.resteEchuJusquA(ids, aujourdhui);
            aVenir = echeances.resteApres(ids, aujourdhui);
            for (UUID id : ids) {
                for (Paiement p : paiements.creditables(id)) {
                    credit += p.getMontant() - imputations.totalImpute(p.getId());
                }
            }
        }
        return new CompteLocataireDto(locataireId, locataire.nom(), arrieres, aVenir, credit, arrieres - credit,
                liste.stream().map(b -> new CompteLocataireDto.BailCompte(b.id(), b.numero(), b.statut())).toList());
    }

    // ================================================================== interne

    private void verifierBailVisible(UUID bailId) {
        if (baux.bailIdsVisibles().map(ids -> !ids.contains(bailId)).orElse(false)) {
            throw new ErreurMetier("BAIL_INTROUVABLE", "Bail introuvable", HttpStatus.NOT_FOUND);
        }
    }

    private List<PaiementDto> versDto(List<Paiement> liste) {
        Set<UUID> annules = new java.util.HashSet<>(liste.isEmpty() ? List.of()
                : paiements.dejaContrePasses(liste.stream().map(Paiement::getId).toList()));
        Map<UUID, BailResume> cacheBaux = new HashMap<>();
        Map<UUID, String> cacheLocataires = new HashMap<>();
        Map<UUID, String> cacheAgents = new HashMap<>();
        List<PaiementDto> resultat = new ArrayList<>();
        for (Paiement p : liste) {
            BailResume bail = cacheBaux.computeIfAbsent(p.getBailId(), id -> baux.bail(id).orElse(null));
            UUID locataireId = bail == null ? null : bail.locataireId();
            String nom = locataireId == null ? null
                    : cacheLocataires.computeIfAbsent(locataireId, id -> tiers.locataire(id).map(l -> l.nom()).orElse(null));
            String agent = p.getSaisiPar() == null ? null : cacheAgents.computeIfAbsent(p.getSaisiPar(), this::nomUtilisateur);
            resultat.add(new PaiementDto(p.getId(), p.getBailId(), bail == null ? null : bail.numero(), locataireId, nom, p.getType(),
                    p.getMode() == null ? null : ModePaiement.valueOf(p.getMode()), p.getMontant(), p.getDatePaiement(), p.getReference(),
                    p.getSaisiPar(), agent, p.getMotif(), annules.contains(p.getId()), p.getContrePassationDe(),
                    p.getCreatedAt() == null ? null : p.getCreatedAt()));
        }
        return resultat;
    }

    private String nomUtilisateur(UUID id) {
        if (id == null) {
            return null;
        }
        return utilisateurs.nom(id).orElse(null);
    }

    private UUID locataireActeur() {
        return acteurCourant.acteur().filter(a -> a.aLeRole("LOCATAIRE"))
                .map(a -> tiers.locataireDeUtilisateur(a.id()).orElse(new UUID(0, 0))).orElse(null);
    }

    private boolean baileDe(UUID bailId, UUID locataire) {
        return baux.bail(bailId).map(b -> locataire.equals(b.locataireId())).orElse(false);
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

    private static String empreinte(PaiementSaisie s, String reference) {
        String brut = "%s|%d|%s|%s|%s".formatted(s.bailId(), s.montant(), s.mode(), reference == null ? "" : reference,
                s.datePaiement() == null ? "" : s.datePaiement());
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(brut.getBytes(StandardCharsets.UTF_8)));
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private LocalDate aujourdhui() {
        return LocalDate.now(horloge.withZone(DOUALA));
    }
}
