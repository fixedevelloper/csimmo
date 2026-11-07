package com.immogest.app.patrimoine;

import com.immogest.app.licence.api.ControlePalier;
import com.immogest.app.patrimoine.domain.Compteur;
import com.immogest.app.patrimoine.domain.Immeuble;
import com.immogest.app.patrimoine.domain.Lot;
import com.immogest.app.patrimoine.domain.PhotoPatrimoine;
import com.immogest.app.patrimoine.infra.ImmeubleRepository;
import com.immogest.app.patrimoine.infra.LotRepository;
import com.immogest.app.patrimoine.infra.PatrimoineMapper;
import com.immogest.app.patrimoine.infra.PatrimoineSpecifications;
import com.immogest.app.patrimoine.infra.PhotoRepository;
import com.immogest.app.shared.ActeurCourant;
import com.immogest.app.shared.ErreurMetier;
import com.immogest.app.stockage.FichierDto;
import com.immogest.app.stockage.StockageService;
import com.immogest.app.tiers.api.LecteurTiers;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Immeubles, lots et photos. Un lot ne se supprime pas : il s'archive (des baux peuvent le référencer). */
@Service
public class PatrimoineService {

    private static final Logger LOG = LoggerFactory.getLogger(PatrimoineService.class);
    static final int PHOTOS_MAX = 20;
    static final int EQUIPEMENTS_MAX = 50;
    static final int COMPTEURS_MAX = 10;

    private final ImmeubleRepository immeubles;
    private final LotRepository lots;
    private final PhotoRepository photos;
    private final PatrimoineMapper mapper;
    private final ControlePalier palier;
    private final StockageService stockage;
    private final ApplicationEventPublisher evenements;
    private final ActeurCourant acteurCourant;
    private final LecteurTiers tiers;
    private final com.immogest.app.shared.PerimetreAcces perimetre;

    PatrimoineService(ImmeubleRepository immeubles, LotRepository lots, PhotoRepository photos, PatrimoineMapper mapper,
            ControlePalier palier, StockageService stockage, ApplicationEventPublisher evenements,
            ActeurCourant acteurCourant, LecteurTiers tiers, com.immogest.app.shared.PerimetreAcces perimetre) {
        this.immeubles = immeubles;
        this.lots = lots;
        this.photos = photos;
        this.mapper = mapper;
        this.palier = palier;
        this.stockage = stockage;
        this.evenements = evenements;
        this.acteurCourant = acteurCourant;
        this.tiers = tiers;
        this.perimetre = perimetre;
    }

    // ================================================================== immeubles

    @Transactional(readOnly = true)
    public Page<ImmeubleDto> listerImmeubles(FiltreImmeubles filtre, Pageable pageable) {
        FiltreImmeubles f = new FiltreImmeubles(filtre.ville(), filtre.quartier(), filtre.q(), filtre.inclureArchives(),
                proprietaireImpose().orElse(filtre.proprietaireTiersId()));
        Page<Immeuble> page = immeubles.findAll(PatrimoineSpecifications.immeubles(f).and(perimetre.immeublesVisibles().map(PatrimoineSpecifications::immeublesDans).orElse(null)), pageable);
        Map<UUID, int[]> comptes = comptes(page.getContent().stream().map(Immeuble::getId).toList());
        return page.map(i -> avecComptes(mapper.versDto(i), comptes));
    }

    @Transactional(readOnly = true)
    public ImmeubleDto obtenirImmeuble(UUID id) {
        Immeuble immeuble = chargerImmeubleVisible(id);
        return avecComptes(mapper.versDto(immeuble), comptes(List.of(id)));
    }

    @Transactional
    public ImmeubleDto creerImmeuble(ImmeubleSaisie saisie) {
        ImmeubleSaisie s = normaliser(saisie);
        verifierProprietaire(s.proprietaireTiersId());
        Immeuble immeuble = immeubles.save(new Immeuble(s.nom(), s.adresse(), s.quartier(), s.ville(), s.latitude(),
                s.longitude(), s.description(), s.proprietaireTiersId()));
        publier("PATRIMOINE.IMMEUBLE_CREE", "immeuble", immeuble.getId(), null, resume(immeuble));
        return mapper.versDto(immeuble);
    }

