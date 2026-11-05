package com.immogest.app.finance.domain;

import com.immogest.app.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;

/** Dépense imputée au propriétaire d'un immeuble (déduite de son reversement du mois de la dépense). Jamais supprimée : annulée. */
@Entity
@Table(name = "fin_depense")
public class Depense extends BaseEntity {

    public static final String ENREGISTREE = "ENREGISTREE";
    public static final String ANNULEE = "ANNULEE";

    @Column(name = "immeuble_id", nullable = false, updatable = false)
    private UUID immeubleId;
    @Column(name = "lot_id", updatable = false)
    private UUID lotId;
    @Column(name = "proprietaire_id", nullable = false, updatable = false)
    private UUID proprietaireId;
    @Column(name = "date_depense", nullable = false, updatable = false)
    private LocalDate dateDepense;
    @Column(nullable = false, updatable = false)
    private String nature;
    @Column(nullable = false, updatable = false)
    private long montant;
    @Column(updatable = false)
    private String reference;
    @Column(nullable = false)
    private String statut = ENREGISTREE;
    @Column(name = "motif_annulation")
    private String motifAnnulation;
    @Column(name = "saisi_par", updatable = false)
    private UUID saisiPar;

    protected Depense() {
    }

    public Depense(UUID immeubleId, UUID lotId, UUID proprietaireId, LocalDate date, String nature, long montant, String reference, UUID saisiPar) {
        this.immeubleId = immeubleId;
        this.lotId = lotId;
        this.proprietaireId = proprietaireId;
        this.dateDepense = date;
        this.nature = nature;
        this.montant = montant;
        this.reference = reference;
        this.saisiPar = saisiPar;
    }

    public void annuler(String motif) {
        this.statut = ANNULEE;
        this.motifAnnulation = motif;
    }

    public UUID getImmeubleId() { return immeubleId; }
    public UUID getLotId() { return lotId; }
    public UUID getProprietaireId() { return proprietaireId; }
    public LocalDate getDateDepense() { return dateDepense; }
    public String getNature() { return nature; }
    public long getMontant() { return montant; }
    public String getReference() { return reference; }
    public String getStatut() { return statut; }
    public String getMotifAnnulation() { return motifAnnulation; }
}
