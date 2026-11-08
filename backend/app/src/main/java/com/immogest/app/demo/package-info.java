/**
 * Jeu de données de démonstration (profil {@code demo}, jamais en production) : immeubles, lots, propriétaires, locataires, baux,
 * paiements, dépenses, employés restreints et reversements, pour développer, présenter et tester de bout en bout.
 */
@ApplicationModule(displayName = "Démonstration", allowedDependencies = {"shared", "securite", "patrimoine", "tiers", "bail", "finance"})
package com.immogest.app.demo;

import org.springframework.modulith.ApplicationModule;