    @Transactional
    public ImmeubleDto modifierImmeuble(UUID id, ImmeubleSaisie saisie) {
        Immeuble immeuble = chargerImmeubleActif(id);
        ImmeubleSaisie s = normaliser(saisie);
        verifierProprietaire(s.proprietaireTiersId());
        Map<String, Object> avant = resume(immeuble);
        immeuble.modifier(s.nom(), s.adresse(), s.quartier(), s.ville(), s.latitude(), s.longitude(), s.description(),
                s.proprietaireTiersId());
        publier("PATRIMOINE.IMMEUBLE_MODIFIE", "immeuble", id, avant, resume(immeuble));
        return avecComptes(mapper.versDto(immeuble), comptes(List.of(id)));
    }

    /** Archive l'immeuble ; refusé tant qu'il contient des lots non archivés. */
    @Transactional
    public void archiverImmeuble(UUID id) {
        Immeuble immeuble = chargerImmeubleActif(id);
        if (lots.countByImmeubleIdAndArchiveFalse(id) > 0) {
            throw new ErreurMetier("IMMEUBLE_LOTS_ACTIFS", "Archivez d'abord les lots de cet immeuble", HttpStatus.CONFLICT);
        }
        Map<String, Object> avant = resume(immeuble);
        immeuble.archiver();
        publier("PATRIMOINE.IMMEUBLE_ARCHIVE", "immeuble", id, avant, resume(immeuble));
    }

    // ================================================================== lots

    @Transactional(readOnly = true)
    public Page<LotDto> listerLots(FiltreLots filtre, Pageable pageable) {
        FiltreLots f = new FiltreLots(filtre.immeubleId(), filtre.statut(), filtre.type(), filtre.ville(), filtre.quartier(),
                filtre.q(), filtre.inclureArchives(), proprietaireImpose().orElse(filtre.proprietaireTiersId()));
        return lots.findAll(PatrimoineSpecifications.lots(f).and(perimetre.immeublesVisibles().map(PatrimoineSpecifications::lotsDansImmeubles).orElse(null)), pageable).map(mapper::versDto);
    }

    @Transactional(readOnly = true)
    public LotDto obtenirLot(UUID id) {
        Lot lot = chargerLot(id);
        if (!visible(lot.getImmeuble())) {
            throw new ErreurMetier("LOT_INTROUVABLE", "Lot introuvable", HttpStatus.NOT_FOUND);
        }
        return mapper.versDto(lot);
    }

    /** Crée un lot après contrôle du palier de la licence (409 {@code LICENCE_PALIER_ATTEINT}). */
    @Transactional
    public LotDto creerLot(LotSaisie saisie) {
        if (saisie.immeubleId() == null) {
            throw new ErreurMetier("LOT_IMMEUBLE_REQUIS", "L'immeuble du lot est obligatoire", HttpStatus.BAD_REQUEST);
        }
        LotSaisie s = normaliser(saisie);
        Immeuble immeuble = chargerImmeubleActif(s.immeubleId());
        lots.verrouillerCreation();
        palier.verifierCreationLot(lots.countByArchiveFalse());
        if (lots.existsByImmeubleIdAndReference(immeuble.getId(), s.reference())) {
            throw referenceExistante();
        }
        Lot lot = new Lot(immeuble, s.reference(), s.type(), s.surfaceM2(), s.pieces(), s.loyerIndicatif());
        appliquerDetails(lot, s);
        lots.saveAndFlush(lot);
        publier("PATRIMOINE.LOT_CREE", "lot", lot.getId(), null, resume(lot));
        return mapper.versDto(lot);
    }

