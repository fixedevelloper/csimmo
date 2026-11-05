package com.immogest.app.securite.domain;

import com.immogest.app.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

/** Jeton de rafraîchissement opaque ; seul son hash SHA-256 est stocké. */
@Entity
@Table(name = "sec_refresh_token")
public class RefreshToken extends BaseEntity {

    @Column(name = "utilisateur_id", nullable = false, updatable = false)
    private UUID utilisateurId;

    @Column(name = "token_hash", nullable = false, updatable = false)
    private String tokenHash;

    @Column(name = "expire_le", nullable = false, updatable = false)
    private OffsetDateTime expireLe;

    @Column(name = "revoque_le")
    private OffsetDateTime revoqueLe;

    protected RefreshToken() {
    }

    public RefreshToken(UUID utilisateurId, String tokenHash, OffsetDateTime expireLe) {
        this.utilisateurId = utilisateurId;
        this.tokenHash = tokenHash;
        this.expireLe = expireLe;
    }

    public boolean estRevoque() {
        return revoqueLe != null;
    }

    public boolean estExpire(OffsetDateTime maintenant) {
        return !maintenant.isBefore(expireLe);
    }

    public void revoquer(OffsetDateTime maintenant) {
        if (revoqueLe == null) {
            revoqueLe = maintenant;
        }
    }

    public UUID getUtilisateurId() {
        return utilisateurId;
    }

    public OffsetDateTime getExpireLe() {
        return expireLe;
    }
}
