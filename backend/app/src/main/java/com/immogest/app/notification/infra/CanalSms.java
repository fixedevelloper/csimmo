package com.immogest.app.notification.infra;

import com.immogest.app.notification.TypeCanal;
import java.util.List;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * SMS par un fournisseur HTTP quelconque : POST d'un corps JSON modèle où {@code {to}}, {@code {from}} et {@code {message}} sont
 * remplacés (valeurs échappées), avec un en-tête d'authentification. Succès = réponse HTTP 2xx.
 */
@Component
@ConditionalOnProperty(name = "immogest.notification.simuler", havingValue = "false", matchIfMissing = true)
public class CanalSms implements CanalNotification {

    static final String CORPS_DEFAUT = "{\"to\":\"{to}\",\"from\":\"{from}\",\"message\":\"{message}\"}";

    @Override
    public TypeCanal type() {
        return TypeCanal.SMS;
    }

    @Override
    public List<Champ> champs() {
        return List.of(new Champ("url", false, true, null), new Champ("corps", false, false, CORPS_DEFAUT),
                new Champ("expediteur", false, false, null), new Champ("enteteNom", false, false, "Authorization"),
                new Champ("enteteValeur", true, false, null));
    }

    @Override
    public void envoyer(Envoi envoi, Map<String, String> config) throws EnvoiException {
        String modele = config.getOrDefault("corps", "").isBlank() ? CORPS_DEFAUT : config.get("corps");
        String corps = modele.replace("{to}", ClientHttp.echapperJson(chiffres(envoi.destinataire())))
                .replace("{from}", ClientHttp.echapperJson(config.getOrDefault("expediteur", "")))
                .replace("{message}", ClientHttp.echapperJson(envoi.corps()));
        String nomEntete = config.getOrDefault("enteteNom", "").isBlank() ? "Authorization" : config.get("enteteNom").strip();
        Map<String, String> entetes = Map.of(nomEntete, config.getOrDefault("enteteValeur", ""));
        ClientHttp.exigerSucces(ClientHttp.poster(config.get("url"), entetes, "application/json", corps));
    }

    /** Numéro au format international sans « + » ni espaces (+237 6 99 11 22 33 → 237699112233). */
    static String chiffres(String numero) {
        return numero == null ? "" : numero.replaceAll("[^0-9]", "");
    }
}
