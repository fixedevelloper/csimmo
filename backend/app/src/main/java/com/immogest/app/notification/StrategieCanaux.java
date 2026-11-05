package com.immogest.app.notification;

/** Choix des canaux pour un message à un locataire. */
public enum StrategieCanaux {
    /** Documents (quittance, reçu) : tous les canaux WhatsApp, e-mail et push actifs ; SMS seulement en repli. */
    DOCUMENTS,
    /** Relances : un seul canal, le premier disponible dans l'ordre WhatsApp → push → SMS (canal actif ET contact connu). */
    PREFERENCE
}
