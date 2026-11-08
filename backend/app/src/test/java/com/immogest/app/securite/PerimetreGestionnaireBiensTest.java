package com.immogest.app.securite;

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
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

/**
 * GESTIONNAIRE_BIENS (docs/adr/0009-equipe-et-perimetres.md, étape 12.3) : baux, locataires, relances et signalements dans les seuls
 * immeubles affectés ; les locataires qu'il crée lui restent visibles avant leur premier bail ; hors périmètre : 404 ; tout le reste : 403.
 */
@IntegrationTest
class PerimetreGestionnaireBiensTest {

    private static final RequestPostProcessor ADMIN = jwtRole(Role.ADMIN, UUID.fromString("00000000-0000-0000-0000-0000000000c1"));
    private static final AtomicLong TEL = new AtomicLong(650_000_000L);

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    UtilisateurService utilisateurs;

    UUID immeubleA;
    UUID immeubleB;
    UUID lotA;
    UUID lotB;
    UUID lotA2;
    UUID locataireA;
    UUID locataireB;
    UUID bailA;
    UUID bailB;
    UUID employe;
    RequestPostProcessor lui;

    @BeforeEach
    void preparer() throws Exception {
        String suffixe = Long.toString(System.nanoTime(), 36);
        LocalDate debut = LocalDate.now(ZoneId.of("Africa/Douala")).withDayOfMonth(1).minusMonths(3);
        immeubleA = creer("/api/v1/immeubles", "nom", "Gest A " + suffixe, "adresse", "1 rue", "ville", "Douala");
        immeubleB = creer("/api/v1/immeubles", "nom", "Gest B " + suffixe, "adresse", "2 rue", "ville", "Douala");
        lotA = creer("/api/v1/lots", "immeubleId", immeubleA.toString(), "reference", "GA1-" + suffixe, "type", "STUDIO");
        lotA2 = creer("/api/v1/lots", "immeubleId", immeubleA.toString(), "reference", "GA2-" + suffixe, "type", "STUDIO");
        lotB = creer("/api/v1/lots", "immeubleId", immeubleB.toString(), "reference", "GB1-" + suffixe, "type", "STUDIO");
        locataireA = creer("/api/v1/locataires", "nom", "Loc Gest A " + suffixe, "telephone", tel());
        locataireB = creer("/api/v1/locataires", "nom", "Loc Gest B " + suffixe, "telephone", tel());
        bailA = bailActif(lotA, locataireA, debut);
        bailB = bailActif(lotB, locataireB, debut);
        employe = nouvelEmploye();
        lui = jwtRole(Role.GESTIONNAIRE_BIENS, employe);
        affecter(employe, immeubleA);
    }

    // ------------------------------------------------------------------ lecture

    @Test
    void ne_voit_que_son_perimetre() throws Exception {
        assertThat(ids(mvc.perform(get("/api/v1/immeubles").param("size", "100").with(lui)))).containsExactly(immeubleA);
        assertThat(ids(mvc.perform(get("/api/v1/lots").param("size", "100").with(lui)))).containsExactlyInAnyOrder(lotA, lotA2);
        assertThat(ids(mvc.perform(get("/api/v1/baux").param("size", "100").with(lui)))).containsExactly(bailA);
        assertThat(ids(mvc.perform(get("/api/v1/locataires").param("size", "100").with(lui)))).containsExactly(locataireA);

        mvc.perform(get("/api/v1/locataires/{id}", locataireA).with(lui)).andExpect(status().isOk());
        mvc.perform(get("/api/v1/locataires/{id}", locataireB).with(lui)).andExpect(status().isNotFound());
        mvc.perform(get("/api/v1/locataires/{id}/garants", locataireB).with(lui)).andExpect(status().isNotFound());
        mvc.perform(get("/api/v1/locataires/{id}/pieces", locataireB).with(lui)).andExpect(status().isNotFound());
        mvc.perform(get("/api/v1/baux/{id}/documents", bailB).with(lui)).andExpect(status().isNotFound());
        mvc.perform(get("/api/v1/baux/{id}/apercu", bailB).with(lui)).andExpect(status().isNotFound());
        mvc.perform(get("/api/v1/baux/{id}/documents", bailA).with(lui)).andExpect(status().isOk());
    }

    @Test
    void sans_affectation_il_ne_voit_rien() throws Exception {
        RequestPostProcessor sansRien = jwtRole(Role.GESTIONNAIRE_BIENS, nouvelEmploye());
        assertThat(ids(mvc.perform(get("/api/v1/immeubles").with(sansRien)))).isEmpty();
        assertThat(ids(mvc.perform(get("/api/v1/baux").with(sansRien)))).isEmpty();
        assertThat(ids(mvc.perform(get("/api/v1/locataires").with(sansRien)))).isEmpty();
        mvc.perform(get("/api/v1/arrieres").with(sansRien)).andExpect(status().isOk()).andExpect(jsonPath("$.content").isEmpty());
        mvc.perform(get("/api/v1/signalements").with(sansRien)).andExpect(status().isOk()).andExpect(jsonPath("$.content").isEmpty());
    }

