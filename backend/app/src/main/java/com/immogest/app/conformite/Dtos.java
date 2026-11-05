package com.immogest.app.conformite;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/** Types du module (publics : le contrôleur est dans le sous-package {@code web}). Noms uniques pour ne pas entrer en collision dans l'OpenAPI. */
public final class Dtos {

    private Dtos() {
    }

    public enum TypePersonne {
        LOCATAIRE, GARANT, PROPRIETAIRE, PRESTATAIRE
    }

    /** Finalités soumises à consentement : celles qui ne découlent pas du contrat de location. */
    public enum Finalite {
        NOTIFICATIONS, PIECES_IDENTITE, TRANSMISSION_PROPRIETAIRE, APPLICATION_MOBILE
    }

    public enum ModeRecueil {
        PAPIER, ELECTRONIQUE, ORAL
    }

    public record ModeleConsentementDto(Finalite finalite, String version, String titre, String texte) {
    }

    public record ConsentementSaisie(TypePersonne type, UUID personneId, Finalite finalite, boolean accorde, ModeRecueil modeRecueil, String referenceDocument) {
    }

    public record ConsentementDto(UUID id, Finalite finalite, boolean accorde, String versionModele, ModeRecueil modeRecueil, String referenceDocument, String recueilliPar, OffsetDateTime decideLe) {
    }

    /** @param statut ACCORDE, REFUSE (refusé ou retiré) ou NON_RECUEILLI */
    public record FinaliteSituationDto(Finalite finalite, String statut, ConsentementDto derniere) {
    }

    public record SituationConsentementDto(TypePersonne type, UUID personneId, List<FinaliteSituationDto> finalites, List<ConsentementDto> historique) {
    }

    public record ObstacleDto(String code, String message) {
    }

    public record DemandeSaisie(TypePersonne type, UUID personneId, String motif) {
    }

    /** @param obstacles ce qui empêcherait l'anonymisation aujourd'hui (vide pour une demande traitée) */
    public record DemandeAnonymisationDto(UUID id, TypePersonne type, UUID personneId, String statut, String motif, String motifRefus, OffsetDateTime recueLe, String recuePar,
            OffsetDateTime traiteeLe, String traiteePar, List<ObstacleDto> obstacles) {
    }

    /** @param conserve ce qui est gardé malgré l'anonymisation, et pourquoi (information à donner à la personne) */
    public record ResultatAnonymisationDto(UUID demandeId, int piecesSupprimees, List<String> conserve) {
    }

    public record TraitementDto(String id, String nom, String finalite, String baseLegale, List<String> donnees, List<String> personnes, List<String> destinataires, String conservation,
            List<String> mesures) {
    }

    public record RegistreDto(String version, String responsable, String note, List<TraitementDto> traitements) {
    }
}
