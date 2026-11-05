package com.immogest.app.finance;

/** Résultat d'un passage du moteur. */
public record BilanGeneration(int baux, int echeancesCreees, int echeancesAjustees, int avancesImputees, int statutsMisAJour) {
}
