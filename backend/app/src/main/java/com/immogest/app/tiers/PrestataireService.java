package com.immogest.app.tiers;

import com.immogest.app.securite.Role;
import com.immogest.app.shared.ErreurMetier;
import com.immogest.app.tiers.domain.Prestataire;
import com.immogest.app.tiers.infra.JournalTiers;
import com.immogest.app.tiers.infra.LiensUtilisateur;
import com.immogest.app.tiers.infra.PrestataireRepository;
import com.immogest.app.tiers.infra.RechercheSpecifications;
import com.immogest.app.tiers.infra.Textes;
import com.immogest.app.tiers.infra.TiersMapper;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Plombiers, électriciens… : fiches des prestataires (leur compte mobile est facultatif). */
@Service
public class PrestataireService {

    private final PrestataireRepository prestataires;
    private final TiersMapper mapper;
    private final LiensUtilisateur liens;
    private final JournalTiers journal;

    PrestataireService(PrestataireRepository prestataires, TiersMapper mapper, LiensUtilisateur liens, JournalTiers journal) {
        this.prestataires = prestataires;
        this.mapper = mapper;
        this.liens = liens;
        this.journal = journal;
    }

    @Transactional(readOnly = true)
    public Page<PrestataireDto> lister(String q, boolean inclureArchives, Pageable pageable) {
        return prestataires.findAll(RechercheSpecifications.<Prestataire>parNomOuTelephone(q, inclureArchives), pageable)
                .map(mapper::versDto);
    }

    @Transactional(readOnly = true)
    public PrestataireDto obtenir(UUID id) {
        return mapper.versDto(charger(id));
    }

    @Transactional
    public PrestataireDto creer(PrestataireSaisie s) {
        String telephone = Textes.telephone(s.telephone());
        String email = Textes.email(s.email());
        verifier(s, telephone, email, null);
        Prestataire p = prestataires.saveAndFlush(new Prestataire(Textes.exiger(s.nom(), "nom", 200), Textes.optionnel(s.metier(), 100),
                telephone, email, Textes.optionnel(s.adresse(), 300), s.utilisateurId(), Textes.optionnel(s.notes(), 2000)));
        journal.publier("TIERS.PRESTATAIRE_CREE", "prestataire", p.getId(), null, resume(p));
        return mapper.versDto(p);
    }

    @Transactional
    public PrestataireDto modifier(UUID id, PrestataireSaisie s) {
        Prestataire p = chargerActif(id);
        String telephone = Textes.telephone(s.telephone());
        String email = Textes.email(s.email());
        verifier(s, telephone, email, id);
        Map<String, Object> avant = resume(p);
        p.modifier(Textes.exiger(s.nom(), "nom", 200), Textes.optionnel(s.metier(), 100), telephone, email,
                Textes.optionnel(s.adresse(), 300), s.utilisateurId(), Textes.optionnel(s.notes(), 2000));
        prestataires.flush();
        journal.publier("TIERS.PRESTATAIRE_MODIFIE", "prestataire", id, avant, resume(p));
        return mapper.versDto(p);
    }

    @Transactional
    public void archiver(UUID id) {
        Prestataire p = chargerActif(id);
        Map<String, Object> avant = resume(p);
        p.archiver();
        journal.publier("TIERS.PRESTATAIRE_ARCHIVE", "prestataire", id, avant, resume(p));
    }

    private void verifier(PrestataireSaisie s, String telephone, String email, UUID idCourant) {
        Textes.contactRequis(telephone, email);
        if (s.utilisateurId() != null) {
            liens.verifier(s.utilisateurId(), Role.PRESTATAIRE);
            if (prestataires.existsByUtilisateurIdAndIdNot(s.utilisateurId(), idCourant == null ? new UUID(0, 0) : idCourant)) {
                throw LiensUtilisateur.dejaLie();
            }
        }
    }

    private Prestataire charger(UUID id) {
        return prestataires.findById(id).orElseThrow(() ->
                new ErreurMetier("PRESTATAIRE_INTROUVABLE", "Prestataire introuvable", HttpStatus.NOT_FOUND));
    }

    private Prestataire chargerActif(UUID id) {
        Prestataire p = charger(id);
        if (p.isArchive()) {
            throw new ErreurMetier("PRESTATAIRE_ARCHIVE", "Ce prestataire est archivé", HttpStatus.CONFLICT);
        }
        return p;
    }

    private static Map<String, Object> resume(Prestataire p) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("nom", p.getNom());
        m.put("metier", p.getMetier());
        m.put("archive", p.isArchive());
        return m;
    }
}
