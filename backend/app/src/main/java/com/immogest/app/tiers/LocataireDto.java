package com.immogest.app.tiers;

import java.time.LocalDate;
import java.util.UUID;

/** @param numeroCni renseigné seulement sur la fiche détaillée (jamais dans les listes) */
public record LocataireDto(UUID id, String nom, String profession, String telephone, String email, String adresse,
        LocalDate dateNaissance, String numeroCni, UUID utilisateurId, String notes, boolean archive) {
}
