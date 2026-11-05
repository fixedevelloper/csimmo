package com.immogest.app.shared;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class MontantTest {

    @Test
    void additionneEtSoustrait() {
        assertThat(Montant.of(150_000).plus(Montant.of(25_000))).isEqualTo(Montant.of(175_000));
        assertThat(Montant.of(150_000).moins(Montant.of(25_000))).isEqualTo(Montant.of(125_000));
    }

    @Test
    void pourcentageEnPointsDeBase() {
        assertThat(Montant.of(200_000).pourcentage(1000)).isEqualTo(Montant.of(20_000));
        assertThat(Montant.of(100_000).pourcentage(750)).isEqualTo(Montant.of(7_500));
    }

    @Test
    void pourcentageArrondiALUniteLaPlusProche() {
        // 12345 * 5 % = 617,25 -> 617 ; 12350 * 5 % = 617,5 -> 618
        assertThat(Montant.of(12_345).pourcentage(500)).isEqualTo(Montant.of(617));
        assertThat(Montant.of(12_350).pourcentage(500)).isEqualTo(Montant.of(618));
    }

    @Test
    void debordementDetecte() {
        assertThatThrownBy(() -> Montant.of(Long.MAX_VALUE).plus(Montant.of(1)))
                .isInstanceOf(ArithmeticException.class);
    }
}
