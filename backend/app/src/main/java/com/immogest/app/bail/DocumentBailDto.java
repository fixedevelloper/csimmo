package com.immogest.app.bail;

import java.time.OffsetDateTime;
import java.util.UUID;

/** @param fichierId à télécharger par {@code GET /api/v1/fichiers/{fichierId}} (PDF chiffré au repos, contrôle d'accès par l'API) */
public record DocumentBailDto(UUID id, UUID bailId, UUID fichierId, int version, OffsetDateTime genereLe) {
}
