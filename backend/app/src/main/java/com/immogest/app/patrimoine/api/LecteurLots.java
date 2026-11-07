package com.immogest.app.patrimoine.api;

import java.util.Optional;
import java.util.UUID;

/** Lecture seule des lots (CLAUDE.md §5.1 : appel synchrone permis pour une lecture dans la même transaction). */
public interface LecteurLots {

    Optional<LotResume> lot(UUID lotId);

    /** Plusieurs lots en une requête (évite une requête par lot dans les agrégats), par identifiant. Les identifiants inconnus sont ignorés. */
    java.util.Map<UUID, LotResume> lots(java.util.Collection<UUID> lotIds);

    Optional<ImmeubleResume> immeuble(UUID immeubleId);

    /** Nombre de lots non archivés par statut (LIBRE, OCCUPE, EN_TRAVAUX) ; un statut absent vaut 0. */
    java.util.Map<String, Long> compterParStatut();

    /** Identifiants de tous les lots (archivés compris) des immeubles donnés : base du périmètre des employés restreints. */
    java.util.Set<UUID> lotIdsDesImmeubles(java.util.Collection<UUID> immeubleIds);
}
