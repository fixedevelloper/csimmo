package com.immogest.app.shared;

import java.util.UUID;

/**
 * Contrôle d'accès aux fichiers, implémenté par chaque module propriétaire de fichiers.
 * Le module {@code stockage} délègue à l'implémentation dont {@link #module()} correspond au fichier ;
 * sans implémentation, l'accès est refusé.
 */
public interface ControleAccesFichier {

    /** Nom du module propriétaire (ex. {@code patrimoine}). */
    String module();

    boolean peutLire(String entite, UUID entiteId, Acteur acteur);
}
