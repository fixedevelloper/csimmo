package com.immogest.app.sauvegarde;

import static com.immogest.app.securite.SecuriteTestSupport.json;
import static com.immogest.app.securite.SecuriteTestSupport.jwtRole;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.immogest.app.IntegrationTest;
import com.immogest.app.securite.Role;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

/** Écran Paramètres > Sauvegardes : lecture des manifestes, demandes déposées pour le conteneur de sauvegarde, diagnostic, droits. */
@IntegrationTest
class SauvegardeTest {

    static Path sauvegardes;
    static Path demandes;
    static final RequestPostProcessor ADMIN = jwtRole(Role.ADMIN, UUID.fromString("00000000-0000-0000-0000-0000000000a1"));

    @DynamicPropertySource
    static void proprietes(DynamicPropertyRegistry r) throws IOException {
        sauvegardes = Files.createTempDirectory("sauvegardes");
        demandes = Files.createTempDirectory("demandes");
        r.add("immogest.sauvegarde.repertoire", () -> sauvegardes.toString());
        r.add("immogest.sauvegarde.demandes", () -> demandes.toString());
        r.add("immogest.sauvegarde.version", () -> "1.2.0");
    }

    @Autowired
    MockMvc mvc;
    @Autowired
    JdbcTemplate jdbc;

    @BeforeEach
    void vider() throws IOException {
        for (Path racine : new Path[] {sauvegardes, demandes}) {
            try (Stream<Path> s = Files.walk(racine)) {
                s.sorted(java.util.Comparator.reverseOrder()).filter(p -> !p.equals(racine)).forEach(p -> p.toFile().delete());
            }
        }
    }

    private static String id(OffsetDateTime d, String etiquette) {
        return d.withOffsetSameInstant(ZoneOffset.UTC).format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + (etiquette.isEmpty() ? "" : "-" + etiquette);
    }

    private String sauvegarde(OffsetDateTime date, String etiquette, String version, String externe) throws IOException {
        String id = id(date, etiquette);
        Path d = Files.createDirectories(sauvegardes.resolve(id));
        Files.writeString(d.resolve("MANIFEST.json"), "{\"id\":\"" + id + "\",\"date\":\"" + date.withOffsetSameInstant(ZoneOffset.UTC) + "\",\"version\":\"" + version + "\",\"etiquette\":\"" + etiquette
                + "\",\"tailleOctets\":123456,\"objets\":7,\"sha256Base\":\"abc\",\"externe\":\"" + externe + "\",\"statut\":\"COMPLETE\"}");
        return id;
    }

    private void etat(String operation, String statut, OffsetDateTime debut) throws IOException {
        Files.writeString(demandes.resolve("etat.json"), "{\"operation\":\"" + operation + "\",\"statut\":\"" + statut + "\",\"id\":\"x\",\"debut\":\"" + debut + "\",\"fin\":null,\"message\":\"\"}");
    }

    // ------------------------------------------------------------------ lecture

    @Test
    void listeLesSauvegardesCompletesDeLaPlusRecenteALaPlusAncienne() throws Exception {
        OffsetDateTime maintenant = OffsetDateTime.now();
        String recente = sauvegarde(maintenant.minusHours(2), "quotidienne", "1.2.0", "OK");
        String ancienne = sauvegarde(maintenant.minusDays(3), "", "1.1.0", "NON_CONFIGURE");
        // incomplète (pas de manifeste) et dossier étranger : ignorés
        Files.createDirectories(sauvegardes.resolve(id(maintenant.minusHours(1), "manuelle")));
        Files.createDirectories(sauvegardes.resolve("cles"));
        Files.createDirectories(sauvegardes.resolve("20991231-235959.partiel"));

        mvc.perform(get("/api/v1/sauvegardes").with(ADMIN)).andExpect(status().isOk())
                .andExpect(jsonPath("$.sauvegardes.length()").value(2))
                .andExpect(jsonPath("$.sauvegardes[0].id").value(recente)).andExpect(jsonPath("$.sauvegardes[0].restaurable").value(true))
                .andExpect(jsonPath("$.sauvegardes[0].externe").value("OK")).andExpect(jsonPath("$.sauvegardes[0].objets").value(7))
                .andExpect(jsonPath("$.sauvegardes[1].id").value(ancienne)).andExpect(jsonPath("$.sauvegardes[1].restaurable").value(false)) // autre version
                .andExpect(jsonPath("$.versionInstallee").value("1.2.0")).andExpect(jsonPath("$.heureQuotidienne").value("02:30"))
                .andExpect(jsonPath("$.operation").doesNotExist());
    }

    @Test
    void aucuneSauvegardeOuDossierAbsentDonneUneListeVide() throws Exception {
        mvc.perform(get("/api/v1/sauvegardes").with(ADMIN)).andExpect(status().isOk()).andExpect(jsonPath("$.sauvegardes.length()").value(0));
    }

