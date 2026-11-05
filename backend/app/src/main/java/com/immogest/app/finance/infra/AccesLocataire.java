package com.immogest.app.finance.infra;

import com.immogest.app.bail.api.LecteurBaux;
import com.immogest.app.shared.ActeurCourant;
import com.immogest.app.tiers.api.LecteurTiers;
import java.util.UUID;
import org.springframework.stereotype.Component;

/** Pour un acteur LOCATAIRE : sa fiche (identifiant impossible s'il n'en a pas) ; vide pour les autres rôles. */
@Component
public class AccesLocataire {

    private final ActeurCourant acteurCourant;
    private final LecteurTiers tiers;
    private final LecteurBaux baux;

    AccesLocataire(ActeurCourant acteurCourant, LecteurTiers tiers, LecteurBaux baux) {
        this.acteurCourant = acteurCourant;
        this.tiers = tiers;
        this.baux = baux;
    }

    public UUID locataireImpose() {
        return acteurCourant.acteur().filter(a -> a.aLeRole("LOCATAIRE"))
                .map(a -> tiers.locataireDeUtilisateur(a.id()).orElse(new UUID(0, 0))).orElse(null);
    }

    public boolean bailDe(UUID bailId, UUID locataire) {
        return baux.bail(bailId).map(b -> locataire.equals(b.locataireId())).orElse(false);
    }
}
