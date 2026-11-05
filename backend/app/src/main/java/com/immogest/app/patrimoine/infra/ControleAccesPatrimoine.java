package com.immogest.app.patrimoine.infra;

import com.immogest.app.shared.Acteur;
import com.immogest.app.shared.ControleAccesFichier;
import com.immogest.app.tiers.api.LecteurTiers;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Qui peut télécharger une photo d'immeuble ou de lot : ADMIN et GESTIONNAIRE, et un propriétaire tiers pour SES immeubles
 * (et leurs lots). Les locataires (le lot de leur bail actif) seront ajoutés avec le module bail (lot 3.1) ; d'ici là
 * l'accès leur est refusé, comme à tout autre rôle.
 */
@Component
class ControleAccesPatrimoine implements ControleAccesFichier {

    private final ImmeubleRepository immeubles;
    private final LotRepository lots;
    private final LecteurTiers tiers;

    ControleAccesPatrimoine(ImmeubleRepository immeubles, LotRepository lots, LecteurTiers tiers) {
        this.immeubles = immeubles;
        this.lots = lots;
        this.tiers = tiers;
    }

    @Override
    public String module() {
        return "patrimoine";
    }

    @Override
    @Transactional(readOnly = true)
    public boolean peutLire(String entite, UUID entiteId, Acteur acteur) {
        if (acteur.aLeRole("ADMIN") || acteur.aLeRole("GESTIONNAIRE")) {
            return true;
        }
        if (!acteur.aLeRole("PROPRIETAIRE_TIERS")) {
            return false;
        }
        UUID proprietaire = tiers.proprietaireDeUtilisateur(acteur.id()).orElse(null);
        if (proprietaire == null) {
            return false;
        }
        UUID proprietaireDuBien = switch (entite) {
            case "immeuble" -> immeubles.findById(entiteId).map(i -> i.getProprietaireTiersId()).orElse(null);
            case "lot" -> lots.findById(entiteId).map(l -> l.getImmeuble().getProprietaireTiersId()).orElse(null);
            default -> null;
        };
        return proprietaire.equals(proprietaireDuBien);
    }
}
