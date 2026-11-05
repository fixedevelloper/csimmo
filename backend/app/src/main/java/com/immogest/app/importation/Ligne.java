package com.immogest.app.importation;

import java.util.Map;

/** Une ligne de données lue dans le fichier ; {@code numero} est le numéro de ligne tel que l'utilisateur le voit (en-tête = 1). */
record Ligne(int numero, Map<String, String> valeurs) {

    String get(String colonne) {
        String v = valeurs.get(colonne);
        return v == null || v.isBlank() ? null : v.strip();
    }
}
