package com.immogest.app.tiers.api;

import java.util.UUID;

/** Identité d'un garant pour rédiger le bail (numéro de CNI déchiffré : voir {@link LocataireContrat}). */
public record GarantContrat(UUID id, String nom, String lien, String profession, String telephone, String adresse, String numeroCni) {
}
