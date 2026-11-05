package com.immogest.app.patrimoine.api;

import java.util.UUID;

/** Vue minimale d'un lot pour les autres modules. */
public record LotResume(
        UUID id,
        UUID immeubleId,
        String reference,
        String statut,
        Long loyerIndicatif,
        boolean archive,
        String type,
        java.math.BigDecimal surfaceM2,
        Integer pieces,
        String immeubleNom,
        String adresse,
        String quartier,
        String ville,
        java.util.UUID proprietaireTiersId) {
}
