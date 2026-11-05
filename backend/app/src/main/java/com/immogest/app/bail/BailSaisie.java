package com.immogest.app.bail;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Création ou modification (brouillon) d'un bail. Montants en FCFA entiers ; {@code preavisMois} par défaut 3.
 *
 * @param dateFin facultative : sans date de fin, le bail court jusqu'à sa résiliation
 */
public record BailSaisie(UUID lotId, UUID locataireId, List<UUID> garantIds, LocalDate dateDebut, LocalDate dateFin, Long loyer,
        Long charges, Periodicite periodicite, Integer jourEcheance, Long caution, Integer moisAvance, Integer preavisMois) {
}
