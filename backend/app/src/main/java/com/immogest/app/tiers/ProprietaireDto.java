package com.immogest.app.tiers;

import java.util.UUID;

public record ProprietaireDto(UUID id, String nom, String telephone, String email, String adresse,
        ModeReversement modeReversement, String coordonneesReversement, UUID utilisateurId, String notes, boolean archive) {
}
