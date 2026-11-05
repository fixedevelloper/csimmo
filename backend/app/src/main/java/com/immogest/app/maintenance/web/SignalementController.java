package com.immogest.app.maintenance.web;

import com.immogest.app.maintenance.SignalementDto;
import com.immogest.app.maintenance.SignalementService;
import com.immogest.app.maintenance.StatutSignalement;
import com.immogest.app.shared.ErreurMetier;
import com.immogest.app.shared.PageReponse;
import com.immogest.app.shared.Pagination;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/signalements")
class SignalementController {

    private static final Set<String> TRIS = Set.of("createdAt", "statut");

    record StatutSignalementRequete(@NotNull StatutSignalement statut, @Size(max = 1000) String note) {
    }

    private final SignalementService service;

    SignalementController(SignalementService service) {
        this.service = service;
    }

    /** Un locataire signale une panne (description + photo facultative) sur un de ses baux en cours. */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('LOCATAIRE')")
    @ResponseStatus(HttpStatus.CREATED)
    @io.swagger.v3.oas.annotations.Operation(operationId = "creerSignalement")
    SignalementDto creer(@RequestPart("description") String description, @RequestPart(value = "bailId", required = false) String bailId,
            @RequestPart(value = "photo", required = false) MultipartFile photo) throws IOException {
        // description et bail voyagent dans le corps (parties multipart), jamais dans l'URL : elle finit dans les journaux des proxys
        UUID bail = null;
        if (bailId != null && !bailId.isBlank()) {
            try {
                bail = UUID.fromString(bailId.strip());
            } catch (IllegalArgumentException e) {
                throw new ErreurMetier("BAIL_INTROUVABLE", "Bail introuvable", HttpStatus.NOT_FOUND);
            }
        }
        if (photo != null && photo.isEmpty()) {
            photo = null;
        }
        if (photo != null && photo.getSize() > 10L * 1024 * 1024) {
            throw new ErreurMetier("FICHIER_TROP_VOLUMINEUX", "La photo dépasse 10 Mo", HttpStatus.PAYLOAD_TOO_LARGE);
        }
        return service.creer(bail, description, photo == null ? null : photo.getOriginalFilename(), photo == null ? null : photo.getInputStream());
    }

    /** Gestionnaires : tous les signalements ; locataire : les siens. */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE', 'LOCATAIRE')")
    @io.swagger.v3.oas.annotations.Operation(operationId = "listerSignalements")
    PageReponse<SignalementDto> lister(@RequestParam(required = false) StatutSignalement statut, @RequestParam(required = false) UUID bailId,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size, @RequestParam(required = false) String sort) {
        return PageReponse.de(service.lister(statut, bailId, Pagination.de(page, size, sort, TRIS, "createdAt")));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE', 'LOCATAIRE')")
    SignalementDto obtenir(@PathVariable UUID id) {
        return service.obtenir(id);
    }

    @PutMapping("/{id}/statut")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE')")
    SignalementDto changerStatut(@PathVariable UUID id, @Valid @RequestBody StatutSignalementRequete r) {
        return service.changerStatut(id, r.statut(), r.note());
    }
}
