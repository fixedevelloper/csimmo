package com.immogest.app.bail;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record BailDto(
        UUID id,
        String numero,
        UUID lotId,
        String lotReference,
        String immeubleNom,
        UUID locataireId,
        String locataireNom,
        UUID bailPrecedentId,
        StatutBail statut,
        LocalDate dateDebut,
        LocalDate dateFin,
        long loyer,
        long charges,
        Periodicite periodicite,
        int jourEcheance,
        long caution,
        int moisAvance,
        int preavisMois,
        LocalDate dateNotificationPreavis,
        LocalDate dateSortie,
        String motifResiliation,
        LocalDate dateFinEffective,
        List<GarantResume> garants) {

    public record GarantResume(UUID id, String nom, String telephone) {
    }
}
