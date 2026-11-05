package com.immogest.app.finance;

import java.time.LocalDate;
import java.util.UUID;

/** Arriérés d'un bail (FCFA) : échéances échues et non soldées. */
public record ArriereBail(UUID bailId, long total, int nombre, LocalDate plusAncienne) {
}
