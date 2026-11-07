package com.immogest.app.shared;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Périmètre d'immeubles de l'utilisateur de la requête ; implémenté par le module {@code securite}.
 * Chaque module en déduit ce qu'il montre (lots, baux, paiements…) : voir docs/adr/0009-equipe-et-perimetres.md.
 */
public interface PerimetreAcces {

    /**
     * Vide : aucune restriction à ce niveau (ADMIN, GESTIONNAIRE, tâches système, ou rôle qui a sa propre règle comme le locataire).
     * Sinon : les immeubles visibles ; un ensemble vide signifie que l'utilisateur ne voit rien.
     */
    Optional<Set<UUID>> immeublesVisibles();
}
