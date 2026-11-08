package com.immogest.app.securite.infra;

import com.immogest.app.securite.domain.DefiDeuxFacteurs;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DefiDeuxFacteursRepository extends JpaRepository<DefiDeuxFacteurs, UUID> {

    Optional<DefiDeuxFacteurs> findByTokenHash(String tokenHash);

    Optional<DefiDeuxFacteurs> findFirstByUtilisateurIdAndTypeOrderByCreatedAtDesc(UUID utilisateurId, DefiDeuxFacteurs.Type type);
}
