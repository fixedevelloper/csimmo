package com.immogest.app.securite;

/** Rôles de l'application (CLAUDE.md §6). */
public enum Role {
    ADMIN,
    GESTIONNAIRE,
    PROPRIETAIRE_TIERS,
    LOCATAIRE,
    PRESTATAIRE;

    /** Rôles qui doivent toujours s'authentifier par mot de passe (la connexion par OTP seul leur est interdite). */
    public boolean motDePasseObligatoire() {
        return this == ADMIN || this == GESTIONNAIRE;
    }
}
