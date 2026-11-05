package com.immogest.app.securite;

import static com.immogest.app.securite.SecuriteTestSupport.json;
import static com.immogest.app.securite.SecuriteTestSupport.jwtRole;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.immogest.app.IntegrationTest;
import java.util.Base64;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

/** Revue de sécurité (étape 11.2) : limite de débit sur /auth, en-têtes, plafond de pagination, jetons falsifiés, erreurs sans fuite. */
@IntegrationTest
@TestPropertySource(properties = {"immogest.securite.limite-auth-par-minute=5", "immogest.securite.limite-refresh-par-minute=3"})
class DurcissementApiTest {

    @Autowired
    MockMvc mvc;

    private static RequestPostProcessor depuis(String ip) {
        return r -> {
            r.setRemoteAddr(ip);
            return r;
        };
    }

    private void tentative(String ip, String chemin) throws Exception {
        mvc.perform(post(chemin).with(depuis(ip)).contentType(MediaType.APPLICATION_JSON).content(json("identifiant", "inconnu@x.cm", "motDePasse", "faux")));
    }

    // ------------------------------------------------------------------ limite de débit

    @Test
    void laConnexionEstLimiteeParAdresseIp() throws Exception {
        for (int i = 0; i < 5; i++) {
            mvc.perform(post("/api/v1/auth/login").with(depuis("203.0.113.10")).contentType(MediaType.APPLICATION_JSON).content(json("identifiant", "x@y.cm", "motDePasse", "faux")))
                    .andExpect(status().isUnauthorized());
        }
        mvc.perform(post("/api/v1/auth/login").with(depuis("203.0.113.10")).contentType(MediaType.APPLICATION_JSON).content(json("identifiant", "x@y.cm", "motDePasse", "faux")))
                .andExpect(status().isTooManyRequests()).andExpect(jsonPath("$.code").value("TROP_DE_REQUETES")).andExpect(header().exists("Retry-After"));
        // une autre adresse n'est pas touchée
        mvc.perform(post("/api/v1/auth/login").with(depuis("203.0.113.11")).contentType(MediaType.APPLICATION_JSON).content(json("identifiant", "x@y.cm", "motDePasse", "faux")))
                .andExpect(status().isUnauthorized());
        // les lectures ne sont pas comptées
        for (int i = 0; i < 10; i++) {
            mvc.perform(get("/api/v1/health").with(depuis("203.0.113.10"))).andExpect(status().isOk());
        }
    }

    @Test
    void lesDemandesOtpSontLimiteesAussi() throws Exception {
        for (int i = 0; i < 5; i++) {
            tentative("203.0.113.20", "/api/v1/auth/otp/demander");
        }
        mvc.perform(post("/api/v1/auth/otp/demander").with(depuis("203.0.113.20")).contentType(MediaType.APPLICATION_JSON).content(json("telephone", "+237600000000")))
                .andExpect(status().isTooManyRequests());
    }

    @Test
    void leRenouvellementADonCompteurIndependant() throws Exception {
        for (int i = 0; i < 3; i++) {
            mvc.perform(post("/api/v1/auth/refresh").with(depuis("203.0.113.30")).contentType(MediaType.APPLICATION_JSON).content(json("refreshToken", "bidon"))).andExpect(status().isUnauthorized());
        }
        mvc.perform(post("/api/v1/auth/refresh").with(depuis("203.0.113.30")).contentType(MediaType.APPLICATION_JSON).content(json("refreshToken", "bidon"))).andExpect(status().isTooManyRequests());
        // la connexion de la même adresse reste possible : autre compteur
        mvc.perform(post("/api/v1/auth/login").with(depuis("203.0.113.30")).contentType(MediaType.APPLICATION_JSON).content(json("identifiant", "x@y.cm", "motDePasse", "faux"))).andExpect(status().isUnauthorized());
    }

    // ------------------------------------------------------------------ en-têtes

