package com.immogest.app.patrimoine;

import com.immogest.app.shared.EvenementMetier;
import java.util.Map;
import java.util.UUID;

/** Création, modification ou archivage d'un immeuble, d'un lot ou d'une photo : journalisé par {@code audit}. */
public record EvenementPatrimoine(String action, String entite, UUID entiteId, Map<String, Object> avant,
        Map<String, Object> apres, String auteur) implements EvenementMetier {
}
