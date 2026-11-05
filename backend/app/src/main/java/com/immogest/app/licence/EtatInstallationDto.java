package com.immogest.app.licence;

/**
 * @param installee         l'assistant de premier lancement a déjà été passé
 * @param empreinteServeur  empreinte à communiquer à l'éditeur pour obtenir la licence ; seulement tant que
 *                          l'installation n'est pas faite (ensuite elle est réservée à l'écran Licence des administrateurs)
 */
public record EtatInstallationDto(boolean installee, String empreinteServeur) {
}
