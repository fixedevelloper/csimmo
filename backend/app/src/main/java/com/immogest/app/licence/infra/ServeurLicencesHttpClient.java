package com.immogest.app.licence.infra;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.immogest.app.licence.ServeurLicencesClient;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * Client HTTP du serveur de licences de l'éditeur : {@code POST {serveur-url}/api/v1/verification}.
 * Toute anomalie (réseau, délai dépassé, statut autre que 200, réponse illisible) est traitée comme « serveur
 * injoignable » : le délai de grâce continue de courir, la session de l'utilisateur n'est jamais coupée sur cette base.
 */
@Component
class ServeurLicencesHttpClient implements ServeurLicencesClient {

    private final LicenceProperties proprietes;
    private final ObjectMapper mapper;
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();

    ServeurLicencesHttpClient(LicenceProperties proprietes, ObjectMapper mapper) {
        this.proprietes = proprietes;
        this.mapper = mapper;
    }

    @Override
    public ReponseVerification verifier(DemandeVerification demande) throws ServeurLicencesIndisponibleException {
        if (!proprietes.verificationEnLigneActive()) {
            throw new ServeurLicencesIndisponibleException("aucune URL de serveur de licences configurée");
        }
        try {
            String url = proprietes.serveurUrl().replaceAll("/+$", "") + "/api/v1/verification";
            HttpRequest requete = HttpRequest.newBuilder(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(Map.of(
                            "licenseId", demande.licenseId(),
                            "empreinteServeur", demande.empreinteServeur(),
                            "versionApplication", demande.versionApplication()))))
                    .build();
            HttpResponse<String> reponse = http.send(requete, HttpResponse.BodyHandlers.ofString());
            if (reponse.statusCode() != 200) {
                throw new ServeurLicencesIndisponibleException("réponse HTTP " + reponse.statusCode());
            }
            JsonNode corps = mapper.readTree(reponse.body());
            ReponseVerification.Statut statut = ReponseVerification.Statut.valueOf(corps.path("statut").asText());
            String renouvelee = corps.hasNonNull("licenceRenouvelee") ? corps.get("licenceRenouvelee").asText() : null;
            return new ReponseVerification(statut, renouvelee);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ServeurLicencesIndisponibleException("interrompu", e);
        } catch (IOException | IllegalArgumentException e) {
            throw new ServeurLicencesIndisponibleException(e.getClass().getSimpleName(), e);
        }
    }
}
