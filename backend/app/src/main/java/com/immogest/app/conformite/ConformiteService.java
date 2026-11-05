package com.immogest.app.conformite;

import static com.immogest.app.conformite.Dtos.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.immogest.app.bail.api.BailResume;
import com.immogest.app.bail.api.LecteurBaux;
import com.immogest.app.finance.EcheanceService;
import com.immogest.app.patrimoine.FiltreImmeubles;
import com.immogest.app.patrimoine.PatrimoineService;
import com.immogest.app.shared.ActeurCourant;
import com.immogest.app.shared.ErreurMetier;
import com.immogest.app.shared.PersonneAnonymisee;
import com.immogest.app.tiers.AnonymisationService;
import java.io.IOException;
import java.io.InputStream;
import java.time.Clock;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Consentements, registre des traitements, demandes d'anonymisation. Une anonymisation n'est jamais automatique : elle est demandée, ses obstacles
 * (bail en cours, impayés, biens gérés) sont exposés, puis un ADMIN l'exécute ou la refuse avec un motif.
 */
@Service
public class ConformiteService {

    /** Version du texte des modèles : à changer quand un modèle change (le consentement mémorise la version signée). */
    static final String VERSION_MODELES = "1.0";
    private static final List<String> CONSERVE = List.of(
            "Baux, quittances, reçus, mises en demeure et relevés de gérance déjà émis (documents comptables : obligation légale de conservation de 10 ans)",
            "Écritures financières (échéances, paiements, imputations, contre-passations), rattachées à une fiche anonymisée",
            "Journal d'audit (auteur, action, date), dont les données personnelles ont été remplacées par « ANONYME »");

    private final JdbcTemplate jdbc;
    private final AnonymisationService anonymisation;
    private final LecteurBaux baux;
    private final EcheanceService echeances;
    private final PatrimoineService patrimoine;
    private final ActeurCourant acteur;
    private final ApplicationEventPublisher evenements;
    private final Clock horloge;
    private final ObjectMapper mapper;

    ConformiteService(JdbcTemplate jdbc, AnonymisationService anonymisation, LecteurBaux baux, EcheanceService echeances, PatrimoineService patrimoine, ActeurCourant acteur,
            ApplicationEventPublisher evenements, Clock horloge, ObjectMapper mapper) {
        this.jdbc = jdbc;
        this.anonymisation = anonymisation;
        this.baux = baux;
        this.echeances = echeances;
        this.patrimoine = patrimoine;
        this.acteur = acteur;
        this.evenements = evenements;
        this.horloge = horloge;
        this.mapper = mapper;
    }

    private String par() {
        return acteur.acteur().map(a -> a.id().toString()).orElse("inconnu");
    }

    // ------------------------------------------------------------------ registre et modèles

    public RegistreDto registre() {
        return lire("conformite/registre-traitements.json", RegistreDto.class);
    }

    /** @param langue fr (défaut) ou en */
    public List<ModeleConsentementDto> modeles(String langue) {
        String l = "en".equalsIgnoreCase(langue) ? "en" : "fr";
        return lire("conformite/modeles-consentement_" + l + ".json", new TypeReference<List<ModeleConsentementDto>>() {
        });
    }

    private <T> T lire(String chemin, Class<T> type) {
        try (InputStream in = new ClassPathResource(chemin).getInputStream()) {
            return mapper.readValue(in, type);
        } catch (IOException e) {
            throw new IllegalStateException("Ressource illisible : " + chemin, e);
        }
    }

    private <T> T lire(String chemin, TypeReference<T> type) {
        try (InputStream in = new ClassPathResource(chemin).getInputStream()) {
            return mapper.readValue(in, type);
        } catch (IOException e) {
            throw new IllegalStateException("Ressource illisible : " + chemin, e);
        }
    }

    // ------------------------------------------------------------------ consentements

    private void exigerPersonne(TypePersonne type, UUID id) {
        if (id == null || !anonymisation.existe(type.name(), id)) {
            throw new ErreurMetier("PERSONNE_INTROUVABLE", "Fiche introuvable", HttpStatus.NOT_FOUND);
        }
    }

    private static final RowMapper<ConsentementDto> CONSENTEMENT = (rs, i) -> new ConsentementDto(rs.getObject("id", UUID.class), Finalite.valueOf(rs.getString("finalite")), rs.getBoolean("accorde"),
            rs.getString("version_modele"), ModeRecueil.valueOf(rs.getString("mode_recueil")), rs.getString("reference_document"), rs.getString("recueilli_par"),
            rs.getObject("decide_le", OffsetDateTime.class));

