package com.immogest.app.bail;

import com.immogest.app.shared.EvenementMetier;
import java.util.Map;
import java.util.UUID;

/** Création, activation, résiliation… : journalisé par {@code audit}. Pas de donnée d'identité (seulement des identifiants). */
public record EvenementBail(String action, UUID entiteId, Map<String, Object> avant, Map<String, Object> apres, String auteur)
        implements EvenementMetier {

    @Override
    public String entite() {
        return "bail";
    }
}
