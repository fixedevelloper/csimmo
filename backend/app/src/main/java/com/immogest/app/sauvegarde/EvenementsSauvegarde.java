package com.immogest.app.sauvegarde;

import com.immogest.app.shared.EvenementMetier;
import java.util.Map;
import java.util.UUID;

/** Événements audités : l'identifiant de sauvegarde n'est pas un UUID, il est donc dans le contenu. */
public final class EvenementsSauvegarde {

    private EvenementsSauvegarde() {
    }

    public record SauvegardeDemandee(String par) implements EvenementMetier {
        @Override
        public String action() {
            return "SAUVEGARDE.DEMANDEE";
        }

        @Override
        public String entite() {
            return "sauvegarde";
        }

        @Override
        public UUID entiteId() {
            return null;
        }

        @Override
        public Object apres() {
            return Map.of("par", par);
        }
    }

    public record RestaurationDemandee(String sauvegardeId, String par) implements EvenementMetier {
        @Override
        public String action() {
            return "SAUVEGARDE.RESTAURATION_DEMANDEE";
        }

        @Override
        public String entite() {
            return "sauvegarde";
        }

        @Override
        public UUID entiteId() {
            return null;
        }

        @Override
        public Object apres() {
            return Map.of("sauvegardeId", sauvegardeId, "par", par);
        }
    }
}
