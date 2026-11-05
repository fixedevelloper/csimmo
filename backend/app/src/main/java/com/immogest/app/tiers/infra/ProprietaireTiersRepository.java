package com.immogest.app.tiers.infra;

import com.immogest.app.tiers.domain.ProprietaireTiers;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProprietaireTiersRepository extends JpaRepository<ProprietaireTiers, UUID>, JpaSpecificationExecutor<ProprietaireTiers> {

    boolean existsByUtilisateurIdAndIdNot(UUID utilisateurId, UUID id);

    java.util.Optional<ProprietaireTiers> findByUtilisateurId(UUID utilisateurId);
}
