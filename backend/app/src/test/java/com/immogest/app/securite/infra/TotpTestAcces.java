package com.immogest.app.securite.infra;

/** Accès de test aux fonctions internes de {@link Totp}. */
public final class TotpTestAcces {

    private TotpTestAcces() {
    }

    public static byte[] decoder(String base32) {
        return Totp.decoderBase32(base32);
    }

    public static String code(String base32, long pas) {
        return Totp.code(Totp.decoderBase32(base32), pas, Totp.CHIFFRES);
    }
}