    // ------------------------------------------------------------------ locataires

    @Test
    void retrouve_les_locataires_qu_il_a_crees_avant_leur_premier_bail() throws Exception {
        UUID cree = id(mvc.perform(post("/api/v1/locataires").with(lui).contentType(MediaType.APPLICATION_JSON)
                .content(json("nom", "Nouveau Sans Bail", "telephone", tel()))).andExpect(status().isCreated()));
        assertThat(ids(mvc.perform(get("/api/v1/locataires").param("size", "100").with(lui)))).containsExactlyInAnyOrder(locataireA, cree);
        mvc.perform(get("/api/v1/locataires/{id}", cree).with(lui)).andExpect(status().isOk());

        // un autre employé (même périmètre) ne le voit pas tant qu'il n'a pas de bail dans ce périmètre ; l'ADMIN le voit
        UUID autreId = nouvelEmploye();
        RequestPostProcessor autre = jwtRole(Role.GESTIONNAIRE_BIENS, autreId);
        affecter(autreId, immeubleA);
        assertThat(ids(mvc.perform(get("/api/v1/locataires").param("size", "100").with(autre)))).containsExactly(locataireA);
        mvc.perform(get("/api/v1/locataires/{id}", cree).with(autre)).andExpect(status().isNotFound());
        assertThat(ids(mvc.perform(get("/api/v1/locataires").param("size", "100").with(ADMIN)))).contains(cree);
    }

    @Test
    void ne_modifie_ni_n_archive_un_locataire_hors_perimetre() throws Exception {
        mvc.perform(put("/api/v1/locataires/{id}", locataireB).with(lui).contentType(MediaType.APPLICATION_JSON)
                .content(json("nom", "Pirate", "telephone", tel()))).andExpect(status().isNotFound());
        mvc.perform(delete("/api/v1/locataires/{id}", locataireB).with(lui)).andExpect(status().isNotFound());
        mvc.perform(put("/api/v1/locataires/{id}", locataireA).with(lui).contentType(MediaType.APPLICATION_JSON)
                .content(json("nom", "Loc A modifié", "telephone", tel()))).andExpect(status().isOk());
    }

    // ------------------------------------------------------------------ baux

    @Test
    void cree_un_bail_dans_son_perimetre_avec_un_locataire_visible() throws Exception {
        UUID cree = id(mvc.perform(post("/api/v1/locataires").with(lui).contentType(MediaType.APPLICATION_JSON)
                .content(json("nom", "Futur Locataire", "telephone", tel()))).andExpect(status().isCreated()));
        String debut = LocalDate.now(ZoneId.of("Africa/Douala")).plusDays(5).toString();

        // lot du périmètre + locataire créé par lui : accepté
        UUID bail = id(mvc.perform(post("/api/v1/baux").with(lui).contentType(MediaType.APPLICATION_JSON).content(corpsBail(lotA2, cree, debut)))
                .andExpect(status().isCreated()));
        mvc.perform(post("/api/v1/baux/{id}/activer", bail).with(lui)).andExpect(status().isOk());
        assertThat(ids(mvc.perform(get("/api/v1/baux").param("size", "100").with(lui)))).contains(bail);

        // lot hors périmètre, ou locataire hors périmètre : introuvables
        mvc.perform(post("/api/v1/baux").with(lui).contentType(MediaType.APPLICATION_JSON).content(corpsBail(lotB, cree, debut)))
                .andExpect(status().isNotFound());
        mvc.perform(post("/api/v1/baux").with(lui).contentType(MediaType.APPLICATION_JSON).content(corpsBail(lotA2, locataireB, debut)))
                .andExpect(status().isNotFound());
    }

    @Test
    void ne_modifie_ni_n_active_ni_ne_resilie_un_bail_hors_perimetre() throws Exception {
        String debut = LocalDate.now(ZoneId.of("Africa/Douala")).plusDays(5).toString();
        mvc.perform(put("/api/v1/baux/{id}", bailB).with(lui).contentType(MediaType.APPLICATION_JSON).content(corpsBail(lotA2, locataireA, debut)))
                .andExpect(status().isNotFound());
        mvc.perform(post("/api/v1/baux/{id}/resilier", bailB).with(lui).contentType(MediaType.APPLICATION_JSON)
                .content(json("dateSortie", debut, "motif", "test"))).andExpect(status().isNotFound());
        mvc.perform(post("/api/v1/baux/{id}/renouveler", bailB).with(lui).contentType(MediaType.APPLICATION_JSON)
                .content(json("dateFin", debut))).andExpect(status().isNotFound());
        mvc.perform(delete("/api/v1/baux/{id}", bailB).with(lui)).andExpect(status().isNotFound());
    }

