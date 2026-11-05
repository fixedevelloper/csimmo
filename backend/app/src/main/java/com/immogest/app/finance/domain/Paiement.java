package com.immogest.app.finance.domain;

import com.immogest.app.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;

/** Encaissement. À ce stade (4.1), seulement l'avance versée à la signature ({@code type = AVANCE}). */
@Entity
@Table(name = "fin_paiement")
public class Paiement extends BaseEntity {

    public static final String AVANCE = "AVANCE";
    public static final String ENCAISSEMENT = "ENCAISSEMENT";
    public static final String CONTRE_PASSATION = "CONTRE_PASSATION";

    @Column(name = "bail_id", nullable = false, updatable = false)
    private UUID bailId;
    @Column(nullable = false, updatable = false)
    private String type;
    @Column(nullable = false, updatable = false)
    private long montant;
    @Column(name = "date_paiement", nullable = false, updatable = false)
    private LocalDate datePaiement;

    @Column(updatable = false)
    private String mode;
    @Column(updatable = false)
    private String reference;
    @Column(name = "saisi_par", updatable = false)
    private UUID saisiPar;
    @Column(name = "cle_idempotence", updatable = false)
    private String cleIdempotence;
    @Column(updatable = false)
    private String empreinte;
    @Column(updatable = false)
    private String motif;
    @Column(name = "contre_passation_de", updatable = false)
    private UUID contrePassationDe;

    protected Paiement() {
    }

    public static Paiement encaissement(UUID bailId, long montant, LocalDate date, String mode, String reference, UUID saisiPar,
            String cleIdempotence, String empreinte) {
        Paiement p = new Paiement(bailId, ENCAISSEMENT, montant, date);
        p.mode = mode;
        p.reference = reference;
        p.saisiPar = saisiPar;
        p.cleIdempotence = cleIdempotence;
        p.empreinte = empreinte;
        return p;
    }

    /** Écriture inverse d'un encaissement : même montant et même mode, motif obligatoire. */
    public static Paiement contrePassation(Paiement origine, LocalDate date, String motif, UUID saisiPar) {
        Paiement p = new Paiement(origine.bailId, CONTRE_PASSATION, origine.montant, date);
        p.mode = origine.mode;
        p.reference = origine.reference;
        p.saisiPar = saisiPar;
        p.motif = motif;
        p.contrePassationDe = origine.getId();
        return p;
    }

    public Paiement(UUID bailId, String type, long montant, LocalDate datePaiement) {
        this.bailId = bailId;
        this.type = type;
        this.montant = montant;
        this.datePaiement = datePaiement;
    }

    public UUID getBailId() { return bailId; }
    public String getType() { return type; }
    public long getMontant() { return montant; }
    public LocalDate getDatePaiement() { return datePaiement; }
    public String getMode() { return mode; }
    public String getReference() { return reference; }
    public UUID getSaisiPar() { return saisiPar; }
    public String getCleIdempotence() { return cleIdempotence; }
    public String getEmpreinte() { return empreinte; }
    public String getMotif() { return motif; }
    public UUID getContrePassationDe() { return contrePassationDe; }
}
