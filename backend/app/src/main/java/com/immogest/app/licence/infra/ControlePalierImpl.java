package com.immogest.app.licence.infra;

import com.immogest.app.licence.EtatLicence;
import com.immogest.app.licence.EtatLicenceDto;
import com.immogest.app.licence.LicenceService;
import com.immogest.app.licence.api.ControlePalier;
import com.immogest.app.shared.ErreurMetier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
class ControlePalierImpl implements ControlePalier {

    private final LicenceService licences;

    ControlePalierImpl(LicenceService licences) {
        this.licences = licences;
    }

    @Override
    public void verifierCreationLot(long nombreDeLotsActuel) {
        EtatLicenceDto etat = licences.etat();
        if (etat.etat() != EtatLicence.VALIDE) {
            throw new ErreurMetier(LicenceFilter.CODE, "Licence non valide : création impossible", HttpStatus.FORBIDDEN);
        }
        if (nombreDeLotsActuel >= etat.palierLots()) {
            throw new ErreurMetier("LICENCE_PALIER_ATTEINT",
                    "Le palier de %d lots de votre licence est atteint".formatted(etat.palierLots()), HttpStatus.CONFLICT);
        }
    }

    @Override
    public boolean ecrituresAutorisees() {
        return licences.ecrituresAutorisees();
    }

    @Override
    public int palierLots() {
        EtatLicenceDto etat = licences.etat();
        return etat.etat() == EtatLicence.VALIDE ? etat.palierLots() : 0;
    }
}
