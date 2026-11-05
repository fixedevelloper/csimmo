package com.immogest.app.tiers.api;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Identité d'un locataire pour rédiger son bail. Contient le numéro de CNI déchiffré : à n'utiliser que pour produire
 * un document destiné aux personnes autorisées, jamais pour une liste ni pour un journal.
 */
public record LocataireContrat(UUID id, String nom, String profession, String telephone, String email, String adresse,
        LocalDate dateNaissance, String numeroCni) {
}
