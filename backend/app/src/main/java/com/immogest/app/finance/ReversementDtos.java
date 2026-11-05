package com.immogest.app.finance;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/** DTO des dépenses et reversements (montants en FCFA entiers, taux en points de base). */
public final class ReversementDtos {

    private ReversementDtos() {
    }

    /** @param periode premier jour du mois ; @param fichierId relevé PDF (reversement validé), à télécharger par {@code /api/v1/fichiers/{id}} */
    public record ReversementDto(UUID id, UUID proprietaireId, String proprietaireNom, LocalDate periode, String statut, String numero,
            long loyersEncaisses, long chargesEncaissees, long brut, long honoraires, Integer tauxMoyenBp, long depenses, long net,
            boolean sansMandat, UUID fichierId, OffsetDateTime calculeLe, OffsetDateTime valideLe, LocalDate payeLe, String modePaiement,
            String referencePaiement) {
    }

    public record DepenseDto(UUID id, UUID immeubleId, String immeubleNom, UUID lotId, UUID proprietaireId, LocalDate dateDepense, String nature,
            long montant, String reference, String statut, String motifAnnulation) {
    }

    public record DepenseSaisie(UUID immeubleId, UUID lotId, LocalDate dateDepense, String nature, Long montant, String reference) {
    }

    public record PaiementReversementSaisie(String mode, String reference, LocalDate date) {
    }

    /** Contenu déjà formaté du relevé PDF. */
    public record DonneesReleve(String titre, String numero, boolean projet, List<String> corps, List<LigneEncaissement> encaissements,
            List<LigneDepense> depenses, List<LigneRecap> recap, String note, String emission) {
    }

    public record LigneEncaissement(String libelle, String loyers, String charges, String total) {
    }

    public record LigneDepense(String date, String nature, String montant) {
    }

    public record LigneRecap(String libelle, String valeur, boolean total) {
    }
}
