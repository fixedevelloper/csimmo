package com.immogest.app.patrimoine.domain;

import com.immogest.app.patrimoine.StatutLot;
import com.immogest.app.patrimoine.TypeLot;
import com.immogest.app.shared.BaseEntity;
import com.immogest.app.shared.ErreurMetier;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.http.HttpStatus;

@Entity
@Table(name = "lot")
public class Lot extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "immeuble_id", nullable = false, updatable = false)
    private Immeuble immeuble;

    @Column(nullable = false)
    private String reference;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeLot type;

    @Column(name = "surface_m2")
    private BigDecimal surfaceM2;

    private Integer pieces;

    @Column(name = "loyer_indicatif")
    private Long loyerIndicatif;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutLot statut = StatutLot.LIBRE;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "lot_equipement", joinColumns = @JoinColumn(name = "lot_id"))
    @Column(name = "libelle", nullable = false)
    private Set<String> equipements = new LinkedHashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "lot_id", nullable = false)
    private List<Compteur> compteurs = new ArrayList<>();

    @Column(nullable = false)
    private boolean archive;

    protected Lot() {
    }

    public Lot(Immeuble immeuble, String reference, TypeLot type, BigDecimal surfaceM2, Integer pieces,
            Long loyerIndicatif) {
        this.immeuble = immeuble;
        modifier(reference, type, surfaceM2, pieces, loyerIndicatif);
    }

    public void modifier(String reference, TypeLot type, BigDecimal surfaceM2, Integer pieces, Long loyerIndicatif) {
        this.reference = reference;
        this.type = type;
        this.surfaceM2 = surfaceM2;
        this.pieces = pieces;
        this.loyerIndicatif = loyerIndicatif;
    }

    public void remplacerEquipements(Set<String> nouveaux) {
        equipements.retainAll(nouveaux);
        equipements.addAll(nouveaux);
    }

    public void remplacerCompteurs(List<Compteur> nouveaux) {
        compteurs.clear();
        compteurs.addAll(nouveaux);
    }

    /** Changement manuel : seulement LIBRE ↔ EN_TRAVAUX, et jamais sur un lot occupé. */
    public void changerStatutManuellement(StatutLot cible) {
        if (statut == StatutLot.OCCUPE) {
            throw new ErreurMetier("LOT_OCCUPE_STATUT_NON_MODIFIABLE",
                    "Le statut d'un lot occupé change avec son bail", HttpStatus.CONFLICT);
        }
        if (cible == StatutLot.OCCUPE) {
            throw new ErreurMetier("LOT_STATUT_OCCUPE_PAR_BAIL",
                    "Un lot devient occupé quand un bail y est activé", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        this.statut = cible;
    }

    /** Statut posé par l'événement BailActive ; renvoie vrai si le statut a changé. */
    public boolean marquerOccupe() {
        if (statut == StatutLot.OCCUPE) {
            return false;
        }
        statut = StatutLot.OCCUPE;
        return true;
    }

    /** Statut posé par l'événement BailTermine ; renvoie vrai si le statut a changé. */
    public boolean marquerLibre() {
        if (statut != StatutLot.OCCUPE) {
            return false;
        }
        statut = StatutLot.LIBRE;
        return true;
    }

    public void archiver() {
        if (statut == StatutLot.OCCUPE) {
            throw new ErreurMetier("LOT_OCCUPE_ARCHIVAGE_REFUSE", "Un lot occupé ne peut pas être archivé",
                    HttpStatus.CONFLICT);
        }
        this.archive = true;
    }

    public Immeuble getImmeuble() {
        return immeuble;
    }

    public String getReference() {
        return reference;
    }

    public TypeLot getType() {
        return type;
    }

    public BigDecimal getSurfaceM2() {
        return surfaceM2;
    }

    public Integer getPieces() {
        return pieces;
    }

    public Long getLoyerIndicatif() {
        return loyerIndicatif;
    }

    public StatutLot getStatut() {
        return statut;
    }

    public Set<String> getEquipements() {
        return equipements;
    }

    public List<Compteur> getCompteurs() {
        return compteurs;
    }

    public boolean isArchive() {
        return archive;
    }
}
