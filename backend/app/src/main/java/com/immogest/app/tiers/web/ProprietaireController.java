package com.immogest.app.tiers.web;

import com.immogest.app.shared.PageReponse;
import com.immogest.app.shared.Pagination;
import com.immogest.app.tiers.MandatDto;
import com.immogest.app.tiers.MandatSaisie;
import com.immogest.app.tiers.ModeReversement;
import com.immogest.app.tiers.ProprietaireDto;
import com.immogest.app.tiers.ProprietaireSaisie;
import com.immogest.app.tiers.ProprietaireService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.http.HttpStatus;
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

@RestController
@RequestMapping("/api/v1")
@PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE')")
class ProprietaireController {

    private static final Set<String> TRIS = Set.of("nom", "createdAt");

    record ProprietaireRequete(@NotBlank @Size(max = 200) String nom, @Size(max = 30) String telephone, @Size(max = 200) String email,
            @Size(max = 300) String adresse, ModeReversement modeReversement, @Size(max = 300) String coordonneesReversement,
            UUID utilisateurId, @Size(max = 2000) String notes) {

        ProprietaireSaisie enSaisie() {
            return new ProprietaireSaisie(nom, telephone, email, adresse, modeReversement, coordonneesReversement, utilisateurId, notes);
        }
    }

    /** @param tauxHonorairesBp points de base : 1000 = 10 % */
    record MandatRequete(@NotNull @Min(0) @Max(10_000) Integer tauxHonorairesBp, @NotNull LocalDate dateDebut, LocalDate dateFin,
            @Size(max = 1000) String notes) {

        MandatSaisie enSaisie() {
            return new MandatSaisie(tauxHonorairesBp, dateDebut, dateFin, notes);
        }
    }

    private final ProprietaireService service;

    ProprietaireController(ProprietaireService service) {
        this.service = service;
    }

    @GetMapping("/proprietaires-tiers")
    PageReponse<ProprietaireDto> lister(@RequestParam(required = false) String q, @RequestParam(defaultValue = "false") boolean inclureArchives,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size, @RequestParam(required = false) String sort) {
        return PageReponse.de(service.lister(q, inclureArchives, Pagination.de(page, size, sort, TRIS, "nom")));
    }

    @GetMapping("/proprietaires-tiers/{id}")
    ProprietaireDto obtenir(@PathVariable UUID id) {
        return service.obtenir(id);
    }

    @PostMapping("/proprietaires-tiers")
    @ResponseStatus(HttpStatus.CREATED)
    ProprietaireDto creer(@Valid @RequestBody ProprietaireRequete requete) {
        return service.creer(requete.enSaisie());
    }

    @PutMapping("/proprietaires-tiers/{id}")
    ProprietaireDto modifier(@PathVariable UUID id, @Valid @RequestBody ProprietaireRequete requete) {
        return service.modifier(id, requete.enSaisie());
    }

    @DeleteMapping("/proprietaires-tiers/{id}")
    ResponseEntity<Void> archiver(@PathVariable UUID id) {
        service.archiver(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/proprietaires-tiers/{id}/mandats")
    List<MandatDto> mandats(@PathVariable UUID id) {
        return service.listerMandats(id);
    }

    @PostMapping("/proprietaires-tiers/{id}/mandats")
    @ResponseStatus(HttpStatus.CREATED)
    MandatDto creerMandat(@PathVariable UUID id, @Valid @RequestBody MandatRequete requete) {
        return service.creerMandat(id, requete.enSaisie());
    }

    @GetMapping("/mandats/{id}")
    MandatDto mandat(@PathVariable UUID id) {
        return service.obtenirMandat(id);
    }

    @PutMapping("/mandats/{id}")
    MandatDto modifierMandat(@PathVariable UUID id, @Valid @RequestBody MandatRequete requete) {
        return service.modifierMandat(id, requete.enSaisie());
    }
}
