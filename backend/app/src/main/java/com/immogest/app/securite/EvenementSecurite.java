package com.immogest.app.securite;

import com.immogest.app.shared.EvenementMetier;
import java.util.Map;
import java.util.UUID;

/** Événement journalisé par {@code audit} (création, modification, verrouillage… d'un compte). Sans secret. */
public record EvenementSecurite(
        String action,
        UUID utilisateurId,
        Map<String, Object> avant,
        Map<String, Object> apres,
        String auteur) implements EvenementMetier {

    @Override
    public String entite() {
        return "utilisateur";
    }

    @Override
    public UUID entiteId() {
        return utilisateurId;
    }
}
