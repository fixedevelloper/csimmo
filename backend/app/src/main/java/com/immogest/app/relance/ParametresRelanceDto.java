package com.immogest.app.relance;

import java.util.List;

/** @param niveaux jours par rapport à l'échéance : -5 = 5 jours avant, 30 = 30 jours après (le dernier déclenche la possibilité de mise en demeure) */
public record ParametresRelanceDto(boolean actif, List<Integer> niveaux, int delaiMiseEnDemeureJours) {
}
