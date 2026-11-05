package com.immogest.app.relance;

/** @param envoyees relances mises en file ; @param sansCanal faute de canal disponible ; @param ignorees niveaux sautés */
public record BilanRelances(int examinees, int envoyees, int sansCanal, int ignorees) {
}
