package com.immogest.app.notification.infra;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.immogest.app.notification.TypeCanal;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Push Firebase (FCM HTTP v1). Le jeton OAuth2 s'obtient avec le compte de service saisi par le client : JWT RS256 signé avec le
 * JDK, échangé contre un jeton d'accès (mis en cache). Aucun SDK.
 */
@Component
@ConditionalOnProperty(name = "immogest.notification.simuler", havingValue = "false", matchIfMissing = true)
public class CanalPush implements CanalNotification {

    private static final ObjectMapper JSON = new ObjectMapper();
    private final String baseFcm;

    private volatile String jetonCache;
    private volatile Instant jetonExpire = Instant.EPOCH;
    private volatile String jetonPour;

    CanalPush(@Value("${immogest.notification.fcm-base:https://fcm.googleapis.com}") String baseFcm) {
        this.baseFcm = baseFcm.replaceAll("/+$", "");
    }

    @Override
    public TypeCanal type() {
        return TypeCanal.PUSH;
    }

    @Override
    public List<Champ> champs() {
        // apiKey, appId et messagingSenderId sont les identifiants PUBLICS de l'application Firebase côté téléphone : le serveur les
        // transmet à l'application mobile (qui s'initialise ainsi sur le projet Firebase du client, sans fichier google-services.json)
        return List.of(new Champ("projectId", false, true, null), new Champ("compteService", true, true, null),
                new Champ("apiKey", false, false, null), new Champ("appId", false, false, null), new Champ("messagingSenderId", false, false, null));
    }

    @Override
    public void envoyer(Envoi envoi, Map<String, String> config) throws EnvoiException {
        String jeton = jeton(config.get("compteService"));
        String corps = "{\"message\":{\"token\":\"%s\",\"notification\":{\"title\":\"%s\",\"body\":\"%s\"}}}".formatted(
                ClientHttp.echapperJson(envoi.destinataire()), ClientHttp.echapperJson(envoi.sujet() == null ? "ImmoGest" : envoi.sujet()),
                ClientHttp.echapperJson(envoi.corps()));
        ClientHttp.exigerSucces(ClientHttp.poster(baseFcm + "/v1/projects/" + config.get("projectId") + "/messages:send",
                Map.of("Authorization", "Bearer " + jeton), "application/json", corps));
    }

    private synchronized String jeton(String compteServiceJson) throws EnvoiException {
        if (jetonCache != null && compteServiceJson.equals(jetonPour) && Instant.now().isBefore(jetonExpire)) {
            return jetonCache;
        }
        try {
            JsonNode compte = JSON.readTree(compteServiceJson);
            String email = compte.path("client_email").asText();
            String tokenUri = compte.path("token_uri").asText("https://oauth2.googleapis.com/token");
            long maintenant = Instant.now().getEpochSecond();
            String entete = b64("{\"alg\":\"RS256\",\"typ\":\"JWT\"}");
            String charge = b64("{\"iss\":\"%s\",\"scope\":\"https://www.googleapis.com/auth/firebase.messaging\",\"aud\":\"%s\",\"iat\":%d,\"exp\":%d}"
                    .formatted(ClientHttp.echapperJson(email), ClientHttp.echapperJson(tokenUri), maintenant, maintenant + 3600));
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(cle(compte.path("private_key").asText()));
            signature.update((entete + "." + charge).getBytes(StandardCharsets.US_ASCII));
            String assertion = entete + "." + charge + "." + Base64.getUrlEncoder().withoutPadding().encodeToString(signature.sign());
            ClientHttp.Reponse r = ClientHttp.poster(tokenUri, Map.of(), "application/x-www-form-urlencoded",
                    "grant_type=urn%3Aietf%3Aparams%3Aoauth%3Agrant-type%3Ajwt-bearer&assertion=" + assertion);
            ClientHttp.exigerSucces(r);
            JsonNode rep = JSON.readTree(r.corps());
            jetonCache = rep.path("access_token").asText();
            jetonExpire = Instant.now().plusSeconds(Math.max(60, rep.path("expires_in").asLong(3600) - 300));
            jetonPour = compteServiceJson;
            if (jetonCache.isBlank()) {
                throw new EnvoiException("Jeton FCM absent de la réponse");
            }
            return jetonCache;
        } catch (EnvoiException e) {
            throw e;
        } catch (Exception e) {
            throw new EnvoiException("Compte de service Firebase invalide ou jeton refusé", e);
        }
    }

    private static String b64(String json) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(json.getBytes(StandardCharsets.UTF_8));
    }

    private static PrivateKey cle(String pem) throws Exception {
        String brut = pem.replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "").replaceAll("\\s", "");
        return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(brut)));
    }
}
