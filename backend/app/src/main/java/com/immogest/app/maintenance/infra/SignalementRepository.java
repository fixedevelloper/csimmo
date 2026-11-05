package com.immogest.app.maintenance.infra;

import com.immogest.app.maintenance.StatutSignalement;
import com.immogest.app.maintenance.domain.Signalement;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SignalementRepository extends JpaRepository<Signalement, UUID>, JpaSpecificationExecutor<Signalement> {

    long countByLocataireIdAndStatut(UUID locataireId, StatutSignalement statut);
}
