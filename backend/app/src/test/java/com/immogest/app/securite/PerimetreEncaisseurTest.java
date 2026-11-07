package com.immogest.app.securite;

import static com.immogest.app.securite.SecuriteTestSupport.json;
import static com.immogest.app.securite.SecuriteTestSupport.jwtRole;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.immogest.app.IntegrationTest;
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
 * ENCAISSEUR (docs/adr/0009-equipe-et-perimetres.md) : il ne voit et n'encaisse que dans les immeubles qui lui sont affectés ;
 * sans affectation il ne voit rien ; hors périmètre c'est « introuvable » (404), jamais 403 ; toute route non ouverte reste refusée.
 */
@IntegrationTest
class PerimetreEncaisseurTest {

    private static final RequestPostProcessor ADMIN = jwtRole(Role.ADMIN, UUID.fromString("00000000-0000-0000-0000-0000000000b1"));
    private static final AtomicLong TEL = new AtomicLong(660_000_000L);

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
    UUID locataireA;
    UUID locataireB;
    UUID bailA;
    UUID bailB;
    UUID employe;
    RequestPostProcessor lui;

    @BeforeEach
    void preparer() throws Exception {
        String suffixe = Long.toString(System.nanoTime(), 36);
        LocalDate debut = LocalDate.now(ZoneId.of("Africa/Douala")).withDayOfMonth(1).minusMonths(1);
        immeubleA = creer("/api/v1/immeubles", "nom", "Immeuble A " + suffixe, "adresse", "1 rue", "ville", "Douala");
        immeubleB = creer("/api/v1/immeubles", "nom", "Immeuble B " + suffixe, "adresse", "2 rue", "ville", "Douala");
        lotA = creer("/api/v1/lots", "immeubleId", immeubleA.toString(), "reference", "A1-" + suffixe, "type", "STUDIO");
        lotB = creer("/api/v1/lots", "immeubleId", immeubleB.toString(), "reference", "B1-" + suffixe, "type", "STUDIO");
        locataireA = creer("/api/v1/locataires", "nom", "Locataire A " + suffixe, "telephone", tel());
        locataireB = creer("/api/v1/locataires", "nom", "Locataire B " + suffixe, "telephone", tel());
        bailA = bailActif(lotA, locataireA, debut);
        bailB = bailActif(lotB, locataireB, debut);
        employe = nouvelEmploye();
        lui = jwtRole(Role.ENCAISSEUR, employe);
        affecter(employe, immeubleA);
    }

    // ------------------------------------------------------------------ lecture dans le périmètre

    @Test
    void ne_voit_que_les_immeubles_lots_baux_et_locataires_de_ses_affectations() throws Exception {
        assertThat(ids(mvc.perform(get("/api/v1/immeubles").param("size", "100").with(lui)))).containsExactly(immeubleA);
        assertThat(ids(mvc.perform(get("/api/v1/lots").param("size", "100").with(lui)))).containsExactly(lotA);
        assertThat(ids(mvc.perform(get("/api/v1/baux").param("size", "100").with(lui)))).containsExactly(bailA);
        assertThat(ids(mvc.perform(get("/api/v1/locataires").param("size", "100").with(lui)))).containsExactly(locataireA);

        mvc.perform(get("/api/v1/immeubles/{id}", immeubleA).with(lui)).andExpect(status().isOk());
        mvc.perform(get("/api/v1/immeubles/{id}", immeubleB).with(lui)).andExpect(status().isNotFound());
        mvc.perform(get("/api/v1/lots/{id}", lotA).with(lui)).andExpect(status().isOk());
        mvc.perform(get("/api/v1/lots/{id}", lotB).with(lui)).andExpect(status().isNotFound());
        mvc.perform(get("/api/v1/baux/{id}", bailA).with(lui)).andExpect(status().isOk());
        mvc.perform(get("/api/v1/baux/{id}", bailB).with(lui)).andExpect(status().isNotFound());
    }

