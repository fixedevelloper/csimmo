package com.immogest.app.licence.infra;

import com.immogest.licensing.EmpreinteServeur;
import com.immogest.licensing.IdentifiantMachine;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
class FournisseurEmpreinteSysteme implements FournisseurEmpreinte {

    private final LicenceProperties proprietes;

    FournisseurEmpreinteSysteme(LicenceProperties proprietes) {
        this.proprietes = proprietes;
    }

    @Override
    public Optional<String> empreinte(String identifiantInstallation) {
        Optional<String> machine = IdentifiantMachine.detecter();
        if (machine.isEmpty() && proprietes.identifiantMachineSecours() != null
                && !proprietes.identifiantMachineSecours().isBlank()) {
            machine = Optional.of(proprietes.identifiantMachineSecours());
        }
        return machine.map(m -> EmpreinteServeur.calculer(m, identifiantInstallation));
    }
}
