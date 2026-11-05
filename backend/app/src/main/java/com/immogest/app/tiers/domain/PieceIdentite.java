package com.immogest.app.tiers.domain;

import com.immogest.app.shared.BaseEntity;
import com.immogest.app.tiers.CiblePiece;
import com.immogest.app.tiers.TypePiece;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "tiers_piece")
public class PieceIdentite extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "cible_type", nullable = false, updatable = false)
    private CiblePiece cibleType;

    @Column(name = "cible_id", nullable = false, updatable = false)
    private UUID cibleId;

    @Column(name = "fichier_id", nullable = false, updatable = false)
    private UUID fichierId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_piece", nullable = false, updatable = false)
    private TypePiece typePiece;

    protected PieceIdentite() {
    }

    public PieceIdentite(CiblePiece cibleType, UUID cibleId, UUID fichierId, TypePiece typePiece) {
        this.cibleType = cibleType;
        this.cibleId = cibleId;
        this.fichierId = fichierId;
        this.typePiece = typePiece;
    }

    public CiblePiece getCibleType() {
        return cibleType;
    }

    public UUID getCibleId() {
        return cibleId;
    }

    public UUID getFichierId() {
        return fichierId;
    }

    public TypePiece getTypePiece() {
        return typePiece;
    }
}
