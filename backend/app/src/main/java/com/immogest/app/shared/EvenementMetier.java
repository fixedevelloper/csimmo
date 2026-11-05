package com.immogest.app.shared;

import java.util.UUID;

/**
 * Événement métier journalisé par le module {@code audit}. Tout événement qui modifie des données
 * (création, annulation, contre-passation…) l'implémente.
 */
public interface EvenementMetier {

    /** Action effectuée, au format {@code MODULE.ACTION} (ex. {@code FINANCE.PAIEMENT_ENREGISTRE}). */
    String action();

    String entite();

    UUID entiteId();

    /** État avant l'action (sérialisable en JSON), ou {@code null}. */
    default Object avant() {
        return null;
    }

    /** État après l'action (sérialisable en JSON), ou {@code null}. */
    default Object apres() {
        return null;
    }

    default String auteur() {
        return "systeme";
    }
}
