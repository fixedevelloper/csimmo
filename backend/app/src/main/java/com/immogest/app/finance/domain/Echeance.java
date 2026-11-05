package com.immogest.app.finance.domain;

import com.immogest.app.finance.StatutEcheance;
import com.immogest.app.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "fin_echeance")
public class Echeance extends BaseEntity {

    @Column(name = "bail_id", nullable = false, updatable = false)
    private UUID bailId;
    @Column(name = "periode_debut", nullable = false, updatable = false)
    private LocalDate periodeDebut;
    @Column(name = "periode_fin", nullable = false)
    private LocalDate periodeFin;
    @Column(name = "date_echeance", nullable = false)
    private LocalDate dateEcheance;
    @Column(nullable = false)
    private long montant;
    @Column(name = "montant_paye", nullable = false)
    private long montantPaye;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutEcheance statut;
    /** Échéance d'avant la mise en service (reprise de données) : créée avec son montant déjà payé, sans paiement ni imputation. */
    @Column(nullable = false)
    private boolean reprise;

    protected Echeance() {
    }

    public Echeance(UUID bailId, LocalDate debut, LocalDate fin, LocalDate dateEcheance, long montant, StatutEcheance statut) {
        this.bailId = bailId;
        this.periodeDebut = debut;
        this.periodeFin = fin;
        this.dateEcheance = dateEcheance;
        this.montant = montant;
        this.statut = statut;
    }

    /** Marque l'échéance comme reprise de l'historique, avec la part déjà réglée avant la mise en service. */
    public void reprendre(long dejaPaye) {
        this.montantPaye = dejaPaye;
        this.reprise = true;
    }

    public long reste() {
        return montant - montantPaye;
    }

    public void imputer(long somme) {
        this.montantPaye += somme;
    }

    /** Ajuste période et montant (sortie anticipée) : seulement tant que rien n'est payé. */
    public void ajuster(LocalDate fin, long montant) {
        this.periodeFin = fin;
        this.montant = montant;
    }

    public void setStatut(StatutEcheance statut) {
        this.statut = statut;
    }

    public UUID getBailId() { return bailId; }
    public LocalDate getPeriodeDebut() { return periodeDebut; }
    public LocalDate getPeriodeFin() { return periodeFin; }
    public LocalDate getDateEcheance() { return dateEcheance; }
    public long getMontant() { return montant; }
    public long getMontantPaye() { return montantPaye; }
    public StatutEcheance getStatut() { return statut; }
    public boolean isReprise() { return reprise; }
}
