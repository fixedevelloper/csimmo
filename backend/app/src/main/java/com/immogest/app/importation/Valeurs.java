package com.immogest.app.importation;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.regex.Pattern;

/** Lecture tolérante des valeurs saisies à la main (dates JJ/MM/AAAA ou AAAA-MM-JJ, montants avec espaces, virgule décimale). */
final class Valeurs {

    private static final Pattern ENTIER = Pattern.compile("^\\d+([.,]0+)?$");
    private static final Pattern DECIMAL = Pattern.compile("^\\d+([.,]\\d+)?$");
    private static final Pattern TELEPHONE = Pattern.compile("^\\+?[0-9]{8,15}$");
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final DateTimeFormatter JJMMAAAA = DateTimeFormatter.ofPattern("d/M/uuuu").withResolverStyle(java.time.format.ResolverStyle.STRICT); // STRICT : « 31/02/2025 » est refusé, pas ramené au 28

    private Valeurs() {
    }

    /** Normalise un en-tête : sans accents, minuscules, séparateurs en « _ » (« Date début » → « date_debut »). */
    static String enTete(String s) {
        String n = Normalizer.normalize(s == null ? "" : s, Normalizer.Form.NFD).replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT);
        return n.replaceAll("[^a-z0-9]+", "_").replaceAll("^_+|_+$", "");
    }

    static LocalDate date(String v) {
        try {
            String t = v.strip();
            return t.contains("/") ? LocalDate.parse(t, JJMMAAAA) : LocalDate.parse(t.length() > 10 ? t.substring(0, 10) : t);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("date invalide (attendu AAAA-MM-JJ ou JJ/MM/AAAA)");
        }
    }

    /** FCFA entiers : « 150 000 », « 150000 », « 150000,0 » acceptés ; un montant avec décimales non nulles est refusé. */
    static long montant(String v) {
        String t = v.replaceAll("[\\s\\u00a0\\u202f]", "");
        if (!ENTIER.matcher(t).matches()) {
            throw new IllegalArgumentException("montant invalide (FCFA entier, sans décimales)");
        }
        try {
            return Long.parseLong(t.replaceAll("[.,]0+$", ""));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("montant trop grand");
        }
    }

    static int entier(String v, int max) {
        long n = montant(v);
        if (n > max) {
            throw new IllegalArgumentException("valeur trop grande (max " + max + ")");
        }
        return (int) n;
    }

    static BigDecimal decimal(String v) {
        String t = v.replaceAll("[\\s\\u00a0\\u202f]", "");
        if (!DECIMAL.matcher(t).matches()) {
            throw new IllegalArgumentException("nombre invalide");
        }
        return new BigDecimal(t.replace(',', '.'));
    }

    static String telephone(String v) {
        String t = v.replaceAll("[\\s.\\-()]", "");
        if (!TELEPHONE.matcher(t).matches()) {
            throw new IllegalArgumentException("numéro de téléphone invalide");
        }
        return t;
    }

    static String email(String v) {
        if (!EMAIL.matcher(v).matches() || v.length() > 200) {
            throw new IllegalArgumentException("adresse e-mail invalide");
        }
        return v;
    }

    /** Clé de rapprochement insensible à la casse, aux accents et aux espaces multiples (« Résidence  Akwa » = « residence akwa »). */
    static String cle(String v) {
        return Normalizer.normalize(v.strip(), Normalizer.Form.NFD).replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
    }
}
