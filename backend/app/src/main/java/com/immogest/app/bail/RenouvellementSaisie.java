package com.immogest.app.bail;

import java.time.LocalDate;

/** Conditions du bail renouvelé ; ce qui est absent reprend la valeur du bail renouvelé. Début = lendemain de la fin du bail courant. */
public record RenouvellementSaisie(LocalDate dateFin, Long loyer, Long charges, Long caution, Integer moisAvance) {
}
