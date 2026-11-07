package com.immogest.app.finance.web;

import com.immogest.app.finance.CaisseLigne;
import com.immogest.app.finance.CompteLocataireDto;
import com.immogest.app.finance.ModePaiement;
import com.immogest.app.finance.PaiementDto;
import com.immogest.app.finance.PaiementSaisie;
import com.immogest.app.finance.PaiementService;
import com.immogest.app.finance.ResultatPaiement;
import com.immogest.app.shared.PageReponse;
import com.immogest.app.shared.Pagination;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
class PaiementController {

    private static final String GESTION = "hasAnyRole('ADMIN', 'GESTIONNAIRE')";
    private static final String LECTURE = "hasAnyRole('ADMIN', 'GESTIONNAIRE', 'LOCATAIRE', 'ENCAISSEUR')";
    /** Saisie d'un encaissement et caisse du jour : l'ENCAISSEUR n'agit que dans son périmètre (filtré par PaiementService). */
    private static final String ENCAISSEMENT = "hasAnyRole('ADMIN', 'GESTIONNAIRE', 'ENCAISSEUR')";
    private static final Set<String> TRIS = Set.of("datePaiement", "montant", "createdAt");

    /** @param montant FCFA entiers */
    record PaiementRequete(@NotNull UUID bailId, @NotNull @jakarta.validation.constraints.Min(1) Long montant, @NotNull ModePaiement mode,
            @Size(max = 100) String reference, LocalDate datePaiement) {
    }

    record ContrePassationRequete(@NotBlank @Size(max = 500) String motif) {
    }

    private final PaiementService service;

    PaiementController(PaiementService service) {
        this.service = service;
    }

    /** Idempotent avec `Idempotency-Key` : un rejeu renvoie 200 et le paiement d'origine, sans rien recréer. */
    @PostMapping("/paiements")
    @PreAuthorize(ENCAISSEMENT)
    ResponseEntity<PaiementDto> enregistrer(@Valid @RequestBody PaiementRequete r,
            @RequestHeader(value = "Idempotency-Key", required = false) String cle) {
        ResultatPaiement res = service.enregistrer(new PaiementSaisie(r.bailId(), r.montant(), r.mode(), r.reference(), r.datePaiement()), cle);
        return ResponseEntity.status(res.rejoue() ? HttpStatus.OK : HttpStatus.CREATED).body(res.paiement());
    }

    @PostMapping("/paiements/{id}/contre-passation")
    @PreAuthorize(GESTION)
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.CREATED)
    PaiementDto contrePasser(@PathVariable UUID id, @Valid @RequestBody ContrePassationRequete r) {
        return service.contrePasser(id, r.motif());
    }

    @GetMapping("/paiements")
    @PreAuthorize(LECTURE)
    PageReponse<PaiementDto> lister(@RequestParam(required = false) UUID bailId, @RequestParam(required = false) UUID locataireId,
            @RequestParam(required = false) ModePaiement mode,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate du,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate au,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sort) {
        return PageReponse.de(service.lister(bailId, locataireId, mode, du, au, Pagination.de(page, size, sort, TRIS, "createdAt")));
    }

    /** Rapprochement de caisse : espèces du jour par agent (un gestionnaire ne voit que les siennes). */
    @GetMapping("/paiements/caisse")
    @PreAuthorize(ENCAISSEMENT)
    List<CaisseLigne> caisse(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate jour) {
        return service.caisse(jour);
    }

    @GetMapping("/mon-compte")
    @PreAuthorize("hasRole('LOCATAIRE')")
    CompteLocataireDto monCompte() {
        return service.monCompte();
    }

    @GetMapping("/locataires/{id}/compte")
    @PreAuthorize(LECTURE)
    CompteLocataireDto compte(@PathVariable UUID id) {
        return service.compte(id);
    }
}
