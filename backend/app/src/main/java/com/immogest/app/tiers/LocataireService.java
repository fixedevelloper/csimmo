package com.immogest.app.tiers;

import com.immogest.app.securite.Role;
import com.immogest.app.shared.ErreurMetier;
import com.immogest.app.stockage.ChiffrementDonnees;
import com.immogest.app.stockage.FichierDto;
import com.immogest.app.stockage.StockageService;
import com.immogest.app.tiers.domain.Garant;
import com.immogest.app.tiers.domain.Locataire;
import com.immogest.app.tiers.domain.PieceIdentite;
import com.immogest.app.tiers.infra.GarantRepository;
import com.immogest.app.tiers.infra.JournalTiers;
import com.immogest.app.tiers.infra.LiensUtilisateur;
import com.immogest.app.tiers.infra.LocataireRepository;
import com.immogest.app.tiers.infra.PieceIdentiteRepository;
import com.immogest.app.tiers.infra.RechercheSpecifications;
import com.immogest.app.tiers.infra.Textes;
import com.immogest.app.tiers.infra.TiersMapper;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Locataires, leurs garants et leurs pièces d'identité. Le numéro de CNI est chiffré (AES-GCM) avant d'être écrit en
 * base ; les fichiers de pièces sont chiffrés avant d'être envoyés au stockage. Dans les listes, le numéro n'est jamais renvoyé.
 */
@Service
public class LocataireService {

    static final int PIECES_MAX = 10;

    private final LocataireRepository locataires;
    private final GarantRepository garants;
    private final PieceIdentiteRepository pieces;
    private final TiersMapper mapper;
    private final ChiffrementDonnees chiffrement;
    private final StockageService stockage;
    private final LiensUtilisateur liens;
    private final JournalTiers journal;

    LocataireService(LocataireRepository locataires, GarantRepository garants, PieceIdentiteRepository pieces, TiersMapper mapper,
            ChiffrementDonnees chiffrement, StockageService stockage, LiensUtilisateur liens, JournalTiers journal) {
        this.locataires = locataires;
        this.garants = garants;
        this.pieces = pieces;
        this.mapper = mapper;
        this.chiffrement = chiffrement;
        this.stockage = stockage;
        this.liens = liens;
        this.journal = journal;
    }

    // ================================================================== locataires

    @Transactional(readOnly = true)
    public Page<LocataireDto> lister(String q, boolean inclureArchives, Pageable pageable) {
        return locataires.findAll(RechercheSpecifications.<Locataire>parNomOuTelephone(q, inclureArchives), pageable)
                .map(mapper::versDto); // numéro de CNI absent des listes
    }

    @Transactional(readOnly = true)
    public LocataireDto obtenir(UUID id) {
        return detail(charger(id));
    }

    @Transactional
    public LocataireDto creer(LocataireSaisie s) {
        Locataire l = locataires.saveAndFlush(construire(s, null));
        journal.publier("TIERS.LOCATAIRE_CREE", "locataire", l.getId(), null, resume(l));
        return detail(l);
    }

    @Transactional
    public LocataireDto modifier(UUID id, LocataireSaisie s) {
        Locataire l = chargerActif(id);
        Map<String, Object> avant = resume(l);
        Locataire n = construire(s, id);
        l.modifier(n.getNom(), n.getProfession(), n.getTelephone(), n.getEmail(), n.getAdresse(), n.getDateNaissance(),
                n.getNumeroCniChiffre(), n.getUtilisateurId(), n.getNotes());
        locataires.flush();
        journal.publier("TIERS.LOCATAIRE_MODIFIE", "locataire", id, avant, resume(l));
        return detail(l);
    }

    @Transactional
    public void archiver(UUID id) {
        Locataire l = chargerActif(id);
        Map<String, Object> avant = resume(l);
        l.archiver();
        journal.publier("TIERS.LOCATAIRE_ARCHIVE", "locataire", id, avant, resume(l));
    }

    // ================================================================== garants

    @Transactional(readOnly = true)
    public List<GarantDto> listerGarants(UUID locataireId) {
        charger(locataireId);
        return garants.findByLocataireIdAndArchiveFalseOrderByNomAsc(locataireId).stream().map(this::detail).toList();
    }

