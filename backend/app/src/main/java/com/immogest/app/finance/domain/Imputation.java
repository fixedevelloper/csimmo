package com.immogest.app.finance.domain;

import com.immogest.app.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

/** Part d'un paiement affectée à une échéance (jamais modifiée ni supprimée). */
@Entity
@Table(name = "fin_imputation")
public class Imputation extends BaseEntity {

    @Column(name = "paiement_id", nullable = false, updatable = false)
    private UUID paiementId;
    @Column(name = "echeance_id", nullable = false, updatable = false)
    private UUID echeanceId;
    @Column(nullable = false, updatable = false)
    private long montant;

    protected Imputation() {
    }

    public Imputation(UUID paiementId, UUID echeanceId, long montant) {
        this.paiementId = paiementId;
        this.echeanceId = echeanceId;
        this.montant = montant;
    }

    public UUID getPaiementId() { return paiementId; }
    public UUID getEcheanceId() { return echeanceId; }
    public long getMontant() { return montant; }
}
