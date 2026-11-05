package com.immogest.app.paiementenligne;

import com.immogest.app.shared.EvenementMetier;
import java.util.Map;
import java.util.UUID;

/** Événements audités (identifiants et montants seulement : ni numéro de téléphone ni contenu de webhook). Des records : ils sont sérialisés par le registre d'événements. */
public final class EvenementsPaiementEnLigne {

    private EvenementsPaiementEnLigne() {
    }

    public record Initiee(UUID id, UUID bailId, long montant, OperateurMobile operateur) implements EvenementMetier {
        @Override
        public String action() {
            return "PAIEMENT_EN_LIGNE.INITIEE";
        }

        @Override
        public String entite() {
            return "transaction_en_ligne";
        }

        @Override
        public UUID entiteId() {
            return id;
        }

        @Override
        public Object apres() {
            return Map.of("bailId", bailId.toString(), "montant", montant, "operateur", operateur.name());
        }
    }

    public record Confirmee(UUID id, UUID paiementId, long montant) implements EvenementMetier {
        @Override
        public String action() {
            return "PAIEMENT_EN_LIGNE.CONFIRMEE";
        }

        @Override
        public String entite() {
            return "transaction_en_ligne";
        }

        @Override
        public UUID entiteId() {
            return id;
        }

        @Override
        public Object apres() {
            return Map.of("paiementId", paiementId.toString(), "montant", montant);
        }
    }

    /** @param statut ECHOUEE ou EXPIREE */
    public record Echouee(UUID id, String statut, String cause) implements EvenementMetier {
        @Override
        public String action() {
            return "PAIEMENT_EN_LIGNE." + statut;
        }

        @Override
        public String entite() {
            return "transaction_en_ligne";
        }

        @Override
        public UUID entiteId() {
            return id;
        }

        @Override
        public Object apres() {
            return Map.of("cause", cause);
        }
    }

    /** Montant annoncé par l'opérateur différent du montant demandé : à investiguer. */
    public record Anomalie(UUID id, long montantDemande, long montantAnnonce) implements EvenementMetier {
        @Override
        public String action() {
            return "PAIEMENT_EN_LIGNE.ANOMALIE";
        }

        @Override
        public String entite() {
            return "transaction_en_ligne";
        }

        @Override
        public UUID entiteId() {
            return id;
        }

        @Override
        public Object apres() {
            return Map.of("montantDemande", montantDemande, "montantAnnonce", montantAnnonce);
        }
    }

    /** Webhook dont la signature est invalide (tentative de falsification ou secret mal configuré). */
    public record WebhookRejete(String fournisseur) implements EvenementMetier {
        @Override
        public String action() {
            return "PAIEMENT_EN_LIGNE.WEBHOOK_REJETE";
        }

        @Override
        public String entite() {
            return "webhook";
        }

        @Override
        public UUID entiteId() {
            return null;
        }

        @Override
        public Object apres() {
            return Map.of("fournisseur", fournisseur);
        }
    }

    public record FournisseurModifie(String code, boolean actif) implements EvenementMetier {
        @Override
        public String action() {
            return "PAIEMENT_EN_LIGNE.FOURNISSEUR_MODIFIE";
        }

        @Override
        public String entite() {
            return "fournisseur_paiement";
        }

        @Override
        public UUID entiteId() {
            return null;
        }

        @Override
        public Object apres() {
            return Map.of("fournisseur", code, "actif", actif);
        }
    }
}
