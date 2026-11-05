package com.immogest.app.conformite.web;

import static com.immogest.app.conformite.Dtos.*;

import com.immogest.app.conformite.ConformiteService;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Conformité (loi 2024/017). Lecture et consentements : ADMIN et GESTIONNAIRE ; exécution ou refus d'une anonymisation : ADMIN seul. */
@RestController
@RequestMapping("/api/v1/conformite")
class ConformiteController {

    private final ConformiteService service;

    ConformiteController(ConformiteService service) {
        this.service = service;
    }

    record RefusRequete(@NotBlank String motif) {
    }

    @GetMapping("/registre")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE')")
    RegistreDto registre() {
        return service.registre();
    }

    @GetMapping("/modeles-consentement")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE')")
    List<ModeleConsentementDto> modeles(@RequestParam(defaultValue = "fr") String langue) {
        return service.modeles(langue);
    }

    @GetMapping("/consentements/{type}/{personneId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE')")
    SituationConsentementDto situation(@PathVariable TypePersonne type, @PathVariable UUID personneId) {
        return service.situation(type, personneId);
    }

    @PostMapping("/consentements")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE')")
    @ResponseStatus(HttpStatus.CREATED)
    ConsentementDto enregistrer(@RequestBody ConsentementSaisie saisie) {
        return service.enregistrer(saisie);
    }

    @GetMapping("/demandes")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE')")
    List<DemandeAnonymisationDto> demandes(@RequestParam(required = false) String statut) {
        return service.lister(statut);
    }

    @PostMapping("/demandes")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE')")
    @ResponseStatus(HttpStatus.CREATED)
    DemandeAnonymisationDto demander(@RequestBody DemandeSaisie saisie) {
        return service.demander(saisie);
    }

    @GetMapping("/demandes/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE')")
    DemandeAnonymisationDto demande(@PathVariable UUID id) {
        return service.obtenir(id);
    }

    @PostMapping("/demandes/{id}/execution")
    @PreAuthorize("hasRole('ADMIN')")
    ResultatAnonymisationDto executer(@PathVariable UUID id) {
        return service.executer(id);
    }

    @PostMapping("/demandes/{id}/refus")
    @PreAuthorize("hasRole('ADMIN')")
    DemandeAnonymisationDto refuser(@PathVariable UUID id, @RequestBody RefusRequete requete) {
        return service.refuser(id, requete.motif());
    }
}
