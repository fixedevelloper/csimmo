package com.immogest.app.paiementenligne;

import java.time.OffsetDateTime;
import java.util.UUID;

/** @param instructions consigne à afficher au locataire (ex. valider sur son téléphone) ; @param paiementId présent seulement une fois confirmée */
public record TransactionDto(UUID id, UUID bailId, String bailNumero, UUID locataireId, String locataireNom, OperateurMobile operateur,
        String telephone, long montant, StatutTransaction statut, String instructions, String erreur, UUID paiementId, OffsetDateTime creeLe,
        OffsetDateTime confirmeeLe) {
}
