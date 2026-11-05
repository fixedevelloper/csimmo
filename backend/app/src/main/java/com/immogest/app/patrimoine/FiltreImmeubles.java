package com.immogest.app.patrimoine;

/** Filtres de la liste des immeubles ; tous facultatifs. {@code q} cherche dans le nom et l'adresse. */
public record FiltreImmeubles(String ville, String quartier, String q, boolean inclureArchives,
        java.util.UUID proprietaireTiersId) {
}
