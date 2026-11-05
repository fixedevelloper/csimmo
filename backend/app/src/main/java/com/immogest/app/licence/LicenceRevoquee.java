package com.immogest.app.licence;

import com.immogest.app.shared.EvenementMetier;
import java.util.UUID;

/** Le serveur de licences a révoqué la licence : lecture seule. */
public record LicenceRevoquee(UUID id, String licenseId) implements EvenementMetier {

    @Override
    public String action() {
        return "LICENCE.REVOQUEE";
    }

    @Override
    public String entite() {
        return "licence";
    }

    @Override
    public UUID entiteId() {
        return id;
    }
}
