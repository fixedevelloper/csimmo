package com.immogest.licensing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import org.junit.jupiter.api.Test;

class Ed25519Test {

    private static final byte[] DONNEES = "contenu".getBytes(StandardCharsets.UTF_8);

    @Test
    void signatureEtVerification() {
        KeyPair paire = Ed25519.genererPaire();
        byte[] signature = Ed25519.signer(DONNEES, paire.getPrivate());

        assertThat(signature).hasSize(64);
        assertThat(Ed25519.verifier(DONNEES, signature, paire.getPublic())).isTrue();
        assertThat(Ed25519.verifier("autre".getBytes(StandardCharsets.UTF_8), signature, paire.getPublic())).isFalse();
    }

    @Test
    void signatureDeterministe() {
        var privee = Fixtures.clePrivee();
        assertThat(Ed25519.signer(DONNEES, privee)).isEqualTo(Ed25519.signer(DONNEES, privee));
    }

    @Test
    void uneAutreCleNeVerifiePas() {
        byte[] signature = Ed25519.signer(DONNEES, Ed25519.genererPaire().getPrivate());
        assertThat(Ed25519.verifier(DONNEES, signature, Fixtures.clePublique())).isFalse();
    }

    @Test
    void signatureDeMauvaiseTailleOuVideRefusee() {
        assertThat(Ed25519.verifier(DONNEES, new byte[10], Fixtures.clePublique())).isFalse();
        assertThat(Ed25519.verifier(DONNEES, new byte[0], Fixtures.clePublique())).isFalse();
        assertThat(Ed25519.verifier(DONNEES, new byte[64], Fixtures.clePublique())).isFalse();
    }

    @Test
    void encodageEtDecodageDesCles() {
        KeyPair paire = Ed25519.genererPaire();

        assertThat(Ed25519.decoderClePublique(Ed25519.encoder(paire.getPublic()))).isEqualTo(paire.getPublic());
        assertThat(Ed25519.decoderClePrivee(Ed25519.encoder(paire.getPrivate()))).isEqualTo(paire.getPrivate());
        assertThat(Ed25519.decoderClePublique("  " + Ed25519.encoder(paire.getPublic()) + "\n")).isEqualTo(paire.getPublic());
    }

    @Test
    void clesInvalidesRefusees() throws Exception {
        assertThatThrownBy(() -> Ed25519.decoderClePublique("pas du base64 !!")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Ed25519.decoderClePublique("AAAA")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Ed25519.decoderClePrivee("")).isInstanceOf(IllegalArgumentException.class);

        // Une clé publique d'un autre algorithme n'est pas acceptée comme clé Ed25519
        KeyPairGenerator rsa = KeyPairGenerator.getInstance("RSA");
        rsa.initialize(2048);
        String cleRsa = java.util.Base64.getEncoder().encodeToString(rsa.generateKeyPair().getPublic().getEncoded());
        assertThatThrownBy(() -> Ed25519.decoderClePublique(cleRsa)).isInstanceOf(IllegalArgumentException.class);
    }
}
