package com.immogest.app.tiers;

import static org.assertj.core.api.Assertions.assertThat;

import com.immogest.app.SeaweedFsTestConfiguration;
import com.immogest.app.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;
import org.springframework.test.context.TestPropertySource;

/** Module tiers isolé : une création publie l'événement d'audit, sans donnée d'identité en clair. */
@ApplicationModuleTest(mode = ApplicationModuleTest.BootstrapMode.ALL_DEPENDENCIES)
@Import({TestcontainersConfiguration.class, SeaweedFsTestConfiguration.class, com.immogest.app.PerimetreTestSupport.class})
@TestPropertySource(properties = {"immogest.dev.installation-auto=false", "db-scheduler.enabled=false"})
class TiersModuleTest {

    @Autowired
    LocataireService locataires;

    @Test
    void creerUnLocatairePublieUnEvenementSansCni(Scenario scenario) {
        scenario.stimulate(() -> locataires.creer(new LocataireSaisie("Module Locataire", null, "+237699123456", null, null, null,
                        "CNI-SECRETE-42", null, null)))
                .andWaitForEventOfType(EvenementTiers.class)
                .matching(e -> e.action().equals("TIERS.LOCATAIRE_CREE"))
                .toArriveAndVerify(e -> {
                    assertThat(e.entite()).isEqualTo("locataire");
                    assertThat(e.apres().toString()).contains("Module Locataire").doesNotContain("CNI-SECRETE-42").doesNotContain("699123456");
                });
    }
}
