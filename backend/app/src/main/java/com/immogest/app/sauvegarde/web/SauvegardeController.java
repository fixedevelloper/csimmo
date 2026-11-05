package com.immogest.app.sauvegarde.web;

import com.immogest.app.sauvegarde.DiagnosticDto;
import com.immogest.app.sauvegarde.EtatSauvegardesDto;
import com.immogest.app.sauvegarde.SauvegardeService;
import com.immogest.app.shared.ActeurCourant;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Sauvegardes : réservé à l'administrateur. Ces routes restent utilisables sous une licence expirée (CLAUDE.md §6 licence 6bis). */
@RestController
@RequestMapping("/api/v1/sauvegardes")
@PreAuthorize("hasRole('ADMIN')")
class SauvegardeController {

    private final SauvegardeService service;
    private final ActeurCourant acteur;

    SauvegardeController(SauvegardeService service, ActeurCourant acteur) {
        this.service = service;
        this.acteur = acteur;
    }

    record RestaurationRequete(@NotBlank String confirmation) {
    }

    private String par() {
        return acteur.acteur().map(a -> a.id().toString()).orElse("inconnu");
    }

    @GetMapping
    EtatSauvegardesDto etat() {
        return service.etat();
    }

    @GetMapping("/diagnostic")
    DiagnosticDto diagnostic() {
        return service.diagnostic();
    }

    /** Demande une sauvegarde immédiate : traitée par le conteneur de sauvegarde (suivre l'état avec GET). */
    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    void sauvegarder() {
        service.demanderSauvegarde(par());
    }

    @PostMapping("/{id}/restauration")
    @ResponseStatus(HttpStatus.ACCEPTED)
    void restaurer(@PathVariable String id, @Valid @RequestBody RestaurationRequete requete) {
        service.demanderRestauration(id, requete.confirmation(), par());
    }
}
