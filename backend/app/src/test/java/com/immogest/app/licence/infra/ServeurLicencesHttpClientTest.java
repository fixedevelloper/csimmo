package com.immogest.app.licence.infra;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.immogest.app.licence.ServeurLicencesClient.DemandeVerification;
import com.immogest.app.licence.ServeurLicencesClient.ReponseVerification;
import com.immogest.app.licence.ServeurLicencesClient.ServeurLicencesIndisponibleException;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Le vrai client HTTP, contre un faux serveur local (aucun appel externe). */
class ServeurLicencesHttpClientTest {

    private static final DemandeVerification DEMANDE = new DemandeVerification("L-1", "ab".repeat(32), "1.0");

    private HttpServer serveur;
    private final AtomicReference<String> corpsRecu = new AtomicReference<>();
    private final AtomicReference<String> cheminRecu = new AtomicReference<>();
    private volatile int statut = 200;
    private volatile String reponse = "{\"statut\":\"VALIDE\"}";
    private volatile long delaiMs;

    @BeforeEach
    void demarrer() throws IOException {
        serveur = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        serveur.createContext("/", echange -> {
            cheminRecu.set(echange.getRequestURI().getPath() + " " + echange.getRequestMethod() + " "
                    + echange.getRequestHeaders().getFirst("Content-Type"));
            corpsRecu.set(new String(echange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            try {
                Thread.sleep(delaiMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            byte[] octets = reponse.getBytes(StandardCharsets.UTF_8);
            echange.sendResponseHeaders(statut, octets.length);
            echange.getResponseBody().write(octets);
            echange.close();
        });
        serveur.start();
    }

    @AfterEach
    void arreter() {
        serveur.stop(0);
    }

    private ServeurLicencesHttpClient client(String urlServeur) {
        return new ServeurLicencesHttpClient(
                new LicenceProperties(null, urlServeur, null, 30, 7, List.of(30, 15, 7), null), new ObjectMapper());
    }

    private String url() {
        return "http://127.0.0.1:" + serveur.getAddress().getPort();
    }

    @Test
    void envoieLaDemandeEtLitLaReponse() throws Exception {
        reponse = "{\"statut\":\"VALIDE\",\"licenceRenouvelee\":\"{contenu}\"}";

        ReponseVerification r = client(url() + "/").verifier(DEMANDE);

        assertThat(r.statut()).isEqualTo(ReponseVerification.Statut.VALIDE);
        assertThat(r.licenceRenouvelee()).isEqualTo("{contenu}");
        assertThat(cheminRecu.get()).isEqualTo("/api/v1/verification POST application/json");
        var json = new ObjectMapper().readTree(corpsRecu.get());
        assertThat(json.path("licenseId").asText()).isEqualTo("L-1");
        assertThat(json.path("empreinteServeur").asText()).isEqualTo("ab".repeat(32));
        assertThat(json.path("versionApplication").asText()).isEqualTo("1.0");
    }

    @Test
    void lesTroisStatutsSontLus() throws Exception {
        for (ReponseVerification.Statut attendu : ReponseVerification.Statut.values()) {
            reponse = "{\"statut\":\"" + attendu + "\"}";
            ReponseVerification r = client(url()).verifier(DEMANDE);
            assertThat(r.statut()).isEqualTo(attendu);
            assertThat(r.licenceRenouvelee()).isNull();
        }
    }

    @Test
    void uneErreurDuServeurEstTraiteeCommeUnServeurInjoignable() {
        for (int code : new int[] {500, 503, 404, 401, 429}) {
            statut = code;
            assertThatThrownBy(() -> client(url()).verifier(DEMANDE))
                    .as("HTTP %d", code).isInstanceOf(ServeurLicencesIndisponibleException.class);
        }
    }

    @Test
    void uneReponseIllisibleOuInattendueEstTraiteeCommeUnServeurInjoignable() {
        for (String corps : new String[] {"pas du json", "{}", "{\"statut\":\"PEUT_ETRE\"}", "[]", ""}) {
            reponse = corps;
            assertThatThrownBy(() -> client(url()).verifier(DEMANDE)).as(corps)
                    .isInstanceOf(ServeurLicencesIndisponibleException.class);
        }
    }

    @Test
    void unServeurQuiNeRepondPasEstInjoignable() {
        String adresse = url();
        serveur.stop(0);

        assertThatThrownBy(() -> client(adresse).verifier(DEMANDE)).isInstanceOf(ServeurLicencesIndisponibleException.class);
    }

    @Test
    void sansUrlConfigureeLeClientRefuseSansAppelReseau() {
        assertThatThrownBy(() -> client("").verifier(DEMANDE)).isInstanceOf(ServeurLicencesIndisponibleException.class);
        assertThatThrownBy(() -> client(null).verifier(DEMANDE)).isInstanceOf(ServeurLicencesIndisponibleException.class);
        assertThat(corpsRecu.get()).isNull();
    }
}
