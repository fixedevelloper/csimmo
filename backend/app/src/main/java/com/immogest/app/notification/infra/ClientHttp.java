package com.immogest.app.notification.infra;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;

/** Appels HTTP sortants des canaux : délai court, aucune redirection suivie, réponse tronquée dans les erreurs. */
final class ClientHttp {

    private static final HttpClient CLIENT = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10))
            .followRedirects(HttpClient.Redirect.NEVER).build();

    private ClientHttp() {
    }

    record Reponse(int statut, String corps) {
    }

    static Reponse poster(String url, Map<String, String> entetes, String contentType, String corps) throws EnvoiException {
        URI uri = ValidationUrl.verifier(url);
        HttpRequest.Builder r = HttpRequest.newBuilder(uri).timeout(Duration.ofSeconds(20)).header("Content-Type", contentType)
                .POST(HttpRequest.BodyPublishers.ofString(corps, StandardCharsets.UTF_8));
        entetes.forEach((k, v) -> {
            if (k != null && !k.isBlank() && v != null && !v.isBlank()) {
                r.header(k, v);
            }
        });
        try {
            HttpResponse<String> rep = CLIENT.send(r.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            return new Reponse(rep.statusCode(), rep.body());
        } catch (IOException e) {
            throw new EnvoiException("Fournisseur injoignable", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new EnvoiException("Envoi interrompu", e);
        }
    }

    static void exigerSucces(Reponse r) throws EnvoiException {
        if (r.statut() < 200 || r.statut() >= 300) {
            String extrait = r.corps() == null ? "" : r.corps().replaceAll("\\s+", " ");
            throw new EnvoiException("Le fournisseur a répondu HTTP %d %s".formatted(r.statut(), extrait.substring(0, Math.min(200, extrait.length()))).strip());
        }
    }

    /** Valeur échappée pour être insérée entre guillemets dans un JSON. */
    static String echapperJson(String valeur) {
        StringBuilder sb = new StringBuilder();
        for (char c : (valeur == null ? "" : valeur).toCharArray()) {
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        return sb.toString();
    }
}
