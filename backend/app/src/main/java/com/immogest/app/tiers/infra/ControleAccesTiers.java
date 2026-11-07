package com.immogest.app.tiers.infra;

import com.immogest.app.shared.Acteur;
import com.immogest.app.shared.ControleAccesFichier;
import com.immogest.app.tiers.domain.Garant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Qui peut télécharger une pièce d'identité : ADMIN et GESTIONNAIRE, et un locataire pour SES propres pièces (celles de
 * sa fiche ou de ses garants). Les propriétaires tiers et les prestataires n'y ont jamais accès.
 */
@Component
class ControleAccesTiers implements ControleAccesFichier {

    private final LocataireRepository locataires;
    private final GarantRepository garants;
    private final VisibiliteLocataires visibilite;

    ControleAccesTiers(LocataireRepository locataires, GarantRepository garants, VisibiliteLocataires visibilite) {
        this.locataires = locataires;
        this.garants = garants;
        this.visibilite = visibilite;
    }

    @Override
    public String module() {
        return "tiers";
    }

    @Override
    @Transactional(readOnly = true)
    public boolean peutLire(String entite, UUID entiteId, Acteur acteur) {
        if (acteur.aLeRole("ADMIN") || acteur.aLeRole("GESTIONNAIRE")) {
            return true;
        }
        if (acteur.aLeRole("GESTIONNAIRE_BIENS")) { // pièces d'identité des locataires (et de leurs garants) de son périmètre
            UUID locataireVisible = switch (entite) {
                case "locataire" -> entiteId;
                case "garant" -> garants.findById(entiteId).map(Garant::getLocataireId).orElse(null);
                default -> null;
            };
            return locataireVisible != null && locataires.findById(locataireVisible).map(visibilite::visible).orElse(false);
        }
        if (!acteur.aLeRole("LOCATAIRE")) {
            return false;
        }
        UUID locataireId = switch (entite) {
            case "locataire" -> entiteId;
            case "garant" -> garants.findById(entiteId).map(Garant::getLocataireId).orElse(null);
            default -> null;
        };
        return locataireId != null && Optional.ofNullable(locataireId).flatMap(locataires::findById)
                .map(l -> acteur.id().equals(l.getUtilisateurId())).orElse(false);
    }
}
