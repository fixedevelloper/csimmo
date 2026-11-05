package com.immogest.app.tableaudebord;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/** Montants en FCFA entiers ; taux en points de base (8750 = 87,5 %), {@code null} quand le dénominateur est nul. */
public final class DashboardDto {

    private DashboardDto() {
    }

    /** @param tauxBp occupés / (libres + occupés) : les lots en travaux ne sont pas louables */
    public record Occupation(long libres, long occupes, long enTravaux, Integer tauxBp) {
    }

    public record Synthese(Occupation occupation, long attenduMois, long encaisseMois, Integer tauxRecouvrementMoisBp, long arrieresTotal,
            int echeancesEnRetard, int bauxAEcheance) {
    }

    public record Mois(LocalDate mois, long attendu, long encaisse, Integer tauxBp) {
    }

    /** @param id immeuble ou locataire */
    public record LigneArriere(UUID id, String libelle, long total, int echeances) {
    }

    public record Arrieres(long total, List<LigneArriere> parBien, List<LigneArriere> parLocataire) {
    }

    public record BailAEcheance(UUID bailId, String numero, String locataireNom, String logement, LocalDate dateFin, long joursRestants,
            String statut) {
    }
}
