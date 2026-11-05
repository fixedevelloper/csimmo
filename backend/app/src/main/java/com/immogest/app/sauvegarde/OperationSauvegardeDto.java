package com.immogest.app.sauvegarde;

import java.time.OffsetDateTime;

/** Dernière opération du conteneur de sauvegarde. {@code operation} : sauvegarde ou restauration ; {@code statut} : EN_COURS, OK ou ECHEC. */
public record OperationSauvegardeDto(String operation, String statut, String id, OffsetDateTime debut, OffsetDateTime fin, String message) {
}
