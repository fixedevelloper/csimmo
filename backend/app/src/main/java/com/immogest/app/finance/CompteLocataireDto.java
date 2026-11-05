package com.immogest.app.finance;

import java.util.List;
import java.util.UUID;

/**
 * Situation d'un locataire (FCFA). {@code arrieres} : reste dû sur les échéances arrivées à terme ; {@code aVenir} : reste sur les
 * suivantes ; {@code credit} : encaissements pas encore imputés ; {@code solde} = arriérés − crédit (positif = il doit).
 */
public record CompteLocataireDto(UUID locataireId, String locataireNom, long arrieres, long aVenir, long credit, long solde,
        List<BailCompte> baux) {

    public record BailCompte(UUID bailId, String numero, String statut) {
    }
}
