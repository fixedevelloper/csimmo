package com.immogest.app.notification;

/** Modèles de messages (clés de {@code notification/messages_fr|en.properties}). */
public enum TypeMessage {
    /** Reçu SMS d'un encaissement en espèces (traçabilité de la caisse). */
    RECU_ESPECES,
    QUITTANCE_EMISE,
    RECU_EMIS,
    /** Rappel avant l'échéance (J-5…). */
    RAPPEL_ECHEANCE,
    /** Relance d'un loyer impayé (J+3, J+10, J+30…). */
    RELANCE_IMPAYE,
    /** Une mise en demeure vient d'être émise. */
    MISE_EN_DEMEURE,
    TEST
}
