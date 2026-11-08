package com.immogest.app.securite;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Résultat d'une connexion. Si {@code deuxFacteursRequis}, aucun jeton n'est émis : le client demande le code de l'application d'authentification
 * et appelle {@code /api/v1/auth/deux-facteurs/verifier} avec le {@code defi}. Si {@code enrolementRequis}, le compte doit d'abord configurer
 * son application ({@code /api/v1/auth/deux-facteurs/enrolement/demarrer} puis {@code /confirmer}, avec le {@code defi}).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ReponseConnexion(
        String accessToken,
        String refreshToken,
        Long expiresIn,
        String tokenType,
        Role role,
        boolean deuxFacteursRequis,
        boolean enrolementRequis,
        String defi) {

    static ReponseConnexion tokens(String access, String refresh, long expiresIn, Role role) {
        return new ReponseConnexion(access, refresh, expiresIn, "Bearer", role, false, false, null);
    }

    static ReponseConnexion deuxFacteurs(String defi) {
        return new ReponseConnexion(null, null, null, null, null, true, false, defi);
    }

    static ReponseConnexion enrolement(String defi) {
        return new ReponseConnexion(null, null, null, null, null, false, true, defi);
    }
}
