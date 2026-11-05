package com.immogest.app.stockage.domain;

import com.immogest.app.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "fichier")
public class Fichier extends BaseEntity {

    @Column(nullable = false, updatable = false)
    private String cle;

    @Column(name = "nom_origine", nullable = false, updatable = false)
    private String nomOrigine;

    @Column(name = "type_mime", nullable = false, updatable = false)
    private String typeMime;

    @Column(nullable = false, updatable = false)
    private long taille;

    @Column(nullable = false, updatable = false)
    private String sha256;

    @Column(nullable = false, updatable = false)
    private boolean chiffre;

    @Column(name = "module_proprietaire", nullable = false, updatable = false)
    private String moduleProprietaire;

    @Column(nullable = false, updatable = false)
    private String entite;

    @Column(name = "entite_id", nullable = false, updatable = false)
    private UUID entiteId;

    protected Fichier() {
    }

    public Fichier(String cle, String nomOrigine, String typeMime, long taille, String sha256, boolean chiffre,
            String moduleProprietaire, String entite, UUID entiteId) {
        this.cle = cle;
        this.nomOrigine = nomOrigine;
        this.typeMime = typeMime;
        this.taille = taille;
        this.sha256 = sha256;
        this.chiffre = chiffre;
        this.moduleProprietaire = moduleProprietaire;
        this.entite = entite;
        this.entiteId = entiteId;
    }

    public String getCle() {
        return cle;
    }

    public String getNomOrigine() {
        return nomOrigine;
    }

    public String getTypeMime() {
        return typeMime;
    }

    public long getTaille() {
        return taille;
    }

    public String getSha256() {
        return sha256;
    }

    public boolean isChiffre() {
        return chiffre;
    }

    public String getModuleProprietaire() {
        return moduleProprietaire;
    }

    public String getEntite() {
        return entite;
    }

    public UUID getEntiteId() {
        return entiteId;
    }
}
