package com.immogest.app.securite;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/** Outils communs aux tests du module securite : OTP capturés, création d'utilisateurs, connexion. */
@TestConfiguration(proxyBeanMethods = false)
public class SecuriteTestSupport {

    public static final String MOT_DE_PASSE = "Motdepasse123";
    static final ObjectMapper JSON = new ObjectMapper();

    /** Remplace l'envoi SMS : mémorise les codes envoyés. */
    public static class OtpCapture implements OtpSender {
        public record Envoi(String telephone, String code) {
        }

        private final List<Envoi> envois = new CopyOnWriteArrayList<>();

        @Override
        public void envoyer(String telephone, String code) {
            envois.add(new Envoi(telephone, code));
        }

        public List<Envoi> pour(String telephone) {
            return envois.stream().filter(e -> e.telephone().equals(telephone)).toList();
        }

        public String dernierCode(String telephone) {
            List<Envoi> liste = pour(telephone);
            return liste.get(liste.size() - 1).code();
        }
    }

    @Bean
    @Primary
    OtpCapture otpCapture() {
        return new OtpCapture();
    }

    public static String telephoneUnique() {
        return "+2376" + "%08d".formatted(ThreadLocalRandom.current().nextInt(100_000_000));
    }

    public static String emailUnique() {
        return "u" + UUID.randomUUID().toString().substring(0, 8) + "@test.local";
    }

    public static String json(Object... pairesCleValeur) {
        var noeud = JSON.createObjectNode();
        for (int i = 0; i < pairesCleValeur.length; i += 2) {
            noeud.putPOJO((String) pairesCleValeur[i], pairesCleValeur[i + 1]);
        }
        return noeud.toString();
    }

    public static JsonNode corps(MvcResult resultat) throws Exception {
        return JSON.readTree(resultat.getResponse().getContentAsString());
    }

    public static ResultPost postJson(MockMvc mvc, String url, String corps) throws Exception {
        return new ResultPost(mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(corps)).andReturn());
    }

    /** Résultat d'un POST : statut + corps JSON. */
    public record ResultPost(MvcResult resultat) {
        public int statut() {
            return resultat.getResponse().getStatus();
        }

        public JsonNode corps() throws Exception {
            return SecuriteTestSupport.corps(resultat);
        }

        public String code() throws Exception {
            return corps().path("code").asText();
        }
    }

    /** Connexion réelle par mot de passe : renvoie le corps de la réponse. */
    public static JsonNode connecter(MockMvc mvc, String identifiant, String motDePasse) throws Exception {
        var r = postJson(mvc, "/api/v1/auth/login", json("identifiant", identifiant, "motDePasse", motDePasse));
        if (r.statut() != 200) {
            throw new AssertionError("Connexion refusée : " + r.statut() + " " + r.corps());
        }
        return r.corps();
    }

    public static RequestPostProcessor jwtRole(Role role, UUID utilisateurId) {
        return jwt().jwt(j -> j.subject(utilisateurId.toString()))
                .authorities(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    public static RequestPostProcessor jwtRole(Role role) {
        return jwtRole(role, UUID.randomUUID());
    }
}
