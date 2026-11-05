package com.immogest.app.notification.infra;

import com.immogest.app.notification.TypeCanal;
import java.util.List;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * WhatsApp Cloud API. Hors fenêtre de 24 h, seuls les messages « template » approuvés par Meta sont acceptés : le client crée un
 * modèle dont le corps contient une variable {@code {{1}}}, et son nom est saisi ici. Le texte du message est passé en variable.
 */
@Component
@ConditionalOnProperty(name = "immogest.notification.simuler", havingValue = "false", matchIfMissing = true)
public class CanalWhatsapp implements CanalNotification {

    static final String BASE_DEFAUT = "https://graph.facebook.com/v21.0";

    @Override
    public TypeCanal type() {
        return TypeCanal.WHATSAPP;
    }

    @Override
    public List<Champ> champs() {
        return List.of(new Champ("phoneNumberId", false, true, null), new Champ("modele", false, true, null),
                new Champ("langue", false, false, "fr"), new Champ("apiBase", false, false, BASE_DEFAUT), new Champ("token", true, true, null));
    }

    @Override
    public void envoyer(Envoi envoi, Map<String, String> config) throws EnvoiException {
        String base = config.getOrDefault("apiBase", "").isBlank() ? BASE_DEFAUT : config.get("apiBase").replaceAll("/+$", "");
        String langue = config.getOrDefault("langue", "").isBlank() ? "fr" : config.get("langue");
        String texte = envoi.corps().length() > 1000 ? envoi.corps().substring(0, 1000) : envoi.corps();
        String corps = """
                {"messaging_product":"whatsapp","to":"%s","type":"template","template":{"name":"%s","language":{"code":"%s"},
                "components":[{"type":"body","parameters":[{"type":"text","text":"%s"}]}]}}"""
                .formatted(ClientHttp.echapperJson(CanalSms.chiffres(envoi.destinataire())), ClientHttp.echapperJson(config.get("modele")),
                        ClientHttp.echapperJson(langue), ClientHttp.echapperJson(texte.replace('\n', ' ')));
        ClientHttp.exigerSucces(ClientHttp.poster(base + "/" + config.get("phoneNumberId") + "/messages",
                Map.of("Authorization", "Bearer " + config.get("token")), "application/json", corps));
    }
}
