package com.immogest.app;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

/** Garde-fou : échoue si un module accède à l'intérieur d'un autre ou si des cycles apparaissent. */
class ModularityTests {

    private final ApplicationModules modules = ApplicationModules.of(ImmoGestApplication.class);

    @Test
    void verifieLesFrontieresDeModules() {
        modules.verify();
    }

    @Test
    void genereLaDocumentation() {
        new Documenter(modules, Documenter.Options.defaults().withOutputFolder("../../docs/modules"))
                .writeModulesAsPlantUml()
                .writeIndividualModulesAsPlantUml()
                .writeModuleCanvases();
    }
}
