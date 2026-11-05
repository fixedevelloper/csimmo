package com.immogest.app.finance.infra;

import com.immogest.app.finance.EcheanceService;
import com.immogest.app.shared.BailActive;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

/** À l'activation d'un bail : premières échéances et imputation de l'avance, sans attendre la nuit. */
@Component
class EcheancesBailListener {

    private final EcheanceService echeances;

    EcheancesBailListener(EcheanceService echeances) {
        this.echeances = echeances;
    }

    @ApplicationModuleListener
    void surBailActive(BailActive evenement) {
        echeances.genererPourBail(evenement.bailId());
    }
}
