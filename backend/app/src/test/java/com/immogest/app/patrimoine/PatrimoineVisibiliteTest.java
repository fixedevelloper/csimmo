package com.immogest.app.patrimoine;

import static com.immogest.app.securite.SecuriteTestSupport.json;
import static com.immogest.app.securite.SecuriteTestSupport.jwtRole;
import static com.immogest.app.securite.SecuriteTestSupport.telephoneUnique;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.immogest.app.IntegrationTest;
import com.immogest.app.securite.Role;
import com.immogest.app.securite.UtilisateurService;
import com.immogest.app.stockage.StockageTestSupport;
import com.immogest.app.tiers.ProprietaireSaisie;
import com.immogest.app.tiers.ProprietaireService;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

/** Un propriétaire tiers consulte SES immeubles (et leurs lots et photos) en lecture seule, et rien d'autre. */
@IntegrationTest
class PatrimoineVisibiliteTest extends PatrimoineTestBase {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    JdbcTemplate jdbc;
    @Autowired
    UtilisateurService utilisateurs;
    @Autowired
    ProprietaireService proprietaires;

    @Override
    protected MockMvc mvc() {
        return mvc;
    }

    @Override
    protected ObjectMapper mapper() {
        return mapper;
    }

    /** Un propriétaire tiers avec son compte, son immeuble et un lot. */
    private record Proprio(UUID compte, UUID fiche, UUID immeuble, UUID lot, RequestPostProcessor auth) {
    }

    @BeforeEach
    void vider() {
        viderPatrimoine(jdbc);
        jdbc.execute("TRUNCATE tiers_piece, garant, locataire, mandat, proprietaire_tiers, prestataire CASCADE");
    }

    private UUID compte() {
        return utilisateurs.creer(new UtilisateurService.Saisie("Compte proprio", telephoneUnique(), null, Role.PROPRIETAIRE_TIERS, true, false),
                "Motdepasse123").id();
    }

