package com.immogest.licenseserver;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/** DTO du serveur de licences. */
public final class Modeles {
    private Modeles() {
    }

    public static final String EMPREINTE = "^[0-9a-fA-F]{64}$";

    public record PageReponse<T>(List<T> content, int page, int size, long totalElements) {
    }

    public record ConnexionRequete(@NotBlank String email, @NotBlank String motDePasse) {
    }

    public record JetonDto(String jeton, long expireDans) {
    }

    public record ClientRequete(@NotBlank @Size(max = 200) String nom, @Size(max = 200) String email, @Size(max = 50) String telephone, @Size(max = 100) String pays,
            @Size(max = 2000) String notes) {
    }

    public record ClientDto(UUID id, String nom, String email, String telephone, String pays, String notes, OffsetDateTime creeLe) {
    }

    public record EmissionRequete(@NotNull UUID clientId, @Positive int palierLots, @NotNull LocalDate dateDebut, @NotNull LocalDate dateExpiration,
            @NotBlank @Pattern(regexp = EMPREINTE) String empreinteServeur, List<@NotBlank @Size(max = 100) String> fonctionnalites) {
    }

    public record RenouvellementRequete(@NotNull LocalDate dateExpiration, Integer palierLots, List<@NotBlank @Size(max = 100) String> fonctionnalites) {
    }

    public record TransfertRequete(@NotBlank @Pattern(regexp = EMPREINTE) String empreinteServeur) {
    }

    public record RevocationRequete(@NotBlank @Size(max = 500) String motif) {
    }

    public record LicenceDto(UUID id, UUID clientId, String clientNom, String statut, int palierLots, LocalDate dateDebut, LocalDate dateExpiration,
            String empreinteServeur, List<String> fonctionnalites, long joursRestants, int transferts, OffsetDateTime derniereVerification,
            String versionApplication, String motifRevocation) {
    }

    /** Résultat d'une émission : la licence et le contenu du fichier {@code .lic} à remettre au client. */
    public record EmissionDto(LicenceDto licence, String fichier) {
    }

    public record HistoriqueEmissionDto(UUID id, String motif, int palierLots, LocalDate dateDebut, LocalDate dateExpiration, String empreinteServeur,
            String emisePar, OffsetDateTime creeLe) {
    }

    public record PaiementRequete(@Positive long montant, @NotBlank @Size(max = 50) String mode, @Size(max = 200) String reference, @NotNull LocalDate datePaiement,
            @Size(max = 500) String note) {
    }

    public record PaiementDto(UUID id, UUID licenceId, long montant, String mode, String reference, LocalDate datePaiement, String note, String enregistrePar,
            OffsetDateTime creeLe) {
    }

    public record VersionRequete(@NotBlank @Size(max = 50) @Pattern(regexp = "^[0-9A-Za-z][0-9A-Za-z.+\\-]*$") String numero, @Size(max = 5000) String notes,
            @NotBlank @Size(max = 500) @Pattern(regexp = "^https://.+") String urlTelechargement, @NotBlank @Pattern(regexp = "^[0-9a-fA-F]{64}$") String sha256) {
    }

    public record VersionDto(UUID id, String numero, String notes, String urlTelechargement, String sha256, String publieePar, OffsetDateTime publieeLe) {
    }

    public record VerificationRequete(@NotBlank @Size(max = 100) String licenseId, @NotBlank @Size(max = 100) String empreinteServeur, @Size(max = 50) String versionApplication) {
    }

    /** Réponse lue par l'installation (contrat de CLAUDE.md §6ter). */
    public record VerificationReponse(String statut, String licenceRenouvelee) {
    }
}
