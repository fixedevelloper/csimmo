package com.immogest.app.patrimoine;

import java.util.UUID;

/** @param fichierId à télécharger par {@code GET /api/v1/fichiers/{fichierId}} (contrôle d'accès par l'API) */
public record PhotoDto(UUID id, CibleTypePhoto cibleType, UUID cibleId, UUID fichierId, String legende, int ordre) {
}
