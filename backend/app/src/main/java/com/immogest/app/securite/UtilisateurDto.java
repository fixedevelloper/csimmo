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
        boolean verrouille) {
}
