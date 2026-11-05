package com.immogest.app.finance;

/** @param rejoue la requête portait une clé d'idempotence déjà traitée : c'est la réponse d'origine, rien n'a été créé */
public record ResultatPaiement(PaiementDto paiement, boolean rejoue) {
}
