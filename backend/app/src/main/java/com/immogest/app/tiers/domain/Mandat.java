package com.immogest.app.tiers.domain;

import com.immogest.app.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "mandat")
public class Mandat extends BaseEntity {

    @Column(name = "proprietaire_id", nullable = false, updatable = false)
    private UUID proprietaireId;

    @Column(name = "taux_honoraires_bp", nullable = false)
    private int tauxHonorairesBp;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    private String notes;

    protected Mandat() {
    }

    public Mandat(UUID proprietaireId, int tauxHonorairesBp, LocalDate dateDebut, LocalDate dateFin, String notes) {
        this.proprietaireId = proprietaireId;
        modifier(tauxHonorairesBp, dateDebut, dateFin, notes);
    }

    public void modifier(int tauxHonorairesBp, LocalDate dateDebut, LocalDate dateFin, String notes) {
        this.tauxHonorairesBp = tauxHonorairesBp;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.notes = notes;
    }

    public boolean couvre(LocalDate date) {
        return !date.isBefore(dateDebut) && (dateFin == null || !date.isAfter(dateFin));
    }

    public UUID getProprietaireId() {
        return proprietaireId;
    }

    public int getTauxHonorairesBp() {
        return tauxHonorairesBp;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public String getNotes() {
        return notes;
    }
}
