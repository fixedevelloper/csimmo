package com.immogest.app.securite.infra;

import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.OptionalLong;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Mots de passe à usage unique basés sur le temps (TOTP, RFC 6238 : HMAC-SHA1, pas de 30 s, 6 chiffres), compatibles avec Google Authenticator,
 * Microsoft Authenticator, Authy, FreeOTP, 1Password, Bitwarden… Aucune dépendance externe.
 */
public final class Totp {

    public static final int PAS_SECONDES = 30;
    public static final int CHIFFRES = 6;
    /** Tolérance d'horloge : le pas précédent et le suivant sont acceptés (±30 s). */
    private static final int FENETRE = 1;
    private static final int OCTETS_SECRET = 20; // 160 bits, la taille d'une clé HMAC-SHA1
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
    private static final SecureRandom ALEATOIRE = new SecureRandom();

    private Totp() {
    }

    /** Nouveau secret aléatoire, en base 32 (32 caractères, à saisir ou à scanner dans l'application d'authentification). */
    public static String genererSecret() {
        byte[] octets = new byte[OCTETS_SECRET];
        ALEATOIRE.nextBytes(octets);
        return encoderBase32(octets);
    }

    /** Pas de temps (compteur) correspondant à l'instant donné. */
    public static long pas(Instant instant) {
        return Math.floorDiv(instant.getEpochSecond(), PAS_SECONDES);
    }

    /** Code à {@code chiffres} chiffres du compteur {@code pas}. */
    static String code(byte[] secret, long pas, int chiffres) {
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(secret, "HmacSHA1"));
            byte[] h = mac.doFinal(ByteBuffer.allocate(8).putLong(pas).array());
            int decalage = h[h.length - 1] & 0x0f;
            int binaire = ((h[decalage] & 0x7f) << 24) | ((h[decalage + 1] & 0xff) << 16) | ((h[decalage + 2] & 0xff) << 8) | (h[decalage + 3] & 0xff);
            long modulo = 1;
            for (int i = 0; i < chiffres; i++) {
                modulo *= 10;
            }
            return String.format("%0" + chiffres + "d", binaire % modulo);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("HmacSHA1 indisponible", e);
        }
    }

    /**
     * Vérifie un code saisi. Renvoie le pas qui correspond, ou rien si le code est faux ou déjà utilisé : un pas inférieur ou égal à
     * {@code dernierPas} est refusé (un code ne sert qu'une fois, même dans sa fenêtre de 90 s).
     */
    public static OptionalLong verifier(String secretBase32, String saisie, Instant maintenant, long dernierPas) {
        if (saisie == null) {
            return OptionalLong.empty();
        }
        String propre = saisie.replaceAll("\\s", "");
        if (!propre.matches("\\d{" + CHIFFRES + "}")) {
            return OptionalLong.empty();
        }
        byte[] secret = decoderBase32(secretBase32);
        long courant = pas(maintenant);
        OptionalLong trouve = OptionalLong.empty();
        for (long p = courant - FENETRE; p <= courant + FENETRE; p++) {
            // tous les pas sont comparés, sans sortie anticipée : le temps de réponse ne dit pas lequel convient
            boolean egal = MessageDigest.isEqual(code(secret, p, CHIFFRES).getBytes(StandardCharsets.US_ASCII), propre.getBytes(StandardCharsets.US_ASCII));
            if (egal && p > dernierPas && (trouve.isEmpty() || p > trouve.getAsLong())) {
                trouve = OptionalLong.of(p);
            }
        }
        return trouve;
    }

    /** Adresse {@code otpauth://} à encoder en code QR (ou à ouvrir) pour enregistrer le compte dans l'application d'authentification. */
    public static String uri(String emetteur, String compte, String secretBase32) {
        String e = encoderUrl(emetteur);
        return "otpauth://totp/" + e + ":" + encoderUrl(compte) + "?secret=" + secretBase32 + "&issuer=" + e
                + "&algorithm=SHA1&digits=" + CHIFFRES + "&period=" + PAS_SECONDES;
    }

    private static String encoderUrl(String texte) {
        return URLEncoder.encode(texte, StandardCharsets.UTF_8).replace("+", "%20");
    }

    // ------------------------------------------------------------------ base 32 (RFC 4648)

    static String encoderBase32(byte[] octets) {
        StringBuilder sb = new StringBuilder((octets.length * 8 + 4) / 5);
        int tampon = 0;
        int bits = 0;
        for (byte b : octets) {
            tampon = (tampon << 8) | (b & 0xff);
            bits += 8;
            while (bits >= 5) {
                sb.append(ALPHABET.charAt((tampon >> (bits - 5)) & 31));
                bits -= 5;
            }
            tampon &= (1 << bits) - 1;
        }
        if (bits > 0) {
            sb.append(ALPHABET.charAt((tampon << (5 - bits)) & 31));
        }
        return sb.toString();
    }

    static byte[] decoderBase32(String texte) {
        String propre = texte.replaceAll("[\\s=-]", "").toUpperCase();
        byte[] sortie = new byte[propre.length() * 5 / 8];
        int tampon = 0;
        int bits = 0;
        int n = 0;
        for (char c : propre.toCharArray()) {
            int v = ALPHABET.indexOf(c);
            if (v < 0) {
                throw new IllegalArgumentException("caractère base 32 invalide");
            }
            tampon = (tampon << 5) | v;
            bits += 5;
            if (bits >= 8) {
                sortie[n++] = (byte) ((tampon >> (bits - 8)) & 0xff);
                bits -= 8;
                tampon &= (1 << bits) - 1;
            }
        }
        return sortie;
    }
}
