package com.immogest.app.finance;

import java.time.LocalDate;
import java.util.UUID;

/** @param montant FCFA entiers ; @param datePaiement défaut : aujourd'hui (jamais dans le futur) */
public record PaiementSaisie(UUID bailId, Long montant, ModePaiement mode, String reference, LocalDate datePaiement) {
}
