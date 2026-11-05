/**
 * Maintenance : à ce stade, les signalements de pannes faits par les locataires (description + photo) et leur suivi par les gestionnaires.
 * Devis, prestataires et clôture avec imputation viendront avec la V2.
 */
@ApplicationModule(displayName = "Maintenance", allowedDependencies = {"shared", "bail :: api", "tiers :: api", "patrimoine :: api", "stockage"})
package com.immogest.app.maintenance;

import org.springframework.modulith.ApplicationModule;
