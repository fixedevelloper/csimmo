package com.immogest.app.notification.infra;

import java.net.InetAddress;
import java.net.URI;

/** Garde minimale contre l'usage des canaux pour atteindre les services de métadonnées cloud (adresses link-local). */
final class ValidationUrl {

    private ValidationUrl() {
    }

    static URI verifier(String url) throws EnvoiException {
        URI uri;
        try {
            uri = URI.create(url.strip());
        } catch (IllegalArgumentException e) {
            throw new EnvoiException("URL invalide");
        }
        if (uri.getHost() == null || (!"https".equals(uri.getScheme()) && !"http".equals(uri.getScheme()))) {
            throw new EnvoiException("L'URL doit être http(s) avec un hôte");
        }
        verifierHote(uri.getHost());
        return uri;
    }

    static void verifierHote(String hote) throws EnvoiException {
        try {
            for (InetAddress a : InetAddress.getAllByName(hote)) {
                if (a.isLinkLocalAddress() || a.isAnyLocalAddress()) {
                    throw new EnvoiException("Adresse non autorisée");
                }
            }
        } catch (java.net.UnknownHostException e) {
            throw new EnvoiException("Hôte introuvable");
        }
    }
}
