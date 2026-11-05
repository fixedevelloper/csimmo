package com.immogest.app.tiers.domain;

import com.immogest.app.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "garant")
public class Garant extends BaseEntity {

    @Column(name = "locataire_id", nullable = false, updatable = false)
    private UUID locataireId;

    @Column(nullable = false)
    private String nom;
    private String lien;
    private String profession;

    @Column(nullable = false)
    private String telephone;
    private String adresse;

    @Column(name = "numero_cni_chiffre")
    private String numeroCniChiffre;

    @Column(nullable = false)
    private boolean archive;

    protected Garant() {
    }

    public Garant(UUID locataireId, String nom, String lien, String profession, String telephone, String adresse,
            String numeroCniChiffre) {
        this.locataireId = locataireId;
        modifier(nom, lien, profession, telephone, adresse, numeroCniChiffre);
    }

    public void modifier(String nom, String lien, String profession, String telephone, String adresse, String numeroCniChiffre) {
        this.nom = nom;
        this.lien = lien;
        this.profession = profession;
        this.telephone = telephone;
        this.adresse = adresse;
        this.numeroCniChiffre = numeroCniChiffre;
    }

    public void archiver() {
        this.archive = true;
    }

    public UUID getLocataireId() {
        return locataireId;
    }

    public String getNom() {
        return nom;
    }

    public String getLien() {
        return lien;
    }

    public String getProfession() {
        return profession;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getAdresse() {
        return adresse;
    }

    public String getNumeroCniChiffre() {
        return numeroCniChiffre;
    }

    public boolean isArchive() {
        return archive;
    }
}
