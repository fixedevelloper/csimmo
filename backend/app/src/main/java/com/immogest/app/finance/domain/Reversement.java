package com.immogest.app.finance.domain;

import com.immogest.app.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Reversement mensuel à un propriétaire tiers : encaissé − honoraires − dépenses. CALCULE (recalculable) → VALIDE (figé, relevé PDF
 * numéroté) → PAYE. Le trigger SQL empêche toute modification d'un reversement validé.
 */
@Entity
@Table(name = "fin_reversement")
public class Reversement extends BaseEntity {

    public static final String CALCULE = "CALCULE";
    public static final String VALIDE = "VALIDE";
    public static final String PAYE = "PAYE";

    @Column(name = "proprietaire_id", nullable = false, updatable = false)
    private UUID proprietaireId;
    @Column(nullable = false, updatable = false)
    private LocalDate periode;
    @Column(nullable = false)
    private String statut = CALCULE;
    private String numero;
    @Column(name = "loyers_encaisses", nullable = false)
    private long loyersEncaisses;
    @Column(name = "charges_encaissees", nullable = false)
    private long chargesEncaissees;
    @Column(nullable = false)
    private long brut;
    @Column(nullable = false)
    private long honoraires;
    @Column(name = "taux_moyen_bp")
    private Integer tauxMoyenBp;
    @Column(nullable = false)
    private long depenses;
    @Column(nullable = false)
    private long net;
    @Column(name = "sans_mandat", nullable = false)
    private boolean sansMandat;
    @Column(name = "fichier_id")
    private UUID fichierId;
    @Column(name = "calcule_le", nullable = false)
    private OffsetDateTime calculeLe;
    @Column(name = "valide_le")
    private OffsetDateTime valideLe;
    @Column(name = "valide_par")
    private UUID validePar;
    @Column(name = "paye_le")
    private LocalDate payeLe;
    @Column(name = "mode_paiement")
    private String modePaiement;
    @Column(name = "reference_paiement")
    private String referencePaiement;

    protected Reversement() {
    }

    public Reversement(UUID proprietaireId, LocalDate periode) {
        this.proprietaireId = proprietaireId;
        this.periode = periode;
    }

    public void recalculer(long loyers, long charges, long honoraires, Integer tauxMoyenBp, long depenses, boolean sansMandat, OffsetDateTime quand) {
        this.loyersEncaisses = loyers;
        this.chargesEncaissees = charges;
        this.brut = loyers + charges;
        this.honoraires = honoraires;
        this.tauxMoyenBp = tauxMoyenBp;
        this.depenses = depenses;
        this.net = brut - honoraires - depenses;
        this.sansMandat = sansMandat;
        this.calculeLe = quand;
    }

    public void valider(String numero, UUID fichierId, UUID par, OffsetDateTime quand) {
        this.statut = VALIDE;
        this.numero = numero;
        this.fichierId = fichierId;
        this.validePar = par;
        this.valideLe = quand;
    }

    public void payer(String mode, String reference, LocalDate date) {
        this.statut = PAYE;
        this.modePaiement = mode;
        this.referencePaiement = reference;
        this.payeLe = date;
    }

    public UUID getProprietaireId() { return proprietaireId; }
    public LocalDate getPeriode() { return periode; }
    public String getStatut() { return statut; }
    public String getNumero() { return numero; }
    public long getLoyersEncaisses() { return loyersEncaisses; }
    public long getChargesEncaissees() { return chargesEncaissees; }
    public long getBrut() { return brut; }
    public long getHonoraires() { return honoraires; }
    public Integer getTauxMoyenBp() { return tauxMoyenBp; }
    public long getDepenses() { return depenses; }
    public long getNet() { return net; }
    public boolean isSansMandat() { return sansMandat; }
    public UUID getFichierId() { return fichierId; }
    public OffsetDateTime getCalculeLe() { return calculeLe; }
    public OffsetDateTime getValideLe() { return valideLe; }
    public LocalDate getPayeLe() { return payeLe; }
    public String getModePaiement() { return modePaiement; }
    public String getReferencePaiement() { return referencePaiement; }
}
