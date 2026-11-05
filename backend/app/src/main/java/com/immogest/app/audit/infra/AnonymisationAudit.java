package com.immogest.app.audit.infra;

import com.immogest.app.shared.PersonneAnonymisee;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

/**
 * Le journal d'audit est conservé 10 ans et ne se supprime jamais, mais il ne doit pas garder de données personnelles d'une personne anonymisée :
 * ses champs nom, téléphone, e-mail, adresse y sont remplacés par « ANONYME ». Seules les colonnes avant/apres changent ; l'auteur, l'action, la date
 * et l'entité restent intacts (le déclencheur de la base l'impose, et n'autorise la modification que sous l'indicateur
 * {@code immogest.anonymisation} posé ici pour cette seule transaction). Idempotent.
 */
@Component
class AnonymisationAudit {

    private static final String[] CHAMPS = {"nom", "telephone", "email", "adresse", "profession", "coordonneesReversement"};
    private final JdbcTemplate jdbc;

    AnonymisationAudit(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @ApplicationModuleListener // déjà transactionnelle (nouvelle transaction) : le SET LOCAL ci-dessous ne vaut que pour elle
    void on(PersonneAnonymisee e) {
        jdbc.execute("SET LOCAL immogest.anonymisation = 'on'");
        for (UUID id : java.util.Arrays.asList(e.personneId(), e.utilisateurId())) {
            if (id == null) {
                continue;
            }
            for (String champ : CHAMPS) {
                for (String colonne : new String[] {"avant", "apres"}) {
                    // jsonb_exists(j, k) est l'opérateur « ? » de PostgreSQL, que JDBC confondrait avec un paramètre
                    jdbc.update("UPDATE audit_log SET " + colonne + " = jsonb_set(" + colonne + ", ARRAY[?], '\"ANONYME\"') WHERE entite_id = ? AND jsonb_exists(" + colonne + ", ?) AND "
                            + colonne + " ->> ? <> 'ANONYME'", champ, id, champ, champ);
                }
            }
        }
    }
}
