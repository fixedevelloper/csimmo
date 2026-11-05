package com.immogest.app.paiementenligne.infra;

import com.immogest.app.paiementenligne.OperateurMobile;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Un agrégateur de paiement Mobile Money. Les implémentations réelles (CinetPay, Campay, NotchPay…) parlent à l'API du fournisseur
 * avec les identifiants marchands du client ; {@link AgregateurSimule} sert en développement et en test.
 */
public interface AgregateurPaiement {

    /** Champ de configuration saisi par le client dans Paramètres. */
    record Champ(String nom, boolean secret, boolean requis) {
    }

    /** @param montant FCFA entiers ; @param reference identifiant de notre transaction, à transmettre à l'agrégateur */
    record Demande(String reference, long montant, OperateurMobile operateur, String telephone, String description) {
    }

    /** @param referenceFournisseur identifiant de la transaction chez l'agrégateur ; @param instructions consigne à montrer au payeur */
    record Initiation(String referenceFournisseur, String instructions) {
    }

    enum Etat { EN_ATTENTE, SUCCES, ECHEC }

    /** @param referenceOperateur référence de l'opérateur (preuve du paiement) ; @param montant montant que l'opérateur annonce avoir débité */
    record EtatDistant(Etat etat, String referenceOperateur, Long montant) {
    }

    /** Webhook dont la signature a été vérifiée. @param evenementId identifiant unique de l'événement (dédoublonnage des rejeux) */
    record EvenementWebhook(String evenementId, String referenceFournisseur, EtatDistant etat) {
    }

    /** Échec de l'agrégateur (injoignable, refus, réponse inattendue). Le message n'expose jamais de secret. */
    class AgregateurException extends Exception {
        public AgregateurException(String message) {
            super(message);
        }

        public AgregateurException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /** Signature absente, mauvaise ou expirée : l'appelant répond 401 sans rien traiter. */
    class SignatureInvalideException extends Exception {
        public SignatureInvalideException(String message) {
            super(message);
        }
    }

    /** Identifiant du fournisseur dans l'URL du webhook : {@code /api/v1/webhooks/{code}}. */
    String code();

    List<Champ> champs();

    /** @param config valeurs saisies, secrets déchiffrés inclus */
    Initiation initier(Demande demande, Map<String, String> config) throws AgregateurException;

    EtatDistant verifierStatut(String referenceFournisseur, Map<String, String> config) throws AgregateurException;

    /**
     * Vérifie la signature de la requête puis l'interprète. Doit être résistante au rejeu et comparer les signatures en temps constant.
     *
     * @param entetes noms d'en-têtes en minuscules
     * @return l'événement, ou vide si la requête est authentique mais sans effet (ping, événement ignoré)
     */
    Optional<EvenementWebhook> validerWebhook(Map<String, String> entetes, byte[] corps, Map<String, String> config) throws SignatureInvalideException;
}
