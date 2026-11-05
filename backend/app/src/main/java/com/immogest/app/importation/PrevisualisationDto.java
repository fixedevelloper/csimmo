package com.immogest.app.importation;

import java.util.List;
import java.util.Map;

/**
 * @param empreinte SHA-256 du fichier analysé : l'import refuse un fichier différent de celui qui a été prévisualisé
 * @param feuilles  par type : lignes lues et nombre d'objets qui seraient créés
 * @param apercu    les premières lignes de chaque type (valeurs telles que lues)
 * @param importable vrai seulement s'il n'y a aucune erreur et au moins une ligne
 */
public record PrevisualisationDto(String empreinte, List<ResumeFeuilleDto> feuilles, List<ErreurImportDto> erreurs, Map<String, List<Map<String, String>>> apercu, boolean importable) {

    public record ResumeFeuilleDto(TypeImport type, String feuille, int lignes) {
    }
}
