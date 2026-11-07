package com.immogest.app.securite;

/** Rôles de l'application (CLAUDE.md §6, docs/adr/0009-equipe-et-perimetres.md). */
public enum Role {
    ADMIN,
    GESTIONNAIRE,
    PROPRIETAIRE_TIERS,
    LOCATAIRE,
    PRESTATAIRE,
    /** Employé qui encaisse sur les immeubles qui lui sont affectés (refus par défaut : chaque route doit l'ouvrir). */
    ENCAISSEUR,
    /** Employé qui gère baux, locataires, relances et signalements des immeubles qui lui sont affectés (refus par défaut). */
    GESTIONNAIRE_BIENS,
    /** Employé qui lit les paiements, dépenses et reversements de son périmètre et valide les reversements (refus par défaut). */
    COMPTABLE;

    /** Rôles qui doivent toujours s'authentifier par mot de passe (la connexion par OTP seul leur est interdite). */
    public boolean motDePasseObligatoire() {
        return this == ADMIN || this == GESTIONNAIRE || restreintAuxAffectations();
    }

    /** Employés limités aux immeubles qui leur sont affectés ; sans affectation, ils ne voient rien. */
    public boolean restreintAuxAffectations() {
        return this == ENCAISSEUR || this == GESTIONNAIRE_BIENS || this == COMPTABLE;
    }

    /** Rôles pour lesquels la double authentification est imposée (aucun compte ne peut s'en passer). */
    public boolean deuxFacteursObligatoire() {
        return restreintAuxAffectations();
    }
}
