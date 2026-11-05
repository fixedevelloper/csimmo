package com.immogest.app.notification.infra;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import org.springframework.stereotype.Component;

/** Modèles de messages fr/en ({@code notification/messages_*.properties}), variables {@code {nom}}. Texte brut, jamais interprété. */
@Component
public class ModelesMessages {

    public record Rendu(String sujet, String corps, String court) {
    }

    public Rendu rendre(String cle, String langue, Map<String, String> variables) {
        ResourceBundle b = ResourceBundle.getBundle("notification/messages", Locale.forLanguageTag(langue));
        return new Rendu(remplacer(b, cle + ".sujet", variables), remplacer(b, cle + ".corps", variables), remplacer(b, cle + ".court", variables));
    }

    /** Texte court (SMS) : pas de sujet ni de corps long. */
    public String court(String cle, String langue, Map<String, String> variables) {
        return rendre(cle, langue, variables).court();
    }

    private static String remplacer(ResourceBundle b, String cle, Map<String, String> variables) {
        if (!b.containsKey(cle)) {
            return null;
        }
        String texte = b.getString(cle).replace("''", "'");
        for (Map.Entry<String, String> e : variables.entrySet()) {
            texte = texte.replace("{" + e.getKey() + "}", e.getValue() == null ? "" : e.getValue());
        }
        return texte;
    }
}
