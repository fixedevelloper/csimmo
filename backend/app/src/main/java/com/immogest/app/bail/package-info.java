/**
 * Baux : création, activation, renouvellement, résiliation avec préavis, génération du bail en PDF.
 * Règle n°1 : un lot a au plus un bail en vigueur à une date donnée (service ET contrainte d'exclusion en base).
 */
@ApplicationModule(displayName = "Baux", allowedDependencies = {"shared", "patrimoine :: api", "tiers :: api", "stockage"})
package com.immogest.app.bail;

import org.springframework.modulith.ApplicationModule;
