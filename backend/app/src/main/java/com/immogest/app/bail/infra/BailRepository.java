package com.immogest.app.bail.infra;

import com.immogest.app.bail.StatutBail;
import com.immogest.app.bail.domain.Bail;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BailRepository extends JpaRepository<Bail, UUID>, JpaSpecificationExecutor<Bail> {

    /** Un bail ACTIF ou RESILIE du lot couvre-t-il tout ou partie de [debut, fin] ? (fin = date lointaine si sans limite) */
    @Query("select count(b) > 0 from Bail b where b.lotId = :lot and b.statut in :statuts "
            + "and b.dateDebut <= :fin and (b.dateFinEffective is null or b.dateFinEffective >= :debut) "
            + "and (:exclu is null or b.id <> :exclu)")
    boolean chevauche(@Param("lot") UUID lot, @Param("statuts") Collection<StatutBail> statuts, @Param("debut") LocalDate debut,
            @Param("fin") LocalDate fin, @Param("exclu") UUID exclu);

    /** Baux dont le dernier jour d'effet est passé : à terminer. */
    List<Bail> findByStatutInAndDateFinEffectiveBefore(Collection<StatutBail> statuts, LocalDate date);

    boolean existsByBailPrecedentId(UUID bailPrecedentId);

    /** Baux (tous statuts) des lots donnés : base du périmètre des employés restreints. */
    @Query("select b.id from Bail b where b.lotId in :lotIds")
    List<UUID> idsDesLots(@Param("lotIds") Collection<UUID> lotIds);

    @Query("select distinct b.locataireId from Bail b where b.lotId in :lotIds")
    List<UUID> locataireIdsDesLots(@Param("lotIds") Collection<UUID> lotIds);

    /** Bails ACTIF/RESILIE du lot encore en vigueur à la date donnée ou à venir (autres que {@code exclu}). */
    @Query("select count(b) > 0 from Bail b where b.lotId = :lot and b.statut in :statuts and b.id <> :exclu "
            + "and (b.dateFinEffective is null or b.dateFinEffective >= :date)")
    boolean existeAutreEnVigueurOuAVenir(@Param("lot") UUID lot, @Param("statuts") Collection<StatutBail> statuts,
            @Param("exclu") UUID exclu, @Param("date") LocalDate date);

    @Query("select b from Bail b where b.statut in :statuts and b.dateDebut <= :date and (b.dateFinEffective is null or b.dateFinEffective >= :date)")
    List<Bail> enVigueur(@Param("statuts") Collection<StatutBail> statuts, @Param("date") LocalDate date);

    @Query("select b from Bail b where b.statut <> com.immogest.app.bail.StatutBail.BROUILLON and b.dateDebut <= :date")
    List<Bail> signesCommencesAvant(@Param("date") LocalDate date);

    @Query("select b from Bail b where b.locataireId = :locataire and b.statut <> com.immogest.app.bail.StatutBail.BROUILLON")
    List<Bail> signesDuLocataire(@Param("locataire") UUID locataire);

    @Query("select b from Bail b where b.statut in :statuts and b.dateFinEffective between :du and :au order by b.dateFinEffective asc")
    List<Bail> arrivantAEcheance(@Param("statuts") Collection<StatutBail> statuts, @Param("du") LocalDate du, @Param("au") LocalDate au);

    @Query(value = "select nextval('bail_numero_seq')", nativeQuery = true)
    long prochainNumero();
}
