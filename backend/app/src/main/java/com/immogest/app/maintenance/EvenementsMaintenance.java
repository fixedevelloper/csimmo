package com.immogest.app.maintenance;

import com.immogest.app.shared.EvenementMetier;
import java.util.Map;
import java.util.UUID;

public final class EvenementsMaintenance {

    private EvenementsMaintenance() {
    }

    /** Signalement créé (identifiants seulement : la description peut contenir des données personnelles). */
    public record SignalementCree(UUID id, UUID bailId, UUID lotId, boolean avecPhoto, String auteur) implements EvenementMetier {
        @Override
        public String action() {
            return "MAINTENANCE.SIGNALEMENT_CREE";
        }

        @Override
        public String entite() {
            return "signalement";
        }

        @Override
        public UUID entiteId() {
            return id;
        }

        @Override
        public Object apres() {
            return Map.of("bailId", bailId.toString(), "lotId", lotId.toString(), "avecPhoto", avecPhoto);
        }
    }

    public record SignalementTraite(UUID id, String de, String vers, String auteur) implements EvenementMetier {
        @Override
        public String action() {
            return "MAINTENANCE.SIGNALEMENT_" + vers;
        }

        @Override
        public String entite() {
            return "signalement";
        }

        @Override
        public UUID entiteId() {
            return id;
        }

        @Override
        public Object avant() {
            return Map.of("statut", de);
        }

        @Override
        public Object apres() {
            return Map.of("statut", vers);
        }
    }
}
