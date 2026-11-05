package com.immogest.app.tiers.infra;

import com.immogest.app.tiers.domain.Garant;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;


public interface GarantRepository extends JpaRepository<Garant, UUID> {

    java.util.List<Garant> findByLocataireIdAndArchiveFalseOrderByNomAsc(UUID locataireId);
}
