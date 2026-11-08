package com.immogest.app.securite;

import static com.immogest.app.securite.SecuriteTestSupport.MOT_DE_PASSE;
import static com.immogest.app.securite.SecuriteTestSupport.json;
import static com.immogest.app.securite.SecuriteTestSupport.jwtRole;
import static com.immogest.app.securite.SecuriteTestSupport.postJson;
import static com.immogest.app.securite.SecuriteTestSupport.telephoneUnique;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.immogest.app.IntegrationTest;
import com.immogest.app.securite.SecuriteTestSupport.ResultPost;
import com.immogest.app.securite.infra.Totp;
import com.immogest.app.securite.infra.TotpTestAcces;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

/**
 * Double authentification par application (TOTP) : défi après le mot de passe, enrôlement à la première connexion, code à usage unique,
 * verrouillage, réinitialisation par l'ADMIN, activation et désactivation depuis le profil.
 */
@IntegrationTest
@Import(SecuriteTestSupport.class)
class DeuxFacteursApiTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    UtilisateurService service;
    @Autowired
    SecuriteTestSupport.OtpCapture sms;
    @Autowired
    JdbcTemplate jdbc;

    private UtilisateurDto creer(Role role, boolean deuxFacteurs) {
        return service.creer(new UtilisateurService.Saisie("Compte " + role, telephoneUnique(), null, role, true, deuxFacteurs), MOT_DE_PASSE);
    }

    private ResultPost login(UtilisateurDto u) throws Exception {
        return postJson(mvc, "/api/v1/auth/login", json("identifiant", u.telephone(), "motDePasse", MOT_DE_PASSE));
    }

    private ResultPost verifier(String defi, String code) throws Exception {
        return postJson(mvc, "/api/v1/auth/deux-facteurs/verifier", json("defi", defi, "code", code));
    }

    private JsonNode demarrer(String defi) throws Exception {
        ResultPost r = postJson(mvc, "/api/v1/auth/deux-facteurs/enrolement/demarrer", json("defi", defi));
        assertThat(r.statut()).isEqualTo(200);
        return r.corps();
    }

    private ResultPost confirmer(String defi, String code) throws Exception {
        return postJson(mvc, "/api/v1/auth/deux-facteurs/enrolement/confirmer", json("defi", defi, "code", code));
    }

    /** Code que afficherait l'application à {@code decalage} pas (de 30 s) du pas courant. */
    private static String code(String secret, int decalage) {
        return TotpTestAcces.code(secret, Totp.pas(Instant.now()) + decalage);
    }

    private static String fauxCode(String bon) {
        return "%06d".formatted((Integer.parseInt(bon) + 1) % 1_000_000);
    }

    /** Compte avec application déjà configurée (enrôlement fait) ; renvoie son secret et le dernier décalage de pas utilisé. */
    private record Configure(UtilisateurDto utilisateur, String secret) {
    }

    private Configure configurer(Role role) throws Exception {
        UtilisateurDto u = creer(role, true);
        String defi = login(u).corps().path("defi").asText();
        String secret = demarrer(defi).path("secret").asText();
        assertThat(confirmer(defi, code(secret, -1)).statut()).isEqualTo(200); // pas -1 : laisse 0 et +1 pour les connexions suivantes
        return new Configure(u, secret);
    }

    // ------------------------------------------------------------------ enrôlement à la première connexion

    @Test
    void un_compte_sans_application_la_configure_avant_d_obtenir_une_session() throws Exception {
        UtilisateurDto u = creer(Role.ENCAISSEUR, true);

        ResultPost premiere = login(u);
        assertThat(premiere.statut()).isEqualTo(200);
        assertThat(premiere.corps().path("enrolementRequis").asBoolean()).isTrue();
        assertThat(premiere.corps().path("deuxFacteursRequis").asBoolean()).isFalse();
        assertThat(premiere.corps().has("accessToken")).isFalse();
        String defi = premiere.corps().path("defi").asText();

        JsonNode e = demarrer(defi);
        assertThat(e.path("secret").asText()).matches("[A-Z2-7]{32}");
        assertThat(e.path("uri").asText()).startsWith("otpauth://totp/ImmoGest:").contains("secret=" + e.path("secret").asText()).contains("issuer=ImmoGest");

        // un mauvais code ne configure rien et ne donne aucune session
        ResultPost mauvais = confirmer(defi, fauxCode(code(e.path("secret").asText(), 0)));
        assertThat(mauvais.statut()).isEqualTo(401);
        assertThat(mauvais.code()).isEqualTo("OTP_INVALIDE");
        mvc.perform(get("/api/v1/auth/moi").with(jwtRole(u.role(), u.id()))).andExpect(status().isOk());

        ResultPost ok = confirmer(defi, code(e.path("secret").asText(), 0));
        assertThat(ok.statut()).isEqualTo(200);
        assertThat(ok.corps().path("accessToken").asText()).isNotBlank();
        assertThat(ok.corps().path("role").asText()).isEqualTo("ENCAISSEUR");
        assertThat(service.obtenir(u.id()).deuxFacteursConfigure()).isTrue();
    }

    @Test
    void le_secret_est_chiffre_en_base_et_n_est_jamais_renvoye() throws Exception {
        Configure c = configurer(Role.COMPTABLE);
        String enBase = jdbc.queryForObject("SELECT totp_secret_chiffre FROM utilisateur WHERE id = ?", String.class, c.utilisateur().id());
        assertThat(enBase).isNotBlank().doesNotContain(c.secret());
        String moi = mvc.perform(get("/api/v1/auth/moi").with(jwtRole(Role.COMPTABLE, c.utilisateur().id()))).andReturn().getResponse().getContentAsString();
        assertThat(moi).doesNotContain(c.secret()).doesNotContain(enBase).contains("\"deuxFacteursConfigure\":true");
        String liste = mvc.perform(get("/api/v1/utilisateurs").param("size", "100").with(jwtRole(Role.ADMIN))).andReturn().getResponse().getContentAsString();
        assertThat(liste).doesNotContain(c.secret()).doesNotContain(enBase);
    }

    // ------------------------------------------------------------------ connexions suivantes

    @Test
    void la_connexion_exige_le_mot_de_passe_puis_le_code_de_l_application() throws Exception {
        Configure c = configurer(Role.GESTIONNAIRE);

        ResultPost etape1 = login(c.utilisateur());
        assertThat(etape1.corps().path("deuxFacteursRequis").asBoolean()).isTrue();
        assertThat(etape1.corps().path("enrolementRequis").asBoolean()).isFalse();
        assertThat(etape1.corps().has("accessToken")).isFalse();
        String defi = etape1.corps().path("defi").asText();

        assertThat(verifier(defi, fauxCode(code(c.secret(), 0))).statut()).isEqualTo(401);
        ResultPost ok = verifier(defi, code(c.secret(), 0));
        assertThat(ok.statut()).isEqualTo(200);
        assertThat(ok.corps().path("accessToken").asText()).isNotBlank();
        assertThat(ok.corps().path("refreshToken").asText()).isNotBlank();
        // le défi est à usage unique
        assertThat(verifier(defi, code(c.secret(), 1)).statut()).isEqualTo(401);
    }

    @Test
    void un_code_deja_utilise_est_refuse() throws Exception {
        Configure c = configurer(Role.GESTIONNAIRE);
        String code = code(c.secret(), 0);
        assertThat(verifier(login(c.utilisateur()).corps().path("defi").asText(), code).statut()).isEqualTo(200);
        ResultPost rejeu = verifier(login(c.utilisateur()).corps().path("defi").asText(), code);
        assertThat(rejeu.statut()).isEqualTo(401);
        assertThat(rejeu.code()).isEqualTo("OTP_INVALIDE");
        // le code du pas précédent (déjà dépassé par le pas confirmé) l'est aussi
        assertThat(verifier(login(c.utilisateur()).corps().path("defi").asText(), code(c.secret(), -1)).statut()).isEqualTo(401);
        assertThat(verifier(login(c.utilisateur()).corps().path("defi").asText(), code(c.secret(), 1)).statut()).isEqualTo(200);
    }

    @Test
    void un_code_seul_ne_donne_jamais_de_session() throws Exception {
        Configure c = configurer(Role.GESTIONNAIRE);
        // sans défi, ou avec un défi inventé
        assertThat(verifier("", code(c.secret(), 0)).statut()).isEqualTo(400);
        ResultPost inconnu = verifier("defi-invente-qui-n-existe-pas", code(c.secret(), 0));
        assertThat(inconnu.statut()).isEqualTo(401);
        assertThat(inconnu.code()).isEqualTo("DEFI_DEUX_FACTEURS_INVALIDE");
        // pas de connexion par code SMS pour ces comptes
        assertThat(postJson(mvc, "/api/v1/auth/otp/verifier", json("identifiant", c.utilisateur().telephone(), "code", code(c.secret(), 0))).statut()).isEqualTo(401);
        // un mauvais mot de passe ne délivre aucun défi
        assertThat(postJson(mvc, "/api/v1/auth/login", json("identifiant", c.utilisateur().telephone(), "motDePasse", "faux-faux-faux")).corps().has("defi")).isFalse();
    }

    @Test
    void les_defis_ne_servent_qu_a_leur_etape() throws Exception {
        Configure c = configurer(Role.GESTIONNAIRE);
        String defiVerif = login(c.utilisateur()).corps().path("defi").asText();
        assertThat(postJson(mvc, "/api/v1/auth/deux-facteurs/enrolement/demarrer", json("defi", defiVerif)).statut()).isEqualTo(401);

        UtilisateurDto neuf = creer(Role.ENCAISSEUR, true);
        String defiEnrolement = login(neuf).corps().path("defi").asText();
        assertThat(verifier(defiEnrolement, "123456").statut()).isEqualTo(401);
    }

    @Test
    void un_defi_expire_est_refuse() throws Exception {
        Configure c = configurer(Role.GESTIONNAIRE);
        String defi = login(c.utilisateur()).corps().path("defi").asText();
        jdbc.update("UPDATE sec_defi_2fa SET expire_le = now() - interval '1 minute' WHERE utilisateur_id = ?", c.utilisateur().id());
        ResultPost r = verifier(defi, code(c.secret(), 1));
        assertThat(r.statut()).isEqualTo(401);
        assertThat(r.code()).isEqualTo("DEFI_DEUX_FACTEURS_INVALIDE");
    }

    @Test
    void trop_de_codes_faux_invalident_le_defi_puis_verrouillent_le_compte() throws Exception {
        Configure c = configurer(Role.GESTIONNAIRE);
        String defi = login(c.utilisateur()).corps().path("defi").asText();
        String faux = fauxCode(code(c.secret(), 0));
        for (int i = 0; i < 4; i++) {
            assertThat(verifier(defi, faux).statut()).isEqualTo(401);
        }
        // 5e échec : le compte se verrouille (même seuil que les mots de passe), et le défi ne sert plus
        assertThat(verifier(defi, faux).statut()).isEqualTo(423);
        assertThat(verifier(defi, code(c.secret(), 0)).statut()).isIn(401, 423);
        assertThat(login(c.utilisateur()).statut()).isEqualTo(423);
    }

    // ------------------------------------------------------------------ réinitialisation par l'ADMIN

    @Test
    void l_admin_reinitialise_l_application_perdue() throws Exception {
        Configure c = configurer(Role.ENCAISSEUR);
        String refresh = verifier(login(c.utilisateur()).corps().path("defi").asText(), code(c.secret(), 0)).corps().path("refreshToken").asText();

        mvc.perform(post("/api/v1/utilisateurs/{id}/reinitialiser-deux-facteurs", c.utilisateur().id()).with(jwtRole(Role.GESTIONNAIRE)))
                .andExpect(status().isForbidden());
        mvc.perform(post("/api/v1/utilisateurs/{id}/reinitialiser-deux-facteurs", c.utilisateur().id()).with(jwtRole(Role.ADMIN)))
                .andExpect(status().isNoContent());

        // sessions fermées, secret retiré, nouvel enrôlement à la prochaine connexion
        assertThat(postJson(mvc, "/api/v1/auth/refresh", json("refreshToken", refresh)).statut()).isEqualTo(401);
        assertThat(service.obtenir(c.utilisateur().id()).deuxFacteursConfigure()).isFalse();
        assertThat(login(c.utilisateur()).corps().path("enrolementRequis").asBoolean()).isTrue();
    }

    // ------------------------------------------------------------------ profil : activer et désactiver

    @Test
    void active_depuis_le_profil_avec_un_premier_code_puis_la_connexion_l_exige() throws Exception {
        UtilisateurDto u = creer(Role.ADMIN, false);
        RequestPostProcessor moi = jwtRole(u.role(), u.id());

        mvc.perform(post("/api/v1/auth/moi/double-authentification/demarrer").with(moi).contentType(MediaType.APPLICATION_JSON)
                .content(json("motDePasse", "faux-faux-faux"))).andExpect(status().isUnprocessableEntity());
        String reponse = mvc.perform(post("/api/v1/auth/moi/double-authentification/demarrer").with(moi).contentType(MediaType.APPLICATION_JSON)
                .content(json("motDePasse", MOT_DE_PASSE))).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        String secret = new com.fasterxml.jackson.databind.ObjectMapper().readTree(reponse).path("secret").asText();
        assertThat(secret).matches("[A-Z2-7]{32}");
        assertThat(service.obtenir(u.id()).deuxFacteursActif()).isFalse(); // pas encore activée

        mvc.perform(post("/api/v1/auth/moi/double-authentification/activer").with(moi).contentType(MediaType.APPLICATION_JSON)
                .content(json("code", fauxCode(code(secret, 0))))).andExpect(status().isUnprocessableEntity());
        mvc.perform(post("/api/v1/auth/moi/double-authentification/activer").with(moi).contentType(MediaType.APPLICATION_JSON)
                .content(json("code", code(secret, -1)))).andExpect(status().isOk());
        assertThat(service.obtenir(u.id()).deuxFacteursActif()).isTrue();
        assertThat(service.obtenir(u.id()).deuxFacteursConfigure()).isTrue();

        assertThat(login(u).corps().path("deuxFacteursRequis").asBoolean()).isTrue();
        // déjà active
        mvc.perform(post("/api/v1/auth/moi/double-authentification/demarrer").with(moi).contentType(MediaType.APPLICATION_JSON)
                .content(json("motDePasse", MOT_DE_PASSE))).andExpect(status().isConflict());
    }

    @Test
    void desactive_avec_le_mot_de_passe_et_le_code_de_l_application() throws Exception {
        Configure c = configurer(Role.ADMIN);
        RequestPostProcessor moi = jwtRole(Role.ADMIN, c.utilisateur().id());

        mvc.perform(post("/api/v1/auth/moi/double-authentification/desactiver").with(moi).contentType(MediaType.APPLICATION_JSON)
                .content(json("motDePasse", MOT_DE_PASSE))).andExpect(status().isUnprocessableEntity()); // le code est exigé
        mvc.perform(post("/api/v1/auth/moi/double-authentification/desactiver").with(moi).contentType(MediaType.APPLICATION_JSON)
                .content(json("motDePasse", "faux-faux-faux", "code", code(c.secret(), 0)))).andExpect(status().isUnprocessableEntity());
        assertThat(service.obtenir(c.utilisateur().id()).deuxFacteursActif()).isTrue();

        mvc.perform(post("/api/v1/auth/moi/double-authentification/desactiver").with(moi).contentType(MediaType.APPLICATION_JSON)
                .content(json("motDePasse", MOT_DE_PASSE, "code", code(c.secret(), 0)))).andExpect(status().isOk());
        assertThat(service.obtenir(c.utilisateur().id()).deuxFacteursActif()).isFalse();
        assertThat(service.obtenir(c.utilisateur().id()).deuxFacteursConfigure()).isFalse();
        assertThat(login(c.utilisateur()).corps().path("accessToken").asText()).isNotBlank(); // connexion sans second facteur
    }

    @Test
    void les_employes_restreints_peuvent_la_desactiver() throws Exception {
        for (Role role : new Role[] {Role.ENCAISSEUR, Role.GESTIONNAIRE_BIENS, Role.COMPTABLE}) {
            Configure c = configurer(role);
            mvc.perform(post("/api/v1/auth/moi/double-authentification/desactiver").with(jwtRole(role, c.utilisateur().id())).contentType(MediaType.APPLICATION_JSON)
                    .content(json("motDePasse", MOT_DE_PASSE, "code", code(c.secret(), 0)))).andExpect(status().isOk());
            assertThat(service.obtenir(c.utilisateur().id()).deuxFacteursActif()).isFalse();
        }
    }

    @Test
    void ne_concerne_pas_les_locataires() throws Exception {
        UtilisateurDto locataire = creer(Role.LOCATAIRE, false);
        mvc.perform(post("/api/v1/auth/moi/double-authentification/demarrer").with(jwtRole(Role.LOCATAIRE, locataire.id())).contentType(MediaType.APPLICATION_JSON)
                .content(json("motDePasse", MOT_DE_PASSE))).andExpect(status().isConflict());
        // et ils gardent leur connexion par code SMS
        assertThat(postJson(mvc, "/api/v1/auth/otp/demander", json("identifiant", locataire.telephone())).statut()).isEqualTo(202);
        assertThat(sms.pour(locataire.telephone())).hasSize(1);
    }

    @Test
    void sans_session_les_routes_du_profil_sont_refusees() throws Exception {
        mvc.perform(post("/api/v1/auth/moi/double-authentification/demarrer").contentType(MediaType.APPLICATION_JSON).content(json("motDePasse", "x")))
                .andExpect(status().isUnauthorized());
        mvc.perform(post("/api/v1/auth/moi/double-authentification/activer").contentType(MediaType.APPLICATION_JSON).content(json("code", "123456")))
                .andExpect(status().isUnauthorized());
        mvc.perform(post("/api/v1/auth/moi/double-authentification/desactiver").contentType(MediaType.APPLICATION_JSON).content(json("motDePasse", "x")))
                .andExpect(status().isUnauthorized());
        mvc.perform(post("/api/v1/utilisateurs/{id}/reinitialiser-deux-facteurs", java.util.UUID.randomUUID())).andExpect(status().isUnauthorized());
    }
}
