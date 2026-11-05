package com.immogest.app.bail;

public enum Periodicite {
    MENSUELLE(1), TRIMESTRIELLE(3), ANNUELLE(12);

    private final int mois;

    Periodicite(int mois) {
        this.mois = mois;
    }

    public int mois() {
        return mois;
    }
}
