package com.immogest.app.patrimoine;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/** @param loyerIndicatif FCFA entiers */
public record LotDto(
        UUID id,
        UUID immeubleId,
        String immeubleNom,
        String ville,
        String quartier,
        String reference,
        TypeLot type,
        BigDecimal surfaceM2,
        Integer pieces,
        Long loyerIndicatif,
        StatutLot statut,
        List<String> equipements,
        List<CompteurDto> compteurs,
        boolean archive) {
}
