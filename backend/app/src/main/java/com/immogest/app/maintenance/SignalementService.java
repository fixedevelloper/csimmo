package com.immogest.app.maintenance;

import com.immogest.app.bail.api.BailResume;
import com.immogest.app.bail.api.LecteurBaux;
import com.immogest.app.maintenance.domain.Signalement;
import com.immogest.app.maintenance.infra.SignalementRepository;
import com.immogest.app.patrimoine.api.LecteurLots;
import com.immogest.app.patrimoine.api.LotResume;
import com.immogest.app.shared.ActeurCourant;
import com.immogest.app.shared.ErreurMetier;
import com.immogest.app.stockage.FichierDto;
import com.immogest.app.stockage.StockageService;
import com.immogest.app.tiers.api.LecteurTiers;
import jakarta.persistence.criteria.Predicate;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Signalements de pannes : créés par un locataire pour un de ses baux en cours, suivis par les gestionnaires. */
@Service
public class SignalementService {

    static final int MAX_OUVERTS = 10;

    private final SignalementRepository signalements;
    private final LecteurBaux baux;
    private final LecteurLots lots;
    private final LecteurTiers tiers;
    private final StockageService stockage;
    private final ActeurCourant acteurCourant;
    private final ApplicationEventPublisher evenements;

    SignalementService(SignalementRepository signalements, LecteurBaux baux, LecteurLots lots, LecteurTiers tiers, StockageService stockage,
            ActeurCourant acteurCourant, ApplicationEventPublisher evenements) {
        this.signalements = signalements;
        this.baux = baux;
        this.lots = lots;
        this.tiers = tiers;
        this.stockage = stockage;
        this.acteurCourant = acteurCourant;
        this.evenements = evenements;
    }

    /**
     * @param bailId obligatoire seulement si le locataire a plusieurs baux en cours
     * @param photo  facultative (jpeg, png, webp ; redimensionnée par le module stockage)
     */
    @Transactional
    public SignalementDto creer(UUID bailId, String descriptionSaisie, String nomPhoto, InputStream photo) {
        UUID locataire = locataireActeur().orElseThrow(() -> new ErreurMetier("ACCES_REFUSE", "Réservé aux locataires", HttpStatus.FORBIDDEN));
        String description = descriptionSaisie == null ? "" : descriptionSaisie.strip();
        if (description.length() < 5 || description.length() > 2000) {
            throw new ErreurMetier("SIGNALEMENT_DESCRIPTION_INVALIDE", "Décrivez la panne en 5 à 2000 caractères", HttpStatus.BAD_REQUEST);
        }
        List<BailResume> enCours = baux.bauxDuLocataire(locataire).stream().filter(b -> "ACTIF".equals(b.statut()) || "RESILIE".equals(b.statut())).toList();
        BailResume bail;
        if (bailId != null) {
            bail = enCours.stream().filter(b -> b.id().equals(bailId)).findFirst()
                    .orElseThrow(() -> new ErreurMetier("BAIL_INTROUVABLE", "Bail introuvable", HttpStatus.NOT_FOUND));
        } else if (enCours.isEmpty()) {
            throw new ErreurMetier("SIGNALEMENT_AUCUN_BAIL", "Vous n'avez aucun bail en cours", HttpStatus.CONFLICT);
        } else if (enCours.size() > 1) {
            throw new ErreurMetier("SIGNALEMENT_BAIL_REQUIS", "Précisez le bail concerné", HttpStatus.CONFLICT);
        } else {
            bail = enCours.get(0);
        }
        if (signalements.countByLocataireIdAndStatut(locataire, StatutSignalement.NOUVEAU) >= MAX_OUVERTS) {
            throw new ErreurMetier("SIGNALEMENT_LIMITE", "Trop de signalements en attente : attendez leur prise en charge", HttpStatus.TOO_MANY_REQUESTS);
        }
        Signalement s = new Signalement(bail.id(), bail.lotId(), locataire, description);
        if (photo != null) {
            FichierDto fichier = stockage.enregistrer("maintenance", "signalements", s.getId(), nomPhoto, photo, false);
            s.attacherPhoto(fichier.id());
        }
        signalements.saveAndFlush(s);
        evenements.publishEvent(new EvenementsMaintenance.SignalementCree(s.getId(), bail.id(), bail.lotId(), s.getFichierId() != null, auteur()));
        return dto(s, new HashMap<>(), new HashMap<>(), new HashMap<>());
    }

