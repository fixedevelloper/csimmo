package com.immogest.app.notification.infra;

import com.immogest.app.notification.TypeCanal;
import com.immogest.app.notification.domain.CanalConfig;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CanalConfigRepository extends JpaRepository<CanalConfig, UUID> {

    Optional<CanalConfig> findByCanal(TypeCanal canal);
}
