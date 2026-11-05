package com.immogest.app.shared;

import java.util.Set;
import java.util.UUID;

/** Utilisateur authentifié qui effectue une requête (identifiant et rôles). */
public record Acteur(UUID id, Set<String> roles) {

    public boolean aLeRole(String role) {
        return roles.contains(role);
    }
}
