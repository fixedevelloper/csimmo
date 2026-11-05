package com.immogest.app.maintenance;

import java.time.OffsetDateTime;
import java.util.UUID;

/** @param photoFichierId photo à télécharger par {@code GET /api/v1/fichiers/{id}} (contrôle d'accès par l'API) ; {@code null} sans photo */
public record SignalementDto(UUID id, UUID bailId, String bailNumero, UUID lotId, String logement, UUID locataireId, String locataireNom,
        String description, StatutSignalement statut, UUID photoFichierId, String noteGestion, OffsetDateTime creeLe) {
}
