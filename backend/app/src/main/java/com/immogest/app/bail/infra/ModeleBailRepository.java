package com.immogest.app.bail.infra;

import com.immogest.app.bail.domain.ModeleBail;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModeleBailRepository extends JpaRepository<ModeleBail, UUID> {

    Optional<ModeleBail> findBySingletonTrue();
}
