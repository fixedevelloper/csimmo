package com.immogest.app.tiers.infra;

import com.immogest.app.tiers.domain.Prestataire;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PrestataireRepository extends JpaRepository<Prestataire, UUID>, JpaSpecificationExecutor<Prestataire> {

    boolean existsByUtilisateurIdAndIdNot(UUID utilisateurId, UUID id);
}
