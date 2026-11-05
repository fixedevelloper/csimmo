package com.immogest.app.notification.infra;

/** Échec d'envoi par un canal ; le message ne contient jamais de secret. */
public class EnvoiException extends Exception {

    public EnvoiException(String message) {
        super(message);
    }

    public EnvoiException(String message, Throwable cause) {
        super(message, cause);
    }
}
