package com.immogest.app.patrimoine;

import java.util.UUID;

/** Filtres de la liste des lots ; tous facultatifs. {@code q} cherche dans la référence et le nom de l'immeuble. */
public record FiltreLots(UUID immeubleId, StatutLot statut, TypeLot type, String ville, String quartier, String q,
        boolean inclureArchives, UUID proprietaireTiersId) {
}
