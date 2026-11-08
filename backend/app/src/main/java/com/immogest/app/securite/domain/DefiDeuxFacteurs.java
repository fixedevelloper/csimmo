package com.immogest.app.securite.domain;

import com.immogest.app.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

/** Défi de double authentification (voir la migration V26) : seul le hash du jeton est stocké. */
@Entity
@Table(name = "sec_defi_2fa")
public class DefiDeuxFacteurs extends BaseEntity {

    public enum Type { VERIFICATION, ENROLEMENT, PROFIL }

    @Column(name = "utilisateur_id", nullable = false, updatable = false)
    private UUID utilisateurId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private Type type;

    @Column(name = "token_hash", nullable = false, updatable = false)
    private String tokenHash;

    @Column(name = "expire_le", nullable = false)
    private OffsetDateTime expireLe;

    @Column(nullable = false)
    private int tentatives;

    @Column(nullable = false)
    private boolean utilise;

    /** Secret généré mais pas encore confirmé par un premier code (chiffré). */
    @Column(name = "secret_en_attente_chiffre")
    private String secretEnAttenteChiffre;

    protected DefiDeuxFacteurs() {
    }

    public DefiDeuxFacteurs(UUID utilisateurId, Type type, String tokenHash, OffsetDateTime expireLe) {
        this.utilisateurId = utilisateurId;
        this.type = type;
        this.tokenHash = tokenHash;
        this.expireLe = expireLe;
    }

    public boolean estValide(OffsetDateTime maintenant, int tentativesMax) {
        return !utilise && maintenant.isBefore(expireLe) && tentatives < tentativesMax;
    }

    public void compterTentative() {
        tentatives++;
    }

    public void consommer() {
        utilise = true;
    }

    public void definirSecretEnAttente(String secretChiffre) {
        this.secretEnAttenteChiffre = secretChiffre;
    }

    public UUID getUtilisateurId() {
        return utilisateurId;
    }

    public Type getType() {
        return type;
    }

    public String getSecretEnAttenteChiffre() {
        return secretEnAttenteChiffre;
    }

    public int getTentatives() {
        return tentatives;
    }

    public boolean isUtilise() {
        return utilise;
    }

    public OffsetDateTime getExpireLe() {
        return expireLe;
    }
}
