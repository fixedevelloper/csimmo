package com.immogest.app.patrimoine;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Création ou modification d'un lot. {@code immeubleId} est obligatoire à la création ; à la modification il doit être
 * absent ou identique (un lot ne change pas d'immeuble).
 */
public record LotSaisie(UUID immeubleId, String reference, TypeLot type, BigDecimal surfaceM2, Integer pieces,
        Long loyerIndicatif, List<String> equipements, List<CompteurSaisie> compteurs) {

    public record CompteurSaisie(TypeCompteur type, String numero) {
    }
}
