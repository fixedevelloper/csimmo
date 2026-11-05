package com.immogest.app.patrimoine.domain;

import com.immogest.app.patrimoine.CibleTypePhoto;
import com.immogest.app.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "patrimoine_photo")
public class PhotoPatrimoine extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "cible_type", nullable = false, updatable = false)
    private CibleTypePhoto cibleType;

    @Column(name = "cible_id", nullable = false, updatable = false)
    private UUID cibleId;

    @Column(name = "fichier_id", nullable = false, updatable = false)
    private UUID fichierId;

    private String legende;

    @Column(nullable = false)
    private int ordre;

    protected PhotoPatrimoine() {
    }

    public PhotoPatrimoine(CibleTypePhoto cibleType, UUID cibleId, UUID fichierId, String legende, int ordre) {
        this.cibleType = cibleType;
        this.cibleId = cibleId;
        this.fichierId = fichierId;
        this.legende = legende;
        this.ordre = ordre;
    }

    public CibleTypePhoto getCibleType() {
        return cibleType;
    }

    public UUID getCibleId() {
        return cibleId;
    }

    public UUID getFichierId() {
        return fichierId;
    }

    public String getLegende() {
        return legende;
    }

    public int getOrdre() {
        return ordre;
    }
}
