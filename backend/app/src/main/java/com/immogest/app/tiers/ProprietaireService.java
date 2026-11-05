package com.immogest.app.tiers;

import com.immogest.app.securite.Role;
import com.immogest.app.shared.ErreurMetier;
import com.immogest.app.tiers.domain.Mandat;
import com.immogest.app.tiers.domain.ProprietaireTiers;
import com.immogest.app.tiers.infra.JournalTiers;
import com.immogest.app.tiers.infra.LiensUtilisateur;
import com.immogest.app.tiers.infra.MandatRepository;
import com.immogest.app.tiers.infra.ProprietaireTiersRepository;
import com.immogest.app.tiers.infra.RechercheSpecifications;
import com.immogest.app.tiers.infra.Textes;
import com.immogest.app.tiers.infra.TiersMapper;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Propriétaires tiers (clients d'une agence) et leurs mandats de gestion. */
@Service
public class ProprietaireService {

    private static final LocalDate SANS_LIMITE = LocalDate.of(9999, 12, 31);

    private final ProprietaireTiersRepository proprietaires;
    private final MandatRepository mandats;
    private final TiersMapper mapper;
    private final LiensUtilisateur liens;
    private final JournalTiers journal;

    ProprietaireService(ProprietaireTiersRepository proprietaires, MandatRepository mandats, TiersMapper mapper,
            LiensUtilisateur liens, JournalTiers journal) {
        this.proprietaires = proprietaires;
        this.mandats = mandats;
        this.mapper = mapper;
        this.liens = liens;
        this.journal = journal;
    }

    @Transactional(readOnly = true)
    public Page<ProprietaireDto> lister(String q, boolean inclureArchives, Pageable pageable) {
        return proprietaires.findAll(RechercheSpecifications.<ProprietaireTiers>parNomOuTelephone(q, inclureArchives), pageable)
                .map(mapper::versDto);
    }

    @Transactional(readOnly = true)
    public ProprietaireDto obtenir(UUID id) {
        return mapper.versDto(charger(id));
    }

    @Transactional
    public ProprietaireDto creer(ProprietaireSaisie s) {
        Normalisee n = normaliser(s, null);
        ProprietaireTiers p = proprietaires.saveAndFlush(new ProprietaireTiers(n.nom, n.telephone, n.email, n.adresse, n.mode,
                n.coordonnees, n.utilisateurId, n.notes));
        journal.publier("TIERS.PROPRIETAIRE_CREE", "proprietaire", p.getId(), null, resume(p));
        return mapper.versDto(p);
    }

    @Transactional
    public ProprietaireDto modifier(UUID id, ProprietaireSaisie s) {
        ProprietaireTiers p = chargerActif(id);
        Normalisee n = normaliser(s, id);
        Map<String, Object> avant = resume(p);
        p.modifier(n.nom, n.telephone, n.email, n.adresse, n.mode, n.coordonnees, n.utilisateurId, n.notes);
        proprietaires.flush();
        journal.publier("TIERS.PROPRIETAIRE_MODIFIE", "proprietaire", id, avant, resume(p));
        return mapper.versDto(p);
    }

    @Transactional
    public void archiver(UUID id) {
        ProprietaireTiers p = chargerActif(id);
        Map<String, Object> avant = resume(p);
        p.archiver();
        journal.publier("TIERS.PROPRIETAIRE_ARCHIVE", "proprietaire", id, avant, resume(p));
    }

    // ------------------------------------------------------------------ mandats

    @Transactional(readOnly = true)
    public List<MandatDto> listerMandats(UUID proprietaireId) {
        charger(proprietaireId);
        return mandats.findByProprietaireIdOrderByDateDebutDesc(proprietaireId).stream().map(mapper::versDto).toList();
    }

    @Transactional(readOnly = true)
    public MandatDto obtenirMandat(UUID id) {
        return mapper.versDto(chargerMandat(id));
    }

    @Transactional
    public MandatDto creerMandat(UUID proprietaireId, MandatSaisie s) {
        chargerActif(proprietaireId);
        MandatSaisie n = normaliser(s);
        verifierChevauchement(proprietaireId, n, null);
        try {
            Mandat m = mandats.saveAndFlush(new Mandat(proprietaireId, n.tauxHonorairesBp(), n.dateDebut(), n.dateFin(), n.notes()));
            journal.publier("TIERS.MANDAT_CREE", "mandat", m.getId(), null, resume(m));
            return mapper.versDto(m);
        } catch (DataIntegrityViolationException e) {
            throw chevauchement(); // course entre deux créations : la contrainte d'exclusion de la base tranche
        }
    }

    @Transactional
    public MandatDto modifierMandat(UUID id, MandatSaisie s) {
        Mandat m = chargerMandat(id);
        MandatSaisie n = normaliser(s);
        verifierChevauchement(m.getProprietaireId(), n, id);
        Map<String, Object> avant = resume(m);
        m.modifier(n.tauxHonorairesBp(), n.dateDebut(), n.dateFin(), n.notes());
        try {
            mandats.flush();
        } catch (DataIntegrityViolationException e) {
            throw chevauchement();
        }
        journal.publier("TIERS.MANDAT_MODIFIE", "mandat", id, avant, resume(m));
        return mapper.versDto(m);
    }

    // ------------------------------------------------------------------ interne

    private record Normalisee(String nom, String telephone, String email, String adresse, ModeReversement mode,
            String coordonnees, UUID utilisateurId, String notes) {
    }

    private Normalisee normaliser(ProprietaireSaisie s, UUID idCourant) {
        String telephone = Textes.telephone(s.telephone());
        String email = Textes.email(s.email());
        Textes.contactRequis(telephone, email);
        if (s.utilisateurId() != null) {
            liens.verifier(s.utilisateurId(), Role.PROPRIETAIRE_TIERS);
            if (proprietaires.existsByUtilisateurIdAndIdNot(s.utilisateurId(), idCourant == null ? new UUID(0, 0) : idCourant)) {
                throw LiensUtilisateur.dejaLie();
            }
        }
        return new Normalisee(Textes.exiger(s.nom(), "nom", 200), telephone, email, Textes.optionnel(s.adresse(), 300),
                s.modeReversement() == null ? ModeReversement.VIREMENT : s.modeReversement(),
                Textes.optionnel(s.coordonneesReversement(), 300), s.utilisateurId(), Textes.optionnel(s.notes(), 2000));
    }

    private static MandatSaisie normaliser(MandatSaisie s) {
        if (s.tauxHonorairesBp() == null || s.tauxHonorairesBp() < 0 || s.tauxHonorairesBp() > 10_000) {
            throw new ErreurMetier("MANDAT_TAUX_INVALIDE", "Le taux d'honoraires est compris entre 0 et 10 000 points de base (100 %)",
                    HttpStatus.BAD_REQUEST);
        }
        if (s.dateDebut() == null) {
            throw new ErreurMetier("MANDAT_DATE_DEBUT_REQUISE", "La date de début est obligatoire", HttpStatus.BAD_REQUEST);
        }
        if (s.dateFin() != null && s.dateFin().isBefore(s.dateDebut())) {
            throw new ErreurMetier("MANDAT_DATES_INVALIDES", "La date de fin précède la date de début", HttpStatus.BAD_REQUEST);
        }
        return new MandatSaisie(s.tauxHonorairesBp(), s.dateDebut(), s.dateFin(), Textes.optionnel(s.notes(), 1000));
    }

    private void verifierChevauchement(UUID proprietaireId, MandatSaisie s, UUID exclu) {
        if (mandats.chevauche(proprietaireId, s.dateDebut(), s.dateFin() == null ? SANS_LIMITE : s.dateFin(), exclu)) {
            throw chevauchement();
        }
    }

    private static ErreurMetier chevauchement() {
        return new ErreurMetier("MANDAT_CHEVAUCHEMENT", "Ce propriétaire a déjà un mandat sur tout ou partie de cette période",
                HttpStatus.CONFLICT);
    }

    private ProprietaireTiers charger(UUID id) {
        return proprietaires.findById(id).orElseThrow(() ->
                new ErreurMetier("PROPRIETAIRE_INTROUVABLE", "Propriétaire introuvable", HttpStatus.NOT_FOUND));
    }

    private ProprietaireTiers chargerActif(UUID id) {
        ProprietaireTiers p = charger(id);
        if (p.isArchive()) {
            throw new ErreurMetier("PROPRIETAIRE_ARCHIVE", "Ce propriétaire est archivé", HttpStatus.CONFLICT);
        }
        return p;
    }

    private Mandat chargerMandat(UUID id) {
        return mandats.findById(id).orElseThrow(() -> new ErreurMetier("MANDAT_INTROUVABLE", "Mandat introuvable", HttpStatus.NOT_FOUND));
    }

    private static Map<String, Object> resume(ProprietaireTiers p) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("nom", p.getNom());
        m.put("modeReversement", p.getModeReversement().name());
        m.put("utilisateurId", p.getUtilisateurId() == null ? null : p.getUtilisateurId().toString());
        m.put("archive", p.isArchive());
        return m;
    }

    private static Map<String, Object> resume(Mandat m) {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("proprietaireId", m.getProprietaireId().toString());
        r.put("tauxHonorairesBp", m.getTauxHonorairesBp());
        r.put("dateDebut", m.getDateDebut().toString());
        r.put("dateFin", m.getDateFin() == null ? null : m.getDateFin().toString());
        return r;
    }
}
