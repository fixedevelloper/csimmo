package com.immogest.app.shared;

import org.springframework.http.HttpStatus;

/** Erreur métier avec un code stable (ex. {@code BAIL_LOT_DEJA_OCCUPE}) traduit côté front. */
public class ErreurMetier extends RuntimeException {

    private final String code;
    private final HttpStatus status;

    public ErreurMetier(String code, String message, HttpStatus status) {
        super(message);
        this.code = code;
        this.status = status;
    }

    public ErreurMetier(String code, String message) {
        this(code, message, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    public String code() {
        return code;
    }

    public HttpStatus status() {
        return status;
    }
}
