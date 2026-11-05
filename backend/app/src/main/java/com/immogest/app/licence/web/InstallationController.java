package com.immogest.app.licence.web;

import com.immogest.app.licence.DemandeInstallation;
import com.immogest.app.licence.EtatInstallationDto;
import com.immogest.app.licence.InstallationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Assistant de premier lancement. Public, mais utilisable une seule fois (409 ensuite). */
@RestController
@RequestMapping("/api/v1/installation")
class InstallationController {

    record Administrateur(@NotBlank @Size(max = 200) String nom, @Size(max = 30) String telephone,
            @Size(max = 200) String email, @NotBlank @Size(min = 8, max = 100) String motDePasse) {
    }

    record InstallationRequete(@NotNull @Valid Administrateur admin, @NotBlank @Size(max = 65536) String licence) {
    }

    private final InstallationService installation;

    InstallationController(InstallationService installation) {
        this.installation = installation;
    }

    @GetMapping
    EtatInstallationDto etat() {
        return installation.etat();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    EtatInstallationDto installer(@Valid @RequestBody InstallationRequete requete) {
        Administrateur a = requete.admin();
        installation.installer(new DemandeInstallation(a.nom(), a.telephone(), a.email(), a.motDePasse(), requete.licence()));
        return installation.etat();
    }
}
