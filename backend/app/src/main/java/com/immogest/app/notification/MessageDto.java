package com.immogest.app.notification;

import java.time.OffsetDateTime;
import java.util.UUID;

/** Une ligne du journal des envois. */
public record MessageDto(UUID id, TypeCanal canal, TypeMessage typeMessage, String destinataire, String sujet, String corps,
        StatutMessage statut, int tentatives, OffsetDateTime prochaineTentative, String derniereErreur, OffsetDateTime envoyeLe,
        OffsetDateTime creeLe) {
}
