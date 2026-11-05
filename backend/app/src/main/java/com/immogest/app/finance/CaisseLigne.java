package com.immogest.app.finance;

import java.util.UUID;

/** Espèces saisies par un agent un jour donné, nettes des contre-passations (FCFA). */
public record CaisseLigne(UUID agentId, String agentNom, long total, int operations) {
}
