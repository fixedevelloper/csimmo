/**
 * Conformité à la loi camerounaise n°2024/017 sur la protection des données personnelles : registre des traitements, consentements (modèles de
 * texte, enregistrement en ajout seul) et demandes d'anonymisation. L'anonymisation est compatible avec « aucune suppression » (CLAUDE.md §6 règle 11) :
 * les écritures financières et documents comptables sont conservés (obligation légale, 10 ans), seules les données personnelles sont effacées.
 * L'événement {@code shared.PersonneAnonymisee} fait effacer comptes, messages et journal d'audit par leurs modules.
 */
@ApplicationModule(displayName = "Conformité", allowedDependencies = {"shared", "tiers", "bail :: api", "finance", "patrimoine"})
package com.immogest.app.conformite;

import org.springframework.modulith.ApplicationModule;
