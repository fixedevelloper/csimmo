package com.immogest.app.tiers.domain;

import com.immogest.app.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "prestataire")
public class Prestataire extends BaseEntity {

    @Column(nullable = false)
    private String nom;
    private String metier;
    private String telephone;
    private String email;
    private String adresse;

    @Column(name = "utilisateur_id")
    private UUID utilisateurId;

    private String notes;

    @Column(nullable = false)
    private boolean archive;

    protected Prestataire() {
    }

    public Prestataire(String nom, String metier, String telephone, String email, String adresse, UUID utilisateurId, String notes) {
        modifier(nom, metier, telephone, email, adresse, utilisateurId, notes);
    }

    public void modifier(String nom, String metier, String telephone, String email, String adresse, UUID utilisateurId, String notes) {
        this.nom = nom;
        this.metier = metier;
        this.telephone = telephone;
        this.email = email;
        this.adresse = adresse;
        this.utilisateurId = utilisateurId;
        this.notes = notes;
    }

    public void archiver() {
        this.archive = true;
    }

    public String getNom() {
        return nom;
    }

    public String getMetier() {
        return metier;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getEmail() {
        return email;
    }

    public String getAdresse() {
        return adresse;
    }

    public UUID getUtilisateurId() {
        return utilisateurId;
    }

    public String getNotes() {
        return notes;
    }

    public boolean isArchive() {
        return archive;
    }
}
