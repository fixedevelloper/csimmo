package com.immogest.app.notification.domain;

import com.immogest.app.notification.StatutMessage;
import com.immogest.app.notification.TypeCanal;
import com.immogest.app.notification.TypeMessage;
import com.immogest.app.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

/** Message de la file d'envoi, conservé ensuite comme ligne du journal. */
@Entity
@Table(name = "notif_message")
public class Message extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private TypeCanal canal;
    @Enumerated(EnumType.STRING)
    @Column(name = "type_message", nullable = false, updatable = false)
    private TypeMessage typeMessage;
    @Column(nullable = false, updatable = false)
    private String destinataire;
    @Column(updatable = false)
    private String sujet;
    @Column(nullable = false, updatable = false, columnDefinition = "TEXT")
    private String corps;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutMessage statut = StatutMessage.EN_ATTENTE;
    @Column(nullable = false)
    private int tentatives;
    @Column(name = "prochaine_tentative", nullable = false)
    private OffsetDateTime prochaineTentative;
    @Column(name = "derniere_erreur", columnDefinition = "TEXT")
    private String derniereErreur;
    @Column(name = "envoye_le")
    private OffsetDateTime envoyeLe;
    @Column(name = "locataire_id", updatable = false)
    private UUID locataireId;
    @Column(name = "reference_id", updatable = false)
    private UUID referenceId;
    @Column(name = "cle_dedoublonnage", nullable = false, updatable = false)
    private String cleDedoublonnage;

    protected Message() {
    }

    public Message(TypeCanal canal, TypeMessage type, String destinataire, String sujet, String corps, UUID locataireId, UUID referenceId,
            String cle, OffsetDateTime maintenant) {
        this.canal = canal;
        this.typeMessage = type;
        this.destinataire = destinataire;
        this.sujet = sujet;
        this.corps = corps;
        this.locataireId = locataireId;
        this.referenceId = referenceId;
        this.cleDedoublonnage = cle;
        this.prochaineTentative = maintenant;
    }

    /** Message qu'on sait ne pas pouvoir envoyer (canal inactif, contact manquant) : visible dans le journal plutôt que perdu. */
    public void echouerDirectement(String erreur) {
        this.statut = StatutMessage.ECHEC;
        this.derniereErreur = erreur;
    }

    public void marquerEnvoye(OffsetDateTime quand) {
        tentatives++;
        statut = StatutMessage.ENVOYE;
        envoyeLe = quand;
        derniereErreur = null;
    }

    /** @param prochaine date du prochain essai, ou {@code null} si les essais sont épuisés */
    public void marquerEchec(String erreur, OffsetDateTime prochaine) {
        tentatives++;
        derniereErreur = erreur;
        if (prochaine == null) {
            statut = StatutMessage.ECHEC;
        } else {
            prochaineTentative = prochaine;
        }
    }

    public void relancer(OffsetDateTime maintenant) {
        statut = StatutMessage.EN_ATTENTE;
        tentatives = 0;
        prochaineTentative = maintenant;
    }

    public TypeCanal getCanal() { return canal; }
    public TypeMessage getTypeMessage() { return typeMessage; }
    public String getDestinataire() { return destinataire; }
    public String getSujet() { return sujet; }
    public String getCorps() { return corps; }
    public StatutMessage getStatut() { return statut; }
    public int getTentatives() { return tentatives; }
    public OffsetDateTime getProchaineTentative() { return prochaineTentative; }
    public String getDerniereErreur() { return derniereErreur; }
    public OffsetDateTime getEnvoyeLe() { return envoyeLe; }
    public UUID getLocataireId() { return locataireId; }
    public UUID getReferenceId() { return referenceId; }
}
