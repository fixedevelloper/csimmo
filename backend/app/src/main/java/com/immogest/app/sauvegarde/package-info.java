/**
 * Sauvegardes et diagnostic. Les sauvegardes elles-mêmes sont faites par le conteneur « sauvegarde » (pg_dump + rclone, voir deploy/) ;
 * ce module les LIT (manifestes en lecture seule), dépose des demandes (sauvegarde immédiate, restauration) dans un dossier partagé et
 * expose l'état et le diagnostic à l'administrateur. L'application ne manipule jamais elle-même la base ni les fichiers à restaurer.
 * Exception assumée à « pas de fichier sur disque local » (CLAUDE.md §9) : ce sont des signaux entre conteneurs, pas des données métier.
 */
@ApplicationModule(displayName = "Sauvegardes", allowedDependencies = {"shared", "licence"})
package com.immogest.app.sauvegarde;

import org.springframework.modulith.ApplicationModule;
