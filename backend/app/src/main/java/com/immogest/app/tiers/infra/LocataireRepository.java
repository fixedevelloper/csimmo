package com.immogest.app.tiers.infra;

import com.immogest.app.tiers.domain.Locataire;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LocataireRepository extends JpaRepository<Locataire, UUID>, JpaSpecificationExecutor<Locataire> {

    boolean existsByUtilisateurIdAndIdNot(UUID utilisateurId, UUID id);

    java.util.Optional<Locataire> findByUtilisateurId(UUID utilisateurId);
}
