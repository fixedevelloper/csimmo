package com.immogest.app.licence;

import com.immogest.app.shared.EvenementMetier;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

/** Une licence a été importée (installation, renouvellement manuel ou automatique). */
public record LicenceImportee(UUID id, String licenseId, String client, LocalDate dateExpiration, String auteur)
        implements EvenementMetier {

    @Override
    public String action() {
        return "LICENCE.IMPORTEE";
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
        return Map.of("licenseId", licenseId, "client", client, "dateExpiration", dateExpiration.toString());
    }
}