    // ------------------------------------------------------------------ finance, relances, signalements

    @Test
    void arrieres_relances_et_paiements_restent_dans_le_perimetre() throws Exception {
        await().atMost(Duration.ofSeconds(20)).until(() -> contenu(mvc.perform(get("/api/v1/echeances").param("bailId", bailA.toString()).param("size", "100").with(ADMIN))).size() >= 2
                && contenu(mvc.perform(get("/api/v1/echeances").param("bailId", bailB.toString()).param("size", "100").with(ADMIN))).size() >= 2);

        List<String> bailsEnRetard = new ArrayList<>();
        contenu(mvc.perform(get("/api/v1/arrieres").param("size", "100").with(lui))).forEach(n -> bailsEnRetard.add(n.path("bailId").asText()));
        assertThat(bailsEnRetard).isNotEmpty().containsOnly(bailA.toString());

        UUID echeanceB = UUID.fromString(contenu(mvc.perform(get("/api/v1/echeances").param("bailId", bailB.toString()).with(ADMIN))).get(0).path("id").asText());
        mvc.perform(post("/api/v1/relances").with(lui).contentType(MediaType.APPLICATION_JSON).content(json("echeanceId", echeanceB.toString())))
                .andExpect(status().isNotFound());
        mvc.perform(post("/api/v1/mises-en-demeure").with(lui).contentType(MediaType.APPLICATION_JSON).content(json("echeanceId", echeanceB.toString())))
                .andExpect(status().isNotFound());
        mvc.perform(get("/api/v1/relances").param("bailId", bailB.toString()).with(lui)).andExpect(status().isNotFound());
        mvc.perform(get("/api/v1/relances").param("echeanceId", echeanceB.toString()).with(lui)).andExpect(status().isNotFound());
        mvc.perform(get("/api/v1/relances").with(lui)).andExpect(status().isOk()).andExpect(jsonPath("$.content").isEmpty());
        mvc.perform(get("/api/v1/mises-en-demeure").param("bailId", bailB.toString()).with(lui)).andExpect(status().isNotFound());

        // relance manuelle dans son périmètre
        UUID echeanceA = UUID.fromString(contenu(mvc.perform(get("/api/v1/echeances").param("bailId", bailA.toString()).with(ADMIN))).get(0).path("id").asText());
        mvc.perform(post("/api/v1/relances").with(lui).contentType(MediaType.APPLICATION_JSON).content(json("echeanceId", echeanceA.toString())))
                .andExpect(status().isCreated());

        // encaisse chez A, pas chez B
        mvc.perform(post("/api/v1/paiements").with(lui).contentType(MediaType.APPLICATION_JSON)
                .content(json("bailId", bailA.toString(), "montant", 10_000, "mode", "ESPECES"))).andExpect(status().isCreated());
        mvc.perform(post("/api/v1/paiements").with(lui).contentType(MediaType.APPLICATION_JSON)
                .content(json("bailId", bailB.toString(), "montant", 10_000, "mode", "ESPECES"))).andExpect(status().isNotFound());
    }

    @Test
    void signalements_hors_perimetre_introuvables() throws Exception {
        mvc.perform(get("/api/v1/signalements").with(lui)).andExpect(status().isOk());
        mvc.perform(get("/api/v1/signalements").param("bailId", bailB.toString()).with(lui)).andExpect(status().isNotFound());
        mvc.perform(get("/api/v1/signalements/{id}", UUID.randomUUID()).with(lui)).andExpect(status().isNotFound());
        mvc.perform(put("/api/v1/signalements/{id}/statut", UUID.randomUUID()).with(lui).contentType(MediaType.APPLICATION_JSON)
                .content(json("statut", "EN_COURS"))).andExpect(status().isNotFound());
    }

    // ------------------------------------------------------------------ refus par défaut

