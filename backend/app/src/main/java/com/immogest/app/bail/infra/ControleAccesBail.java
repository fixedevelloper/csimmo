package com.immogest.app.bail.infra;

import com.immogest.app.shared.Acteur;
import com.immogest.app.shared.ControleAccesFichier;
import com.immogest.app.tiers.api.LecteurTiers;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Qui peut télécharger un exemplaire PDF du bail : ADMIN et GESTIONNAIRE, le gestionnaire de biens pour son périmètre, et le locataire du bail. */
@Component
class ControleAccesBail implements ControleAccesFichier {

    private final BailRepository baux;
    private final LecteurTiers tiers;
    private final com.immogest.app.bail.api.LecteurBaux lecteur;

    ControleAccesBail(BailRepository baux, LecteurTiers tiers, com.immogest.app.bail.api.LecteurBaux lecteur) {
        this.baux = baux;
        this.tiers = tiers;
        this.lecteur = lecteur;
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
        if (acteur.aLeRole("GESTIONNAIRE_BIENS") && "baux".equals(entite)) { // exemplaires PDF des baux de son périmètre
            return lecteur.bailIdsVisibles().map(ids -> ids.contains(entiteId)).orElse(false);
        }
        if (!acteur.aLeRole("LOCATAIRE") || !"baux".equals(entite)) {
            return false;
        }
        UUID locataire = tiers.locataireDeUtilisateur(acteur.id()).orElse(null);
        return locataire != null && baux.findById(entiteId).map(b -> locataire.equals(b.getLocataireId())).orElse(false);
    }
}
