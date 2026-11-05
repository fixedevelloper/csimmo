package com.immogest.app.tiers;

import java.util.UUID;

public record PrestataireSaisie(String nom, String metier, String telephone, String email, String adresse,
        UUID utilisateurId, String notes) {
}