    @Test
    void manifesteIllisibleOuIncompletEstIgnore() throws Exception {
        Path d = Files.createDirectories(sauvegardes.resolve("20260101-010101"));
        Files.writeString(d.resolve("MANIFEST.json"), "{pas du json");
        Path e = Files.createDirectories(sauvegardes.resolve("20260102-010101"));
        Files.writeString(e.resolve("MANIFEST.json"), "{\"id\":\"x\",\"date\":\"2026-01-02T01:01:01Z\",\"statut\":\"PARTIELLE\"}");
        mvc.perform(get("/api/v1/sauvegardes").with(ADMIN)).andExpect(status().isOk()).andExpect(jsonPath("$.sauvegardes.length()").value(0));
    }

    @Test
    void lEtatDeLOperationEnCoursEstExpose() throws Exception {
        etat("restauration", "EN_COURS", OffsetDateTime.now().minusMinutes(1));
        mvc.perform(get("/api/v1/sauvegardes").with(ADMIN)).andExpect(jsonPath("$.operation.operation").value("restauration")).andExpect(jsonPath("$.operation.statut").value("EN_COURS"));
    }

    // ------------------------------------------------------------------ demandes

    @Test
    void sauvegardeImmediateDeposeUneDemandeEtEstAuditee() throws Exception {
        mvc.perform(post("/api/v1/sauvegardes").with(ADMIN)).andExpect(status().isAccepted());
        assertThat(demandes.resolve("sauvegarde.demande")).exists();
        // une seconde demande tant que la première n'est pas traitée : refusée, pas dupliquée
        mvc.perform(post("/api/v1/sauvegardes").with(ADMIN)).andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("SAUVEGARDE_DEMANDE_EN_ATTENTE"));
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() ->
                assertThat(jdbc.queryForObject("SELECT count(*) FROM audit_log WHERE action = 'SAUVEGARDE.DEMANDEE'", Long.class)).isGreaterThanOrEqualTo(1));
    }

    @Test
    void refuseUneDemandePendantUneOperationEnCours() throws Exception {
        etat("sauvegarde", "EN_COURS", OffsetDateTime.now().minusMinutes(2));
        mvc.perform(post("/api/v1/sauvegardes").with(ADMIN)).andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("SAUVEGARDE_OPERATION_EN_COURS"));
        assertThat(demandes.resolve("sauvegarde.demande")).doesNotExist();
        // une opération « en cours » vieille de plus de 3 h est considérée comme interrompue
        etat("sauvegarde", "EN_COURS", OffsetDateTime.now().minusHours(5));
        mvc.perform(post("/api/v1/sauvegardes").with(ADMIN)).andExpect(status().isAccepted());
    }

    @Test
    void restaurationExigeLaConfirmationUneSauvegardeExistanteEtLaMemeVersion() throws Exception {
        String bonne = sauvegarde(OffsetDateTime.now().minusHours(1), "quotidienne", "1.2.0", "OK");
        String autreVersion = sauvegarde(OffsetDateTime.now().minusDays(1), "", "1.1.0", "OK");
        String url = "/api/v1/sauvegardes/{id}/restauration";

        mvc.perform(post(url, bonne).with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("confirmation", "oui"))).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("SAUVEGARDE_CONFIRMATION_INVALIDE"));
        mvc.perform(post(url, bonne).with(ADMIN).contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isBadRequest());
        mvc.perform(post(url, "20200101-000000").with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("confirmation", "RESTAURER"))).andExpect(status().isNotFound());
        mvc.perform(post(url, "n-importe-quoi").with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("confirmation", "RESTAURER"))).andExpect(status().isNotFound());
        mvc.perform(post(url, autreVersion).with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("confirmation", "RESTAURER"))).andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("SAUVEGARDE_VERSION_DIFFERENTE"));
        try (Stream<Path> s = Files.list(demandes)) {
            assertThat(s).isEmpty(); // aucune demande déposée pour tous ces refus
        }

        mvc.perform(post(url, bonne).with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("confirmation", "RESTAURER"))).andExpect(status().isAccepted());
        assertThat(demandes.resolve("restauration-" + bonne + ".demande")).exists();
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() ->
                assertThat(jdbc.queryForObject("SELECT count(*) FROM audit_log WHERE action = 'SAUVEGARDE.RESTAURATION_DEMANDEE'", Long.class)).isGreaterThanOrEqualTo(1));
    }

    @Test
    void unIdentifiantMalveillantNeSortJamaisDuDossier() throws Exception {
        // un dossier voisin portant un manifeste valide ne doit pas être atteignable par « .. »
        for (String id : new String[] {"..", "%2e%2e", "..%2fdemandes", "20260101-010101/../../x", "20260101-010101;rm"}) {
            mvc.perform(post("/api/v1/sauvegardes/" + id + "/restauration").with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("confirmation", "RESTAURER")))
                    .andExpect(result -> assertThat(result.getResponse().getStatus()).isIn(400, 404, 405));
        }
        try (Stream<Path> s = Files.list(demandes)) {
            assertThat(s).isEmpty();
        }
    }

    // ------------------------------------------------------------------ diagnostic

    @Test
    void diagnosticSansSauvegardeAlerte() throws Exception {
        mvc.perform(get("/api/v1/sauvegardes/diagnostic").with(ADMIN)).andExpect(status().isOk())
                .andExpect(jsonPath("$.versionApplication").value("1.2.0")).andExpect(jsonPath("$.versionBase").isNotEmpty())
                .andExpect(jsonPath("$.tailleBaseOctets").isNumber()).andExpect(jsonPath("$.disqueLibreOctets").isNumber())
                .andExpect(jsonPath("$.alertes[?(@ == 'SAUVEGARDE_ABSENTE')]").exists()).andExpect(jsonPath("$.derniereSauvegarde").doesNotExist());
    }

    @Test
    void diagnosticSauvegardeRecenteAvecCopieExterneEnPlace() throws Exception {
        sauvegarde(OffsetDateTime.now().minusHours(3), "quotidienne", "1.2.0", "OK");
        String corps = mvc.perform(get("/api/v1/sauvegardes/diagnostic").with(ADMIN)).andExpect(status().isOk()).andExpect(jsonPath("$.copieExterne").value("OK"))
                .andExpect(jsonPath("$.ageDerniereSauvegardeHeures").value(3)).andReturn().getResponse().getContentAsString();
        assertThat(corps).doesNotContain("SAUVEGARDE_ABSENTE").doesNotContain("SAUVEGARDE_ANCIENNE").doesNotContain("COPIE_EXTERNE");
    }

    @Test
    void diagnosticSignaleSauvegardeAncienneEtCopieExterneAbsenteOuEnEchec() throws Exception {
        sauvegarde(OffsetDateTime.now().minusDays(4), "quotidienne", "1.2.0", "NON_CONFIGURE");
        mvc.perform(get("/api/v1/sauvegardes/diagnostic").with(ADMIN)).andExpect(jsonPath("$.alertes[?(@ == 'SAUVEGARDE_ANCIENNE')]").exists())
                .andExpect(jsonPath("$.alertes[?(@ == 'COPIE_EXTERNE_ABSENTE')]").exists());
        sauvegarde(OffsetDateTime.now().minusHours(1), "manuelle", "1.2.0", "ECHEC");
        etat("sauvegarde", "ECHEC", OffsetDateTime.now().minusHours(1));
        mvc.perform(get("/api/v1/sauvegardes/diagnostic").with(ADMIN)).andExpect(jsonPath("$.alertes[?(@ == 'COPIE_EXTERNE_ECHEC')]").exists())
                .andExpect(jsonPath("$.alertes[?(@ == 'DERNIERE_OPERATION_ECHEC')]").exists()).andExpect(jsonPath("$.alertes[?(@ == 'SAUVEGARDE_ANCIENNE')]").doesNotExist());
    }

    @Test
    void diagnosticNeContientAucunSecret() throws Exception {
        String corps = mvc.perform(get("/api/v1/sauvegardes/diagnostic").with(ADMIN)).andReturn().getResponse().getContentAsString().toLowerCase();
        assertThat(corps).doesNotContain("password").doesNotContain("secret").doesNotContain("cle").doesNotContain("jdbc:");
    }

    // ------------------------------------------------------------------ droits

    @Test
    void reserveALAdministrateur() throws Exception {
        String bonne = sauvegarde(OffsetDateTime.now().minusHours(1), "quotidienne", "1.2.0", "OK");
        for (RequestPostProcessor interdit : new RequestPostProcessor[] {jwtRole(Role.GESTIONNAIRE), jwtRole(Role.LOCATAIRE), jwtRole(Role.PROPRIETAIRE_TIERS), jwtRole(Role.PRESTATAIRE)}) {
            mvc.perform(get("/api/v1/sauvegardes").with(interdit)).andExpect(status().isForbidden());
            mvc.perform(get("/api/v1/sauvegardes/diagnostic").with(interdit)).andExpect(status().isForbidden());
            mvc.perform(post("/api/v1/sauvegardes").with(interdit)).andExpect(status().isForbidden());
            mvc.perform(post("/api/v1/sauvegardes/{id}/restauration", bonne).with(interdit).contentType(MediaType.APPLICATION_JSON).content(json("confirmation", "RESTAURER")))
                    .andExpect(status().isForbidden());
        }
        mvc.perform(get("/api/v1/sauvegardes")).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/v1/sauvegardes")).andExpect(status().isUnauthorized());
        try (Stream<Path> s = Files.list(demandes)) {
            assertThat(s).isEmpty();
        }
    }
}