    @Transactional
    public GarantDto creerGarant(UUID locataireId, GarantSaisie s) {
        chargerActif(locataireId);
        Garant g = garants.saveAndFlush(new Garant(locataireId, Textes.exiger(s.nom(), "nom", 200), Textes.optionnel(s.lien(), 100),
                Textes.optionnel(s.profession(), 100), telephoneGarant(s), Textes.optionnel(s.adresse(), 300), chiffrerCni(s.numeroCni())));
        journal.publier("TIERS.GARANT_CREE", "garant", g.getId(), null, resumeGarant(g));
        return detail(g);
    }

    @Transactional
    public GarantDto modifierGarant(UUID id, GarantSaisie s) {
        Garant g = chargerGarantActif(id);
        Map<String, Object> avant = resumeGarant(g);
        g.modifier(Textes.exiger(s.nom(), "nom", 200), Textes.optionnel(s.lien(), 100), Textes.optionnel(s.profession(), 100),
                telephoneGarant(s), Textes.optionnel(s.adresse(), 300), chiffrerCni(s.numeroCni()));
        garants.flush();
        journal.publier("TIERS.GARANT_MODIFIE", "garant", id, avant, resumeGarant(g));
        return detail(g);
    }

    @Transactional
    public void archiverGarant(UUID id) {
        Garant g = chargerGarantActif(id);
        Map<String, Object> avant = resumeGarant(g);
        g.archiver();
        journal.publier("TIERS.GARANT_ARCHIVE", "garant", id, avant, resumeGarant(g));
    }

    // ================================================================== pièces d'identité

    @Transactional(readOnly = true)
    public List<PieceDto> listerPieces(CiblePiece cible, UUID cibleId) {
        verifierCible(cible, cibleId, false);
        return pieces.findByCibleTypeAndCibleIdOrderByCreatedAtAsc(cible, cibleId).stream().map(mapper::versDto).toList();
    }

    /** Valide le fichier (jpeg, png, webp, pdf ; 10 Mo), le chiffre en AES-GCM puis le stocke. */
    @Transactional
    public PieceDto ajouterPiece(CiblePiece cible, UUID cibleId, TypePiece type, String nomFichier, InputStream contenu) {
        verifierCible(cible, cibleId, true);
        if (type == null) {
            throw new ErreurMetier("PIECE_TYPE_REQUIS", "Le type de pièce est obligatoire", HttpStatus.BAD_REQUEST);
        }
        if (pieces.findByCibleTypeAndCibleIdOrderByCreatedAtAsc(cible, cibleId).size() >= PIECES_MAX) {
            throw new ErreurMetier("PIECES_LIMITE_ATTEINTE", "Au plus %d pièces par fiche".formatted(PIECES_MAX), HttpStatus.CONFLICT);
        }
        FichierDto fichier = stockage.enregistrer("tiers", cible.name().toLowerCase(), cibleId, nomFichier, contenu, true);
        try {
            PieceIdentite piece = pieces.saveAndFlush(new PieceIdentite(cible, cibleId, fichier.id(), type));
            journal.publier("TIERS.PIECE_AJOUTEE", cible.name().toLowerCase(), cibleId, null, Map.of("pieceId", piece.getId().toString(), "type", type.name()));
            return mapper.versDto(piece);
        } catch (RuntimeException e) {
            stockage.supprimer(fichier.id());
            throw e;
        }
    }

    @Transactional
    public void supprimerPiece(UUID pieceId) {
        PieceIdentite piece = pieces.findById(pieceId).orElseThrow(() ->
                new ErreurMetier("PIECE_INTROUVABLE", "Pièce introuvable", HttpStatus.NOT_FOUND));
        pieces.delete(piece);
        stockage.supprimer(piece.getFichierId());
        journal.publier("TIERS.PIECE_SUPPRIMEE", piece.getCibleType().name().toLowerCase(), piece.getCibleId(),
                Map.of("pieceId", pieceId.toString()), null);
    }

    // ================================================================== interne

