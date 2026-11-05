package com.immogest.licensing;

import java.security.PublicKey;
import java.time.LocalDate;

/**
 * Vérification hors ligne d'un fichier de licence. Ordre des contrôles : format → signature → serveur → dates.
 * Une licence falsifiée est donc toujours signalée {@link StatutLicence#SIGNATURE_INVALIDE}, même si elle est aussi
 * expirée, et son contenu n'est jamais renvoyé.
 */
public final class VerificateurLicence {

    private VerificateurLicence() {
    }

    /**
     * @param contenu          contenu du fichier {@code .lic}
     * @param clePublique      clé publique de l'éditeur (embarquée dans l'application)
     * @param empreinteLocale  empreinte de ce serveur (voir {@link EmpreinteServeur})
     * @param aujourdhui       date du jour (fournie par l'appelant : fuseau {@code Africa/Douala})
     */
    public static ResultatVerification verifier(String contenu, PublicKey clePublique, String empreinteLocale,
            LocalDate aujourdhui) {
        LicenceSignee signee;
        try {
            signee = LicenceSignee.lire(contenu);
        } catch (LicenceInvalideException e) {
            return new ResultatVerification(StatutLicence.FORMAT_INVALIDE, null, e.getMessage());
        }
        if (!signee.signatureValide(clePublique)) {
            return new ResultatVerification(StatutLicence.SIGNATURE_INVALIDE, null, null);
        }
        Licence licence = signee.licence();
        if (empreinteLocale == null || !licence.empreinteServeur().equalsIgnoreCase(empreinteLocale.strip())) {
            return new ResultatVerification(StatutLicence.MAUVAISE_EMPREINTE, licence, null);
        }
        if (aujourdhui.isBefore(licence.dateDebut())) {
            return new ResultatVerification(StatutLicence.PAS_ENCORE_VALIDE, licence, null);
        }
        if (licence.estExpireeAu(aujourdhui)) {
            return new ResultatVerification(StatutLicence.EXPIREE, licence, null);
        }
        return new ResultatVerification(StatutLicence.VALIDE, licence, null);
    }
}
