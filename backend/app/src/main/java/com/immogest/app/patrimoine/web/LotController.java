package com.immogest.app.patrimoine.web;

import com.immogest.app.patrimoine.CibleTypePhoto;
import com.immogest.app.patrimoine.FiltreLots;
import com.immogest.app.patrimoine.LotDto;
import com.immogest.app.patrimoine.LotSaisie;
import com.immogest.app.patrimoine.PatrimoineService;
import com.immogest.app.patrimoine.PhotoDto;
import com.immogest.app.patrimoine.StatutLot;
import com.immogest.app.patrimoine.TypeCompteur;
import com.immogest.app.patrimoine.TypeLot;
import com.immogest.app.shared.PageReponse;
import com.immogest.app.shared.Pagination;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;
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
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/lots")
@PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE')")
class LotController {

    private static final String LECTURE = "hasAnyRole('ADMIN', 'GESTIONNAIRE', 'PROPRIETAIRE_TIERS')";
    private static final Set<String> TRIS = Set.of("reference", "loyerIndicatif", "surfaceM2", "type", "statut", "createdAt");

    record CompteurRequete(@NotNull TypeCompteur type, @NotBlank @Size(max = 50) String numero) {
    }

    /** @param loyerIndicatif FCFA entiers */
    record LotRequete(
            UUID immeubleId,
            @NotBlank @Size(max = 50) String reference,
            @NotNull TypeLot type,
            @DecimalMin(value = "0", inclusive = false) BigDecimal surfaceM2,
            @Min(0) @Max(100) Integer pieces,
            @Min(0) Long loyerIndicatif,
            @Size(max = 50) List<@Size(max = 100) String> equipements,
            @Size(max = 10) List<@Valid CompteurRequete> compteurs) {

        LotSaisie enSaisie() {
            return new LotSaisie(immeubleId, reference, type, surfaceM2, pieces, loyerIndicatif, equipements,
                    compteurs == null ? null : compteurs.stream().map(c -> new LotSaisie.CompteurSaisie(c.type(), c.numero())).toList());
        }
    }

    record StatutRequete(@NotNull StatutLot statut) {
    }

    private final PatrimoineService service;

    LotController(PatrimoineService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize(LECTURE)
    @io.swagger.v3.oas.annotations.Operation(operationId = "listerLots")
    PageReponse<LotDto> lister(@RequestParam(required = false) UUID proprietaireTiersId, @RequestParam(required = false) UUID immeubleId, @RequestParam(required = false) StatutLot statut,
            @RequestParam(required = false) TypeLot type, @RequestParam(required = false) String ville,
            @RequestParam(required = false) String quartier, @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "false") boolean inclureArchives, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size, @RequestParam(required = false) String sort) {
        return PageReponse.de(service.listerLots(new FiltreLots(immeubleId, statut, type, ville, quartier, q, inclureArchives, proprietaireTiersId),
                Pagination.de(page, size, sort, TRIS, "reference")));
    }

    @GetMapping("/{id}")
    @PreAuthorize(LECTURE)
    LotDto obtenir(@PathVariable UUID id) {
        return service.obtenirLot(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    LotDto creer(@Valid @RequestBody LotRequete requete) {
        return service.creerLot(requete.enSaisie());
    }

    @PutMapping("/{id}")
    LotDto modifier(@PathVariable UUID id, @Valid @RequestBody LotRequete requete) {
        return service.modifierLot(id, requete.enSaisie());
    }

    /** Lot libre ↔ en travaux. Le statut occupé vient du bail. */
    @PutMapping("/{id}/statut")
    LotDto changerStatut(@PathVariable UUID id, @Valid @RequestBody StatutRequete requete) {
        return service.changerStatut(id, requete.statut());
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> archiver(@PathVariable UUID id) {
        service.archiverLot(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/photos")
    @PreAuthorize(LECTURE)
    List<PhotoDto> photos(@PathVariable UUID id) {
        return service.listerPhotos(CibleTypePhoto.LOT, id);
    }

    @PostMapping(path = "/{id}/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    PhotoDto ajouterPhoto(@PathVariable UUID id, @RequestParam("fichier") MultipartFile fichier,
            @RequestParam(required = false) @Size(max = 200) String legende) throws IOException {
        return service.ajouterPhoto(CibleTypePhoto.LOT, id, fichier.getOriginalFilename(), fichier.getInputStream(), legende);
    }
}
