package com.immogest.app.tiers.infra;

import com.immogest.app.shared.ActeurCourant;
import com.immogest.app.shared.LocatairesDuPerimetre;
import com.immogest.app.tiers.domain.Locataire;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

/**
 * Locataires visibles par l'utilisateur de la requête. Employé restreint : ceux qui ont (ou ont eu) un bail dans son périmètre, et ceux
 * qu'il a créés lui-même (avant leur premier bail). Les autres rôles ne sont pas restreints ici.
 */
@Component
public class VisibiliteLocataires {

    private final LocatairesDuPerimetre perimetre;
    private final ActeurCourant acteurCourant;

    VisibiliteLocataires(LocatairesDuPerimetre perimetre, ActeurCourant acteurCourant) {
        this.perimetre = perimetre;
        this.acteurCourant = acteurCourant;
    }

    public boolean visible(Locataire locataire) {
        return perimetre.locatairesVisibles()
                .map(ids -> ids.contains(locataire.getId()) || moi() != null && moi().equals(locataire.getCreePar()))
                .orElse(true);
    }

    /** Restriction à ajouter aux recherches ; {@code null} si aucune (le résultat peut être passé tel quel à {@code Specification.and}). */
    public Specification<Locataire> restriction() {
        UUID moi = moi();
        return perimetre.locatairesVisibles().<Specification<Locataire>>map(ids -> (r, q, cb) -> {
            var cree = moi == null ? cb.disjunction() : cb.equal(r.get("creePar"), moi);
            return ids.isEmpty() ? cree : cb.or(r.get("id").in(ids), cree);
        }).orElse(null);
    }

    private UUID moi() {
        return acteurCourant.acteur().map(a -> a.id()).orElse(null);
    }
}
