package com.immogest.app.relance.infra;

import com.immogest.app.relance.domain.MiseEnDemeure;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MiseEnDemeureRepository extends JpaRepository<MiseEnDemeure, UUID> {

    Optional<MiseEnDemeure> findByEcheanceId(UUID echeanceId);

    List<MiseEnDemeure> findByEcheanceIdIn(Collection<UUID> ids);

    @Query("select m from MiseEnDemeure m where (:bail is null or m.bailId = :bail)")
    Page<MiseEnDemeure> lister(@Param("bail") UUID bail, Pageable pageable);

    @Query("select m from MiseEnDemeure m where m.bailId in :bails")
    Page<MiseEnDemeure> listerDesBaux(@Param("bails") Collection<UUID> bails, Pageable pageable);
}
