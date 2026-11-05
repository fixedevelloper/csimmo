package com.immogest.licenseserver;

import org.springframework.http.HttpStatus;

/** Erreur métier avec un code stable (traduit côté portail). */
public class ErreurMetier extends RuntimeException {
    private final HttpStatus statut;
    private final String code;

    public ErreurMetier(HttpStatus statut, String code, String message) {
        super(message);
        this.statut = statut;
        this.code = code;
    }

    public HttpStatus statut() {
        return statut;
    }

    public String code() {
        return code;
    }
}
