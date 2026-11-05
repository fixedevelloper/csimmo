package com.immogest.app.notification.domain;

import com.immogest.app.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

/** Appareil mobile enregistré pour les notifications push. */
@Entity
@Table(name = "notif_appareil")
public class Appareil extends BaseEntity {

    @Column(name = "utilisateur_id", nullable = false)
    private UUID utilisateurId;
    @Column(nullable = false, updatable = false)
    private String token;
    @Column(nullable = false)
    private String plateforme;

    protected Appareil() {
    }

    public Appareil(UUID utilisateurId, String token, String plateforme) {
        this.utilisateurId = utilisateurId;
        this.token = token;
        this.plateforme = plateforme;
    }

    public void rattacher(UUID utilisateurId, String plateforme) {
        this.utilisateurId = utilisateurId;
        this.plateforme = plateforme;
    }

    public UUID getUtilisateurId() { return utilisateurId; }
    public String getToken() { return token; }
    public String getPlateforme() { return plateforme; }
}
