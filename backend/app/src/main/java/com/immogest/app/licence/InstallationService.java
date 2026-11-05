package com.immogest.app.licence;

import com.immogest.app.licence.domain.Installation;
import com.immogest.app.licence.infra.InstallationRepository;
import com.immogest.app.licence.infra.RegistreInstallation;
import com.immogest.app.securite.Role;
import com.immogest.app.securite.UtilisateurService;
import com.immogest.app.shared.ErreurMetier;
import java.time.Clock;
import java.time.OffsetDateTime;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Assistant de premier lancement : crée l'ADMIN et importe la licence dans UNE transaction, une seule fois.
 * Si la licence est refusée ou si le compte ne peut pas être créé, rien n'est enregistré et l'assistant reste ouvert.
 */
@Service
public class InstallationService {

    private final InstallationRepository repository;
    private final RegistreInstallation registre;
    private final LicenceService licences;
    private final UtilisateurService utilisateurs;
    private final ApplicationEventPublisher evenements;
    private final Clock horloge;

    InstallationService(InstallationRepository repository, RegistreInstallation registre, LicenceService licences,
            UtilisateurService utilisateurs, ApplicationEventPublisher evenements, Clock horloge) {
        this.repository = repository;
        this.registre = registre;
        this.licences = licences;
        this.utilisateurs = utilisateurs;
        this.evenements = evenements;
        this.horloge = horloge;
    }

    @Transactional(readOnly = true)
    public EtatInstallationDto etat() {
        registre.identifiant(); // crée la ligne au besoin
        Installation i = repository.findBySingletonTrue().orElseThrow();
        return new EtatInstallationDto(i.isInstallee(), i.isInstallee() ? null : registre.empreinte().orElse(null));
    }

    @Transactional
    public void installer(DemandeInstallation demande) {
        registre.identifiant();
        Installation i = repository.verrouiller().orElseThrow();
        if (i.isInstallee()) {
            throw new ErreurMetier("INSTALLATION_DEJA_FAITE", "L'installation a déjà été effectuée", HttpStatus.CONFLICT);
        }
        EtatLicenceDto licence = licences.importer(demande.licence());
        utilisateurs.creer(new UtilisateurService.Saisie(demande.nom(), demande.telephone(), demande.email(),
                Role.ADMIN, true, false), demande.motDePasse());
        i.marquerInstallee(OffsetDateTime.now(horloge));
        repository.save(i);
        evenements.publishEvent(new InstallationTerminee(i.getId(), licence.client()));
    }

    /** Profil dev : marque l'installation faite sans créer de compte (l'ADMIN de dev existe déjà). */
    @Transactional
    public void marquerInstallee() {
        registre.identifiant();
        Installation i = repository.verrouiller().orElseThrow();
        if (!i.isInstallee()) {
            i.marquerInstallee(OffsetDateTime.now(horloge));
            repository.save(i);
        }
    }
}
