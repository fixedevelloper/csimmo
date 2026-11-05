package com.immogest.app.finance;

import com.immogest.app.shared.EvenementMetier;
import java.util.Map;
import java.util.UUID;

public final class EvenementsReversement {

    private EvenementsReversement() {
    }

    public record ReversementValide(UUID id, UUID proprietaireId, String periode, String numero, long net, String auteur) implements EvenementMetier {
        @Override
        public String action() {
            return "FINANCE.REVERSEMENT_VALIDE";
        }

        @Override
        public String entite() {
            return "reversement";
        }

        @Override
        public UUID entiteId() {
            return id;
        }

        @Override
        public Object apres() {
            return Map.of("proprietaireId", proprietaireId.toString(), "periode", periode, "numero", numero, "net", net);
        }
    }

    public record ReversementPaye(UUID id, String mode, String auteur) implements EvenementMetier {
        @Override
        public String action() {
            return "FINANCE.REVERSEMENT_PAYE";
        }

        @Override
        public String entite() {
            return "reversement";
        }

        @Override
        public UUID entiteId() {
            return id;
        }

        @Override
        public Object apres() {
            return Map.of("mode", mode);
        }
    }

    public record DepenseEnregistree(UUID id, UUID immeubleId, UUID proprietaireId, long montant, String auteur) implements EvenementMetier {
        @Override
        public String action() {
            return "FINANCE.DEPENSE_ENREGISTREE";
        }

        @Override
        public String entite() {
            return "depense";
        }

        @Override
        public UUID entiteId() {
            return id;
        }

        @Override
        public Object apres() {
            return Map.of("immeubleId", immeubleId.toString(), "proprietaireId", proprietaireId.toString(), "montant", montant);
        }
    }

    public record DepenseAnnulee(UUID id, String motif, String auteur) implements EvenementMetier {
        @Override
        public String action() {
            return "FINANCE.DEPENSE_ANNULEE";
        }

        @Override
        public String entite() {
            return "depense";
        }

        @Override
        public UUID entiteId() {
            return id;
        }

        @Override
        public Object apres() {
            return Map.of("motif", motif);
        }
    }
}
