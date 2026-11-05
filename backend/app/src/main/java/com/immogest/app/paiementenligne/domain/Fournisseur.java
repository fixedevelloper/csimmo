package com.immogest.app.paiementenligne.domain;

import com.immogest.app.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/** Identifiants marchands du client chez un agrégateur : non secrets en clair, secrets chiffrés (AES-GCM) et jamais renvoyés. */
@Entity
@Table(name = "ol_fournisseur")
public class Fournisseur extends BaseEntity {

    @Column(nullable = false, updatable = false)
    private String code;
    @Column(nullable = false)
    private boolean actif;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String config = "{}";
    @Column(columnDefinition = "TEXT")
    private String secrets;

    protected Fournisseur() {
    }

    public Fournisseur(String code) {
        this.code = code;
    }

    public void modifier(boolean actif, String config, String secrets) {
        this.actif = actif;
        this.config = config;
        this.secrets = secrets;
    }

    public String getCode() { return code; }
    public boolean isActif() { return actif; }
    public String getConfig() { return config; }
    public String getSecrets() { return secrets; }
}
