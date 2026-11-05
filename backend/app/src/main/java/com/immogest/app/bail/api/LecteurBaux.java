package com.immogest.app.bail.api;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Lecture seule des baux (CLAUDE.md §5.1 : appel synchrone permis pour une lecture). */
public interface LecteurBaux {

    Optional<BailResume> bail(UUID bailId);

    /** Plusieurs baux en une requête (évite une requête par bail dans les agrégats). Les identifiants inconnus sont ignorés. */
    List<BailResume> baux(java.util.Collection<UUID> bailIds);

    /** Baux ACTIF ou RESILIE en vigueur à la date donnée (base du job d'échéances). */
    List<BailResume> bauxEnVigueur(LocalDate date);

    /** Baux ACTIF ou RESILIE dont le dernier jour d'effet tombe entre {@code du} et {@code au} inclus. */
    List<BailResume> bauxArrivantAEcheance(LocalDate du, LocalDate au);

    /** Nom du bailleur tel que saisi dans le modèle de bail (pour les documents émis). */
    String nomBailleur();

    /** Tous les baux signés (hors brouillon) d'un locataire. */
    List<BailResume> bauxDuLocataire(UUID locataireId);

    /** Baux signés (ACTIF, RESILIE ou TERMINE) dont le début est au plus tard à la date donnée : base de la génération des échéances. */
    List<BailResume> bauxSignesCommencesAvant(LocalDate date);
}
