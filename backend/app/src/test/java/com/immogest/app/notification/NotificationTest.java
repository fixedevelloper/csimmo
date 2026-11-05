package com.immogest.app.notification;

import static com.immogest.app.securite.SecuriteTestSupport.json;
import static com.immogest.app.securite.SecuriteTestSupport.jwtRole;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.immogest.app.IntegrationTest;
import com.immogest.app.notification.infra.CanauxSimules.Registre;
import com.immogest.app.securite.OtpSender;
import com.immogest.app.securite.Role;
import com.immogest.app.securite.UtilisateurService;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

/** Notifications : configuration des canaux, file persistante avec réessais, déclencheurs finance, OTP, droits. Canaux simulés. */
@IntegrationTest
@TestPropertySource(properties = {"immogest.notification.delais-reessai=0s,0s,0s", "immogest.finance.horizon-jours=45"})
class NotificationTest {

    private static final RequestPostProcessor ADMIN = jwtRole(Role.ADMIN, UUID.fromString("00000000-0000-0000-0000-0000000000a1"));
    private static final RequestPostProcessor GESTIONNAIRE = jwtRole(Role.GESTIONNAIRE);
    private static final AtomicLong TEL = new AtomicLong(650_000_000L);

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    JdbcTemplate jdbc;
    @Autowired
    NotificationService service;
    @Autowired
    Registre registre;
    @Autowired
    UtilisateurService utilisateurs;
    @Autowired
    OtpSender otp;

    UUID immeuble;
    LocalDate aujourdhui;

