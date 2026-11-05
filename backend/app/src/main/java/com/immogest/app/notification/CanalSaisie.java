package com.immogest.app.notification;

import java.util.Map;

/** @param champs valeurs saisies ; un secret vide ou absent conserve la valeur actuelle */
public record CanalSaisie(boolean actif, Map<String, String> champs) {
}
