package com.immogest.app.patrimoine.domain;

import com.immogest.app.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "immeuble")
public class Immeuble extends BaseEntity {

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String adresse;

    private String quartier;

    @Column(nullable = false)
    private String ville;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private String description;

    @Column(name = "proprietaire_tiers_id")
    private java.util.UUID proprietaireTiersId;

    @Column(nullable = false)
    private boolean archive;

    protected Immeuble() {
    }

    public Immeuble(String nom, String adresse, String quartier, String ville, BigDecimal latitude,
            BigDecimal longitude, String description, java.util.UUID proprietaireTiersId) {
        modifier(nom, adresse, quartier, ville, latitude, longitude, description, proprietaireTiersId);
    }

    public void modifier(String nom, String adresse, String quartier, String ville, BigDecimal latitude,
            BigDecimal longitude, String description, java.util.UUID proprietaireTiersId) {
        this.proprietaireTiersId = proprietaireTiersId;
        this.nom = nom;
        this.adresse = adresse;
        this.quartier = quartier;
        this.ville = ville;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
    }

    public void archiver() {
        this.archive = true;
    }

    public String getNom() {
        return nom;
    }

    public String getAdresse() {
        return adresse;
    }

    public String getQuartier() {
        return quartier;
    }

    public String getVille() {
        return ville;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public String getDescription() {
        return description;
    }

    public java.util.UUID getProprietaireTiersId() {
        return proprietaireTiersId;
    }

    public boolean isArchive() {
        return archive;
    }
}
