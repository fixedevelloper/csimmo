package com.immogest.app.securite.infra;

import com.immogest.app.securite.domain.OtpCode;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpCodeRepository extends JpaRepository<OtpCode, UUID> {

    Optional<OtpCode> findFirstByUtilisateurIdOrderByCreatedAtDesc(UUID utilisateurId);
}
