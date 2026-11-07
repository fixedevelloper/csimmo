package com.immogest.app.securite.infra;

import com.immogest.app.securite.AffectationService;
import com.immogest.app.securite.Role;
import com.immogest.app.shared.Acteur;
import com.immogest.app.shared.ActeurCourant;
import com.immogest.app.shared.PerimetreAcces;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * Périmètre d'immeubles de l'utilisateur courant : ses affectations s'il a un rôle restreint (aucune affectation = aucun immeuble),
 * aucune restriction pour les autres rôles et pour les tâches système (pas d'acteur).
 */
@Component
class PerimetreAccesSecurite implements PerimetreAcces {

    private final ActeurCourant acteurCourant;
    private final AffectationService affectations;

    PerimetreAccesSecurite(ActeurCourant acteurCourant, AffectationService affectations) {
        this.acteurCourant = acteurCourant;
        this.affectations = affectations;
    }

    @Override
    public Optional<Set<UUID>> immeublesVisibles() {
        return acteurCourant.acteur().filter(PerimetreAccesSecurite::estRestreint).map(a -> affectations.immeubles(a.id()));
    }

    /**
     * Restreint dès qu'un rôle restreint est présent, même combiné à un autre : un rôle restreint ne s'élargit jamais.
     * Un rôle inconnu du code est ignoré (il n'ouvre aucune route de toute façon).
     */
    static boolean estRestreint(Acteur acteur) {
        return acteur.roles().stream().anyMatch(r -> {
            try {
                return Role.valueOf(r).restreintAuxAffectations();
            } catch (IllegalArgumentException e) {
                return false;
            }
        });
    }
}
