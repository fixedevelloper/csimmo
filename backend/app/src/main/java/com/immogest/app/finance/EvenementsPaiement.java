package com.immogest.app.finance;

import com.immogest.app.shared.EvenementMetier;
import java.util.Map;
import java.util.UUID;

/** Événements du module : journalisés par {@code audit} ; {@code notification} (étape 5.2) enverra le reçu SMS. */
public final class EvenementsPaiement {

    private EvenementsPaiement() {
    }

    /** Un encaissement est enregistré (aucun nom ni téléphone : seulement des identifiants). */
    public record PaiementEnregistre(UUID paiementId, UUID bailId, UUID locataireId, long montant, ModePaiement mode, String auteur)
            implements EvenementMetier {
        @Override
        public String action() {
            return "FINANCE.PAIEMENT_ENREGISTRE";
        }

        @Override
        public String entite() {
            return "paiement";
        }

        @Override
        public UUID entiteId() {
            return paiementId;
        }

        @Override
        public Object apres() {
            return Map.of("bailId", bailId.toString(), "montant", montant, "mode", mode.name());
        }
    }

    public record PaiementContrePasse(UUID contrePassationId, UUID paiementId, UUID bailId, long montant, String motif, String auteur)
            implements EvenementMetier {
        @Override
        public String action() {
            return "FINANCE.PAIEMENT_CONTRE_PASSE";
        }

        @Override
        public String entite() {
            return "paiement";
        }

        @Override
        public UUID entiteId() {
            return paiementId;
        }

        @Override
        public Object apres() {
            return Map.of("contrePassationId", contrePassationId.toString(), "bailId", bailId.toString(), "montant", montant, "motif", motif);
        }
    }

    /** Une échéance vient d'être entièrement payée : déclenche la quittance. Interne au module. */
    public record EcheancePayee(UUID echeanceId, UUID bailId) {
    }

    /** Un encaissement ne solde pas tout ce qu'il touche : déclenche le reçu. Interne au module. */
    public record RecuAEmettre(UUID paiementId) {
    }

    /** Quittance ou reçu émis (identifiants seulement) : audité, et consommé par {@code notification} pour l'envoi (étape 5.2). */
    public record DocumentEmis(UUID documentId, String type, String numero, UUID bailId, UUID locataireId, long montant, UUID fichierId)
            implements EvenementMetier {
        @Override
        public String action() {
            return "FINANCE." + type + "_EMIS";
        }

        @Override
        public String entite() {
            return "document";
        }

        @Override
        public UUID entiteId() {
            return documentId;
        }

        @Override
        public Object apres() {
            return Map.of("numero", numero, "bailId", bailId.toString(), "montant", montant);
        }
    }

    public record DocumentAnnule(UUID documentId, String numero, String motif, String auteur) implements EvenementMetier {
        @Override
        public String action() {
            return "FINANCE.DOCUMENT_ANNULE";
        }

        @Override
        public String entite() {
            return "document";
        }

        @Override
        public UUID entiteId() {
            return documentId;
        }

        @Override
        public Object apres() {
            return Map.of("numero", numero, "motif", motif);
        }
    }
}
