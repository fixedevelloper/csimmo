package com.immogest.app.sauvegarde;

import java.time.OffsetDateTime;

/** Une sauvegarde complète (manifeste lu sur disque). {@code externe} : OK, ECHEC ou NON_CONFIGURE. */
public record SauvegardeDto(String id, OffsetDateTime date, String version, String etiquette, long tailleOctets, long objets, String externe, boolean restaurable) {
}
