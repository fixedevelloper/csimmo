package com.immogest.app.securite;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Résultat d'une connexion. Si {@code deuxFacteursRequis}, aucun jeton n'est émis : un code a été envoyé par SMS
 * et le client doit appeler {@code /api/v1/auth/otp/verifier}.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ReponseConnexion(
        String accessToken,
        String refreshToken,
        Long expiresIn,
        String tokenType,
        Role role,
        boolean deuxFacteursRequis) {

    static ReponseConnexion tokens(String access, String refresh, long expiresIn, Role role) {
        return new ReponseConnexion(access, refresh, expiresIn, "Bearer", role, false);
    }

    static ReponseConnexion deuxFacteurs() {
        return new ReponseConnexion(null, null, null, null, null, true);
    }
}
