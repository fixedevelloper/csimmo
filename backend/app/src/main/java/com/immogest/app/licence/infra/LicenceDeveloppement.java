package com.immogest.app.licence.infra;

import com.immogest.app.licence.InstallationService;
import com.immogest.app.licence.LicenceService;
import com.immogest.licensing.Ed25519;
import com.immogest.licensing.Licence;
import com.immogest.licensing.LicenceSignee;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Profil dev uniquement ({@code immogest.dev.installation-auto=true}) : signe et importe une licence de développement,
 * et marque l'installation faite, pour travailler sans passer par l'assistant. Jamais actif en production.
 */
@Component
@Profile("dev")
@ConditionalOnProperty(name = "immogest.dev.installation-auto", havingValue = "true")
@Order(20)
class LicenceDeveloppement implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(LicenceDeveloppement.class);

    private final RegistreInstallation registre;
    private final LicenceService licences;
    private final InstallationService installation;
    private final LicenceProperties proprietes;

    LicenceDeveloppement(RegistreInstallation registre, LicenceService licences, InstallationService installation,
            LicenceProperties proprietes) {
        this.registre = registre;
        this.licences = licences;
        this.installation = installation;
        this.proprietes = proprietes;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (installation.etat().installee() || proprietes.devClePrivee() == null || proprietes.devClePrivee().isBlank()) {
            return;
        }
        var empreinte = registre.empreinte();
        if (empreinte.isEmpty()) {
            LOG.warn("Licence de développement non créée : identifiant machine introuvable");
            return;
        }
        LocalDate aujourdhui = LocalDate.now(ZoneId.of("Africa/Douala"));
        Licence licence = new Licence("L-DEV-" + aujourdhui, "Développement", 100, aujourdhui,
                aujourdhui.plusYears(1), empreinte.get(), List.of("relances", "mobile", "paiement-en-ligne"));
        String contenu = LicenceSignee.signer(licence, Ed25519.decoderClePrivee(proprietes.devClePrivee())).versJson();
        try {
            licences.importer(contenu);
            installation.marquerInstallee();
            LOG.warn("Licence de DÉVELOPPEMENT installée ({} lots, jusqu'au {}) : ne jamais utiliser en production",
                    licence.palierLots(), licence.dateExpiration());
        } catch (RuntimeException e) {
            LOG.warn("Licence de développement refusée : {}", e.getMessage());
        }
    }
}
