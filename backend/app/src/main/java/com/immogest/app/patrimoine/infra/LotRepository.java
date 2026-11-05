package com.immogest.app.patrimoine.infra;

import com.immogest.app.patrimoine.StatutLot;
import com.immogest.app.patrimoine.domain.Lot;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LotRepository extends JpaRepository<Lot, UUID>, JpaSpecificationExecutor<Lot> {

    interface CompteStatut {
        UUID getImmeubleId();

        StatutLot getStatut();

        long getNombre();
    }

    long countByArchiveFalse();

    @org.springframework.data.jpa.repository.Query("select l.statut, count(l) from Lot l where l.archive = false group by l.statut")
    java.util.List<Object[]> compterParStatut();

    long countByImmeubleIdAndArchiveFalse(UUID immeubleId);

    boolean existsByImmeubleIdAndReference(UUID immeubleId, String reference);

    boolean existsByImmeubleIdAndReferenceAndIdNot(UUID immeubleId, String reference, UUID id);

    @Query("select l.immeuble.id as immeubleId, l.statut as statut, count(l) as nombre from Lot l "
            + "where l.immeuble.id in :ids and l.archive = false group by l.immeuble.id, l.statut")
    List<CompteStatut> compterParImmeuble(@Param("ids") Collection<UUID> ids);

    /**
     * Verrou transactionnel qui sérialise la création de lots : sans lui, deux créations simultanées peuvent
     * lire le même nombre de lots et dépasser le palier de la licence.
     */
    @Query(value = "select pg_advisory_xact_lock(7101)", nativeQuery = true)
    void verrouillerCreation();
}
