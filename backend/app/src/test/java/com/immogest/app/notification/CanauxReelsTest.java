package com.immogest.app.notification.infra;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/** Canaux réels contre un faux fournisseur HTTP local (aucun appel externe). */
class CanauxReelsTest {

    record Appel(String chemin, Map<String, List<String>> entetes, String corps) {
    }

    private final ObjectMapper json = new ObjectMapper();
    private final List<Appel> appels = new CopyOnWriteArrayList<>();
    private HttpServer serveur;
    private int statutReponse = 200;
    private String corpsReponse = "{}";

    @BeforeEach
    void demarrer() throws Exception {
        serveur = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        serveur.createContext("/", ex -> {
            appels.add(new Appel(ex.getRequestURI().getPath(), ex.getRequestHeaders(), new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8)));
            byte[] rep = corpsReponse.getBytes(StandardCharsets.UTF_8);
            ex.sendResponseHeaders(statutReponse, rep.length);
            ex.getResponseBody().write(rep);
            ex.close();
        });
        serveur.start();
    }

    @AfterEach
    void arreter() {
        serveur.stop(0);
    }

    private String base() {
        return "http://127.0.0.1:" + serveur.getAddress().getPort();
    }

    // ================================================================== SMS

    @Test
    void smsRemplaceLesVariablesEnEchappantLeJsonEtEnvoieLEnteteDAuthentification() throws Exception {
        new CanalSms().envoyer(new CanalNotification.Envoi("+237 6 99 11 22 33", null, "Bonjour \"Paul\"\nMerci \\o/"),
                Map.of("url", base() + "/sms", "expediteur", "IMMO", "enteteNom", "X-Api-Key", "enteteValeur", "secret-123"));

        assertThat(appels).hasSize(1);
        Appel a = appels.get(0);
        assertThat(a.chemin()).isEqualTo("/sms");
        assertThat(a.entetes().get("X-api-key")).containsExactly("secret-123");
        JsonNode corps = json.readTree(a.corps()); // un JSON valide malgré guillemets, saut de ligne et antislash
        assertThat(corps.path("to").asText()).isEqualTo("237699112233");
        assertThat(corps.path("from").asText()).isEqualTo("IMMO");
        assertThat(corps.path("message").asText()).isEqualTo("Bonjour \"Paul\"\nMerci \\o/");
    }

    @Test
    void smsAvecCorpsPersonnalise() throws Exception {
        new CanalSms().envoyer(new CanalNotification.Envoi("+237699112233", null, "Salut"),
                Map.of("url", base() + "/api", "corps", "{\"destination\":\"{to}\",\"sms\":{\"text\":\"{message}\"}}"));
        assertThat(json.readTree(appels.get(0).corps()).path("sms").path("text").asText()).isEqualTo("Salut");
        assertThat(appels.get(0).entetes().containsKey("Authorization")).isFalse(); // pas de valeur d'authentification → pas d'en-tête
    }

    @Test
    void erreurHttpDuFournisseurEstRemonteeSansSecret() {
        statutReponse = 401;
        corpsReponse = "{\"error\":\"bad key\"}";
        assertThatThrownBy(() -> new CanalSms().envoyer(new CanalNotification.Envoi("+237699112233", null, "x"),
                Map.of("url", base(), "enteteValeur", "TOP-SECRET")))
                .isInstanceOf(EnvoiException.class).hasMessageContaining("HTTP 401").hasMessageNotContaining("TOP-SECRET");
    }

    @Test
    void fournisseurInjoignable() {
        serveur.stop(0);
        assertThatThrownBy(() -> new CanalSms().envoyer(new CanalNotification.Envoi("+237699112233", null, "x"), Map.of("url", base())))
                .isInstanceOf(EnvoiException.class).hasMessageContaining("injoignable");
    }

    @Test
    void lesAdressesDeMetadonneesCloudSontRefusees() {
        for (String url : List.of("http://169.254.169.254/latest/meta-data", "ftp://exemple.cm/x", "pas une url", "http:///x")) {
            assertThatThrownBy(() -> new CanalSms().envoyer(new CanalNotification.Envoi("+237699112233", null, "x"), Map.of("url", url)))
                    .isInstanceOf(EnvoiException.class);
        }
    }

    @Test
    void lesRedirectionsNeSontPasSuivies() {
        serveur.removeContext("/");
        serveur.createContext("/", ex -> {
            ex.getResponseHeaders().add("Location", "http://169.254.169.254/");
            ex.sendResponseHeaders(302, -1);
            ex.close();
        });
        assertThatThrownBy(() -> new CanalSms().envoyer(new CanalNotification.Envoi("+237699112233", null, "x"), Map.of("url", base())))
                .isInstanceOf(EnvoiException.class).hasMessageContaining("HTTP 302");
    }

    // ================================================================== WhatsApp

    @Test
    void whatsappEnvoieUnMessageTemplateAvecLeJetonBearer() throws Exception {
        new CanalWhatsapp().envoyer(new CanalNotification.Envoi("+237699112233", "sujet", "Votre quittance\nest prête"),
                Map.of("apiBase", base() + "/v21.0", "phoneNumberId", "1234567890", "modele", "avis_loyer", "langue", "fr", "token", "EAAG-tok"));
        Appel a = appels.get(0);
        assertThat(a.chemin()).isEqualTo("/v21.0/1234567890/messages");
        assertThat(a.entetes().get("Authorization")).containsExactly("Bearer EAAG-tok");
        JsonNode c = json.readTree(a.corps());
        assertThat(c.path("to").asText()).isEqualTo("237699112233");
        assertThat(c.path("type").asText()).isEqualTo("template");
        assertThat(c.path("template").path("name").asText()).isEqualTo("avis_loyer");
        assertThat(c.path("template").path("language").path("code").asText()).isEqualTo("fr");
        assertThat(c.path("template").path("components").get(0).path("parameters").get(0).path("text").asText()).isEqualTo("Votre quittance est prête");
    }

    // ================================================================== Push

    @Test
    void pushObtientUnJetonOauthSigneRs256PuisEnvoieEtLeMetEnCache() throws Exception {
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(2048);
        KeyPair paire = gen.generateKeyPair();
        String pem = "-----BEGIN PRIVATE KEY-----\n" + Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(paire.getPrivate().getEncoded())
                + "\n-----END PRIVATE KEY-----\n";
        String compte = json.writeValueAsString(Map.of("client_email", "svc@projet.iam.gserviceaccount.com", "private_key", pem,
                "token_uri", base() + "/token"));
        corpsReponse = "{\"access_token\":\"ya29.jeton\",\"expires_in\":3600}";

        CanalPush push = new CanalPush(base());
        CanalNotification.Envoi envoi = new CanalNotification.Envoi("tok-appareil", "Quittance", "Disponible \"maintenant\"");
        push.envoyer(envoi, Map.of("projectId", "mon-projet", "compteService", compte));
        push.envoyer(envoi, Map.of("projectId", "mon-projet", "compteService", compte));

        assertThat(appels.stream().filter(a -> a.chemin().equals("/token"))).hasSize(1); // jeton mis en cache
        Appel token = appels.get(0);
        String assertion = token.corps().substring(token.corps().indexOf("assertion=") + 10);
        String[] parts = assertion.split("\\.");
        assertThat(json.readTree(Base64.getUrlDecoder().decode(parts[0])).path("alg").asText()).isEqualTo("RS256");
        assertThat(json.readTree(Base64.getUrlDecoder().decode(parts[1])).path("iss").asText()).isEqualTo("svc@projet.iam.gserviceaccount.com");
        java.security.Signature v = java.security.Signature.getInstance("SHA256withRSA");
        v.initVerify(paire.getPublic());
        v.update((parts[0] + "." + parts[1]).getBytes(StandardCharsets.US_ASCII));
        assertThat(v.verify(Base64.getUrlDecoder().decode(parts[2]))).isTrue();

        Appel envoye = appels.stream().filter(a -> a.chemin().equals("/v1/projects/mon-projet/messages:send")).findFirst().orElseThrow();
        assertThat(envoye.entetes().get("Authorization")).containsExactly("Bearer ya29.jeton");
        JsonNode m = json.readTree(envoye.corps()).path("message");
        assertThat(m.path("token").asText()).isEqualTo("tok-appareil");
        assertThat(m.path("notification").path("body").asText()).isEqualTo("Disponible \"maintenant\"");
    }

    @Test
    void pushAvecCompteDeServiceInvalide() {
        assertThatThrownBy(() -> new CanalPush(base()).envoyer(new CanalNotification.Envoi("t", "s", "c"),
                Map.of("projectId", "p", "compteService", "pas du json"))).isInstanceOf(EnvoiException.class).hasMessageContaining("invalide");
    }

    // ================================================================== E-mail

    @Test
    void emailConstruitUnClientSmtpDepuisLaConfiguration() throws Exception {
        JavaMailSenderImpl s = new CanalEmail().fabrique(Map.of("hote", "127.0.0.1", "port", "2525", "utilisateur", "u", "motDePasse", "p",
                "expediteur", "noreply@exemple.cm", "starttls", "false"));
        assertThat(s.getHost()).isEqualTo("127.0.0.1");
        assertThat(s.getPort()).isEqualTo(2525);
        assertThat(s.getUsername()).isEqualTo("u");
        assertThat(s.getJavaMailProperties().getProperty("mail.smtp.auth")).isEqualTo("true");
        assertThat(s.getJavaMailProperties().getProperty("mail.smtp.starttls.enable")).isEqualTo("false");
        JavaMailSenderImpl sansAuth = new CanalEmail().fabrique(Map.of("hote", "127.0.0.1", "expediteur", "a@b.cm"));
        assertThat(sansAuth.getPort()).isEqualTo(587);
        assertThat(sansAuth.getJavaMailProperties().getProperty("mail.smtp.auth")).isEqualTo("false");
    }

    @Test
    void emailInjoignableDonneUneErreurSansMotDePasse() {
        serveur.stop(0); // le port est libre : connexion refusée
        assertThatThrownBy(() -> new CanalEmail().envoyer(new CanalNotification.Envoi("a@b.cm", "S", "C"),
                Map.of("hote", "127.0.0.1", "port", String.valueOf(serveur.getAddress().getPort()), "utilisateur", "u", "motDePasse", "MDP-SECRET",
                        "expediteur", "x@y.cm", "starttls", "false")))
                .isInstanceOf(EnvoiException.class).hasMessageNotContaining("MDP-SECRET");
    }
}
