package com.immogest.app.patrimoine;

import java.math.BigDecimal;

public record ImmeubleSaisie(String nom, String adresse, String quartier, String ville, BigDecimal latitude,
        BigDecimal longitude, String description, java.util.UUID proprietaireTiersId) {
}
