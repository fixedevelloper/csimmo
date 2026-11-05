package com.immogest.app.patrimoine;

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
import com.immogest.app.patrimoine.api.LecteurLots;
import com.immogest.app.securite.Role;
import com.immogest.app.shared.BailActive;
import com.immogest.app.shared.BailTermine;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.support.TransactionTemplate;

/** CRUD, filtres, pagination, règles de statut et d'archivage, événements du bail, rôles (licence de dev : 100 lots). */
@IntegrationTest
class PatrimoineApiTest extends PatrimoineTestBase {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    JdbcTemplate jdbc;
    @Autowired
    ApplicationEventPublisher evenements;
    @Autowired
    TransactionTemplate transaction;
    @Autowired
    LecteurLots lecteurLots;

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

    private void publierDansTransaction(Object evenement) {
        transaction.executeWithoutResult(s -> evenements.publishEvent(evenement));
    }

    // ================================================================== immeubles

    @Test
    void creerLireModifierUnImmeuble() throws Exception {
        String reponse = mvc.perform(post("/api/v1/immeubles").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(json("nom", "  Résidence Les Palmiers ", "adresse", "12 rue des Palmiers", "quartier", "Bonanjo",
                                "ville", "Douala", "latitude", 4.0511, "longitude", 9.7679, "description", "R+4, gardien")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nom").value("Résidence Les Palmiers"))
                .andExpect(jsonPath("$.ville").value("Douala"))
                .andExpect(jsonPath("$.latitude").value(4.0511))
                .andExpect(jsonPath("$.nombreLots").value(0))
                .andExpect(jsonPath("$.archive").value(false))
                .andReturn().getResponse().getContentAsString();
        UUID id = UUID.fromString(mapper.readTree(reponse).path("id").asText());

        mvc.perform(get("/api/v1/immeubles/{id}", id).with(GESTIONNAIRE))
                .andExpect(status().isOk()).andExpect(jsonPath("$.quartier").value("Bonanjo"));

        mvc.perform(put("/api/v1/immeubles/{id}", id).with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(json("nom", "Résidence Palmiers II", "adresse", "14 rue des Palmiers", "ville", "Douala")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Résidence Palmiers II"))
                .andExpect(jsonPath("$.quartier").doesNotExist())
                .andExpect(jsonPath("$.latitude").doesNotExist());
    }

    @Test
    void immeubleInconnuOuDonneesInvalides() throws Exception {
        mvc.perform(get("/api/v1/immeubles/{id}", UUID.randomUUID()).with(ADMIN))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value("IMMEUBLE_INTROUVABLE"));

        mvc.perform(post("/api/v1/immeubles").with(ADMIN).contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("VALIDATION_ERREUR"));
        mvc.perform(post("/api/v1/immeubles").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(json("nom", "X", "adresse", "Y", "ville", "Douala", "latitude", 4.05)))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("IMMEUBLE_GPS_INCOMPLET"));
        mvc.perform(post("/api/v1/immeubles").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(json("nom", "X", "adresse", "Y", "ville", "Douala", "latitude", 91, "longitude", 9.7)))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("VALIDATION_ERREUR"));
        mvc.perform(post("/api/v1/immeubles").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(json("nom", "   ", "adresse", "Y", "ville", "Douala")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void archiverUnImmeubleLeCacheDeLaListeSansLeSupprimer() throws Exception {
        UUID id = creerImmeuble("À archiver", "Douala", "Akwa");

        mvc.perform(delete("/api/v1/immeubles/{id}", id).with(ADMIN)).andExpect(status().isNoContent());

        mvc.perform(get("/api/v1/immeubles").with(ADMIN).param("ville", "Douala"))
                .andExpect(jsonPath("$.totalElements").value(0));
        mvc.perform(get("/api/v1/immeubles").with(ADMIN).param("ville", "Douala").param("inclureArchives", "true"))
                .andExpect(jsonPath("$.totalElements").value(1)).andExpect(jsonPath("$.content[0].archive").value(true));
        mvc.perform(get("/api/v1/immeubles/{id}", id).with(ADMIN)).andExpect(status().isOk());
        assertThat(jdbc.queryForObject("SELECT count(*) FROM immeuble", Integer.class)).isEqualTo(1);

        mvc.perform(put("/api/v1/immeubles/{id}", id).with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(json("nom", "X", "adresse", "Y", "ville", "Douala")))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("IMMEUBLE_ARCHIVE"));
    }

    @Test
    void unImmeubleAvecDesLotsActifsNePeutPasEtreArchive() throws Exception {
        UUID immeuble = creerImmeuble("Occupé", "Douala", "Akwa");
        UUID lot = creerLot(immeuble, "A1", "APPARTEMENT");

        mvc.perform(delete("/api/v1/immeubles/{id}", immeuble).with(ADMIN))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("IMMEUBLE_LOTS_ACTIFS"));

        mvc.perform(delete("/api/v1/lots/{id}", lot).with(ADMIN)).andExpect(status().isNoContent());
        mvc.perform(delete("/api/v1/immeubles/{id}", immeuble).with(ADMIN)).andExpect(status().isNoContent());
    }

    // ------------------------------------------------------------------ filtres, pagination, tri

    @Test
    void filtresDeLaListeDesImmeubles() throws Exception {
        creerImmeuble("Résidence Soleil", "Douala", "Bonanjo");
        creerImmeuble("Villa Horizon", "Douala", "Akwa");
        creerImmeuble("Tour Mfoundi", "Yaoundé", "Bastos");

        assertThat(totalImmeubles("ville", "douala")).as("ville : insensible à la casse").isEqualTo(2);
        assertThat(totalImmeubles("ville", "yaou")).as("ville : contient").isEqualTo(1);
        assertThat(totalImmeubles("quartier", "AKW")).isEqualTo(1);
        assertThat(totalImmeubles("q", "soleil")).as("recherche sur le nom").isEqualTo(1);
        assertThat(totalImmeubles("q", "palmiers")).as("recherche sur l'adresse").isEqualTo(3);
        assertThat(totalImmeubles("q", "n'existe pas")).isZero();
    }

    @Test
    void lesCaracteresJokersDeLaRechercheSontLitteraux() throws Exception {
        creerImmeuble("Immeuble 100%", "Douala", "Akwa");
        creerImmeuble("Immeuble 1000", "Douala", "Akwa");

        assertThat(totalImmeubles("q", "100%")).as("« % » ne doit pas matcher n'importe quoi").isEqualTo(1);
        assertThat(totalImmeubles("q", "_")).as("« _ » ne doit pas matcher un caractère quelconque").isZero();
    }

    @Test
    void paginationEtTri() throws Exception {
        for (String nom : List.of("Alpha", "Bravo", "Charlie", "Delta", "Echo")) {
            creerImmeuble(nom, "Douala", "Akwa");
        }

        JsonNode page0 = corps(mvc.perform(get("/api/v1/immeubles").with(ADMIN).param("size", "2").param("page", "0").param("sort", "nom")));
        JsonNode page2 = corps(mvc.perform(get("/api/v1/immeubles").with(ADMIN).param("size", "2").param("page", "2").param("sort", "nom")));
        JsonNode desc = corps(mvc.perform(get("/api/v1/immeubles").with(ADMIN).param("size", "5").param("sort", "nom,desc")));

        assertThat(page0.path("totalElements").asInt()).isEqualTo(5);
        assertThat(page0.path("size").asInt()).isEqualTo(2);
        assertThat(textes(page0.path("content"), "nom")).containsExactly("Alpha", "Bravo");
        assertThat(textes(page2.path("content"), "nom")).containsExactly("Echo");
        assertThat(textes(desc.path("content"), "nom")).containsExactly("Echo", "Delta", "Charlie", "Bravo", "Alpha");
    }

    @Test
    void unTriSurUnChampNonAutoriseEstIgnoreEtLaTailleDePageEstBornee() throws Exception {
        creerImmeuble("Seul", "Douala", "Akwa");

        mvc.perform(get("/api/v1/immeubles").with(ADMIN).param("sort", "motDePasse,desc;drop table")).andExpect(status().isOk());
        mvc.perform(get("/api/v1/immeubles").with(ADMIN).param("size", "100000"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.size").value(100));
        mvc.perform(get("/api/v1/immeubles").with(ADMIN).param("page", "-3").param("size", "0"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.page").value(0));
    }

    private int totalImmeubles(String parametre, String valeur) throws Exception {
        return corps(mvc.perform(get("/api/v1/immeubles").with(ADMIN).param(parametre, valeur))).path("totalElements").asInt();
    }

    // ================================================================== lots

    @Test
    void creerUnLotAvecEquipementsEtCompteurs() throws Exception {
        UUID immeuble = creerImmeuble("Résidence A", "Douala", "Bonanjo");

        String reponse = creerLotBrut(immeuble, " A101 ", "APPARTEMENT", "surfaceM2", 65.5, "pieces", 3, "loyerIndicatif", 150000,
                "equipements", List.of("Climatisation", "Parking", "climatisation ", "Gardiennage", " "),
                "compteurs", List.of(Map.of("type", "EAU", "numero", "E-123"), Map.of("type", "ELECTRICITE", "numero", "ENEO-9")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reference").value("A101"))
                .andExpect(jsonPath("$.type").value("APPARTEMENT"))
                .andExpect(jsonPath("$.statut").value("LIBRE"))
                .andExpect(jsonPath("$.surfaceM2").value(65.5))
                .andExpect(jsonPath("$.loyerIndicatif").value(150000))
                .andExpect(jsonPath("$.immeubleNom").value("Résidence A"))
                .andExpect(jsonPath("$.ville").value("Douala"))
                .andExpect(jsonPath("$.compteurs.length()").value(2))
                .andReturn().getResponse().getContentAsString();

        JsonNode lot = mapper.readTree(reponse);
        assertThat(lot.path("equipements")).extracting(JsonNode::asText)
                .as("triés, sans doublon ni vide (« climatisation » = « Climatisation »)")
                .containsExactly("Climatisation", "climatisation", "Gardiennage", "Parking");
    }

    @Test
    void modifierUnLotRemplaceEquipementsEtCompteurs() throws Exception {
        UUID immeuble = creerImmeuble("Résidence A", "Douala", "Bonanjo");
        UUID lot = creerLot(immeuble, "A101", "APPARTEMENT", "equipements", List.of("Parking", "Balcon"),
                "compteurs", List.of(Map.of("type", "EAU", "numero", "E-1")));

        mvc.perform(put("/api/v1/lots/{id}", lot).with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(json("reference", "A101-bis", "type", "STUDIO", "loyerIndicatif", 90000,
                                "equipements", List.of("Balcon", "Fibre"), "compteurs", List.of())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reference").value("A101-bis"))
                .andExpect(jsonPath("$.type").value("STUDIO"))
                .andExpect(jsonPath("$.equipements[0]").value("Balcon"))
                .andExpect(jsonPath("$.equipements[1]").value("Fibre"))
                .andExpect(jsonPath("$.equipements.length()").value(2))
                .andExpect(jsonPath("$.compteurs.length()").value(0));

        mvc.perform(get("/api/v1/lots/{id}", lot).with(ADMIN))
                .andExpect(jsonPath("$.equipements.length()").value(2)).andExpect(jsonPath("$.compteurs.length()").value(0));
        assertThat(jdbc.queryForObject("SELECT count(*) FROM compteur", Integer.class)).as("compteur orphelin supprimé").isZero();
    }

    @Test
    void laReferenceEstUniqueParImmeuble() throws Exception {
        UUID a = creerImmeuble("A", "Douala", "Akwa");
        UUID b = creerImmeuble("B", "Douala", "Akwa");
        UUID lotA = creerLot(a, "101", "APPARTEMENT");

        creerLotBrut(a, "101", "STUDIO").andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("LOT_REFERENCE_EXISTANTE"));
        creerLotBrut(b, "101", "STUDIO").andExpect(status().isCreated());

        UUID autre = creerLot(a, "102", "STUDIO");
        mvc.perform(put("/api/v1/lots/{id}", autre).with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(json("reference", "101", "type", "STUDIO")))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("LOT_REFERENCE_EXISTANTE"));
        mvc.perform(put("/api/v1/lots/{id}", lotA).with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(json("reference", "101", "type", "BUREAU")))
                .andExpect(status().isOk()); // garder sa propre référence est permis
    }

    @Test
    void donneesDeLotInvalides() throws Exception {
        UUID immeuble = creerImmeuble("A", "Douala", "Akwa");

        creerLotBrut(null, "A1", "STUDIO").andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("LOT_IMMEUBLE_REQUIS"));
        creerLotBrut(UUID.randomUUID(), "A1", "STUDIO").andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value("IMMEUBLE_INTROUVABLE"));
        creerLotBrut(immeuble, "A1", null).andExpect(status().isBadRequest());
        creerLotBrut(immeuble, "A1", "CHATEAU").andExpect(status().isBadRequest());
        creerLotBrut(immeuble, "", "STUDIO").andExpect(status().isBadRequest());
        creerLotBrut(immeuble, "A1", "STUDIO", "surfaceM2", 0).andExpect(status().isBadRequest());
        creerLotBrut(immeuble, "A1", "STUDIO", "surfaceM2", -5).andExpect(status().isBadRequest());
        creerLotBrut(immeuble, "A1", "STUDIO", "pieces", 101).andExpect(status().isBadRequest());
        creerLotBrut(immeuble, "A1", "STUDIO", "loyerIndicatif", -1).andExpect(status().isBadRequest());
        creerLotBrut(immeuble, "A1", "STUDIO", "compteurs", List.of(Map.of("type", "WIFI", "numero", "1"))).andExpect(status().isBadRequest());
        creerLotBrut(immeuble, "A1", "STUDIO", "compteurs", List.of(Map.of("type", "EAU", "numero", " "))).andExpect(status().isBadRequest());
        assertThat(jdbc.queryForObject("SELECT count(*) FROM lot", Integer.class)).isZero();
    }

    @Test
    void unLotNeChangePasDImmeuble() throws Exception {
        UUID a = creerImmeuble("A", "Douala", "Akwa");
        UUID b = creerImmeuble("B", "Douala", "Akwa");
        UUID lot = creerLot(a, "101", "STUDIO");

        mvc.perform(put("/api/v1/lots/{id}", lot).with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(json("immeubleId", b.toString(), "reference", "101", "type", "STUDIO")))
                .andExpect(status().isUnprocessableEntity()).andExpect(jsonPath("$.code").value("LOT_IMMEUBLE_NON_MODIFIABLE"));
    }

    @Test
    void lImmeubleArchiveNeRecoitPlusDeLots() throws Exception {
        UUID immeuble = creerImmeuble("Fermé", "Douala", "Akwa");
        mvc.perform(delete("/api/v1/immeubles/{id}", immeuble).with(ADMIN)).andExpect(status().isNoContent());

        creerLotBrut(immeuble, "A1", "STUDIO").andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("IMMEUBLE_ARCHIVE"));
    }

    // ------------------------------------------------------------------ filtres des lots

    @Test
    void filtresDeLaListeDesLots() throws Exception {
        UUID bonanjo = creerImmeuble("Résidence Soleil", "Douala", "Bonanjo");
        UUID bastos = creerImmeuble("Tour Mfoundi", "Yaoundé", "Bastos");
        UUID l1 = creerLot(bonanjo, "A1", "APPARTEMENT", "loyerIndicatif", 200000);
        creerLot(bonanjo, "A2", "STUDIO", "loyerIndicatif", 80000);
        creerLot(bonanjo, "B1", "BOUTIQUE", "loyerIndicatif", 350000);
        creerLot(bastos, "T1", "BUREAU", "loyerIndicatif", 500000);
        changerStatut(l1, "EN_TRAVAUX").andExpect(status().isOk());

        assertThat(totalLots("immeubleId", bonanjo.toString())).isEqualTo(3);
        assertThat(totalLots("statut", "EN_TRAVAUX")).isEqualTo(1);
        assertThat(totalLots("statut", "LIBRE")).isEqualTo(3);
        assertThat(totalLots("statut", "OCCUPE")).isZero();
        assertThat(totalLots("type", "STUDIO")).isEqualTo(1);
        assertThat(totalLots("ville", "yaoundé")).isEqualTo(1);
        assertThat(totalLots("quartier", "bonanjo")).isEqualTo(3);
        assertThat(totalLots("q", "mfoundi")).as("recherche sur le nom de l'immeuble").isEqualTo(1);
        assertThat(totalLots("q", "b1")).as("recherche sur la référence").isEqualTo(1);

        JsonNode parLoyer = corps(mvc.perform(get("/api/v1/lots").with(ADMIN).param("sort", "loyerIndicatif,desc")));
        assertThat(textes(parLoyer.path("content"), "reference")).containsExactly("T1", "B1", "A1", "A2");
        mvc.perform(get("/api/v1/lots").with(ADMIN).param("statut", "INCONNU")).andExpect(status().isBadRequest());
    }

    private int totalLots(String parametre, String valeur) throws Exception {
        return corps(mvc.perform(get("/api/v1/lots").with(ADMIN).param(parametre, valeur))).path("totalElements").asInt();
    }

    @Test
    void lesComptesDeLImmeubleSuiventLeStatutDesLots() throws Exception {
        UUID immeuble = creerImmeuble("Comptes", "Douala", "Akwa");
        UUID l1 = creerLot(immeuble, "1", "STUDIO");
        UUID l2 = creerLot(immeuble, "2", "STUDIO");
        creerLot(immeuble, "3", "STUDIO");
        creerLot(immeuble, "4", "STUDIO");
        changerStatut(l1, "EN_TRAVAUX").andExpect(status().isOk());
        publierDansTransaction(new BailActive(UUID.randomUUID(), l2));
        await().atMost(Duration.ofSeconds(15)).untilAsserted(() -> mvc.perform(get("/api/v1/immeubles/{id}", immeuble).with(ADMIN))
                .andExpect(jsonPath("$.lotsOccupes").value(1)));

        mvc.perform(get("/api/v1/immeubles/{id}", immeuble).with(ADMIN))
                .andExpect(jsonPath("$.nombreLots").value(4)).andExpect(jsonPath("$.lotsLibres").value(2))
                .andExpect(jsonPath("$.lotsOccupes").value(1)).andExpect(jsonPath("$.lotsEnTravaux").value(1));
        JsonNode liste = corps(mvc.perform(get("/api/v1/immeubles").with(ADMIN).param("q", "Comptes")));
        assertThat(liste.path("content").get(0).path("nombreLots").asInt()).isEqualTo(4);
    }

    // ------------------------------------------------------------------ statut et archivage

    @Test
    void changerLeStatutManuellementLibreEtEnTravaux() throws Exception {
        UUID lot = creerLot(creerImmeuble("A", "Douala", "Akwa"), "1", "STUDIO");

        changerStatut(lot, "EN_TRAVAUX").andExpect(status().isOk()).andExpect(jsonPath("$.statut").value("EN_TRAVAUX"));
        changerStatut(lot, "LIBRE").andExpect(status().isOk()).andExpect(jsonPath("$.statut").value("LIBRE"));
        changerStatut(lot, "OCCUPE").andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("LOT_STATUT_OCCUPE_PAR_BAIL"));
        mvc.perform(put("/api/v1/lots/{id}/statut", lot).with(ADMIN).contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void unLotOccupeNeChangePasDeStatutNiNEstArchive() throws Exception {
        UUID lot = creerLot(creerImmeuble("A", "Douala", "Akwa"), "1", "STUDIO");
        publierDansTransaction(new BailActive(UUID.randomUUID(), lot));
        await().atMost(Duration.ofSeconds(15)).untilAsserted(() -> mvc.perform(get("/api/v1/lots/{id}", lot).with(ADMIN))
                .andExpect(jsonPath("$.statut").value("OCCUPE")));

        changerStatut(lot, "EN_TRAVAUX").andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("LOT_OCCUPE_STATUT_NON_MODIFIABLE"));
        changerStatut(lot, "LIBRE").andExpect(status().isConflict());
        mvc.perform(delete("/api/v1/lots/{id}", lot).with(ADMIN)).andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("LOT_OCCUPE_ARCHIVAGE_REFUSE"));
    }

    @Test
    void archiverUnLotLeCacheSansLeSupprimerEtPermetDeLeRetrouver() throws Exception {
        UUID lot = creerLot(creerImmeuble("A", "Douala", "Akwa"), "1", "STUDIO");

        mvc.perform(delete("/api/v1/lots/{id}", lot).with(ADMIN)).andExpect(status().isNoContent());

        assertThat(totalLots("q", "1")).isZero();
        assertThat(corps(mvc.perform(get("/api/v1/lots").with(ADMIN).param("inclureArchives", "true"))).path("totalElements").asInt()).isEqualTo(1);
        mvc.perform(get("/api/v1/lots/{id}", lot).with(ADMIN)).andExpect(jsonPath("$.archive").value(true));
        mvc.perform(put("/api/v1/lots/{id}", lot).with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(json("reference", "1", "type", "STUDIO")))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("LOT_ARCHIVE"));
        assertThat(jdbc.queryForObject("SELECT count(*) FROM lot", Integer.class)).isEqualTo(1);
    }

    // ================================================================== événements du bail

    @Test
    void leStatutDuLotSuitLesEvenementsDuBail() throws Exception {
        UUID lot = creerLot(creerImmeuble("A", "Douala", "Akwa"), "1", "STUDIO");
        UUID bail = UUID.randomUUID();

        publierDansTransaction(new BailActive(bail, lot));
        await().atMost(Duration.ofSeconds(15)).untilAsserted(() -> mvc.perform(get("/api/v1/lots/{id}", lot).with(ADMIN))
                .andExpect(jsonPath("$.statut").value("OCCUPE")));

        publierDansTransaction(new BailActive(bail, lot)); // rejeu : sans effet
        publierDansTransaction(new BailTermine(bail, lot));
        await().atMost(Duration.ofSeconds(15)).untilAsserted(() -> mvc.perform(get("/api/v1/lots/{id}", lot).with(ADMIN))
                .andExpect(jsonPath("$.statut").value("LIBRE")));
    }

    @Test
    void leChangementDeStatutParLeBailEstJournalise() throws Exception {
        UUID lot = creerLot(creerImmeuble("A", "Douala", "Akwa"), "1", "STUDIO");

        publierDansTransaction(new BailActive(UUID.randomUUID(), lot));

        await().atMost(Duration.ofSeconds(15)).untilAsserted(() -> assertThat(jdbc.queryForList(
                "SELECT auteur, avant::text AS avant, apres::text AS apres FROM audit_log WHERE action = 'PATRIMOINE.LOT_OCCUPE' AND entite_id = ?", lot))
                .singleElement().satisfies(ligne -> {
                    assertThat(ligne.get("auteur")).isEqualTo("systeme");
                    assertThat((String) ligne.get("avant")).contains("LIBRE");
                    assertThat((String) ligne.get("apres")).contains("OCCUPE");
                }));
    }

    @Test
    void unEvenementPourUnLotInconnuNePlantePasEtLeSuivantEstTraite() throws Exception {
        UUID lot = creerLot(creerImmeuble("A", "Douala", "Akwa"), "1", "STUDIO");

        publierDansTransaction(new BailActive(UUID.randomUUID(), UUID.randomUUID()));
        publierDansTransaction(new BailActive(UUID.randomUUID(), lot));

        await().atMost(Duration.ofSeconds(15)).untilAsserted(() -> mvc.perform(get("/api/v1/lots/{id}", lot).with(ADMIN))
                .andExpect(jsonPath("$.statut").value("OCCUPE")));
    }

    // ================================================================== audit et API pour les autres modules

    @Test
    void lesCreationsEtModificationsSontJournaliseesAvecLAuteur() throws Exception {
        UUID immeuble = creerImmeuble("Audité", "Douala", "Akwa");
        UUID lot = creerLot(immeuble, "Z1", "STUDIO", "loyerIndicatif", 100000);

        await().atMost(Duration.ofSeconds(15)).untilAsserted(() -> {
            assertThat(jdbc.queryForList("SELECT action, auteur FROM audit_log WHERE entite_id = ? ORDER BY date", lot))
                    .extracting(l -> l.get("action")).contains("PATRIMOINE.LOT_CREE");
            assertThat(jdbc.queryForObject("SELECT auteur FROM audit_log WHERE entite_id = ? AND action = 'PATRIMOINE.IMMEUBLE_CREE'",
                    String.class, immeuble)).isEqualTo("00000000-0000-0000-0000-0000000000a1");
        });
    }

    @Test
    void lApiPourLesAutresModulesExposeLeResumeDUnLot() throws Exception {
        UUID lot = creerLot(creerImmeuble("A", "Douala", "Akwa"), "R1", "STUDIO", "loyerIndicatif", 120000);

        var resume = lecteurLots.lot(lot).orElseThrow();

        assertThat(resume.reference()).isEqualTo("R1");
        assertThat(resume.statut()).isEqualTo("LIBRE");
        assertThat(resume.loyerIndicatif()).isEqualTo(120000L);
        assertThat(resume.archive()).isFalse();
        assertThat(lecteurLots.lot(UUID.randomUUID())).isEmpty();
    }

    // ================================================================== rôles

    @Test
    void ladministrateurEtLeGestionnaireOntAccesLesAutresRolesNon() throws Exception {
        UUID immeuble = creerImmeuble("Rôles", "Douala", "Akwa");
        UUID lot = creerLot(immeuble, "1", "STUDIO");

        for (Role role : new Role[] {Role.LOCATAIRE, Role.PRESTATAIRE}) {
            var auth = jwtRole(role);
            String r = role.name();
            mvc.perform(get("/api/v1/immeubles").with(auth)).andExpect(status().isForbidden()).andExpect(jsonPath("$.code").value("ACCES_REFUSE")).andReturn();
            mvc.perform(get("/api/v1/immeubles/{id}", immeuble).with(auth)).andExpect(status().isForbidden());
            mvc.perform(post("/api/v1/immeubles").with(auth).contentType(MediaType.APPLICATION_JSON)
                    .content(json("nom", r, "adresse", "x", "ville", "x"))).andExpect(status().isForbidden());
            mvc.perform(put("/api/v1/immeubles/{id}", immeuble).with(auth).contentType(MediaType.APPLICATION_JSON)
                    .content(json("nom", r, "adresse", "x", "ville", "x"))).andExpect(status().isForbidden());
            mvc.perform(delete("/api/v1/immeubles/{id}", immeuble).with(auth)).andExpect(status().isForbidden());
            mvc.perform(get("/api/v1/lots").with(auth)).andExpect(status().isForbidden());
            mvc.perform(get("/api/v1/lots/{id}", lot).with(auth)).andExpect(status().isForbidden());
            mvc.perform(post("/api/v1/lots").with(auth).contentType(MediaType.APPLICATION_JSON)
                    .content(json("immeubleId", immeuble.toString(), "reference", r, "type", "STUDIO"))).andExpect(status().isForbidden());
            mvc.perform(put("/api/v1/lots/{id}/statut", lot).with(auth).contentType(MediaType.APPLICATION_JSON)
                    .content(json("statut", "EN_TRAVAUX"))).andExpect(status().isForbidden());
            mvc.perform(delete("/api/v1/lots/{id}", lot).with(auth)).andExpect(status().isForbidden());
            mvc.perform(get("/api/v1/immeubles/{id}/photos", immeuble).with(auth)).andExpect(status().isForbidden());
            mvc.perform(get("/api/v1/lots/{id}/photos", lot).with(auth)).andExpect(status().isForbidden());
            mvc.perform(delete("/api/v1/photos/{id}", UUID.randomUUID()).with(auth)).andExpect(status().isForbidden());
        }
        // Un propriétaire tiers consulte ses biens (voir PatrimoineVisibiliteTest) mais ne modifie jamais rien
        var proprietaire = jwtRole(Role.PROPRIETAIRE_TIERS);
        mvc.perform(post("/api/v1/immeubles").with(proprietaire).contentType(MediaType.APPLICATION_JSON)
                .content(json("nom", "x", "adresse", "x", "ville", "x"))).andExpect(status().isForbidden());
        mvc.perform(put("/api/v1/immeubles/{id}", immeuble).with(proprietaire).contentType(MediaType.APPLICATION_JSON)
                .content(json("nom", "x", "adresse", "x", "ville", "x"))).andExpect(status().isForbidden());
        mvc.perform(delete("/api/v1/immeubles/{id}", immeuble).with(proprietaire)).andExpect(status().isForbidden());
        mvc.perform(post("/api/v1/lots").with(proprietaire).contentType(MediaType.APPLICATION_JSON)
                .content(json("immeubleId", immeuble.toString(), "reference", "x", "type", "STUDIO"))).andExpect(status().isForbidden());
        mvc.perform(put("/api/v1/lots/{id}/statut", lot).with(proprietaire).contentType(MediaType.APPLICATION_JSON)
                .content(json("statut", "EN_TRAVAUX"))).andExpect(status().isForbidden());
        mvc.perform(delete("/api/v1/lots/{id}", lot).with(proprietaire)).andExpect(status().isForbidden());
        mvc.perform(delete("/api/v1/photos/{id}", UUID.randomUUID()).with(proprietaire)).andExpect(status().isForbidden());
        changerStatut(lot, "LIBRE").andExpect(status().isOk());
        assertThat(jdbc.queryForObject("SELECT count(*) FROM immeuble", Integer.class)).as("aucun refus n'a modifié les données").isEqualTo(1);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM lot", Integer.class)).isEqualTo(1);

        mvc.perform(get("/api/v1/immeubles").with(GESTIONNAIRE)).andExpect(status().isOk());
        mvc.perform(get("/api/v1/lots").with(GESTIONNAIRE)).andExpect(status().isOk());
    }

    @Test
    void sansAuthentification401() throws Exception {
        mvc.perform(get("/api/v1/immeubles")).andExpect(status().isUnauthorized()).andExpect(jsonPath("$.code").value("NON_AUTHENTIFIE"));
        mvc.perform(get("/api/v1/lots")).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/v1/immeubles").contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isUnauthorized());
        mvc.perform(delete("/api/v1/photos/{id}", UUID.randomUUID())).andExpect(status().isUnauthorized());
    }
}
