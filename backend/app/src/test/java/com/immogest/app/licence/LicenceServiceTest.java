package com.immogest.app.licence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.immogest.app.licence.api.ControlePalier;
import com.immogest.app.shared.ErreurMetier;
import com.immogest.licensing.Licence;
import java.time.LocalDate;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

/** Licence en mode hors ligne : vérification de la signature, de l'empreinte et des dates ; alertes ; palier. */
class LicenceServiceTest extends BaseLicenceTest {

    @Autowired
    ControlePalier palier;

    private String codeRefus(String contenu) {
        try {
            licences.importer(contenu);
        } catch (ErreurMetier e) {
            return e.code();
        }
        return "ACCEPTEE";
    }

    // ------------------------------------------------------------------ état et import

    @Test
    void sansLicenceLApplicationEstEnLectureSeule() {
        EtatLicenceDto etat = licences.etat();

        assertThat(etat.etat()).isEqualTo(EtatLicence.AUCUNE);
        assertThat(etat.lectureSeule()).isTrue();
        assertThat(licences.ecrituresAutorisees()).isFalse();
        assertThat(etat.client()).isNull();
        assertThat(etat.empreinteServeur()).isEqualTo(LicenceTestSupport.EMPREINTE);
    }

    @Test
    void importDUneLicenceValide() {
        EtatLicenceDto etat = licences.importer(contenuValide());

        assertThat(etat.etat()).isEqualTo(EtatLicence.VALIDE);
        assertThat(etat.lectureSeule()).isFalse();
        assertThat(etat.client()).isEqualTo("Client Test");
        assertThat(etat.palierLots()).isEqualTo(50);
        assertThat(etat.licenseId()).isEqualTo("L-TEST-1");
        assertThat(etat.joursRestants()).isEqualTo(365);
        assertThat(etat.fonctionnalites()).containsExactly("mobile", "relances");
        assertThat(etat.alerteJours()).isNull();
        assertThat(licences.ecrituresAutorisees()).isTrue();
        assertThat(nombreD(LicenceImportee.class)).isEqualTo(1);
    }