    @Test
    void un_filtre_explicite_ne_sort_pas_du_perimetre() throws Exception {
        assertThat(ids(mvc.perform(get("/api/v1/lots").param("immeubleId", immeubleB.toString()).with(lui)))).isEmpty();
        assertThat(ids(mvc.perform(get("/api/v1/baux").param("lotId", lotB.toString()).with(lui)))).isEmpty();
        assertThat(ids(mvc.perform(get("/api/v1/baux").param("locataireId", locataireB.toString()).with(lui)))).isEmpty();
        assertThat(ids(mvc.perform(get("/api/v1/locataires").param("q", "Locataire B").with(lui)))).isEmpty();
    }

    // ------------------------------------------------------------------ encaissement

    @Test
    void encaisse_dans_son_perimetre_et_pas_ailleurs() throws Exception {
        mvc.perform(post("/api/v1/paiements").with(lui).contentType(MediaType.APPLICATION_JSON)
                .content(json("bailId", bailA.toString(), "montant", 50_000, "mode", "ESPECES")))
                .andExpect(status().isCreated());
        mvc.perform(post("/api/v1/paiements").with(lui).contentType(MediaType.APPLICATION_JSON)
                .content(json("bailId", bailB.toString(), "montant", 50_000, "mode", "ESPECES")))
                .andExpect(status().isNotFound());

        // l'historique ne montre que son périmètre, et un bail extérieur est introuvable
        assertThat(ids(mvc.perform(get("/api/v1/paiements").param("size", "100").with(lui)))).hasSize(1);
        mvc.perform(get("/api/v1/paiements").param("bailId", bailB.toString()).with(lui)).andExpect(status().isNotFound());
    }

    @Test
    void la_cle_d_idempotence_d_un_autre_perimetre_ne_rejoue_rien() throws Exception {
        String cle = "perimetre-" + UUID.randomUUID();
        mvc.perform(post("/api/v1/paiements").with(ADMIN).header("Idempotency-Key", cle).contentType(MediaType.APPLICATION_JSON)
                .content(json("bailId", bailB.toString(), "montant", 20_000, "mode", "ESPECES")))
                .andExpect(status().isCreated());
        mvc.perform(post("/api/v1/paiements").with(lui).header("Idempotency-Key", cle).contentType(MediaType.APPLICATION_JSON)
                .content(json("bailId", bailB.toString(), "montant", 20_000, "mode", "ESPECES")))
                .andExpect(status().isNotFound());
    }

    @Test
    void la_caisse_ne_montre_que_ses_propres_especes() throws Exception {
        mvc.perform(post("/api/v1/paiements").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("bailId", bailA.toString(), "montant", 11_000, "mode", "ESPECES"))).andExpect(status().isCreated());
        mvc.perform(post("/api/v1/paiements").with(lui).contentType(MediaType.APPLICATION_JSON)
                .content(json("bailId", bailA.toString(), "montant", 7_000, "mode", "ESPECES"))).andExpect(status().isCreated());

