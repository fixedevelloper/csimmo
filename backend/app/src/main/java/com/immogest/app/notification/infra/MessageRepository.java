package com.immogest.app.notification.infra;

import com.immogest.app.notification.domain.Message;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface MessageRepository extends JpaRepository<Message, UUID>, JpaSpecificationExecutor<Message> {

    boolean existsByCleDedoublonnage(String cle);

    /** Prochain message dû ; SKIP LOCKED : plusieurs instances ou tâches ne prennent jamais le même message. */
    @Query(value = "SELECT * FROM notif_message WHERE statut = 'EN_ATTENTE' AND prochaine_tentative <= ?1 "
            + "ORDER BY prochaine_tentative LIMIT 1 FOR UPDATE SKIP LOCKED", nativeQuery = true)
    Optional<Message> prochainDu(OffsetDateTime maintenant);
}
