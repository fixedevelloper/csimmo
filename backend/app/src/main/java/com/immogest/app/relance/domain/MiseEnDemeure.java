package com.immogest.app.relance.domain;

import com.immogest.app.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "rel_mise_en_demeure")
public class MiseEnDemeure extends BaseEntity {

    @Column(nullable = false, updatable = false)
    private String numero;
    @Column(name = "bail_id", nullable = false, updatable = false)
    private UUID bailId;
    @Column(name = "echeance_id", nullable = false, updatable = false)
    private UUID echeanceId;
    @Column(name = "montant_arrieres", nullable = false, updatable = false)
    private long montantArrieres;
    @Column(name = "delai_jours", nullable = false, updatable = false)
    private int delaiJours;
    @Column(name = "date_emission", nullable = false, updatable = false)
    private LocalDate dateEmission;
    @Column(name = "fichier_id", nullable = false)
    private UUID fichierId;
    @Column(name = "emis_par", updatable = false)
    private UUID emisPar;

    protected MiseEnDemeure() {
    }

    public MiseEnDemeure(String numero, UUID bailId, UUID echeanceId, long montant, int delai, LocalDate date, UUID emisPar) {
        this.numero = numero;
        this.bailId = bailId;
        this.echeanceId = echeanceId;
        this.montantArrieres = montant;
        this.delaiJours = delai;
        this.dateEmission = date;
        this.emisPar = emisPar;
    }

    public void attacherFichier(UUID fichierId) {
        this.fichierId = fichierId;
    }

    public String getNumero() { return numero; }
    public UUID getBailId() { return bailId; }
    public UUID getEcheanceId() { return echeanceId; }
    public long getMontantArrieres() { return montantArrieres; }
    public int getDelaiJours() { return delaiJours; }
    public LocalDate getDateEmission() { return dateEmission; }
    public UUID getFichierId() { return fichierId; }
}
