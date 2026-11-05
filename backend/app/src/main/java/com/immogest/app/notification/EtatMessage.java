package com.immogest.app.notification;

/** Canal utilisé et état d'envoi d'un message de la file. */
public record EtatMessage(StatutMessage statut, TypeCanal canal) {
}
