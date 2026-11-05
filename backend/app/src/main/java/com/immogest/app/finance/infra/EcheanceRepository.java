package com.immogest.app.finance.infra;

import com.immogest.app.finance.StatutEcheance;
import com.immogest.app.finance.domain.Echeance;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EcheanceRepository extends JpaRepository<Echeance, UUID> {

    List<Echeance> findByBailIdOrderByPeriodeDebutAsc(UUID bailId);

    /** Échéances encore à régler, de la plus ancienne à la plus récente (ordre d'imputation). */
    @Query("select e from Echeance e where e.bailId = :bail and e.statut <> com.immogest.app.finance.StatutEcheance.ANNULEE "
            + "and e.montantPaye < e.montant order by e.periodeDebut asc")
    List<Echeance> ouvertes(@Param("bail") UUID bail);

    @Query("select coalesce(sum(e.montant - e.montantPaye), 0) from Echeance e where e.bailId in :bails "
            + "and e.statut <> com.immogest.app.finance.StatutEcheance.ANNULEE and e.dateEcheance <= :date")
    long resteEchuJusquA(@Param("bails") Collection<UUID> bails, @Param("date") java.time.LocalDate date);

    @Query("select coalesce(sum(e.montant - e.montantPaye), 0) from Echeance e where e.bailId in :bails "
            + "and e.statut <> com.immogest.app.finance.StatutEcheance.ANNULEE and e.dateEcheance > :date")
    long resteApres(@Param("bails") Collection<UUID> bails, @Param("date") java.time.LocalDate date);

    @Query("select e from Echeance e where e.statut <> com.immogest.app.finance.StatutEcheance.ANNULEE and e.montantPaye < e.montant "
            + "and e.dateEcheance <= :date order by e.dateEcheance asc")
    List<Echeance> ouvertesJusquA(@Param("date") java.time.LocalDate date);

    /** Échéances arrivées à terme (strictement avant la date) et pas entièrement payées ; {@code filtrer=false} : tous les baux. */
    @Query(value = "select e from Echeance e where e.statut <> com.immogest.app.finance.StatutEcheance.ANNULEE and e.montantPaye < e.montant "
            + "and e.dateEcheance < :date and (:filtrer = false or e.bailId in :bails)",
            countQuery = "select count(e) from Echeance e where e.statut <> com.immogest.app.finance.StatutEcheance.ANNULEE "
                    + "and e.montantPaye < e.montant and e.dateEcheance < :date and (:filtrer = false or e.bailId in :bails)")
    Page<Echeance> arrieres(@Param("date") java.time.LocalDate date, @Param("filtrer") boolean filtrer, @Param("bails") Collection<UUID> bails,
            Pageable pageable);

    @Query("select e from Echeance e where e.bailId = :bail and e.statut <> com.immogest.app.finance.StatutEcheance.ANNULEE "
            + "and e.montantPaye < e.montant and e.dateEcheance < :date order by e.dateEcheance asc")
    List<Echeance> arrieresDuBail(@Param("bail") UUID bail, @Param("date") java.time.LocalDate date);

    List<Echeance> findByStatutIn(Collection<StatutEcheance> statuts);

    @Query("select e from Echeance e where (:bail is null or e.bailId = :bail) and (:statut is null or e.statut = :statut)")
    Page<Echeance> rechercher(@Param("bail") UUID bail, @Param("statut") StatutEcheance statut, Pageable pageable);
}
