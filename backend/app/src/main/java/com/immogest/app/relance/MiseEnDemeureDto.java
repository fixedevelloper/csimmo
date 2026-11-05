package com.immogest.app.relance;

import java.time.LocalDate;
import java.util.UUID;

/** @param fichierId PDF à télécharger par {@code GET /api/v1/fichiers/{fichierId}} */
public record MiseEnDemeureDto(UUID id, String numero, UUID bailId, String bailNumero, UUID echeanceId, String locataireNom,
        long montantArrieres, int delaiJours, LocalDate dateEmission, UUID fichierId) {
}
