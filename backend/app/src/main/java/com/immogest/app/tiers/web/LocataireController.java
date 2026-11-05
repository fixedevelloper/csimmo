package com.immogest.app.tiers.web;

import com.immogest.app.shared.PageReponse;
import com.immogest.app.shared.Pagination;
import com.immogest.app.tiers.CiblePiece;
import com.immogest.app.tiers.GarantDto;
import com.immogest.app.tiers.GarantSaisie;
import com.immogest.app.tiers.LocataireDto;
import com.immogest.app.tiers.LocataireSaisie;
import com.immogest.app.tiers.LocataireService;
import com.immogest.app.tiers.PieceDto;
import com.immogest.app.tiers.TypePiece;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.IOException;
import java.time.LocalDate;
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
@RequestMapping("/api/v1")
@PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE')")
class LocataireController {

    private static final Set<String> TRIS = Set.of("nom", "createdAt");

    record LocataireRequete(@NotBlank @Size(max = 200) String nom, @Size(max = 100) String profession, @Size(max = 30) String telephone,
            @Size(max = 200) String email, @Size(max = 300) String adresse, LocalDate dateNaissance, @Size(max = 50) String numeroCni,
            UUID utilisateurId, @Size(max = 2000) String notes) {

        LocataireSaisie enSaisie() {
            return new LocataireSaisie(nom, profession, telephone, email, adresse, dateNaissance, numeroCni, utilisateurId, notes);
        }
    }

    record GarantRequete(@NotBlank @Size(max = 200) String nom, @Size(max = 100) String lien, @Size(max = 100) String profession,
            @NotBlank @Size(max = 30) String telephone, @Size(max = 300) String adresse, @Size(max = 50) String numeroCni) {

        GarantSaisie enSaisie() {
            return new GarantSaisie(nom, lien, profession, telephone, adresse, numeroCni);
        }
    }

    private final LocataireService service;

    LocataireController(LocataireService service) {
        this.service = service;
    }

    @GetMapping("/locataires")
    @io.swagger.v3.oas.annotations.Operation(operationId = "listerLocataires")
    PageReponse<LocataireDto> lister(@RequestParam(required = false) String q, @RequestParam(defaultValue = "false") boolean inclureArchives,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size, @RequestParam(required = false) String sort) {
        return PageReponse.de(service.lister(q, inclureArchives, Pagination.de(page, size, sort, TRIS, "nom")));
    }

    @GetMapping("/locataires/{id}")
    LocataireDto obtenir(@PathVariable UUID id) {
        return service.obtenir(id);
    }

    @PostMapping("/locataires")
    @ResponseStatus(HttpStatus.CREATED)
    LocataireDto creer(@Valid @RequestBody LocataireRequete requete) {
        return service.creer(requete.enSaisie());
    }

    @PutMapping("/locataires/{id}")
    LocataireDto modifier(@PathVariable UUID id, @Valid @RequestBody LocataireRequete requete) {
        return service.modifier(id, requete.enSaisie());
    }

    @DeleteMapping("/locataires/{id}")
    ResponseEntity<Void> archiver(@PathVariable UUID id) {
        service.archiver(id);
        return ResponseEntity.noContent().build();
    }

    // ------------------------------------------------------------------ garants

    @GetMapping("/locataires/{id}/garants")
    List<GarantDto> garants(@PathVariable UUID id) {
        return service.listerGarants(id);
    }

    @PostMapping("/locataires/{id}/garants")
    @ResponseStatus(HttpStatus.CREATED)
    GarantDto creerGarant(@PathVariable UUID id, @Valid @RequestBody GarantRequete requete) {
        return service.creerGarant(id, requete.enSaisie());
    }

    @PutMapping("/garants/{id}")
    GarantDto modifierGarant(@PathVariable UUID id, @Valid @RequestBody GarantRequete requete) {
        return service.modifierGarant(id, requete.enSaisie());
    }

    @DeleteMapping("/garants/{id}")
    ResponseEntity<Void> archiverGarant(@PathVariable UUID id) {
        service.archiverGarant(id);
        return ResponseEntity.noContent().build();
    }

    // ------------------------------------------------------------------ pièces d'identité (fichiers chiffrés)

    @GetMapping("/locataires/{id}/pieces")
    List<PieceDto> piecesLocataire(@PathVariable UUID id) {
        return service.listerPieces(CiblePiece.LOCATAIRE, id);
    }

    @PostMapping(path = "/locataires/{id}/pieces", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    PieceDto ajouterPieceLocataire(@PathVariable UUID id, @RequestParam("type") TypePiece type,
            @RequestParam("fichier") MultipartFile fichier) throws IOException {
        return service.ajouterPiece(CiblePiece.LOCATAIRE, id, type, fichier.getOriginalFilename(), fichier.getInputStream());
    }

    @GetMapping("/garants/{id}/pieces")
    List<PieceDto> piecesGarant(@PathVariable UUID id) {
        return service.listerPieces(CiblePiece.GARANT, id);
    }

    @PostMapping(path = "/garants/{id}/pieces", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    PieceDto ajouterPieceGarant(@PathVariable UUID id, @RequestParam("type") TypePiece type,
            @RequestParam("fichier") MultipartFile fichier) throws IOException {
        return service.ajouterPiece(CiblePiece.GARANT, id, type, fichier.getOriginalFilename(), fichier.getInputStream());
    }

    @DeleteMapping("/pieces/{id}")
    ResponseEntity<Void> supprimerPiece(@PathVariable UUID id) {
        service.supprimerPiece(id);
        return ResponseEntity.noContent().build();
    }
}
