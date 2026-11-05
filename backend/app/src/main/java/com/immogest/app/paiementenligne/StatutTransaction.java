package com.immogest.app.paiementenligne;

public enum StatutTransaction {
    /** Demande acceptée par l'agrégateur, le locataire doit valider sur son téléphone. */
    EN_ATTENTE,
    /** L'opérateur a confirmé : le paiement existe et est imputé. */
    CONFIRMEE,
    ECHOUEE,
    /** Sans confirmation au bout de 24 h. Si l'opérateur confirme plus tard, la transaction passe quand même CONFIRMEE (l'argent a bougé). */
    EXPIREE,
    /** L'opérateur annonce un montant différent de celui demandé : aucun paiement créé, à traiter à la main. */
    ANOMALIE;

    public boolean finale() {
        return this != EN_ATTENTE;
    }
}
