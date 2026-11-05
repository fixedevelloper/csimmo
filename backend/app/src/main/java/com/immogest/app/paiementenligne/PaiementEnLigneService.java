package com.immogest.app.paiementenligne;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.immogest.app.bail.api.BailResume;
import com.immogest.app.bail.api.LecteurBaux;
import com.immogest.app.finance.ModePaiement;
import com.immogest.app.finance.PaiementSaisie;
import com.immogest.app.finance.PaiementService;
import com.immogest.app.finance.ResultatPaiement;
import com.immogest.app.licence.api.ControlePalier;
import com.immogest.app.paiementenligne.domain.Fournisseur;
import com.immogest.app.paiementenligne.domain.Transaction;
import com.immogest.app.paiementenligne.infra.AgregateurPaiement;
import com.immogest.app.paiementenligne.infra.AgregateurPaiement.AgregateurException;
import com.immogest.app.paiementenligne.infra.AgregateurPaiement.Etat;
import com.immogest.app.paiementenligne.infra.AgregateurPaiement.EtatDistant;
import com.immogest.app.paiementenligne.infra.AgregateurPaiement.EvenementWebhook;
import com.immogest.app.paiementenligne.infra.AgregateurPaiement.Initiation;
import com.immogest.app.paiementenligne.infra.AgregateurPaiement.SignatureInvalideException;
import com.immogest.app.paiementenligne.infra.FournisseurRepository;
import com.immogest.app.paiementenligne.infra.TransactionRepository;
import com.immogest.app.shared.ActeurCourant;
import com.immogest.app.shared.ErreurMetier;
import com.immogest.app.stockage.ChiffrementDonnees;
import com.immogest.app.tiers.api.LecteurTiers;
import jakarta.persistence.criteria.Predicate;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Clock;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Paiement Mobile Money. Règle n°8 : le paiement (et son imputation) n'est créé qu'une fois l'opérateur ayant confirmé, par webhook signé
 * ou par vérification de statut auprès de l'agrégateur ; jamais parce que le locataire ou son téléphone l'affirme.
 *
 * <p>Garanties : la confirmation est idempotente (verrou par transaction + clé d'idempotence côté finance + webhooks dédoublonnés), un
 * montant différent de celui demandé ne crée rien (ANOMALIE), et une confirmation tardive d'une transaction expirée est quand même
 * enregistrée, car l'argent a bien été débité.
 */
@Service
public class PaiementEnLigneService {

    private static final Logger LOG = LoggerFactory.getLogger(PaiementEnLigneService.class);
    private static final ObjectMapper JSON = new ObjectMapper();
    private static final TypeReference<Map<String, String>> MAP = new TypeReference<>() {
    };
    static final long MONTANT_MAX = 20_000_000L;
    static final int EN_ATTENTE_MAX = 3;
    static final Duration EXPIRATION = Duration.ofHours(24);
    static final Duration INITIATION_ABANDONNEE = Duration.ofMinutes(10);
    static final int LOT_VERIFICATION = 50;

    private final TransactionRepository transactions;
    private final FournisseurRepository fournisseurs;
    private final Map<String, AgregateurPaiement> agregateurs = new LinkedHashMap<>();
    private final PaiementService paiements;
    private final LecteurBaux baux;
    private final LecteurTiers tiers;
    private final ControlePalier licence;
    private final ChiffrementDonnees chiffrement;
    private final ActeurCourant acteurCourant;
    private final ApplicationEventPublisher evenements;
    private final TransactionTemplate tx;
    private final JdbcTemplate jdbc;
    private final Clock horloge;
    private final jakarta.persistence.EntityManager em;
    /** Installation sans Internet : l'agrégateur ne peut ni être appelé ni rappeler (webhooks). */
    private final boolean reseauLocal;

    PaiementEnLigneService(TransactionRepository transactions, FournisseurRepository fournisseurs, List<AgregateurPaiement> implementations,
            PaiementService paiements, LecteurBaux baux, LecteurTiers tiers, ControlePalier licence, ChiffrementDonnees chiffrement,
            ActeurCourant acteurCourant, ApplicationEventPublisher evenements, PlatformTransactionManager gestionnaire, JdbcTemplate jdbc, Clock horloge,
            jakarta.persistence.EntityManager em, @org.springframework.beans.factory.annotation.Value("${immogest.reseau-local:false}") boolean reseauLocal) {
        this.em = em;
        this.reseauLocal = reseauLocal;
        this.transactions = transactions;
        this.fournisseurs = fournisseurs;
        implementations.forEach(a -> agregateurs.put(a.code(), a));
        this.paiements = paiements;
        this.baux = baux;
        this.tiers = tiers;
        this.licence = licence;
        this.chiffrement = chiffrement;
        this.acteurCourant = acteurCourant;
        this.evenements = evenements;
        this.tx = new TransactionTemplate(gestionnaire);
        this.jdbc = jdbc;
        this.horloge = horloge;
    }

    // ================================================================== identifiants marchands (Paramètres)

    public List<FournisseurDto> fournisseurs() {
        return tx.execute(s -> agregateurs.values().stream().map(this::versDto).toList());
    }

    /** Un seul fournisseur actif à la fois ; activer exige tous les champs obligatoires. Un secret laissé vide est conservé. */
    public FournisseurDto modifierFournisseur(String code, boolean actif, Map<String, String> saisis) {
        AgregateurPaiement agregateur = agregateur(code);
        return tx.execute(s -> {
            Fournisseur f = fournisseurs.findByCode(code).orElseGet(() -> new Fournisseur(code));
            Map<String, String> config = new LinkedHashMap<>();
            Map<String, String> secrets = new LinkedHashMap<>(lireSecrets(f));
            Map<String, String> actuelle = lire(f.getConfig());
            Map<String, String> champs = saisis == null ? Map.of() : saisis;
            for (String nom : champs.keySet()) {
                if (agregateur.champs().stream().noneMatch(c -> c.nom().equals(nom))) {
                    throw new ErreurMetier("PAIEMENT_EN_LIGNE_CHAMP_INCONNU", "Champ inconnu : " + nom, HttpStatus.BAD_REQUEST);
                }
            }
            for (AgregateurPaiement.Champ c : agregateur.champs()) {
                String v = champs.get(c.nom());
                if (c.secret()) {
                    if (v != null && !v.isBlank()) {
                        secrets.put(c.nom(), v.strip());
                    }
                } else {
                    String retenue = champs.containsKey(c.nom()) ? v : actuelle.get(c.nom());
                    if (retenue != null && !retenue.isBlank()) {
                        if (retenue.length() > 500) {
                            throw new ErreurMetier("CHAMP_TROP_LONG", "Valeur trop longue pour " + c.nom(), HttpStatus.BAD_REQUEST);
                        }
                        config.put(c.nom(), retenue.strip());
                    }
                }
            }
            if (actif && !complet(agregateur, config, secrets)) {
                throw new ErreurMetier("PAIEMENT_EN_LIGNE_CONFIG_INCOMPLETE", "Renseignez tous les champs obligatoires avant d'activer ce fournisseur", HttpStatus.BAD_REQUEST);
            }
            f.modifier(actif, ecrire(config), secrets.isEmpty() ? null : chiffrement.chiffrer(ecrire(secrets)));
            fournisseurs.saveAndFlush(f);
            if (actif) { // un seul fournisseur actif
                fournisseurs.findByActifTrue().stream().filter(x -> !x.getCode().equals(code)).forEach(x -> x.modifier(false, x.getConfig(), x.getSecrets()));
            }
            evenements.publishEvent(new EvenementsPaiementEnLigne.FournisseurModifie(code, actif));
            return versDto(agregateur);
        });
    }

    // ================================================================== initiation par le locataire

    public record ResultatInitiation(TransactionDto transaction, boolean rejoue) {
    }

    /**
     * Le locataire demande à payer. Rien n'est enregistré comme paiement : on ouvre seulement une transaction EN_ATTENTE chez l'agrégateur,
     * que le locataire valide sur son téléphone. {@code cleIdempotence} (obligatoire) protège du double appui et des renvois réseau.
     */
    public ResultatInitiation initier(InitiationSaisie s, String cleIdempotence) {
        if (reseauLocal) {
            throw new ErreurMetier("PAIEMENT_EN_LIGNE_INDISPONIBLE", "Le paiement en ligne n'est pas disponible sur une installation en réseau local", HttpStatus.CONFLICT);
        }
        UUID locataire = acteurCourant.acteur().filter(a -> a.aLeRole("LOCATAIRE")).flatMap(a -> tiers.locataireDeUtilisateur(a.id()))
                .orElseThrow(() -> new ErreurMetier("ACCES_REFUSE", "Réservé aux locataires", HttpStatus.FORBIDDEN));
        String cle = cleIdempotence == null ? "" : cleIdempotence.strip();
        if (cle.isEmpty() || cle.length() > 100) {
            throw new ErreurMetier("IDEMPOTENCE_CLE_REQUISE", "L'en-tête Idempotency-Key est obligatoire (100 caractères au plus)", HttpStatus.BAD_REQUEST);
        }
        if (s.bailId() == null || s.montant() == null || s.operateur() == null || s.telephone() == null) {
            throw new ErreurMetier("PAIEMENT_EN_LIGNE_CHAMPS_REQUIS", "Bail, montant, opérateur et téléphone sont obligatoires", HttpStatus.BAD_REQUEST);
        }
        if (s.montant() <= 0 || s.montant() > MONTANT_MAX) {
            throw new ErreurMetier("PAIEMENT_EN_LIGNE_MONTANT_INVALIDE", "Le montant doit être compris entre 1 et %d FCFA".formatted(MONTANT_MAX), HttpStatus.BAD_REQUEST);
        }
        String telephone = telephone(s.telephone());
        BailResume bail = baux.bail(s.bailId()).filter(b -> locataire.equals(b.locataireId()) && !"BROUILLON".equals(b.statut()))
                .orElseThrow(() -> new ErreurMetier("BAIL_INTROUVABLE", "Bail introuvable", HttpStatus.NOT_FOUND));
        Fournisseur actif = fournisseurs.findByActifTrue().stream().filter(f -> agregateurs.containsKey(f.getCode())).findFirst()
                .orElseThrow(() -> new ErreurMetier("PAIEMENT_EN_LIGNE_INDISPONIBLE", "Le paiement en ligne n'est pas disponible", HttpStatus.CONFLICT));
        AgregateurPaiement agregateur = agregateurs.get(actif.getCode());
        Map<String, String> config = configComplete(actif);
        String empreinte = empreinte(bail.id(), s.montant(), s.operateur(), telephone);

        // 1. la transaction est écrite AVANT l'appel à l'agrégateur : on retrouve toujours ce qu'on a demandé
        record Cree(UUID id, boolean rejoue) {
        }
        Cree cree = tx.execute(st -> {
            jdbc.queryForObject("select pg_advisory_xact_lock(?)", Object.class, locataire.getMostSignificantBits() ^ locataire.getLeastSignificantBits());
            var existante = transactions.findByLocataireIdAndCleIdempotence(locataire, cle);
            if (existante.isPresent()) {
                if (!empreinte.equals(existante.get().getEmpreinte())) {
                    throw new ErreurMetier("IDEMPOTENCE_CONFLIT", "Cette Idempotency-Key a déjà servi pour une autre demande", HttpStatus.CONFLICT);
                }
                return new Cree(existante.get().getId(), true);
            }
            if (transactions.countByLocataireIdAndStatut(locataire, StatutTransaction.EN_ATTENTE) >= EN_ATTENTE_MAX) {
                throw new ErreurMetier("PAIEMENT_EN_LIGNE_TROP_DE_TRANSACTIONS", "Trop de paiements en attente : validez-les ou patientez", HttpStatus.CONFLICT);
            }
            Transaction t = transactions.saveAndFlush(new Transaction(bail.id(), locataire, actif.getCode(), s.operateur(), telephone, s.montant(), cle, empreinte));
            return new Cree(t.getId(), false);
        });
        if (cree.rejoue()) {
            return new ResultatInitiation(obtenirDto(cree.id(), null), true);
        }

        // 2. appel à l'agrégateur, hors transaction de base
        Initiation initiation;
        try {
            initiation = agregateur.initier(new AgregateurPaiement.Demande(cree.id().toString(), s.montant(), s.operateur(), telephone,
                    "Loyer " + bail.numero()), config);
        } catch (AgregateurException e) {
            tx.executeWithoutResult(st -> {
                transactions.findById(cree.id()).ifPresent(t -> t.terminer(StatutTransaction.ECHOUEE, "AGREGATEUR_INDISPONIBLE"));
                evenements.publishEvent(new EvenementsPaiementEnLigne.Echouee(cree.id(), "ECHOUEE", "AGREGATEUR_INDISPONIBLE"));
            });
            throw new ErreurMetier("AGREGATEUR_INDISPONIBLE", "Le service de paiement est momentanément indisponible", HttpStatus.BAD_GATEWAY);
        }
        tx.executeWithoutResult(st -> {
            transactions.findById(cree.id()).orElseThrow().referencer(initiation.referenceFournisseur());
            evenements.publishEvent(new EvenementsPaiementEnLigne.Initiee(cree.id(), bail.id(), s.montant(), s.operateur()));
        });
        return new ResultatInitiation(obtenirDto(cree.id(), initiation.instructions()), false);
    }

    // ================================================================== confirmation (webhook ou vérification)

    /**
     * Webhook de l'agrégateur. La signature est vérifiée avant tout ; un rejeu (même identifiant d'événement) ne refait rien ; un événement
     * authentique sur une transaction inconnue est ignoré sans erreur (l'agrégateur ne doit pas insister).
     */
    public ResultatWebhook recevoirWebhook(String code, Map<String, String> entetes, byte[] corps) {
        Fournisseur f = fournisseurs.findByCode(code).filter(Fournisseur::isActif).filter(x -> agregateurs.containsKey(x.getCode()))
                .orElseThrow(() -> new ErreurMetier("WEBHOOK_INCONNU", "Fournisseur inconnu", HttpStatus.NOT_FOUND));
        AgregateurPaiement agregateur = agregateurs.get(code);
        Map<String, String> config = tx.execute(s -> configComplete(f));
        EvenementWebhook evenement;
        try {
            var lu = agregateur.validerWebhook(entetes, corps, config);
            if (lu.isEmpty()) {
                return ResultatWebhook.IGNORE;
            }
            evenement = lu.get();
        } catch (SignatureInvalideException e) {
            tx.executeWithoutResult(s -> evenements.publishEvent(new EvenementsPaiementEnLigne.WebhookRejete(code))); // dans une transaction : sinon jamais audité
            LOG.warn("Webhook {} rejeté : signature invalide", code);
            throw new ErreurMetier("WEBHOOK_SIGNATURE_INVALIDE", "Signature invalide", HttpStatus.UNAUTHORIZED);
        }
        return tx.execute(s -> {
            // dédoublonnage et traitement dans la même transaction : si le traitement échoue, le webhook sera rejoué et traité
            int nouveau = jdbc.update("INSERT INTO ol_webhook_recu (id, fournisseur, evenement_id, recu_le) VALUES (?, ?, ?, now()) ON CONFLICT DO NOTHING",
                    UUID.randomUUID(), code, evenement.evenementId());
            if (nouveau == 0) {
                return ResultatWebhook.REJOUE;
            }
            var t = transactions.findByFournisseurAndReferenceFournisseur(code, evenement.referenceFournisseur());
            if (t.isEmpty()) {
                LOG.warn("Webhook {} : transaction inconnue", code);
                return ResultatWebhook.IGNORE;
            }
            appliquer(t.get().getId(), evenement.etat());
            return ResultatWebhook.TRAITE;
        });
    }

    /** Applique l'état annoncé par l'opérateur à une transaction (sous verrou). À appeler dans une transaction de base de données. */
    private void appliquer(UUID id, EtatDistant etat) {
        jdbc.queryForObject("select pg_advisory_xact_lock(?)", Object.class, id.getMostSignificantBits() ^ id.getLeastSignificantBits());
        Transaction t = transactions.findById(id).orElseThrow();
        em.refresh(t); // état réel après le verrou : une entité chargée avant l'attente serait périmée (confirmation faite entre-temps par un autre passage)
        switch (etat.etat()) {
            case SUCCES -> {
                if (t.getStatut() == StatutTransaction.CONFIRMEE || t.getStatut() == StatutTransaction.ANOMALIE) {
                    return;
                }
                if (etat.montant() != null && etat.montant() != t.getMontant()) {
                    t.terminer(StatutTransaction.ANOMALIE, "MONTANT_DIFFERENT");
                    evenements.publishEvent(new EvenementsPaiementEnLigne.Anomalie(id, t.getMontant(), etat.montant()));
                    LOG.error("Transaction {} : l'opérateur annonce {} FCFA au lieu de {}", id, etat.montant(), t.getMontant());
                    return;
                }
                String reference = etat.referenceOperateur() != null ? etat.referenceOperateur() : t.getReferenceFournisseur();
                // même clé d'idempotence côté finance : même en cas de course, un seul paiement existe pour cette transaction
                ResultatPaiement r = paiements.enregistrer(new PaiementSaisie(t.getBailId(), t.getMontant(), ModePaiement.valueOf(t.getOperateur().name()),
                        reference, null), "enligne-" + id);
                t.confirmer(r.paiement().id(), etat.referenceOperateur(), OffsetDateTime.now(horloge));
                evenements.publishEvent(new EvenementsPaiementEnLigne.Confirmee(id, r.paiement().id(), t.getMontant()));
            }
            case ECHEC -> {
                if (t.getStatut() == StatutTransaction.EN_ATTENTE) {
                    t.terminer(StatutTransaction.ECHOUEE, "REFUSEE_PAR_OPERATEUR");
                    evenements.publishEvent(new EvenementsPaiementEnLigne.Echouee(id, "ECHOUEE", "REFUSEE_PAR_OPERATEUR"));
                }
            }
            case EN_ATTENTE -> {
                // rien : l'opérateur n'a pas encore tranché
            }
        }
    }

    /**
     * Revérifie auprès de l'agrégateur les transactions EN_ATTENTE (webhook perdu, retard de l'opérateur) ; abandonne celles jamais
     * référencées ; expire au bout de 24 h. Sans effet si la licence n'autorise pas les écritures.
     *
     * @return nombre de transactions examinées
     */
    public int verifierEnAttente() {
        if (!licence.ecrituresAutorisees()) {
            return 0;
        }
        OffsetDateTime maintenant = OffsetDateTime.now(horloge);
        List<Transaction> liste = tx.execute(s -> transactions.aVerifier(maintenant.minusSeconds(30), maintenant.minusSeconds(60), PageRequest.of(0, LOT_VERIFICATION)));
        int n = 0;
        for (Transaction t : liste == null ? List.<Transaction>of() : liste) {
            n++;
            try {
                verifier(t.getId(), maintenant);
            } catch (RuntimeException e) {
                LOG.error("Vérification de la transaction {} : échec ({})", t.getId(), e.getClass().getSimpleName());
            }
        }
        return n;
    }

    private void verifier(UUID id, OffsetDateTime maintenant) {
        Transaction t = tx.execute(s -> transactions.findById(id).orElseThrow());
        if (t.getStatut() != StatutTransaction.EN_ATTENTE) {
            return;
        }
        if (t.getReferenceFournisseur() == null) {
            if (t.getCreatedAt().isBefore(maintenant.minus(INITIATION_ABANDONNEE))) { // l'agrégateur n'a jamais répondu : rien à attendre
                tx.executeWithoutResult(s -> transactions.findById(id).filter(x -> x.getStatut() == StatutTransaction.EN_ATTENTE)
                        .ifPresent(x -> x.terminer(StatutTransaction.ECHOUEE, "INITIATION_INCOMPLETE")));
            }
            return;
        }
        AgregateurPaiement agregateur = agregateurs.get(t.getFournisseur());
        Fournisseur f = fournisseurs.findByCode(t.getFournisseur()).orElse(null);
        if (agregateur == null || f == null) {
            return;
        }
        EtatDistant etat;
        try {
            etat = agregateur.verifierStatut(t.getReferenceFournisseur(), tx.execute(s -> configComplete(f)));
        } catch (AgregateurException e) {
            tx.executeWithoutResult(s -> transactions.findById(id).ifPresent(x -> x.noterVerification(maintenant)));
            return;
        }
        tx.executeWithoutResult(s -> {
            appliquer(id, etat);
            Transaction apres = transactions.findById(id).orElseThrow();
            apres.noterVerification(maintenant);
            if (apres.getStatut() == StatutTransaction.EN_ATTENTE && apres.getCreatedAt().isBefore(maintenant.minus(EXPIRATION))) {
                apres.terminer(StatutTransaction.EXPIREE, "SANS_CONFIRMATION");
                evenements.publishEvent(new EvenementsPaiementEnLigne.Echouee(id, "EXPIREE", "SANS_CONFIRMATION"));
            }
        });
    }

    // ================================================================== lecture

    public TransactionDto obtenir(UUID id) {
        return tx.execute(s -> {
            Transaction t = transactions.findById(id).orElseThrow(() -> new ErreurMetier("TRANSACTION_INTROUVABLE", "Transaction introuvable", HttpStatus.NOT_FOUND));
            exigerVisible(t);
            return versDto(t, null, new HashMap<>(), new HashMap<>());
        });
    }

    public Page<TransactionDto> lister(StatutTransaction statut, UUID bailId, Pageable pageable) {
        return tx.execute(s -> {
            UUID impose = locataireImpose();
            boolean estLocataire = acteurCourant.acteur().filter(a -> a.aLeRole("LOCATAIRE")).isPresent();
            UUID filtre = estLocataire ? (impose == null ? new UUID(0, 0) : impose) : null;
            Specification<Transaction> spec = (r, q, cb) -> {
                List<Predicate> c = new ArrayList<>();
                if (filtre != null) {
                    c.add(cb.equal(r.get("locataireId"), filtre));
                }
                if (statut != null) {
                    c.add(cb.equal(r.get("statut"), statut));
                }
                if (bailId != null) {
                    c.add(cb.equal(r.get("bailId"), bailId));
                }
                return cb.and(c.toArray(Predicate[]::new));
            };
            Page<Transaction> page = transactions.findAll(spec, pageable);
            Map<UUID, BailResume> cacheBaux = new HashMap<>();
            Map<UUID, String> cacheNoms = new HashMap<>();
            return new PageImpl<>(page.getContent().stream().map(t -> versDto(t, null, cacheBaux, cacheNoms)).toList(), pageable, page.getTotalElements());
        });
    }

    // ================================================================== interne

    private TransactionDto obtenirDto(UUID id, String instructions) {
        return tx.execute(s -> versDto(transactions.findById(id).orElseThrow(), instructions, new HashMap<>(), new HashMap<>()));
    }

    private void exigerVisible(Transaction t) {
        boolean estLocataire = acteurCourant.acteur().filter(a -> a.aLeRole("LOCATAIRE")).isPresent();
        if (estLocataire && !t.getLocataireId().equals(locataireImpose())) {
            throw new ErreurMetier("TRANSACTION_INTROUVABLE", "Transaction introuvable", HttpStatus.NOT_FOUND);
        }
    }

    private UUID locataireImpose() {
        return acteurCourant.acteur().filter(a -> a.aLeRole("LOCATAIRE")).flatMap(a -> tiers.locataireDeUtilisateur(a.id())).orElse(null);
    }

    private TransactionDto versDto(Transaction t, String instructions, Map<UUID, BailResume> cacheBaux, Map<UUID, String> cacheNoms) {
        BailResume bail = cacheBaux.computeIfAbsent(t.getBailId(), id -> baux.bail(id).orElse(null));
        String nom = cacheNoms.computeIfAbsent(t.getLocataireId(), id -> tiers.locataire(id).map(l -> l.nom()).orElse(null));
        String tel = t.getTelephone();
        String masque = tel.length() <= 3 ? tel : "•".repeat(tel.length() - 3) + tel.substring(tel.length() - 3);
        return new TransactionDto(t.getId(), t.getBailId(), bail == null ? null : bail.numero(), t.getLocataireId(), nom, t.getOperateur(), masque,
                t.getMontant(), t.getStatut(), instructions, t.getErreur(), t.getPaiementId(), t.getCreatedAt(), t.getConfirmeeLe());
    }

    private AgregateurPaiement agregateur(String code) {
        AgregateurPaiement a = agregateurs.get(code);
        if (a == null) {
            throw new ErreurMetier("PAIEMENT_EN_LIGNE_FOURNISSEUR_INCONNU", "Fournisseur non disponible", HttpStatus.NOT_FOUND);
        }
        return a;
    }

    private FournisseurDto versDto(AgregateurPaiement a) {
        Fournisseur f = fournisseurs.findByCode(a.code()).orElse(null);
        Map<String, String> config = f == null ? Map.of() : lire(f.getConfig());
        Map<String, String> secrets = f == null ? Map.of() : lireSecrets(f);
        List<FournisseurDto.ChampDto> champs = a.champs().stream().map(c -> new FournisseurDto.ChampDto(c.nom(), c.secret(), c.requis(),
                c.secret() ? null : config.get(c.nom()), c.secret() ? secrets.containsKey(c.nom()) : config.containsKey(c.nom()))).toList();
        return new FournisseurDto(a.code(), f != null && f.isActif(), complet(a, config, secrets), champs);
    }

    private static boolean complet(AgregateurPaiement a, Map<String, String> config, Map<String, String> secrets) {
        return a.champs().stream().filter(AgregateurPaiement.Champ::requis).allMatch(c -> {
            String v = c.secret() ? secrets.get(c.nom()) : config.get(c.nom());
            return v != null && !v.isBlank();
        });
    }

    private Map<String, String> configComplete(Fournisseur f) {
        Map<String, String> tout = new HashMap<>(lire(f.getConfig()));
        tout.putAll(lireSecrets(f));
        return tout;
    }

    private Map<String, String> lireSecrets(Fournisseur f) {
        return f == null || f.getSecrets() == null ? Map.of() : lire(chiffrement.dechiffrer(f.getSecrets()));
    }

    private static Map<String, String> lire(String json) {
        try {
            return JSON.readValue(json, MAP);
        } catch (Exception e) {
            throw new IllegalStateException("Configuration de fournisseur illisible", e);
        }
    }

    private static String ecrire(Map<String, String> m) {
        try {
            return JSON.writeValueAsString(m);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /** Numéro au format international sans « + » ni espaces (9 à 15 chiffres) ; les caractères de mise en forme usuels sont tolérés. */
    static String telephone(String saisie) {
        String chiffres = saisie.replaceAll("[\\s.\\-()]", "");
        if (chiffres.startsWith("+")) {
            chiffres = chiffres.substring(1);
        }
        if (!chiffres.matches("\\d{9,15}")) {
            throw new ErreurMetier("PAIEMENT_EN_LIGNE_TELEPHONE_INVALIDE", "Numéro de téléphone invalide", HttpStatus.BAD_REQUEST);
        }
        return chiffres;
    }

    private static String empreinte(UUID bailId, long montant, OperateurMobile operateur, String telephone) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest("%s|%d|%s|%s".formatted(bailId, montant, operateur, telephone).getBytes(StandardCharsets.UTF_8)));
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
