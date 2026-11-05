package com.immogest.licensing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class EmpreinteServeurTest {

    @Test
    void empreinteDeterministeDe64CaracteresHexadecimaux() {
        String e = EmpreinteServeur.calculer("machine-1", "install-1");

        assertThat(e).matches("[0-9a-f]{64}").isEqualTo(EmpreinteServeur.calculer("machine-1", "install-1"));
    }

    @Test
    void changeSiLaMachineOuLInstallationChange() {
        String base = EmpreinteServeur.calculer("machine-1", "install-1");

        assertThat(EmpreinteServeur.calculer("machine-2", "install-1")).isNotEqualTo(base);
        assertThat(EmpreinteServeur.calculer("machine-1", "install-2")).isNotEqualTo(base);
    }

    @Test
    void lesChampsNePeuventPasEtreConfondus() {
        // « a » + « bc » ne doit pas donner la même empreinte que « ab » + « c »
        assertThat(EmpreinteServeur.calculer("a", "bc")).isNotEqualTo(EmpreinteServeur.calculer("ab", "c"));
    }

    @Test
    void ignoreLesEspacesAutourDesIdentifiants() {
        assertThat(EmpreinteServeur.calculer(" m \n", " i ")).isEqualTo(EmpreinteServeur.calculer("m", "i"));
    }

    @Test
    void refuseLesIdentifiantsVides() {
        assertThatThrownBy(() -> EmpreinteServeur.calculer("", "i")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> EmpreinteServeur.calculer("m", " ")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> EmpreinteServeur.calculer(null, "i")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void identifiantsDInstallationUniques() {
        assertThat(EmpreinteServeur.nouvelIdentifiantInstallation())
                .isNotEqualTo(EmpreinteServeur.nouvelIdentifiantInstallation())
                .matches("[0-9a-f-]{36}");
    }

    // ------------------------------------------------------------------ identifiant machine

    @Test
    void variableDEnvironnementPrioritaire(@TempDir Path dossier) throws IOException {
        Path fichier = dossier.resolve("machine-id");
        Files.writeString(fichier, "depuis-le-fichier\n");

        assertThat(IdentifiantMachine.detecter(Map.of(IdentifiantMachine.VARIABLE, " hote-docker ")::get, List.of(fichier)))
                .contains("hote-docker");
    }

    @Test
    void litLePremierFichierUtilisable(@TempDir Path dossier) throws IOException {
        Path absent = dossier.resolve("absent");
        Path vide = dossier.resolve("vide");
        Files.writeString(vide, "  \n");
        Path bon = dossier.resolve("bon");
        Files.writeString(bon, "abc123\n");

        assertThat(IdentifiantMachine.detecter(nom -> null, List.of(absent, vide, bon))).contains("abc123");
    }

    @Test
    void aucuneSourceDonneVide() {
        assertThat(IdentifiantMachine.detecter(nom -> null, List.of(Path.of("/nexiste/pas")))).isEqualTo(Optional.empty());
        assertThat(IdentifiantMachine.detecter(nom -> "  ", List.of())).isEmpty();
    }

    @Test
    void extraitLeGuidDuRegistreWindows() {
        String sortie = """

                HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Cryptography
                    MachineGuid    REG_SZ    4c4c4544-0042-4410-8051-b8c04f4b5a31

                """;

        assertThat(IdentifiantMachine.extraireMachineGuid(sortie)).contains("4c4c4544-0042-4410-8051-b8c04f4b5a31");
        assertThat(IdentifiantMachine.extraireMachineGuid("ERREUR : introuvable")).isEmpty();
    }
}
