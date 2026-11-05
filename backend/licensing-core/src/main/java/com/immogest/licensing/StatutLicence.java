package com.immogest.licensing;

/** Résultat d'une vérification, du plus grave (fichier inutilisable) au plus bénin. */
public enum StatutLicence {
    VALIDE,
    /** Fichier illisible, incomplet ou de format inconnu. */
    FORMAT_INVALIDE,
    /** La signature ne correspond pas au contenu (fichier modifié, ou signé par une autre clé). */
    SIGNATURE_INVALIDE,
    /** Licence authentique mais émise pour un autre serveur. */
    MAUVAISE_EMPREINTE,
    /** Licence authentique mais pas encore commencée. */
    PAS_ENCORE_VALIDE,
    /** Licence authentique mais expirée : l'application passe en lecture seule. */
    EXPIREE
}
