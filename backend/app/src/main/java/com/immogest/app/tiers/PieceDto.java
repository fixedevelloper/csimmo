package com.immogest.app.tiers;

import java.util.UUID;

/** @param fichierId à télécharger par {@code GET /api/v1/fichiers/{fichierId}} (déchiffré par l'API après contrôle des droits) */
public record PieceDto(UUID id, CiblePiece cibleType, UUID cibleId, UUID fichierId, TypePiece typePiece) {
}
