package com.immogest.app.notification;

/**
 * Configuration que l'application mobile reçoit du serveur de son installation. {@code push} est {@code null} tant que le canal push n'est
 * pas actif avec ses identifiants Firebase publics (apiKey, appId, messagingSenderId) : l'application n'initialise alors pas Firebase.
 */
public record ConfigMobileDto(PushMobile push) {

    public record PushMobile(String projectId, String apiKey, String appId, String messagingSenderId) {
    }
}
