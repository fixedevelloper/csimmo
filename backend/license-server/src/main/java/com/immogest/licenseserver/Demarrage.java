package com.immogest.licenseserver;

import java.time.Clock;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
class Demarrage {

    private static final Logger LOG = LoggerFactory.getLogger(Demarrage.class);

    @Bean
    Clock horloge() {
        return Clock.systemUTC();
    }

    /** Crée le premier compte éditeur depuis la configuration si la base n'en contient aucun. Le mot de passe n'est jamais journalisé. */
    @Bean
    ApplicationRunner premierEditeur(JdbcTemplate jdbc, PasswordEncoder encodeur, Proprietes p) {
        return (ApplicationArguments args) -> {
            Long n = jdbc.queryForObject("SELECT count(*) FROM ls_editeur", Long.class);
            if (n != null && n == 0 && p.adminEmail() != null && !p.adminEmail().isBlank() && p.adminMotDePasse() != null && p.adminMotDePasse().length() >= 12) {
                jdbc.update("INSERT INTO ls_editeur (id, email, nom, mot_de_passe_hash) VALUES (?,?,?,?)", UUID.randomUUID(), p.adminEmail().strip().toLowerCase(), "Éditeur",
                        encodeur.encode(p.adminMotDePasse()));
                LOG.info("Compte éditeur initial créé : {}", p.adminEmail());
            } else if (n != null && n == 0) {
                LOG.warn("Aucun compte éditeur : renseignez LICENSE_SERVER_ADMIN_EMAIL et LICENSE_SERVER_ADMIN_MOT_DE_PASSE (12 caractères au moins)");
            }
        };
    }
}
