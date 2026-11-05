package com.immogest.app.maintenance.domain;

import com.immogest.app.maintenance.StatutSignalement;
import com.immogest.app.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "mnt_signalement")
public class Signalement extends BaseEntity {

    @Column(name = "bail_id", nullable = false, updatable = false)
    private UUID bailId;
    @Column(name = "lot_id", nullable = false, updatable = false)
    private UUID lotId;
    @Column(name = "locataire_id", nullable = false, updatable = false)
    private UUID locataireId;
    @Column(nullable = false, updatable = false, columnDefinition = "TEXT")
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutSignalement statut = StatutSignalement.NOUVEAU;
    @Column(name = "fichier_id")
    private UUID fichierId;
    @Column(name = "note_gestion", columnDefinition = "TEXT")
    private String noteGestion;

    protected Signalement() {
    }

    public Signalement(UUID bailId, UUID lotId, UUID locataireId, String description) {
        this.bailId = bailId;
        this.lotId = lotId;
        this.locataireId = locataireId;
        this.description = description;
    }

    public void attacherPhoto(UUID fichierId) {
        this.fichierId = fichierId;
    }

    public void traiter(StatutSignalement statut, String note) {
        this.statut = statut;
        this.noteGestion = note;
    }

    public UUID getBailId() { return bailId; }
    public UUID getLotId() { return lotId; }
    public UUID getLocataireId() { return locataireId; }
    public String getDescription() { return description; }
    public StatutSignalement getStatut() { return statut; }
    public UUID getFichierId() { return fichierId; }
    public String getNoteGestion() { return noteGestion; }
}
