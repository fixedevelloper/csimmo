package com.immogest.app.licence;

import com.immogest.app.shared.EvenementMetier;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

/**
 * La licence expire dans {@code seuilJours} jours (30, 15 ou 7). Publiée une seule fois par seuil ;
 * le module {@code notification} (lot 5.2) l'écoutera pour prévenir l'administrateur.
 */
public record LicenceBientotExpiree(UUID id, String licenseId, int seuilJours, long joursRestants,
        LocalDate dateExpiration) implements EvenementMetier {

    @Override
    public String action() {
        return "LICENCE.BIENTOT_EXPIREE";
    }

    @Override
    public String entite() {
        return "licence";
    }

    @Override
    public UUID entiteId() {
        return id;
    }

    @Override
    public Object apres() {
        return Map.of("seuilJours", seuilJours, "joursRestants", joursRestants, "dateExpiration", dateExpiration.toString());
    }
}
