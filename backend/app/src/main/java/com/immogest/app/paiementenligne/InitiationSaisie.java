package com.immogest.app.paiementenligne;

import java.util.UUID;

/** @param montant FCFA entiers ; @param telephone numéro Mobile Money qui paiera (format international) */
public record InitiationSaisie(UUID bailId, Long montant, OperateurMobile operateur, String telephone) {
}