    @BeforeEach
    void preparer() throws Exception {
        aujourdhui = LocalDate.now(ZoneId.of("Africa/Douala"));
        attendreOutboxVide();
        jdbc.execute("TRUNCATE notif_message, notif_appareil, notif_canal, fin_document, fin_compteur_numero, fin_imputation, fin_paiement, fin_echeance, bail_document, bail_garant, bail, patrimoine_photo, compteur, lot_equipement, lot, immeuble, garant, tiers_piece, locataire CASCADE");
        registre.reinitialiser();
        immeuble = id(mvc.perform(post("/api/v1/immeubles").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("nom", "Résidence Notif", "adresse", "1 rue", "ville", "Douala"))));
    }

    private void attendreOutboxVide() {
        try {
            await().atMost(Duration.ofSeconds(20)).until(() -> jdbc.queryForObject("SELECT count(*) FROM event_publication WHERE completion_date IS NULL", Long.class) == 0);
        } catch (org.awaitility.core.ConditionTimeoutException ignore) {
            // des publications en échec ne bloquent pas les tests suivants
        }
    }

    private UUID id(ResultActions r) throws Exception {
        return UUID.fromString(mapper.readTree(r.andReturn().getResponse().getContentAsString()).path("id").asText());
    }

    private UUID locataire(String email, UUID compte) throws Exception {
        return id(mvc.perform(post("/api/v1/locataires").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("nom", "Paul Notif", "telephone", "+237" + TEL.incrementAndGet(), "email", email, "utilisateurId", compte == null ? null : compte.toString())))
                .andExpect(status().isCreated()));
    }

    private UUID bailActif(UUID loc, String ref) throws Exception {
        UUID lot = id(mvc.perform(post("/api/v1/lots").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("immeubleId", immeuble.toString(), "reference", ref, "type", "STUDIO"))));
        UUID b = id(mvc.perform(post("/api/v1/baux").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("lotId", lot.toString(), "locataireId", loc.toString(), "dateDebut", aujourdhui.withDayOfMonth(1).minusMonths(2).toString(),
                        "loyer", 150_000, "charges", 10_000, "periodicite", "MENSUELLE", "jourEcheance", 1, "moisAvance", 0))).andExpect(status().isCreated()));
        mvc.perform(post("/api/v1/baux/{id}/activer", b).with(ADMIN)).andExpect(status().isOk());
        await().atMost(Duration.ofSeconds(15)).until(() -> jdbc.queryForObject("SELECT count(*) FROM fin_echeance WHERE bail_id = ?", Long.class, b) >= 3);
        return b;
    }

    private void payer(UUID bail, long montant, String mode) throws Exception {
        mvc.perform(post("/api/v1/paiements").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("bailId", bail.toString(), "montant", montant, "mode", mode, "reference", "REF1"))).andExpect(status().isCreated());
    }

    private void activerCanal(String canal, Map<String, String> champs) throws Exception {
        mvc.perform(put("/api/v1/notifications/canaux/{c}", canal).with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("actif", true, "champs", champs))).andExpect(status().isOk()).andExpect(jsonPath("$.actif").value(true));
    }

    private JsonNode journal(String query) throws Exception {
        return mapper.readTree(mvc.perform(get("/api/v1/notifications/journal" + query).with(ADMIN)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString()).path("content");
    }

    // ================================================================== configuration

    @Test
    void configurationDesCanauxSecretsMasquesEtChiffres() throws Exception {
        mvc.perform(get("/api/v1/notifications/canaux").with(ADMIN)).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0].canal").value("SMS")).andExpect(jsonPath("$[0].actif").value(false)).andExpect(jsonPath("$[0].configure").value(false));

        mvc.perform(put("/api/v1/notifications/canaux/WHATSAPP").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(json("actif", true, "champs", Map.of("phoneNumberId", "123"))))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("NOTIF_CONFIG_INCOMPLETE"));
        mvc.perform(put("/api/v1/notifications/canaux/WHATSAPP").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(json("actif", false, "champs", Map.of("inconnu", "x"))))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("NOTIF_CHAMP_INCONNU"));

        String reponse = mvc.perform(put("/api/v1/notifications/canaux/WHATSAPP").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(json("actif", true, "champs", Map.of("phoneNumberId", "123", "token", "EAAG-SUPER-SECRET"))))
                .andExpect(status().isOk()).andExpect(jsonPath("$.configure").value(true)).andReturn().getResponse().getContentAsString();
        assertThat(reponse).doesNotContain("EAAG-SUPER-SECRET");
        JsonNode token = mapper.readTree(reponse).path("champs").get(1);
        assertThat(token.path("nom").asText()).isEqualTo("token");
        assertThat(token.path("secret").asBoolean()).isTrue();
        assertThat(token.path("renseigne").asBoolean()).isTrue();
        assertThat(token.path("valeur").isNull() || token.path("valeur").isMissingNode()).isTrue();
        assertThat(jdbc.queryForObject("SELECT secrets FROM notif_canal WHERE canal = 'WHATSAPP'", String.class)).isNotBlank().doesNotContain("EAAG-SUPER-SECRET");
        assertThat(jdbc.queryForObject("SELECT config FROM notif_canal WHERE canal = 'WHATSAPP'", String.class)).doesNotContain("EAAG-SUPER-SECRET");

        // un secret laissé vide conserve la valeur ; la lecture ne le révèle jamais
        String relu = mvc.perform(put("/api/v1/notifications/canaux/WHATSAPP").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(json("actif", true, "champs", Map.of("phoneNumberId", "456", "token", ""))))
                .andExpect(status().isOk()).andExpect(jsonPath("$.configure").value(true)).andReturn().getResponse().getContentAsString();
        assertThat(relu).doesNotContain("EAAG-SUPER-SECRET").contains("456");
        mvc.perform(get("/api/v1/notifications/canaux").with(ADMIN)).andExpect(jsonPath("$[1].champs[1].renseigne").value(true));
        assertThat(registre.envois()).isEmpty();
    }

    @Test
    void testDeCanal() throws Exception {
        mvc.perform(post("/api/v1/notifications/canaux/SMS/test").with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("destinataire", "+237699112233")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.succes").value(false)).andExpect(jsonPath("$.erreur").value("Configuration incomplète"));
        // la configuration s'éprouve avant d'être activée
        mvc.perform(put("/api/v1/notifications/canaux/SMS").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("actif", false, "champs", Map.of("url", "https://sms.exemple.cm/send")))).andExpect(status().isOk());
        mvc.perform(post("/api/v1/notifications/canaux/SMS/test").with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("destinataire", "+237699112233")))
                .andExpect(jsonPath("$.succes").value(true));
        assertThat(registre.envois()).hasSize(1);
        assertThat(registre.envois().get(0).envoi().corps()).contains("message de test");
        assertThat(jdbc.queryForObject("SELECT count(*) FROM notif_message", Long.class)).isZero(); // un test n'entre pas dans la file
        registre.echouerProchains(TypeCanal.SMS, 1);
        mvc.perform(post("/api/v1/notifications/canaux/SMS/test").with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("destinataire", "+237699112233")))
                .andExpect(jsonPath("$.succes").value(false)).andExpect(jsonPath("$.erreur").value("Échec simulé"));
    }

    // ================================================================== déclencheurs

    @Test
    void encaissementEnEspecesEnvoieUnRecuSms() throws Exception {
        activerCanal("SMS", Map.of("url", "https://sms.exemple.cm/send"));
        UUID loc = locataire(null, null);
        UUID bail = bailActif(loc, "N1");
        payer(bail, 160_000, "ESPECES");

        await().atMost(Duration.ofSeconds(20)).untilAsserted(() -> assertThat(registre.envois().stream().filter(t -> t.canal() == TypeCanal.SMS).count()).isGreaterThanOrEqualTo(1));
        JsonNode sms = journal("?canal=SMS&statut=ENVOYE");
        assertThat(sms.size()).isGreaterThanOrEqualTo(1);
        String corps = sms.findValuesAsText("corps").stream().filter(c -> c.contains("espèces")).findFirst().orElseThrow();
        assertThat(corps).contains("160 000 FCFA").contains(Montants160());
        assertThat(sms.get(0).path("destinataire").asText()).startsWith("+237");
    }

    private String Montants160() {
        return "reçu en espèces";
    }

    @Test
    void paiementEspecesSansCanalSmsEstJournaliseEnEchecPlutotQuePerdu() throws Exception {
        UUID loc = locataire(null, null);
        UUID bail = bailActif(loc, "N2");
        payer(bail, 50_000, "ESPECES");
        await().atMost(Duration.ofSeconds(20)).untilAsserted(() -> assertThat(journal("?statut=ECHEC").size()).isEqualTo(1));
        JsonNode echec = journal("?statut=ECHEC").get(0);
        assertThat(echec.path("derniereErreur").asText()).contains("inactif ou non configuré");
        assertThat(echec.path("typeMessage").asText()).isEqualTo("RECU_ESPECES");
        assertThat(registre.envois()).isEmpty();
    }

    @Test
    void virementNEnvoiePasDeRecuSmsMaisLaQuittanceVaSurLesCanauxDocuments() throws Exception {
        activerCanal("SMS", Map.of("url", "https://sms.exemple.cm/send"));
        activerCanal("EMAIL", Map.of("hote", "smtp.exemple.cm", "expediteur", "noreply@exemple.cm"));
        UUID loc = locataire("paul@exemple.cm", null);
        UUID bail = bailActif(loc, "N3");
        payer(bail, 160_000, "VIREMENT");

        await().atMost(Duration.ofSeconds(25)).untilAsserted(() -> assertThat(registre.envois().stream().filter(t -> t.canal() == TypeCanal.EMAIL).count()).isEqualTo(1));
        var mail = registre.envois().stream().filter(t -> t.canal() == TypeCanal.EMAIL).findFirst().orElseThrow();
        assertThat(mail.envoi().destinataire()).isEqualTo("paul@exemple.cm");
        assertThat(mail.envoi().sujet()).startsWith("Votre quittance de loyer QUI-");
        assertThat(mail.envoi().corps()).contains("Paul Notif").contains("160 000");
        assertThat(mail.config().get("hote")).isEqualTo("smtp.exemple.cm");
        // l'e-mail est disponible : pas de SMS de repli, et pas de reçu SMS pour un virement
        assertThat(registre.envois().stream().filter(t -> t.canal() == TypeCanal.SMS)).isEmpty();
    }

    @Test
    void smsEnRepliSiAucunAutreCanalDocumentEstActif() throws Exception {
        activerCanal("SMS", Map.of("url", "https://sms.exemple.cm/send"));
        UUID loc = locataire(null, null);
        UUID bail = bailActif(loc, "N4");
        payer(bail, 160_000, "VIREMENT");
        await().atMost(Duration.ofSeconds(25)).untilAsserted(() -> assertThat(registre.envois().stream()
                .anyMatch(t -> t.canal() == TypeCanal.SMS && t.envoi().corps().contains("quittance QUI-"))).isTrue());
    }

    @Test
    void aucunCanalActifAucunMessageDeDocument() throws Exception {
        UUID loc = locataire("paul@exemple.cm", null);
        UUID bail = bailActif(loc, "N5");
        payer(bail, 160_000, "VIREMENT");
        await().atMost(Duration.ofSeconds(20)).until(() -> jdbc.queryForObject("SELECT count(*) FROM fin_document", Long.class) >= 1);
        attendreOutboxVide();
        assertThat(jdbc.queryForObject("SELECT count(*) FROM notif_message", Long.class)).isZero();
    }

    @Test
    void pushVersLesAppareilsDuLocataire() throws Exception {
        activerCanal("PUSH", Map.of("projectId", "p", "compteService", "{}"));
        UUID compte = utilisateurs.creer(new UtilisateurService.Saisie("Compte", "+237" + TEL.incrementAndGet(), null, Role.LOCATAIRE, true, false), "Motdepasse123").id();
        UUID loc = locataire(null, compte);
        RequestPostProcessor moi = jwtRole(Role.LOCATAIRE, compte);
        mvc.perform(post("/api/v1/notifications/appareils").with(moi).contentType(MediaType.APPLICATION_JSON).content(json("token", "fcm-tok-1", "plateforme", "ANDROID")))
                .andExpect(status().isNoContent());
        mvc.perform(post("/api/v1/notifications/appareils").with(moi).contentType(MediaType.APPLICATION_JSON).content(json("token", "fcm-tok-1", "plateforme", "ANDROID")))
                .andExpect(status().isNoContent()); // idempotent
        mvc.perform(post("/api/v1/notifications/appareils").with(moi).contentType(MediaType.APPLICATION_JSON).content(json("token", "x", "plateforme", "WINDOWS")))
                .andExpect(status().isBadRequest());
        assertThat(jdbc.queryForObject("SELECT count(*) FROM notif_appareil", Long.class)).isEqualTo(1);

        service.notifierLocataire(TypeMessage.QUITTANCE_EMISE, loc, UUID.randomUUID(), Map.of("numero", "QUI-2026-000009", "montant", "160 000 FCFA"));
        assertThat(registre.envois()).hasSize(1);
        assertThat(registre.envois().get(0).canal()).isEqualTo(TypeCanal.PUSH);
        assertThat(registre.envois().get(0).envoi().destinataire()).isEqualTo("fcm-tok-1");

        mvc.perform(delete("/api/v1/notifications/appareils").param("token", "fcm-tok-1").with(jwtRole(Role.LOCATAIRE))).andExpect(status().isNoContent());
        assertThat(jdbc.queryForObject("SELECT count(*) FROM notif_appareil", Long.class)).isEqualTo(1); // l'appareil d'un autre compte reste
        mvc.perform(delete("/api/v1/notifications/appareils").param("token", "fcm-tok-1").with(moi)).andExpect(status().isNoContent());
        assertThat(jdbc.queryForObject("SELECT count(*) FROM notif_appareil", Long.class)).isZero();
    }

    // ================================================================== file, réessais, dédoublonnage

    @Test
    void echecReessayeTroisFoisPuisSucces() throws Exception {
        activerCanal("SMS", Map.of("url", "https://sms.exemple.cm/send"));
        UUID loc = locataire(null, null);
        registre.echouerProchains(TypeCanal.SMS, 3);
        service.notifierLocataire(TypeMessage.RECU_ESPECES, loc, UUID.randomUUID(), Map.of("montant", "1 000 FCFA", "date", "4 novembre 2026"));

        JsonNode m = journal("").get(0);
        assertThat(m.path("statut").asText()).isEqualTo("ENVOYE");
        assertThat(m.path("tentatives").asInt()).isEqualTo(4); // 1 essai + 3 réessais
        assertThat(registre.tentatives(TypeCanal.SMS)).isEqualTo(4);
        assertThat(registre.envois()).hasSize(1);
    }

    @Test
    void apresTroisReessaisLeMessageEstEnEchecPuisRelancable() throws Exception {
        activerCanal("SMS", Map.of("url", "https://sms.exemple.cm/send"));
        UUID loc = locataire(null, null);
        registre.echouerProchains(TypeCanal.SMS, 4);
        service.notifierLocataire(TypeMessage.RECU_ESPECES, loc, UUID.randomUUID(), Map.of("montant", "1 000 FCFA", "date", "4 novembre 2026"));

        JsonNode m = journal("").get(0);
        assertThat(m.path("statut").asText()).isEqualTo("ECHEC");
        assertThat(m.path("tentatives").asInt()).isEqualTo(4);
        assertThat(m.path("derniereErreur").asText()).isEqualTo("Échec simulé");
        assertThat(registre.envois()).isEmpty();
        UUID id = UUID.fromString(m.path("id").asText());

        mvc.perform(post("/api/v1/notifications/journal/{id}/reessayer", id).with(GESTIONNAIRE)).andExpect(status().isNoContent());
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> assertThat(journal("").get(0).path("statut").asText()).isEqualTo("ENVOYE"));
        assertThat(registre.envois()).hasSize(1);
        mvc.perform(post("/api/v1/notifications/journal/{id}/reessayer", id).with(ADMIN)).andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("NOTIF_NON_RELANCABLE"));
        mvc.perform(post("/api/v1/notifications/journal/{id}/reessayer", UUID.randomUUID()).with(ADMIN)).andExpect(status().isNotFound());
    }

    @Test
    void unMessageEnAttenteSurvitAuRedemarrageEtEstRepris() throws Exception {
        activerCanal("SMS", Map.of("url", "https://sms.exemple.cm/send"));
        UUID loc = locataire(null, null);
        // message écrit mais jamais envoyé (arrêt avant l'envoi) : la tâche périodique le reprend
        jdbc.update("""
                INSERT INTO notif_message (id, canal, type_message, destinataire, sujet, corps, statut, tentatives, prochaine_tentative, locataire_id, cle_dedoublonnage, created_at, updated_at, version)
                VALUES (?, 'SMS', 'RECU_ESPECES', '+237699000000', 's', 'corps en attente', 'EN_ATTENTE', 0, now(), ?, 'cle-reprise', now(), now(), 0)
                """, UUID.randomUUID(), loc);
        assertThat(service.traiterFile()).isEqualTo(1);
        assertThat(registre.envois()).hasSize(1);
        assertThat(registre.envois().get(0).envoi().corps()).isEqualTo("corps en attente");
        assertThat(service.traiterFile()).isZero(); // rien ne part deux fois
    }

    @Test
    void redeliveranceDUnEvenementNeDoublePasLeMessage() throws Exception {
        activerCanal("SMS", Map.of("url", "https://sms.exemple.cm/send"));
        UUID loc = locataire(null, null);
        UUID ref = UUID.randomUUID();
        for (int i = 0; i < 3; i++) {
            service.notifierLocataire(TypeMessage.RECU_ESPECES, loc, ref, Map.of("montant", "1 000 FCFA", "date", "4 novembre 2026"));
        }
        assertThat(jdbc.queryForObject("SELECT count(*) FROM notif_message", Long.class)).isEqualTo(1);
        assertThat(registre.envois()).hasSize(1);
    }

    @Test
    void messagesEnAnglaisSelonLaLangue() throws Exception {
        // le modèle anglais existe pour chaque type (parité des clés avec le français)
        var fr = java.util.ResourceBundle.getBundle("notification/messages", java.util.Locale.FRENCH);
        var en = java.util.ResourceBundle.getBundle("notification/messages", java.util.Locale.ENGLISH);
        assertThat(java.util.Collections.list(en.getKeys())).containsExactlyInAnyOrderElementsOf(java.util.Collections.list(fr.getKeys()));
    }

    // ================================================================== OTP

    @Test
    void codeOtpEnvoyeParSmsDirectSansPasserParLaFileNiLeJournal() throws Exception {
        activerCanal("SMS", Map.of("url", "https://sms.exemple.cm/send"));
        otp.envoyer("+237699112233", "482913");
        assertThat(registre.envois()).hasSize(1);
        assertThat(registre.envois().get(0).envoi().corps()).contains("482913");
        assertThat(jdbc.queryForObject("SELECT count(*) FROM notif_message", Long.class)).isZero();

        registre.echouerProchains(TypeCanal.SMS, 1);
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> otp.envoyer("+237699112233", "111111"))
                .hasMessageContaining("SMS");
    }

    @Test
    void sansCanalSmsLOtpRetombeSurLImplementationDeSecurite() {
        otp.envoyer("+237699112233", "123456"); // profil dev : console, aucune exception
        assertThat(registre.envois()).isEmpty();
    }

    // ================================================================== droits

    @Test
    void droits() throws Exception {
        for (RequestPostProcessor interdit : new RequestPostProcessor[] {GESTIONNAIRE, jwtRole(Role.LOCATAIRE), jwtRole(Role.PROPRIETAIRE_TIERS), jwtRole(Role.PRESTATAIRE)}) {
            mvc.perform(get("/api/v1/notifications/canaux").with(interdit)).andExpect(status().isForbidden()).andExpect(jsonPath("$.code").value("ACCES_REFUSE"));
            mvc.perform(put("/api/v1/notifications/canaux/SMS").with(interdit).contentType(MediaType.APPLICATION_JSON).content(json("actif", false, "champs", Map.of())))
                    .andExpect(status().isForbidden());
            mvc.perform(post("/api/v1/notifications/canaux/SMS/test").with(interdit).contentType(MediaType.APPLICATION_JSON).content(json("destinataire", "x")))
                    .andExpect(status().isForbidden());
        }
        for (RequestPostProcessor interdit : new RequestPostProcessor[] {jwtRole(Role.LOCATAIRE), jwtRole(Role.PROPRIETAIRE_TIERS), jwtRole(Role.PRESTATAIRE)}) {
            mvc.perform(get("/api/v1/notifications/journal").with(interdit)).andExpect(status().isForbidden());
            mvc.perform(post("/api/v1/notifications/journal/{id}/reessayer", UUID.randomUUID()).with(interdit)).andExpect(status().isForbidden());
        }
        mvc.perform(get("/api/v1/notifications/journal").with(GESTIONNAIRE)).andExpect(status().isOk());
        mvc.perform(get("/api/v1/notifications/canaux")).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/v1/notifications/appareils").contentType(MediaType.APPLICATION_JSON).content(json("token", "x", "plateforme", "ANDROID")))
                .andExpect(status().isUnauthorized());
    }
}
