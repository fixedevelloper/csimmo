package com.immogest.app.licence.infra;

import com.immogest.app.licence.LicenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** Au démarrage : crée l'identifiant d'installation et vérifie la licence (signature, empreinte, dates). */
@Component
@Order(10)
class DemarrageLicence implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(DemarrageLicence.class);

    private final RegistreInstallation registre;
    private final LicenceService licences;

    DemarrageLicence(RegistreInstallation registre, LicenceService licences) {
        this.registre = registre;
        this.licences = licences;
    }

    @Override
    public void run(ApplicationArguments args) {
        registre.identifiant();
        if (registre.empreinte().isEmpty()) {
            LOG.error("Identifiant machine introuvable : définir IMMOGEST_MACHINE_ID (Docker : identifiant de l'hôte)");
        }
        try {
            licences.controleQuotidien();
        } catch (RuntimeException e) {
            LOG.error("Contrôle de licence au démarrage en erreur : {}", e.toString());
        }
        LOG.info("Licence : {}", licences.etat().etat());
    }
}
