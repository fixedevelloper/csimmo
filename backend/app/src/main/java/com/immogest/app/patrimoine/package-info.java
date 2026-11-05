/**
 * Patrimoine : immeubles, lots (équipements, compteurs) et photos. Le statut OCCUPE d'un lot ne change que par les
 * événements {@code BailActive} / {@code BailTermine} publiés par le module bail.
 */
@ApplicationModule(displayName = "Patrimoine", allowedDependencies = {"shared", "licence :: api", "stockage", "tiers :: api"})
package com.immogest.app.patrimoine;

import org.springframework.modulith.ApplicationModule;
