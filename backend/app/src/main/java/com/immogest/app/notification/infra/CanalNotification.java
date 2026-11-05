package com.immogest.app.notification.infra;

import com.immogest.app.notification.TypeCanal;
import java.util.List;
import java.util.Map;

/** Un moyen d'envoi. Les implémentations réelles parlent au fournisseur ; les canaux simulés (tests, dev) enregistrent seulement. */
public interface CanalNotification {

    /** Champ de configuration saisi par le client dans Paramètres. */
    record Champ(String nom, boolean secret, boolean requis, String defaut) {
    }

    /** Message prêt à partir : {@code destinataire} est un numéro, une adresse e-mail ou un jeton d'appareil selon le canal. */
    record Envoi(String destinataire, String sujet, String corps) {
    }

    TypeCanal type();

    List<Champ> champs();

    /** @param config valeurs saisies, secrets déchiffrés inclus */
    void envoyer(Envoi envoi, Map<String, String> config) throws EnvoiException;
}
