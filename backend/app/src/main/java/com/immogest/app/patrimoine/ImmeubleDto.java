package com.immogest.app.patrimoine;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * @param nombreLots   lots non archivés
 * @param lotsLibres   dont libres
 * @param lotsOccupes  dont occupés
 * @param lotsEnTravaux dont en travaux
 */
public record ImmeubleDto(
        UUID id,
        String nom,
        String adresse,
        String quartier,
        String ville,
        BigDecimal latitude,
        BigDecimal longitude,
        String description,
        UUID proprietaireTiersId,
        boolean archive,
        int nombreLots,
        int lotsLibres,
        int lotsOccupes,
        int lotsEnTravaux) {

    public ImmeubleDto avecComptes(int libres, int occupes, int enTravaux) {
        return new ImmeubleDto(id, nom, adresse, quartier, ville, latitude, longitude, description, proprietaireTiersId, archive,
                libres + occupes + enTravaux, libres, occupes, enTravaux);
    }
}
