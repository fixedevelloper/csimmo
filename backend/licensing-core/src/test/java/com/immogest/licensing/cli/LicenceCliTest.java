package com.immogest.licensing.cli;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

import com.immogest.licensing.IdentifiantMachine;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class LicenceCliTest {

    private static final String EMPREINTE = "e4b0db06d22596999a6cf16f41031182db7f4c753c94ea97a00ca1e79d95c464";

    @TempDir
    Path dossier;

    private ByteArrayOutputStream sortie;
    private ByteArrayOutputStream erreur;

    @BeforeEach
    void init() {
        sortie = new ByteArrayOutputStream();
        erreur = new ByteArrayOutputStream();
    }

    private int lancer(String... args) {
        return LicenceCli.executer(args, new PrintStream(sortie, true, StandardCharsets.UTF_8),
                new PrintStream(erreur, true, StandardCharsets.UTF_8));
    }

    private String sortie() {
        return sortie.toString(StandardCharsets.UTF_8);
    }

    private String erreur() {
        return erreur.toString(StandardCharsets.UTF_8);
    }

    private Path cles() {
        return dossier.resolve("cles");
    }

    private int signer(String expiration, Path lic) {
        return lancer("signer", "--cle-privee", cles().resolve("licence-private.key").toString(),
                "--client", "Client Test", "--palier", "50", "--debut", "2026-01-01", "--expiration", expiration,
                "--empreinte", EMPREINTE, "--fonctionnalites", "relances, mobile", "--sortie", lic.toString());
    }

    private int verifier(Path lic, String... enPlus) {
        List<String> args = new ArrayList<>(List.of("verifier", "--cle-publique",
                cles().resolve("licence-public.key").toString(), "--fichier", lic.toString(), "--empreinte", EMPREINTE));
        args.addAll(List.of(enPlus));
        return lancer(args.toArray(String[]::new));
    }

    @Test
    void genererSignerEtVerifier() {
        assertThat(lancer("generer-cles", "--sortie", cles().toString())).isEqualTo(LicenceCli.OK);
        assertThat(cles().resolve("licence-private.key")).exists();
        assertThat(cles().resolve("licence-public.key")).exists();

        Path lic = dossier.resolve("test.lic");
        assertThat(signer("2026-12-31", lic)).isEqualTo(LicenceCli.OK);
        assertThat(sortie()).contains("Client Test").contains("50 lots");

        sortie.reset();
        assertThat(verifier(lic, "--date", "2026-06-15")).isEqualTo(LicenceCli.OK);
        assertThat(sortie()).contains("Statut : VALIDE").contains("199 jour(s) restant(s)");
    }

    @Test
    void laCleHorsEtatNEstJamaisAfficheeNiEcrasee() throws IOException {
        lancer("generer-cles", "--sortie", cles().toString());
        String privee = Files.readString(cles().resolve("licence-private.key")).strip();
        assertThat(sortie()).doesNotContain(privee);

        sortie.reset();
        assertThat(lancer("generer-cles", "--sortie", cles().toString())).isEqualTo(LicenceCli.ECHEC);
        assertThat(erreur()).contains("existent déjà");
        assertThat(Files.readString(cles().resolve("licence-private.key")).strip()).isEqualTo(privee);
    }

    @Test
    void laClePriveeEstLisibleParSonProprietaireSeulement() throws IOException {
        lancer("generer-cles", "--sortie", cles().toString());
        var fs = cles().getFileSystem();
        assumeThat(fs.supportedFileAttributeViews()).contains("posix");

        assertThat(PosixFilePermissions.toString(Files.getPosixFilePermissions(cles().resolve("licence-private.key"))))
                .isEqualTo("rw-------");
    }

    @Test
    void verificationEchoueSurLicenceExpiree() {
        lancer("generer-cles", "--sortie", cles().toString());
        Path lic = dossier.resolve("test.lic");
        signer("2026-12-31", lic);
        sortie.reset();

        assertThat(verifier(lic, "--date", "2027-01-01")).isEqualTo(LicenceCli.ECHEC);
        assertThat(sortie()).contains("Statut : EXPIREE");
    }

    @Test
    void verificationEchoueSurFichierModifie() throws IOException {
        lancer("generer-cles", "--sortie", cles().toString());
        Path lic = dossier.resolve("test.lic");
        signer("2026-12-31", lic);
        Files.writeString(lic, Files.readString(lic).replace("\"palierLots\" : 50", "\"palierLots\" : 5000"));
        sortie.reset();

        assertThat(verifier(lic, "--date", "2026-06-15")).isEqualTo(LicenceCli.ECHEC);
        assertThat(sortie()).contains("Statut : SIGNATURE_INVALIDE").doesNotContain("5000");
    }

    @Test
    void verificationEchoueSurAutreServeur() {
        lancer("generer-cles", "--sortie", cles().toString());
        Path lic = dossier.resolve("test.lic");
        signer("2026-12-31", lic);
        sortie.reset();

        int code = lancer("verifier", "--cle-publique", cles().resolve("licence-public.key").toString(),
                "--fichier", lic.toString(), "--empreinte", "0".repeat(64), "--date", "2026-06-15");

        assertThat(code).isEqualTo(LicenceCli.ECHEC);
        assertThat(sortie()).contains("MAUVAISE_EMPREINTE");
    }

    @Test
    void neSigneJamaisParDessusUneLicenceExistante() {
        lancer("generer-cles", "--sortie", cles().toString());
        Path lic = dossier.resolve("test.lic");
        assertThat(signer("2026-12-31", lic)).isEqualTo(LicenceCli.OK);

        assertThat(signer("2027-12-31", lic)).isEqualTo(LicenceCli.ECHEC);
        assertThat(erreur()).contains("existe déjà");
    }

    @Test
    void donneesInvalidesRefusees() {
        lancer("generer-cles", "--sortie", cles().toString());
        Path lic = dossier.resolve("test.lic");

        assertThat(signer("2025-12-31", lic)).as("expiration avant le début").isEqualTo(LicenceCli.ECHEC);
        assertThat(signer("pas-une-date", lic)).isEqualTo(LicenceCli.USAGE);
        assertThat(lic).doesNotExist();
    }

    @Test
    void usageIncorrect() {
        assertThat(lancer()).isEqualTo(LicenceCli.USAGE);
        assertThat(lancer("inconnue")).isEqualTo(LicenceCli.USAGE);
        assertThat(lancer("generer-cles")).isEqualTo(LicenceCli.USAGE);
        assertThat(lancer("generer-cles", "--sortie")).isEqualTo(LicenceCli.USAGE);
        assertThat(lancer("generer-cles", "--n-importe-quoi", "x")).isEqualTo(LicenceCli.USAGE);
        assertThat(erreur()).contains("Usage");
    }

    @Test
    void fichierIntrouvableEstUnEchecPropre() {
        lancer("generer-cles", "--sortie", cles().toString());

        assertThat(verifier(dossier.resolve("absent.lic"))).isEqualTo(LicenceCli.ECHEC);
        assertThat(erreur()).contains("Échec");
    }

    @Test
    void empreinteDuServeurLocal() {
        assumeThat(IdentifiantMachine.detecter()).isPresent();

        assertThat(lancer("empreinte", "--installation-id", "install-test")).isEqualTo(LicenceCli.OK);
        assertThat(sortie()).contains("install-test").containsPattern("[0-9a-f]{64}");
    }
}
