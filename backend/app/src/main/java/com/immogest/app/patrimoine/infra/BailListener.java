package com.immogest.app.patrimoine.infra;

import com.immogest.app.patrimoine.PatrimoineService;
import com.immogest.app.shared.BailActive;
import com.immogest.app.shared.BailTermine;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

/** Le statut d'un lot suit son bail : OCCUPE à l'activation, LIBRE à la fin (jamais d'appel direct depuis bail). */
@Component
class BailListener {

    private final PatrimoineService patrimoine;

    BailListener(PatrimoineService patrimoine) {
        this.patrimoine = patrimoine;
    }

    @ApplicationModuleListener
    void surBailActive(BailActive evenement) {
        patrimoine.marquerOccupe(evenement.lotId());
    }

    @ApplicationModuleListener
    void surBailTermine(BailTermine evenement) {
        patrimoine.marquerLibre(evenement.lotId());
    }
}
