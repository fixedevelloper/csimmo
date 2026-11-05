package com.immogest.app.licence.infra;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration {@code immogest.licence.*}.
 *
 * @param clePublique              clé publique de l'éditeur (base64) ; vide : clé embarquée dans le jar
 * @param serveurUrl               URL du serveur de licences ; vide : vérification en ligne et délai de grâce désactivés
 * @param identifiantMachineSecours identifiant machine de secours (dev) si aucun n'est détectable
 * @param graceJours               jours sans confirmation en ligne avant le mode lecture seule
 * @param verificationIntervalleJours intervalle entre deux vérifications en ligne réussies
 * @param alertesJours             seuils d'alerte avant l'expiration
 * @param devClePrivee             profil dev uniquement : clé qui signe la licence de développement
 */
@ConfigurationProperties("immogest.licence")
public record LicenceProperties(
        String clePublique,
        String serveurUrl,
        String identifiantMachineSecours,
        int graceJours,
        int verificationIntervalleJours,
        List<Integer> alertesJours,
        String devClePrivee) {

    public boolean verificationEnLigneActive() {
        return serveurUrl != null && !serveurUrl.isBlank();
    }
}
