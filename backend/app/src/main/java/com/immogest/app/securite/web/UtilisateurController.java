package com.immogest.app.securite.web;

import com.immogest.app.securite.Role;
import com.immogest.app.securite.UtilisateurDto;
import com.immogest.app.securite.UtilisateurService;
import com.immogest.app.shared.PageReponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

/** Réservé au rôle ADMIN (règle dans la configuration de sécurité). */
@RestController
@org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')") // en plus de la règle HTTP : un contrôle de moins ne doit pas ouvrir la porte
@RequestMapping("/api/v1/utilisateurs")
class UtilisateurController {

    record CreationRequete(@NotBlank @Size(max = 200) String nom, @Size(max = 30) String telephone,
            @Size(max = 200) String email, @NotNull Role role, boolean deuxFacteursActif,
            @NotBlank @Size(min = 8, max = 100) String motDePasse) {
    }

    record ModificationRequete(@NotBlank @Size(max = 200) String nom, @Size(max = 30) String telephone,
            @Size(max = 200) String email, @NotNull Role role, boolean actif, boolean deuxFacteursActif) {
    }

    record ReinitialisationRequete(@NotBlank @Size(min = 8, max = 100) String motDePasse) {
    }

    record AffectationsRequete(@jakarta.validation.constraints.NotNull @Size(max = 500) java.util.List<UUID> immeubleIds) {
    }

    record AffectationsReponse(java.util.List<UUID> immeubleIds) {
    }

    private final UtilisateurService service;
    private final com.immogest.app.securite.AffectationService affectations;

    UtilisateurController(UtilisateurService service, com.immogest.app.securite.AffectationService affectations) {
        this.service = service;
        this.affectations = affectations;
    }

    @GetMapping
    PageReponse<UtilisateurDto> lister(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size, @RequestParam(defaultValue = "nom") String sort) {
        int taille = Math.min(Math.max(size, 1), 100);
        String champ = switch (sort) {
            case "nom", "role", "createdAt" -> sort;
            default -> "nom";
        };
        return PageReponse.de(service.lister(PageRequest.of(Math.max(page, 0), taille, Sort.by(champ))));
    }

    @GetMapping("/{id}")
    UtilisateurDto obtenir(@PathVariable UUID id) {
        return service.obtenir(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    UtilisateurDto creer(@Valid @RequestBody CreationRequete r) {
        return service.creer(new UtilisateurService.Saisie(r.nom(), r.telephone(), r.email(), r.role(), true,
                r.deuxFacteursActif()), r.motDePasse());
    }

    @PutMapping("/{id}")
    UtilisateurDto modifier(@PathVariable UUID id, @Valid @RequestBody ModificationRequete r) {
        return service.modifier(id, new UtilisateurService.Saisie(r.nom(), r.telephone(), r.email(), r.role(),
                r.actif(), r.deuxFacteursActif()));
    }

    /** Désactive le compte (pas de suppression physique). */
    @DeleteMapping("/{id}")
    ResponseEntity<Void> desactiver(@PathVariable UUID id) {
        service.desactiver(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/deverrouiller")
    ResponseEntity<Void> deverrouiller(@PathVariable UUID id) {
        service.deverrouiller(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/mot-de-passe")
    ResponseEntity<Void> reinitialiser(@PathVariable UUID id, @Valid @RequestBody ReinitialisationRequete r) {
        service.reinitialiserMotDePasse(id, r.motDePasse());
        return ResponseEntity.noContent().build();
    }

    /** Immeubles affectés à un employé restreint. */
    @GetMapping("/{id}/affectations")
    @io.swagger.v3.oas.annotations.Operation(operationId = "listerAffectations")
    AffectationsReponse listerAffectations(@PathVariable UUID id) {
        return new AffectationsReponse(affectations.lister(id));
    }

    /** Remplace les immeubles affectés à un employé restreint (les autres rôles : 409). */
    @PutMapping("/{id}/affectations")
    @io.swagger.v3.oas.annotations.Operation(operationId = "definirAffectations")
    AffectationsReponse definirAffectations(@PathVariable UUID id, @Valid @RequestBody AffectationsRequete r) {
        return new AffectationsReponse(affectations.definir(id, r.immeubleIds()));
    }
}
