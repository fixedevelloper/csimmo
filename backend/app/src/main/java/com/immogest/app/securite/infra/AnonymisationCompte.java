package com.immogest.app.securite.infra;

import com.immogest.app.shared.PersonneAnonymisee;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

/**
 * Efface les données personnelles du compte rattaché à une personne anonymisée : nom, téléphone, e-mail ; mot de passe rendu inutilisable ; compte
 * désactivé ; sessions et codes OTP supprimés. La ligne reste (des événements d'audit y renvoient par identifiant). Idempotent.
 */
@Component
class AnonymisationCompte {

    private final JdbcTemplate jdbc;

    AnonymisationCompte(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @ApplicationModuleListener
    void on(PersonneAnonymisee e) {
        UUID id = e.utilisateurId();
        if (id == null) {
            return;
        }
        jdbc.update("DELETE FROM sec_refresh_token WHERE utilisateur_id = ?", id);
        jdbc.update("DELETE FROM sec_otp_code WHERE utilisateur_id = ?", id);
        // « ! » n'est pas un condensé bcrypt valide : aucun mot de passe ne correspondra jamais. Le téléphone unique garde la contrainte « un contact ».
        jdbc.update("UPDATE utilisateur SET nom = 'Utilisateur anonymisé', telephone = 'anonyme-' || id::text, email = NULL, mot_de_passe_hash = '!', actif = FALSE, "
                + "deux_facteurs_actif = FALSE, updated_at = now() WHERE id = ?", id);
    }
}
