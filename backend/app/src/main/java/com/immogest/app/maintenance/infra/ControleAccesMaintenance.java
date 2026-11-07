package com.immogest.app.maintenance.infra;

import com.immogest.app.shared.Acteur;
import com.immogest.app.shared.ControleAccesFichier;
import com.immogest.app.tiers.api.LecteurTiers;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Photos de signalements : ADMIN et GESTIONNAIRE ; le locataire qui a fait le signalement. */
@Component
class ControleAccesMaintenance implements ControleAccesFichier {

    private final SignalementRepository signalements;
    private final LecteurTiers tiers;
    private final com.immogest.app.bail.api.LecteurBaux baux;

    ControleAccesMaintenance(SignalementRepository signalements, LecteurTiers tiers, com.immogest.app.bail.api.LecteurBaux baux) {
        this.signalements = signalements;
        this.tiers = tiers;
        this.baux = baux;
    }

    @Override
    public String module() {
        return "maintenance";
    }

    @Override
    @Transactional(readOnly = true)
    public boolean peutLire(String entite, UUID entiteId, Acteur acteur) {
        if (acteur.aLeRole("ADMIN") || acteur.aLeRole("GESTIONNAIRE")) {
            return true;
        }
        if (acteur.aLeRole("GESTIONNAIRE_BIENS") && "signalements".equals(entite)) { // photos des signalements de son périmètre
            return signalements.findById(entiteId).map(x -> baux.bailIdsVisibles().map(ids -> ids.contains(x.getBailId())).orElse(false)).orElse(false);
        }
        if (!acteur.aLeRole("LOCATAIRE") || !"signalements".equals(entite)) {
            return false;
        }
        UUID locataire = tiers.locataireDeUtilisateur(acteur.id()).orElse(null);
        return locataire != null && signalements.findById(entiteId).map(s -> locataire.equals(s.getLocataireId())).orElse(false);
    }
}
