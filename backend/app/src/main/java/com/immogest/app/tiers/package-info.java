/**
 * Tiers : propriétaires tiers et leurs mandats, locataires, garants, prestataires, pièces d'identité (chiffrées).
 * Dépend de {@code securite} pour vérifier le rôle d'un compte rattaché à une fiche, et de {@code stockage} pour les pièces.
 */
@ApplicationModule(displayName = "Tiers", allowedDependencies = {"shared", "stockage", "securite"})
package com.immogest.app.tiers;

import org.springframework.modulith.ApplicationModule;