    /** Enregistre une décision (accord, refus ou retrait). Ajout seul : on ne modifie jamais un consentement passé. */
    @Transactional
    public ConsentementDto enregistrer(ConsentementSaisie s) {
        if (s == null || s.type() == null || s.finalite() == null || s.modeRecueil() == null) {
            throw new ErreurMetier("CONSENTEMENT_INCOMPLET", "Type de personne, finalité et mode de recueil sont obligatoires", HttpStatus.BAD_REQUEST);
        }
        exigerPersonne(s.type(), s.personneId());
        if (anonymisation.dejaAnonymisee(s.type().name(), s.personneId())) {
            throw new ErreurMetier("PERSONNE_DEJA_ANONYMISEE", "Cette fiche est anonymisée", HttpStatus.CONFLICT);
        }
        String reference = s.referenceDocument() == null || s.referenceDocument().isBlank() ? null : s.referenceDocument().strip();
        if (reference != null && reference.length() > 200) {
            throw new ErreurMetier("CONSENTEMENT_REFERENCE_TROP_LONGUE", "Référence trop longue (200 caractères)", HttpStatus.BAD_REQUEST);
        }
        UUID id = UUID.randomUUID();
        jdbc.update("INSERT INTO conf_consentement (id, personne_type, personne_id, finalite, accorde, version_modele, mode_recueil, reference_document, recueilli_par) VALUES (?,?,?,?,?,?,?,?,?)",
                id, s.type().name(), s.personneId(), s.finalite().name(), s.accorde(), VERSION_MODELES, s.modeRecueil().name(), reference, par());
        return jdbc.queryForObject("SELECT * FROM conf_consentement WHERE id = ?", CONSENTEMENT, id);
    }

    @Transactional(readOnly = true)
    public SituationConsentementDto situation(TypePersonne type, UUID personneId) {
        exigerPersonne(type, personneId);
        List<ConsentementDto> historique = jdbc.query("SELECT * FROM conf_consentement WHERE personne_type = ? AND personne_id = ? ORDER BY decide_le DESC", CONSENTEMENT, type.name(), personneId);
        Map<Finalite, ConsentementDto> dernieres = new EnumMap<>(Finalite.class);
        for (ConsentementDto c : historique) {
            dernieres.putIfAbsent(c.finalite(), c); // historique trié du plus récent au plus ancien
        }
        List<FinaliteSituationDto> finalites = new ArrayList<>();
        for (Finalite f : Finalite.values()) {
            ConsentementDto d = dernieres.get(f);
            finalites.add(new FinaliteSituationDto(f, d == null ? "NON_RECUEILLI" : d.accorde() ? "ACCORDE" : "REFUSE", d));
        }
        return new SituationConsentementDto(type, personneId, finalites, historique);
    }

    // ------------------------------------------------------------------ demandes d'anonymisation

    private static final RowMapper<Object[]> BRUT = (rs, i) -> new Object[] {rs.getObject("id", UUID.class), rs.getString("personne_type"), rs.getObject("personne_id", UUID.class),
            rs.getString("statut"), rs.getString("motif"), rs.getString("motif_refus"), rs.getObject("recue_le", OffsetDateTime.class), rs.getString("recue_par"),
            rs.getObject("traitee_le", OffsetDateTime.class), rs.getString("traitee_par")};

    private DemandeAnonymisationDto dto(Object[] r) {
        TypePersonne type = TypePersonne.valueOf((String) r[1]);
        UUID personne = (UUID) r[2];
        String statut = (String) r[3];
        List<ObstacleDto> obstacles = "RECUE".equals(statut) ? obstacles(type, personne) : List.of();
        return new DemandeAnonymisationDto((UUID) r[0], type, personne, statut, (String) r[4], (String) r[5], (OffsetDateTime) r[6], (String) r[7], (OffsetDateTime) r[8], (String) r[9], obstacles);
    }

    @Transactional
    public DemandeAnonymisationDto demander(DemandeSaisie s) {
        if (s == null || s.type() == null) {
            throw new ErreurMetier("DEMANDE_INCOMPLETE", "Le type de personne est obligatoire", HttpStatus.BAD_REQUEST);
        }
        exigerPersonne(s.type(), s.personneId());
        if (anonymisation.dejaAnonymisee(s.type().name(), s.personneId())) {
            throw new ErreurMetier("PERSONNE_DEJA_ANONYMISEE", "Cette fiche est déjà anonymisée", HttpStatus.CONFLICT);
        }
        String motif = s.motif() == null || s.motif().isBlank() ? null : s.motif().strip();
        if (motif != null && motif.length() > 500) {
            throw new ErreurMetier("DEMANDE_MOTIF_TROP_LONG", "Motif trop long (500 caractères)", HttpStatus.BAD_REQUEST);
        }
        UUID id = UUID.randomUUID();
        try {
            jdbc.update("INSERT INTO conf_demande (id, personne_type, personne_id, statut, motif, recue_par) VALUES (?,?,?,'RECUE',?,?)", id, s.type().name(), s.personneId(), motif, par());
        } catch (org.springframework.dao.DuplicateKeyException e) {
            throw new ErreurMetier("DEMANDE_DEJA_OUVERTE", "Une demande est déjà en cours pour cette personne", HttpStatus.CONFLICT);
        }
        return obtenir(id);
    }