    @Test
    void les_routes_non_ouvertes_sont_refusees() throws Exception {
        mvc.perform(post("/api/v1/immeubles").with(lui).contentType(MediaType.APPLICATION_JSON)
                .content(json("nom", "X", "adresse", "x", "ville", "x"))).andExpect(status().isForbidden());
        mvc.perform(post("/api/v1/lots").with(lui).contentType(MediaType.APPLICATION_JSON)
                .content(json("immeubleId", immeubleA.toString(), "reference", "ZZ", "type", "STUDIO"))).andExpect(status().isForbidden());
        mvc.perform(post("/api/v1/paiements/{id}/contre-passation", UUID.randomUUID()).with(lui).contentType(MediaType.APPLICATION_JSON)
                .content(json("motif", "erreur"))).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/reversements").with(lui)).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/depenses").with(lui)).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/proprietaires-tiers").with(lui)).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/prestataires").with(lui)).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/utilisateurs").with(lui)).andExpect(status().isForbidden());
        mvc.perform(put("/api/v1/utilisateurs/{id}/affectations", employe).with(lui).contentType(MediaType.APPLICATION_JSON)
                .content(json("immeubleIds", List.of(immeubleB)))).andExpect(status().isForbidden());
        mvc.perform(put("/api/v1/relances/parametres").with(lui).contentType(MediaType.APPLICATION_JSON)
                .content(json("actif", true, "niveaux", List.of(-5, 3), "delaiMiseEnDemeureJours", 8))).andExpect(status().isForbidden());
        mvc.perform(post("/api/v1/relances/executer").with(lui)).andExpect(status().isForbidden());
        mvc.perform(put("/api/v1/baux/modele").with(lui).contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/tableau-de-bord/synthese").with(lui)).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/import/modele").param("type", "IMMEUBLES").with(lui)).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/sauvegardes").with(lui)).andExpect(status().isForbidden());
    }

    @Test
    void les_affectations_sont_acceptees_sans_double_authentification() throws Exception {
        mvc.perform(post("/api/v1/utilisateurs").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("nom", "Sans 2FA", "telephone", tel(), "role", "GESTIONNAIRE_BIENS", "deuxFacteursActif", false,
                        "motDePasse", SecuriteTestSupport.MOT_DE_PASSE)))
                .andExpect(status().isCreated());
        mvc.perform(get("/api/v1/utilisateurs/{id}/affectations", employe).with(ADMIN)).andExpect(status().isOk())
                .andExpect(jsonPath("$.immeubleIds[0]").value(immeubleA.toString()));
    }

    @Test
    void retirer_l_affectation_agit_tout_de_suite() throws Exception {
        assertThat(ids(mvc.perform(get("/api/v1/baux").with(lui)))).containsExactly(bailA);
        affecter(employe);
        assertThat(ids(mvc.perform(get("/api/v1/baux").with(lui)))).isEmpty();
        mvc.perform(get("/api/v1/locataires/{id}", locataireA).with(lui)).andExpect(status().isNotFound());
    }

    private String corpsBail(UUID lot, UUID locataire, String debut) {
        return json("lotId", lot.toString(), "locataireId", locataire.toString(), "dateDebut", debut, "loyer", 120_000, "charges", 5_000,
                "periodicite", "MENSUELLE", "jourEcheance", 1, "moisAvance", 0);
    }

    private UUID id(ResultActions r) throws Exception {
        return UUID.fromString(mapper.readTree(r.andReturn().getResponse().getContentAsString()).path("id").asText());
    }

    private JsonNode contenu(ResultActions r) throws Exception {
        return mapper.readTree(r.andReturn().getResponse().getContentAsString()).path("content");
    }

    // ------------------------------------------------------------------ outils

    private static String tel() {
        return "+237" + TEL.incrementAndGet();
    }

    private UUID creer(String chemin, Object... champs) throws Exception {
        String reponse = mvc.perform(post(chemin).with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json(champs)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return UUID.fromString(mapper.readTree(reponse).path("id").asText());
    }

    private UUID bailActif(UUID lot, UUID locataire, LocalDate debut) throws Exception {
        UUID bail = creer("/api/v1/baux", "lotId", lot.toString(), "locataireId", locataire.toString(), "dateDebut", debut.toString(),
                "loyer", 150_000, "charges", 10_000, "periodicite", "MENSUELLE", "jourEcheance", 1, "moisAvance", 0);
        mvc.perform(post("/api/v1/baux/{id}/activer", bail).with(ADMIN)).andExpect(status().isOk());
        return bail;
    }

    private UUID nouvelEmploye() {
        return utilisateurs.creer(new UtilisateurService.Saisie("Employé " + System.nanoTime(), tel(), null, Role.GESTIONNAIRE_BIENS, true, true),
                SecuriteTestSupport.MOT_DE_PASSE).id();
    }

    private void affecter(UUID utilisateur, UUID... immeubles) throws Exception {
        List<String> ids = new ArrayList<>();
        for (UUID i : immeubles) {
            ids.add(i.toString());
        }
        mvc.perform(put("/api/v1/utilisateurs/{id}/affectations", utilisateur).with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("immeubleIds", ids))).andExpect(status().isOk());
    }

    private List<UUID> ids(ResultActions r) throws Exception {
        JsonNode contenu = mapper.readTree(r.andExpect(status().isOk()).andReturn().getResponse().getContentAsString()).path("content");
        List<UUID> ids = new ArrayList<>();
        contenu.forEach(n -> ids.add(UUID.fromString(n.path("id").asText())));
        return ids;
    }
}
