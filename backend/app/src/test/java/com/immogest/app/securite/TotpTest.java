package com.immogest.app.securite;

import static org.assertj.core.api.Assertions.assertThat;

import com.immogest.app.securite.infra.Totp;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.OptionalLong;
import org.junit.jupiter.api.Test;

/** TOTP (RFC 6238) : vecteurs officiels, fenêtre de tolérance, rejeu, base 32 et adresse otpauth. */
class TotpTest {

    /** Secret ASCII « 12345678901234567890 » de l'annexe B du RFC 6238 (SHA-1), en base 32. */
    private static final String SECRET_RFC = "GEZDGNBVGY3TQOJQGEZDGNBVGY3TQOJQ";

    @Test
    void les_vecteurs_du_rfc_6238_donnent_les_bons_codes() throws Exception {
        // codes à 8 chiffres du RFC ; les 6 derniers chiffres sont les codes à 6 chiffres
        long[][] cas = {{59, 94287082}, {1111111109, 7081804}, {1111111111, 14050471}, {1234567890, 89005924}, {2000000000, 69279037}, {20000000000L, 65353130}};
        var m = Totp.class.getDeclaredMethod("code", byte[].class, long.class, int.class);
        m.setAccessible(true);
        byte[] secret = "12345678901234567890".getBytes(StandardCharsets.US_ASCII);
        for (long[] c : cas) {
            assertThat((String) m.invoke(null, secret, c[0] / 30, 8)).isEqualTo("%08d".formatted(c[1]));
            assertThat((String) m.invoke(null, secret, c[0] / 30, 6)).isEqualTo("%06d".formatted(c[1] % 1_000_000));
        }
    }

    @Test
    void verifie_un_code_avec_une_tolerance_d_un_pas_de_chaque_cote() {
        Instant t = Instant.ofEpochSecond(1111111109L); // pas 37037036 ; code 081804
        assertThat(Totp.verifier(SECRET_RFC, "081804", t, 0)).isPresent();
        assertThat(Totp.verifier(SECRET_RFC, "081804", t.plusSeconds(30), 0)).isPresent();   // pas suivant
        assertThat(Totp.verifier(SECRET_RFC, "081804", t.minusSeconds(30), 0)).isPresent();  // pas précédent
        assertThat(Totp.verifier(SECRET_RFC, "081804", t.plusSeconds(95), 0)).isEmpty();     // hors fenêtre
        assertThat(Totp.verifier(SECRET_RFC, "081805", t, 0)).isEmpty();                      // faux
    }

    @Test
    void un_code_ne_sert_qu_une_fois() {
        Instant t = Instant.ofEpochSecond(1111111109L);
        OptionalLong pas = Totp.verifier(SECRET_RFC, "081804", t, 0);
        assertThat(pas).isPresent();
        assertThat(Totp.verifier(SECRET_RFC, "081804", t, pas.getAsLong())).isEmpty(); // rejeu refusé
        assertThat(Totp.verifier(SECRET_RFC, "081804", t, pas.getAsLong() - 1)).isPresent();
    }

    @Test
    void refuse_les_saisies_mal_formees_et_tolere_les_espaces() {
        Instant t = Instant.ofEpochSecond(1111111109L);
        assertThat(Totp.verifier(SECRET_RFC, null, t, 0)).isEmpty();
        assertThat(Totp.verifier(SECRET_RFC, "", t, 0)).isEmpty();
        assertThat(Totp.verifier(SECRET_RFC, "08180", t, 0)).isEmpty();
        assertThat(Totp.verifier(SECRET_RFC, "0818041", t, 0)).isEmpty();
        assertThat(Totp.verifier(SECRET_RFC, "abcdef", t, 0)).isEmpty();
        assertThat(Totp.verifier(SECRET_RFC, "081 804", t, 0)).isPresent(); // l'application affiche « 081 804 »
    }

    @Test
    void les_secrets_generes_sont_en_base_32_et_differents() {
        String a = Totp.genererSecret();
        String b = Totp.genererSecret();
        assertThat(a).matches("[A-Z2-7]{32}").isNotEqualTo(b);
        // un secret généré fonctionne avec lui-même
        Instant t = Instant.now();
        byte[] octets = com.immogest.app.securite.infra.TotpTestAcces.decoder(a);
        assertThat(octets).hasSize(20);
    }

    @Test
    void l_adresse_otpauth_est_encodee() {
        assertThat(Totp.uri("ImmoGest", "boris@x.cm", SECRET_RFC)).isEqualTo(
                "otpauth://totp/ImmoGest:boris%40x.cm?secret=" + SECRET_RFC + "&issuer=ImmoGest&algorithm=SHA1&digits=6&period=30");
        assertThat(Totp.uri("Agence Dupont & Fils", "+237 690 000 001", SECRET_RFC)).contains("Agence%20Dupont%20%26%20Fils:%2B237%20690%20000%20001");
    }
}
