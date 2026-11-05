package com.immogest.app.securite;

import static com.immogest.app.securite.SecuriteTestSupport.MOT_DE_PASSE;
import static com.immogest.app.securite.SecuriteTestSupport.connecter;
import static com.immogest.app.securite.SecuriteTestSupport.emailUnique;
import static com.immogest.app.securite.SecuriteTestSupport.json;
import static com.immogest.app.securite.SecuriteTestSupport.postJson;
import static com.immogest.app.securite.SecuriteTestSupport.telephoneUnique;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.immogest.app.IntegrationTest;
import com.immogest.app.securite.infra.JwtService;
import com.immogest.app.securite.infra.UtilisateurRepository;
import java.time.Instant;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

@IntegrationTest
@Import(SecuriteTestSupport.class)
class AuthApiTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    UtilisateurService service;
    @Autowired
    UtilisateurRepository repository;
    @Autowired
    JwtService jwtService;
    @Autowired
    JdbcTemplate jdbc;

    private UtilisateurDto creer(Role role, String telephone, String email) {
        return service.creer(new UtilisateurService.Saisie("Test " + role, telephone, email, role, true, false), MOT_DE_PASSE);
    }

    private UtilisateurDto creer(Role role) {
        return creer(role, telephoneUnique(), emailUnique());
    }

    private SecuriteTestSupport.ResultPost login(String identifiant, String motDePasse) throws Exception {
        return postJson(mvc, "/api/v1/auth/login", json("identifiant", identifiant, "motDePasse", motDePasse));
    }

    // ------------------------------------------------------------------ login

    @Test
    void loginParTelephoneEtParEmail() throws Exception {
        UtilisateurDto u = creer(Role.GESTIONNAIRE);

        for (String identifiant : new String[] {u.telephone(), u.email(), u.email().toUpperCase()}) {
            var r = login(identifiant, MOT_DE_PASSE);
            assertThat(r.statut()).isEqualTo(200);
            assertThat(r.corps().path("accessToken").asText()).isNotBlank();
            assertThat(r.corps().path("refreshToken").asText()).isNotBlank();
            assertThat(r.corps().path("role").asText()).isEqualTo("GESTIONNAIRE");
            assertThat(r.corps().path("expiresIn").asLong()).isEqualTo(900);
        }
    }

    @Test
    void telephoneSaisiAvecEspacesEstNormalise() throws Exception {
        String numero = telephoneUnique();
        String avecEspaces = numero.substring(0, 4) + " " + numero.substring(4, 6) + " " + numero.substring(6);
        creer(Role.LOCATAIRE, avecEspaces, null);

        assertThat(login(numero, MOT_DE_PASSE).statut()).isEqualTo(200);
        assertThat(login(avecEspaces, MOT_DE_PASSE).statut()).isEqualTo(200);
    }

    @Test
    void motDePasseIncorrectEtIdentifiantInconnuDonnentLaMemeReponse() throws Exception {
        UtilisateurDto u = creer(Role.GESTIONNAIRE);

        var mauvais = login(u.telephone(), "mauvais-mot-de-passe");
        var inconnu = login(telephoneUnique(), MOT_DE_PASSE);

        assertThat(mauvais.statut()).isEqualTo(401);
        assertThat(inconnu.statut()).isEqualTo(401);
        assertThat(mauvais.code()).isEqualTo("IDENTIFIANTS_INVALIDES").isEqualTo(inconnu.code());
        assertThat(mauvais.corps().path("detail")).isEqualTo(inconnu.corps().path("detail"));
    }

    @Test
    void corpsInvalideRenvoie400() throws Exception {
        var r = postJson(mvc, "/api/v1/auth/login", "{}");
        assertThat(r.statut()).isEqualTo(400);
        assertThat(r.code()).isEqualTo("VALIDATION_ERREUR");
    }

    @Test
    void compteVerrouilleApresCinqEchecsPuisDeverrouille() throws Exception {
        UtilisateurDto u = creer(Role.GESTIONNAIRE);

        for (int i = 1; i <= 4; i++) {
            assertThat(login(u.telephone(), "faux").statut()).as("échec %d", i).isEqualTo(401);
        }
        var cinquieme = login(u.telephone(), "faux");
        assertThat(cinquieme.statut()).isEqualTo(423);
        assertThat(cinquieme.code()).isEqualTo("COMPTE_VERROUILLE");

        // Même avec le bon mot de passe, le compte reste verrouillé
        assertThat(login(u.telephone(), MOT_DE_PASSE).statut()).isEqualTo(423);

        // Le verrouillage expire
        var entite = repository.findById(u.id()).orElseThrow();
        entite.setVerrouilleJusqua(OffsetDateTime.now().minusMinutes(1));
        repository.save(entite);
        assertThat(login(u.telephone(), MOT_DE_PASSE).statut()).isEqualTo(200);
    }

    @Test
    void unEchecSuivitDUnSuccesRemetLeCompteur() throws Exception {
        UtilisateurDto u = creer(Role.GESTIONNAIRE);
        for (int i = 0; i < 4; i++) {
            login(u.telephone(), "faux");
        }
        assertThat(login(u.telephone(), MOT_DE_PASSE).statut()).isEqualTo(200);
        for (int i = 0; i < 4; i++) {
            assertThat(login(u.telephone(), "faux").statut()).isEqualTo(401);
        }
    }

    @Test
    void compteDesactiveRefuse() throws Exception {
        UtilisateurDto u = creer(Role.GESTIONNAIRE);
        service.desactiver(u.id());

        var r = login(u.telephone(), MOT_DE_PASSE);
        assertThat(r.statut()).isEqualTo(403);
        assertThat(r.code()).isEqualTo("COMPTE_DESACTIVE");
    }

    // ------------------------------------------------------------------ jeton d'accès

    @Test
    void jetonValideDonneAccesEtSansJetonRefuse() throws Exception {
        UtilisateurDto u = creer(Role.GESTIONNAIRE);
        String acces = connecter(mvc, u.telephone(), MOT_DE_PASSE).path("accessToken").asText();

        mvc.perform(get("/api/v1/auth/moi").header("Authorization", "Bearer " + acces))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(u.id().toString()))
                .andExpect(jsonPath("$.role").value("GESTIONNAIRE"))
                .andExpect(jsonPath("$.motDePasseHash").doesNotExist());

        mvc.perform(get("/api/v1/auth/moi"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("NON_AUTHENTIFIE"));
    }

    @Test
    void jetonIllisibleOuAltereRefuse() throws Exception {
        UtilisateurDto u = creer(Role.GESTIONNAIRE);
        String acces = connecter(mvc, u.telephone(), MOT_DE_PASSE).path("accessToken").asText();
        String altere = acces.substring(0, acces.length() - 3) + (acces.endsWith("AAA") ? "BBB" : "AAA");

        for (String jeton : new String[] {"n-importe-quoi", altere}) {
            mvc.perform(get("/api/v1/auth/moi").header("Authorization", "Bearer " + jeton))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value("NON_AUTHENTIFIE"));
        }
    }

    @Test
    void jetonExpireRenvoieTokenExpire() throws Exception {
        UtilisateurDto u = creer(Role.GESTIONNAIRE);
        String expire = jwtService.creerAcces(u.id(), Role.GESTIONNAIRE, Instant.now().minusSeconds(3600));

        mvc.perform(get("/api/v1/auth/moi").header("Authorization", "Bearer " + expire))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("TOKEN_EXPIRE"));
    }

    // ------------------------------------------------------------------ refresh / logout

    @Test
    void refreshFaitTournerLeJeton() throws Exception {
        UtilisateurDto u = creer(Role.GESTIONNAIRE);
        JsonNode session = connecter(mvc, u.telephone(), MOT_DE_PASSE);
        String refresh1 = session.path("refreshToken").asText();

        var r = postJson(mvc, "/api/v1/auth/refresh", json("refreshToken", refresh1));
        assertThat(r.statut()).isEqualTo(200);
        String refresh2 = r.corps().path("refreshToken").asText();
        assertThat(refresh2).isNotEqualTo(refresh1);
        assertThat(r.corps().path("accessToken").asText()).isNotBlank();
    }

    @Test
    void reutilisationDUnAncienRefreshRevoqueToutesLesSessions() throws Exception {
        UtilisateurDto u = creer(Role.GESTIONNAIRE);
        String refresh1 = connecter(mvc, u.telephone(), MOT_DE_PASSE).path("refreshToken").asText();
        String refresh2 = postJson(mvc, "/api/v1/auth/refresh", json("refreshToken", refresh1))
                .corps().path("refreshToken").asText();

        var rejeu = postJson(mvc, "/api/v1/auth/refresh", json("refreshToken", refresh1));
        assertThat(rejeu.statut()).isEqualTo(401);
        assertThat(rejeu.code()).isEqualTo("REFRESH_TOKEN_REUTILISE");

        // Le jeton légitime issu de la rotation est révoqué lui aussi
        assertThat(postJson(mvc, "/api/v1/auth/refresh", json("refreshToken", refresh2)).statut()).isEqualTo(401);
    }

    @Test
    void refreshInconnuOuExpireRefuse() throws Exception {
        var inconnu = postJson(mvc, "/api/v1/auth/refresh", json("refreshToken", "inconnu"));
        assertThat(inconnu.statut()).isEqualTo(401);
        assertThat(inconnu.code()).isEqualTo("REFRESH_TOKEN_INVALIDE");

        UtilisateurDto u = creer(Role.GESTIONNAIRE);
        String refresh = connecter(mvc, u.telephone(), MOT_DE_PASSE).path("refreshToken").asText();
        jdbc.update("UPDATE sec_refresh_token SET expire_le = now() - interval '1 day' WHERE utilisateur_id = ?", u.id());

        var expire = postJson(mvc, "/api/v1/auth/refresh", json("refreshToken", refresh));
        assertThat(expire.statut()).isEqualTo(401);
        assertThat(expire.code()).isEqualTo("REFRESH_TOKEN_EXPIRE");
    }

    @Test
    void refreshRefuseSiLeCompteEstDesactive() throws Exception {
        UtilisateurDto u = creer(Role.GESTIONNAIRE);
        String refresh = connecter(mvc, u.telephone(), MOT_DE_PASSE).path("refreshToken").asText();

        service.desactiver(u.id());

        assertThat(postJson(mvc, "/api/v1/auth/refresh", json("refreshToken", refresh)).statut()).isEqualTo(401);
    }

    @Test
    void logoutRevoqueLeRefresh() throws Exception {
        UtilisateurDto u = creer(Role.GESTIONNAIRE);
        String refresh = connecter(mvc, u.telephone(), MOT_DE_PASSE).path("refreshToken").asText();

        mvc.perform(post("/api/v1/auth/logout").contentType(MediaType.APPLICATION_JSON)
                .content(json("refreshToken", refresh))).andExpect(status().isNoContent());

        assertThat(postJson(mvc, "/api/v1/auth/refresh", json("refreshToken", refresh)).statut()).isEqualTo(401);
    }

    // ------------------------------------------------------------------ mot de passe

    @Test
    void changementDeMotDePasseRevoqueLesSessions() throws Exception {
        UtilisateurDto u = creer(Role.GESTIONNAIRE);
        JsonNode session = connecter(mvc, u.telephone(), MOT_DE_PASSE);
        String acces = session.path("accessToken").asText();

        mvc.perform(post("/api/v1/auth/mot-de-passe").header("Authorization", "Bearer " + acces)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("ancienMotDePasse", MOT_DE_PASSE, "nouveauMotDePasse", "Nouveau-mdp-456")))
                .andExpect(status().isNoContent());

        assertThat(login(u.telephone(), MOT_DE_PASSE).statut()).isEqualTo(401);
        assertThat(login(u.telephone(), "Nouveau-mdp-456").statut()).isEqualTo(200);
        assertThat(postJson(mvc, "/api/v1/auth/refresh", json("refreshToken", session.path("refreshToken").asText()))
                .statut()).isEqualTo(401);
    }

    @Test
    void changementRefuseSiAncienMotDePasseIncorrectOuNouveauTropCourt() throws Exception {
        UtilisateurDto u = creer(Role.GESTIONNAIRE);
        String acces = connecter(mvc, u.telephone(), MOT_DE_PASSE).path("accessToken").asText();

        mvc.perform(post("/api/v1/auth/mot-de-passe").header("Authorization", "Bearer " + acces)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("ancienMotDePasse", "faux", "nouveauMotDePasse", "Nouveau-mdp-456")))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("MOT_DE_PASSE_ACTUEL_INCORRECT"));

        mvc.perform(post("/api/v1/auth/mot-de-passe").header("Authorization", "Bearer " + acces)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("ancienMotDePasse", MOT_DE_PASSE, "nouveauMotDePasse", "court")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERREUR"));
    }

    @Test
    void changementDeMotDePasseExigeUneAuthentification() throws Exception {
        mvc.perform(post("/api/v1/auth/mot-de-passe").contentType(MediaType.APPLICATION_JSON)
                        .content(json("ancienMotDePasse", "a", "nouveauMotDePasse", "Nouveau-mdp-456")))
                .andExpect(status().isUnauthorized());
    }
}
