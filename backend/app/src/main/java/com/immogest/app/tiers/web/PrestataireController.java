package com.immogest.app.tiers.web;

import com.immogest.app.shared.PageReponse;
import com.immogest.app.shared.Pagination;
import com.immogest.app.tiers.PrestataireDto;
import com.immogest.app.tiers.PrestataireSaisie;
import com.immogest.app.tiers.PrestataireService;
import com.immogest.app.tiers.RechercheTiersService;
import com.immogest.app.tiers.ResultatRecherche;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
class PrestataireController {

    private static final Set<String> TRIS = Set.of("nom", "metier", "createdAt");

    record PrestataireRequete(@NotBlank @Size(max = 200) String nom, @Size(max = 100) String metier, @Size(max = 30) String telephone,
            @Size(max = 200) String email, @Size(max = 300) String adresse, UUID utilisateurId, @Size(max = 2000) String notes) {

        PrestataireSaisie enSaisie() {
            return new PrestataireSaisie(nom, metier, telephone, email, adresse, utilisateurId, notes);
        }
    }

    private final PrestataireService service;
    private final RechercheTiersService recherche;

    PrestataireController(PrestataireService service, RechercheTiersService recherche) {
        this.service = service;
        this.recherche = recherche;
    }

    @GetMapping("/prestataires")
    PageReponse<PrestataireDto> lister(@RequestParam(required = false) String q, @RequestParam(defaultValue = "false") boolean inclureArchives,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size, @RequestParam(required = false) String sort) {
        return PageReponse.de(service.lister(q, inclureArchives, Pagination.de(page, size, sort, TRIS, "nom")));
    }

    @GetMapping("/prestataires/{id}")
    PrestataireDto obtenir(@PathVariable UUID id) {
        return service.obtenir(id);
    }

    @PostMapping("/prestataires")
    @ResponseStatus(HttpStatus.CREATED)
    PrestataireDto creer(@Valid @RequestBody PrestataireRequete requete) {
        return service.creer(requete.enSaisie());
    }

    @PutMapping("/prestataires/{id}")
    PrestataireDto modifier(@PathVariable UUID id, @Valid @RequestBody PrestataireRequete requete) {
        return service.modifier(id, requete.enSaisie());
    }

    @DeleteMapping("/prestataires/{id}")
    ResponseEntity<Void> archiver(@PathVariable UUID id) {
        service.archiver(id);
        return ResponseEntity.noContent().build();
    }

    /** Recherche rapide par nom ou téléphone parmi les propriétaires, locataires et prestataires. */
    @GetMapping("/tiers/recherche")
    List<ResultatRecherche> rechercher(@RequestParam String q) {
        return recherche.rechercher(q);
    }
}
