package com.immogest.app.licence;

import com.immogest.app.shared.EvenementMetier;
import java.util.UUID;

/** L'assistant de premier lancement est terminé (ADMIN créé, licence importée). */
public record InstallationTerminee(UUID id, String client) implements EvenementMetier {

    @Override
    public String action() {
        return "LICENCE.INSTALLATION_TERMINEE";
    }

    @Override
    public String entite() {
        return "installation";
    }

    @Override
    public UUID entiteId() {
        return id;
    }
}
