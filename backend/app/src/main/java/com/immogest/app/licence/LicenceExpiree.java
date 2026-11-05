package com.immogest.app.licence;

import com.immogest.app.shared.EvenementMetier;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

/** La licence a expiré : l'application passe en lecture seule. Publiée une seule fois. */
public record LicenceExpiree(UUID id, String licenseId, LocalDate dateExpiration) implements EvenementMetier {

    @Override
    public String action() {
        return "LICENCE.EXPIREE";
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
        return Map.of("dateExpiration", dateExpiration.toString());
    }
}
