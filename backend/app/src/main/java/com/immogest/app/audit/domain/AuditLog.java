package com.immogest.app.audit.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Immutable
@Table(name = "audit_log")
public class AuditLog {

    @Id
    private UUID id = UUID.randomUUID();

    @Column(nullable = false)
    private String auteur;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private String entite;

    @Column(name = "entite_id")
    private UUID entiteId;

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> avant;

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> apres;

    @Column(nullable = false)
    private OffsetDateTime date;

    protected AuditLog() {
    }

    public AuditLog(String auteur, String action, String entite, UUID entiteId,
            Map<String, Object> avant, Map<String, Object> apres, OffsetDateTime date) {
        this.auteur = auteur;
        this.action = action;
        this.entite = entite;
        this.entiteId = entiteId;
        this.avant = avant;
        this.apres = apres;
        this.date = date;
    }

    public UUID getId() {
        return id;
    }

    public String getAuteur() {
        return auteur;
    }

    public String getAction() {
        return action;
    }

    public String getEntite() {
        return entite;
    }

    public UUID getEntiteId() {
        return entiteId;
    }

    public Map<String, Object> getAvant() {
        return avant;
    }

    public Map<String, Object> getApres() {
        return apres;
    }

    public OffsetDateTime getDate() {
        return date;
    }
}
