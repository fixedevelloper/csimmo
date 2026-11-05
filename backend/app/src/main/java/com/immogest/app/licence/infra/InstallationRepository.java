package com.immogest.app.licence.infra;

import com.immogest.app.licence.domain.Installation;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface InstallationRepository extends JpaRepository<Installation, UUID> {

    Optional<Installation> findBySingletonTrue();

    /** Verrou d'écriture : deux assistants simultanés ne peuvent pas réussir tous les deux. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Installation i where i.singleton = true")
    Optional<Installation> verrouiller();
}
