package com.immogest.app.importation;

import com.immogest.app.bail.BailSaisie;
import com.immogest.app.bail.BailService;
import com.immogest.app.bail.Periodicite;
import com.immogest.app.finance.RepriseService;
import com.immogest.app.licence.api.ControlePalier;
import com.immogest.app.patrimoine.FiltreImmeubles;
import com.immogest.app.patrimoine.FiltreLots;
import com.immogest.app.patrimoine.ImmeubleSaisie;
import com.immogest.app.patrimoine.LotSaisie;
import com.immogest.app.patrimoine.PatrimoineService;
import com.immogest.app.shared.ActeurCourant;
import com.immogest.app.shared.ErreurMetier;
import com.immogest.app.tiers.LocataireDto;
import com.immogest.app.tiers.LocataireSaisie;
import com.immogest.app.tiers.LocataireService;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Prévisualisation (aucune écriture) et import transactionnel tout ou rien. L'import rejoue l'analyse sur le fichier reçu (pas sur un état gardé
 * entre deux appels) et vérifie son empreinte : on n'importe que ce qui a été prévisualisé. Limite connue : l'activation d'un bail génère son PDF
 * dans le stockage ; si l'import échoue après cela, ces PDF restent orphelins (inoffensifs, mais présents).
 */
@Service
public class ImportService {

    private static final Logger LOG = LoggerFactory.getLogger(ImportService.class);
    private static final long VERROU = 0x494d504f5254L; // « IMPORT » : un seul import à la fois
    private static final int APERCU = 20;

    private final PatrimoineService patrimoine;
    private final LocataireService locataires;
    private final BailService baux;
    private final RepriseService reprise;
    private final ControlePalier palier;
    private final ActeurCourant acteur;
    private final ApplicationEventPublisher evenements;
    private final TransactionTemplate transaction;
    private final JdbcTemplate jdbc;
    private final Clock horloge;

    ImportService(PatrimoineService patrimoine, LocataireService locataires, BailService baux, RepriseService reprise, ControlePalier palier, ActeurCourant acteur,
            ApplicationEventPublisher evenements, PlatformTransactionManager gestionnaire, JdbcTemplate jdbc, Clock horloge) {
        this.patrimoine = patrimoine;
        this.locataires = locataires;
        this.baux = baux;
        this.reprise = reprise;
        this.palier = palier;
        this.acteur = acteur;
        this.evenements = evenements;
        this.transaction = new TransactionTemplate(gestionnaire);
        this.jdbc = jdbc;
        this.horloge = horloge;
    }

    private LocalDate aujourdhui() {
        return LocalDate.now(horloge.withZone(ZoneId.of("Africa/Douala")));
    }

