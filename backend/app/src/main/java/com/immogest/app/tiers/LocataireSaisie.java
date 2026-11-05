package com.immogest.app.tiers;

import java.time.LocalDate;
import java.util.UUID;

public record LocataireSaisie(String nom, String profession, String telephone, String email, String adresse,
        LocalDate dateNaissance, String numeroCni, UUID utilisateurId, String notes) {
}
