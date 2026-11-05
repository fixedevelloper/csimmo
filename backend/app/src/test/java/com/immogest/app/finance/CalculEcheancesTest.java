package com.immogest.app.finance;

import static org.assertj.core.api.Assertions.assertThat;

import com.immogest.app.finance.CalculEcheances.Periode;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class CalculEcheancesTest {

    private static LocalDate d(String s) {
        return LocalDate.parse(s);
    }

    @Test
    void mensuelleUneEcheanceParMoisSansTrouNiChevauchement() {
        List<Periode> p = CalculEcheances.periodes(d("2026-01-01"), null, 1, 160_000, 5, d("2026-04-10"));
        assertThat(p).extracting(Periode::debut).containsExactly(d("2026-01-01"), d("2026-02-01"), d("2026-03-01"), d("2026-04-01"));
        assertThat(p).extracting(Periode::dateEcheance).containsExactly(d("2026-01-05"), d("2026-02-05"), d("2026-03-05"), d("2026-04-05"));
        assertThat(p.get(0).fin()).isEqualTo(d("2026-01-31"));
        assertThat(p.get(1).fin()).isEqualTo(d("2026-02-28"));
        assertThat(p).extracting(Periode::montant).containsOnly(160_000L);
    }

    @Test
    void trimestrielleEtAnnuelleMultiplientLeMensuel() {
        List<Periode> t = CalculEcheances.periodes(d("2026-01-01"), null, 3, 100_000, 1, d("2026-07-01"));
        assertThat(t).extracting(Periode::debut).containsExactly(d("2026-01-01"), d("2026-04-01"), d("2026-07-01"));
        assertThat(t.get(0).montant()).isEqualTo(300_000);
        assertThat(t.get(0).fin()).isEqualTo(d("2026-03-31"));

        List<Periode> a = CalculEcheances.periodes(d("2026-03-01"), null, 12, 100_000, 1, d("2027-02-28"));
        assertThat(a).hasSize(1);
        assertThat(a.get(0).montant()).isEqualTo(1_200_000);
        assertThat(a.get(0).fin()).isEqualTo(d("2027-02-28"));
    }

    @Test
    void moisCourtsPasDeDeriveEtJourRamenéAFinDeMois() {
        // début le 31 : février tombe le 28, mars retrouve le 31
        List<Periode> p = CalculEcheances.periodes(d("2026-01-31"), null, 1, 100, 31, d("2026-03-31"));
        assertThat(p).extracting(Periode::debut).containsExactly(d("2026-01-31"), d("2026-02-28"), d("2026-03-31"));
        assertThat(p).extracting(Periode::fin).containsExactly(d("2026-02-27"), d("2026-03-30"), d("2026-04-29"));
        // échéance le 31 dans un mois de 30 jours ou de 28 : dernier jour du mois
        List<Periode> q = CalculEcheances.periodes(d("2026-02-01"), null, 1, 100, 31, d("2026-02-28"));
        assertThat(q.get(0).dateEcheance()).isEqualTo(d("2026-02-28"));
    }

    @Test
    void echeanceJamaisAvantLeDebutDeLaPeriode() {
        List<Periode> p = CalculEcheances.periodes(d("2026-01-15"), null, 1, 100, 5, d("2026-02-20"));
        assertThat(p).extracting(Periode::dateEcheance).containsExactly(d("2026-01-15"), d("2026-02-15"));
    }

    @Test
    void anneeBissextile() {
        List<Periode> p = CalculEcheances.periodes(d("2028-01-29"), null, 1, 100, 29, d("2028-03-01"));
        assertThat(p).extracting(Periode::debut).containsExactly(d("2028-01-29"), d("2028-02-29"));
    }

    @Test
    void derniereEcheanceAuProrataDeLaSortie() {
        // sortie le 14 mars : période du 1er au 31 mars = 31 jours, 14 jours occupés
        List<Periode> p = CalculEcheances.periodes(d("2026-01-01"), d("2026-03-14"), 1, 310_000, 5, d("2026-12-31"));
        assertThat(p).hasSize(3);
        Periode derniere = p.get(2);
        assertThat(derniere.fin()).isEqualTo(d("2026-03-14"));
        assertThat(derniere.montant()).isEqualTo(140_000);
        assertThat(p.get(1).montant()).isEqualTo(310_000);
    }

    @Test
    void finDeBailAlignéeSurLaPeriodeNEstPasProratisee() {
        List<Periode> p = CalculEcheances.periodes(d("2026-01-01"), d("2026-12-31"), 1, 100_000, 5, d("2027-06-01"));
        assertThat(p).hasSize(12).extracting(Periode::montant).containsOnly(100_000L);
    }

    @Test
    void prorataArrondiALEntierLePlusProche() {
        assertThat(CalculEcheances.prorata(100, 1, 3)).isEqualTo(33);
        assertThat(CalculEcheances.prorata(100, 2, 3)).isEqualTo(67);
        assertThat(CalculEcheances.prorata(1, 1, 2)).isEqualTo(1);
    }

    @Test
    void statuts() {
        LocalDate due = d("2026-03-05");
        assertThat(CalculEcheances.statut(100, 0, due, d("2026-03-04"), 5)).isEqualTo(StatutEcheance.A_VENIR);
        assertThat(CalculEcheances.statut(100, 0, due, d("2026-03-05"), 5)).isEqualTo(StatutEcheance.DUE);
        assertThat(CalculEcheances.statut(100, 0, due, d("2026-03-10"), 5)).isEqualTo(StatutEcheance.DUE);
        assertThat(CalculEcheances.statut(100, 0, due, d("2026-03-11"), 5)).isEqualTo(StatutEcheance.EN_RETARD);
        assertThat(CalculEcheances.statut(100, 40, due, d("2026-03-07"), 5)).isEqualTo(StatutEcheance.PARTIELLE);
        assertThat(CalculEcheances.statut(100, 40, due, d("2026-03-11"), 5)).isEqualTo(StatutEcheance.EN_RETARD);
        assertThat(CalculEcheances.statut(100, 100, due, d("2026-06-01"), 5)).isEqualTo(StatutEcheance.PAYEE);
        assertThat(CalculEcheances.statut(100, 0, due, d("2026-03-08"), 0)).isEqualTo(StatutEcheance.EN_RETARD);
    }
}
