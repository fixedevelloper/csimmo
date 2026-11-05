package com.immogest.app.finance;

import java.time.LocalDate;
import java.util.UUID;

/**
 * @param type      QUITTANCE ou RECU
 * @param statut    EMIS ou ANNULE (contre-passation du paiement)
 * @param fichierId PDF à télécharger par {@code GET /api/v1/fichiers/{fichierId}} (contrôle d'accès par l'API)
 */
public record DocumentDto(UUID id, String type, String numero, String statut, UUID bailId, String bailNumero, UUID locataireId,
        String locataireNom, UUID echeanceId, UUID paiementId, LocalDate periodeDebut, LocalDate periodeFin, long montant,
        LocalDate dateEmission, UUID fichierId, String motifAnnulation) {
}
