package com.immogest.app.relance.infra;

import com.immogest.app.bail.api.LecteurBaux;
import com.immogest.app.shared.Acteur;
import com.immogest.app.shared.ControleAccesFichier;
import com.immogest.app.tiers.api.LecteurTiers;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** PDF des mises en demeure : ADMIN et GESTIONNAIRE ; le locataire du bail concerné. */
@Component
class ControleAccesRelance implements ControleAccesFichier {

    private final MiseEnDemeureRepository miseEnDemeures;
    private final LecteurBaux baux;
    private final LecteurTiers tiers;

    ControleAccesRelance(MiseEnDemeureRepository miseEnDemeures, LecteurBaux baux, LecteurTiers tiers) {
        this.miseEnDemeures = miseEnDemeures;
        this.baux = baux;
        this.tiers = tiers;
    }

    @Override
    public String module() {
        return "relance";
    }

    @Override
    @Transactional(readOnly = true)
    public boolean peutLire(String entite, UUID entiteId, Acteur acteur) {
        if (acteur.aLeRole("ADMIN") || acteur.aLeRole("GESTIONNAIRE")) {
            return true;
        }
        if (!acteur.aLeRole("LOCATAIRE") || !"mises-en-demeure".equals(entite)) {
            return false;
        }
        UUID locataire = tiers.locataireDeUtilisateur(acteur.id()).orElse(null);
        return locataire != null && miseEnDemeures.findById(entiteId).flatMap(m -> baux.bail(m.getBailId()))
                .map(b -> locataire.equals(b.locataireId())).orElse(false);
    }
}
