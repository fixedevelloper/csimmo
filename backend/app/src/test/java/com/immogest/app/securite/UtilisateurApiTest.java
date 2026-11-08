package com.immogest.app.securite;

import static com.immogest.app.securite.SecuriteTestSupport.MOT_DE_PASSE;
import static com.immogest.app.securite.SecuriteTestSupport.connecter;
import static com.immogest.app.securite.SecuriteTestSupport.emailUnique;
import static com.immogest.app.securite.SecuriteTestSupport.json;
import static com.immogest.app.securite.SecuriteTestSupport.jwtRole;
import static com.immogest.app.securite.SecuriteTestSupport.telephoneUnique;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.immogest.app.IntegrationTest;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

@IntegrationTest
@Import(SecuriteTestSupport.class)
class UtilisateurApiTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    UtilisateurService service;
    @Autowired
    JdbcTemplate jdbc;

    private final RequestPostProcessor admin = jwtRole(Role.ADMIN);

    private String creation(String telephone, String email, Role role) {
        return json("nom", "Nouvel utilisateur", "telephone", telephone, "email", email, "role", role.name(),
                "deuxFacteursActif", false, "motDePasse", MOT_DE_PASSE);
    }

    private JsonNode creerViaApi(Role role) throws Exception {
        String reponse = mvc.perform(post("/api/v1/utilisateurs").with(admin).contentType(MediaType.APPLICATION_JSON)
                        .content(creation(telephoneUnique(), emailUnique(), role)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return SecuriteTestSupport.JSON.readTree(reponse);
    }

    // ------------------------------------------------------------------ ADMIN

    @Test
    void le_mot_de_passe_d_un_compte_a_au_moins_12_caracteres() throws Exception {
        for (String court : new String[] {"Court1234!", "Onze-chars1"}) { // 10 et 11 caractères
            mvc.perform(post("/api/v1/utilisateurs").with(jwtRole(Role.ADMIN)).contentType(MediaType.APPLICATION_JSON)
                    .content(json("nom", "Trop court", "telephone", telephoneUnique(), "role", "LOCATAIRE", "motDePasse", court)))
                    .andExpect(status().isBadRequest());
        }
        mvc.perform(post("/api/v1/utilisateurs").with(jwtRole(Role.ADMIN)).contentType(MediaType.APPLICATION_JSON)
                .content(json("nom", "Assez long", "telephone", telephoneUnique(), "role", "LOCATAIRE", "motDePasse", "Douze-chars12")))
                .andExpect(status().isCreated());
    }

    @Test
    void adminCreeUnUtilisateurQuiPeutSeConnecter() throws Exception {
        String telephone = telephoneUnique();

        mvc.perform(post("/api/v1/utilisateurs").with(admin).contentType(MediaType.APPLICATION_JSON)
                        .content(creation(telephone, null, Role.LOCATAIRE)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.role").value("LOCATAIRE"))
                .andExpect(jsonPath("$.actif").value(true))
                .andExpect(jsonPath("$.motDePasse").doesNotExist())
                .andExpect(jsonPath("$.motDePasseHash").doesNotExist());

        assertThat(connecter(mvc, telephone, MOT_DE_PASSE).path("role").asText()).isEqualTo("LOCATAIRE");
    }

    @Test
    void adminAvecUnVraiJetonAccedeALaGestionDesUtilisateurs() throws Exception {
        UtilisateurDto a = service.creer(new UtilisateurService.Saisie("Vrai admin", telephoneUnique(), null,
                Role.ADMIN, true, false), MOT_DE_PASSE);
        String acces = connecter(mvc, a.telephone(), MOT_DE_PASSE).path("accessToken").asText();

        mvc.perform(get("/api/v1/utilisateurs").header("Authorization", "Bearer " + acces))
                .andExpect(status().isOk());
    }

    @Test
    void listePaginee() throws Exception {
        creerViaApi(Role.LOCATAIRE);
        creerViaApi(Role.LOCATAIRE);

        mvc.perform(get("/api/v1/utilisateurs").with(admin).param("page", "0").param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(1))
                .andExpect(jsonPath("$.totalElements").value(org.hamcrest.Matchers.greaterThanOrEqualTo(2)));
    }

    @Test
    void lectureEtModification() throws Exception {
        JsonNode cree = creerViaApi(Role.LOCATAIRE);
        String id = cree.path("id").asText();

        mvc.perform(get("/api/v1/utilisateurs/{id}", id).with(admin))
                .andExpect(status().isOk()).andExpect(jsonPath("$.nom").value("Nouvel utilisateur"));

        mvc.perform(put("/api/v1/utilisateurs/{id}", id).with(admin).contentType(MediaType.APPLICATION_JSON)
                        .content(json("nom", "Nom modifié", "telephone", cree.path("telephone").asText(),
                                "email", cree.path("email").asText(), "role", "PRESTATAIRE", "actif", true,
                                "deuxFacteursActif", false)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Nom modifié"))
                .andExpect(jsonPath("$.role").value("PRESTATAIRE"));

        mvc.perform(get("/api/v1/utilisateurs/{id}", UUID.randomUUID()).with(admin))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("UTILISATEUR_INTROUVABLE"));
    }

    @Test
    void suppressionDesactiveLeCompteEtCoupeLesSessions() throws Exception {
        UtilisateurDto u = service.creer(new UtilisateurService.Saisie("À désactiver", telephoneUnique(), null,
                Role.GESTIONNAIRE, true, false), MOT_DE_PASSE);
        String refresh = connecter(mvc, u.telephone(), MOT_DE_PASSE).path("refreshToken").asText();

        mvc.perform(delete("/api/v1/utilisateurs/{id}", u.id()).with(admin)).andExpect(status().isNoContent());

        mvc.perform(get("/api/v1/utilisateurs/{id}", u.id()).with(admin))
                .andExpect(jsonPath("$.actif").value(false));
        mvc.perform(post("/api/v1/auth/refresh").contentType(MediaType.APPLICATION_JSON)
                        .content(json("refreshToken", refresh)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminNePeutNiSeDesactiverNiSeRetrograder() throws Exception {
        UtilisateurDto a = service.creer(new UtilisateurService.Saisie("Moi", telephoneUnique(), null, Role.ADMIN,
                true, false), MOT_DE_PASSE);

        mvc.perform(delete("/api/v1/utilisateurs/{id}", a.id()).with(jwtRole(Role.ADMIN, a.id())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("UTILISATEUR_AUTO_DESACTIVATION"));

        mvc.perform(put("/api/v1/utilisateurs/{id}", a.id()).with(jwtRole(Role.ADMIN, a.id()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("nom", "Moi", "telephone", a.telephone(), "role", "GESTIONNAIRE", "actif", true,
                                "deuxFacteursActif", false)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("UTILISATEUR_AUTO_RETRAIT"));
    }

    @Test
    void reinitialisationDuMotDePasseEtDeverrouillage() throws Exception {
        UtilisateurDto u = service.creer(new UtilisateurService.Saisie("Bloqué", telephoneUnique(), null,
                Role.GESTIONNAIRE, true, false), MOT_DE_PASSE);
        for (int i = 0; i < 5; i++) {
            mvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                    .content(json("identifiant", u.telephone(), "motDePasse", "faux")));
        }
        mvc.perform(get("/api/v1/utilisateurs/{id}", u.id()).with(admin)).andExpect(jsonPath("$.verrouille").value(true));

        mvc.perform(post("/api/v1/utilisateurs/{id}/mot-de-passe", u.id()).with(admin)
                        .contentType(MediaType.APPLICATION_JSON).content(json("motDePasse", "Reinit-789-xyz")))
                .andExpect(status().isNoContent());

        mvc.perform(get("/api/v1/utilisateurs/{id}", u.id()).with(admin)).andExpect(jsonPath("$.verrouille").value(false));
        assertThat(connecter(mvc, u.telephone(), "Reinit-789-xyz").path("accessToken").asText()).isNotBlank();

        mvc.perform(post("/api/v1/utilisateurs/{id}/deverrouiller", u.id()).with(admin)).andExpect(status().isNoContent());
    }

    // ------------------------------------------------------------------ validations

    @Test
    void doublonTelephoneOuEmailRefuse() throws Exception {
        JsonNode existant = creerViaApi(Role.LOCATAIRE);

        for (String corps : new String[] {
                creation(existant.path("telephone").asText(), null, Role.LOCATAIRE),
                creation(telephoneUnique(), existant.path("email").asText().toUpperCase(), Role.LOCATAIRE)}) {
            mvc.perform(post("/api/v1/utilisateurs").with(admin).contentType(MediaType.APPLICATION_JSON).content(corps))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.code").value("UTILISATEUR_DEJA_EXISTANT"));
        }
    }

    @Test
    void donneesInvalidesRefusees() throws Exception {
        List<String> corpsInvalides = List.of(
                json("telephone", telephoneUnique(), "role", "LOCATAIRE", "motDePasse", MOT_DE_PASSE),
                json("nom", "X", "role", "LOCATAIRE", "motDePasse", MOT_DE_PASSE),
                json("nom", "X", "telephone", telephoneUnique(), "role", "LOCATAIRE", "motDePasse", "court"),
                json("nom", "X", "telephone", telephoneUnique(), "role", "INCONNU", "motDePasse", MOT_DE_PASSE));
        for (String corps : corpsInvalides) {
            mvc.perform(post("/api/v1/utilisateurs").with(admin).contentType(MediaType.APPLICATION_JSON).content(corps))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    void telephoneInvalideEtContactObligatoire() throws Exception {
        mvc.perform(post("/api/v1/utilisateurs").with(admin).contentType(MediaType.APPLICATION_JSON)
                        .content(creation("abc", null, Role.LOCATAIRE)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("UTILISATEUR_TELEPHONE_INVALIDE"));

        // la double authentification passe par une application : plus besoin de téléphone pour recevoir un code
        mvc.perform(post("/api/v1/utilisateurs").with(admin).contentType(MediaType.APPLICATION_JSON)
                        .content(json("nom", "X", "email", emailUnique(), "role", "GESTIONNAIRE",
                                "deuxFacteursActif", true, "motDePasse", MOT_DE_PASSE)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.deuxFacteursActif").value(true))
                .andExpect(jsonPath("$.deuxFacteursConfigure").value(false));

        mvc.perform(post("/api/v1/utilisateurs").with(admin).contentType(MediaType.APPLICATION_JSON)
                        .content(json("nom", "X", "role", "LOCATAIRE", "motDePasse", MOT_DE_PASSE)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("UTILISATEUR_CONTACT_REQUIS"));
    }

    // ------------------------------------------------------------------ accès interdit

    @Test
    void nonAdminRecoit403SurTousLesEndpoints() throws Exception {
        UUID id = UUID.randomUUID();
        for (Role role : new Role[] {Role.GESTIONNAIRE, Role.PROPRIETAIRE_TIERS, Role.LOCATAIRE, Role.PRESTATAIRE}) {
            RequestPostProcessor auth = jwtRole(role);
            List<ResultActions> appels = List.of(
                    mvc.perform(get("/api/v1/utilisateurs").with(auth)),
                    mvc.perform(get("/api/v1/utilisateurs/{id}", id).with(auth)),
                    mvc.perform(post("/api/v1/utilisateurs").with(auth).contentType(MediaType.APPLICATION_JSON)
                            .content(creation(telephoneUnique(), null, Role.LOCATAIRE))),
                    mvc.perform(put("/api/v1/utilisateurs/{id}", id).with(auth).contentType(MediaType.APPLICATION_JSON)
                            .content("{}")),
                    mvc.perform(delete("/api/v1/utilisateurs/{id}", id).with(auth)),
                    mvc.perform(post("/api/v1/utilisateurs/{id}/deverrouiller", id).with(auth)),
                    mvc.perform(post("/api/v1/utilisateurs/{id}/mot-de-passe", id).with(auth)
                            .contentType(MediaType.APPLICATION_JSON).content(json("motDePasse", MOT_DE_PASSE))));
            for (ResultActions appel : appels) {
                appel.andExpect(status().isForbidden()).andExpect(jsonPath("$.code").value("ACCES_REFUSE"));
            }
        }
    }

    @Test
    void sansAuthentification401() throws Exception {
        mvc.perform(get("/api/v1/utilisateurs"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("NON_AUTHENTIFIE"));
        mvc.perform(post("/api/v1/utilisateurs").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void aucuneCreationNEstPossibleParUnNonAdminMemeAvecUnVraiJeton() throws Exception {
        UtilisateurDto g = service.creer(new UtilisateurService.Saisie("Gest", telephoneUnique(), null,
                Role.GESTIONNAIRE, true, false), MOT_DE_PASSE);
        String acces = connecter(mvc, g.telephone(), MOT_DE_PASSE).path("accessToken").asText();

        mvc.perform(post("/api/v1/utilisateurs").header("Authorization", "Bearer " + acces)
                        .contentType(MediaType.APPLICATION_JSON).content(creation(telephoneUnique(), null, Role.ADMIN)))
                .andExpect(status().isForbidden());
    }

    // ------------------------------------------------------------------ audit

    @Test
    void creationEtModificationSontJournaliseesSansSecret() throws Exception {
        JsonNode cree = creerViaApi(Role.LOCATAIRE);
        UUID id = UUID.fromString(cree.path("id").asText());

        await().atMost(Duration.ofSeconds(15)).untilAsserted(() -> {
            List<Map<String, Object>> lignes = jdbc.queryForList(
                    "SELECT action, apres::text AS apres FROM audit_log WHERE entite = 'utilisateur' AND entite_id = ?", id);
            assertThat(lignes).hasSize(1);
            assertThat(lignes.get(0)).containsEntry("action", "SECURITE.UTILISATEUR_CREE");
            assertThat((String) lignes.get(0).get("apres"))
                    .contains("LOCATAIRE")
                    .doesNotContainIgnoringCase("motdepasse")
                    .doesNotContainIgnoringCase("hash")
                    .doesNotContain(MOT_DE_PASSE);
        });
    }
}
