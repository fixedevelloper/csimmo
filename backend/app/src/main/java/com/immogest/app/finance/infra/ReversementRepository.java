package com.immogest.app.finance.infra;

import com.immogest.app.finance.domain.Reversement;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ReversementRepository extends JpaRepository<Reversement, UUID>, JpaSpecificationExecutor<Reversement> {

    Optional<Reversement> findByProprietaireIdAndPeriode(UUID proprietaireId, LocalDate periode);

    List<Reversement> findByPeriode(LocalDate periode);
}
