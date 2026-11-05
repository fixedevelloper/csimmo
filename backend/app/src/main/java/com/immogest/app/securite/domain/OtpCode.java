package com.immogest.app.securite.domain;

import com.immogest.app.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

/** Code à usage unique ; seul son hash est stocké. */
@Entity
@Table(name = "sec_otp_code")
public class OtpCode extends BaseEntity {

    @Column(name = "utilisateur_id", nullable = false, updatable = false)
    private UUID utilisateurId;

    @Column(name = "code_hash", nullable = false, updatable = false)
    private String codeHash;

    @Column(name = "expire_le", nullable = false, updatable = false)
    private OffsetDateTime expireLe;

    @Column(nullable = false)
    private int tentatives;

    @Column(nullable = false)
    private boolean utilise;

    protected OtpCode() {
    }

    public OtpCode(UUID utilisateurId, String codeHash, OffsetDateTime expireLe) {
        this.utilisateurId = utilisateurId;
        this.codeHash = codeHash;
        this.expireLe = expireLe;
    }

    public boolean estValide(OffsetDateTime maintenant, int tentativesMax) {
        return !utilise && tentatives < tentativesMax && maintenant.isBefore(expireLe);
    }

    public void compterTentative() {
        tentatives++;
    }

    public void consommer() {
        utilise = true;
    }

    public String getCodeHash() {
        return codeHash;
    }

    public UUID getUtilisateurId() {
        return utilisateurId;
    }

    public OffsetDateTime getExpireLe() {
        return expireLe;
    }

    public int getTentatives() {
        return tentatives;
    }

    public boolean isUtilise() {
        return utilise;
    }

    public void setExpireLe(OffsetDateTime expireLe) {
        this.expireLe = expireLe;
    }
}
