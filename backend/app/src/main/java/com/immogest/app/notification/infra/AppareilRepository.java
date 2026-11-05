package com.immogest.app.notification.infra;

import com.immogest.app.notification.domain.Appareil;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppareilRepository extends JpaRepository<Appareil, UUID> {

    Optional<Appareil> findByToken(String token);

    List<Appareil> findByUtilisateurId(UUID utilisateurId);
}
