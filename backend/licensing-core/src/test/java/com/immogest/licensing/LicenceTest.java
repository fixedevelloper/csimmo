package com.immogest.licensing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class LicenceTest {

    private static Licence avec(String id, String client, int palier, LocalDate debut, LocalDate fin, String empreinte,
            List<String> fonctionnalites) {
        return new Licence(id, client, palier, debut, fin, empreinte, fonctionnalites);
    }

    private final Licence licence = Fixtures.licence();

    @Test
    void valideLeJourDeExpirationEtExpireeLeLendemain() {
        assertThat(licence.estExpireeAu(LocalDate.of(2026, 12, 31))).isFalse();
        assertThat(licence.estExpireeAu(LocalDate.of(2027, 1, 1))).isTrue();
    }

    @Test
    void activeEntreLesDeuxDatesIncluses() {
        assertThat(licence.estActiveAu(LocalDate.of(2025, 12, 31))).isFalse();
        assertThat(licence.estActiveAu(Fixtures.DEBUT)).isTrue();
        assertThat(licence.estActiveAu(Fixtures.EXPIRATION)).isTrue();
        assertThat(licence.estActiveAu(LocalDate.of(2027, 1, 1))).isFalse();
    }

    @Test
    void joursRestants() {
        assertThat(licence.joursRestantsAu(LocalDate.of(2026, 12, 1))).isEqualTo(30);
        assertThat(licence.joursRestantsAu(Fixtures.EXPIRATION)).isZero();
        assertThat(licence.joursRestantsAu(LocalDate.of(2027, 1, 2))).isEqualTo(-2);
    }

    @Test
    void normaliseLesValeurs() {
        Licence l = avec("  L-1 ", "Café Central", 10, Fixtures.DEBUT, Fixtures.EXPIRATION, "ABCDEF",
                List.of("b", "a", "b", " c "));

        assertThat(l.licenseId()).isEqualTo("L-1");
        assertThat(l.client()).isEqualTo("Café Central").hasSize(12); // NFC : « é » en un seul caractère
        assertThat(l.empreinteServeur()).isEqualTo("abcdef");
        assertThat(l.fonctionnalites()).containsExactly("a", "b", "c");
    }

    @Test
    void listeDeFonctionnalitesImmuable() {
        assertThatThrownBy(() -> licence.fonctionnalites().add("x")).isInstanceOf(UnsupportedOperationException.class);
        List<String> source = new ArrayList<>(List.of("a"));
        Licence l = avec("L", "C", 1, Fixtures.DEBUT, Fixtures.EXPIRATION, "e", source);
        source.add("b");
        assertThat(l.fonctionnalites()).containsExactly("a");
    }

    @Test
    void refuseLesValeursInvalides() {
        assertThatThrownBy(() -> avec(null, "C", 1, Fixtures.DEBUT, Fixtures.EXPIRATION, "e", List.of()))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> avec("L", " ", 1, Fixtures.DEBUT, Fixtures.EXPIRATION, "e", List.of()))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> avec("L", "C", 0, Fixtures.DEBUT, Fixtures.EXPIRATION, "e", List.of()))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> avec("L", "C", 1, null, Fixtures.EXPIRATION, "e", List.of()))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> avec("L", "C", 1, Fixtures.EXPIRATION, Fixtures.DEBUT, "e", List.of()))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> avec("L", "x".repeat(201), 1, Fixtures.DEBUT, Fixtures.EXPIRATION, "e", List.of()))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> avec("L", "C", 1, Fixtures.DEBUT, Fixtures.EXPIRATION, "e", List.of(" ")))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
