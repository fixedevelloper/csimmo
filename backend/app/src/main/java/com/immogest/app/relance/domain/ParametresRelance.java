package com.immogest.app.relance.domain;

import com.immogest.app.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "rel_parametres")
public class ParametresRelance extends BaseEntity {

    @Column(nullable = false, updatable = false)
    private boolean singleton = true;
    @Column(nullable = false)
    private boolean actif = true;
    @Column(nullable = false)
    private String niveaux = "-5,3,10,30";
    @Column(name = "delai_mise_en_demeure_jours", nullable = false)
    private int delaiMiseEnDemeureJours = 8;

    public void modifier(boolean actif, String niveaux, int delai) {
        this.actif = actif;
        this.niveaux = niveaux;
        this.delaiMiseEnDemeureJours = delai;
    }

    public boolean isActif() { return actif; }
    public String getNiveaux() { return niveaux; }
    public int getDelaiMiseEnDemeureJours() { return delaiMiseEnDemeureJours; }
}
