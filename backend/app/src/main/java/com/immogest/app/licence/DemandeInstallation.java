package com.immogest.app.licence;

/**
 * Données de l'assistant de premier lancement.
 *
 * @param licence contenu du fichier {@code .lic} fourni par l'éditeur
 */
public record DemandeInstallation(String nom, String telephone, String email, String motDePasse, String licence) {
}
