package com.immogest.app.notification;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.immogest.app.notification.domain.Appareil;
import com.immogest.app.notification.domain.CanalConfig;
import com.immogest.app.notification.domain.Message;
import com.immogest.app.notification.infra.AppareilRepository;
import com.immogest.app.notification.infra.CanalConfigRepository;
import com.immogest.app.notification.infra.CanalNotification;
import com.immogest.app.notification.infra.EnvoiException;
import com.immogest.app.notification.infra.MessageRepository;
import com.immogest.app.notification.infra.ModelesMessages;
import com.immogest.app.shared.ActeurCourant;
import com.immogest.app.shared.EvenementMetier;
import com.immogest.app.shared.ErreurMetier;
import com.immogest.app.stockage.ChiffrementDonnees;
import com.immogest.app.tiers.api.LecteurTiers;
import jakarta.persistence.criteria.Predicate;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * File d'envoi persistante : les messages sont écrits en base, puis envoyés (1 essai + 3 réessais espacés), et restent ensuite comme
 * journal. Un message qu'on sait ne pas pouvoir envoyer (canal inactif, contact absent) est journalisé en ECHEC plutôt que perdu.
 */
@Service
@EnableConfigurationProperties(NotificationProperties.class)
public class NotificationService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationService.class);
    private static final ObjectMapper JSON = new ObjectMapper();
    private static final TypeReference<Map<String, String>> MAP = new TypeReference<>() {
    };
    private static final List<TypeCanal> ORDRE = List.of(TypeCanal.SMS, TypeCanal.WHATSAPP, TypeCanal.EMAIL, TypeCanal.PUSH);

    /** Publié après l'écriture de messages : déclenche l'envoi dès le commit (sans attendre la tâche périodique). */
    public record FileAlimentee() {
    }

    private final CanalConfigRepository configs;
    private final MessageRepository messages;
    private final AppareilRepository appareils;
    private final Map<TypeCanal, CanalNotification> canaux = new EnumMap<>(TypeCanal.class);
    private final ChiffrementDonnees chiffrement;
    private final LecteurTiers tiers;
    private final ModelesMessages modeles;
    private final TransactionTemplate transaction;
    private final ApplicationEventPublisher evenements;
    private final ActeurCourant acteurCourant;
    private final NotificationProperties proprietes;
    private final Clock horloge;

    NotificationService(CanalConfigRepository configs, MessageRepository messages, AppareilRepository appareils,
            List<CanalNotification> implementations, ChiffrementDonnees chiffrement, LecteurTiers tiers, ModelesMessages modeles,
            PlatformTransactionManager gestionnaireTransactions, ApplicationEventPublisher evenements, ActeurCourant acteurCourant,
            NotificationProperties proprietes, Clock horloge) {
        this.configs = configs;
        this.messages = messages;
        this.appareils = appareils;
        implementations.forEach(c -> canaux.put(c.type(), c));
        this.chiffrement = chiffrement;
        this.tiers = tiers;
        this.modeles = modeles;
        // REQUIRES_NEW : le déclencheur après-commit s'exécute encore dans la synchronisation de la transaction qui vient de se
        // terminer ; sans transaction propre, rien de ce qu'il modifie ne serait enregistré.
        this.transaction = new TransactionTemplate(gestionnaireTransactions);
        this.transaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        this.evenements = evenements;
        this.acteurCourant = acteurCourant;
        this.proprietes = proprietes;
        this.horloge = horloge;
    }

    // ================================================================== paramètres des canaux

    @Transactional(readOnly = true)
    public List<CanalParametresDto> parametres() {
        return ORDRE.stream().filter(canaux::containsKey).map(this::versDto).toList();
    }

    @Transactional
    public CanalParametresDto modifier(TypeCanal type, CanalSaisie saisie) {
        CanalNotification canal = canal(type);
        Map<String, String> config = new LinkedHashMap<>();
        Map<String, String> secrets = new LinkedHashMap<>(lireSecrets(configs.findByCanal(type).orElse(null)));
        Map<String, String> saisis = saisie.champs() == null ? Map.of() : saisie.champs();
        for (String nom : saisis.keySet()) {
            if (canal.champs().stream().noneMatch(c -> c.nom().equals(nom))) {
                throw new ErreurMetier("NOTIF_CHAMP_INCONNU", "Champ inconnu : " + nom, HttpStatus.BAD_REQUEST);
            }
        }
        Map<String, String> actuelle = configs.findByCanal(type).map(this::lireConfig).orElse(Map.of());
        for (CanalNotification.Champ c : canal.champs()) {
            String valeur = saisis.get(c.nom());
            if (c.secret()) {
                if (valeur != null && !valeur.isBlank()) {
                    secrets.put(c.nom(), valeur.strip());
                }
            } else {
                String retenue = saisis.containsKey(c.nom()) ? valeur : actuelle.get(c.nom());
                if (retenue != null && !retenue.isBlank()) {
                    if (retenue.length() > 2000) {
                        throw new ErreurMetier("CHAMP_TROP_LONG", "Valeur trop longue pour " + c.nom(), HttpStatus.BAD_REQUEST);
                    }
                    config.put(c.nom(), retenue.strip());
                }
            }
        }
        if (saisie.actif() && !complet(canal, config, secrets)) {
            throw new ErreurMetier("NOTIF_CONFIG_INCOMPLETE", "Renseignez tous les champs obligatoires avant d'activer ce canal", HttpStatus.BAD_REQUEST);
        }
        CanalConfig ligne = configs.findByCanal(type).orElseGet(() -> new CanalConfig(type));
        ligne.modifier(saisie.actif(), ecrire(config), secrets.isEmpty() ? null : chiffrement.chiffrer(ecrire(secrets)));
        configs.saveAndFlush(ligne);
        // Jamais de valeur de champ dans l'audit : seulement l'état d'activation
        evenements.publishEvent(new EvenementConfig(type, saisie.actif(), auteur()));
        return versDto(type);
    }

    /** Envoie un message de test tout de suite (hors file, hors journal) : la configuration peut être éprouvée avant d'être activée. */
    @Transactional(readOnly = true)
    public ResultatTest tester(TypeCanal type, String destinataire) {
        CanalNotification canal = canal(type);
        if (destinataire == null || destinataire.isBlank()) {
            throw new ErreurMetier("NOTIF_DESTINATAIRE_REQUIS", "Indiquez un destinataire pour le test", HttpStatus.BAD_REQUEST);
        }
        Map<String, String> config = configComplete(type);
        if (!complet(canal, config, config)) {
            return new ResultatTest(false, "Configuration incomplète");
        }
        ModelesMessages.Rendu r = modeles.rendre("TEST", proprietes.langue(), Map.of());
        try {
            canal.envoyer(new CanalNotification.Envoi(destinataire.strip(), r.sujet(), type == TypeCanal.SMS ? r.court() : r.corps()), config);
            return new ResultatTest(true, null);
        } catch (EnvoiException e) {
            return new ResultatTest(false, e.getMessage());
        } catch (RuntimeException e) {
            return new ResultatTest(false, "Erreur : " + e.getClass().getSimpleName());
        }
    }

    /** Identifiants Firebase publics à donner à l'application mobile, ou {@code push = null} si le canal n'est pas prêt. */
    @Transactional(readOnly = true)
    public ConfigMobileDto configMobile() {
        if (!pret(TypeCanal.PUSH)) {
            return new ConfigMobileDto(null);
        }
        Map<String, String> c = lireConfig(configs.findByCanal(TypeCanal.PUSH).orElse(null));
        String cle = c.get("apiKey");
        String app = c.get("appId");
        String expediteur = c.get("messagingSenderId");
        if (cle == null || app == null || expediteur == null) {
            return new ConfigMobileDto(null);
        }
        return new ConfigMobileDto(new ConfigMobileDto.PushMobile(c.get("projectId"), cle, app, expediteur));
    }

    // ================================================================== mise en file

    /**
     * Prépare les messages d'un locataire. {@code RECU_ESPECES} part toujours par SMS ; les autres types vont sur les canaux
     * WhatsApp, e-mail et push actifs, et par SMS seulement si aucun autre canal n'est disponible. Idempotent par (type, référence,
     * canal, destinataire).
     */
    @Transactional
    public void notifierLocataire(TypeMessage type, UUID locataireId, UUID referenceId, Map<String, String> variables) {
        notifierLocataire(type, locataireId, referenceId, variables, StrategieCanaux.DOCUMENTS);
    }

    /** @return identifiants des messages mis en file (vide si aucun canal n'est disponible pour ce locataire) */
    @Transactional
    public List<UUID> notifierLocataire(TypeMessage type, UUID locataireId, UUID referenceId, Map<String, String> variables,
            StrategieCanaux strategie) {
        var locataire = tiers.locataire(locataireId).filter(l -> !l.archive()).orElse(null);
        if (locataire == null) {
            return List.of();
        }
        Map<String, String> vars = new HashMap<>(variables);
        vars.put("nom", locataire.nom());
        ModelesMessages.Rendu rendu = modeles.rendre(type.name(), proprietes.langue(), vars);
        OffsetDateTime maintenant = OffsetDateTime.now(horloge);
        List<UUID> crees = new ArrayList<>();

        if (strategie == StrategieCanaux.PREFERENCE) {
            for (TypeCanal c : List.of(TypeCanal.WHATSAPP, TypeCanal.PUSH, TypeCanal.SMS)) {
                List<String> destinataires = pret(c) ? destinataires(c, locataire.telephone(), locataire.email(), locataireId) : List.of();
                if (destinataires.isEmpty()) {
                    continue;
                }
                String corps = c == TypeCanal.SMS || c == TypeCanal.PUSH ? rendu.court() : rendu.corps();
                for (String d : destinataires) {
                    UUID id = ajouter(c, type, d, rendu, corps, locataireId, referenceId, maintenant, null);
                    if (id != null) {
                        crees.add(id);
                    }
                }
                break; // un seul canal : le premier disponible
            }
            if (!crees.isEmpty()) {
                evenements.publishEvent(new FileAlimentee());
            }
            return crees;
        }

        List<TypeCanal> cibles = new ArrayList<>();
        if (type == TypeMessage.RECU_ESPECES) {
            cibles.add(TypeCanal.SMS);
        } else {
            for (TypeCanal c : List.of(TypeCanal.WHATSAPP, TypeCanal.EMAIL, TypeCanal.PUSH)) {
                if (pret(c)) {
                    cibles.add(c);
                }
            }
            if (cibles.isEmpty() && pret(TypeCanal.SMS)) {
                cibles.add(TypeCanal.SMS);
            }
        }
        for (TypeCanal c : cibles) {
            List<String> destinataires = destinataires(c, locataire.telephone(), locataire.email(), locataireId);
            String corps = c == TypeCanal.SMS || c == TypeCanal.PUSH ? rendu.court() : rendu.corps();
            if (destinataires.isEmpty()) {
                if (type == TypeMessage.RECU_ESPECES) { // le client doit voir qu'un reçu n'a pas pu partir
                    ajouter(c, type, "-", rendu, corps, locataireId, referenceId, maintenant, "Aucun contact pour ce canal");
                }
                continue;
            }
            for (String d : destinataires) {
                String refus = type == TypeMessage.RECU_ESPECES && !pret(c) ? "Canal SMS inactif ou non configuré" : null;
                UUID id = ajouter(c, type, d, rendu, corps, locataireId, referenceId, maintenant, refus);
                if (id != null && refus == null) {
                    crees.add(id);
                }
            }
        }
        if (!crees.isEmpty()) {
            evenements.publishEvent(new FileAlimentee());
        }
        return crees;
    }

    /** Statut d'envoi de messages précis (pour l'historique des relances). */
    @Transactional(readOnly = true)
    public Map<UUID, EtatMessage> etats(java.util.Collection<UUID> ids) {
        Map<UUID, EtatMessage> r = new HashMap<>();
        if (!ids.isEmpty()) {
            messages.findAllById(ids).forEach(m -> r.put(m.getId(), new EtatMessage(m.getStatut(), m.getCanal())));
        }
        return r;
    }

    private List<String> destinataires(TypeCanal c, String telephone, String email, UUID locataireId) {
        return switch (c) {
            case SMS, WHATSAPP -> telephone == null ? List.of() : List.of(telephone);
            case EMAIL -> email == null ? List.of() : List.of(email);
            case PUSH -> tiers.utilisateurDuLocataire(locataireId).map(u -> appareils.findByUtilisateurId(u).stream().map(Appareil::getToken).toList())
                    .orElse(List.of());
        };
    }

    private UUID ajouter(TypeCanal canal, TypeMessage type, String destinataire, ModelesMessages.Rendu rendu, String corps,
            UUID locataireId, UUID referenceId, OffsetDateTime maintenant, String refus) {
        String cle = "%s:%s:%s:%s".formatted(type, referenceId, canal, destinataire);
        if (messages.existsByCleDedoublonnage(cle)) {
            return null;
        }
        Message m = new Message(canal, type, destinataire, rendu.sujet(), corps, locataireId, referenceId, cle, maintenant);
        if (refus != null) {
            m.echouerDirectement(refus);
        }
        messages.save(m);
        return m.getId();
    }

    // ================================================================== envoi

    /** Envoie les messages dus, un par transaction (SKIP LOCKED : sans danger si plusieurs passages se croisent). */
    public int traiterFile() {
        int n = 0;
        while (n < 200) {
            Boolean traite = transaction.execute(s -> messages.prochainDu(OffsetDateTime.now(horloge)).map(m -> {
                envoyer(m);
                return true;
            }).orElse(false));
            if (!Boolean.TRUE.equals(traite)) {
                break;
            }
            n++;
        }
        return n;
    }

    private void envoyer(Message m) {
        OffsetDateTime maintenant = OffsetDateTime.now(horloge);
        String erreur = null;
        try {
            if (!pret(m.getCanal())) {
                erreur = "Canal inactif ou non configuré";
            } else {
                canal(m.getCanal()).envoyer(new CanalNotification.Envoi(m.getDestinataire(), m.getSujet(), m.getCorps()), configComplete(m.getCanal()));
            }
        } catch (EnvoiException e) {
            erreur = e.getMessage();
        } catch (RuntimeException e) {
            erreur = "Erreur : " + e.getClass().getSimpleName();
        }
        if (erreur == null) {
            m.marquerEnvoye(maintenant);
            return;
        }
        int deja = m.getTentatives() + 1;
        OffsetDateTime prochaine = deja >= proprietes.tentativesMax() ? null : maintenant.plus(proprietes.delaisReessai().get(deja - 1));
        m.marquerEchec(erreur, prochaine);
        LOG.warn("Notification {} ({}) : échec {}/{} — {}", m.getId(), m.getCanal(), deja, proprietes.tentativesMax(), erreur);
    }

    /** Pour un code OTP : envoi direct par SMS, jamais écrit dans la file ni dans le journal. */
    public boolean smsDisponible() {
        return transaction.execute(s -> pret(TypeCanal.SMS));
    }

    public void envoyerSmsDirect(String telephone, String texte) {
        try {
            canal(TypeCanal.SMS).envoyer(new CanalNotification.Envoi(telephone, null, texte), configComplete(TypeCanal.SMS));
        } catch (EnvoiException | RuntimeException e) {
            throw new ErreurMetier("OTP_ENVOI_ECHEC", "L'envoi du SMS a échoué", HttpStatus.BAD_GATEWAY);
        }
    }

    public String texteOtp(String code) {
        return modeles.court("OTP", proprietes.langue(), Map.of("code", code));
    }

    // ================================================================== journal

    @Transactional(readOnly = true)
    public Page<MessageDto> journal(StatutMessage statut, TypeCanal canal, Pageable pageable) {
        Specification<Message> spec = (r, q, cb) -> {
            List<Predicate> c = new ArrayList<>();
            if (statut != null) {
                c.add(cb.equal(r.get("statut"), statut));
            }
            if (canal != null) {
                c.add(cb.equal(r.get("canal"), canal));
            }
            return cb.and(c.toArray(Predicate[]::new));
        };
        return messages.findAll(spec, pageable).map(m -> new MessageDto(m.getId(), m.getCanal(), m.getTypeMessage(), m.getDestinataire(),
                m.getSujet(), m.getCorps(), m.getStatut(), m.getTentatives(), m.getProchaineTentative(), m.getDerniereErreur(),
                m.getEnvoyeLe(), m.getCreatedAt()));
    }

    /** Remet un message en ECHEC dans la file (nouveau cycle d'essais). */
    @Transactional
    public void reessayer(UUID id) {
        Message m = messages.findById(id).orElseThrow(() -> new ErreurMetier("NOTIF_MESSAGE_INTROUVABLE", "Message introuvable", HttpStatus.NOT_FOUND));
        if (m.getStatut() != StatutMessage.ECHEC) {
            throw new ErreurMetier("NOTIF_NON_RELANCABLE", "Seul un message en échec peut être relancé", HttpStatus.CONFLICT);
        }
        m.relancer(OffsetDateTime.now(horloge));
        evenements.publishEvent(new FileAlimentee());
    }

    // ================================================================== appareils push

    @Transactional
    public void enregistrerAppareil(UUID utilisateurId, String token, String plateforme) {
        String t = token == null ? "" : token.strip();
        if (t.isEmpty() || t.length() > 4096 || !List.of("ANDROID", "IOS").contains(plateforme)) {
            throw new ErreurMetier("APPAREIL_INVALIDE", "Jeton ou plateforme invalide", HttpStatus.BAD_REQUEST);
        }
        Optional<Appareil> existant = appareils.findByToken(t);
        if (existant.isPresent()) {
            existant.get().rattacher(utilisateurId, plateforme);
        } else {
            appareils.save(new Appareil(utilisateurId, t, plateforme));
        }
    }

    @Transactional
    public void retirerAppareil(UUID utilisateurId, String token) {
        appareils.findByToken(token == null ? "" : token.strip()).filter(a -> a.getUtilisateurId().equals(utilisateurId)).ifPresent(appareils::delete);
    }

    // ================================================================== interne

    private CanalNotification canal(TypeCanal type) {
        CanalNotification c = canaux.get(type);
        if (c == null) {
            throw new ErreurMetier("NOTIF_CANAL_INCONNU", "Canal non disponible", HttpStatus.NOT_FOUND);
        }
        return c;
    }

    private CanalParametresDto versDto(TypeCanal type) {
        CanalNotification canal = canal(type);
        CanalConfig ligne = configs.findByCanal(type).orElse(null);
        Map<String, String> config = lireConfig(ligne);
        Map<String, String> secrets = lireSecrets(ligne);
        List<ChampDto> champs = canal.champs().stream().map(c -> new ChampDto(c.nom(), c.secret(), c.requis(),
                c.secret() ? null : config.getOrDefault(c.nom(), c.defaut()),
                c.secret() ? secrets.containsKey(c.nom()) : config.containsKey(c.nom()))).toList();
        return new CanalParametresDto(type, ligne != null && ligne.isActif(), complet(canal, config, secrets), champs);
    }

    private boolean complet(CanalNotification canal, Map<String, String> config, Map<String, String> secrets) {
        return canal.champs().stream().filter(CanalNotification.Champ::requis).allMatch(c -> {
            String v = c.secret() ? secrets.get(c.nom()) : config.get(c.nom());
            return v != null && !v.isBlank();
        });
    }

    /** Canal activé par le client ET entièrement configuré. */
    private boolean pret(TypeCanal type) {
        CanalNotification canal = canaux.get(type);
        CanalConfig ligne = configs.findByCanal(type).orElse(null);
        return canal != null && ligne != null && ligne.isActif() && complet(canal, lireConfig(ligne), lireSecrets(ligne));
    }

    private Map<String, String> configComplete(TypeCanal type) {
        CanalConfig ligne = configs.findByCanal(type).orElse(null);
        Map<String, String> tout = new HashMap<>(lireConfig(ligne));
        tout.putAll(lireSecrets(ligne));
        return tout;
    }

    private Map<String, String> lireConfig(CanalConfig ligne) {
        return ligne == null ? Map.of() : lire(ligne.getConfig());
    }

    private Map<String, String> lireSecrets(CanalConfig ligne) {
        return ligne == null || ligne.getSecrets() == null ? Map.of() : lire(chiffrement.dechiffrer(ligne.getSecrets()));
    }

    private static Map<String, String> lire(String json) {
        try {
            return JSON.readValue(json, MAP);
        } catch (Exception e) {
            throw new IllegalStateException("Configuration de canal illisible", e);
        }
    }

    private static String ecrire(Map<String, String> map) {
        try {
            return JSON.writeValueAsString(map);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private String auteur() {
        return acteurCourant.acteur().map(a -> a.id().toString()).orElse("systeme");
    }

    /** Modification de la configuration d'un canal (audité, sans aucune valeur de champ). */
    public record EvenementConfig(TypeCanal canal, boolean actif, String auteur) implements EvenementMetier {
        @Override
        public String action() {
            return "NOTIFICATION.CANAL_MODIFIE";
        }

        @Override
        public String entite() {
            return "notification_canal";
        }

        @Override
        public UUID entiteId() {
            return null;
        }

        @Override
        public Object apres() {
            return Map.of("canal", canal.name(), "actif", actif);
        }
    }
}
