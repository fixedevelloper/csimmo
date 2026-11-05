package com.immogest.licensing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class VerificateurLicenceTest {

    private static final LocalDate AUJOURDHUI = LocalDate.of(2026, 6, 15);

    private static ResultatVerification verifier(String fichier) {
        return VerificateurLicence.verifier(fichier, Fixtures.clePublique(), Fixtures.EMPREINTE, AUJOURDHUI);
    }

    // ------------------------------------------------------------------ licence valide

    @Test
    void licenceValide() {
        ResultatVerification r = verifier(Fixtures.licenceSignee().versJson());

        assertThat(r.statut()).isEqualTo(StatutLicence.VALIDE);
        assertThat(r.estValide()).isTrue();
        assertThat(r.licence()).isEqualTo(Fixtures.licence());
        assertThat(r.detail()).isNull();
    }

    @Test
    void fichierDeReferenceSigneAvantCeTestResteValide() {
        // Non-régression : un fichier .lic émis avec une version antérieure doit toujours être accepté.
        ResultatVerification r = verifier(Fixtures.GOLDEN_FICHIER);

        assertThat(r.statut()).isEqualTo(StatutLicence.VALIDE);
        assertThat(r.licence().client()).isEqualTo("Société_Étoile");
        assertThat(r.licence().fonctionnalites()).containsExactly("mobile", "relances");
    }

    @Test
    void signatureDeReferenceReproductible() {
        assertThat(Fixtures.licenceSignee().signature()).isEqualTo(Fixtures.GOLDEN_SIGNATURE);
    }

    @Test
    void lAllerRetourDuFichierConserveLaSignature() {
        LicenceSignee signee = Fixtures.licenceSignee();
        LicenceSignee relue = LicenceSignee.lire(signee.versJson());

        assertThat(relue).isEqualTo(signee);
        assertThat(relue.signatureValide(Fixtures.clePublique())).isTrue();
    }

    @Test
    void laMiseEnFormeDuFichierNEstPasSignee() {
        // JSON compact, ordre des champs différent, espaces : même contenu, donc même signature valide
        String compact = "{\"signature\":\"" + Fixtures.GOLDEN_SIGNATURE + "\",\"licence\":{\"fonctionnalites\":[\"relances\","
                + "\"mobile\"],\"empreinteServeur\":\"" + Fixtures.EMPREINTE + "\",\"dateExpiration\":\"2026-12-31\","
                + "\"dateDebut\":\"2026-01-01\",\"palierLots\":50,\"client\":\"Société_Étoile\",\"licenseId\":\"L-GOLDEN-0001\"},"
                + "\"format\":1}";

        assertThat(verifier(compact).statut()).isEqualTo(StatutLicence.VALIDE);
    }

    @Test
    void texteAccentueEnFormeDecomposeeResteValide() {
        // « É » et « é » écrits en lettre + accent combinant : normalisés en NFC avant signature et vérification
        String decompose = Fixtures.GOLDEN_FICHIER.replace("Société_Étoile", "Société_Étoile");

        assertThat(verifier(decompose).statut()).isEqualTo(StatutLicence.VALIDE);
    }

    // ------------------------------------------------------------------ signature altérée

    @Test
    void signatureAltereeRefusee() {
        String signature = Fixtures.GOLDEN_SIGNATURE;
        char premier = signature.charAt(0) == 'A' ? 'B' : 'A';
        String fichier = Fixtures.GOLDEN_FICHIER.replace(signature, premier + signature.substring(1));

        ResultatVerification r = verifier(fichier);

        assertThat(r.statut()).isEqualTo(StatutLicence.SIGNATURE_INVALIDE);
        assertThat(r.licence()).as("un contenu non authentifié n'est jamais renvoyé").isNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "AAAA", "pas du base64 !!", "%%%%"})
    void signatureTronqueeVideOuIllisibleRefusee(String signature) {
        String fichier = Fixtures.GOLDEN_FICHIER.replace(Fixtures.GOLDEN_SIGNATURE, signature);

        assertThat(verifier(fichier).statut()).isEqualTo(StatutLicence.SIGNATURE_INVALIDE);
    }

    @Test
    void signatureD_uneAutreCleRefusee() {
        var autre = Ed25519.genererPaire();
        String fichier = LicenceSignee.signer(Fixtures.licence(), autre.getPrivate()).versJson();

        assertThat(verifier(fichier).statut()).isEqualTo(StatutLicence.SIGNATURE_INVALIDE);
    }

    // ------------------------------------------------------------------ champ modifié

    static Stream<Arguments> modifications() {
        return Stream.of(
                Arguments.of("palier ×10", "\"palierLots\" : 50", "\"palierLots\" : 500"),
                Arguments.of("expiration repoussée", "\"dateExpiration\" : \"2026-12-31\"", "\"dateExpiration\" : \"2099-12-31\""),
                Arguments.of("début avancé", "\"dateDebut\" : \"2026-01-01\"", "\"dateDebut\" : \"2020-01-01\""),
                Arguments.of("client changé", "Société_Étoile", "Société_Autre"),
                Arguments.of("identifiant changé", "L-GOLDEN-0001", "L-GOLDEN-0002"),
                Arguments.of("empreinte changée", "\"empreinteServeur\" : \"e4b0", "\"empreinteServeur\" : \"e4b1"),
                Arguments.of("fonctionnalité ajoutée", "[ \"mobile\", \"relances\" ]", "[ \"mobile\", \"paiement\", \"relances\" ]"),
                Arguments.of("fonctionnalité retirée", "[ \"mobile\", \"relances\" ]", "[ \"mobile\" ]"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("modifications")
    void unChampModifieInvalideLaSignature(String description, String ancien, String nouveau) {
        assertThat(Fixtures.GOLDEN_FICHIER).contains(ancien);
        String fichier = Fixtures.GOLDEN_FICHIER.replace(ancien, nouveau);

        ResultatVerification r = VerificateurLicence.verifier(fichier, Fixtures.clePublique(),
                // l'empreinte locale suit la modification pour ne tester QUE la signature
                fichier.contains("e4b1") ? fichier.substring(fichier.indexOf("e4b1"), fichier.indexOf("e4b1") + 64) : Fixtures.EMPREINTE,
                AUJOURDHUI);

        assertThat(r.statut()).isEqualTo(StatutLicence.SIGNATURE_INVALIDE);
        assertThat(r.licence()).isNull();
    }

    @Test
    void ajouterUnChampInconnuNeChangeNiLaSignatureNiLeContenuLu() {
        // Les champs non signés sont ignorés : ils ne peuvent rien accorder
        String fichier = Fixtures.GOLDEN_FICHIER.replace("\"palierLots\" : 50,", "\"palierLots\" : 50, \"gratuit\" : true,");

        ResultatVerification r = verifier(fichier);

        assertThat(r.statut()).isEqualTo(StatutLicence.VALIDE);
        assertThat(r.licence()).isEqualTo(Fixtures.licence());
    }

    // ------------------------------------------------------------------ expirée / pas commencée

    @Test
    void expireeMaisAuthentique() {
        ResultatVerification r = VerificateurLicence.verifier(Fixtures.GOLDEN_FICHIER, Fixtures.clePublique(),
                Fixtures.EMPREINTE, LocalDate.of(2027, 1, 1));

        assertThat(r.statut()).isEqualTo(StatutLicence.EXPIREE);
        assertThat(r.estValide()).isFalse();
        assertThat(r.licence()).as("le contenu authentifié sert à afficher la date d'expiration").isNotNull();
    }

    @Test
    void valideLePremierEtLeDernierJour() {
        for (LocalDate jour : new LocalDate[] {Fixtures.DEBUT, Fixtures.EXPIRATION}) {
            assertThat(VerificateurLicence.verifier(Fixtures.GOLDEN_FICHIER, Fixtures.clePublique(), Fixtures.EMPREINTE, jour)
                    .statut()).as(jour.toString()).isEqualTo(StatutLicence.VALIDE);
        }
    }

    @Test
    void pasEncoreValide() {
        ResultatVerification r = VerificateurLicence.verifier(Fixtures.GOLDEN_FICHIER, Fixtures.clePublique(),
                Fixtures.EMPREINTE, LocalDate.of(2025, 12, 31));

        assertThat(r.statut()).isEqualTo(StatutLicence.PAS_ENCORE_VALIDE);
        assertThat(r.licence()).isNotNull();
    }

    @Test
    void uneLicenceFalsifieeEtExpireeEstSignaleeFalsifiee() {
        String fichier = Fixtures.GOLDEN_FICHIER.replace("\"palierLots\" : 50", "\"palierLots\" : 5000");

        ResultatVerification r = VerificateurLicence.verifier(fichier, Fixtures.clePublique(), Fixtures.EMPREINTE,
                LocalDate.of(2030, 1, 1));

        assertThat(r.statut()).isEqualTo(StatutLicence.SIGNATURE_INVALIDE);
    }

    // ------------------------------------------------------------------ mauvaise empreinte

    @Test
    void mauvaiseEmpreinteRefusee() {
        ResultatVerification r = VerificateurLicence.verifier(Fixtures.GOLDEN_FICHIER, Fixtures.clePublique(),
                EmpreinteServeur.calculer("autre-machine", "install-test"), AUJOURDHUI);

        assertThat(r.statut()).isEqualTo(StatutLicence.MAUVAISE_EMPREINTE);
        assertThat(r.licence()).isNotNull();
    }

    @Test
    void empreinteAbsenteRefusee() {
        assertThat(VerificateurLicence.verifier(Fixtures.GOLDEN_FICHIER, Fixtures.clePublique(), null, AUJOURDHUI).statut())
                .isEqualTo(StatutLicence.MAUVAISE_EMPREINTE);
        assertThat(VerificateurLicence.verifier(Fixtures.GOLDEN_FICHIER, Fixtures.clePublique(), "", AUJOURDHUI).statut())
                .isEqualTo(StatutLicence.MAUVAISE_EMPREINTE);
    }

    @Test
    void empreinteComparéeSansTenirCompteDeLaCasse() {
        ResultatVerification r = VerificateurLicence.verifier(Fixtures.GOLDEN_FICHIER, Fixtures.clePublique(),
                "  " + Fixtures.EMPREINTE.toUpperCase() + " ", AUJOURDHUI);

        assertThat(r.statut()).isEqualTo(StatutLicence.VALIDE);
    }

    @Test
    void empreinteCalculeeParLeServeurCorrespondALaLicence() {
        assertThat(EmpreinteServeur.calculer("machine-test", "install-test")).isEqualTo(Fixtures.EMPREINTE);
    }

    // ------------------------------------------------------------------ fichiers mal formés : jamais d'exception

    static Stream<String> fichiersMalFormes() {
        String signature = Fixtures.GOLDEN_SIGNATURE;
        return Stream.of(
                "", "   ", "null", "[]", "42", "\"texte\"", "{", "{}", "pas du json", "{\"format\":1}",
                "\u0000\u0001\u0002",
                // champs manquants
                Fixtures.GOLDEN_FICHIER.replace("\"client\" : \"Société_Étoile\",", ""),
                Fixtures.GOLDEN_FICHIER.replace("\"signature\" : \"" + signature + "\"", "\"x\" : 1"),
                Fixtures.GOLDEN_FICHIER.replace("\"format\" : 1,", ""),
                // mauvais types
                Fixtures.GOLDEN_FICHIER.replace("\"palierLots\" : 50", "\"palierLots\" : \"50\""),
                Fixtures.GOLDEN_FICHIER.replace("\"palierLots\" : 50", "\"palierLots\" : 50.0"),
                Fixtures.GOLDEN_FICHIER.replace("\"palierLots\" : 50", "\"palierLots\" : 99999999999"),
                Fixtures.GOLDEN_FICHIER.replace("\"palierLots\" : 50", "\"palierLots\" : 0"),
                Fixtures.GOLDEN_FICHIER.replace("\"palierLots\" : 50", "\"palierLots\" : -5"),
                Fixtures.GOLDEN_FICHIER.replace("[ \"mobile\", \"relances\" ]", "\"mobile\""),
                Fixtures.GOLDEN_FICHIER.replace("[ \"mobile\", \"relances\" ]", "[ 1, 2 ]"),
                Fixtures.GOLDEN_FICHIER.replace("\"signature\" : \"" + signature + "\"", "\"signature\" : 12"),
                Fixtures.GOLDEN_FICHIER.replace("\"licence\" : {", "\"licence\" : [ ] , \"x\" : {"),
                // dates
                Fixtures.GOLDEN_FICHIER.replace("2026-12-31", "31/12/2026"),
                Fixtures.GOLDEN_FICHIER.replace("2026-12-31", "2025-12-31"), // expiration avant le début
                // format
                Fixtures.GOLDEN_FICHIER.replace("\"format\" : 1", "\"format\" : 2"),
                Fixtures.GOLDEN_FICHIER.replace("\"format\" : 1", "\"format\" : \"1\""),
                // clés dupliquées (jamais « la dernière gagne »)
                Fixtures.GOLDEN_FICHIER.replace("\"palierLots\" : 50,", "\"palierLots\" : 50, \"palierLots\" : 5000,"),
                // contenu après l'objet
                Fixtures.GOLDEN_FICHIER + " {\"autre\":1}",
                // trop volumineux
                Fixtures.GOLDEN_FICHIER + " ".repeat(LicenceSignee.TAILLE_MAX));
    }

    @ParameterizedTest
    @MethodSource("fichiersMalFormes")
    void fichierMalFormeDonneFormatInvalideSansException(String fichier) {
        ResultatVerification r = verifier(fichier);

        assertThat(r.statut()).isEqualTo(StatutLicence.FORMAT_INVALIDE);
        assertThat(r.licence()).isNull();
        assertThat(r.detail()).isNotBlank();
    }

    @Test
    void fichierNullDonneFormatInvalide() {
        assertThat(verifier(null).statut()).isEqualTo(StatutLicence.FORMAT_INVALIDE);
    }

    @Test
    void lireSansVerifierLeveUneExceptionExplicite() {
        assertThatThrownBy(() -> LicenceSignee.lire("{}")).isInstanceOf(LicenceInvalideException.class);
    }

    @Test
    void leDetailNeContientNiLaSignatureNiLeContenu() {
        String fichier = Fixtures.GOLDEN_FICHIER.replace("\"palierLots\" : 50", "\"palierLots\" : \"secret-50\"");

        ResultatVerification r = verifier(fichier);

        assertThat(r.detail()).doesNotContain(Fixtures.GOLDEN_SIGNATURE).doesNotContain("secret-50");
    }
}
