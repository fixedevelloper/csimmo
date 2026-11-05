package com.immogest.licensing;

/** Fichier de licence illisible ou mal formé (jamais levée pour une simple expiration). */
public class LicenceInvalideException extends RuntimeException {

    public LicenceInvalideException(String message) {
        super(message);
    }

    public LicenceInvalideException(String message, Throwable cause) {
        super(message, cause);
    }
}
