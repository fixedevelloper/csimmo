package com.immogest.app.importation;

import java.util.List;
import java.util.Map;

/** {@code importe} faux : rien n'a été écrit (tout ou rien), {@code erreurs} dit pourquoi. */
public record ResultatImportDto(boolean importe, Map<TypeImport, Integer> crees, int echeancesReprises, List<ErreurImportDto> erreurs) {
}
