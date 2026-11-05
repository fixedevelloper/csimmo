package com.immogest.app.securite.domain;

import com.immogest.app.securite.Role;
import com.immogest.app.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Table(name = "utilisateur")
public class Utilisateur extends BaseEntity {

    @Column(nullable = false)
    private String nom;

    private String telephone;

    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name = "mot_de_passe_hash", nullable = false)
    private String motDePasseHash;

    @Column(nullable = false)
    private boolean actif = true;

    @Column(name = "deux_facteurs_actif", nullable = false)
    private boolean deuxFacteursActif;

    @Column(name = "tentatives_echouees", nullable = false)
    private int tentativesEchouees;

    @Column(name = "verrouille_jusqua")
    private OffsetDateTime verrouilleJusqua;

    @Column(name = "derniere_connexion")
    private OffsetDateTime derniereConnexion;

    protected Utilisateur() {
    }

    public Utilisateur(String nom, String telephone, String email, Role role, String motDePasseHash,
            boolean deuxFacteursActif) {
        this.nom = nom;
        this.telephone = telephone;
        this.email = email;
        this.role = role;
        this.motDePasseHash = motDePasseHash;
        this.deuxFacteursActif = deuxFacteursActif;
    }

    public boolean estVerrouille(OffsetDateTime maintenant) {
        return verrouilleJusqua != null && maintenant.isBefore(verrouilleJusqua);
    }

    /** Enregistre un échec ; verrouille le compte à {@code echecsMax} échecs. Retourne vrai si le compte vient d'être verrouillé. */
    public boolean enregistrerEchec(int echecsMax, OffsetDateTime verrouilleJusqua) {
        tentativesEchouees++;
        if (tentativesEchouees >= echecsMax) {
            this.verrouilleJusqua = verrouilleJusqua;
            tentativesEchouees = 0;
            return true;
        }
        return false;
    }

    public void enregistrerConnexion(OffsetDateTime maintenant) {
        tentativesEchouees = 0;
        verrouilleJusqua = null;
        derniereConnexion = maintenant;
    }

    public void deverrouiller() {
        tentativesEchouees = 0;
        verrouilleJusqua = null;
    }

    public void modifier(String nom, String telephone, String email, Role role, boolean actif,
            boolean deuxFacteursActif) {
        this.nom = nom;
        this.telephone = telephone;
        this.email = email;
        this.role = role;
        this.actif = actif;
        this.deuxFacteursActif = deuxFacteursActif;
    }

    public void changerMotDePasse(String hash) {
        this.motDePasseHash = hash;
        deverrouiller();
    }

    public void desactiver() {
        this.actif = false;
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

    public Role getRole() {
        return role;
    }

    public String getMotDePasseHash() {
        return motDePasseHash;
    }

    public boolean isActif() {
        return actif;
    }

    public boolean isDeuxFacteursActif() {
        return deuxFacteursActif;
    }

    public int getTentativesEchouees() {
        return tentativesEchouees;
    }

    public OffsetDateTime getVerrouilleJusqua() {
        return verrouilleJusqua;
    }

    public void setVerrouilleJusqua(OffsetDateTime verrouilleJusqua) {
        this.verrouilleJusqua = verrouilleJusqua;
    }

    public OffsetDateTime getDerniereConnexion() {
        return derniereConnexion;
    }
}
