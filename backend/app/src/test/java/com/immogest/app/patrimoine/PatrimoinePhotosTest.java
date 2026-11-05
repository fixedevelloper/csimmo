package com.immogest.app.patrimoine;

import static com.immogest.app.securite.SecuriteTestSupport.jwtRole;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.immogest.app.IntegrationTest;
import com.immogest.app.securite.Role;
import com.immogest.app.stockage.StockageTestSupport;
import java.io.ByteArrayInputStream;
import java.time.Duration;
import java.util.UUID;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;

/** Photos d'immeubles et de lots : stockage S3, contrôle d'accès au téléchargement, limites. */
@IntegrationTest
class PatrimoinePhotosTest extends PatrimoineTestBase {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    JdbcTemplate jdbc;

    @Override
    protected MockMvc mvc() {
        return mvc;
    }

    @Override
    protected ObjectMapper mapper() {
        return mapper;
    }

    @BeforeEach
    void vider() {
        viderPatrimoine(jdbc);
    }

    private ResultActions envoyer(String cible, UUID id, byte[] contenu, String nom, String type, String legende) throws Exception {
        MockMultipartHttpServletRequestBuilder requete = multipart("/api/v1/{cible}/{id}/photos", cible, id)
                .file(new MockMultipartFile("fichier", nom, type, contenu));
        if (legende != null) {
            requete.param("legende", legende);
        }
        return mvc.perform(requete.with(ADMIN));
    }

    private UUID photoImmeuble(UUID immeuble) throws Exception {
        return UUID.fromString(corps(envoyer("immeubles", immeuble, StockageTestSupport.png(40, 30), "p.png", "image/png", null))
                .path("fichierId").asText());
    }

    @Test
    void ajouterUnePhotoLaListerEtLaTelechargerAvecControleDAcces() throws Exception {
        UUID immeuble = creerImmeuble("Photos", "Douala", "Akwa");
        byte[] png = StockageTestSupport.png(400, 300);

        JsonNode photo = corps(envoyer("immeubles", immeuble, png, "../facade.png", "image/png", "  Façade principale ")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cibleType").value("IMMEUBLE"))
                .andExpect(jsonPath("$.cibleId").value(immeuble.toString()))
                .andExpect(jsonPath("$.legende").value("Façade principale"))
                .andExpect(jsonPath("$.ordre").value(0)));
        String fichierId = photo.path("fichierId").asText();

        mvc.perform(get("/api/v1/immeubles/{id}/photos", immeuble).with(GESTIONNAIRE))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].fichierId").value(fichierId));

