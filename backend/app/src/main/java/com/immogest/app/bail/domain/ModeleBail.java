package com.immogest.app.bail.domain;

import com.immogest.app.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "bail_modele")
public class ModeleBail extends BaseEntity {

    @Column(nullable = false, updatable = false)
    private boolean singleton = true;

    @Column(name = "bailleur_nom", nullable = false)
    private String bailleurNom;

    @Column(name = "bailleur_adresse")
    private String bailleurAdresse;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String preambule;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String clauses;

    @Column(name = "pied_de_page")
    private String piedDePage;

    protected ModeleBail() {
    }

    public ModeleBail(String bailleurNom, String bailleurAdresse, String preambule, String clauses, String piedDePage) {
        modifier(bailleurNom, bailleurAdresse, preambule, clauses, piedDePage);
    }

    public void modifier(String bailleurNom, String bailleurAdresse, String preambule, String clauses, String piedDePage) {
        this.bailleurNom = bailleurNom;
        this.bailleurAdresse = bailleurAdresse;
        this.preambule = preambule;
        this.clauses = clauses;
        this.piedDePage = piedDePage;
    }

    public String getBailleurNom() {
        return bailleurNom;
    }

    public String getBailleurAdresse() {
        return bailleurAdresse;
    }

    public String getPreambule() {
        return preambule;
    }

    public String getClauses() {
        return clauses;
    }

    public String getPiedDePage() {
        return piedDePage;
    }
}