    @Transactional(readOnly = true)
    public Page<SignalementDto> lister(StatutSignalement statut, UUID bailId, Pageable pageable) {
        UUID impose = locataireActeur().orElse(null);
        boolean locataire = acteurCourant.acteur().filter(a -> a.aLeRole("LOCATAIRE")).isPresent();
        UUID filtreLocataire = locataire ? (impose == null ? new UUID(0, 0) : impose) : null;
        Specification<Signalement> spec = (r, q, cb) -> {
            List<Predicate> c = new ArrayList<>();
            if (filtreLocataire != null) {
                c.add(cb.equal(r.get("locataireId"), filtreLocataire));
            }
            if (statut != null) {
                c.add(cb.equal(r.get("statut"), statut));
            }
            if (bailId != null) {
                c.add(cb.equal(r.get("bailId"), bailId));
            }
            return cb.and(c.toArray(Predicate[]::new));
        };
        Page<Signalement> page = signalements.findAll(spec, pageable);
        Map<UUID, BailResume> cacheBaux = new HashMap<>();
        Map<UUID, LotResume> cacheLots = new HashMap<>();
        Map<UUID, String> cacheNoms = new HashMap<>();
        return new PageImpl<>(page.getContent().stream().map(s -> dto(s, cacheBaux, cacheLots, cacheNoms)).toList(), pageable, page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public SignalementDto obtenir(UUID id) {
        Signalement s = charger(id);
        UUID impose = locataireActeur().orElse(null);
        boolean locataire = acteurCourant.acteur().filter(a -> a.aLeRole("LOCATAIRE")).isPresent();
        if (locataire && !s.getLocataireId().equals(impose)) {
            throw new ErreurMetier("SIGNALEMENT_INTROUVABLE", "Signalement introuvable", HttpStatus.NOT_FOUND);
        }
        return dto(s, new HashMap<>(), new HashMap<>(), new HashMap<>());
    }

    /** NOUVEAU → EN_COURS, RESOLU ou REJETE ; EN_COURS → RESOLU ou REJETE. Les états terminaux sont définitifs. */
    @Transactional
    public SignalementDto changerStatut(UUID id, StatutSignalement vers, String noteSaisie) {
        Signalement s = charger(id);
        StatutSignalement de = s.getStatut();
        if (de.terminal() || vers == StatutSignalement.NOUVEAU || vers == de) {
            throw new ErreurMetier("SIGNALEMENT_TRANSITION_INVALIDE", "Transition impossible de %s vers %s".formatted(de, vers), HttpStatus.CONFLICT);
        }
        String note = noteSaisie == null || noteSaisie.isBlank() ? null : noteSaisie.strip();
        if (note != null && note.length() > 1000) {
            throw new ErreurMetier("CHAMP_TROP_LONG", "Note trop longue (1000 caractères au plus)", HttpStatus.BAD_REQUEST);
        }
        s.traiter(vers, note);
        evenements.publishEvent(new EvenementsMaintenance.SignalementTraite(id, de.name(), vers.name(), auteur()));
        return dto(s, new HashMap<>(), new HashMap<>(), new HashMap<>());
    }

    // ------------------------------------------------------------------

    private Signalement charger(UUID id) {
        return signalements.findById(id).orElseThrow(() -> new ErreurMetier("SIGNALEMENT_INTROUVABLE", "Signalement introuvable", HttpStatus.NOT_FOUND));
    }

    private java.util.Optional<UUID> locataireActeur() {
        return acteurCourant.acteur().filter(a -> a.aLeRole("LOCATAIRE")).flatMap(a -> tiers.locataireDeUtilisateur(a.id()));
    }

    private String auteur() {
        return acteurCourant.acteur().map(a -> a.id().toString()).orElse("systeme");
    }

    private SignalementDto dto(Signalement s, Map<UUID, BailResume> cacheBaux, Map<UUID, LotResume> cacheLots, Map<UUID, String> cacheNoms) {
        BailResume bail = cacheBaux.computeIfAbsent(s.getBailId(), id -> baux.bail(id).orElse(null));
        LotResume lot = cacheLots.computeIfAbsent(s.getLotId(), id -> lots.lot(id).orElse(null));
        String nom = cacheNoms.computeIfAbsent(s.getLocataireId(), id -> tiers.locataire(id).map(l -> l.nom()).orElse(null));
        return new SignalementDto(s.getId(), s.getBailId(), bail == null ? null : bail.numero(), s.getLotId(),
                lot == null ? null : lot.immeubleNom() + " — " + lot.reference(), s.getLocataireId(), nom, s.getDescription(), s.getStatut(),
                s.getFichierId(), s.getNoteGestion(), s.getCreatedAt());
    }
}
