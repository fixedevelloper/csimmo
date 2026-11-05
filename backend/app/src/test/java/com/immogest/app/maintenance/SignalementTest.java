package com.immogest.app.maintenance;

import static com.immogest.app.securite.SecuriteTestSupport.json;
import static com.immogest.app.securite.SecuriteTestSupport.jwtRole;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.immogest.app.IntegrationTest;
import com.immogest.app.notification.infra.CanauxSimules.Registre;
import com.immogest.app.securite.Role;
import com.immogest.app.securite.UtilisateurService;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

/** Signalements de pannes (locataire → gestionnaire), mon-compte et configuration Firebase mobile. */
@IntegrationTest
class SignalementTest {

    private static final RequestPostProcessor ADMIN = jwtRole(Role.ADMIN, UUID.fromString("00000000-0000-0000-0000-0000000000a1"));
    private static final RequestPostProcessor GESTIONNAIRE = jwtRole(Role.GESTIONNAIRE);
    private static final AtomicLong TEL = new AtomicLong(610_000_000L);
    private static final byte[] PNG = Base64.getDecoder().decode(
            "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR4nGNgYGD4DwABBAEAwS2OUAAAAABJRU5ErkJggg==");

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    JdbcTemplate jdbc;
    @Autowired
    UtilisateurService utilisateurs;
    @Autowired
    Registre registre;

    LocalDate aujourdhui;
    UUID immeuble;
    UUID compte;
    UUID locataire;
    UUID bail;
    RequestPostProcessor moi;