    @Test
    void enTetesDeSecuriteSurLApi() throws Exception {
        mvc.perform(get("/api/v1/health").secure(true)).andExpect(status().isOk())
                .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                .andExpect(header().string("X-Frame-Options", "DENY"))
                .andExpect(header().string("Referrer-Policy", "no-referrer"))
                .andExpect(header().string("Content-Security-Policy", "default-src 'none'; frame-ancestors 'none'"))
                .andExpect(header().string("Permissions-Policy", "camera=(), microphone=(), geolocation=(), payment=()"))
                .andExpect(header().string("Strict-Transport-Security", "max-age=31536000 ; includeSubDomains"));
        // les réponses authentifiées ne sont jamais mises en cache (données personnelles)
        mvc.perform(get("/api/v1/immeubles").with(jwtRole(Role.ADMIN))).andExpect(status().isOk()).andExpect(header().string("Cache-Control", org.hamcrest.Matchers.containsString("no-store")));
    }

    // ------------------------------------------------------------------ entrées

    @Test
    void laTailleDePageEstPlafonnee() throws Exception {
        mvc.perform(get("/api/v1/immeubles").param("size", "100000").with(jwtRole(Role.ADMIN))).andExpect(status().isOk()).andExpect(jsonPath("$.size").value(100));
    }

    @Test
    void unTriSurUneProprieteInconnueOuMalveillanteNEstJamaisUneErreurServeur() throws Exception {
        for (String tri : new String[] {"inexistante,asc", "nom;drop table immeuble", "nom') or 1=1 --", "../../etc/passwd", "nom,desc,asc", ""}) {
            int statut = mvc.perform(get("/api/v1/immeubles").param("sort", tri).with(jwtRole(Role.ADMIN))).andReturn().getResponse().getStatus();
            assertThat(statut).as("tri « %s »", tri).isIn(200, 400);
        }
    }

    @Test
    void uneErreurNeRevelePasLaPileNiLesChemins() throws Exception {
        String corps = mvc.perform(post("/api/v1/immeubles").with(jwtRole(Role.ADMIN)).contentType(MediaType.APPLICATION_JSON).content("{pas du json")).andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
        assertThat(corps).doesNotContain("Exception").doesNotContain("com.immogest").doesNotContain("org.springframework").doesNotContain(".java");
        String corps2 = mvc.perform(get("/api/v1/immeubles/{id}", "pas-un-uuid").with(jwtRole(Role.ADMIN))).andReturn().getResponse().getContentAsString();
        assertThat(corps2).doesNotContain("Exception").doesNotContain("com.immogest").doesNotContain("org.springframework");
        assertThat(mvc.perform(get("/api/v1/inexistant").with(jwtRole(Role.ADMIN))).andReturn().getResponse().getContentAsString()).doesNotContain("Exception");
    }

    // ------------------------------------------------------------------ jetons

    private static String b64(String s) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(s.getBytes());
    }

    @Test
    void desJetonsFalsifiesSontRefuses() throws Exception {
        String charge = b64("{\"sub\":\"" + UUID.randomUUID() + "\",\"roles\":[\"ADMIN\"],\"exp\":4102444800}");
        // algorithme « none »
        mvc.perform(get("/api/v1/immeubles").header("Authorization", "Bearer " + b64("{\"alg\":\"none\",\"typ\":\"JWT\"}") + "." + charge + ".")).andExpect(status().isUnauthorized());
        // signature d'un autre secret (HS256 valide mais mauvaise clé)
        mvc.perform(get("/api/v1/immeubles").header("Authorization", "Bearer " + b64("{\"alg\":\"HS256\",\"typ\":\"JWT\"}") + "." + charge + "." + b64("signature-bidon"))).andExpect(status().isUnauthorized());
        // jeton vide, malformé, schéma inconnu
        mvc.perform(get("/api/v1/immeubles").header("Authorization", "Bearer ")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/v1/immeubles").header("Authorization", "Bearer a.b")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/v1/immeubles").header("Authorization", "Basic YWRtaW46YWRtaW4=")).andExpect(status().isUnauthorized());
    }

    @Test
    void unLocataireNAccedeAAucuneFonctionDAdministration() throws Exception {
        RequestPostProcessor locataire = jwtRole(Role.LOCATAIRE, UUID.randomUUID());
        for (String url : new String[] {"/api/v1/utilisateurs", "/api/v1/sauvegardes", "/api/v1/sauvegardes/diagnostic", "/api/v1/import/modele", "/api/v1/paiements-en-ligne/fournisseurs",
                "/api/v1/tableau-de-bord/synthese", "/api/v1/immeubles", "/api/v1/locataires", "/api/v1/reversements"}) {
            mvc.perform(get(url).with(locataire)).andExpect(status().isForbidden());
        }
    }
}
