package com.immogest.app.patrimoine.domain;

import com.immogest.app.patrimoine.TypeCompteur;
import com.immogest.app.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "compteur")
public class Compteur extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeCompteur type;

    @Column(nullable = false)
    private String numero;

    protected Compteur() {
    }

    public Compteur(TypeCompteur type, String numero) {
        this.type = type;
        this.numero = numero;
    }

    public TypeCompteur getType() {
        return type;
    }

    public String getNumero() {
        return numero;
    }
}
