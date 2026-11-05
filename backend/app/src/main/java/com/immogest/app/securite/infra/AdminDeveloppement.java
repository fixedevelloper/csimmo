package com.immogest.app.securite.infra;

import com.immogest.app.securite.Role;
import com.immogest.app.securite.UtilisateurService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Profil dev uniquement : crée un ADMIN si la base est vide pour pouvoir se connecter.
 * En production, l'ADMIN est créé par l'assistant d'installation (étape 1.2).
 */
@Component
@Profile("dev")
@ConditionalOnProperty(name = "immogest.dev.installation-auto", havingValue = "true")
class AdminDeveloppement implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(AdminDeveloppement.class);

    private final UtilisateurRepository utilisateurs;
    private final UtilisateurService service;

    AdminDeveloppement(UtilisateurRepository utilisateurs, UtilisateurService service) {
        this.utilisateurs = utilisateurs;
        this.service = service;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (utilisateurs.count() == 0) {
            service.creer(new UtilisateurService.Saisie("Admin Dev", "+237600000000", "admin@immogest.local",
                    Role.ADMIN, true, false), "Admin1234!");
            LOG.warn("Compte ADMIN de développement créé : admin@immogest.local / Admin1234! (ne jamais utiliser en production)");
        }
    }
}
