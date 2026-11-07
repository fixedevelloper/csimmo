package com.immogest.app.bail.web;

import com.immogest.app.bail.BailDto;
import com.immogest.app.bail.BailSaisie;
import com.immogest.app.bail.BailService;
import com.immogest.app.bail.DocumentBailDto;
import com.immogest.app.bail.FiltreBaux;
import com.immogest.app.bail.ModeleBailDto;
import com.immogest.app.bail.ModeleBailSaisie;
import com.immogest.app.bail.ModeleBailService;
import com.immogest.app.bail.Periodicite;
import com.immogest.app.bail.RenouvellementSaisie;
import com.immogest.app.bail.ResiliationSaisie;
import com.immogest.app.bail.StatutBail;
import com.immogest.app.shared.PageReponse;
import com.immogest.app.shared.Pagination;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/baux")
@PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE')")
class BailController {

    private static final String LECTURE = "hasAnyRole('ADMIN', 'GESTIONNAIRE', 'LOCATAIRE')";
    /** Liste et fiche d'un bail : l'ENCAISSEUR les lit dans son périmètre (BailService) ; pas les documents ni les actions. */
    private static final String LECTURE_BAIL = "hasAnyRole('ADMIN', 'GESTIONNAIRE', 'LOCATAIRE', 'ENCAISSEUR')";
    private static final Set<String> TRIS = Set.of("numero", "dateDebut", "dateFin", "loyer", "statut", "createdAt");

    /** Montants en FCFA entiers. */
    record BailRequete(
            @NotNull UUID lotId,
            @NotNull UUID locataireId,
            @Size(max = 10) List<UUID> garantIds,
            @NotNull LocalDate dateDebut,
            LocalDate dateFin,
            @NotNull @Min(1) Long loyer,
            @Min(0) Long charges,
            @NotNull Periodicite periodicite,
            @NotNull @Min(1) @Max(31) Integer jourEcheance,
            @Min(0) Long caution,
            @Min(0) @Max(24) Integer moisAvance,
            @Min(0) @Max(12) Integer preavisMois) {

        BailSaisie enSaisie() {
            return new BailSaisie(lotId, locataireId, garantIds, dateDebut, dateFin, loyer, charges, periodicite, jourEcheance, caution,
                    moisAvance, preavisMois);
        }
    }

    record ResiliationRequete(LocalDate dateNotification, LocalDate dateSortie, boolean preavisReduitAccepte,
            @Size(max = 1000) String motif) {
    }

    record RenouvellementRequete(LocalDate dateFin, @Min(1) Long loyer, @Min(0) Long charges, @Min(0) Long caution,
            @Min(0) @Max(24) Integer moisAvance) {
    }

    record ModeleRequete(@Size(max = 200) String bailleurNom, @Size(max = 300) String bailleurAdresse, @Size(max = 4000) String preambule,
            @Size(max = 20000) String clauses, @Size(max = 500) String piedDePage) {
    }

    private final BailService service;
    private final ModeleBailService modeles;

    BailController(BailService service, ModeleBailService modeles) {
        this.service = service;
        this.modeles = modeles;
    }

    @GetMapping
    @PreAuthorize(LECTURE_BAIL)
    @io.swagger.v3.oas.annotations.Operation(operationId = "listerBaux")
    PageReponse<BailDto> lister(@RequestParam(required = false) StatutBail statut, @RequestParam(required = false) UUID lotId,
            @RequestParam(required = false) UUID locataireId, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size, @RequestParam(required = false) String sort) {
        return PageReponse.de(service.lister(new FiltreBaux(statut, lotId, locataireId),
                Pagination.de(page, size, sort, TRIS, "createdAt")));
    }

    @GetMapping("/{id}")
    @PreAuthorize(LECTURE_BAIL)
    BailDto obtenir(@PathVariable UUID id) {
        return service.obtenir(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    BailDto creer(@Valid @RequestBody BailRequete requete) {
        return service.creer(requete.enSaisie());
    }

    @PutMapping("/{id}")
    BailDto modifier(@PathVariable UUID id, @Valid @RequestBody BailRequete requete) {
        return service.modifier(id, requete.enSaisie());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void supprimer(@PathVariable UUID id) {
        service.supprimer(id);
    }

    @PostMapping("/{id}/activer")
    BailDto activer(@PathVariable UUID id) {
        return service.activer(id);
    }

    @PostMapping("/{id}/resilier")
    BailDto resilier(@PathVariable UUID id, @Valid @RequestBody ResiliationRequete r) {
        return service.resilier(id, new ResiliationSaisie(r.dateNotification(), r.dateSortie(), r.preavisReduitAccepte(), r.motif()));
    }

    @PostMapping("/{id}/renouveler")
    @ResponseStatus(HttpStatus.CREATED)
    BailDto renouveler(@PathVariable UUID id, @Valid @RequestBody RenouvellementRequete r) {
        return service.renouveler(id, new RenouvellementSaisie(r.dateFin(), r.loyer(), r.charges(), r.caution(), r.moisAvance()));
    }

    @GetMapping(value = "/{id}/apercu", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE')")
    ResponseEntity<byte[]> apercu(@PathVariable UUID id) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"bail.pdf\"")
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .contentType(MediaType.APPLICATION_PDF).body(service.apercuPdf(id));
    }

    @PostMapping("/{id}/documents")
    @ResponseStatus(HttpStatus.CREATED)
    DocumentBailDto genererDocument(@PathVariable UUID id) {
        return service.genererDocument(id);
    }

    @GetMapping("/{id}/documents")
    @PreAuthorize(LECTURE)
    List<DocumentBailDto> documents(@PathVariable UUID id) {
        return service.listerDocuments(id);
    }

    @GetMapping("/modele")
    ModeleBailDto modele() {
        return modeles.obtenir();
    }

    @PutMapping("/modele")
    @PreAuthorize("hasRole('ADMIN')")
    ModeleBailDto modifierModele(@Valid @RequestBody ModeleRequete r) {
        return modeles.modifier(new com.immogest.app.bail.ModeleBailSaisie(r.bailleurNom(), r.bailleurAdresse(), r.preambule(), r.clauses(), r.piedDePage()));
    }

    @DeleteMapping("/modele")
    @PreAuthorize("hasRole('ADMIN')")
    ModeleBailDto reinitialiserModele() {
        return modeles.reinitialiser();
    }
}
