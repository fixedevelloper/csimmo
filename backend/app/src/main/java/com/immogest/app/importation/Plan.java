package com.immogest.app.importation;

import com.immogest.app.patrimoine.TypeLot;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/** Ce qui sera créé, une fois le fichier entièrement validé. {@code ligne} renvoie à la ligne du fichier pour les rapports d'erreur. */
record Plan(List<Imm> immeubles, List<Lot> lots, List<Loc> locataires, List<Bail> baux, Map<String, Solde> soldes) {

    record Imm(int ligne, String nom, String adresse, String quartier, String ville, String description) {
    }

    record Lot(int ligne, String immeubleCle, String immeubleNom, String reference, TypeLot type, BigDecimal surface, Integer pieces, Long loyerIndicatif) {
    }

    record Loc(int ligne, String nom, String telephone, String email, String profession, String adresse, LocalDate naissance, String cni) {
    }

    record Bail(int ligne, String immeubleCle, String lotRef, String telephone, LocalDate debut, LocalDate fin, long loyer, long charges, String periodicite, int jourEcheance,
            long caution, int preavisMois) {
        String cleLot() {
            return immeubleCle + "|" + Valeurs.cle(lotRef);
        }
    }

    record Solde(int ligne, long solde, LocalDate dateReprise) {
    }
}
