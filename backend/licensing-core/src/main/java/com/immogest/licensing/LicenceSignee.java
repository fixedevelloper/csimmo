package com.immogest.licensing;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Licence accompagnée de sa signature Ed25519 : le contenu du fichier {@code .lic}.
 *
 * <pre>{"format":1,"licence":{…},"signature":"&lt;base64url&gt;"}</pre>
 *
 * Les octets signés sont le JSON canonique de {@code {"format":1,"licence":{…}}} reconstruit à partir des champs lus
 * (jamais le texte du fichier) : la mise en forme du fichier n'a aucune influence sur la signature.
 */
public record LicenceSignee(Licence licence, String signature) {

    public static final int FORMAT = 1;
    /** Un fichier de licence fait quelques centaines d'octets : au-delà, on refuse de le lire. */
    static final int TAILLE_MAX = 64 * 1024;

    private static final ObjectMapper MAPPER = JsonMapper.builder()
            .enable(StreamReadFeature.STRICT_DUPLICATE_DETECTION)
            .enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
            .build();

    // ------------------------------------------------------------------ signature

    /** Signe une licence avec la clé privée de l'éditeur. */
    public static LicenceSignee signer(Licence licence, PrivateKey clePrivee) {
        byte[] signature = Ed25519.signer(octetsSignes(licence), clePrivee);
        return new LicenceSignee(licence, Base64.getUrlEncoder().withoutPadding().encodeToString(signature));
    }

    /** Vrai si la signature correspond exactement au contenu de la licence. */
    public boolean signatureValide(PublicKey clePublique) {
        byte[] octets;
        try {
            octets = Base64.getUrlDecoder().decode(signature);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return Ed25519.verifier(octetsSignes(licence), octets, clePublique);
    }

    /** Octets effectivement signés (exposé pour les tests de non-régression). */
    static byte[] octetsSignes(Licence licence) {
        return CanonicalJson.ecrire(Map.of("format", FORMAT, "licence", enMap(licence)))
                .getBytes(StandardCharsets.UTF_8);
    }

    private static Map<String, Object> enMap(Licence l) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("licenseId", l.licenseId());
        m.put("client", l.client());
        m.put("palierLots", l.palierLots());
        m.put("dateDebut", l.dateDebut().toString());
        m.put("dateExpiration", l.dateExpiration().toString());
        m.put("empreinteServeur", l.empreinteServeur());
        m.put("fonctionnalites", l.fonctionnalites());
        return m;
    }

    // ------------------------------------------------------------------ fichier

    /** Contenu du fichier {@code .lic} (JSON lisible ; la mise en forme n'est pas signée). */
    public String versJson() {
        Map<String, Object> fichier = new LinkedHashMap<>();
        fichier.put("format", FORMAT);
        fichier.put("licence", enMap(licence));
        fichier.put("signature", signature);
        try {
            return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(fichier) + "\n";
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Lit un fichier de licence de façon stricte (clés dupliquées, types, champs manquants ou format inconnu refusés).
     * Ne vérifie PAS la signature : voir {@link #signatureValide(PublicKey)}.
     *
     * @throws LicenceInvalideException si le fichier est mal formé
     */
    public static LicenceSignee lire(String json) {
        if (json == null || json.isBlank()) {
            throw new LicenceInvalideException("fichier vide");
        }
        if (json.length() > TAILLE_MAX) {
            throw new LicenceInvalideException("fichier trop volumineux");
        }
        JsonNode racine;
        try (JsonParser parser = MAPPER.createParser(json)) {
            racine = MAPPER.readTree(parser);
        } catch (IOException e) {
            throw new LicenceInvalideException("JSON illisible", e);
        }
        if (racine == null || !racine.isObject()) {
            throw new LicenceInvalideException("le fichier doit être un objet JSON");
        }
        JsonNode format = racine.get("format");
        if (format == null || !format.isInt()) {
            throw new LicenceInvalideException("champ format manquant");
        }
        if (format.intValue() != FORMAT) {
            throw new LicenceInvalideException("format de licence non supporté : " + format.intValue());
        }
        String signature = texte(racine, "signature");
        JsonNode noeud = racine.get("licence");
        if (noeud == null || !noeud.isObject()) {
            throw new LicenceInvalideException("champ licence manquant");
        }
        try {
            Licence licence = new Licence(
                    texte(noeud, "licenseId"),
                    texte(noeud, "client"),
                    entier(noeud, "palierLots"),
                    date(noeud, "dateDebut"),
                    date(noeud, "dateExpiration"),
                    texte(noeud, "empreinteServeur"),
                    liste(noeud, "fonctionnalites"));
            return new LicenceSignee(licence, signature);
        } catch (IllegalArgumentException e) {
            throw new LicenceInvalideException("licence invalide : " + e.getMessage(), e);
        }
    }

    private static String texte(JsonNode parent, String champ) {
        JsonNode n = parent.get(champ);
        if (n == null || !n.isTextual()) {
            throw new LicenceInvalideException("champ " + champ + " manquant ou de mauvais type");
        }
        return n.textValue();
    }

    private static int entier(JsonNode parent, String champ) {
        JsonNode n = parent.get(champ);
        if (n == null || !n.isInt()) {
            throw new LicenceInvalideException("champ " + champ + " manquant ou n'est pas un entier");
        }
        return n.intValue();
    }

    private static LocalDate date(JsonNode parent, String champ) {
        try {
            return LocalDate.parse(texte(parent, champ));
        } catch (DateTimeParseException e) {
            throw new LicenceInvalideException("champ " + champ + " : date invalide (AAAA-MM-JJ attendu)", e);
        }
    }

    private static List<String> liste(JsonNode parent, String champ) {
        JsonNode n = parent.get(champ);
        if (n == null || !n.isArray()) {
            throw new LicenceInvalideException("champ " + champ + " manquant ou n'est pas une liste");
        }
        List<String> valeurs = new ArrayList<>();
        for (JsonNode element : n) {
            if (!element.isTextual()) {
                throw new LicenceInvalideException("champ " + champ + " doit contenir des textes");
            }
            valeurs.add(element.textValue());
        }
        return valeurs;
    }
}
