package com.immogest.app.finance.infra;

import com.immogest.app.finance.domain.Depense;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DepenseRepository extends JpaRepository<Depense, UUID>, JpaSpecificationExecutor<Depense> {

    @Query("select d from Depense d where d.proprietaireId = :proprietaire and d.statut = 'ENREGISTREE' and d.dateDepense between :du and :au order by d.dateDepense")
    List<Depense> actives(@Param("proprietaire") UUID proprietaire, @Param("du") LocalDate du, @Param("au") LocalDate au);

    @Query("select d from Depense d where d.statut = 'ENREGISTREE' and d.dateDepense between :du and :au")
    List<Depense> activesDuMois(@Param("du") LocalDate du, @Param("au") LocalDate au);
}
