package com.immogest.app.finance;

import com.immogest.app.shared.EvenementMetier;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

public final class EvenementsReprise {

    private EvenementsReprise() {
    }

    /** Historique d'un bail repris à la mise en service : identifiants et montants seulement. */
    public record RepriseEffectuee(UUID bailId, LocalDate dateReprise, int echeances, long soldeOuverture) implements EvenementMetier {
        @Override
        public String action() {
            return "FINANCE.REPRISE_EFFECTUEE";
        }

        @Override
        public String entite() {
            return "bail";
        }

        @Override
        public UUID entiteId() {
            return bailId;
        }

        @Override
        public Object apres() {
            return Map.of("dateReprise", dateReprise.toString(), "echeances", echeances, "soldeOuverture", soldeOuverture);
        }
    }
}
