package com.immogest.app.finance.infra;

import com.immogest.app.finance.domain.Imputation;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ImputationRepository extends JpaRepository<Imputation, UUID> {

    java.util.List<Imputation> findByPaiementId(UUID paiementId);

    /** Total net imputé à une échéance, par paiement (les paiements contre-passés ont un net nul et disparaissent). */
    @Query("select i.paiementId, sum(i.montant) from Imputation i where i.echeanceId = :echeance group by i.paiementId having sum(i.montant) > 0")
    java.util.List<Object[]> netParPaiement(@Param("echeance") UUID echeance);

    @Query("select coalesce(sum(i.montant), 0) from Imputation i where i.paiementId = :paiement")
    long totalImpute(@Param("paiement") UUID paiement);
}
