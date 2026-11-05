package com.immogest.licensing;

/**
 * @param statut  conclusion de la vérification
 * @param licence contenu de la licence, <b>uniquement si la signature est valide</b> (sinon {@code null} : un contenu
 *                non authentifié ne doit jamais être affiché ni utilisé)
 * @param detail  raison courte pour {@link StatutLicence#FORMAT_INVALIDE} (sans donnée sensible), sinon {@code null}
 */
public record ResultatVerification(StatutLicence statut, Licence licence, String detail) {

    public boolean estValide() {
        return statut == StatutLicence.VALIDE;
    }
}
