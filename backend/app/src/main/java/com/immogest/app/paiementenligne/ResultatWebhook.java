package com.immogest.app.paiementenligne;

public enum ResultatWebhook {
    /** Événement authentique appliqué à une transaction. */
    TRAITE,
    /** Déjà reçu : l'agrégateur rejoue, rien n'est refait. */
    REJOUE,
    /** Authentique mais sans effet (transaction inconnue, simple notification). */
    IGNORE
}
