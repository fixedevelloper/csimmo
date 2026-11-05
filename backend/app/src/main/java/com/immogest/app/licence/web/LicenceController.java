package com.immogest.app.licence.web;

import com.immogest.app.licence.EtatLicenceDto;
import com.immogest.app.licence.LicenceService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/licence")
class LicenceController {

    record ImportRequete(@NotBlank @Size(max = 65536) String contenu) {
    }

    private final LicenceService licences;

    LicenceController(LicenceService licences) {
        this.licences = licences;
    }

    /** État de la licence (écran Paramètres et bandeau d'alerte). */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE')")
    EtatLicenceDto etat() {
        return licences.etat();
    }

    /** Importe une nouvelle licence (renouvellement, changement de palier, transfert). Permis même si l'ancienne a expiré. */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    EtatLicenceDto importer(@Valid @RequestBody ImportRequete requete) {
        return licences.importer(requete.contenu());
    }
}
