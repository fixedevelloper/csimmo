package com.immogest.app.finance;

import java.time.LocalDate;
import java.util.UUID;

/** Échéance avec un reste à payer (FCFA) : base des relances et de la liste des arriérés. */
public record EcheanceOuverte(UUID echeanceId, UUID bailId, LocalDate periodeDebut, LocalDate periodeFin, LocalDate dateEcheance,
        long montant, long reste, StatutEcheance statut) {
}
