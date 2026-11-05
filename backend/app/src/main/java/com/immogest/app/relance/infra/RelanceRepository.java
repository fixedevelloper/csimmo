package com.immogest.app.relance.infra;

import com.immogest.app.relance.domain.Relance;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RelanceRepository extends JpaRepository<Relance, UUID> {

    @Query("select r.niveau from Relance r where r.echeanceId = :echeance and r.type = com.immogest.app.relance.TypeRelance.AUTO")
    List<Integer> niveauxAuto(@Param("echeance") UUID echeance);

    /** Dernière relance réellement tentée (les niveaux sautés ne comptent pas). */
    Optional<Relance> findFirstByEcheanceIdAndResultatNotOrderByCreatedAtDesc(UUID echeanceId, com.immogest.app.relance.ResultatRelance exclu);

    @Query("select r from Relance r where (:echeance is null or r.echeanceId = :echeance) and (:bail is null or r.bailId = :bail)")
    Page<Relance> historique(@Param("echeance") UUID echeance, @Param("bail") UUID bail, Pageable pageable);

    List<Relance> findByEcheanceIdIn(Collection<UUID> ids);
}
