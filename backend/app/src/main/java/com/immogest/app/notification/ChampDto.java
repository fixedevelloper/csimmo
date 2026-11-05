package com.immogest.app.notification;

/**
 * Champ de configuration d'un canal. {@code valeur} n'est renseignée que pour les champs non secrets ; un secret n'est jamais
 * renvoyé, seulement {@code renseigne}.
 */
public record ChampDto(String nom, boolean secret, boolean requis, String valeur, boolean renseigne) {
}
