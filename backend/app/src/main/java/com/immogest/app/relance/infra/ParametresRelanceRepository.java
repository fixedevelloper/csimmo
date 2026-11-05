package com.immogest.app.relance.infra;

import com.immogest.app.relance.domain.ParametresRelance;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParametresRelanceRepository extends JpaRepository<ParametresRelance, UUID> {

    Optional<ParametresRelance> findBySingletonTrue();
}
