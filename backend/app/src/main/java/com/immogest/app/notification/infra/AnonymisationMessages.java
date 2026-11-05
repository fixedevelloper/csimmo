package com.immogest.app.notification.infra;

import com.immogest.app.shared.PersonneAnonymisee;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

/**
 * Efface ce que la file de messages garde d'une personne anonymisée : destinataire (numéro, adresse), sujet et corps (qui citent son nom, ses
 * montants). La ligne reste comme trace technique (canal, statut, dates). Les appareils push du compte sont supprimés. Idempotent.
 */
@Component
class AnonymisationMessages {

    private final JdbcTemplate jdbc;

    AnonymisationMessages(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @ApplicationModuleListener
    void on(PersonneAnonymisee e) {
        if ("LOCATAIRE".equals(e.type())) {
            jdbc.update("UPDATE notif_message SET destinataire = 'anonymisé', sujet = NULL, corps = '[message anonymisé]', derniere_erreur = NULL, updated_at = now() WHERE locataire_id = ?",
                    e.personneId());
        }
        if (e.utilisateurId() != null) {
            jdbc.update("DELETE FROM notif_appareil WHERE utilisateur_id = ?", e.utilisateurId());
        }
    }
}
