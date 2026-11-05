package com.immogest.app.shared;

import java.util.Map;
import java.util.UUID;

/**
 * Une personne vient d'être anonymisée (droit à l'effacement, loi 2024/017). Les modules qui détiennent des données personnelles liées (comptes,
 * messages envoyés, journal d'audit) l'écoutent et les effacent à leur tour ; l'événement est livré même après un redémarrage (registre de
 * publication). {@code type} : LOCATAIRE, GARANT, PROPRIETAIRE ou PRESTATAIRE ; {@code utilisateurId} : compte rattaché, ou null.
 */
public record PersonneAnonymisee(String type, UUID personneId, UUID utilisateurId, String par) implements EvenementMetier {

    @Override
    public String action() {
        return "CONFORMITE.PERSONNE_ANONYMISEE";
    }

    @Override
    public String entite() {
        return type.toLowerCase();
    }

    @Override
    public UUID entiteId() {
        return personneId;
    }

    @Override
    public Object apres() {
        return Map.of("anonymise", true);
    }

    @Override
    public String auteur() {
        return par;
    }
}
