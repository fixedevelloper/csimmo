package com.immogest.app.tiers;

import com.immogest.app.shared.ErreurMetier;
import com.immogest.app.tiers.infra.GarantRepository;
import com.immogest.app.tiers.infra.JournalTiers;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Anonymisation d'une personne (droit à l'effacement, loi 2024/017). La fiche reste, car baux, quittances et écritures y renvoient par
 * identifiant, mais nom, contacts, adresse, profession, date de naissance, notes et numéro de pièce d'identité sont effacés, les pièces
 * d'identité supprimées (base et stockage), le compte lié détaché. Irréversible. Les conditions d'opportunité (bail en cours, impayés…) sont
 * vérifiées par le module {@code conformite} avant d'appeler ce service ; il ne contrôle que l'existence et l'état de la fiche.
 */
@Service
public class AnonymisationService {

    /** @param utilisateurId compte rattaché à la fiche (à anonymiser aussi), ou null ; @param pieces pièces d'identité supprimées */
    public record Resultat(UUID utilisateurId, int pieces, int garants) {
    }

    private final JdbcTemplate jdbc;
    private final LocataireService locataires;
    private final GarantRepository garants;
    private final JournalTiers journal;

    AnonymisationService(JdbcTemplate jdbc, LocataireService locataires, GarantRepository garants, JournalTiers journal) {
        this.jdbc = jdbc;
        this.locataires = locataires;
        this.garants = garants;
        this.journal = journal;
    }

    private static String table(String type) {
        return switch (type) {
            case "LOCATAIRE" -> "locataire";
            case "GARANT" -> "garant";
            case "PROPRIETAIRE" -> "proprietaire_tiers";
            case "PRESTATAIRE" -> "prestataire";
            default -> throw new ErreurMetier("PERSONNE_TYPE_INCONNU", "Type de personne inconnu : " + type, HttpStatus.BAD_REQUEST);
        };
    }

    /** Vrai si la fiche existe (anonymisée ou non). */
    @Transactional(readOnly = true)
    public boolean existe(String type, UUID id) {
        return jdbc.queryForObject("SELECT count(*) FROM " + table(type) + " WHERE id = ?", Long.class, id) > 0;
    }

    @Transactional(readOnly = true)
    public boolean dejaAnonymisee(String type, UUID id) {
        return jdbc.queryForObject("SELECT count(*) FROM " + table(type) + " WHERE id = ? AND anonymise_le IS NOT NULL", Long.class, id) > 0;
    }

    /** Locataire dont dépend un garant (les obstacles à l'anonymisation d'un garant sont ceux de son locataire). */
    @Transactional(readOnly = true)
    public Optional<UUID> locataireDuGarant(UUID garantId) {
        return jdbc.query("SELECT locataire_id FROM garant WHERE id = ?", (rs, i) -> rs.getObject(1, UUID.class), garantId).stream().findFirst();
    }

    @Transactional
    public Resultat anonymiser(String type, UUID id) {
        if (!existe(type, id)) {
            throw new ErreurMetier("PERSONNE_INTROUVABLE", "Fiche introuvable", HttpStatus.NOT_FOUND);
        }
        if (dejaAnonymisee(type, id)) {
            throw new ErreurMetier("PERSONNE_DEJA_ANONYMISEE", "Cette fiche est déjà anonymisée", HttpStatus.CONFLICT);
        }
        return switch (type) {
            case "LOCATAIRE" -> locataire(id);
            case "GARANT" -> garant(id, true);
            case "PROPRIETAIRE" -> proprietaire(id);
            default -> prestataire(id);
        };
    }

    private Resultat locataire(UUID id) {
        UUID utilisateur = jdbc.query("SELECT utilisateur_id FROM locataire WHERE id = ?", (rs, i) -> rs.getObject(1, UUID.class), id).get(0);
        int pieces = supprimerPieces(CiblePiece.LOCATAIRE, id);
        int nbGarants = 0;
        for (var g : garants.findAll().stream().filter(g -> g.getLocataireId().equals(id) && !dejaAnonymisee("GARANT", g.getId())).toList()) {
            nbGarants++;
            pieces += garant(g.getId(), false).pieces();
        }
        jdbc.update("""
                UPDATE locataire SET nom = ?, profession = NULL, telephone = NULL, email = NULL, adresse = NULL, date_naissance = NULL, numero_cni_chiffre = NULL,
                       utilisateur_id = NULL, notes = NULL, archive = TRUE, anonymise_le = now(), updated_at = now() WHERE id = ?""", "Locataire anonymisé", id);
        journal.publier("TIERS.LOCATAIRE_ANONYMISE", "locataire", id, null, Map.of("anonymise", true));
        return new Resultat(utilisateur, pieces, nbGarants);
    }

    private Resultat garant(UUID id, boolean journaliser) {
        int pieces = supprimerPieces(CiblePiece.GARANT, id);
        jdbc.update("UPDATE garant SET nom = ?, lien = NULL, profession = NULL, telephone = NULL, adresse = NULL, numero_cni_chiffre = NULL, archive = TRUE, anonymise_le = now(), updated_at = now() WHERE id = ?",
                "Garant anonymisé", id);
        if (journaliser) {
            journal.publier("TIERS.GARANT_ANONYMISE", "garant", id, null, Map.of("anonymise", true));
        }
        return new Resultat(null, pieces, 0);
    }

    private Resultat proprietaire(UUID id) {
        UUID utilisateur = jdbc.query("SELECT utilisateur_id FROM proprietaire_tiers WHERE id = ?", (rs, i) -> rs.getObject(1, UUID.class), id).get(0);
        // les coordonnées de reversement (compte bancaire, numéro Mobile Money) sont des données personnelles : effacées aussi
        jdbc.update("UPDATE proprietaire_tiers SET nom = ?, telephone = NULL, email = NULL, adresse = NULL, coordonnees_reversement = NULL, utilisateur_id = NULL, notes = NULL, archive = TRUE, "
                + "anonymise_le = now(), updated_at = now() WHERE id = ?", "Propriétaire anonymisé", id);
        journal.publier("TIERS.PROPRIETAIRE_ANONYMISE", "proprietaire", id, null, Map.of("anonymise", true));
        return new Resultat(utilisateur, 0, 0);
    }

    private Resultat prestataire(UUID id) {
        UUID utilisateur = jdbc.query("SELECT utilisateur_id FROM prestataire WHERE id = ?", (rs, i) -> rs.getObject(1, UUID.class), id).get(0);
        jdbc.update("UPDATE prestataire SET nom = ?, metier = NULL, telephone = NULL, email = NULL, adresse = NULL, utilisateur_id = NULL, notes = NULL, archive = TRUE, anonymise_le = now(), updated_at = now() WHERE id = ?",
                "Prestataire anonymisé", id);
        journal.publier("TIERS.PRESTATAIRE_ANONYMISE", "prestataire", id, null, Map.of("anonymise", true));
        return new Resultat(utilisateur, 0, 0);
    }

    private int supprimerPieces(CiblePiece cible, UUID id) {
        List<PieceDto> liste = locataires.listerPieces(cible, id);
        liste.forEach(p -> locataires.supprimerPiece(p.id()));
        return liste.size();
    }
}
