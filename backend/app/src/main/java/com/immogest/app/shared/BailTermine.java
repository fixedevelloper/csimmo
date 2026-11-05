package com.immogest.app.shared;

import java.util.Map;
import java.util.UUID;

/** Un bail est terminé (échéance, résiliation) : le lot redevient libre. Voir {@link BailActive}. */
public record BailTermine(UUID bailId, UUID lotId) implements EvenementMetier {

    @Override
    public String action() {
        return "BAIL.TERMINE";
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
