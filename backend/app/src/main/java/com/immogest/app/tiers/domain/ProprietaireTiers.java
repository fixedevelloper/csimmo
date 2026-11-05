package com.immogest.app.tiers.domain;

import com.immogest.app.shared.BaseEntity;
import com.immogest.app.tiers.ModeReversement;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "proprietaire_tiers")
public class ProprietaireTiers extends BaseEntity {

    @Column(nullable = false)
    private String nom;
    private String telephone;
    private String email;
    private String adresse;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode_reversement", nullable = false)
    private ModeReversement modeReversement = ModeReversement.VIREMENT;

    @Column(name = "coordonnees_reversement")
    private String coordonneesReversement;

    @Column(name = "utilisateur_id")
    private UUID utilisateurId;

    private String notes;

    @Column(nullable = false)
    private boolean archive;

    protected ProprietaireTiers() {
    }

    public ProprietaireTiers(String nom, String telephone, String email, String adresse, ModeReversement mode,
            String coordonneesReversement, UUID utilisateurId, String notes) {
        modifier(nom, telephone, email, adresse, mode, coordonneesReversement, utilisateurId, notes);
    }

    public void modifier(String nom, String telephone, String email, String adresse, ModeReversement mode,
            String coordonneesReversement, UUID utilisateurId, String notes) {
        this.nom = nom;
        this.telephone = telephone;
        this.email = email;
        this.adresse = adresse;
        this.modeReversement = mode;
        this.coordonneesReversement = coordonneesReversement;
        this.utilisateurId = utilisateurId;
        this.notes = notes;
    }

    public void archiver() {
        this.archive = true;
    }

    public String getNom() {
        return nom;
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

    public ModeReversement getModeReversement() {
        return modeReversement;
    }

    public String getCoordonneesReversement() {
        return coordonneesReversement;
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
