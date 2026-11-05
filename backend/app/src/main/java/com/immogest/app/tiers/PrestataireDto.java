package com.immogest.app.tiers;

import java.util.UUID;

public record PrestataireDto(UUID id, String nom, String metier, String telephone, String email, String adresse,
        UUID utilisateurId, String notes, boolean archive) {
}
