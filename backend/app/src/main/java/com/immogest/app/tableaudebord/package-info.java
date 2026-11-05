/**
 * Tableaux de bord : agrégats en lecture seule (occupation, attendu vs encaissé, arriérés, baux à échéance) composés à partir des API
 * des autres modules. Aucune table propre, aucune écriture.
 */
@ApplicationModule(displayName = "Tableau de bord", allowedDependencies = {"shared", "patrimoine :: api", "finance", "bail :: api", "tiers :: api"})
package com.immogest.app.tableaudebord;

import org.springframework.modulith.ApplicationModule;
