package com.immogest.app.finance;

import java.time.LocalDate;
import java.util.UUID;

/** Montants en FCFA entiers. */
public record EcheanceDto(UUID id, UUID bailId, LocalDate periodeDebut, LocalDate periodeFin, LocalDate dateEcheance, long montant,
        long montantPaye, long reste, StatutEcheance statut) {
}
