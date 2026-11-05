package com.immogest.app.bail;

import java.util.List;

/**
 * Textes modifiables du bail. Les variables {@code {{NOM}}} des textes sont remplacées à la génération ;
 * {@code variables} liste celles qui sont reconnues.
 */
public record ModeleBailDto(String bailleurNom, String bailleurAdresse, String preambule, String clauses, String piedDePage,
        List<String> variables) {
}
