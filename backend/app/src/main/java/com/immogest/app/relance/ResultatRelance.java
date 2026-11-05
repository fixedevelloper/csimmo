package com.immogest.app.relance;

public enum ResultatRelance {
    /** Message mis en file d'envoi (l'état d'envoi réel est lu dans le journal des notifications). */
    EN_FILE,
    /** Aucun canal utilisable (canal inactif ou contact inconnu) : visible dans l'historique. */
    AUCUN_CANAL,
    /** Niveau sauté : un niveau supérieur était déjà atteint (rattrapage) ou l'échéance est trop ancienne. */
    IGNOREE
}
