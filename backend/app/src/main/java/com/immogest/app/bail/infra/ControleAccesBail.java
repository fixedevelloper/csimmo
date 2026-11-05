package com.immogest.app.bail.infra;

import com.immogest.app.shared.Acteur;
import com.immogest.app.shared.ControleAccesFichier;
import com.immogest.app.tiers.api.LecteurTiers;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Qui peut télécharger un exemplaire PDF du bail : ADMIN et GESTIONNAIRE, et le locataire du bail. */
@Component
class ControleAccesBail implements ControleAccesFichier {

    private final BailRepository baux;
    private final LecteurTiers tiers;

    ControleAccesBail(BailRepository baux, LecteurTiers tiers) {
        this.baux = baux;
        this.tiers = tiers;
    }

    @Override
    public String module() {
        return "bail";
    }

    @Override
    @Transactional(readOnly = true)
    public boolean peutLire(String entite, UUID entiteId, Acteur acteur) {
        if (acteur.aLeRole("ADMIN") || acteur.aLeRole("GESTIONNAIRE")) {
            return true;
        }
        if (!acteur.aLeRole("LOCATAIRE") || !"baux".equals(entite)) {
            return false;
        }
        UUID locataire = tiers.locataireDeUtilisateur(acteur.id()).orElse(null);
        return locataire != null && baux.findById(entiteId).map(b -> locataire.equals(b.getLocataireId())).orElse(false);
    }
}
