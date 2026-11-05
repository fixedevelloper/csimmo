package com.immogest.app.finance.web;

import com.immogest.app.finance.DepenseService;
import com.immogest.app.finance.ReversementDtos.DepenseDto;
import com.immogest.app.finance.ReversementDtos.DepenseSaisie;
import com.immogest.app.finance.ReversementDtos.PaiementReversementSaisie;
import com.immogest.app.finance.ReversementDtos.ReversementDto;
import com.immogest.app.finance.ReversementService;
import com.immogest.app.shared.ErreurMetier;
import com.immogest.app.shared.PageReponse;
import com.immogest.app.shared.Pagination;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
class ReversementController {

    private static final String GESTION = "hasAnyRole('ADMIN', 'GESTIONNAIRE')";
    private static final String LECTURE = "hasAnyRole('ADMIN', 'GESTIONNAIRE', 'PROPRIETAIRE_TIERS')";
    private static final Set<String> TRIS_REV = Set.of("periode", "net", "statut");
    private static final Set<String> TRIS_DEP = Set.of("dateDepense", "montant", "createdAt");

    /** @param mois « AAAA-MM » */
    record CalculRequete(@NotBlank String mois) {
    }

    record PaiementReversementRequete(@NotBlank String mode, @Size(max = 100) String reference, LocalDate date) {
    }

    record DepenseRequete(@NotNull UUID immeubleId, UUID lotId, @NotNull LocalDate dateDepense, @NotBlank @Size(max = 200) String nature,
            @NotNull @jakarta.validation.constraints.Min(1) Long montant, @Size(max = 100) String reference) {
    }

    record AnnulationRequete(@NotBlank @Size(max = 500) String motif) {
    }

    private final ReversementService reversements;
    private final DepenseService depenses;

    ReversementController(ReversementService reversements, DepenseService depenses) {
        this.reversements = reversements;
        this.depenses = depenses;
    }

    private static LocalDate mois(String texte) {
        try {
            return YearMonth.parse(texte.strip()).atDay(1);
        } catch (DateTimeParseException e) {
            throw new ErreurMetier("MOIS_INVALIDE", "Mois attendu au format AAAA-MM", HttpStatus.BAD_REQUEST);
        }
    }

    // ---- reversements

    /** Calcule (ou recalcule) les reversements non validés du mois. Idempotent. */
    @PostMapping("/reversements/calculer")
    @PreAuthorize(GESTION)
    List<ReversementDto> calculer(@Valid @RequestBody CalculRequete r) {
        return reversements.calculer(mois(r.mois()));
    }

    /** Gestionnaires : tout. Propriétaire tiers : ses reversements validés ou payés. */
    @GetMapping("/reversements")
    @PreAuthorize(LECTURE)
    PageReponse<ReversementDto> lister(@RequestParam(required = false) UUID proprietaireId, @RequestParam(required = false) String statut,
            @RequestParam(required = false) String mois, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sort) {
        return PageReponse.de(reversements.lister(proprietaireId, statut, mois == null ? null : mois(mois), Pagination.de(page, size, sort, TRIS_REV, "periode")));
    }

    @GetMapping("/reversements/{id}")
    @PreAuthorize(LECTURE)
    ReversementDto obtenir(@PathVariable UUID id) {
        return reversements.obtenir(id);
    }

    @PostMapping("/reversements/{id}/valider")
    @PreAuthorize(GESTION)
    ReversementDto valider(@PathVariable UUID id) {
        return reversements.valider(id);
    }

    @PostMapping("/reversements/{id}/payer")
    @PreAuthorize(GESTION)
    ReversementDto payer(@PathVariable UUID id, @Valid @RequestBody PaiementReversementRequete r) {
        return reversements.payer(id, new PaiementReversementSaisie(r.mode(), r.reference(), r.date()));
    }

    @GetMapping(value = "/reversements/{id}/apercu", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize(GESTION)
    ResponseEntity<byte[]> apercu(@PathVariable UUID id) {
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"releve.pdf\"").header(HttpHeaders.CACHE_CONTROL, "no-store")
                .contentType(MediaType.APPLICATION_PDF).body(reversements.apercu(id));
    }

    // ---- dépenses

    @PostMapping("/depenses")
    @PreAuthorize(GESTION)
    @ResponseStatus(HttpStatus.CREATED)
    DepenseDto enregistrer(@Valid @RequestBody DepenseRequete r) {
        return depenses.enregistrer(new DepenseSaisie(r.immeubleId(), r.lotId(), r.dateDepense(), r.nature(), r.montant(), r.reference()));
    }

    @GetMapping("/depenses")
    @PreAuthorize(GESTION)
    PageReponse<DepenseDto> listerDepenses(@RequestParam(required = false) UUID immeubleId, @RequestParam(required = false) UUID proprietaireId,
            @RequestParam(required = false) String mois, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sort) {
        return PageReponse.de(depenses.lister(immeubleId, proprietaireId, mois == null ? null : mois(mois), Pagination.de(page, size, sort, TRIS_DEP, "dateDepense")));
    }

    @PostMapping("/depenses/{id}/annuler")
    @PreAuthorize(GESTION)
    DepenseDto annuler(@PathVariable UUID id, @Valid @RequestBody AnnulationRequete r) {
        return depenses.annuler(id, r.motif());
    }
}
