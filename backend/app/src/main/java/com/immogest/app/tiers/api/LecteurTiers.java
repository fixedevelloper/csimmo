package com.immogest.app.tiers.api;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Lecture seule des tiers (CLAUDE.md §5.1 : appel synchrone permis pour une lecture). */
public interface LecteurTiers {

    /** Fiche de propriétaire tiers rattachée à ce compte utilisateur (rôle PROPRIETAIRE_TIERS), si elle existe. */
    Optional<UUID> proprietaireDeUtilisateur(UUID utilisateurId);

    /** Nom d'un propriétaire tiers (archivé ou non). */
    Optional<String> nomProprietaire(UUID proprietaireId);

    /** Vrai si la fiche existe et n'est pas archivée. */
    boolean proprietaireActif(UUID proprietaireId);

    Optional<LocataireResume> locataire(UUID locataireId);

    /** Fiche de locataire rattachée à ce compte utilisateur (rôle LOCATAIRE), si elle existe. */
    Optional<UUID> locataireDeUtilisateur(UUID utilisateurId);

    /** Identifiants des locataires dont le nom ou le téléphone contient {@code q} (200 au plus, archivés exclus). */
    java.util.List<UUID> rechercherLocataires(String q);

    /** Compte utilisateur rattaché à la fiche d'un locataire (pour les notifications push). */
    Optional<UUID> utilisateurDuLocataire(UUID locataireId);

    /** Fiche complète (CNI déchiffrée) d'un locataire non archivé, pour son bail. */
    Optional<LocataireContrat> locatairePourContrat(UUID locataireId);

    /**
     * Garants NON archivés de ce locataire parmi {@code garantIds}. Un identifiant qui n'est pas un garant actif de ce
     * locataire est simplement absent du résultat : l'appelant compare les tailles pour détecter l'anomalie.
     */
    List<GarantContrat> garantsPourContrat(UUID locataireId, Collection<UUID> garantIds);

    /** Mandat en vigueur à la date donnée pour ce propriétaire (honoraires du reversement). */
    Optional<MandatResume> mandatEnVigueur(UUID proprietaireId, LocalDate date);

    /** Vrai si le locataire est visible par l'utilisateur de la requête (employé restreint : bail dans son périmètre ou fiche créée par lui). */
    boolean locataireVisible(UUID locataireId);
}
