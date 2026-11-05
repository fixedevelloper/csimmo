package com.immogest.app.licence;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/** Serveur de licences simulé : aucun appel réseau dans les tests (CLAUDE.md §9). */
public class ServeurLicencesFake implements ServeurLicencesClient {

    public final List<DemandeVerification> appels = new CopyOnWriteArrayList<>();
    private volatile ReponseVerification reponse = new ReponseVerification(ReponseVerification.Statut.VALIDE, null);
    private volatile boolean enPanne;

    public void repondre(ReponseVerification.Statut statut) {
        repondre(statut, null);
    }

    public void repondre(ReponseVerification.Statut statut, String licenceRenouvelee) {
        reponse = new ReponseVerification(statut, licenceRenouvelee);
        enPanne = false;
    }

    public void tomberEnPanne() {
        enPanne = true;
    }

    public void reinitialiser() {
        appels.clear();
        repondre(ReponseVerification.Statut.VALIDE);
    }

    @Override
    public ReponseVerification verifier(DemandeVerification demande) throws ServeurLicencesIndisponibleException {
        appels.add(demande);
        if (enPanne) {
            throw new ServeurLicencesIndisponibleException("serveur de licences simulé en panne");
        }
        return reponse;
    }
}
