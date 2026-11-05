package com.immogest.app.sauvegarde;

import java.util.List;

/** @param operation dernière opération connue (null si aucune) ; @param heureQuotidienne heure de la sauvegarde automatique */
public record EtatSauvegardesDto(List<SauvegardeDto> sauvegardes, OperationSauvegardeDto operation, String heureQuotidienne, String versionInstallee) {
}
