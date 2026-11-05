package com.immogest.app.shared;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Mise en forme française des montants (FCFA) et des dates pour les documents. */
public final class Montants {

    private static final DateTimeFormatter DATE_LONGUE = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.FRENCH);

    private Montants() {
    }

    /** « 1 500 000 FCFA » avec des espaces insécables classiques (U+00A0) : l'espace fine U+202F n'existe pas dans les polices PDF de base. */
    public static String fcfa(long montant) {
        String chiffres = Long.toString(Math.abs(montant));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chiffres.length(); i++) {
            if (i > 0 && (chiffres.length() - i) % 3 == 0) {
                sb.append(' ');
            }
            sb.append(chiffres.charAt(i));
        }
        return (montant < 0 ? "-" : "") + sb + " FCFA";
    }

    public static String date(LocalDate date) {
        return date == null ? "" : date.format(DATE_LONGUE);
    }

    /** Texte sûr pour les polices PDF de base : NFC, espaces fines remplacées, caractères de contrôle retirés. */
    public static String pourPdf(String texte) {
        if (texte == null) {
            return null;
        }
        return Normalizer.normalize(texte, Normalizer.Form.NFC)
                .replace(' ', ' ').replace(' ', ' ').replace('​', ' ')
                .replaceAll("[\\p{Cntrl}&&[^\\n\\r\\t]]", "");
    }
}