    @BeforeEach
    void preparer() throws Exception {
        aujourdhui = LocalDate.now(ZoneId.of("Africa/Douala"));
        try {
            await().atMost(Duration.ofSeconds(20)).until(() -> jdbc.queryForObject("SELECT count(*) FROM event_publication WHERE completion_date IS NULL AND publication_date > now() - interval '30 seconds'", Long.class) == 0);
        } catch (org.awaitility.core.ConditionTimeoutException ignore) {
            // sans effet ici
        }
        jdbc.execute("TRUNCATE mnt_signalement, notif_appareil, notif_canal, rel_relance, rel_mise_en_demeure, notif_message, fin_document, fin_compteur_numero, fin_imputation, fin_paiement, fin_echeance, bail_document, bail_garant, bail, patrimoine_photo, compteur, lot_equipement, lot, immeuble, garant, tiers_piece, locataire CASCADE");
        immeuble = id(mvc.perform(post("/api/v1/immeubles").with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("nom", "Résidence Pannes", "adresse", "1 rue", "ville", "Douala"))));
        compte = utilisateurs.creer(new UtilisateurService.Saisie("Compte", "+237" + TEL.incrementAndGet(), null, Role.LOCATAIRE, true, false), "Motdepasse123").id();
        locataire = locataire(compte);
        bail = bail(locataire, "P1");
        moi = jwtRole(Role.LOCATAIRE, compte);
    }

    private UUID id(ResultActions r) throws Exception {
        return UUID.fromString(mapper.readTree(r.andReturn().getResponse().getContentAsString()).path("id").asText());
    }

    private UUID locataire(UUID cpt) throws Exception {
        return id(mvc.perform(post("/api/v1/locataires").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("nom", "Paul Panne", "telephone", "+237" + TEL.incrementAndGet(), "utilisateurId", cpt == null ? null : cpt.toString()))).andExpect(status().isCreated()));
    }

    private UUID bail(UUID loc, String ref) throws Exception {
        UUID lot = id(mvc.perform(post("/api/v1/lots").with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("immeubleId", immeuble.toString(), "reference", ref, "type", "STUDIO"))));
        UUID b = id(mvc.perform(post("/api/v1/baux").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("lotId", lot.toString(), "locataireId", loc.toString(), "dateDebut", aujourdhui.minusDays(30).toString(), "loyer", 100_000, "charges", 0,
                        "periodicite", "MENSUELLE", "jourEcheance", 1, "moisAvance", 0))).andExpect(status().isCreated()));
        mvc.perform(post("/api/v1/baux/{id}/activer", b).with(ADMIN)).andExpect(status().isOk());
        return b;
    }

    private ResultActions signaler(RequestPostProcessor qui, String description, UUID bailId, boolean photo) throws Exception {
        var requete = multipart("/api/v1/signalements");
        if (photo) {
            requete.file(new MockMultipartFile("photo", "fuite.png", "image/png", PNG));
        }
        requete.file(new MockMultipartFile("description", "", "text/plain", description.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
        if (bailId != null) {
            requete.file(new MockMultipartFile("bailId", "", "text/plain", bailId.toString().getBytes()));
        }
        return mvc.perform(requete.with(qui));

    }

    private JsonNode lire(String chemin, RequestPostProcessor qui) throws Exception {
        return mapper.readTree(mvc.perform(get(chemin).with(qui)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
    }

    // ================================================================== signalements

    @Test
    void signalerUnePanneAvecPhotoEtLaSuivre() throws Exception {
        JsonNode s = mapper.readTree(signaler(moi, "  Fuite sous l'évier de la cuisine  ", null, true).andExpect(status().isCreated())
                .andExpect(jsonPath("$.statut").value("NOUVEAU")).andExpect(jsonPath("$.description").value("Fuite sous l'évier de la cuisine"))
                .andExpect(jsonPath("$.logement").value("Résidence Pannes — P1")).andExpect(jsonPath("$.locataireNom").value("Paul Panne"))
                .andReturn().getResponse().getContentAsString());
        assertThat(s.path("bailId").asText()).isEqualTo(bail.toString()); // un seul bail en cours : choisi d'office
        String photo = s.path("photoFichierId").asText();
        assertThat(photo).isNotBlank();
        mvc.perform(get("/api/v1/fichiers/{id}", photo).with(moi)).andExpect(status().isOk());
        mvc.perform(get("/api/v1/fichiers/{id}", photo).with(GESTIONNAIRE)).andExpect(status().isOk());
        mvc.perform(get("/api/v1/fichiers/{id}", photo).with(jwtRole(Role.LOCATAIRE))).andExpect(status().is4xxClientError());
        mvc.perform(get("/api/v1/fichiers/{id}", photo).with(jwtRole(Role.PROPRIETAIRE_TIERS))).andExpect(status().is4xxClientError());

        // sans photo aussi
        UUID second = UUID.fromString(mapper.readTree(signaler(moi, "Porte d'entrée qui ne ferme plus", null, false).andExpect(status().isCreated())
                .andExpect(jsonPath("$.photoFichierId").doesNotExist()).andReturn().getResponse().getContentAsString()).path("id").asText());
        UUID premier = UUID.fromString(s.path("id").asText());
        assertThat(lire("/api/v1/signalements", moi).path("totalElements").asInt()).isEqualTo(2);
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() ->
                assertThat(jdbc.queryForObject("SELECT count(*) FROM audit_log WHERE action = 'MAINTENANCE.SIGNALEMENT_CREE' AND entite_id IN (?, ?)", Long.class, premier, second)).isEqualTo(2));
    }

    @Test
    void validationsEtLimites() throws Exception {
        signaler(moi, "abc", null, false).andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("SIGNALEMENT_DESCRIPTION_INVALIDE"));
        signaler(moi, "x".repeat(2001), null, false).andExpect(status().isBadRequest());
        signaler(moi, "Une vraie panne", UUID.randomUUID(), false).andExpect(status().isNotFound());
        // un fichier qui n'est pas une image est refusé par le module stockage (contenu vérifié, pas l'extension)
        mvc.perform(multipart("/api/v1/signalements").file(new MockMultipartFile("photo", "faux.png", "image/png", "pas une image".getBytes()))
                .file(new MockMultipartFile("description", "", "text/plain", "Panne avec faux fichier".getBytes())).with(moi))
                .andExpect(status().is4xxClientError());
        assertThat(jdbc.queryForObject("SELECT count(*) FROM mnt_signalement", Long.class)).isZero();
        for (int i = 0; i < SignalementService.MAX_OUVERTS; i++) {
            signaler(moi, "Panne numéro " + i, null, false).andExpect(status().isCreated());
        }
        signaler(moi, "Une de trop", null, false).andExpect(status().isTooManyRequests()).andExpect(jsonPath("$.code").value("SIGNALEMENT_LIMITE"));
    }

    @Test
    void bailsMultiplesEtAucunBail() throws Exception {
        UUID autreBail = bail(locataire, "P2");
        signaler(moi, "Ampoule grillée dans le couloir", null, false).andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("SIGNALEMENT_BAIL_REQUIS"));
        signaler(moi, "Ampoule grillée dans le couloir", autreBail, false).andExpect(status().isCreated()).andExpect(jsonPath("$.logement").value("Résidence Pannes — P2"));

        UUID compte2 = utilisateurs.creer(new UtilisateurService.Saisie("Sans bail", "+237" + TEL.incrementAndGet(), null, Role.LOCATAIRE, true, false), "Motdepasse123").id();
        locataire(compte2);
        signaler(jwtRole(Role.LOCATAIRE, compte2), "Je n'ai pas de bail", null, false).andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("SIGNALEMENT_AUCUN_BAIL"));
        // le bail d'un autre locataire n'est pas utilisable
        signaler(jwtRole(Role.LOCATAIRE, compte2), "Bail d'un autre", bail, false).andExpect(status().isNotFound());
    }

    @Test
    void lesGestionnairesTraitentLesSignalements() throws Exception {
        UUID id = UUID.fromString(mapper.readTree(signaler(moi, "Robinet qui goutte", null, false).andReturn().getResponse().getContentAsString()).path("id").asText());
        assertThat(lire("/api/v1/signalements?statut=NOUVEAU", GESTIONNAIRE).path("totalElements").asInt()).isEqualTo(1);

        mvc.perform(put("/api/v1/signalements/{id}/statut", id).with(GESTIONNAIRE).contentType(MediaType.APPLICATION_JSON).content(json("statut", "NOUVEAU")))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("SIGNALEMENT_TRANSITION_INVALIDE"));
        mvc.perform(put("/api/v1/signalements/{id}/statut", id).with(GESTIONNAIRE).contentType(MediaType.APPLICATION_JSON).content(json("statut", "EN_COURS", "note", "Plombier prévenu")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.statut").value("EN_COURS")).andExpect(jsonPath("$.noteGestion").value("Plombier prévenu"));
        mvc.perform(put("/api/v1/signalements/{id}/statut", id).with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("statut", "RESOLU"))).andExpect(status().isOk());
        mvc.perform(put("/api/v1/signalements/{id}/statut", id).with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("statut", "EN_COURS")))
                .andExpect(status().isConflict()); // état terminal définitif
        mvc.perform(get("/api/v1/signalements/{id}", id).with(moi)).andExpect(jsonPath("$.statut").value("RESOLU")).andExpect(jsonPath("$.noteGestion").doesNotExist());
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() ->
                assertThat(jdbc.queryForObject("SELECT count(*) FROM audit_log WHERE action IN ('MAINTENANCE.SIGNALEMENT_EN_COURS', 'MAINTENANCE.SIGNALEMENT_RESOLU') AND entite_id = ?", Long.class, id)).isEqualTo(2));
    }

    @Test
    void droitsEtCloisonnement() throws Exception {
        UUID id = UUID.fromString(mapper.readTree(signaler(moi, "Volet roulant bloqué", null, false).andReturn().getResponse().getContentAsString()).path("id").asText());
        UUID compte2 = utilisateurs.creer(new UtilisateurService.Saisie("Voisin", "+237" + TEL.incrementAndGet(), null, Role.LOCATAIRE, true, false), "Motdepasse123").id();
        locataire(compte2);
        RequestPostProcessor voisin = jwtRole(Role.LOCATAIRE, compte2);

        assertThat(lire("/api/v1/signalements", voisin).path("totalElements").asInt()).isZero();
        mvc.perform(get("/api/v1/signalements/{id}", id).with(voisin)).andExpect(status().isNotFound());
        mvc.perform(put("/api/v1/signalements/{id}/statut", id).with(moi).contentType(MediaType.APPLICATION_JSON).content(json("statut", "RESOLU"))).andExpect(status().isForbidden());
        for (Role r : new Role[] {Role.PROPRIETAIRE_TIERS, Role.PRESTATAIRE}) {
            mvc.perform(get("/api/v1/signalements").with(jwtRole(r))).andExpect(status().isForbidden()).andExpect(jsonPath("$.code").value("ACCES_REFUSE"));
        }
        signaler(GESTIONNAIRE, "Un gestionnaire ne signale pas", null, false).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/signalements")).andExpect(status().isUnauthorized());
        assertThat(lire("/api/v1/signalements", GESTIONNAIRE).path("totalElements").asInt()).isEqualTo(1);
    }

    // ================================================================== mon compte, config mobile

    @Test
    void monCompteDuLocataire() throws Exception {
        mvc.perform(get("/api/v1/mon-compte").with(moi)).andExpect(status().isOk()).andExpect(jsonPath("$.locataireNom").value("Paul Panne"))
                .andExpect(jsonPath("$.baux[0].bailId").value(bail.toString()));
        mvc.perform(get("/api/v1/mon-compte").with(jwtRole(Role.LOCATAIRE))).andExpect(status().isNotFound()); // compte sans fiche
        mvc.perform(get("/api/v1/mon-compte").with(GESTIONNAIRE)).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/mon-compte")).andExpect(status().isUnauthorized());
    }

    @Test
    void configMobilePushSeulementSiLeCanalEstPret() throws Exception {
        mvc.perform(get("/api/v1/notifications/mobile-config").with(moi)).andExpect(status().isOk()).andExpect(jsonPath("$.push").doesNotExist());
        mvc.perform(put("/api/v1/notifications/canaux/PUSH").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("actif", true, "champs", Map.of("projectId", "mon-projet", "compteService", "{\"secret\":\"X\"}")))).andExpect(status().isOk());
        // Firebase publics manquants : l'application ne doit pas s'initialiser
        mvc.perform(get("/api/v1/notifications/mobile-config").with(moi)).andExpect(jsonPath("$.push").doesNotExist());
        mvc.perform(put("/api/v1/notifications/canaux/PUSH").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("actif", true, "champs", Map.of("apiKey", "AIza-public", "appId", "1:123:android:abc", "messagingSenderId", "123")))).andExpect(status().isOk());
        String corps = mvc.perform(get("/api/v1/notifications/mobile-config").with(moi)).andExpect(status().isOk()).andExpect(jsonPath("$.push.projectId").value("mon-projet"))
                .andExpect(jsonPath("$.push.apiKey").value("AIza-public")).andExpect(jsonPath("$.push.messagingSenderId").value("123")).andReturn().getResponse().getContentAsString();
        assertThat(corps).doesNotContain("secret"); // le compte de service ne sort jamais
        mvc.perform(get("/api/v1/notifications/mobile-config")).andExpect(status().isUnauthorized());
        mvc.perform(put("/api/v1/notifications/canaux/PUSH").with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("actif", false, "champs", Map.of()))).andExpect(status().isOk());
        mvc.perform(get("/api/v1/notifications/mobile-config").with(moi)).andExpect(jsonPath("$.push").doesNotExist());
    }
}
