package com.immogest.app.paiementenligne.infra;

import com.immogest.app.paiementenligne.StatutTransaction;
import com.immogest.app.paiementenligne.domain.Transaction;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionRepository extends JpaRepository<Transaction, UUID>, JpaSpecificationExecutor<Transaction> {

    Optional<Transaction> findByLocataireIdAndCleIdempotence(UUID locataireId, String cle);

    Optional<Transaction> findByFournisseurAndReferenceFournisseur(String fournisseur, String reference);

    long countByLocataireIdAndStatut(UUID locataireId, StatutTransaction statut);

    /** Transactions à revérifier : en attente, créées avant {@code avant}, pas vérifiées depuis {@code derniere}. */
    @Query("select t from Transaction t where t.statut = com.immogest.app.paiementenligne.StatutTransaction.EN_ATTENTE and t.createdAt < :avant "
            + "and (t.derniereVerification is null or t.derniereVerification < :derniere) order by t.createdAt")
    List<Transaction> aVerifier(@Param("avant") OffsetDateTime avant, @Param("derniere") OffsetDateTime derniere, Pageable limite);
}