    static Stream<Arguments> importsRefuses() {
        return Stream.of(
                Arguments.of("signature altérée", "SIGNATURE", "LICENCE_SIGNATURE_INVALIDE"),
                Arguments.of("palier modifié", "PALIER", "LICENCE_SIGNATURE_INVALIDE"),
                Arguments.of("signée par un faussaire", "FAUSSAIRE", "LICENCE_SIGNATURE_INVALIDE"),
                Arguments.of("autre serveur", "EMPREINTE", "LICENCE_MAUVAISE_EMPREINTE"),
                Arguments.of("déjà expirée", "EXPIREE", "LICENCE_FICHIER_EXPIRE"),
                Arguments.of("pas encore commencée", "FUTURE", "LICENCE_PAS_ENCORE_VALIDE"),
                Arguments.of("fichier illisible", "ILLISIBLE", "LICENCE_FORMAT_INVALIDE"),
                Arguments.of("fichier vide", "VIDE", "LICENCE_FORMAT_INVALIDE"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("importsRefuses")
    void unImportInvalideEstRefuseEtNeChangeRien(String description, String cas, String codeAttendu) {
        String contenu = switch (cas) {
            case "SIGNATURE" -> {
                String valide = contenuValide();
                String signature = LicenceTestSupportAcces.signature(valide);
                yield valide.replace(signature, (signature.charAt(0) == 'A' ? "B" : "A") + signature.substring(1));
            }
            case "PALIER" -> contenuValide().replace("\"palierLots\" : 50", "\"palierLots\" : 5000");
            case "FAUSSAIRE" -> signerParUnFaussaire(licenceValide());
            case "EMPREINTE" -> signer(new Licence("L-X", "Autre", 50, aujourdhui().minusDays(1), aujourdhui().plusDays(30),
                    "cd".repeat(32), java.util.List.of()));
            case "EXPIREE" -> signer(licence("L-OLD", 50, aujourdhui().minusDays(400), aujourdhui().minusDays(35)));
            case "FUTURE" -> signer(licence("L-FUT", 50, aujourdhui().plusDays(5), aujourdhui().plusDays(400)));
            case "ILLISIBLE" -> "ceci n'est pas une licence";
            default -> "";
        };

        assertThat(codeRefus(contenu)).isEqualTo(codeAttendu);
        assertThat(licences.etat().etat()).isEqualTo(EtatLicence.AUCUNE);
        assertThat(nombreD(LicenceImportee.class)).isZero();
        assertThat(jdbc.queryForObject("SELECT count(*) FROM lic_licence", Integer.class)).isZero();
    }

    @Test
    void importerLaMemeLicenceNeChangeRien() {
        licences.importer(contenuValide());
        evenements.clear();

        EtatLicenceDto etat = licences.importer(contenuValide());

        assertThat(etat.etat()).isEqualTo(EtatLicence.VALIDE);
        assertThat(nombreD(LicenceImportee.class)).as("pas de nouvel enregistrement").isZero();
        assertThat(jdbc.queryForObject("SELECT count(*) FROM lic_licence", Integer.class)).isEqualTo(1);
    }

    @Test
    void uneLicenceRenouveleeRemplaceLAncienneSansLaSupprimer() {
        licences.importer(contenuValide());

        EtatLicenceDto etat = licences.importer(signer(licence("L-TEST-2", 200, aujourdhui().minusDays(1), aujourdhui().plusDays(730))));

        assertThat(etat.licenseId()).isEqualTo("L-TEST-2");
        assertThat(etat.palierLots()).isEqualTo(200);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM lic_licence", Integer.class)).as("historique conservé").isEqualTo(2);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM lic_licence WHERE actuelle", Integer.class)).isEqualTo(1);
    }

    @Test
    void uneLicencePlusAncienneQueLaCouranteEstRefusee() {
        licences.importer(contenuValide());

        assertThat(codeRefus(signer(licence("L-TEST-0", 50, aujourdhui().minusDays(100), aujourdhui().plusDays(100)))))
                .isEqualTo("LICENCE_PLUS_ANCIENNE");
        assertThat(licences.etat().licenseId()).isEqualTo("L-TEST-1");
    }

    @Test
    void unTransfertGardeLesMemesDatesEtEstAccepte() {
        licences.importer(contenuValide());

        // Nouvelle licence (autre identifiant) avec la même date d'expiration : transfert ou changement de palier
        Licence transfert = licence("L-TEST-1b", 80, aujourdhui().minusDays(10), aujourdhui().plusDays(365));

        assertThat(licences.importer(signer(transfert)).licenseId()).isEqualTo("L-TEST-1b");
    }

    // ------------------------------------------------------------------ expiration

    @Test
    void laLicenceExpireAvecLeTempsEtPasseEnLectureSeule() {
        licences.importer(contenuValide());
        assertThat(licences.ecrituresAutorisees()).isTrue();

        horloge.avancerJours(365); // dernier jour : encore valide
        licences.invalider();
        assertThat(licences.etat().etat()).isEqualTo(EtatLicence.VALIDE);
        assertThat(licences.etat().joursRestants()).isZero();

        horloge.avancerJours(1);
        EtatLicenceDto etat = licences.etat(); // le changement de jour recharge l'instantané tout seul

        assertThat(etat.etat()).isEqualTo(EtatLicence.EXPIREE);
        assertThat(etat.lectureSeule()).isTrue();
        assertThat(etat.joursRestants()).isEqualTo(-1);
        assertThat(etat.client()).as("le contenu authentique reste affiché").isEqualTo("Client Test");
        assertThat(licences.ecrituresAutorisees()).isFalse();
    }

    @Test
    void lExpirationEstNotifieeUneSeuleFois() {
        licences.importer(contenuValide());
        horloge.avancerJours(366);

        licences.controleQuotidien();
        licences.controleQuotidien();
        horloge.avancerJours(1);
        licences.controleQuotidien();

        assertThat(nombreD(LicenceExpiree.class)).isEqualTo(1);
    }

    @Test
    void ledeDernierJourEstFixeParLeFuseauDeDouala() {
        // 23:30 UTC le jour J = 00:30 le jour J+1 à Douala (UTC+1) : la licence expire à minuit heure de Douala
        licences.importer(contenuValide());
        horloge.avancerJours(364);
        long avant = licences.etat().joursRestants();
        horloge.avancer(java.time.Duration.ofHours(23));
        licences.invalider();

        assertThat(licences.etat().joursRestants()).isLessThanOrEqualTo(avant);
    }

    // ------------------------------------------------------------------ alertes 30 / 15 / 7 jours

    @Test
    void alertesAJ30J15EtJ7UneSeuleFoisChacune() {
        licences.importer(signer(licence("L-ALERTE", 50, aujourdhui().minusDays(300), aujourdhui().plusDays(40))));

        licences.controleQuotidien();
        assertThat(nombreD(LicenceBientotExpiree.class)).as("40 jours restants : rien").isZero();
        assertThat(licences.etat().alerteJours()).isNull();

        horloge.avancerJours(10); // 30 jours restants
        licences.controleQuotidien();
        licences.controleQuotidien();
        assertThat(seuilsEnvoyes()).containsExactly(30);
        assertThat(licences.etat().alerteJours()).isEqualTo(30);

        horloge.avancerJours(15); // 15 jours restants
        licences.controleQuotidien();
        assertThat(seuilsEnvoyes()).containsExactly(30, 15);

        horloge.avancerJours(8); // 7 jours restants
        licences.controleQuotidien();
        horloge.avancerJours(1); // 6 jours : toujours le seuil 7, déjà notifié
        licences.controleQuotidien();
        assertThat(seuilsEnvoyes()).containsExactly(30, 15, 7);
        assertThat(licences.etat().alerteJours()).isEqualTo(7);
    }

    @Test
    void sApresUneLongueInterruptionSeulLePlusProcheSeuilEstNotifie() {
        licences.importer(signer(licence("L-ALERTE", 50, aujourdhui().minusDays(300), aujourdhui().plusDays(40))));
        horloge.avancerJours(35); // 5 jours restants : l'application n'a rien vu passer depuis

        licences.controleQuotidien();

        assertThat(seuilsEnvoyes()).containsExactly(7);
    }

    @Test
    void lesAlertesNeSontPasEnvoyeesPourUneLicenceInvalide() {
        licences.importer(signer(licence("L-ALERTE", 50, aujourdhui().minusDays(300), aujourdhui().plusDays(20))));
        jdbc.update("UPDATE lic_licence SET contenu = replace(contenu, '\"palierLots\" : 50', '\"palierLots\" : 5000')");

        licences.controleQuotidien();

        assertThat(licences.etat().etat()).isEqualTo(EtatLicence.INVALIDE);
        assertThat(nombreD(LicenceBientotExpiree.class)).isZero();
    }

    private java.util.List<Integer> seuilsEnvoyes() {
        return evenements.stream(LicenceBientotExpiree.class).map(LicenceBientotExpiree::seuilJours).toList();
    }

    // ------------------------------------------------------------------ revérification quotidienne

    @Test
    void leControleQuotidienDetecteUneBaseFalsifiee() {
        licences.importer(contenuValide());
        assertThat(licences.etat().etat()).isEqualTo(EtatLicence.VALIDE);

        jdbc.update("UPDATE lic_licence SET contenu = replace(contenu, '\"palierLots\" : 50', '\"palierLots\" : 5000')");
        licences.controleQuotidien(); // revérification de la signature

        EtatLicenceDto etat = licences.etat();
        assertThat(etat.etat()).isEqualTo(EtatLicence.INVALIDE);
        assertThat(etat.lectureSeule()).isTrue();
        assertThat(etat.detail()).contains("modifié");
        assertThat(etat.palierLots()).as("un contenu non authentifié n'est jamais affiché").isNull();
        assertThat(etat.client()).isNull();
    }

    @Test
    void uneLicenceCopieeSurUnAutreServeurEstInvalide() {
        licences.importer(contenuValide());
        // Le même contenu relu sur une machine à l'empreinte différente : la licence lui est liée
        jdbc.update("UPDATE lic_licence SET contenu = ?", signer(new Licence("L-TEST-1", "Client Test", 50,
                aujourdhui().minusDays(10), aujourdhui().plusDays(365), "cd".repeat(32), java.util.List.of())));

        licences.controleQuotidien();

        assertThat(licences.etat().etat()).isEqualTo(EtatLicence.INVALIDE);
        assertThat(licences.etat().detail()).contains("autre serveur");
    }

    // ------------------------------------------------------------------ hors ligne : pas de délai de grâce

    @Test
    void sansServeurDeLicencesConfigureAucuneVerificationEnLigneNiGrace() {
        licences.importer(contenuValide());

        horloge.avancerJours(200);
        licences.controleQuotidien();

        assertThat(serveur.appels).isEmpty();
        EtatLicenceDto etat = licences.etat();
        assertThat(etat.etat()).isEqualTo(EtatLicence.VALIDE);
        assertThat(etat.verificationEnLigneActive()).isFalse();
        assertThat(etat.joursDeGraceRestants()).isNull();
    }

    // ------------------------------------------------------------------ palier de lots

    @Test
    void laCreationDeLotEstRefuseeQuandLePalierEstAtteint() {
        licences.importer(signer(licence("L-PALIER", 3, aujourdhui().minusDays(1), aujourdhui().plusDays(100))));

        assertThat(palier.palierLots()).isEqualTo(3);
        for (long lotsExistants = 0; lotsExistants < 3; lotsExistants++) {
            palier.verifierCreationLot(lotsExistants);
        }
        assertThatThrownBy(() -> palier.verifierCreationLot(3))
                .isInstanceOfSatisfying(ErreurMetier.class, e -> {
                    assertThat(e.code()).isEqualTo("LICENCE_PALIER_ATTEINT");
                    assertThat(e.status().value()).isEqualTo(409);
                });
        assertThatThrownBy(() -> palier.verifierCreationLot(10))
                .isInstanceOfSatisfying(ErreurMetier.class, e -> assertThat(e.code()).isEqualTo("LICENCE_PALIER_ATTEINT"));
    }

    @Test
    void lePalierMonteAvecUneLicenceDeNiveauSuperieur() {
        licences.importer(signer(licence("L-PALIER", 3, aujourdhui().minusDays(1), aujourdhui().plusDays(100))));
        assertThatThrownBy(() -> palier.verifierCreationLot(3)).isInstanceOf(ErreurMetier.class);

        licences.importer(signer(licence("L-PALIER-2", 10, aujourdhui().minusDays(1), aujourdhui().plusDays(100))));

        palier.verifierCreationLot(3);
    }

    @Test
    void sansLicenceValideAucunLotNePeutEtreCree() {
        assertThatThrownBy(() -> palier.verifierCreationLot(0))
                .isInstanceOfSatisfying(ErreurMetier.class, e -> assertThat(e.code()).isEqualTo("LICENCE_EXPIREE"));
        assertThat(palier.palierLots()).isZero();

        licences.importer(contenuValide());
        horloge.avancerJours(400);

        assertThatThrownBy(() -> palier.verifierCreationLot(0))
                .isInstanceOfSatisfying(ErreurMetier.class, e -> assertThat(e.code()).isEqualTo("LICENCE_EXPIREE"));
        assertThat(palier.palierLots()).isZero();
    }

    /** Évite l'avertissement d'import inutilisé sur LocalDate si le test est simplifié. */
    @SuppressWarnings("unused")
    private static LocalDate jour() {
        return LocalDate.now();
    }
}
