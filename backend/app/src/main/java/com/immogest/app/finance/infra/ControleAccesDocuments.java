package com.immogest.app.finance.infra;

import com.immogest.app.bail.api.LecteurBaux;
import com.immogest.app.finance.domain.DocumentPaiement;
import com.immogest.app.shared.Acteur;
import com.immogest.app.shared.ControleAccesFichier;
import com.immogest.app.tiers.api.LecteurTiers;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Téléchargement des PDF du module : quittances et reçus (ADMIN, GESTIONNAIRE ; le locataire du bail tant que le document n'est pas annulé)
 * et relevés de gérance (ADMIN, GESTIONNAIRE ; le propriétaire concerné une fois le reversement validé).
 */
@Component
class ControleAccesDocuments implements ControleAccesFichier {

    private final DocumentRepository documents;
    private final ReversementRepository reversements;
    private final LecteurBaux baux;
    private final LecteurTiers tiers;
    private final com.immogest.app.finance.PerimetreReversements perimetre;

    ControleAccesDocuments(DocumentRepository documents, ReversementRepository reversements, LecteurBaux baux, LecteurTiers tiers,
            com.immogest.app.finance.PerimetreReversements perimetre) {
        this.perimetre = perimetre;
        this.documents = documents;
        this.reversements = reversements;
        this.baux = baux;
        this.tiers = tiers;
    }

    @Override
    public String module() {
        return "finance";
    }

    @Override
    @Transactional(readOnly = true)
    public boolean peutLire(String entite, UUID entiteId, Acteur acteur) {
        if (acteur.aLeRole("ADMIN") || acteur.aLeRole("GESTIONNAIRE")) {
            return true;
        }
        if (acteur.aLeRole("COMPTABLE") && "reversements".equals(entite)) { // relevés des propriétaires de son périmètre, une fois validés
            return reversements.findById(entiteId).map(r -> !"CALCULE".equals(r.getStatut())
                    && perimetre.proprietaires().map(ids -> ids.contains(r.getProprietaireId())).orElse(false)).orElse(false);
        }
        if (acteur.aLeRole("PROPRIETAIRE_TIERS") && "reversements".equals(entite)) { // relevé de gérance : le mandant, une fois validé
            UUID proprietaire = tiers.proprietaireDeUtilisateur(acteur.id()).orElse(null);
            return proprietaire != null && reversements.findById(entiteId)
                    .map(r -> proprietaire.equals(r.getProprietaireId()) && !"CALCULE".equals(r.getStatut())).orElse(false);
        }
        if ((acteur.aLeRole("ENCAISSEUR") || acteur.aLeRole("GESTIONNAIRE_BIENS") || acteur.aLeRole("COMPTABLE")) && "documents".equals(entite)) { // quittances et reçus des baux de son périmètre, jamais les annulés
            return documents.findById(entiteId).filter(d -> DocumentPaiement.EMIS.equals(d.getStatut()))
                    .map(d -> baux.bailIdsVisibles().map(ids -> ids.contains(d.getBailId())).orElse(false)).orElse(false);
        }
        if (!acteur.aLeRole("LOCATAIRE") || !"documents".equals(entite)) {
            return false;
        }
        UUID locataire = tiers.locataireDeUtilisateur(acteur.id()).orElse(null);
        return locataire != null && documents.findById(entiteId)
                .filter(d -> DocumentPaiement.EMIS.equals(d.getStatut()))
                .flatMap(d -> baux.bail(d.getBailId()))
                .map(b -> locataire.equals(b.locataireId())).orElse(false);
    }
}
