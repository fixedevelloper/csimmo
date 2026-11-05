package com.immogest.app.shared;

import java.time.LocalDate;
import java.time.YearMonth;

/** Intervalle de dates fermé aux deux bornes. */
public record Periode(LocalDate debut, LocalDate fin) {

    public Periode {
        if (debut == null || fin == null) {
            throw new IllegalArgumentException("Les bornes d'une période sont obligatoires");
        }
        if (fin.isBefore(debut)) {
            throw new IllegalArgumentException("La fin d'une période ne peut précéder son début");
        }
    }

    public static Periode mois(YearMonth mois) {
        return new Periode(mois.atDay(1), mois.atEndOfMonth());
    }

    public boolean contient(LocalDate date) {
        return !date.isBefore(debut) && !date.isAfter(fin);
    }

    public boolean chevauche(Periode autre) {
        return !autre.debut.isAfter(fin) && !debut.isAfter(autre.fin);
    }
}
