package com.immogest.app.sauvegarde;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @param repertoire         dossier des sauvegardes (monté en lecture seule)
 * @param demandes           dossier d'échange avec le conteneur de sauvegarde (demandes, état de l'opération en cours)
 * @param heure              heure de la sauvegarde quotidienne (affichage)
 * @param version            version installée de l'application
 * @param redemarrageSurDemande si vrai, l'application s'arrête (et Docker la relance) quand une restauration vient de remplacer sa base
 */
@ConfigurationProperties("immogest.sauvegarde")
public record SauvegardeProperties(String repertoire, String demandes, String heure, String version, boolean redemarrageSurDemande) {

    public SauvegardeProperties {
        repertoire = repertoire == null || repertoire.isBlank() ? "/sauvegardes" : repertoire;
        demandes = demandes == null || demandes.isBlank() ? "/demandes" : demandes;
        heure = heure == null || heure.isBlank() ? "02:30" : heure;
        version = version == null || version.isBlank() ? "dev" : version;
    }
}
