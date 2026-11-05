package com.immogest.app.bail.domain;

import com.immogest.app.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

/** Exemplaire PDF figé d'un bail (le fichier est chiffré dans le stockage S3). */
@Entity
@Table(name = "bail_document")
public class BailDocument extends BaseEntity {

    @Column(name = "bail_id", nullable = false, updatable = false)
    private UUID bailId;

    @Column(name = "fichier_id", nullable = false, updatable = false)
    private UUID fichierId;

    @Column(name = "numero_version", nullable = false, updatable = false)
    private int numeroVersion;

    @Column(name = "genere_le", nullable = false, updatable = false)
    private OffsetDateTime genereLe;

    protected BailDocument() {
    }

    public BailDocument(UUID bailId, UUID fichierId, int numeroVersion, OffsetDateTime genereLe) {
        this.bailId = bailId;
        this.fichierId = fichierId;
        this.numeroVersion = numeroVersion;
        this.genereLe = genereLe;
    }

    public UUID getBailId() {
        return bailId;
    }

    public UUID getFichierId() {
        return fichierId;
    }

    public int getNumeroVersion() {
        return numeroVersion;
    }

    public OffsetDateTime getGenereLe() {
        return genereLe;
    }
}
