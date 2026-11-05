package com.immogest.app.finance.infra;

import java.util.List;

/** Contenu déjà formaté d'une quittance ou d'un reçu (les textes sont échappés par Thymeleaf à l'affichage). */
public record DonneesDocument(String titre, String numero, List<String> corps, String titreTableau, List<Ligne> lignes, String note,
        String emission) {

    public record Ligne(String libelle, String valeur) {
    }
}
