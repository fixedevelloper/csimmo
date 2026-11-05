package com.immogest.app.sauvegarde.infra;

import com.immogest.app.sauvegarde.SauvegardeProperties;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Après une restauration, la base a été remplacée sous les pieds de l'application (ses connexions pointent vers l'ancienne). Le conteneur de
 * sauvegarde dépose {@code redemarrer-app} ; l'application s'arrête alors proprement et Docker (restart: unless-stopped) la relance sur la base
 * restaurée. Un fichier antérieur au démarrage de l'application est ignoré (il concerne une instance précédente) et supprimé.
 */
@Component
class RedemarrageSurDemande {

    private static final Logger LOG = LoggerFactory.getLogger(RedemarrageSurDemande.class);

    private final SauvegardeProperties proprietes;
    private final ApplicationContext contexte;
    private final Instant demarrage = Instant.now();
    private ScheduledExecutorService planificateur;

    RedemarrageSurDemande(SauvegardeProperties proprietes, ApplicationContext contexte) {
        this.proprietes = proprietes;
        this.contexte = contexte;
    }

    @PostConstruct
    void demarrer() {
        if (!proprietes.redemarrageSurDemande()) {
            return;
        }
        planificateur = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "redemarrage-sur-demande");
            t.setDaemon(true);
            return t;
        });
        planificateur.scheduleWithFixedDelay(this::verifier, 10, 5, TimeUnit.SECONDS);
    }

    @PreDestroy
    void arreter() {
        if (planificateur != null) {
            planificateur.shutdownNow();
        }
    }

    void verifier() {
        Path fichier = Path.of(proprietes.demandes(), "redemarrer-app");
        try {
            if (!Files.exists(fichier)) {
                return;
            }
            Instant dateFichier = Files.getLastModifiedTime(fichier).toInstant();
            Files.deleteIfExists(fichier);
            if (dateFichier.isBefore(demarrage)) {
                return;
            }
            LOG.warn("Restauration terminée : redémarrage de l'application");
            System.exit(SpringApplication.exit(contexte, () -> 0));
        } catch (IOException e) {
            LOG.warn("Lecture de la demande de redémarrage impossible : {}", e.getMessage());
        }
    }
}
