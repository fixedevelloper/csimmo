package com.immogest.app.finance;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * @param type            AVANCE, ENCAISSEMENT ou CONTRE_PASSATION
 * @param contrePasse     l'encaissement a été annulé par une contre-passation
 * @param contrePassationDe pour une contre-passation : l'encaissement annulé
 */
public record PaiementDto(UUID id, UUID bailId, String bailNumero, UUID locataireId, String locataireNom, String type, ModePaiement mode,
        long montant, LocalDate datePaiement, String reference, UUID saisiPar, String saisiParNom, String motif, boolean contrePasse,
        UUID contrePassationDe, OffsetDateTime creeLe) {
}
