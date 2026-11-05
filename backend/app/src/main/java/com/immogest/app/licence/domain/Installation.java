package com.immogest.app.licence.domain;

import com.immogest.app.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

/** Ligne unique : identifiant d'installation (généré au premier démarrage) et état de l'assistant. */
@Entity
@Table(name = "lic_installation")
public class Installation extends BaseEntity {

    @Column(nullable = false, updatable = false)
    private boolean singleton = true;

    @Column(name = "identifiant_installation", nullable = false, updatable = false)
    private String identifiantInstallation;

    @Column(nullable = false)
    private boolean installee;

    @Column(name = "installee_le")
    private OffsetDateTime installeeLe;

    protected Installation() {
    }

    public Installation(String identifiantInstallation) {
        this.identifiantInstallation = identifiantInstallation;
    }

    public void marquerInstallee(OffsetDateTime maintenant) {
        this.installee = true;
        this.installeeLe = maintenant;
    }

    public String getIdentifiantInstallation() {
        return identifiantInstallation;
    }

    public boolean isInstallee() {
        return installee;
    }

    public OffsetDateTime getInstalleeLe() {
        return installeeLe;
    }

    /** Test uniquement : remet l'assistant à zéro. */
    public void reinitialiserPourTest() {
        this.installee = false;
        this.installeeLe = null;
    }
}
