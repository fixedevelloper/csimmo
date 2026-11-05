package com.immogest.app.tiers.api;

import java.time.LocalDate;
import java.util.UUID;

/** @param tauxHonorairesBp points de base : 1000 = 10 % (CLAUDE.md §6) */
public record MandatResume(UUID id, UUID proprietaireId, int tauxHonorairesBp, LocalDate dateDebut, LocalDate dateFin) {
}
