package com.immogest.app.finance;

/** Modes d'encaissement manuel. Mobile Money automatique (webhook) : module {@code paiementenligne}, plus tard. */
public enum ModePaiement {
    ESPECES(false), VIREMENT(true), MTN_MOMO(true), ORANGE_MONEY(true);

    private final boolean referenceObligatoire;

    ModePaiement(boolean referenceObligatoire) {
        this.referenceObligatoire = referenceObligatoire;
    }

    public boolean referenceObligatoire() {
        return referenceObligatoire;
    }
}
