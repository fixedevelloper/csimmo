package com.immogest.app.patrimoine.web;

import com.immogest.app.patrimoine.CibleTypePhoto;
import com.immogest.app.patrimoine.FiltreImmeubles;
import com.immogest.app.patrimoine.ImmeubleDto;
import com.immogest.app.patrimoine.ImmeubleSaisie;
import com.immogest.app.patrimoine.PatrimoineService;
import com.immogest.app.patrimoine.PhotoDto;
import com.immogest.app.shared.PageReponse;
import com.immogest.app.shared.Pagination;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
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
@RequestMapping("/api/v1/immeubles")
@PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE')")
class ImmeubleController {

    /** Un propriétaire tiers consulte (sans modifier) ses propres immeubles ; le service filtre ce qu'il voit. */
    private static final String LECTURE = "hasAnyRole('ADMIN', 'GESTIONNAIRE', 'PROPRIETAIRE_TIERS', 'ENCAISSEUR', 'GESTIONNAIRE_BIENS')";
    private static final Set<String> TRIS = Set.of("nom", "ville", "quartier", "createdAt");

    record ImmeubleRequete(
            @NotBlank @Size(max = 200) String nom,
            @NotBlank @Size(max = 300) String adresse,
            @Size(max = 100) String quartier,
            @NotBlank @Size(max = 100) String ville,
            @DecimalMin("-90") @DecimalMax("90") BigDecimal latitude,
            @DecimalMin("-180") @DecimalMax("180") BigDecimal longitude,
            @Size(max = 2000) String description, UUID proprietaireTiersId) {

        ImmeubleSaisie enSaisie() {
            return new ImmeubleSaisie(nom, adresse, quartier, ville, latitude, longitude, description, proprietaireTiersId);
        }
    }

    private final PatrimoineService service;

    ImmeubleController(PatrimoineService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize(LECTURE)
    PageReponse<ImmeubleDto> lister(@RequestParam(required = false) UUID proprietaireTiersId, @RequestParam(required = false) String ville, @RequestParam(required = false) String quartier,
            @RequestParam(required = false) String q, @RequestParam(defaultValue = "false") boolean inclureArchives,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sort) {
        return PageReponse.de(service.listerImmeubles(new FiltreImmeubles(ville, quartier, q, inclureArchives, proprietaireTiersId),
                Pagination.de(page, size, sort, TRIS, "nom")));
    }

    @GetMapping("/{id}")
    @PreAuthorize(LECTURE)
    ImmeubleDto obtenir(@PathVariable UUID id) {
        return service.obtenirImmeuble(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ImmeubleDto creer(@Valid @RequestBody ImmeubleRequete requete) {
        return service.creerImmeuble(requete.enSaisie());
    }

    @PutMapping("/{id}")
    ImmeubleDto modifier(@PathVariable UUID id, @Valid @RequestBody ImmeubleRequete requete) {
        return service.modifierImmeuble(id, requete.enSaisie());
    }

    /** « Supprimer » = archiver ; refusé tant que l'immeuble contient des lots non archivés. */
    @DeleteMapping("/{id}")
    ResponseEntity<Void> archiver(@PathVariable UUID id) {
        service.archiverImmeuble(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/photos")
    @PreAuthorize(LECTURE)
    List<PhotoDto> photos(@PathVariable UUID id) {
        return service.listerPhotos(CibleTypePhoto.IMMEUBLE, id);
    }

    @PostMapping(path = "/{id}/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    PhotoDto ajouterPhoto(@PathVariable UUID id, @RequestParam("fichier") MultipartFile fichier,
            @RequestParam(required = false) @Size(max = 200) String legende) throws IOException {
        return service.ajouterPhoto(CibleTypePhoto.IMMEUBLE, id, fichier.getOriginalFilename(), fichier.getInputStream(), legende);
    }
}