        JsonNode lignes = mapper.readTree(mvc.perform(get("/api/v1/paiements/caisse").with(lui)).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString());
        assertThat(lignes).hasSize(1);
        assertThat(lignes.get(0).path("agentId").asText()).isEqualTo(employe.toString());
        assertThat(lignes.get(0).path("total").asLong()).isEqualTo(7_000);
    }

    @Test
    void echeances_et_compte_locataire_restent_dans_le_perimetre() throws Exception {
        mvc.perform(get("/api/v1/echeances").param("bailId", bailA.toString()).with(lui)).andExpect(status().isOk());
        mvc.perform(get("/api/v1/echeances").param("bailId", bailB.toString()).with(lui)).andExpect(status().isNotFound());
        mvc.perform(get("/api/v1/echeances").with(lui)).andExpect(status().isOk()).andExpect(jsonPath("$.content").isEmpty());

        mvc.perform(get("/api/v1/locataires/{id}/compte", locataireA).with(lui)).andExpect(status().isOk());
        mvc.perform(get("/api/v1/locataires/{id}/compte", locataireB).with(lui)).andExpect(status().isNotFound());
        mvc.perform(get("/api/v1/documents-paiement").param("bailId", bailB.toString()).with(lui)).andExpect(status().isNotFound());
    }

    // ------------------------------------------------------------------ refus par défaut

    @Test
    void sans_affectation_il_ne_voit_rien_et_n_encaisse_nulle_part() throws Exception {
        UUID sansRien = nouvelEmploye();
        RequestPostProcessor lui2 = jwtRole(Role.ENCAISSEUR, sansRien);
        assertThat(ids(mvc.perform(get("/api/v1/immeubles").with(lui2)))).isEmpty();
        assertThat(ids(mvc.perform(get("/api/v1/lots").with(lui2)))).isEmpty();
        assertThat(ids(mvc.perform(get("/api/v1/baux").with(lui2)))).isEmpty();
        assertThat(ids(mvc.perform(get("/api/v1/locataires").with(lui2)))).isEmpty();
        assertThat(ids(mvc.perform(get("/api/v1/paiements").with(lui2)))).isEmpty();
        mvc.perform(post("/api/v1/paiements").with(lui2).contentType(MediaType.APPLICATION_JSON)
                .content(json("bailId", bailA.toString(), "montant", 1_000, "mode", "ESPECES"))).andExpect(status().isNotFound());
    }

    @Test
    void retirer_une_affectation_agit_tout_de_suite() throws Exception {
        assertThat(ids(mvc.perform(get("/api/v1/immeubles").with(lui)))).containsExactly(immeubleA);
        mvc.perform(put("/api/v1/utilisateurs/{id}/affectations", employe).with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("immeubleIds", List.of()))).andExpect(status().isOk());
        assertThat(ids(mvc.perform(get("/api/v1/immeubles").with(lui)))).isEmpty();
        mvc.perform(get("/api/v1/baux/{id}", bailA).with(lui)).andExpect(status().isNotFound());
    }

    @Test
    void plusieurs_immeubles_sont_visibles_ensemble() throws Exception {
        affecter(employe, immeubleA, immeubleB);
        assertThat(ids(mvc.perform(get("/api/v1/immeubles").param("size", "100").with(lui)))).containsExactlyInAnyOrder(immeubleA, immeubleB);
        assertThat(ids(mvc.perform(get("/api/v1/baux").param("size", "100").with(lui)))).containsExactlyInAnyOrder(bailA, bailB);
    }

    @Test
    void les_routes_non_ouvertes_sont_refusees() throws Exception {
        UUID unPaiement = UUID.randomUUID();
        mvc.perform(post("/api/v1/immeubles").with(lui).contentType(MediaType.APPLICATION_JSON)
                .content(json("nom", "X", "adresse", "x", "ville", "x"))).andExpect(status().isForbidden());
        // corps valide : la validation du corps précède le contrôle d'accès de la méthode (un corps vide répondrait 400)
        mvc.perform(post("/api/v1/baux").with(lui).contentType(MediaType.APPLICATION_JSON)
                .content(json("lotId", lotA.toString(), "locataireId", locataireA.toString(), "dateDebut", "2026-01-01", "loyer", 1, "charges", 0,
                        "periodicite", "MENSUELLE", "jourEcheance", 1, "moisAvance", 0))).andExpect(status().isForbidden());
        mvc.perform(post("/api/v1/baux/{id}/activer", bailA).with(lui)).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/baux/{id}/documents", bailA).with(lui)).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/baux/{id}/apercu", bailA).with(lui)).andExpect(status().isForbidden());
        mvc.perform(post("/api/v1/paiements/{id}/contre-passation", unPaiement).with(lui).contentType(MediaType.APPLICATION_JSON)
                .content(json("motif", "erreur"))).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/locataires/{id}", locataireA).with(lui)).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/locataires/{id}/pieces", locataireA).with(lui)).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/reversements").with(lui)).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/depenses").with(lui)).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/utilisateurs").with(lui)).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/utilisateurs/{id}/affectations", employe).with(lui)).andExpect(status().isForbidden());
        mvc.perform(put("/api/v1/utilisateurs/{id}/affectations", employe).with(lui).contentType(MediaType.APPLICATION_JSON)
                .content(json("immeubleIds", List.of(immeubleB)))).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/tableau-de-bord/synthese").with(lui)).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/signalements").with(lui)).andExpect(status().isForbidden());
    }

    // ------------------------------------------------------------------ gestion des employés (ADMIN)

    @Test
    void un_encaisseur_exige_la_double_authentification_et_un_telephone() throws Exception {
        mvc.perform(post("/api/v1/utilisateurs").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("nom", "Sans 2FA", "telephone", tel(), "role", "ENCAISSEUR", "deuxFacteursActif", false,
                        "motDePasse", SecuriteTestSupport.MOT_DE_PASSE)))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("UTILISATEUR_2FA_OBLIGATOIRE"));
        mvc.perform(post("/api/v1/utilisateurs").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("nom", "Sans tel", "email", "sans.tel." + System.nanoTime() + "@x.cm", "role", "ENCAISSEUR", "deuxFacteursActif", true,
                        "motDePasse", SecuriteTestSupport.MOT_DE_PASSE)))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("UTILISATEUR_TELEPHONE_REQUIS_2FA"));
    }

    @Test
    void les_affectations_ne_concernent_que_les_employes_restreints() throws Exception {
        UUID gestionnaire = utilisateurs.creer(new UtilisateurService.Saisie("Gestionnaire " + System.nanoTime(), tel(), null, Role.GESTIONNAIRE, true, false),
                SecuriteTestSupport.MOT_DE_PASSE).id();
        mvc.perform(put("/api/v1/utilisateurs/{id}/affectations", gestionnaire).with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("immeubleIds", List.of(immeubleA)))).andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("AFFECTATION_ROLE_NON_CONCERNE"));
        mvc.perform(get("/api/v1/utilisateurs/{id}/affectations", employe).with(ADMIN)).andExpect(status().isOk())
                .andExpect(jsonPath("$.immeubleIds[0]").value(immeubleA.toString()));
    }

    @Test
    void la_liste_des_comptes_se_filtre_par_role() throws Exception {
        JsonNode contenu = mapper.readTree(mvc.perform(get("/api/v1/utilisateurs").param("roles", "ENCAISSEUR").param("size", "100").with(ADMIN))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString()).path("content");
        List<String> roles = new ArrayList<>();
        List<String> idsTrouves = new ArrayList<>();
        contenu.forEach(n -> {
            roles.add(n.path("role").asText());
            idsTrouves.add(n.path("id").asText());
        });
        assertThat(roles).isNotEmpty().containsOnly("ENCAISSEUR");
        assertThat(idsTrouves).contains(employe.toString());
        // plusieurs rôles, et sans filtre : tout
        JsonNode deux = mapper.readTree(mvc.perform(get("/api/v1/utilisateurs").param("roles", "ENCAISSEUR", "GESTIONNAIRE").param("size", "100").with(ADMIN))
                .andReturn().getResponse().getContentAsString()).path("content");
        deux.forEach(n -> assertThat(n.path("role").asText()).isIn("ENCAISSEUR", "GESTIONNAIRE"));
    }

    @Test
    void admin_et_gestionnaire_gardent_une_vue_complete() throws Exception {
        for (Role role : List.of(Role.ADMIN, Role.GESTIONNAIRE)) {
            RequestPostProcessor acteur = jwtRole(role, UUID.randomUUID());
            assertThat(ids(mvc.perform(get("/api/v1/immeubles").param("size", "100").with(acteur)))).contains(immeubleA, immeubleB);
            assertThat(ids(mvc.perform(get("/api/v1/baux").param("size", "100").with(acteur)))).contains(bailA, bailB);
            assertThat(ids(mvc.perform(get("/api/v1/locataires").param("size", "100").with(acteur)))).contains(locataireA, locataireB);
        }
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
        return utilisateurs.creer(new UtilisateurService.Saisie("Employé " + System.nanoTime(), tel(), null, Role.ENCAISSEUR, true, true),
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
