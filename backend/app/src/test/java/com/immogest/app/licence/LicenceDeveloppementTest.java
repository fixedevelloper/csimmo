package com.immogest.app.licence;

import static org.assertj.core.api.Assertions.assertThat;

import com.immogest.app.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/** Profil dev (contexte par défaut) : la licence de développement est installée automatiquement au démarrage. */
@IntegrationTest
class LicenceDeveloppementTest {

    @Autowired
    LicenceService licences;
    @Autowired
    InstallationService installation;

    @Test
    void laLicenceDeDeveloppementEstValideEtLInstallationEstMarqueeFaite() {
        EtatLicenceDto etat = licences.etat();

        assertThat(etat.etat()).isEqualTo(EtatLicence.VALIDE);
        assertThat(etat.client()).isEqualTo("Développement");
        assertThat(etat.palierLots()).isEqualTo(100);
        assertThat(etat.lectureSeule()).isFalse();
        assertThat(installation.etat().installee()).isTrue();
        assertThat(installation.etat().empreinteServeur()).isNull();
    }
}
