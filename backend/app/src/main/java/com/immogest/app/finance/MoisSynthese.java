package com.immogest.app.finance;

import java.time.LocalDate;

/**
 * Un mois d'échéances (FCFA) : {@code attendu} = somme des échéances dont la date tombe ce mois ; {@code encaisse} = ce qui en a été payé.
 *
 * @param mois premier jour du mois
 */
public record MoisSynthese(LocalDate mois, long attendu, long encaisse) {
}
