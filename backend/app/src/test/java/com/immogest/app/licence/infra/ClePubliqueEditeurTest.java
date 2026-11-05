package com.immogest.app.licence.infra;

import static org.assertj.core.api.Assertions.assertThat;

import com.immogest.licensing.Ed25519;
import java.security.KeyPair;
import org.junit.jupiter.api.Test;

class ClePubliqueEditeurTest {

    private final KeyPair paire = Ed25519.genererPaire();

    @Test
    void laCleConfigureeEstUtilisee() {
        assertThat(ClePubliqueEditeur.charger(Ed25519.encoder(paire.getPublic()), "licence/inexistante.txt"))
                .isEqualTo(paire.getPublic());
    }

    @Test
    void sansConfigurationNiRessourceAucuneCle() {
        assertThat(ClePubliqueEditeur.charger(null, "licence/inexistante.txt")).isNull();
        assertThat(ClePubliqueEditeur.charger("  ", "licence/inexistante.txt")).isNull();
    }

    @Test
    void uneRessourceFaiteUniquementDeCommentairesNeDonneAucuneCle() {
        assertThat(ClePubliqueEditeur.charger(null, "licence/cle-commentaires-seuls.txt")).isNull();
    }

    @Test
    void uneCleIllisibleEstIgnoreeSansExceptionEtNeBasculePasSurUneAutreCle() {
        assertThat(ClePubliqueEditeur.charger("pas-une-cle", "licence/inexistante.txt")).isNull();
        assertThat(ClePubliqueEditeur.charger("pas-une-cle", ClePubliqueEditeur.RESSOURCE))
                .as("une clé configurée mais invalide ne retombe jamais sur la clé embarquée").isNull();
    }

    @Test
    void uneCleDUnAutreAlgorithmeEstRefusee() throws Exception {
        var generateur = java.security.KeyPairGenerator.getInstance("RSA");
        generateur.initialize(2048);
        String rsa = java.util.Base64.getEncoder().encodeToString(generateur.generateKeyPair().getPublic().getEncoded());

        assertThat(ClePubliqueEditeur.charger(rsa, "licence/inexistante.txt")).isNull();
    }
}
