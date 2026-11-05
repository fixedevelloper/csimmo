package com.immogest.app.licence;

import static org.assertj.core.api.Assertions.assertThat;

import com.immogest.app.licence.ServeurLicencesClient.ReponseVerification.Statut;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

/** Vérification en ligne hebdomadaire et délai de grâce de 30 jours (serveur de licences simulé). */
@TestPropertySource(properties = "immogest.licence.serveur-url=http://serveur-licences.test")
class LicenceEnLigneTest extends BaseLicenceTest {

    private int appels() {
        return serveur.appels.size();
    }

    // ------------------------------------------------------------------ rythme des vérifications

    @Test
    void uneVerificationParSemaineAuPlus() {
        licences.importer(contenuValide());

        licences.controleQuotidien(); // première vérification
        licences.controleQuotidien(); // même jour : pas de nouvel appel
        assertThat(appels()).isEqualTo(1);

        for (int jour = 1; jour <= 6; jour++) {
            horloge.avancerJours(1);
            licences.controleQuotidien();
        }
        assertThat(appels()).as("6 jours plus tard : toujours une seule vérification").isEqualTo(1);

        horloge.avancerJours(1); // 7 jours
        licences.controleQuotidien();
        assertThat(appels()).isEqualTo(2);
    }

    @Test
    void laDemandeContientLaLicenceEtLEmpreinteDeCeServeur() {
        licences.importer(contenuValide());

        licences.controleQuotidien();

        assertThat(serveur.appels).singleElement().satisfies(demande -> {
            assertThat(demande.licenseId()).isEqualTo("L-TEST-1");
            assertThat(demande.empreinteServeur()).isEqualTo(LicenceTestSupport.EMPREINTE);
            assertThat(demande.versionApplication()).isNotBlank();
        });
    }

    @Test
    void apresUnEchecLeServeurEstRetesteLeLendemainEtNonPlusTot() {
        licences.importer(contenuValide());
        serveur.tomberEnPanne();

        licences.controleQuotidien();
        licences.controleQuotidien();
        assertThat(appels()).as("un seul essai par jour même en échec").isEqualTo(1);

        horloge.avancerJours(1);
        licences.controleQuotidien();
        assertThat(appels()).isEqualTo(2);
    }

    @Test
    void uneConfirmationDuServeurRepousseLeDelaiDeGrace() {
        licences.importer(contenuValide());
        horloge.avancerJours(20);

        licences.controleQuotidien(); // le serveur confirme

        EtatLicenceDto etat = licences.etat();
        assertThat(etat.etat()).isEqualTo(EtatLicence.VALIDE);
        assertThat(etat.joursDeGraceRestants()).isEqualTo(30);
        assertThat(etat.derniereVerificationEnLigne()).isNotNull();
        assertThat(etat.verificationEnLigneActive()).isTrue();
    }

    // ------------------------------------------------------------------ délai de grâce hors ligne

    @Test
    void lApplicationFonctionneHorsLigneJusquAu30eJour() {
        licences.importer(contenuValide());
        serveur.tomberEnPanne();

        for (int jour = 1; jour <= 29; jour++) {
            horloge.avancerJours(1);
            licences.controleQuotidien(); // injoignable chaque jour
        }

        EtatLicenceDto etat = licences.etat();
        assertThat(etat.etat()).isEqualTo(EtatLicence.VALIDE);
        assertThat(etat.joursDeGraceRestants()).isEqualTo(1);
        assertThat(licences.ecrituresAutorisees()).isTrue();

        horloge.avancerJours(1); // 30 jours pile : encore dans la grâce
        licences.invalider();
        assertThat(licences.etat().etat()).isEqualTo(EtatLicence.VALIDE);
    }

    @Test
    void auDela30JoursSansConfirmationLApplicationPasseEnLectureSeule() {
        licences.importer(contenuValide());
        serveur.tomberEnPanne();

        horloge.avancerJours(30);
        horloge.avancer(Duration.ofHours(1));
        licences.controleQuotidien();

        EtatLicenceDto etat = licences.etat();
        assertThat(etat.etat()).isEqualTo(EtatLicence.VERIFICATION_REQUISE);
        assertThat(etat.lectureSeule()).isTrue();
        assertThat(etat.joursDeGraceRestants()).isZero();
        assertThat(licences.ecrituresAutorisees()).isFalse();
    }

