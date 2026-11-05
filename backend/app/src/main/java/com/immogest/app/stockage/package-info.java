/**
 * Stockage de fichiers compatible S3 : validation, redimensionnement, chiffrement des fichiers sensibles,
 * métadonnées et téléchargement avec contrôle d'accès.
 */
@ApplicationModule(displayName = "Stockage", allowedDependencies = "shared")
package com.immogest.app.stockage;

import org.springframework.modulith.ApplicationModule;
