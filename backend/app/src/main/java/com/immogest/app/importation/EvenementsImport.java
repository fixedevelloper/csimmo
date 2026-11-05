package com.immogest.app.importation;

import com.immogest.app.shared.EvenementMetier;
import java.util.Map;
import java.util.UUID;

public final class EvenementsImport {

    private EvenementsImport() {
    }

    /** Import réussi : comptes et empreinte du fichier seulement (aucune donnée personnelle). */
    public record ImportEffectue(UUID id, String par, String sha256, Map<String, Integer> comptes) implements EvenementMetier {
        @Override
        public String action() {
            return "IMPORT.EFFECTUE";
        }

        @Override
        public String entite() {
            return "import";
        }

        @Override
        public UUID entiteId() {
            return id;
        }

        @Override
        public Object apres() {
            return Map.of("sha256", sha256, "comptes", comptes);
        }

        @Override
        public String auteur() {
            return par;
        }
    }
}