    static String empreinte(byte[] contenu) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(contenu));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    // ------------------------------------------------------------------ existant (lecture seule)

    /** Immeubles, lots et locataires déjà enregistrés, lus à la demande et mis en cache pour la durée d'une analyse. */
    private final class Referentiel implements Analyseur.Existant {
        private Map<String, UUID> immeubles;
        private final Map<String, Map<String, UUID>> lots = new HashMap<>();
        private final Map<String, UUID> locatairesParTelephone = new HashMap<>();
        private final Set<String> telephonesAbsents = new java.util.HashSet<>();

        Map<String, UUID> immeubles() {
            if (immeubles == null) {
                immeubles = new HashMap<>();
                for (int p = 0;; p++) {
                    var page = patrimoine.listerImmeubles(new FiltreImmeubles(null, null, null, false, null), PageRequest.of(p, 200));
                    page.forEach(i -> immeubles.put(Valeurs.cle(i.nom()), i.id()));
                    if (!page.hasNext()) {
                        break;
                    }
                }
            }
            return immeubles;
        }

        Map<String, UUID> lotsParReference(String cleImmeuble) {
            UUID id = immeubles().get(cleImmeuble);
            if (id == null) {
                return null;
            }
            return lots.computeIfAbsent(cleImmeuble, k -> {
                Map<String, UUID> m = new HashMap<>();
                for (int p = 0;; p++) {
                    var page = patrimoine.listerLots(new FiltreLots(id, null, null, null, null, null, false, null), PageRequest.of(p, 200));
                    page.forEach(l -> m.put(Valeurs.cle(l.reference()), l.id()));
                    if (!page.hasNext()) {
                        break;
                    }
                }
                return m;
            });
        }

        long nombreLots() {
            return patrimoine.listerLots(new FiltreLots(null, null, null, null, null, null, false, null), PageRequest.of(0, 1)).getTotalElements();
        }

        UUID locataire(String telephone) {
            if (telephonesAbsents.contains(telephone)) {
                return null;
            }
            UUID connu = locatairesParTelephone.get(telephone);
            if (connu != null) {
                return connu;
            }
            Page<LocataireDto> page = locataires.lister(telephone, false, PageRequest.of(0, 10));
            for (LocataireDto l : page) {
                if (telephone.equals(l.telephone())) {
                    locatairesParTelephone.put(telephone, l.id());
                    return l.id();
                }
            }
            telephonesAbsents.add(telephone);
            return null;
        }

        @Override
        public boolean immeubleExiste(String cleNom) {
            return immeubles().containsKey(cleNom);
        }

        @Override
        public Set<String> lotsDeImmeuble(String cleNom) {
            Map<String, UUID> m = lotsParReference(cleNom);
            return m == null ? null : m.keySet();
        }

        @Override
        public boolean locataireExiste(String telephone) {
            return locataire(telephone) != null;
        }
    }

    // ------------------------------------------------------------------ prévisualisation

    public PrevisualisationDto previsualiser(byte[] contenu, TypeImport typeCsv) {
        Map<TypeImport, LecteurFichier.Feuille> fichier = LecteurFichier.lire(contenu, typeCsv);
        Referentiel ref = new Referentiel();
        Analyseur.Resultat r = new Analyseur(ref, aujourdhui()).analyser(fichier);
        List<ErreurImportDto> erreurs = new ArrayList<>(r.erreurs());

        List<PrevisualisationDto.ResumeFeuilleDto> feuilles = new ArrayList<>();
        Map<String, List<Map<String, String>>> apercu = new LinkedHashMap<>();
        int total = 0;
        for (TypeImport t : TypeImport.values()) {
            LecteurFichier.Feuille f = fichier.get(t);
            if (f == null) {
                continue;
            }
            feuilles.add(new PrevisualisationDto.ResumeFeuilleDto(t, t.feuille(), f.lignes().size()));
            apercu.put(t.name(), f.lignes().stream().limit(APERCU).map(Ligne::valeurs).toList());
            total += f.lignes().size();
        }
        if (total == 0 && erreurs.isEmpty()) {
            erreurs.add(new ErreurImportDto("", 0, null, "FICHIER_VIDE", "Aucune ligne de données trouvée (onglets attendus : Immeubles, Lots, Locataires, Baux, Soldes)"));
        }
        if (erreurs.isEmpty() && !r.plan().lots().isEmpty()) {
            long apres = ref.nombreLots() + r.plan().lots().size();
            int plafond = palier.palierLots();
            if (plafond > 0 && apres > plafond) {
                erreurs.add(new ErreurImportDto(TypeImport.LOTS.feuille(), 0, null, "LICENCE_PALIER_ATTEINT",
                        "L'import porterait le nombre de lots à %d, au-delà du palier de la licence (%d)".formatted(apres, plafond)));
            }
        }
        return new PrevisualisationDto(empreinte(contenu), feuilles, erreurs, apercu, erreurs.isEmpty() && total > 0);
    }

    // ------------------------------------------------------------------ import

    public ResultatImportDto importer(byte[] contenu, TypeImport typeCsv, String empreinteAttendue) {
        if (empreinteAttendue == null || !empreinteAttendue.equalsIgnoreCase(empreinte(contenu))) {
            throw new ErreurMetier("IMPORT_FICHIER_MODIFIE", "Le fichier n'est pas celui qui a été prévisualisé : relancez la prévisualisation", HttpStatus.CONFLICT);
        }
        PrevisualisationDto p = previsualiser(contenu, typeCsv);
        if (!p.importable()) {
            return new ResultatImportDto(false, Map.of(), 0, p.erreurs());
        }
        Map<TypeImport, LecteurFichier.Feuille> fichier = LecteurFichier.lire(contenu, typeCsv);
        Referentiel ref = new Referentiel();
        Plan plan = new Analyseur(ref, aujourdhui()).analyser(fichier).plan();
        String par = acteur.acteur().map(a -> a.id().toString()).orElse("inconnu");
        String sha = empreinte(contenu);

        ResultatImportDto resultat = transaction.execute(status -> {
            jdbc.queryForObject("select pg_advisory_xact_lock(?)", Object.class, VERROU);
            Echec echec = new Echec();
            Map<TypeImport, Integer> crees = new EnumMap<>(TypeImport.class);
            int echeances = 0;
            try {
                Referentiel actuel = new Referentiel(); // relu sous verrou : un import concurrent a pu créer des objets entre-temps
                Map<String, UUID> immeubleIds = new HashMap<>();
                for (Plan.Imm i : plan.immeubles()) {
                    echec.en(TypeImport.IMMEUBLES, i.ligne());
                    immeubleIds.put(Valeurs.cle(i.nom()), patrimoine.creerImmeuble(new ImmeubleSaisie(i.nom(), i.adresse(), i.quartier(), i.ville(), null, null, i.description(), null)).id());
                    crees.merge(TypeImport.IMMEUBLES, 1, Integer::sum);
                }
                Map<String, UUID> lotIds = new HashMap<>();
                for (Plan.Lot l : plan.lots()) {
                    echec.en(TypeImport.LOTS, l.ligne());
                    UUID immeuble = immeubleIds.getOrDefault(l.immeubleCle(), actuel.immeubles().get(l.immeubleCle()));
                    lotIds.put(l.immeubleCle() + "|" + Valeurs.cle(l.reference()),
                            patrimoine.creerLot(new LotSaisie(immeuble, l.reference(), l.type(), l.surface(), l.pieces(), l.loyerIndicatif(), List.of(), List.of())).id());
                    crees.merge(TypeImport.LOTS, 1, Integer::sum);
                }
                Map<String, UUID> locataireIds = new HashMap<>();
                for (Plan.Loc l : plan.locataires()) {
                    echec.en(TypeImport.LOCATAIRES, l.ligne());
                    locataireIds.put(l.telephone(), locataires.creer(new LocataireSaisie(l.nom(), l.profession(), l.telephone(), l.email(), l.adresse(), l.naissance(), l.cni(), null, null)).id());
                    crees.merge(TypeImport.LOCATAIRES, 1, Integer::sum);
                }
                for (Plan.Bail b : plan.baux()) {
                    echec.en(TypeImport.BAUX, b.ligne());
                    UUID lot = lotIds.get(b.cleLot());
                    if (lot == null) {
                        Map<String, UUID> existants = actuel.lotsParReference(b.immeubleCle());
                        lot = existants == null ? null : existants.get(Valeurs.cle(b.lotRef()));
                    }
                    UUID locataire = locataireIds.getOrDefault(b.telephone(), actuel.locataire(b.telephone()));
                    // moisAvance = 0 : l'avance de l'époque est de l'historique, déjà consommée
                    var bail = baux.creer(new BailSaisie(lot, locataire, List.of(), b.debut(), b.fin(), b.loyer(), b.charges(), Periodicite.valueOf(b.periodicite()), b.jourEcheance(),
                            b.caution(), 0, b.preavisMois()));
                    baux.activer(bail.id());
                    crees.merge(TypeImport.BAUX, 1, Integer::sum);
                    Plan.Solde s = plan.soldes().get(b.cleLot());
                    echec.en(s == null ? TypeImport.BAUX : TypeImport.SOLDES, s == null ? b.ligne() : s.ligne());
                    // sans ligne dans « Soldes », le bail est considéré à jour au jour de l'import
                    echeances += reprise.reprendre(bail.id(), s == null ? aujourdhui() : s.dateReprise(), s == null ? 0 : s.solde());
                    if (s != null) {
                        crees.merge(TypeImport.SOLDES, 1, Integer::sum);
                    }
                }
                Map<String, Integer> comptes = new LinkedHashMap<>();
                crees.forEach((t, n) -> comptes.put(t.name(), n));
                comptes.put("ECHEANCES_REPRISES", echeances);
                UUID id = UUID.randomUUID();
                jdbc.update("INSERT INTO imp_journal (id, par, fichier_sha256, comptes) VALUES (?,?,?,?)", id, par, sha, comptes.toString());
                evenements.publishEvent(new EvenementsImport.ImportEffectue(id, par, sha, comptes));
                return new ResultatImportDto(true, crees, echeances, List.of());
            } catch (ErreurMetier e) {
                status.setRollbackOnly();
                return new ResultatImportDto(false, Map.of(), 0, List.of(echec.erreur(e.code(), e.getMessage())));
            } catch (RuntimeException e) {
                status.setRollbackOnly();
                LOG.error("Import interrompu ({}) : {}", e.getClass().getSimpleName(), e.getMessage());
                return new ResultatImportDto(false, Map.of(), 0, List.of(echec.erreur("IMPORT_ERREUR_INTERNE", "Erreur inattendue : rien n'a été importé")));
            }
        });
        return resultat;
    }

    /** Mémorise la ligne en cours de traitement pour rapporter l'erreur au bon endroit. */
    private static final class Echec {
        private TypeImport type = TypeImport.IMMEUBLES;
        private int ligne;

        void en(TypeImport t, int l) {
            this.type = t;
            this.ligne = l;
        }

        ErreurImportDto erreur(String code, String message) {
            return new ErreurImportDto(type.feuille(), ligne, null, code, message);
        }
    }
}
