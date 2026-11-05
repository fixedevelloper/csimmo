package com.immogest.app.tiers;

import java.util.UUID;

/** Résultat de la recherche rapide (nom ou téléphone) parmi les propriétaires, locataires et prestataires. */
public record ResultatRecherche(String type, UUID id, String nom, String telephone) {
}
