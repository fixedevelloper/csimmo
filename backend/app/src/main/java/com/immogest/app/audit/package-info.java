/**
 * Journal d'audit : écoute les événements métier de tous les modules et les enregistre en ajout seul.
 */
@ApplicationModule(displayName = "Audit", allowedDependencies = "shared")
package com.immogest.app.audit;

import org.springframework.modulith.ApplicationModule;
