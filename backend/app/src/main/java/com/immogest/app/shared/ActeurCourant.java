package com.immogest.app.shared;

import java.util.Optional;

/** Fournit l'acteur de la requête en cours ; implémenté par le module {@code securite}. */
public interface ActeurCourant {

    Optional<Acteur> acteur();
}
