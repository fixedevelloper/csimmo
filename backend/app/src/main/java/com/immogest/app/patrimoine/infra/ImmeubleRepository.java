package com.immogest.app.patrimoine.infra;

import com.immogest.app.patrimoine.domain.Immeuble;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ImmeubleRepository extends JpaRepository<Immeuble, UUID>, JpaSpecificationExecutor<Immeuble> {
}
