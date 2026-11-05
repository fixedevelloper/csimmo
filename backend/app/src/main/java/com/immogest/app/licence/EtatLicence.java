package com.immogest.app.licence;

/** État de la licence de cette installation. Seul {@link #VALIDE} autorise les écritures. */
public enum EtatLicence {
    /** Aucune licence importée. */
    AUCUNE,
    VALIDE,
    EXPIREE,
    /** Fichier altéré, signature fausse, licence d'un autre serveur ou pas encore commencée. */
    INVALIDE,
    /** Le serveur de licences l'a révoquée. */
    REVOQUEE,
    /** Plus de confirmation en ligne depuis plus de 30 jours (délai de grâce dépassé). */
    VERIFICATION_REQUISE;

    public boolean lectureSeule() {
        return this != VALIDE;
    }
}