    @Test
    void leRetourDuReseauRendLesEcritures() {
        licences.importer(contenuValide());
        serveur.tomberEnPanne();
        horloge.avancerJours(31);
        licences.controleQuotidien();
        assertThat(licences.etat().etat()).isEqualTo(EtatLicence.VERIFICATION_REQUISE);

        serveur.repondre(Statut.VALIDE);
        horloge.avancerJours(1);
        licences.controleQuotidien();

        assertThat(licences.etat().etat()).isEqualTo(EtatLicence.VALIDE);
        assertThat(licences.ecrituresAutorisees()).isTrue();
    }

    @Test
    void reimporterLaMemeLicenceNeRallongePasLaGrace() {
        String fichier = contenuValide();
        licences.importer(fichier);
        serveur.tomberEnPanne();
        horloge.avancerJours(20);
        licences.importer(fichier); // exactement le même fichier : aucun effet

        horloge.avancerJours(11); // 31 jours depuis le premier import
        licences.controleQuotidien();

        assertThat(licences.etat().etat()).isEqualTo(EtatLicence.VERIFICATION_REQUISE);
    }

    @Test
    void uneNouvelleLicenceRepartPourTrenteJours() {
        licences.importer(contenuValide());
        serveur.tomberEnPanne();
        horloge.avancerJours(20);
        licences.importer(signer(licence("L-TEST-2", 50, aujourdhui().minusDays(1), aujourdhui().plusDays(700))));

        horloge.avancerJours(11); // 31 jours depuis le premier import, 11 depuis le renouvellement
        licences.controleQuotidien();

        assertThat(licences.etat().etat()).isEqualTo(EtatLicence.VALIDE);
        assertThat(licences.etat().joursDeGraceRestants()).isEqualTo(19);
    }

    // ------------------------------------------------------------------ révocation, licence inconnue, renouvellement

    @Test
    void uneLicenceRevoqueeEstEnLectureSeuleEtLEvenementEstPublieUneFois() {
        licences.importer(contenuValide());
        serveur.repondre(Statut.REVOQUEE);

        licences.controleQuotidien();

        assertThat(licences.etat().etat()).isEqualTo(EtatLicence.REVOQUEE);
        assertThat(licences.ecrituresAutorisees()).isFalse();
        assertThat(nombreD(LicenceRevoquee.class)).isEqualTo(1);

        horloge.avancerJours(7);
        licences.controleQuotidien(); // toujours révoquée : pas de nouvel événement
        assertThat(nombreD(LicenceRevoquee.class)).isEqualTo(1);
    }

    @Test
    void uneRevocationLeveeParLeServeurRendLaLicence() {
        licences.importer(contenuValide());
        serveur.repondre(Statut.REVOQUEE);
        licences.controleQuotidien();
        assertThat(licences.etat().etat()).isEqualTo(EtatLicence.REVOQUEE);

        serveur.repondre(Statut.VALIDE);
        horloge.avancerJours(7);
        licences.controleQuotidien();

        assertThat(licences.etat().etat()).isEqualTo(EtatLicence.VALIDE);
    }

    @Test
    void uneLicenceInconnueDuServeurNeCompteJamaisCommeUneConfirmation() {
        licences.importer(contenuValide());
        serveur.repondre(Statut.INCONNUE);

        horloge.avancerJours(31);
        licences.controleQuotidien();

        assertThat(licences.etat().etat()).isEqualTo(EtatLicence.VERIFICATION_REQUISE);
    }

    @Test
    void unRenouvellementEnvoyeParLeServeurEstImporteAutomatiquement() {
        licences.importer(contenuValide());
        String renouvelee = signer(licence("L-TEST-RENOUVELEE", 120, aujourdhui().minusDays(1), aujourdhui().plusDays(730)));
        serveur.repondre(Statut.VALIDE, renouvelee);

        licences.controleQuotidien();

        EtatLicenceDto etat = licences.etat();
        assertThat(etat.licenseId()).isEqualTo("L-TEST-RENOUVELEE");
        assertThat(etat.palierLots()).isEqualTo(120);
        assertThat(nombreD(LicenceImportee.class)).isEqualTo(2);
    }

    @Test
    void unRenouvellementFalsifieEstIgnore() {
        licences.importer(contenuValide());
        serveur.repondre(Statut.VALIDE, signerParUnFaussaire(
                licence("L-FAUX", 5000, aujourdhui().minusDays(1), aujourdhui().plusDays(3650))));

        licences.controleQuotidien();

        assertThat(licences.etat().licenseId()).isEqualTo("L-TEST-1");
        assertThat(licences.etat().palierLots()).isEqualTo(50);
    }

    @Test
    void sansLicenceLeServeurNEstPasInterroge() {
        licences.controleQuotidien();

        assertThat(serveur.appels).isEmpty();
    }
}
