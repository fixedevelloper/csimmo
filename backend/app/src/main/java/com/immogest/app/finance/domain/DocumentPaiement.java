package com.immogest.app.finance.domain;

import com.immogest.app.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/** Quittance (échéance soldée) ou reçu (paiement partiel). Le PDF est dans le stockage ; jamais supprimé, seulement ANNULE. */
@Entity
@Table(name = "fin_document")
public class DocumentPaiement extends BaseEntity {

    public static final String QUITTANCE = "QUITTANCE";
    public static final String RECU = "RECU";
    public static final String EMIS = "EMIS";
    public static final String ANNULE = "ANNULE";

    @Column(nullable = false, updatable = false)
    private String type;
    @Column(nullable = false, updatable = false)
    private String numero;
    @Column(name = "bail_id", nullable = false, updatable = false)
    private UUID bailId;
    @Column(name = "echeance_id", updatable = false)
    private UUID echeanceId;
    @Column(name = "paiement_id", updatable = false)
    private UUID paiementId;
    @Column(name = "fichier_id", nullable = false)
    private UUID fichierId;
    @Column(nullable = false, updatable = false)
    private long montant;
    @Column(nullable = false)
    private String statut = EMIS;
    @Column(name = "date_emission", nullable = false, updatable = false)
    private LocalDate dateEmission;
    @Column(name = "motif_annulation")
    private String motifAnnulation;
    @Column(name = "annule_le")
    private OffsetDateTime annuleLe;

    protected DocumentPaiement() {
    }

    public DocumentPaiement(String type, String numero, UUID bailId, UUID echeanceId, UUID paiementId, long montant, LocalDate dateEmission) {
        this.type = type;
        this.numero = numero;
        this.bailId = bailId;
        this.echeanceId = echeanceId;
        this.paiementId = paiementId;
        this.montant = montant;
        this.dateEmission = dateEmission;
    }

    public void attacherFichier(UUID fichierId) {
        this.fichierId = fichierId;
    }

    public void annuler(String motif, OffsetDateTime quand) {
        this.statut = ANNULE;
        this.motifAnnulation = motif;
        this.annuleLe = quand;
    }

    public String getType() { return type; }
    public String getNumero() { return numero; }
    public UUID getBailId() { return bailId; }
    public UUID getEcheanceId() { return echeanceId; }
    public UUID getPaiementId() { return paiementId; }
    public UUID getFichierId() { return fichierId; }
    public long getMontant() { return montant; }
    public String getStatut() { return statut; }
    public LocalDate getDateEmission() { return dateEmission; }
    public String getMotifAnnulation() { return motifAnnulation; }
}
