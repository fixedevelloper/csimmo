package com.immogest.app.securite.domain;

import java.util.regex.Pattern;

/** Normalisation des identifiants de connexion (téléphone ou e-mail). */
public final class Identifiants {

    private static final Pattern TELEPHONE = Pattern.compile("\\+?[0-9]{8,15}");

    private Identifiants() {
    }

    public static boolean estEmail(String identifiant) {
        return identifiant != null && identifiant.contains("@");
    }

    /** Retire espaces, points et tirets ; renvoie {@code null} si vide. */
    public static String normaliserTelephone(String telephone) {
        if (telephone == null || telephone.isBlank()) {
            return null;
        }
        return telephone.replaceAll("[\\s.\\-()]", "");
    }

    public static boolean telephoneValide(String telephoneNormalise) {
        return telephoneNormalise != null && TELEPHONE.matcher(telephoneNormalise).matches();
    }

    public static String normaliserEmail(String email) {
        return email == null || email.isBlank() ? null : email.strip().toLowerCase();
    }
}
