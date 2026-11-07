package com.immogest.app.finance;

import com.immogest.app.finance.ReversementDtos.DepenseDto;
import com.immogest.app.finance.ReversementDtos.DepenseSaisie;
import com.immogest.app.finance.domain.Depense;
import com.immogest.app.finance.infra.DepenseRepository;
import com.immogest.app.finance.infra.ReversementRepository;
import com.immogest.app.patrimoine.api.ImmeubleResume;
import com.immogest.app.patrimoine.api.LecteurLots;
import com.immogest.app.patrimoine.api.LotResume;
import com.immogest.app.shared.ActeurCourant;
import com.immogest.app.shared.ErreurMetier;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Dépenses imputées à un propriétaire tiers (travaux, gardiennage…), déduites de son reversement du mois de la dépense. Jamais supprimées :
 * annulées avec motif. Un mois dont le reversement est validé est clos : on date alors la dépense dans le mois courant.
 */
@Service
public class DepenseService {

    private final DepenseRepository depenses;
    private final ReversementRepository reversements;
    private final LecteurLots lots;
    private final EcheanceService moteur;
    private final ActeurCourant acteurCourant;
    private final ApplicationEventPublisher evenements;
    private final JdbcTemplate jdbc;
    private final PerimetreReversements perimetre;

    DepenseService(DepenseRepository depenses, ReversementRepository reversements, LecteurLots lots, EcheanceService moteur,
            ActeurCourant acteurCourant, ApplicationEventPublisher evenements, JdbcTemplate jdbc, PerimetreReversements perimetre) {
        this.perimetre = perimetre;
        this.depenses = depenses;
        this.reversements = reversements;
        this.lots = lots;
        this.moteur = moteur;
        this.acteurCourant = acteurCourant;
        this.evenements = evenements;
        this.jdbc = jdbc;
    }

