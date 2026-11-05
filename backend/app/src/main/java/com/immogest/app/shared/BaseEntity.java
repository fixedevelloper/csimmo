package com.immogest.app.shared;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Version;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.UUID;

/** Entité de base : identifiant UUID, horodatages UTC et verrou optimiste. */
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    private UUID id = UUID.randomUUID();

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Version
    private Long version;

    @PrePersist
    void avantCreation() {
        OffsetDateTime maintenant = OffsetDateTime.now(ZoneOffset.UTC);
        createdAt = maintenant;
        updatedAt = maintenant;
    }

    @PreUpdate
    void avantModification() {
        updatedAt = OffsetDateTime.now(ZoneOffset.UTC);
    }

    public UUID getId() {
        return id;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Long getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof BaseEntity autre
                && getClass().equals(autre.getClass()) && Objects.equals(id, autre.id));
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), id);
    }
}
