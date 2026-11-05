package com.immogest.app.audit.infra;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.immogest.app.audit.domain.AuditLog;
import com.immogest.app.shared.EvenementMetier;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

/** Journalise tout {@link EvenementMetier} publié par n'importe quel module. */
@Component
class AuditListener {

    private static final TypeReference<Map<String, Object>> TYPE_MAP = new TypeReference<>() {
    };

    private final AuditLogRepository repository;
    private final ObjectMapper objectMapper;

    AuditListener(AuditLogRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @ApplicationModuleListener
    void on(EvenementMetier evenement) {
        repository.save(new AuditLog(evenement.auteur(), evenement.action(), evenement.entite(),
                evenement.entiteId(), enMap(evenement.avant()), enMap(evenement.apres()),
                OffsetDateTime.now(ZoneOffset.UTC)));
    }

    private Map<String, Object> enMap(Object valeur) {
        return valeur == null ? null : objectMapper.convertValue(valeur, TYPE_MAP);
    }
}
