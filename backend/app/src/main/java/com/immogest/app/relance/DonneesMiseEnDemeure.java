package com.immogest.app.relance;

import java.util.List;

/** Contenu déjà formaté de la mise en demeure (texte brut, échappé par Thymeleaf). */
public record DonneesMiseEnDemeure(String titre, String numero, List<String> corps, String titreTableau, List<Ligne> lignes, String note,
        String emission, String signature) {

    public record Ligne(String libelle, String valeur) {
    }
}
