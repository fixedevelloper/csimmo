package com.immogest.app.licence.infra;

import java.util.Optional;

/** Empreinte de ce serveur (voir {@code EmpreinteServeur}). Interface pour la fixer dans les tests. */
public interface FournisseurEmpreinte {

    /** Vide si aucun identifiant machine n'est détectable. */
    Optional<String> empreinte(String identifiantInstallation);
}
