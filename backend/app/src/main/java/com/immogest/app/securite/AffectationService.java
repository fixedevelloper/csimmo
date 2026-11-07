package com.immogest.app.securite;

import com.immogest.app.securite.infra.UtilisateurRepository;
import com.immogest.app.shared.ActeurCourant;
import com.immogest.app.shared.ErreurMetier;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Immeubles affectés aux employés restreints (docs/adr/0009-equipe-et-perimetres.md). Le périmètre est lu à chaque requête : un retrait
 * agit tout de suite, sans attendre l'expiration du jeton d'accès. Aucune clé étrangère vers le patrimoine (frontière de module).
 */
@Service
public class AffectationService {

    static final int MAX_AFFECTATIONS = 500;

    private final JdbcTemplate jdbc;
    private final UtilisateurRepository utilisateurs;
    private final ApplicationEventPublisher evenements;
    private final ActeurCourant acteurCourant;
    private final Clock horloge;

    AffectationService(JdbcTemplate jdbc, UtilisateurRepository utilisateurs, ApplicationEventPublisher evenements,
            ActeurCourant acteurCourant, Clock horloge) {
        this.jdbc = jdbc;
        this.utilisateurs = utilisateurs;
        this.evenements = evenements;
        this.acteurCourant = acteurCourant;
        this.horloge = horloge;
    }

    @Transactional(readOnly = true)
    public Set<UUID> immeubles(UUID utilisateurId) {
        return new LinkedHashSet<>(jdbc.query("SELECT immeuble_id FROM sec_affectation WHERE utilisateur_id = ? ORDER BY immeuble_id",
                (rs, i) -> rs.getObject(1, UUID.class), utilisateurId));
    }

    /** Liste des immeubles affectés à un compte (404 si le compte n'existe pas). */
    @Transactional(readOnly = true)
    public List<UUID> lister(UUID utilisateurId) {
        charger(utilisateurId);
        return new ArrayList<>(immeubles(utilisateurId));
    }

    /** Remplace les affectations d'un employé restreint ; renvoie la liste enregistrée. */
    @Transactional
    public List<UUID> definir(UUID utilisateurId, Collection<UUID> immeubleIds) {
        Role role = charger(utilisateurId).getRole();
        if (!role.restreintAuxAffectations()) {
            throw new ErreurMetier("AFFECTATION_ROLE_NON_CONCERNE",
                    "Les affectations ne concernent que les employés restreints (rôles ENCAISSEUR et GESTIONNAIRE_BIENS)", HttpStatus.CONFLICT);
        }
        Set<UUID> voulus = new LinkedHashSet<>(immeubleIds);
        if (voulus.size() > MAX_AFFECTATIONS) {
            throw new ErreurMetier("AFFECTATION_TROP_NOMBREUSES", "Trop d'immeubles affectés (500 au plus)", HttpStatus.BAD_REQUEST);
        }
        Set<UUID> avant = immeubles(utilisateurId);
        jdbc.update("DELETE FROM sec_affectation WHERE utilisateur_id = ?", utilisateurId);
        String auteur = acteurCourant.acteur().map(a -> a.id().toString()).orElse("systeme");
        UUID auteurId = acteurCourant.acteur().map(a -> a.id()).orElse(null);
        OffsetDateTime maintenant = OffsetDateTime.now(horloge);
        for (UUID immeuble : voulus) {
            jdbc.update("INSERT INTO sec_affectation (utilisateur_id, immeuble_id, cree_par, created_at) VALUES (?, ?, ?, ?)",
                    utilisateurId, immeuble, auteurId, maintenant);
        }
        evenements.publishEvent(new EvenementSecurite("SECURITE.AFFECTATIONS_MODIFIEES", utilisateurId,
                Map.of("immeubles", avant.stream().map(UUID::toString).sorted().toList()),
                Map.of("immeubles", voulus.stream().map(UUID::toString).sorted().toList()), auteur));
        return new ArrayList<>(voulus);
    }

    private com.immogest.app.securite.domain.Utilisateur charger(UUID id) {
        return utilisateurs.findById(id).orElseThrow(() ->
                new ErreurMetier("UTILISATEUR_INTROUVABLE", "Utilisateur introuvable", HttpStatus.NOT_FOUND));
    }
}