    @Transactional
    public LotDto modifierLot(UUID id, LotSaisie saisie) {
        Lot lot = chargerLotActif(id);
        if (saisie.immeubleId() != null && !saisie.immeubleId().equals(lot.getImmeuble().getId())) {
            throw new ErreurMetier("LOT_IMMEUBLE_NON_MODIFIABLE", "Un lot ne change pas d'immeuble", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        LotSaisie s = normaliser(saisie);
        if (lots.existsByImmeubleIdAndReferenceAndIdNot(lot.getImmeuble().getId(), s.reference(), id)) {
            throw referenceExistante();
        }
        Map<String, Object> avant = resume(lot);
        lot.modifier(s.reference(), s.type(), s.surfaceM2(), s.pieces(), s.loyerIndicatif());
        appliquerDetails(lot, s);
        lots.flush();
        publier("PATRIMOINE.LOT_MODIFIE", "lot", id, avant, resume(lot));
        return mapper.versDto(lot);
    }

    /** Passe un lot libre en travaux ou l'inverse. OCCUPE vient exclusivement du bail. */
    @Transactional
    public LotDto changerStatut(UUID id, StatutLot cible) {
        Lot lot = chargerLotActif(id);
        Map<String, Object> avant = resume(lot);
        lot.changerStatutManuellement(cible);
        publier("PATRIMOINE.LOT_STATUT_CHANGE", "lot", id, avant, resume(lot));
        return mapper.versDto(lot);
    }

    @Transactional
    public void archiverLot(UUID id) {
        Lot lot = chargerLotActif(id);
        Map<String, Object> avant = resume(lot);
        lot.archiver();
        publier("PATRIMOINE.LOT_ARCHIVE", "lot", id, avant, resume(lot));
    }

    // ================================================================== statut suivant le bail (événements)

    @Transactional
    public void marquerOccupe(UUID lotId) {
        lots.findById(lotId).ifPresentOrElse(lot -> {
            Map<String, Object> avant = resume(lot);
            if (lot.marquerOccupe()) {
                publier("PATRIMOINE.LOT_OCCUPE", "lot", lotId, avant, resume(lot));
            }
        }, () -> LOG.warn("BailActive reçu pour un lot inconnu : {}", lotId));
    }

    @Transactional
    public void marquerLibre(UUID lotId) {
        lots.findById(lotId).ifPresentOrElse(lot -> {
            Map<String, Object> avant = resume(lot);
            if (lot.marquerLibre()) {
                publier("PATRIMOINE.LOT_LIBERE", "lot", lotId, avant, resume(lot));
            }
        }, () -> LOG.warn("BailTermine reçu pour un lot inconnu : {}", lotId));
    }

    // ================================================================== photos

    @Transactional(readOnly = true)
    public List<PhotoDto> listerPhotos(CibleTypePhoto type, UUID cibleId) {
        verifierCible(type, cibleId, false);
        return photos.findByCibleTypeAndCibleIdOrderByOrdreAscCreatedAtAsc(type, cibleId).stream().map(mapper::versDto).toList();
    }

    /**
     * Ajoute une photo : le contenu est validé (jpeg, png, webp ; 10 Mo ; type vérifié sur le contenu), redimensionné
     * à 1600 px puis stocké dans le bucket S3 par le module stockage.
     */
    @Transactional
    public PhotoDto ajouterPhoto(CibleTypePhoto type, UUID cibleId, String nomFichier, InputStream contenu, String legende) {
        verifierCible(type, cibleId, true);
        long existantes = photos.countByCibleTypeAndCibleId(type, cibleId);
        if (existantes >= PHOTOS_MAX) {
            throw new ErreurMetier("PHOTOS_LIMITE_ATTEINTE", "Au plus %d photos par fiche".formatted(PHOTOS_MAX), HttpStatus.CONFLICT);
        }
        FichierDto fichier = stockage.enregistrer("patrimoine", type.name().toLowerCase(), cibleId, nomFichier, contenu, false);
        if (!fichier.typeMime().startsWith("image/")) {
            // Le stockage accepte aussi les PDF ; une photo doit être une image
            stockage.supprimer(fichier.id());
            throw new ErreurMetier("PHOTO_TYPE_INTERDIT", "Une photo doit être une image (jpeg, png ou webp)",
                    HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }
        try {
            PhotoPatrimoine photo = photos.saveAndFlush(new PhotoPatrimoine(type, cibleId, fichier.id(), texte(legende, 200), (int) existantes));
            publier("PATRIMOINE.PHOTO_AJOUTEE", type.name().toLowerCase(), cibleId, null, Map.of("photoId", photo.getId().toString()));
            return mapper.versDto(photo);
        } catch (RuntimeException e) {
            stockage.supprimer(fichier.id());
            throw e;
        }
    }

    @Transactional
    public void supprimerPhoto(UUID photoId) {
        PhotoPatrimoine photo = photos.findById(photoId).orElseThrow(() ->
                new ErreurMetier("PHOTO_INTROUVABLE", "Photo introuvable", HttpStatus.NOT_FOUND));
        photos.delete(photo);
        stockage.supprimer(photo.getFichierId());
        publier("PATRIMOINE.PHOTO_SUPPRIMEE", photo.getCibleType().name().toLowerCase(), photo.getCibleId(),
                Map.of("photoId", photoId.toString()), null);
    }

    // ================================================================== interne

    private Immeuble chargerImmeuble(UUID id) {
        return immeubles.findById(id).orElseThrow(() ->
                new ErreurMetier("IMMEUBLE_INTROUVABLE", "Immeuble introuvable", HttpStatus.NOT_FOUND));
    }

    private Immeuble chargerImmeubleActif(UUID id) {
        Immeuble immeuble = chargerImmeuble(id);
        if (immeuble.isArchive()) {
            throw new ErreurMetier("IMMEUBLE_ARCHIVE", "Cet immeuble est archivé", HttpStatus.CONFLICT);
        }
        return immeuble;
    }

    private Lot chargerLot(UUID id) {
        return lots.findById(id).orElseThrow(() -> new ErreurMetier("LOT_INTROUVABLE", "Lot introuvable", HttpStatus.NOT_FOUND));
    }

    private Lot chargerLotActif(UUID id) {
        Lot lot = chargerLot(id);
        if (lot.isArchive()) {
            throw new ErreurMetier("LOT_ARCHIVE", "Ce lot est archivé", HttpStatus.CONFLICT);
        }
        return lot;
    }

    private void verifierCible(CibleTypePhoto type, UUID id, boolean actifSeulement) {
        if (type == CibleTypePhoto.IMMEUBLE) {
            if (actifSeulement) {
                chargerImmeubleActif(id);
            } else {
                chargerImmeubleVisible(id);
            }
        } else if (actifSeulement) {
            chargerLotActif(id);
        } else {
            obtenirLot(id); // 404 si le lot n'existe pas ou n'est pas visible par cet utilisateur
        }
    }

    // ------------------------------------------------------------------ visibilité par rôle

    private static final UUID AUCUN_PROPRIETAIRE = new UUID(0, 0);

    /**
     * Pour un PROPRIETAIRE_TIERS : son identifiant de propriétaire (ou un identifiant impossible s'il n'a pas de fiche : il ne
     * voit alors rien). Vide pour ADMIN et GESTIONNAIRE, qui voient tout.
     */
    private java.util.Optional<UUID> proprietaireImpose() {
        return acteurCourant.acteur().filter(a -> a.aLeRole("PROPRIETAIRE_TIERS"))
                .map(a -> tiers.proprietaireDeUtilisateur(a.id()).orElse(AUCUN_PROPRIETAIRE));
    }

    private boolean visible(Immeuble immeuble) {
        return proprietaireImpose().map(p -> p.equals(immeuble.getProprietaireTiersId())).orElse(true)
                && perimetre.immeublesVisibles().map(ids -> ids.contains(immeuble.getId())).orElse(true);
    }

    /** Un immeuble qu'on n'a pas le droit de voir est « introuvable » (on ne révèle pas son existence). */
    private Immeuble chargerImmeubleVisible(UUID id) {
        Immeuble immeuble = chargerImmeuble(id);
        if (!visible(immeuble)) {
            throw new ErreurMetier("IMMEUBLE_INTROUVABLE", "Immeuble introuvable", HttpStatus.NOT_FOUND);
        }
        return immeuble;
    }

    private void verifierProprietaire(UUID proprietaireId) {
        if (proprietaireId != null && !tiers.proprietaireActif(proprietaireId)) {
            throw new ErreurMetier("PROPRIETAIRE_INTROUVABLE", "Propriétaire introuvable ou archivé", HttpStatus.NOT_FOUND);
        }
    }

    private Map<UUID, int[]> comptes(List<UUID> ids) {
        Map<UUID, int[]> resultat = new LinkedHashMap<>();
        if (ids.isEmpty()) {
            return resultat;
        }
        for (LotRepository.CompteStatut c : lots.compterParImmeuble(ids)) {
            int[] t = resultat.computeIfAbsent(c.getImmeubleId(), k -> new int[3]);
            t[switch (c.getStatut()) {
                case LIBRE -> 0;
                case OCCUPE -> 1;
                case EN_TRAVAUX -> 2;
            }] = (int) c.getNombre();
        }
        return resultat;
    }

    private static ImmeubleDto avecComptes(ImmeubleDto dto, Map<UUID, int[]> comptes) {
        int[] t = comptes.getOrDefault(dto.id(), new int[3]);
        return dto.avecComptes(t[0], t[1], t[2]);
    }

    private void appliquerDetails(Lot lot, LotSaisie s) {
        lot.remplacerEquipements(new LinkedHashSet<>(s.equipements()));
        lot.remplacerCompteurs(s.compteurs().stream().map(c -> new Compteur(c.type(), c.numero())).toList());
    }

    private static ErreurMetier referenceExistante() {
        return new ErreurMetier("LOT_REFERENCE_EXISTANTE", "Un lot de cet immeuble porte déjà cette référence", HttpStatus.CONFLICT);
    }

    // ------------------------------------------------------------------ normalisation et contrôles

    private static ImmeubleSaisie normaliser(ImmeubleSaisie s) {
        String nom = exiger(s.nom(), "nom", 200);
        String adresse = exiger(s.adresse(), "adresse", 300);
        String ville = exiger(s.ville(), "ville", 100);
        BigDecimal lat = s.latitude();
        BigDecimal lon = s.longitude();
        if ((lat == null) != (lon == null)) {
            throw new ErreurMetier("IMMEUBLE_GPS_INCOMPLET", "Latitude et longitude vont ensemble", HttpStatus.BAD_REQUEST);
        }
        if (lat != null && (lat.abs().compareTo(BigDecimal.valueOf(90)) > 0 || lon.abs().compareTo(BigDecimal.valueOf(180)) > 0)) {
            throw new ErreurMetier("IMMEUBLE_GPS_INVALIDE", "Coordonnées GPS hors limites", HttpStatus.BAD_REQUEST);
        }
        return new ImmeubleSaisie(nom, adresse, texte(s.quartier(), 100), ville, lat == null ? null : lat.setScale(6, java.math.RoundingMode.HALF_UP),
                lon == null ? null : lon.setScale(6, java.math.RoundingMode.HALF_UP), texte(s.description(), 2000), s.proprietaireTiersId());
    }

    private static LotSaisie normaliser(LotSaisie s) {
        String reference = exiger(s.reference(), "reference", 50);
        if (s.type() == null) {
            throw new ErreurMetier("LOT_TYPE_REQUIS", "Le type du lot est obligatoire", HttpStatus.BAD_REQUEST);
        }
        if (s.surfaceM2() != null && s.surfaceM2().signum() <= 0) {
            throw new ErreurMetier("LOT_SURFACE_INVALIDE", "La surface doit être positive", HttpStatus.BAD_REQUEST);
        }
        if (s.pieces() != null && (s.pieces() < 0 || s.pieces() > 100)) {
            throw new ErreurMetier("LOT_PIECES_INVALIDE", "Nombre de pièces invalide", HttpStatus.BAD_REQUEST);
        }
        if (s.loyerIndicatif() != null && s.loyerIndicatif() < 0) {
            throw new ErreurMetier("LOT_LOYER_INVALIDE", "Le loyer ne peut pas être négatif", HttpStatus.BAD_REQUEST);
        }
        Set<String> equipements = (s.equipements() == null ? List.<String>of() : s.equipements()).stream()
                .map(e -> texte(e, 100)).filter(e -> e != null).collect(Collectors.toCollection(LinkedHashSet::new));
        if (equipements.size() > EQUIPEMENTS_MAX) {
            throw new ErreurMetier("LOT_TROP_D_EQUIPEMENTS", "Trop d'équipements", HttpStatus.BAD_REQUEST);
        }
        List<LotSaisie.CompteurSaisie> compteurs = new ArrayList<>();
        for (LotSaisie.CompteurSaisie c : s.compteurs() == null ? List.<LotSaisie.CompteurSaisie>of() : s.compteurs()) {
            if (c.type() == null || texte(c.numero(), 50) == null) {
                throw new ErreurMetier("LOT_COMPTEUR_INVALIDE", "Un compteur a un type et un numéro", HttpStatus.BAD_REQUEST);
            }
            compteurs.add(new LotSaisie.CompteurSaisie(c.type(), texte(c.numero(), 50)));
        }
        if (compteurs.size() > COMPTEURS_MAX) {
            throw new ErreurMetier("LOT_TROP_DE_COMPTEURS", "Trop de compteurs", HttpStatus.BAD_REQUEST);
        }
        return new LotSaisie(s.immeubleId(), reference, s.type(), s.surfaceM2(), s.pieces(), s.loyerIndicatif(),
                new ArrayList<>(equipements), compteurs);
    }

    private static String exiger(String valeur, String champ, int max) {
        String t = texte(valeur, max);
        if (t == null) {
            throw new ErreurMetier("CHAMP_REQUIS", "Le champ " + champ + " est obligatoire", HttpStatus.BAD_REQUEST);
        }
        return t;
    }

    private static String texte(String valeur, int max) {
        if (valeur == null || valeur.isBlank()) {
            return null;
        }
        String t = valeur.strip();
        if (t.length() > max) {
            throw new ErreurMetier("CHAMP_TROP_LONG", "Texte trop long (max %d caractères)".formatted(max), HttpStatus.BAD_REQUEST);
        }
        return t;
    }

    // ------------------------------------------------------------------ audit

    private Map<String, Object> resume(Immeuble i) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("nom", i.getNom());
        m.put("adresse", i.getAdresse());
        m.put("quartier", i.getQuartier());
        m.put("ville", i.getVille());
        m.put("proprietaireTiersId", i.getProprietaireTiersId() == null ? null : i.getProprietaireTiersId().toString());
        m.put("latitude", i.getLatitude() == null ? null : i.getLatitude().toPlainString());
        m.put("longitude", i.getLongitude() == null ? null : i.getLongitude().toPlainString());
        m.put("archive", i.isArchive());
        return m;
    }

    private Map<String, Object> resume(Lot l) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("immeubleId", l.getImmeuble().getId().toString());
        m.put("reference", l.getReference());
        m.put("type", l.getType().name());
        m.put("statut", l.getStatut().name());
        m.put("surfaceM2", l.getSurfaceM2() == null ? null : l.getSurfaceM2().toPlainString());
        m.put("pieces", l.getPieces());
        m.put("loyerIndicatif", l.getLoyerIndicatif());
        m.put("archive", l.isArchive());
        return m;
    }

    private void publier(String action, String entite, UUID id, Map<String, Object> avant, Map<String, Object> apres) {
        String auteur = acteurCourant.acteur().map(a -> a.id().toString()).orElse("systeme");
        evenements.publishEvent(new EvenementPatrimoine(action, entite, id, avant, apres, auteur));
    }
}
