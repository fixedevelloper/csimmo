package com.immogest.app.tiers.infra;

import com.immogest.app.shared.ErreurMetier;
import java.util.regex.Pattern;
import org.springframework.http.HttpStatus;

/** Normalisation et contrôle des champs texte des fiches. */
public final class Textes {

    private static final Pattern TELEPHONE = Pattern.compile("\\+?[0-9]{8,15}");
    private static final Pattern EMAIL = Pattern.compile("[^@\\s]+@[^@\\s]+\\.[^@\\s]+");

    private Textes() {
    }

    public static String exiger(String valeur, String champ, int max) {
        String t = optionnel(valeur, max);
        if (t == null) {
            throw new ErreurMetier("CHAMP_REQUIS", "Le champ " + champ + " est obligatoire", HttpStatus.BAD_REQUEST);
        }
        return t;
    }

    public static String optionnel(String valeur, int max) {
        if (valeur == null || valeur.isBlank()) {
            return null;
        }
        String t = valeur.strip();
        if (t.length() > max) {
            throw new ErreurMetier("CHAMP_TROP_LONG", "Texte trop long (max %d caractères)".formatted(max), HttpStatus.BAD_REQUEST);
        }
        return t;
    }

    /** Téléphone sans espaces, points, tirets ni parenthèses ; {@code null} si vide. */
    public static String telephone(String valeur) {
        if (valeur == null || valeur.isBlank()) {
            return null;
        }
        String t = valeur.replaceAll("[\\s.\\-()]", "");
        if (!TELEPHONE.matcher(t).matches()) {
            throw new ErreurMetier("TELEPHONE_INVALIDE", "Numéro de téléphone invalide", HttpStatus.BAD_REQUEST);
        }
        return t;
    }

    public static String email(String valeur) {
        String t = optionnel(valeur, 200);
        if (t == null) {
            return null;
        }
        if (!EMAIL.matcher(t).matches()) {
            throw new ErreurMetier("EMAIL_INVALIDE", "Adresse e-mail invalide", HttpStatus.BAD_REQUEST);
        }
        return t.toLowerCase();
    }

    /** Au moins un moyen de contact. */
    public static void contactRequis(String telephone, String email) {
        if (telephone == null && email == null) {
            throw new ErreurMetier("CONTACT_REQUIS", "Un téléphone ou un e-mail est obligatoire", HttpStatus.BAD_REQUEST);
        }
    }
}