    private Proprio proprio(String nom) throws Exception {
        UUID compte = compte();
        UUID fiche = proprietaires.creer(new ProprietaireSaisie(nom, telephoneUnique(), null, null, null, null, compte, null)).id();
        UUID immeuble = UUID.fromString(corps(mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/v1/immeubles")
                .with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("nom", "Immeuble de " + nom, "adresse", "1 rue X", "ville", "Douala", "proprietaireTiersId", fiche.toString())))).path("id").asText());
        UUID lot = creerLot(immeuble, "L-" + nom, "STUDIO");
        return new Proprio(compte, fiche, immeuble, lot, jwtRole(Role.PROPRIETAIRE_TIERS, compte));
    }

    private ResultActions envoyerPhoto(String cible, UUID id) throws Exception {
        return mvc.perform(multipart("/api/v1/{cible}/{id}/photos", cible, id)
                .file(new MockMultipartFile("fichier", "p.png", "image/png", StockageTestSupport.png(20, 20))).with(ADMIN));
    }

    @Test
    void unProprietaireNeVoitQueSesImmeubles() throws Exception {
        Proprio a = proprio("Alpha");
        Proprio b = proprio("Bravo");
        UUID sansProprietaire = creerImmeuble("Sans propriétaire", "Douala", "Akwa");

        JsonNode liste = corps(mvc.perform(get("/api/v1/immeubles").with(a.auth())));
        assertThat(liste.path("totalElements").asInt()).isEqualTo(1);
        assertThat(textes(liste.path("content"), "id")).containsExactly(a.immeuble().toString());
        assertThat(liste.path("content").get(0).path("proprietaireTiersId").asText()).isEqualTo(a.fiche().toString());

        mvc.perform(get("/api/v1/immeubles/{id}", a.immeuble()).with(a.auth())).andExpect(status().isOk());
        for (UUID autre : new UUID[] {b.immeuble(), sansProprietaire}) {
            mvc.perform(get("/api/v1/immeubles/{id}", autre).with(a.auth()))
                    .andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value("IMMEUBLE_INTROUVABLE"));
            mvc.perform(get("/api/v1/immeubles/{id}/photos", autre).with(a.auth())).andExpect(status().isNotFound());
        }
    }

    @Test
    void unProprietaireNePeutPasElargirSaVueAvecUnFiltre() throws Exception {
        Proprio a = proprio("Alpha");
        Proprio b = proprio("Bravo");

        JsonNode liste = corps(mvc.perform(get("/api/v1/immeubles").with(a.auth()).param("proprietaireTiersId", b.fiche().toString())));
        JsonNode lots = corps(mvc.perform(get("/api/v1/lots").with(a.auth()).param("proprietaireTiersId", b.fiche().toString())));
        JsonNode parImmeuble = corps(mvc.perform(get("/api/v1/lots").with(a.auth()).param("immeubleId", b.immeuble().toString())));

        assertThat(textes(liste.path("content"), "id")).containsExactly(a.immeuble().toString());
        assertThat(textes(lots.path("content"), "id")).containsExactly(a.lot().toString());
        assertThat(parImmeuble.path("totalElements").asInt()).isZero();
        mvc.perform(get("/api/v1/immeubles").with(a.auth()).param("q", "Bravo")).andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void unProprietaireNeVoitQueLesLotsDeSesImmeubles() throws Exception {
        Proprio a = proprio("Alpha");
        Proprio b = proprio("Bravo");
        creerLot(a.immeuble(), "A2", "BUREAU");

        JsonNode lots = corps(mvc.perform(get("/api/v1/lots").with(a.auth())));
        assertThat(lots.path("totalElements").asInt()).isEqualTo(2);
        assertThat(textes(lots.path("content"), "immeubleId")).containsOnly(a.immeuble().toString());

        mvc.perform(get("/api/v1/lots/{id}", a.lot()).with(a.auth())).andExpect(status().isOk());
        mvc.perform(get("/api/v1/lots/{id}", b.lot()).with(a.auth()))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value("LOT_INTROUVABLE"));
        mvc.perform(get("/api/v1/lots/{id}/photos", b.lot()).with(a.auth())).andExpect(status().isNotFound());
        mvc.perform(get("/api/v1/lots/{id}/photos", a.lot()).with(a.auth())).andExpect(status().isOk());
    }

    @Test
    void lesPhotosDesBiensDUnAutreProprietaireNeSontPasTelechargeables() throws Exception {
        Proprio a = proprio("Alpha");
        Proprio b = proprio("Bravo");
        String photoA = corps(envoyerPhoto("immeubles", a.immeuble())).path("fichierId").asText();
        String photoLotA = corps(envoyerPhoto("lots", a.lot())).path("fichierId").asText();
        String photoB = corps(envoyerPhoto("immeubles", b.immeuble())).path("fichierId").asText();
        String photoLotB = corps(envoyerPhoto("lots", b.lot())).path("fichierId").asText();

        mvc.perform(get("/api/v1/immeubles/{id}/photos", a.immeuble()).with(a.auth())).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get("/api/v1/fichiers/{id}", photoA).with(a.auth())).andExpect(status().isOk());
        mvc.perform(get("/api/v1/fichiers/{id}", photoLotA).with(a.auth())).andExpect(status().isOk());
        mvc.perform(get("/api/v1/fichiers/{id}", photoB).with(a.auth())).andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FICHIER_ACCES_REFUSE"));
        mvc.perform(get("/api/v1/fichiers/{id}", photoLotB).with(a.auth())).andExpect(status().isForbidden());
    }

    @Test
    void unCompteSansFicheRattacheeNeVoitRien() throws Exception {
        Proprio a = proprio("Alpha");
        RequestPostProcessor orphelin = jwtRole(Role.PROPRIETAIRE_TIERS, compte());

        mvc.perform(get("/api/v1/immeubles").with(orphelin)).andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(0));
        mvc.perform(get("/api/v1/lots").with(orphelin)).andExpect(jsonPath("$.totalElements").value(0));
        mvc.perform(get("/api/v1/immeubles/{id}", a.immeuble()).with(orphelin)).andExpect(status().isNotFound());
        String photo = corps(envoyerPhoto("immeubles", a.immeuble())).path("fichierId").asText();
        mvc.perform(get("/api/v1/fichiers/{id}", photo).with(orphelin)).andExpect(status().isForbidden());
    }

    @Test
    void uneFicheArchiveeNeDonnePlusAccesAuxBiens() throws Exception {
        Proprio a = proprio("Alpha");
        mvc.perform(get("/api/v1/immeubles/{id}", a.immeuble()).with(a.auth())).andExpect(status().isOk());

        proprietaires.archiver(a.fiche());

        mvc.perform(get("/api/v1/immeubles/{id}", a.immeuble()).with(a.auth())).andExpect(status().isNotFound());
        mvc.perform(get("/api/v1/immeubles").with(a.auth())).andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void lAdministrateurVoitToutEtPeutFiltrerParProprietaire() throws Exception {
        Proprio a = proprio("Alpha");
        proprio("Bravo");
        creerImmeuble("Sans propriétaire", "Douala", "Akwa");

        assertThat(corps(mvc.perform(get("/api/v1/immeubles").with(ADMIN))).path("totalElements").asInt()).isEqualTo(3);
        assertThat(corps(mvc.perform(get("/api/v1/immeubles").with(GESTIONNAIRE))).path("totalElements").asInt()).isEqualTo(3);
        JsonNode filtre = corps(mvc.perform(get("/api/v1/immeubles").with(ADMIN).param("proprietaireTiersId", a.fiche().toString())));
        assertThat(textes(filtre.path("content"), "id")).containsExactly(a.immeuble().toString());
        assertThat(corps(mvc.perform(get("/api/v1/lots").with(ADMIN).param("proprietaireTiersId", a.fiche().toString()))).path("totalElements").asInt()).isEqualTo(1);
    }

    @Test
    void rattacherUnImmeubleAUnProprietaire() throws Exception {
        Proprio a = proprio("Alpha");
        UUID immeuble = creerImmeuble("Libre", "Douala", "Akwa");
        String corps = json("nom", "Libre", "adresse", "12 rue des Palmiers", "ville", "Douala");

        mvc.perform(put("/api/v1/immeubles/{id}", immeuble).with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(json("nom", "Libre", "adresse", "x", "ville", "Douala", "proprietaireTiersId", a.fiche().toString())))
                .andExpect(status().isOk()).andExpect(jsonPath("$.proprietaireTiersId").value(a.fiche().toString()));
        mvc.perform(get("/api/v1/immeubles/{id}", immeuble).with(a.auth())).andExpect(status().isOk());

        // Détacher : l'immeuble disparaît de la vue du propriétaire
        mvc.perform(put("/api/v1/immeubles/{id}", immeuble).with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(corps))
                .andExpect(status().isOk()).andExpect(jsonPath("$.proprietaireTiersId").doesNotExist());
        mvc.perform(get("/api/v1/immeubles/{id}", immeuble).with(a.auth())).andExpect(status().isNotFound());
    }

    @Test
    void ProprietaireInconnuOuArchiveRefuse() throws Exception {
        Proprio a = proprio("Alpha");
        UUID immeuble = creerImmeuble("Libre", "Douala", "Akwa");
        proprietaires.archiver(a.fiche());

        for (UUID refuse : new UUID[] {UUID.randomUUID(), a.fiche()}) {
            mvc.perform(put("/api/v1/immeubles/{id}", immeuble).with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                            .content(json("nom", "Libre", "adresse", "x", "ville", "Douala", "proprietaireTiersId", refuse.toString())))
                    .andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value("PROPRIETAIRE_INTROUVABLE"));
            mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/v1/immeubles").with(ADMIN)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json("nom", "Neuf", "adresse", "x", "ville", "Douala", "proprietaireTiersId", refuse.toString())))
                    .andExpect(status().isNotFound());
        }
    }
}
