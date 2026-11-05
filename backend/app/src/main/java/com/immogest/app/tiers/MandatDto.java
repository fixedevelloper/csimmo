package com.immogest.app.tiers;

import java.time.LocalDate;
import java.util.UUID;

/** @param tauxHonorairesBp points de base : 1000 = 10 % */
public record MandatDto(UUID id, UUID proprietaireId, int tauxHonorairesBp, LocalDate dateDebut, LocalDate dateFin, String notes) {
}