    @Transactional(readOnly = true)
    public DemandeAnonymisationDto obtenir(UUID id) {
        return jdbc.query("SELECT * FROM conf_demande WHERE id = ?", BRUT, id).stream().findFirst().map(this::dto)
                .orElseThrow(() -> new ErreurMetier("DEMANDE_INTROUVABLE", "Demande introuvable", HttpStatus.NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<DemandeAnonymisationDto> lister(String statut) {
        List<Object[]> lignes = statut == null || statut.isBlank()
                ? jdbc.query("SELECT * FROM conf_demande ORDER BY recue_le DESC LIMIT 200", BRUT)
                : jdbc.query("SELECT * FROM conf_demande WHERE statut = ? ORDER BY recue_le DESC LIMIT 200", BRUT, statut);
        return lignes.stream().map(this::dto).toList();
    }

    /** Ce qui empêcherait l'anonymisation aujourd'hui. Une personne qui doit de l'argent, ou qui a un contrat en cours, ne s'efface pas. */
    List<ObstacleDto> obstacles(TypePersonne type, UUID personneId) {
        List<ObstacleDto> o = new ArrayList<>();
        UUID locataire = switch (type) {
            case LOCATAIRE -> personneId;
            case GARANT -> anonymisation.locataireDuGarant(personneId).orElse(null);
            default -> null;
        };
        if (locataire != null) {
            LocalDate aujourdhui = LocalDate.now(horloge.withZone(ZoneId.of("Africa/Douala")));
            for (BailResume b : baux.bauxDuLocataire(locataire)) {
                boolean enVigueur = ("ACTIF".equals(b.statut()) || "RESILIE".equals(b.statut())) && (b.dateFinEffective() == null || !b.dateFinEffective().isBefore(aujourdhui));
                if (enVigueur) {
                    o.add(new ObstacleDto("BAIL_EN_COURS", "Le bail " + b.numero() + " est en cours : attendez sa fin"));
                }
                if (!echeances.arrieresDuBail(b.id()).isEmpty()) {
                    o.add(new ObstacleDto("IMPAYES", "Le bail " + b.numero() + " a des impayés : la créance justifie la conservation des données"));
                }
            }
        }
        if (type == TypePersonne.PROPRIETAIRE) {
            long biens = patrimoine.listerImmeubles(new FiltreImmeubles(null, null, null, false, personneId), PageRequest.of(0, 1)).getTotalElements();
            if (biens > 0) {
                o.add(new ObstacleDto("BIENS_GERES", "Ce propriétaire a encore des immeubles rattachés : réaffectez ou archivez-les d'abord"));
            }
        }
        return o;
    }

    @Transactional
    public ResultatAnonymisationDto executer(UUID demandeId) {
        DemandeAnonymisationDto d = obtenir(demandeId);
        if (!"RECUE".equals(d.statut())) {
            throw new ErreurMetier("DEMANDE_DEJA_TRAITEE", "Cette demande est déjà traitée", HttpStatus.CONFLICT);
        }
        if (!d.obstacles().isEmpty()) {
            throw new ErreurMetier("ANONYMISATION_IMPOSSIBLE", "Anonymisation impossible : " + String.join(" ; ", d.obstacles().stream().map(ObstacleDto::message).toList()), HttpStatus.CONFLICT);
        }
        var r = anonymisation.anonymiser(d.type().name(), d.personneId());
        jdbc.update("UPDATE conf_demande SET statut = 'EXECUTEE', traitee_le = now(), traitee_par = ? WHERE id = ?", par(), demandeId);
        evenements.publishEvent(new PersonneAnonymisee(d.type().name(), d.personneId(), r.utilisateurId(), par()));
        return new ResultatAnonymisationDto(demandeId, r.pieces(), CONSERVE);
    }

    @Transactional
    public DemandeAnonymisationDto refuser(UUID demandeId, String motif) {
        if (motif == null || motif.isBlank()) {
            throw new ErreurMetier("DEMANDE_MOTIF_REQUIS", "Le motif du refus est obligatoire (il sera communiqué à la personne)", HttpStatus.BAD_REQUEST);
        }
        if (motif.strip().length() > 500) {
            throw new ErreurMetier("DEMANDE_MOTIF_TROP_LONG", "Motif trop long (500 caractères)", HttpStatus.BAD_REQUEST);
        }
        DemandeAnonymisationDto d = obtenir(demandeId);
        if (!"RECUE".equals(d.statut())) {
            throw new ErreurMetier("DEMANDE_DEJA_TRAITEE", "Cette demande est déjà traitée", HttpStatus.CONFLICT);
        }
        jdbc.update("UPDATE conf_demande SET statut = 'REFUSEE', motif_refus = ?, traitee_le = now(), traitee_par = ? WHERE id = ?", motif.strip(), par(), demandeId);
        return obtenir(demandeId);
    }
}
