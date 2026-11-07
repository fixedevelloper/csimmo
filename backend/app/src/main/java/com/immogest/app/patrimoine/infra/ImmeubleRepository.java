package com.immogest.app.patrimoine.infra;

import com.immogest.app.patrimoine.domain.Immeuble;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ImmeubleRepository extends JpaRepository<Immeuble, UUID>, JpaSpecificationExecutor<Immeuble> {

    /**
     * Propriétaires tiers dont TOUS les immeubles (archivés compris) font partie de l'ensemble donné : seuls leurs reversements peuvent être
     * montrés à un employé restreint sans révéler les montants d'un immeuble hors de son périmètre.
     */
    @org.springframework.data.jpa.repository.Query("select i.proprietaireTiersId from Immeuble i where i.proprietaireTiersId is not null "
            + "group by i.proprietaireTiersId having count(i) = sum(case when i.id in :ids then 1 else 0 end)")
    java.util.List<UUID> proprietairesEntierementDans(@org.springframework.data.repository.query.Param("ids") java.util.Collection<UUID> ids);
}
