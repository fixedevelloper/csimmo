package com.immogest.app.finance;

public enum StatutEcheance {
    A_VENIR, DUE, PARTIELLE, PAYEE, EN_RETARD,
    /** Période postérieure à la sortie du locataire, sans aucun paiement : conservée pour la traçabilité, jamais due. */
    ANNULEE
}
