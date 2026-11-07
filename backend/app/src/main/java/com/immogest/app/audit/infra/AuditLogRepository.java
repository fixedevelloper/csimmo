package com.immogest.app.audit.infra;

import com.immogest.app.audit.domain.AuditLog;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

    List<AuditLog> findByEntiteIdOrderByDateAsc(UUID entiteId);

    /** Actions d'une personne (identifiant du compte), les plus récentes d'abord. */
    org.springframework.data.domain.Page<AuditLog> findByAuteur(String auteur, org.springframework.data.domain.Pageable pageable);
}
