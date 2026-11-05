package com.immogest.app.importation;

/** Une erreur de validation : {@code ligne} 0 = erreur de structure ou globale ; {@code colonne} peut être null. */
public record ErreurImportDto(String feuille, int ligne, String colonne, String code, String message) {
}
