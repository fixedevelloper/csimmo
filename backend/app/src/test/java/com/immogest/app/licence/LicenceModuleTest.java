package com.immogest.app.licence;

import static org.assertj.core.api.Assertions.assertThat;

import com.immogest.licensing.Licence;
import com.immogest.licensing.LicenceSignee;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;

/** Module licence isolé (avec ses dépendances directes) : les événements publiés arrivent bien (outbox). */
@ApplicationModuleTest(mode = ApplicationModuleTest.BootstrapMode.ALL_DEPENDENCIES)
@Import(LicenceTestSupport.class)
@TestPropertySource(properties = {"immogest.dev.installation-auto=false", "db-scheduler.enabled=false"})
class LicenceModuleTest {

    @Autowired
    LicenceService licences;

    private String contenu(String id, int joursAvantExpiration) {
        LocalDate jour = LocalDate.now(ZoneId.of("Africa/Douala"));
        return LicenceSignee.signer(new Licence(id, "Client Module", 25, jour.minusDays(1), jour.plusDays(joursAvantExpiration),
                LicenceTestSupport.EMPREINTE, List.of()), LicenceTestSupport.EDITEUR.getPrivate()).versJson();
    }

    @Test
    void importerUneLicencePublieLicenceImportee(Scenario scenario) {
        String contenu = contenu("L-MODULE-" + System.nanoTime(), 400);

        scenario.stimulate(() -> licences.importer(contenu))
                .andWaitForEventOfType(LicenceImportee.class)
                .matching(e -> e.client().equals("Client Module"))
                .toArriveAndVerify(e -> {
                    assertThat(e.action()).isEqualTo("LICENCE.IMPORTEE");
                    assertThat(e.dateExpiration()).isAfter(LocalDate.now());
                });
    }
}
