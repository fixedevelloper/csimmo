package com.immogest.app.patrimoine.web;

import com.immogest.app.patrimoine.PatrimoineService;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Suppression d'une photo (le téléchargement passe par {@code GET /api/v1/fichiers/{id}}). */
@RestController
@RequestMapping("/api/v1/photos")
@PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE')")
class PhotoController {

    private final PatrimoineService service;

    PhotoController(PatrimoineService service) {
        this.service = service;
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> supprimer(@PathVariable UUID id) {
        service.supprimerPhoto(id);
        return ResponseEntity.noContent().build();
    }
}
