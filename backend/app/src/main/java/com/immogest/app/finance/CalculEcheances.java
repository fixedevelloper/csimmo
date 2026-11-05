package com.immogest.app.finance;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * Calendrier et montants des échéances d'un bail (fonctions pures, FCFA entiers).
 * Le loyer et les charges du bail sont MENSUELS ; une échéance couvre {@code mois} mois (1, 3 ou 12).
 */
public final class CalculEcheances {

    private CalculEcheances() {
    }

    /** @param montant FCFA ; au prorata des jours si la période est écourtée par la sortie ou la fin du bail */
    public record Periode(LocalDate debut, LocalDate fin, LocalDate dateEcheance, long montant) {
    }

    /**
     * Périodes dont l'échéance tombe au plus tard à {@code horizon}, sans dépasser {@code finEffective}.
     * La période i commence à {@code debutBail + i×mois} (calculé depuis le début, sans dérive : 31 janv. → 28 févr. → 31 mars)
     * et finit la veille de la suivante. L'échéance est le {@code jour} du mois de début (ramené à la fin des mois courts),
     * jamais avant le début de la période.
     */
    public static List<Periode> periodes(LocalDate debutBail, LocalDate finEffective, int mois, long loyerMensuel, int jour, LocalDate horizon) {
        List<Periode> resultat = new ArrayList<>();
        long plein = loyerMensuel * mois;
        for (int i = 0;; i++) {
            LocalDate debut = debutBail.plusMonths((long) i * mois);
            LocalDate suivant = debutBail.plusMonths((long) (i + 1) * mois);
            LocalDate fin = suivant.minusDays(1);
            LocalDate echeance = echeance(debut, jour);
            if (echeance.isAfter(horizon) || (finEffective != null && debut.isAfter(finEffective))) {
                return resultat;
            }
            long montant = plein;
            if (finEffective != null && fin.isAfter(finEffective)) {
                long joursReels = java.time.temporal.ChronoUnit.DAYS.between(debut, finEffective) + 1;
                long joursPeriode = java.time.temporal.ChronoUnit.DAYS.between(debut, suivant);
                montant = prorata(plein, joursReels, joursPeriode);
                fin = finEffective;
            }
            resultat.add(new Periode(debut, fin, echeance, montant));
        }
    }

    static LocalDate echeance(LocalDate debutPeriode, int jour) {
        int dernier = YearMonth.from(debutPeriode).lengthOfMonth();
        LocalDate date = debutPeriode.withDayOfMonth(Math.min(jour, dernier));
        return date.isBefore(debutPeriode) ? debutPeriode : date;
    }

    /** montant × jours / total, arrondi à l'entier le plus proche (moitié vers le haut), en arithmétique entière. */
    static long prorata(long montant, long jours, long total) {
        return (montant * jours * 2 + total) / (total * 2);
    }

    /**
     * Statut selon les montants et la date (règle 4/5 de CLAUDE.md §6) : payée si soldée ; sinon en retard une fois le délai de
     * grâce dépassé (même partiellement payée : elle reste dans le circuit des relances) ; partielle si un paiement a été imputé ;
     * due à partir de la date d'échéance ; à venir avant.
     */
    public static StatutEcheance statut(long montant, long paye, LocalDate dateEcheance, LocalDate aujourdhui, int graceJours) {
        if (paye >= montant) {
            return StatutEcheance.PAYEE;
        }
        if (aujourdhui.isAfter(dateEcheance.plusDays(graceJours))) {
            return StatutEcheance.EN_RETARD;
        }
        if (paye > 0) {
            return StatutEcheance.PARTIELLE;
        }
        return aujourdhui.isBefore(dateEcheance) ? StatutEcheance.A_VENIR : StatutEcheance.DUE;
    }
}
