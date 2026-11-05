package com.immogest.app.securite;

import static com.immogest.app.securite.SecuriteTestSupport.MOT_DE_PASSE;
import static com.immogest.app.securite.SecuriteTestSupport.json;
import static com.immogest.app.securite.SecuriteTestSupport.postJson;
import static com.immogest.app.securite.SecuriteTestSupport.telephoneUnique;
import static org.assertj.core.api.Assertions.assertThat;

import com.immogest.app.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

@IntegrationTest
@Import(SecuriteTestSupport.class)
class OtpApiTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    UtilisateurService service;
    @Autowired
    SecuriteTestSupport.OtpCapture sms;
    @Autowired
    JdbcTemplate jdbc;

    private UtilisateurDto creer(Role role, boolean deuxFacteurs) {
        return service.creer(new UtilisateurService.Saisie("Test " + role, telephoneUnique(), null, role, true,
                deuxFacteurs), MOT_DE_PASSE);
    }

    private SecuriteTestSupport.ResultPost demander(String identifiant) throws Exception {
        return postJson(mvc, "/api/v1/auth/otp/demander", json("identifiant", identifiant));
    }

    private SecuriteTestSupport.ResultPost verifier(String identifiant, String code) throws Exception {
        return postJson(mvc, "/api/v1/auth/otp/verifier", json("identifiant", identifiant, "code", code));
    }

    private static String autreCode(String code) {
        return "%06d".formatted((Integer.parseInt(code) + 1) % 1_000_000);
    }

    @Test
    void locataireSeConnecteParOtpUneSeuleFois() throws Exception {
        UtilisateurDto u = creer(Role.LOCATAIRE, false);

        assertThat(demander(u.telephone()).statut()).isEqualTo(202);
        String code = sms.dernierCode(u.telephone());
        assertThat(code).matches("\\d{6}");

        var ok = verifier(u.telephone(), code);
        assertThat(ok.statut()).isEqualTo(200);
        assertThat(ok.corps().path("accessToken").asText()).isNotBlank();
        assertThat(ok.corps().path("role").asText()).isEqualTo("LOCATAIRE");

        var rejeu = verifier(u.telephone(), code);
        assertThat(rejeu.statut()).isEqualTo(401);
        assertThat(rejeu.code()).isEqualTo("OTP_INVALIDE");
    }

    @Test
    void codeIncorrectRefuse() throws Exception {
        UtilisateurDto u = creer(Role.PRESTATAIRE, false);
        demander(u.telephone());

        var r = verifier(u.telephone(), autreCode(sms.dernierCode(u.telephone())));
        assertThat(r.statut()).isEqualTo(401);
        assertThat(r.code()).isEqualTo("OTP_INVALIDE");
    }

    @Test
    void cinqEssaisIncorrectsInvalidentLeCode() throws Exception {
        UtilisateurDto u = creer(Role.LOCATAIRE, false);
        demander(u.telephone());
        String bon = sms.dernierCode(u.telephone());

        for (int i = 0; i < 5; i++) {
            assertThat(verifier(u.telephone(), autreCode(bon)).statut()).isEqualTo(401);
        }
        assertThat(verifier(u.telephone(), bon).statut()).isEqualTo(401);
    }

    @Test
    void codeExpireRefuse() throws Exception {
        UtilisateurDto u = creer(Role.LOCATAIRE, false);
        demander(u.telephone());
        jdbc.update("UPDATE sec_otp_code SET expire_le = now() - interval '1 minute' WHERE utilisateur_id = ?", u.id());

        assertThat(verifier(u.telephone(), sms.dernierCode(u.telephone())).statut()).isEqualTo(401);
    }

    @Test
    void demandesRapprocheesNEnvoientQuUnCode() throws Exception {
        UtilisateurDto u = creer(Role.LOCATAIRE, false);

        demander(u.telephone());
        demander(u.telephone());

        assertThat(sms.pour(u.telephone())).hasSize(1);
    }

    @Test
    void demandeNeRevelePasSiLeCompteExiste() throws Exception {
        String inconnu = telephoneUnique();

        assertThat(demander(inconnu).statut()).isEqualTo(202);
        assertThat(sms.pour(inconnu)).isEmpty();
        assertThat(verifier(inconnu, "123456").statut()).isEqualTo(401);
    }

    @Test
    void adminEtGestionnaireNePeuventPasSeConnecterParOtpSeul() throws Exception {
        for (Role role : new Role[] {Role.ADMIN, Role.GESTIONNAIRE}) {
            UtilisateurDto u = creer(role, false);

            assertThat(demander(u.telephone()).statut()).isEqualTo(202);
            assertThat(sms.pour(u.telephone())).as("aucun SMS pour %s", role).isEmpty();
            assertThat(verifier(u.telephone(), "000000").statut()).isEqualTo(401);
        }
    }

    @Test
    void compteDesactiveNeRecoitPasDeCode() throws Exception {
        UtilisateurDto u = creer(Role.LOCATAIRE, false);
        service.desactiver(u.id());

        demander(u.telephone());

        assertThat(sms.pour(u.telephone())).isEmpty();
    }

    // ------------------------------------------------------------------ 2FA

    @Test
    void deuxFacteursExigeMotDePassePuisCode() throws Exception {
        UtilisateurDto u = creer(Role.GESTIONNAIRE, true);

        var login = postJson(mvc, "/api/v1/auth/login", json("identifiant", u.telephone(), "motDePasse", MOT_DE_PASSE));
        assertThat(login.statut()).isEqualTo(200);
        assertThat(login.corps().path("deuxFacteursRequis").asBoolean()).isTrue();
        assertThat(login.corps().has("accessToken")).isFalse();
        assertThat(login.corps().has("refreshToken")).isFalse();

        var mauvais = verifier(u.telephone(), autreCode(sms.dernierCode(u.telephone())));
        assertThat(mauvais.statut()).isEqualTo(401);

        var ok = verifier(u.telephone(), sms.dernierCode(u.telephone()));
        assertThat(ok.statut()).isEqualTo(200);
        assertThat(ok.corps().path("accessToken").asText()).isNotBlank();
        assertThat(ok.corps().path("role").asText()).isEqualTo("GESTIONNAIRE");
    }

    @Test
    void deuxFacteursNEnvoieRienSiLeMotDePasseEstFaux() throws Exception {
        UtilisateurDto u = creer(Role.ADMIN, true);

        var login = postJson(mvc, "/api/v1/auth/login", json("identifiant", u.telephone(), "motDePasse", "faux"));

        assertThat(login.statut()).isEqualTo(401);
        assertThat(sms.pour(u.telephone())).isEmpty();
    }
}
