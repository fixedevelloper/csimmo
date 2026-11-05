package com.immogest.app.notification.domain;

import com.immogest.app.notification.TypeCanal;
import com.immogest.app.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "notif_canal")
public class CanalConfig extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private TypeCanal canal;
    @Column(nullable = false)
    private boolean actif;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String config = "{}";
    @Column(columnDefinition = "TEXT")
    private String secrets;

    protected CanalConfig() {
    }

    public CanalConfig(TypeCanal canal) {
        this.canal = canal;
    }

    public void modifier(boolean actif, String config, String secrets) {
        this.actif = actif;
        this.config = config;
        this.secrets = secrets;
    }

    public TypeCanal getCanal() { return canal; }
    public boolean isActif() { return actif; }
    public String getConfig() { return config; }
    public String getSecrets() { return secrets; }
}
