package com.immogest.app.patrimoine;

import static org.assertj.core.api.Assertions.assertThat;

import com.immogest.app.licence.LicenceTestSupport;
import com.immogest.app.patrimoine.domain.Immeuble;
import com.immogest.app.patrimoine.domain.Lot;
import com.immogest.app.patrimoine.infra.ImmeubleRepository;
import com.immogest.app.patrimoine.infra.LotRepository;
import com.immogest.app.shared.BailActive;
import com.immogest.app.shared.BailTermine;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;
import org.springframework.test.context.TestPropertySource;

/** Module patrimoine isolé : il réagit aux événements du bail sans en dépendre (outbox Modulith). */
@ApplicationModuleTest(mode = ApplicationModuleTest.BootstrapMode.ALL_DEPENDENCIES)
@Import({LicenceTestSupport.class, com.immogest.app.PerimetreTestSupport.class})
@TestPropertySource(properties = {"immogest.dev.installation-auto=false", "db-scheduler.enabled=false"})
class PatrimoineModuleTest {

    @Autowired
    ImmeubleRepository immeubles;
    @Autowired
    LotRepository lots;

    private UUID nouveauLot() {
        Immeuble immeuble = immeubles.save(new Immeuble("Module", "1 rue du Test", null, "Douala", null, null, null, null));
        return lots.save(new Lot(immeuble, "M-" + System.nanoTime(), TypeLot.STUDIO, null, null, null)).getId();
    }

    @Test
    void bailActivePasseLeLotEnOccupe(Scenario scenario) {
        UUID lot = nouveauLot();

        scenario.publish(new BailActive(UUID.randomUUID(), lot))
                .andWaitForStateChange(() -> lots.findById(lot).orElseThrow().getStatut(), statut -> statut == StatutLot.OCCUPE)
                .andVerify(statut -> assertThat(statut).isEqualTo(StatutLot.OCCUPE));
    }

    @Test
    void bailTerminePasseLeLotEnLibre(Scenario scenario) {
        UUID lot = nouveauLot();
        UUID bail = UUID.randomUUID();
        scenario.publish(new BailActive(bail, lot))
                .andWaitForStateChange(() -> lots.findById(lot).orElseThrow().getStatut(), statut -> statut == StatutLot.OCCUPE)
                .andVerify(statut -> assertThat(statut).isEqualTo(StatutLot.OCCUPE));

        scenario.publish(new BailTermine(bail, lot))
                .andWaitForStateChange(() -> lots.findById(lot).orElseThrow().getStatut(), statut -> statut == StatutLot.LIBRE)
                .andVerify(statut -> assertThat(statut).isEqualTo(StatutLot.LIBRE));
    }
}
