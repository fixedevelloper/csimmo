package com.immogest.app.bail;

public enum StatutBail {
    BROUILLON, ACTIF, RESILIE, TERMINE;

    /** Un bail ACTIF ou RESILIE occupe le lot sur sa période. */
    public boolean occupeLeLot() {
        return this == ACTIF || this == RESILIE;
    }
}
