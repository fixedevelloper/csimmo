package com.immogest.app.bail.domain;

import com.immogest.app.bail.Periodicite;
import com.immogest.app.bail.StatutBail;
import com.immogest.app.shared.BaseEntity;
import com.immogest.app.shared.ErreurMetier;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import org.springframework.http.HttpStatus;

@Entity
@Table(name = "bail")
public class Bail extends BaseEntity {

    @Column(nullable = false, updatable = false)
    private String numero;

    @Column(name = "lot_id", nullable = false)
    private UUID lotId;

    @Column(name = "locataire_id", nullable = false)
    private UUID locataireId;

    @Column(name = "bail_precedent_id", updatable = false)
    private UUID bailPrecedentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutBail statut = StatutBail.BROUILLON;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    @Column(nullable = false)
    private long loyer;

    @Column(nullable = false)
    private long charges;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Periodicite periodicite;

    @Column(name = "jour_echeance", nullable = false)
    private int jourEcheance;

    @Column(nullable = false)
    private long caution;

    @Column(name = "mois_avance", nullable = false)
    private int moisAvance;

    @Column(name = "preavis_mois", nullable = false)
    private int preavisMois = 3;

    @Column(name = "date_notification_preavis")
    private LocalDate dateNotificationPreavis;

    @Column(name = "date_sortie")
    private LocalDate dateSortie;

    @Column(name = "motif_resiliation")
    private String motifResiliation;

    @Column(name = "date_fin_effective")
    private LocalDate dateFinEffective;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "bail_garant", joinColumns = @JoinColumn(name = "bail_id"))
    @Column(name = "garant_id", nullable = false)
    private Set<UUID> garantIds = new LinkedHashSet<>();

    protected Bail() {
    }

    public Bail(String numero, UUID bailPrecedentId) {
        this.numero = numero;
        this.bailPrecedentId = bailPrecedentId;
    }

    /** Conditions du bail ; seulement tant qu'il est en brouillon. */
    public void definir(UUID lotId, UUID locataireId, Set<UUID> garants, LocalDate debut, LocalDate fin, long loyer, long charges,
            Periodicite periodicite, int jourEcheance, long caution, int moisAvance, int preavisMois) {
        exigerStatut(StatutBail.BROUILLON, "BAIL_NON_MODIFIABLE", "Seul un bail en brouillon peut être modifié");
        this.lotId = lotId;
        this.locataireId = locataireId;
        this.garantIds.retainAll(garants);
        this.garantIds.addAll(garants);
        this.dateDebut = debut;
        this.dateFin = fin;
        this.loyer = loyer;
        this.charges = charges;
        this.periodicite = periodicite;
        this.jourEcheance = jourEcheance;
        this.caution = caution;
        this.moisAvance = moisAvance;
        this.preavisMois = preavisMois;
        recalculerFinEffective();
    }

    public void activer() {
        exigerStatut(StatutBail.BROUILLON, "BAIL_NON_ACTIVABLE", "Seul un bail en brouillon peut être activé");
        statut = StatutBail.ACTIF;
    }

    public void resilier(LocalDate notification, LocalDate sortie, String motif) {
        exigerStatut(StatutBail.ACTIF, "BAIL_NON_RESILIABLE", "Seul un bail actif peut être résilié");
        this.statut = StatutBail.RESILIE;
        this.dateNotificationPreavis = notification;
        this.dateSortie = sortie;
        this.motifResiliation = motif;
        recalculerFinEffective();
    }

    public void terminer() {
        if (!statut.occupeLeLot()) {
            throw new ErreurMetier("BAIL_NON_TERMINABLE", "Seul un bail actif ou résilié peut se terminer", HttpStatus.CONFLICT);
        }
        statut = StatutBail.TERMINE;
    }

    private void recalculerFinEffective() {
        this.dateFinEffective = dateSortie != null ? dateSortie : dateFin;
    }

    private void exigerStatut(StatutBail attendu, String code, String message) {
        if (statut != attendu) {
            throw new ErreurMetier(code, message, HttpStatus.CONFLICT);
        }
    }

    public String getNumero() {
        return numero;
    }

    public UUID getLotId() {
        return lotId;
    }

    public UUID getLocataireId() {
        return locataireId;
    }

    public UUID getBailPrecedentId() {
        return bailPrecedentId;
    }

    public StatutBail getStatut() {
        return statut;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public long getLoyer() {
        return loyer;
    }

    public long getCharges() {
        return charges;
    }

    public Periodicite getPeriodicite() {
        return periodicite;
    }

    public int getJourEcheance() {
        return jourEcheance;
    }

    public long getCaution() {
        return caution;
    }

    public int getMoisAvance() {
        return moisAvance;
    }

    public int getPreavisMois() {
        return preavisMois;
    }

    public LocalDate getDateNotificationPreavis() {
        return dateNotificationPreavis;
    }

    public LocalDate getDateSortie() {
        return dateSortie;
    }

    public String getMotifResiliation() {
        return motifResiliation;
    }

    public LocalDate getDateFinEffective() {
        return dateFinEffective;
    }

    public Set<UUID> getGarantIds() {
        return garantIds;
    }
}
