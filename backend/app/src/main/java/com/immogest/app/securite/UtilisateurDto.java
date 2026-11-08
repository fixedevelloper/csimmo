package com.immogest.app.securite;

import java.util.UUID;

public record UtilisateurDto(
        UUID id,
        String nom,
        String telephone,
        String email,
        Role role,
        boolean actif,
        boolean deuxFacteursActif,
        /** L'application d'authentification est configurée (sinon elle le sera à la prochaine connexion). */
        boolean deuxFacteursConfigure,
        boolean verrouille) {
}
