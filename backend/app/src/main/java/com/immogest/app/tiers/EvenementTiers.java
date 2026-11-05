package com.immogest.app.tiers;

import com.immogest.app.shared.EvenementMetier;
import java.util.Map;
import java.util.UUID;

/** Création, modification, archivage ou pièce ajoutée : journalisé par {@code audit}. Sans donnée d'identité en clair. */
public record EvenementTiers(String action, String entite, UUID entiteId, Map<String, Object> avant,
        Map<String, Object> apres, String auteur) implements EvenementMetier {
}
