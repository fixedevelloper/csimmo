package com.immogest.app.paiementenligne;

import java.util.List;

/** @param configure tous les champs obligatoires sont renseignés (le fournisseur peut être activé) */
public record FournisseurDto(String code, boolean actif, boolean configure, List<ChampDto> champs) {

    /** Un secret n'est jamais renvoyé, seulement {@code renseigne}. */
    public record ChampDto(String nom, boolean secret, boolean requis, String valeur, boolean renseigne) {
    }
}
