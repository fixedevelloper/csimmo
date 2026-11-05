package com.immogest.app.paiementenligne.domain;

import com.immogest.app.paiementenligne.OperateurMobile;
import com.immogest.app.paiementenligne.StatutTransaction;
import com.immogest.app.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "ol_transaction")
public class Transaction extends BaseEntity {

    @Column(name = "bail_id", nullable = false, updatable = false)
    private UUID bailId;
    @Column(name = "locataire_id", nullable = false, updatable = false)
    private UUID locataireId;
    @Column(nullable = false, updatable = false)
    private String fournisseur;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private OperateurMobile operateur;
    @Column(nullable = false, updatable = false)
    private String telephone;
    @Column(nullable = false, updatable = false)
    private long montant;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutTransaction statut = StatutTransaction.EN_ATTENTE;
    @Column(name = "reference_fournisseur")
    private String referenceFournisseur;
    @Column(name = "reference_operateur")
    private String referenceOperateur;
    @Column(name = "cle_idempotence", nullable = false, updatable = false)
    private String cleIdempotence;
    @Column(nullable = false, updatable = false)
    private String empreinte;
    @Column(name = "paiement_id")
    private UUID paiementId;
    private String erreur;
    @Column(nullable = false)
    private int verifications;
    @Column(name = "derniere_verification")
    private OffsetDateTime derniereVerification;
    @Column(name = "confirmee_le")
    private OffsetDateTime confirmeeLe;

    protected Transaction() {
    }

    public Transaction(UUID bailId, UUID locataireId, String fournisseur, OperateurMobile operateur, String telephone, long montant, String cle,
            String empreinte) {
        this.bailId = bailId;
        this.locataireId = locataireId;
        this.fournisseur = fournisseur;
        this.operateur = operateur;
        this.telephone = telephone;
        this.montant = montant;
        this.cleIdempotence = cle;
        this.empreinte = empreinte;
    }

    public void referencer(String referenceFournisseur) {
        this.referenceFournisseur = referenceFournisseur;
    }

    public void confirmer(UUID paiementId, String referenceOperateur, OffsetDateTime quand) {
        this.statut = StatutTransaction.CONFIRMEE;
        this.paiementId = paiementId;
        this.referenceOperateur = referenceOperateur;
        this.confirmeeLe = quand;
        this.erreur = null;
    }

    public void terminer(StatutTransaction statut, String erreur) {
        this.statut = statut;
        this.erreur = erreur;
    }

    public void noterVerification(OffsetDateTime quand) {
        this.verifications++;
        this.derniereVerification = quand;
    }

    public UUID getBailId() { return bailId; }
    public UUID getLocataireId() { return locataireId; }
    public String getFournisseur() { return fournisseur; }
    public OperateurMobile getOperateur() { return operateur; }
    public String getTelephone() { return telephone; }
    public long getMontant() { return montant; }
    public StatutTransaction getStatut() { return statut; }
    public String getReferenceFournisseur() { return referenceFournisseur; }
    public String getReferenceOperateur() { return referenceOperateur; }
    public String getCleIdempotence() { return cleIdempotence; }
    public String getEmpreinte() { return empreinte; }
    public UUID getPaiementId() { return paiementId; }
    public String getErreur() { return erreur; }
    public int getVerifications() { return verifications; }
    public OffsetDateTime getDerniereVerification() { return derniereVerification; }
    public OffsetDateTime getConfirmeeLe() { return confirmeeLe; }
}
