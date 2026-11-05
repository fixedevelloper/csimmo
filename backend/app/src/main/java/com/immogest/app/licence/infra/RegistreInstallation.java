package com.immogest.app.licence.infra;

import com.immogest.app.licence.domain.Installation;
import com.immogest.licensing.EmpreinteServeur;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Identifiant d'installation (créé une fois, au premier démarrage, puis conservé en base) et empreinte de ce serveur.
 */
@Component
public class RegistreInstallation {

    private final InstallationRepository repository;
    private final FournisseurEmpreinte fournisseur;
    private final TransactionTemplate transaction;
    private volatile String identifiant;

    RegistreInstallation(InstallationRepository repository, FournisseurEmpreinte fournisseur,
            PlatformTransactionManager gestionnaire) {
        this.repository = repository;
        this.fournisseur = fournisseur;
        this.transaction = new TransactionTemplate(gestionnaire);
        this.transaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    /** Identifiant d'installation ; généré et enregistré s'il n'existe pas encore. */
    public String identifiant() {
        String valeur = identifiant;
        if (valeur == null) {
            valeur = transaction.execute(status -> repository.findBySingletonTrue()
                    .orElseGet(() -> repository.saveAndFlush(new Installation(EmpreinteServeur.nouvelIdentifiantInstallation())))
                    .getIdentifiantInstallation());
            identifiant = valeur;
        }
        return valeur;
    }

    /** Empreinte de ce serveur, ou vide si aucun identifiant machine n'est détectable. */
    public Optional<String> empreinte() {
        return fournisseur.empreinte(identifiant());
    }

    /** Oublie l'identifiant en mémoire (tests : la base a été remise à zéro). */
    public void oublier() {
        identifiant = null;
    }
}
