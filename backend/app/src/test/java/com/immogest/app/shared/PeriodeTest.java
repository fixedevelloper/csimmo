package com.immogest.app.shared;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.YearMonth;
import org.junit.jupiter.api.Test;

class PeriodeTest {

    @Test
    void moisCouvreToutLeMoisMemeCourt() {
        Periode fevrier = Periode.mois(YearMonth.of(2028, 2));
        assertThat(fevrier.fin()).isEqualTo(LocalDate.of(2028, 2, 29));
        assertThat(fevrier.contient(LocalDate.of(2028, 2, 1))).isTrue();
        assertThat(fevrier.contient(LocalDate.of(2028, 3, 1))).isFalse();
    }

    @Test
    void chevauchement() {
        Periode a = new Periode(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 6, 30));
        Periode b = new Periode(LocalDate.of(2026, 6, 30), LocalDate.of(2026, 12, 31));
        Periode c = new Periode(LocalDate.of(2026, 7, 1), LocalDate.of(2026, 12, 31));
        assertThat(a.chevauche(b)).isTrue();
        assertThat(a.chevauche(c)).isFalse();
    }

    @Test
    void finAvantDebutRefusee() {
        assertThatThrownBy(() -> new Periode(LocalDate.of(2026, 2, 1), LocalDate.of(2026, 1, 1)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
