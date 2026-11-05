package com.immogest.app.shared;

import java.util.Map;
import java.util.UUID;

/**
 * Un bail est devenu actif sur un lot. Publié par le module {@code bail}, consommé par {@code patrimoine} (le lot passe
 * en OCCUPE). Défini dans {@code shared} : le consommateur n'a ainsi pas à dépendre du producteur, qui dépend déjà de lui.
 */
public record BailActive(UUID bailId, UUID lotId) implements EvenementMetier {

    @Override
    public String action() {
        return "BAIL.ACTIVE";
    }

    @Override
    public String entite() {
        return "bail";
    }

    @Override
    public UUID entiteId() {
        return bailId;
    }

    @Override
    public Object apres() {
        return Map.of("lotId", lotId.toString());
    }
}
