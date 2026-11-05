package com.immogest.app.bail;

import java.time.LocalDate;

/**
 * @param dateNotification   date à laquelle le préavis est donné
 * @param dateSortie         date de sortie ; par défaut notification + durée du préavis du bail
 * @param preavisReduitAccepte accord des parties pour une sortie avant la fin du préavis
 */
public record ResiliationSaisie(LocalDate dateNotification, LocalDate dateSortie, boolean preavisReduitAccepte, String motif) {
}
