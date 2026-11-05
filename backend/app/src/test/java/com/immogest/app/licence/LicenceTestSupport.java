package com.immogest.app.licence;

import com.immogest.app.SeaweedFsTestConfiguration;
import com.immogest.app.TestcontainersConfiguration;
import com.immogest.app.licence.infra.FournisseurEmpreinte;
import com.immogest.licensing.Ed25519;
import java.security.KeyPair;
import java.util.Optional;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.DynamicPropertyRegistrar;

/**
 * Environnement de test du module licence : clé de l'éditeur de test, empreinte serveur fixe, horloge pilotable
 * et serveur de licences simulé.
 */
@TestConfiguration(proxyBeanMethods = false)
@Import({TestcontainersConfiguration.class, SeaweedFsTestConfiguration.class})
public class LicenceTestSupport {

    /** Clé de l'éditeur pour les tests (la clé publique est injectée dans la configuration). */
    public static final KeyPair EDITEUR = Ed25519.genererPaire();
    /** Une autre paire : simule un faussaire. */
    public static final KeyPair FAUSSAIRE = Ed25519.genererPaire();
    public static final String EMPREINTE = "ab".repeat(32);

    @Bean
    DynamicPropertyRegistrar clePubliqueDeTest() {
        return registre -> registre.add("immogest.licence.cle-publique", () -> Ed25519.encoder(EDITEUR.getPublic()));
    }

    @Bean
    @Primary
    FournisseurEmpreinte empreinteDeTest() {
        return identifiantInstallation -> Optional.of(EMPREINTE);
    }

    @Bean
    @Primary
    MutableClock horlogeDeTest() {
        return new MutableClock();
    }

    @Bean
    @Primary
    ServeurLicencesFake serveurDeLicencesSimule() {
        return new ServeurLicencesFake();
    }
}
