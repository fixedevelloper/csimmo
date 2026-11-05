package com.immogest.app.stockage.web;

import com.immogest.app.shared.Acteur;
import com.immogest.app.shared.ActeurCourant;
import com.immogest.app.shared.ErreurMetier;
import com.immogest.app.stockage.FichierTelecharge;
import com.immogest.app.stockage.StockageService;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Seul point d'accès aux fichiers : le navigateur et le mobile ne reçoivent jamais d'identifiants S3.
 * Diffuse le flux, ou redirige vers une URL présignée si {@code immogest.stockage.url-presignee=true}.
 */
@RestController
@RequestMapping("/api/v1/fichiers")
class FichierController {

    private final StockageService service;
    private final ObjectProvider<ActeurCourant> acteurCourant;

    FichierController(StockageService service, ObjectProvider<ActeurCourant> acteurCourant) {
        this.service = service;
        this.acteurCourant = acteurCourant;
    }

    @GetMapping("/{id}")
    ResponseEntity<InputStreamResource> telecharger(@PathVariable UUID id) {
        Acteur acteur = acteurCourant.stream().findFirst().flatMap(ActeurCourant::acteur).orElseThrow(() ->
                new ErreurMetier("NON_AUTHENTIFIE", "Authentification requise", HttpStatus.UNAUTHORIZED));

        FichierTelecharge fichier = service.telecharger(id, acteur);
        if (fichier.redirection().isPresent()) {
            return ResponseEntity.status(HttpStatus.FOUND).location(fichier.redirection().get()).build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fichier.fichier().typeMime()))
                .contentLength(fichier.fichier().taille())
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline()
                        .filename(fichier.fichier().nomOrigine(), StandardCharsets.UTF_8).build().toString())
                .header("X-Content-Type-Options", "nosniff")
                .header(HttpHeaders.CACHE_CONTROL, "private, no-store")
                .body(new InputStreamResource(fichier.flux()));
    }
}
