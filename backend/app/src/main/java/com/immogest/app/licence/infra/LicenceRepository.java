package com.immogest.app.licence.infra;

import com.immogest.app.licence.domain.LicenceInstallee;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LicenceRepository extends JpaRepository<LicenceInstallee, UUID> {

    Optional<LicenceInstallee> findByActuelleTrue();
}
