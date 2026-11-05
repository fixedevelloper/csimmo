package com.immogest.app.tiers.infra;

import com.immogest.app.tiers.domain.Mandat;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MandatRepository extends JpaRepository<Mandat, UUID> {

    java.util.List<Mandat> findByProprietaireIdOrderByDateDebutDesc(UUID proprietaireId);

    /** Mandats du propriétaire qui chevauchent [debut, fin] (fin = 9999-12-31 si le mandat est sans limite). */
    @org.springframework.data.jpa.repository.Query("select count(m) > 0 from Mandat m where m.proprietaireId = :proprietaire "
            + "and m.dateDebut <= :fin and (m.dateFin is null or m.dateFin >= :debut) and (:exclu is null or m.id <> :exclu)")
    boolean chevauche(@org.springframework.data.repository.query.Param("proprietaire") UUID proprietaire,
            @org.springframework.data.repository.query.Param("debut") java.time.LocalDate debut,
            @org.springframework.data.repository.query.Param("fin") java.time.LocalDate fin,
            @org.springframework.data.repository.query.Param("exclu") UUID exclu);
}