    private Locataire construire(LocataireSaisie s, UUID idCourant) {
        String telephone = Textes.telephone(s.telephone());
        String email = Textes.email(s.email());
        Textes.contactRequis(telephone, email);
        if (s.utilisateurId() != null) {
            liens.verifier(s.utilisateurId(), Role.LOCATAIRE);
            if (locataires.existsByUtilisateurIdAndIdNot(s.utilisateurId(), idCourant == null ? new UUID(0, 0) : idCourant)) {
                throw LiensUtilisateur.dejaLie();
            }
        }
        if (s.dateNaissance() != null && s.dateNaissance().isAfter(java.time.LocalDate.now())) {
            throw new ErreurMetier("LOCATAIRE_DATE_NAISSANCE_INVALIDE", "La date de naissance est dans le futur", HttpStatus.BAD_REQUEST);
        }
        return new Locataire(Textes.exiger(s.nom(), "nom", 200), Textes.optionnel(s.profession(), 100), telephone, email,
                Textes.optionnel(s.adresse(), 300), s.dateNaissance(), chiffrerCni(s.numeroCni()), s.utilisateurId(),
                Textes.optionnel(s.notes(), 2000));
    }

    private static String telephoneGarant(GarantSaisie s) {
        String t = Textes.telephone(s.telephone());
        if (t == null) {
            throw new ErreurMetier("CHAMP_REQUIS", "Le téléphone du garant est obligatoire", HttpStatus.BAD_REQUEST);
        }
        return t;
    }

    private String chiffrerCni(String numero) {
        String clair = Textes.optionnel(numero, 50);
        return clair == null ? null : chiffrement.chiffrer(clair);
    }

    private LocataireDto detail(Locataire l) {
        LocataireDto d = mapper.versDto(l);
        return new LocataireDto(d.id(), d.nom(), d.profession(), d.telephone(), d.email(), d.adresse(), d.dateNaissance(),
                chiffrement.dechiffrer(l.getNumeroCniChiffre()), d.utilisateurId(), d.notes(), d.archive());
    }

    private GarantDto detail(Garant g) {
        GarantDto d = mapper.versDto(g);
        return new GarantDto(d.id(), d.locataireId(), d.nom(), d.lien(), d.profession(), d.telephone(), d.adresse(),
                chiffrement.dechiffrer(g.getNumeroCniChiffre()), d.archive());
    }

    private void verifierCible(CiblePiece cible, UUID id, boolean actifSeulement) {
        if (cible == CiblePiece.LOCATAIRE) {
            if (actifSeulement) {
                chargerActif(id);
            } else {
                charger(id);
            }
        } else if (actifSeulement) {
            chargerGarantActif(id);
        } else {
            chargerGarant(id);
        }
    }

    private Locataire charger(UUID id) {
        return locataires.findById(id).orElseThrow(() -> new ErreurMetier("LOCATAIRE_INTROUVABLE", "Locataire introuvable", HttpStatus.NOT_FOUND));
    }

    private Locataire chargerActif(UUID id) {
        Locataire l = charger(id);
        if (l.isArchive()) {
            throw new ErreurMetier("LOCATAIRE_ARCHIVE", "Ce locataire est archivé", HttpStatus.CONFLICT);
        }
        return l;
    }

    private Garant chargerGarant(UUID id) {
        return garants.findById(id).orElseThrow(() -> new ErreurMetier("GARANT_INTROUVABLE", "Garant introuvable", HttpStatus.NOT_FOUND));
    }

    private Garant chargerGarantActif(UUID id) {
        Garant g = chargerGarant(id);
        if (g.isArchive()) {
            throw new ErreurMetier("GARANT_ARCHIVE", "Ce garant est archivé", HttpStatus.CONFLICT);
        }
        return g;
    }

    /** Pas de téléphone, d'e-mail ni de numéro de CNI dans le journal d'audit. */
    private static Map<String, Object> resume(Locataire l) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("nom", l.getNom());
        m.put("utilisateurId", l.getUtilisateurId() == null ? null : l.getUtilisateurId().toString());
        m.put("archive", l.isArchive());
        return m;
    }

    private static Map<String, Object> resumeGarant(Garant g) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("locataireId", g.getLocataireId().toString());
        m.put("nom", g.getNom());
        m.put("archive", g.isArchive());
        return m;
    }
}
