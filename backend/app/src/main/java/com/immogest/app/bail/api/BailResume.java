package com.immogest.app.bail.api;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Conditions d'un bail pour le calcul des échéances (montants en FCFA entiers).
 *
 * @param dateFinEffective dernier jour d'effet : la date de sortie si le bail est résilié, sinon la fin prévue ({@code null} = sans limite)
 * @param statut           BROUILLON, ACTIF, RESILIE ou TERMINE
 */
public record BailResume(UUID id, String numero, UUID lotId, UUID locataireId, String statut, LocalDate dateDebut,
        LocalDate dateFinEffective, long loyer, long charges, String periodicite, int jourEcheance, long caution, int moisAvance) {
}
