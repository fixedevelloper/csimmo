package com.immogest.app.shared;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Locataires visibles par l'utilisateur de la requête (ceux qui ont ou ont eu un bail dans son périmètre) ; implémenté par le module
 * {@code bail}, ce qui évite un cycle {@code tiers → bail → tiers}. Vide : aucune restriction.
 */
public interface LocatairesDuPerimetre {

    Optional<Set<UUID>> locatairesVisibles();
}
