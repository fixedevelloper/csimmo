package com.immogest.app.relance;

import com.immogest.app.shared.EvenementMetier;
import java.util.Map;
import java.util.UUID;

public final class EvenementsRelance {

    private EvenementsRelance() {
    }

    /** Une relance a été traitée (identifiants seulement). */
    public record RelanceTraitee(UUID relanceId, UUID echeanceId, UUID bailId, String type, Integer niveau, String resultat, String auteur)
            implements EvenementMetier {
        @Override
        public String action() {
            return "RELANCE.TRAITEE";
        }

        @Override
        public String entite() {
            return "relance";
        }

        @Override
        public UUID entiteId() {
            return relanceId;
        }

        @Override
        public Object apres() {
            return Map.of("echeanceId", echeanceId.toString(), "type", type, "resultat", resultat);
        }
    }

    public record MiseEnDemeureEmise(UUID id, String numero, UUID bailId, UUID echeanceId, long montant, String auteur) implements EvenementMetier {
        @Override
        public String action() {
            return "RELANCE.MISE_EN_DEMEURE_EMISE";
        }

        @Override
        public String entite() {
            return "mise_en_demeure";
        }

        @Override
        public UUID entiteId() {
            return id;
        }

        @Override
        public Object apres() {
            return Map.of("numero", numero, "bailId", bailId.toString(), "echeanceId", echeanceId.toString(), "montant", montant);
        }
    }

    public record ParametresModifies(String niveaux, int delai, boolean actif, String auteur) implements EvenementMetier {
        @Override
        public String action() {
            return "RELANCE.PARAMETRES_MODIFIES";
        }

        @Override
        public String entite() {
            return "relance_parametres";
        }

        @Override
        public UUID entiteId() {
            return null;
        }

        @Override
        public Object apres() {
            return Map.of("niveaux", niveaux, "delaiMiseEnDemeureJours", delai, "actif", actif);
        }
    }
}
