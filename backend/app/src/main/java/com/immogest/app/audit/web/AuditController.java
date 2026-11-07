package com.immogest.app.audit.web;

import com.immogest.app.audit.infra.AuditLogRepository;
import com.immogest.app.shared.PageReponse;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Journal d'activité d'une personne (fiche employé), en lecture seule et réservé à l'ADMIN. Seuls la date, l'action et l'entité visée sont
 * exposés : jamais les valeurs avant/après, qui peuvent contenir des données personnelles.
 */
@RestController
@RequestMapping("/api/v1/audit")
@PreAuthorize("hasRole('ADMIN')")
class AuditController {

    record EntreeAudit(UUID id, OffsetDateTime date, String action, String entite, UUID entiteId) {
    }

    private final AuditLogRepository audit;

    AuditController(AuditLogRepository audit) {
        this.audit = audit;
    }

    @GetMapping
    @io.swagger.v3.oas.annotations.Operation(operationId = "listerActivite")
    @Transactional(readOnly = true)
    PageReponse<EntreeAudit> activite(@RequestParam UUID auteur, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        var demande = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100), Sort.by(Sort.Direction.DESC, "date"));
        return PageReponse.de(audit.findByAuteur(auteur.toString(), demande)
                .map(a -> new EntreeAudit(a.getId(), a.getDate(), a.getAction(), a.getEntite(), a.getEntiteId())));
    }
}
