/**
 * Licence annuelle : installation, vérification (hors ligne puis en ligne), mode lecture seule, palier de lots.
 * Dépend de {@code securite} car l'assistant de premier lancement crée l'ADMIN et importe la licence dans une
 * même transaction.
 */
@ApplicationModule(displayName = "Licence", allowedDependencies = {"shared", "securite"})
package com.immogest.app.licence;

import org.springframework.modulith.ApplicationModule;
