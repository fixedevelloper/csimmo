package com.immogest.app.relance.domain;

import com.immogest.app.relance.ResultatRelance;
import com.immogest.app.relance.TypeRelance;
import com.immogest.app.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;

/** Une ligne de l'historique des relances d'une échéance (jamais supprimée). */
@Entity
@Table(name = "rel_relance")
public class Relance extends BaseEntity {

    @Column(name = "echeance_id", nullable = false, updatable = false)
    private UUID echeanceId;
    @Column(name = "bail_id", nullable = false, updatable = false)
    private UUID bailId;
    @Column(name = "locataire_id", nullable = false, updatable = false)
    private UUID locataireId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private TypeRelance type;
    @Column(updatable = false)
    private Integer niveau;
    @Column(name = "date_relance", nullable = false, updatable = false)
    private LocalDate dateRelance;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResultatRelance resultat;
    @Column(name = "message_id")
    private UUID messageId;
    @Column(name = "reste_du", nullable = false, updatable = false)
    private long resteDu;
    @Column(name = "jours_retard", nullable = false, updatable = false)
    private int joursRetard;
    @Column(updatable = false)
    private UUID auteur;

    protected Relance() {
    }

    public Relance(UUID echeanceId, UUID bailId, UUID locataireId, TypeRelance type, Integer niveau, LocalDate date, long resteDu,
            int joursRetard, UUID auteur) {
        this.echeanceId = echeanceId;
        this.bailId = bailId;
        this.locataireId = locataireId;
        this.type = type;
        this.niveau = niveau;
        this.dateRelance = date;
        this.resteDu = resteDu;
        this.joursRetard = joursRetard;
        this.auteur = auteur;
        this.resultat = ResultatRelance.AUCUN_CANAL;
    }

    public void resultat(ResultatRelance resultat, UUID messageId) {
        this.resultat = resultat;
        this.messageId = messageId;
    }

    public UUID getEcheanceId() { return echeanceId; }
    public UUID getBailId() { return bailId; }
    public UUID getLocataireId() { return locataireId; }
    public TypeRelance getType() { return type; }
    public Integer getNiveau() { return niveau; }
    public LocalDate getDateRelance() { return dateRelance; }
    public ResultatRelance getResultat() { return resultat; }
    public UUID getMessageId() { return messageId; }
    public long getResteDu() { return resteDu; }
    public int getJoursRetard() { return joursRetard; }
}
