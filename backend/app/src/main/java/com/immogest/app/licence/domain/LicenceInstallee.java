package com.immogest.app.licence.domain;

import com.immogest.app.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

/** Licence importée. L'historique est conservé : une nouvelle licence désactive l'ancienne, rien n'est supprimé. */
@Entity
@Table(name = "lic_licence")
public class LicenceInstallee extends BaseEntity {

    @Column(name = "license_id", nullable = false, updatable = false)
    private String licenseId;

    @Column(nullable = false, updatable = false, columnDefinition = "TEXT")
    private String contenu;

    @Column(name = "importee_le", nullable = false, updatable = false)
    private OffsetDateTime importeeLe;

    @Column(nullable = false)
    private boolean actuelle;

    @Column(name = "derniere_verification_en_ligne", nullable = false)
    private OffsetDateTime derniereVerificationEnLigne;

    @Column(name = "derniere_tentative_en_ligne")
    private OffsetDateTime derniereTentativeEnLigne;

    @Column(name = "derniere_tentative_reussie")
    private Boolean derniereTentativeReussie;

    @Column(nullable = false)
    private boolean revoquee;

    @Column(name = "seuil_alerte_envoye")
    private Integer seuilAlerteEnvoye;

    @Column(name = "expiration_notifiee", nullable = false)
    private boolean expirationNotifiee;

    protected LicenceInstallee() {
    }

    public LicenceInstallee(String licenseId, String contenu, OffsetDateTime importeeLe) {
        this.licenseId = licenseId;
        this.contenu = contenu;
        this.importeeLe = importeeLe;
        this.actuelle = true;
        this.derniereVerificationEnLigne = importeeLe;
    }

    public void remplacer() {
        this.actuelle = false;
    }

    /** Résultat d'une tentative de vérification en ligne. */
    public void enregistrerTentative(OffsetDateTime maintenant, boolean reussie) {
        this.derniereTentativeEnLigne = maintenant;
        this.derniereTentativeReussie = reussie;
    }

    public void confirmerEnLigne(OffsetDateTime maintenant) {
        this.derniereVerificationEnLigne = maintenant;
        this.revoquee = false;
    }

    public void revoquer() {
        this.revoquee = true;
    }

    public void alerteEnvoyee(int seuil) {
        this.seuilAlerteEnvoye = seuil;
    }

    public void expirationNotifiee() {
        this.expirationNotifiee = true;
    }

    public String getLicenseId() {
        return licenseId;
    }

    public String getContenu() {
        return contenu;
    }

    public OffsetDateTime getImporteeLe() {
        return importeeLe;
    }

    public boolean isActuelle() {
        return actuelle;
    }

    public OffsetDateTime getDerniereVerificationEnLigne() {
        return derniereVerificationEnLigne;
    }

    public OffsetDateTime getDerniereTentativeEnLigne() {
        return derniereTentativeEnLigne;
    }

    public Boolean getDerniereTentativeReussie() {
        return derniereTentativeReussie;
    }

    public boolean isRevoquee() {
        return revoquee;
    }

    public Integer getSeuilAlerteEnvoye() {
        return seuilAlerteEnvoye;
    }

    public boolean isExpirationNotifiee() {
        return expirationNotifiee;
    }
}
