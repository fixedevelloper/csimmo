package com.immogest.app.tiers.domain;

import com.immogest.app.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "locataire")
public class Locataire extends BaseEntity {

    @Column(nullable = false)
    private String nom;
    private String profession;
    private String telephone;
    private String email;
    private String adresse;

    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    /** Numéro de CNI chiffré par l'application (jamais en clair en base). */
    @Column(name = "numero_cni_chiffre")
    private String numeroCniChiffre;

    @Column(name = "utilisateur_id")
    private UUID utilisateurId;

    private String notes;

    @Column(nullable = false)
    private boolean archive;

    protected Locataire() {
    }

    public Locataire(String nom, String profession, String telephone, String email, String adresse, LocalDate dateNaissance,
            String numeroCniChiffre, UUID utilisateurId, String notes) {
        modifier(nom, profession, telephone, email, adresse, dateNaissance, numeroCniChiffre, utilisateurId, notes);
    }

    public void modifier(String nom, String profession, String telephone, String email, String adresse, LocalDate dateNaissance,
            String numeroCniChiffre, UUID utilisateurId, String notes) {
        this.nom = nom;
        this.profession = profession;
        this.telephone = telephone;
        this.email = email;
        this.adresse = adresse;
        this.dateNaissance = dateNaissance;
        this.numeroCniChiffre = numeroCniChiffre;
        this.utilisateurId = utilisateurId;
        this.notes = notes;
    }

    public void archiver() {
        this.archive = true;
    }

    public String getNom() {
        return nom;
    }

    public String getProfession() {
        return profession;
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

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public String getNumeroCniChiffre() {
        return numeroCniChiffre;
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
