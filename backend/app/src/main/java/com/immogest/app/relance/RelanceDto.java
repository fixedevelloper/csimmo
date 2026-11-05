package com.immogest.app.relance;

import com.immogest.app.notification.StatutMessage;
import com.immogest.app.notification.TypeCanal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * @param niveau      jours par rapport à l'échéance (null pour une relance manuelle)
 * @param canal       canal réellement choisi ; null si aucun canal n'était disponible
 * @param statutEnvoi état d'envoi dans la file de notification (EN_ATTENTE, ENVOYE, ECHEC)
 */
public record RelanceDto(UUID id, UUID echeanceId, UUID bailId, UUID locataireId, TypeRelance type, Integer niveau, LocalDate dateRelance,
        ResultatRelance resultat, TypeCanal canal, StatutMessage statutEnvoi, long resteDu, int joursRetard, OffsetDateTime creeLe) {
}
