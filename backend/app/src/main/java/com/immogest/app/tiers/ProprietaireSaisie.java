package com.immogest.app.tiers;

import java.util.UUID;

public record ProprietaireSaisie(String nom, String telephone, String email, String adresse, ModeReversement modeReversement,
        String coordonneesReversement, UUID utilisateurId, String notes) {
}
