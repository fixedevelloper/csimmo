/**
 * Import de données existantes (Excel .xlsx ou CSV) : immeubles, lots, locataires, baux en cours et soldes d'ouverture. Prévisualisation sans écriture
 * (validation ligne par ligne), puis import <b>tout ou rien</b> dans une seule transaction. Passe par les services publics des autres modules
 * (jamais leurs tables) : les règles métier (palier de licence, un bail en vigueur par lot…) s'appliquent donc comme à la saisie manuelle.
 * Le package s'appelle {@code importation} : {@code import} est un mot réservé de Java.
 */
@ApplicationModule(displayName = "Import", allowedDependencies = {"shared", "patrimoine", "tiers", "bail", "finance", "licence :: api"})
package com.immogest.app.importation;

import org.springframework.modulith.ApplicationModule;
