package com.immogest.app.licence;

/**
 * Client du serveur de licences de l'éditeur (vérification en ligne hebdomadaire). Interface pour pouvoir la simuler :
 * aucun appel réseau réel dans les tests.
 */
public interface ServeurLicencesClient {

    /** @throws ServeurLicencesIndisponibleException si le serveur est injoignable ou répond une erreur */
    ReponseVerification verifier(DemandeVerification demande) throws ServeurLicencesIndisponibleException;

    record DemandeVerification(String licenseId, String empreinteServeur, String versionApplication) {
    }

    /**
     * @param statut            avis du serveur sur la licence
     * @param licenceRenouvelee contenu d'un nouveau fichier {@code .lic} si la licence a été renouvelée (facultatif)
     */
    record ReponseVerification(Statut statut, String licenceRenouvelee) {

        public enum Statut {
            VALIDE,
            REVOQUEE,
            /** Licence inconnue du serveur : ne compte pas comme une confirmation. */
            INCONNUE
        }
    }

    class ServeurLicencesIndisponibleException extends Exception {
        public ServeurLicencesIndisponibleException(String message) {
            super(message);
        }

        public ServeurLicencesIndisponibleException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
