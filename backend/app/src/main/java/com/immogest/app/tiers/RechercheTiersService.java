package com.immogest.app.tiers;

import com.immogest.app.tiers.domain.Locataire;
import com.immogest.app.tiers.domain.Prestataire;
import com.immogest.app.tiers.domain.ProprietaireTiers;
import com.immogest.app.tiers.infra.LocataireRepository;
import com.immogest.app.tiers.infra.PrestataireRepository;
import com.immogest.app.tiers.infra.ProprietaireTiersRepository;
import com.immogest.app.tiers.infra.RechercheSpecifications;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Recherche rapide (barre de recherche globale) par nom ou téléphone dans tous les types de tiers. */
@Service
public class RechercheTiersService {

    static final int PAR_TYPE = 10;
    private static final PageRequest PAGE = PageRequest.of(0, PAR_TYPE, Sort.by("nom"));

    private final ProprietaireTiersRepository proprietaires;
    private final LocataireRepository locataires;
    private final PrestataireRepository prestataires;

    RechercheTiersService(ProprietaireTiersRepository proprietaires, LocataireRepository locataires, PrestataireRepository prestataires) {
        this.proprietaires = proprietaires;
        this.locataires = locataires;
        this.prestataires = prestataires;
    }

    /** Au moins 2 caractères ; 10 résultats au plus par type ; fiches archivées exclues. */
    @Transactional(readOnly = true)
    public List<ResultatRecherche> rechercher(String q) {
        if (q == null || q.strip().length() < 2) {
            return List.of();
        }
        List<ResultatRecherche> resultats = new ArrayList<>();
        proprietaires.findAll(RechercheSpecifications.<ProprietaireTiers>parNomOuTelephone(q, false), PAGE)
                .forEach(p -> resultats.add(new ResultatRecherche("PROPRIETAIRE", p.getId(), p.getNom(), p.getTelephone())));
        locataires.findAll(RechercheSpecifications.<Locataire>parNomOuTelephone(q, false), PAGE)
                .forEach(l -> resultats.add(new ResultatRecherche("LOCATAIRE", l.getId(), l.getNom(), l.getTelephone())));
        prestataires.findAll(RechercheSpecifications.<Prestataire>parNomOuTelephone(q, false), PAGE)
                .forEach(p -> resultats.add(new ResultatRecherche("PRESTATAIRE", p.getId(), p.getNom(), p.getTelephone())));
        return resultats;
    }
}
