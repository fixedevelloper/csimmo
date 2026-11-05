package com.immogest.app.sauvegarde;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Diagnostic de l'installation, sans donnée sensible.
 *
 * @param alertes codes stables traduits par l'interface : SAUVEGARDE_ABSENTE, SAUVEGARDE_ANCIENNE, COPIE_EXTERNE_ABSENTE, COPIE_EXTERNE_ECHEC,
 *                DISQUE_FAIBLE, LICENCE_INVALIDE, LICENCE_PROCHE, DERNIERE_OPERATION_ECHEC
 */
public record DiagnosticDto(String versionApplication, String versionJava, String versionBase, long tailleBaseOctets, long disqueTotalOctets, long disqueLibreOctets,
        String etatLicence, Long joursLicenceRestants, OffsetDateTime derniereSauvegarde, Long ageDerniereSauvegardeHeures, String copieExterne, List<String> alertes) {
}
