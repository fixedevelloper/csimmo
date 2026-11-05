package com.immogest.app.relance;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Une échéance impayée (FCFA).
 *
 * @param peutMiseEnDemeure le dernier niveau de relance est atteint et aucune mise en demeure n'a encore été émise
 */
public record ArriereDto(UUID echeanceId, UUID bailId, String bailNumero, UUID locataireId, String locataireNom, String logement,
        LocalDate periodeDebut, LocalDate periodeFin, LocalDate dateEcheance, long montant, long reste, int joursRetard,
        RelanceDto derniereRelance, boolean peutMiseEnDemeure, UUID miseEnDemeureId) {
}