    @Transactional
    public DepenseDto enregistrer(DepenseSaisie s) {
        if (s.immeubleId() == null || s.dateDepense() == null || s.montant() == null) {
            throw new ErreurMetier("DEPENSE_CHAMPS_REQUIS", "Immeuble, date et montant sont obligatoires", HttpStatus.BAD_REQUEST);
        }
        if (s.montant() <= 0) {
            throw new ErreurMetier("DEPENSE_MONTANT_INVALIDE", "Le montant doit être strictement positif (FCFA)", HttpStatus.BAD_REQUEST);
        }
        String nature = s.nature() == null ? "" : s.nature().strip();
        if (nature.isEmpty() || nature.length() > 200) {
            throw new ErreurMetier("DEPENSE_NATURE_INVALIDE", "La nature de la dépense est obligatoire (200 caractères au plus)", HttpStatus.BAD_REQUEST);
        }
        String reference = s.reference() == null || s.reference().isBlank() ? null : s.reference().strip();
        if (reference != null && reference.length() > 100) {
            throw new ErreurMetier("CHAMP_TROP_LONG", "Référence trop longue (100 caractères au plus)", HttpStatus.BAD_REQUEST);
        }
        if (s.dateDepense().isAfter(moteur.aujourdhui())) {
            throw new ErreurMetier("DEPENSE_DATE_FUTURE", "La date de la dépense ne peut pas être dans le futur", HttpStatus.BAD_REQUEST);
        }
        ImmeubleResume immeuble = lots.immeuble(s.immeubleId())
                .orElseThrow(() -> new ErreurMetier("IMMEUBLE_INTROUVABLE", "Immeuble introuvable", HttpStatus.NOT_FOUND));
        if (immeuble.archive()) {
            throw new ErreurMetier("IMMEUBLE_ARCHIVE", "Cet immeuble est archivé", HttpStatus.CONFLICT);
        }
        if (immeuble.proprietaireTiersId() == null) {
            throw new ErreurMetier("DEPENSE_SANS_PROPRIETAIRE", "Cet immeuble n'a pas de propriétaire tiers : rien à imputer", HttpStatus.CONFLICT);
        }
        if (s.lotId() != null) {
            LotResume lot = lots.lot(s.lotId()).orElse(null);
            if (lot == null || !lot.immeubleId().equals(s.immeubleId())) {
                throw new ErreurMetier("DEPENSE_LOT_INVALIDE", "Ce lot n'appartient pas à cet immeuble", HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }
        verrouillerMois(s.dateDepense());
        exigerMoisOuvert(immeuble.proprietaireTiersId(), s.dateDepense());
        UUID moi = acteurCourant.acteur().map(a -> a.id()).orElse(null);
        Depense d = depenses.saveAndFlush(new Depense(s.immeubleId(), s.lotId(), immeuble.proprietaireTiersId(), s.dateDepense(), nature, s.montant(), reference, moi));
        evenements.publishEvent(new EvenementsReversement.DepenseEnregistree(d.getId(), d.getImmeubleId(), d.getProprietaireId(), d.getMontant(),
                moi == null ? "systeme" : moi.toString()));
        return dto(d, immeuble.nom());
    }

    @Transactional
    public DepenseDto annuler(UUID id, String motifSaisi) {
        Depense d = depenses.findById(id).orElseThrow(() -> new ErreurMetier("DEPENSE_INTROUVABLE", "Dépense introuvable", HttpStatus.NOT_FOUND));
        String motif = motifSaisi == null ? "" : motifSaisi.strip();
        if (motif.isEmpty() || motif.length() > 500) {
            throw new ErreurMetier("DEPENSE_MOTIF_REQUIS", "Le motif de l'annulation est obligatoire (500 caractères au plus)", HttpStatus.BAD_REQUEST);
        }
        verrouillerMois(d.getDateDepense());
        if (!Depense.ENREGISTREE.equals(d.getStatut())) {
            throw new ErreurMetier("DEPENSE_DEJA_ANNULEE", "Cette dépense est déjà annulée", HttpStatus.CONFLICT);
        }
        exigerMoisOuvert(d.getProprietaireId(), d.getDateDepense());
        d.annuler(motif);
        String auteur = acteurCourant.acteur().map(a -> a.id().toString()).orElse("systeme");
        evenements.publishEvent(new EvenementsReversement.DepenseAnnulee(id, motif, auteur));
        return dto(d, lots.immeuble(d.getImmeubleId()).map(ImmeubleResume::nom).orElse(null));
    }

    @Transactional(readOnly = true)
    public Page<DepenseDto> lister(UUID immeubleId, UUID proprietaireId, LocalDate mois, Pageable pageable) {
        var visibles = perimetre.immeubles(); // employé restreint : seulement les dépenses des immeubles de son périmètre
        if (visibles.isPresent() && immeubleId != null && !visibles.get().contains(immeubleId)) {
            throw new ErreurMetier("IMMEUBLE_INTROUVABLE", "Immeuble introuvable", HttpStatus.NOT_FOUND);
        }
        Specification<Depense> spec = (r, q, cb) -> {
            List<Predicate> c = new ArrayList<>();
            if (visibles.isPresent()) {
                c.add(visibles.get().isEmpty() ? cb.disjunction() : r.get("immeubleId").in(visibles.get()));
            }
            if (immeubleId != null) {
                c.add(cb.equal(r.get("immeubleId"), immeubleId));
            }
            if (proprietaireId != null) {
                c.add(cb.equal(r.get("proprietaireId"), proprietaireId));
            }
            if (mois != null) {
                c.add(cb.between(r.get("dateDepense"), mois.withDayOfMonth(1), mois.withDayOfMonth(1).plusMonths(1).minusDays(1)));
            }
            return cb.and(c.toArray(Predicate[]::new));
        };
        Page<Depense> page = depenses.findAll(spec, pageable);
        Map<UUID, String> noms = new HashMap<>();
        List<DepenseDto> dtos = page.getContent().stream()
                .map(d -> dto(d, noms.computeIfAbsent(d.getImmeubleId(), i -> lots.immeuble(i).map(ImmeubleResume::nom).orElse(null)))).toList();
        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }

    // ------------------------------------------------------------------

    /** Même verrou que le calcul des reversements du mois : une dépense ne s'insère pas pendant la validation du reversement. */
    void verrouillerMois(LocalDate date) {
        jdbc.queryForObject("select pg_advisory_xact_lock(?)", Object.class, 8_000_000L + date.withDayOfMonth(1).toEpochDay());
    }

    private void exigerMoisOuvert(UUID proprietaireId, LocalDate date) {
        reversements.findByProprietaireIdAndPeriode(proprietaireId, date.withDayOfMonth(1)).filter(r -> !"CALCULE".equals(r.getStatut())).ifPresent(r -> {
            throw new ErreurMetier("REVERSEMENT_MOIS_CLOS", "Le reversement de ce mois est déjà validé : datez la dépense dans le mois courant", HttpStatus.CONFLICT);
        });
    }

    private static DepenseDto dto(Depense d, String immeubleNom) {
        return new DepenseDto(d.getId(), d.getImmeubleId(), immeubleNom, d.getLotId(), d.getProprietaireId(), d.getDateDepense(), d.getNature(),
                d.getMontant(), d.getReference(), d.getStatut(), d.getMotifAnnulation());
    }
}