        // Téléchargement : un gestionnaire et un administrateur, via l'API (jamais d'identifiants S3)
        for (var auth : new Object[] {GESTIONNAIRE, ADMIN}) {
            byte[] recu = mvc.perform(get("/api/v1/fichiers/{id}", fichierId).with((org.springframework.test.web.servlet.request.RequestPostProcessor) auth))
                    .andExpect(status().isOk())
                    .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header().string("Content-Type", "image/png"))
                    .andReturn().getResponse().getContentAsByteArray();
            assertThat(recu).isEqualTo(png);
        }
    }

    @Test
    void lesAutresRolesEtLesAnonymesNePeuventPasTelechargerLaPhoto() throws Exception {
        UUID fichier = photoImmeuble(creerImmeuble("Photos", "Douala", "Akwa"));

        // Un propriétaire sans fiche rattachée (ou dont ce n'est pas le bien) n'a pas accès non plus
        for (Role role : new Role[] {Role.LOCATAIRE, Role.PRESTATAIRE, Role.PROPRIETAIRE_TIERS}) {
            mvc.perform(get("/api/v1/fichiers/{id}", fichier).with(jwtRole(role)))
                    .andExpect(status().isForbidden()).andExpect(jsonPath("$.code").value("FICHIER_ACCES_REFUSE"));
        }
        mvc.perform(get("/api/v1/fichiers/{id}", fichier)).andExpect(status().isUnauthorized());
    }

    @Test
    void lesAutresRolesNePeuventPasEnvoyerDePhoto() throws Exception {
        UUID immeuble = creerImmeuble("Photos", "Douala", "Akwa");

        for (Role role : new Role[] {Role.LOCATAIRE, Role.PRESTATAIRE, Role.PROPRIETAIRE_TIERS}) {
            mvc.perform(multipart("/api/v1/immeubles/{id}/photos", immeuble)
                            .file(new MockMultipartFile("fichier", "p.png", "image/png", StockageTestSupport.png(10, 10)))
                            .with(jwtRole(role)))
                    .andExpect(status().isForbidden());
        }
        assertThat(jdbc.queryForObject("SELECT count(*) FROM patrimoine_photo", Integer.class)).isZero();
    }

    @Test
    void uneGrandeImageEstRedimensionneeA1600Pixels() throws Exception {
        UUID immeuble = creerImmeuble("Photos", "Douala", "Akwa");
        String fichier = corps(envoyer("immeubles", immeuble, StockageTestSupport.png(3200, 1200), "grande.png", "image/png", null))
                .path("fichierId").asText();

        byte[] recu = mvc.perform(get("/api/v1/fichiers/{id}", fichier).with(ADMIN)).andReturn().getResponse().getContentAsByteArray();

        var image = ImageIO.read(new ByteArrayInputStream(recu));
        assertThat(image.getWidth()).isEqualTo(1600);
        assertThat(image.getHeight()).isEqualTo(600);
    }

    @Test
    void lesFichiersQuiNeSontPasDesImagesSontRefusesEtNeLaissentRien() throws Exception {
        UUID immeuble = creerImmeuble("Photos", "Douala", "Akwa");
        int fichiersAvant = jdbc.queryForObject("SELECT count(*) FROM fichier", Integer.class);

        envoyer("immeubles", immeuble, "ceci n'est pas une image".getBytes(), "faux.png", "image/png", null)
                .andExpect(status().isUnsupportedMediaType()).andExpect(jsonPath("$.code").value("FICHIER_TYPE_INTERDIT"));
        envoyer("immeubles", immeuble, StockageTestSupport.pdf(), "plan.pdf", "application/pdf", null)
                .andExpect(status().isUnsupportedMediaType()).andExpect(jsonPath("$.code").value("PHOTO_TYPE_INTERDIT"));
        envoyer("immeubles", immeuble, new byte[0], "vide.png", "image/png", null)
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("FICHIER_VIDE"));

        assertThat(jdbc.queryForObject("SELECT count(*) FROM patrimoine_photo", Integer.class)).isZero();
        assertThat(jdbc.queryForObject("SELECT count(*) FROM fichier", Integer.class))
                .as("le PDF stocké puis refusé est supprimé : aucun fichier orphelin").isEqualTo(fichiersAvant);
    }

    @Test
    void unFichierDePlusDeDixMoEstRefuse() throws Exception {
        UUID immeuble = creerImmeuble("Photos", "Douala", "Akwa");

        envoyer("immeubles", immeuble, new byte[10 * 1024 * 1024 + 1], "enorme.png", "image/png", null)
                .andExpect(status().isPayloadTooLarge());
        assertThat(jdbc.queryForObject("SELECT count(*) FROM patrimoine_photo", Integer.class)).isZero();
    }

    @Test
    void laRequeteSansFichierEstRefusee() throws Exception {
        UUID immeuble = creerImmeuble("Photos", "Douala", "Akwa");

        mvc.perform(multipart("/api/v1/immeubles/{id}/photos", immeuble).with(ADMIN)).andExpect(status().isBadRequest());
    }

    @Test
    void auPlusVingtPhotosParFiche() throws Exception {
        UUID immeuble = creerImmeuble("Photos", "Douala", "Akwa");
        byte[] png = StockageTestSupport.png(8, 8);
        for (int i = 0; i < 20; i++) {
            envoyer("immeubles", immeuble, png, "p" + i + ".png", "image/png", null).andExpect(status().isCreated());
        }

        envoyer("immeubles", immeuble, png, "de-trop.png", "image/png", null)
                .andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("PHOTOS_LIMITE_ATTEINTE"));

        JsonNode liste = corps(mvc.perform(get("/api/v1/immeubles/{id}/photos", immeuble).with(ADMIN)));
        assertThat(liste).hasSize(20);
        assertThat(liste.findValues("ordre")).extracting(JsonNode::asInt).containsExactlyElementsOf(java.util.stream.IntStream.range(0, 20).boxed().toList());
    }

    @Test
    void supprimerUnePhotoRetireLeFichierDuStockage() throws Exception {
        UUID immeuble = creerImmeuble("Photos", "Douala", "Akwa");
        JsonNode photo = corps(envoyer("immeubles", immeuble, StockageTestSupport.png(40, 30), "p.png", "image/png", null));
        String photoId = photo.path("id").asText();
        String fichierId = photo.path("fichierId").asText();

        mvc.perform(delete("/api/v1/photos/{id}", photoId).with(GESTIONNAIRE)).andExpect(status().isNoContent());

        mvc.perform(get("/api/v1/immeubles/{id}/photos", immeuble).with(ADMIN)).andExpect(jsonPath("$.length()").value(0));
        mvc.perform(get("/api/v1/fichiers/{id}", fichierId).with(ADMIN))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value("FICHIER_INTROUVABLE"));
        assertThat(jdbc.queryForObject("SELECT count(*) FROM fichier WHERE id = ?::uuid", Integer.class, fichierId)).isZero();
        mvc.perform(delete("/api/v1/photos/{id}", photoId).with(ADMIN))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value("PHOTO_INTROUVABLE"));
    }

    @Test
    void lesPhotosDUnLotSontIndependantesDeCellesDeLImmeuble() throws Exception {
        UUID immeuble = creerImmeuble("Photos", "Douala", "Akwa");
        UUID lot = creerLot(immeuble, "P1", "STUDIO");
        photoImmeuble(immeuble);

        envoyer("lots", lot, StockageTestSupport.png(20, 20), "salon.png", "image/png", "Salon").andExpect(status().isCreated())
                .andExpect(jsonPath("$.cibleType").value("LOT"));

        mvc.perform(get("/api/v1/lots/{id}/photos", lot).with(ADMIN))
                .andExpect(jsonPath("$.length()").value(1)).andExpect(jsonPath("$[0].legende").value("Salon"));
        mvc.perform(get("/api/v1/immeubles/{id}/photos", immeuble).with(ADMIN)).andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void pasDePhotoSurUneFicheInconnueOuArchivee() throws Exception {
        UUID immeuble = creerImmeuble("Photos", "Douala", "Akwa");
        UUID lot = creerLot(immeuble, "P1", "STUDIO");
        mvc.perform(delete("/api/v1/lots/{id}", lot).with(ADMIN)).andExpect(status().isNoContent());

        envoyer("lots", lot, StockageTestSupport.png(8, 8), "p.png", "image/png", null)
                .andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("LOT_ARCHIVE"));
        envoyer("lots", UUID.randomUUID(), StockageTestSupport.png(8, 8), "p.png", "image/png", null)
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value("LOT_INTROUVABLE"));
        envoyer("immeubles", UUID.randomUUID(), StockageTestSupport.png(8, 8), "p.png", "image/png", null)
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value("IMMEUBLE_INTROUVABLE"));
        mvc.perform(get("/api/v1/lots/{id}/photos", UUID.randomUUID()).with(ADMIN)).andExpect(status().isNotFound());
    }

    @Test
    void lAjoutEtLaSuppressionDUnePhotoSontJournalises() throws Exception {
        UUID immeuble = creerImmeuble("Photos", "Douala", "Akwa");
        String photoId = corps(envoyer("immeubles", immeuble, StockageTestSupport.png(8, 8), "p.png", "image/png", null)).path("id").asText();
        mvc.perform(delete("/api/v1/photos/{id}", photoId).with(ADMIN)).andExpect(status().isNoContent());

        await().atMost(Duration.ofSeconds(15)).untilAsserted(() -> assertThat(
                jdbc.queryForList("SELECT action FROM audit_log WHERE entite_id = ? ORDER BY date", immeuble))
                .extracting(l -> l.get("action")).contains("PATRIMOINE.PHOTO_AJOUTEE", "PATRIMOINE.PHOTO_SUPPRIMEE"));
    }
}
