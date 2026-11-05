/**
 * Paiement Mobile Money par agrégateur. Règle n°8 : un paiement n'est créé (et imputé) qu'après confirmation de l'opérateur, par
 * webhook signé ou par vérification de statut auprès de l'agrégateur, jamais sur simple déclaration du locataire ou du téléphone.
 * Appelle {@code finance} pour enregistrer le paiement confirmé ; {@code finance} ne dépend jamais de ce module.
 */
@ApplicationModule(displayName = "Paiement en ligne", allowedDependencies = {"shared", "finance", "bail :: api", "tiers :: api", "licence :: api", "stockage"})
package com.immogest.app.paiementenligne;

import org.springframework.modulith.ApplicationModule;
