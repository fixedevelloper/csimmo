package com.immogest.app.tiers;

import java.time.LocalDate;

public record MandatSaisie(Integer tauxHonorairesBp, LocalDate dateDebut, LocalDate dateFin, String notes) {
}
