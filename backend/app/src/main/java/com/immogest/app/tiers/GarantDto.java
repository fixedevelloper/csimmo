package com.immogest.app.tiers;

import java.util.UUID;

public record GarantDto(UUID id, UUID locataireId, String nom, String lien, String profession, String telephone,
        String adresse, String numeroCni, boolean archive) {
}
