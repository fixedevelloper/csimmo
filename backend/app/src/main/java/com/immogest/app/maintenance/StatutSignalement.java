package com.immogest.app.maintenance;

public enum StatutSignalement {
    NOUVEAU, EN_COURS, RESOLU, REJETE;

    public boolean terminal() {
        return this == RESOLU || this == REJETE;
    }
}
