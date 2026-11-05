package com.immogest.app.finance.infra;

import com.immogest.app.finance.domain.Paiement;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaiementRepository extends JpaRepository<Paiement, UUID>, JpaSpecificationExecutor<Paiement> {

    Optional<Paiement> findByBailIdAndType(UUID bailId, String type);

    Optional<Paiement> findByCleIdempotence(String cle);

    /** Paiements (et avance) non contre-passés qui peuvent encore imputer un crédit, du plus ancien au plus récent. */
    @Query("select p from Paiement p where p.bailId = :bail and p.type in ('AVANCE', 'ENCAISSEMENT') "
            + "and not exists (select 1 from Paiement r where r.contrePassationDe = p.id) order by p.datePaiement, p.createdAt")
    List<Paiement> creditables(@Param("bail") UUID bail);

    /** Contre-passations des paiements donnés (pour marquer ceux déjà annulés). */
    @Query("select p.contrePassationDe from Paiement p where p.contrePassationDe in :ids")
    List<UUID> dejaContrePasses(@Param("ids") Collection<UUID> ids);

    boolean existsByContrePassationDe(UUID id);
}
