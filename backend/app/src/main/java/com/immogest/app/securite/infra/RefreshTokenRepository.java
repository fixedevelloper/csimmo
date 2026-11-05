package com.immogest.app.securite.infra;

import com.immogest.app.securite.domain.RefreshToken;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    List<RefreshToken> findByUtilisateurIdAndRevoqueLeIsNull(UUID utilisateurId);

    long deleteByExpireLeBefore(OffsetDateTime limite);
}
