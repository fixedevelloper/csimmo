package com.immogest.app.finance.infra;

import com.immogest.app.finance.domain.DocumentPaiement;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DocumentRepository extends JpaRepository<DocumentPaiement, UUID>, JpaSpecificationExecutor<DocumentPaiement> {

    Optional<DocumentPaiement> findByTypeAndEcheanceIdAndStatut(String type, UUID echeanceId, String statut);

    Optional<DocumentPaiement> findByTypeAndPaiementId(String type, UUID paiementId);

    List<DocumentPaiement> findByTypeAndEcheanceIdInAndStatut(String type, Collection<UUID> echeanceIds, String statut);
}
