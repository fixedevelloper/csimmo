package com.immogest.app.paiementenligne.web;

import com.immogest.app.paiementenligne.FournisseurDto;
import com.immogest.app.paiementenligne.InitiationSaisie;
import com.immogest.app.paiementenligne.OperateurMobile;
import com.immogest.app.paiementenligne.PaiementEnLigneService;
import com.immogest.app.paiementenligne.StatutTransaction;
import com.immogest.app.paiementenligne.TransactionDto;
import com.immogest.app.shared.PageReponse;
import com.immogest.app.shared.Pagination;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/paiements-en-ligne")
class PaiementEnLigneController {

    private static final Set<String> TRIS = Set.of("createdAt", "montant", "statut");

    /** @param montant FCFA entiers */
    record InitiationRequete(@NotNull UUID bailId, @NotNull @Min(1) @Max(20_000_000) Long montant, @NotNull OperateurMobile operateur,
            @NotBlank @Size(max = 30) String telephone) {
    }

    record FournisseurRequete(boolean actif, Map<String, @Size(max = 500) String> champs) {
    }

    private final PaiementEnLigneService service;

    PaiementEnLigneController(PaiementEnLigneService service) {
        this.service = service;
    }

    /**
     * Le locataire lance un paiement Mobile Money : il le valide ensuite sur son téléphone. Réponse 201 ; un rejeu de la même
     * {@code Idempotency-Key} renvoie 200 et la transaction d'origine. Aucun paiement n'existe tant que l'opérateur n'a pas confirmé.
     */
    @PostMapping
    @PreAuthorize("hasRole('LOCATAIRE')")
    @io.swagger.v3.oas.annotations.Operation(operationId = "initierPaiementEnLigne")
    ResponseEntity<TransactionDto> initier(@Valid @RequestBody InitiationRequete r, @RequestHeader(value = "Idempotency-Key", required = false) String cle) {
        var res = service.initier(new InitiationSaisie(r.bailId(), r.montant(), r.operateur(), r.telephone()), cle);
        return ResponseEntity.status(res.rejoue() ? HttpStatus.OK : HttpStatus.CREATED).body(res.transaction());
    }

    /** Suivi d'une transaction (l'application mobile l'interroge jusqu'à CONFIRMEE ou ECHOUEE). */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE', 'LOCATAIRE')")
    @io.swagger.v3.oas.annotations.Operation(operationId = "obtenirPaiementEnLigne")
    TransactionDto obtenir(@PathVariable UUID id) {
        return service.obtenir(id);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE', 'LOCATAIRE')")
    PageReponse<TransactionDto> lister(@RequestParam(required = false) StatutTransaction statut, @RequestParam(required = false) UUID bailId,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size, @RequestParam(required = false) String sort) {
        return PageReponse.de(service.lister(statut, bailId, Pagination.de(page, size, sort, TRIS, "createdAt")));
    }

    @GetMapping("/fournisseurs")
    @PreAuthorize("hasRole('ADMIN')")
    List<FournisseurDto> fournisseurs() {
        return service.fournisseurs();
    }

    @PutMapping("/fournisseurs/{code}")
    @PreAuthorize("hasRole('ADMIN')")
    FournisseurDto modifierFournisseur(@PathVariable String code, @Valid @RequestBody FournisseurRequete r) {
        return service.modifierFournisseur(code, r.actif(), r.champs());
    }
}
