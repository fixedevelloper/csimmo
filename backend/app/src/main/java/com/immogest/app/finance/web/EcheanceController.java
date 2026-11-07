package com.immogest.app.finance.web;

import com.immogest.app.finance.BilanGeneration;
import com.immogest.app.finance.EcheanceDto;
import com.immogest.app.finance.EcheanceService;
import com.immogest.app.finance.StatutEcheance;
import com.immogest.app.shared.PageReponse;
import com.immogest.app.shared.Pagination;
import java.util.Set;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/echeances")
class EcheanceController {

    private static final Set<String> TRIS = Set.of("periodeDebut", "dateEcheance", "montant", "statut");

    private final EcheanceService service;

    EcheanceController(EcheanceService service) {
        this.service = service;
    }

    /** Gestionnaires : toutes les échéances (filtres). Locataire : seulement celles d'un de ses baux (`bailId` obligatoire). */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE', 'GESTIONNAIRE_BIENS', 'LOCATAIRE', 'ENCAISSEUR', 'COMPTABLE')")
    @io.swagger.v3.oas.annotations.Operation(operationId = "listerEcheances")
    PageReponse<EcheanceDto> lister(@RequestParam(required = false) UUID bailId, @RequestParam(required = false) StatutEcheance statut,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sort) {
        return PageReponse.de(service.lister(bailId, statut, Pagination.de(page, size, sort, TRIS, "periodeDebut")));
    }

    /** Déclenche le moteur à la demande (il tourne déjà chaque nuit). Idempotent. */
    @PostMapping("/generer")
    @PreAuthorize("hasRole('ADMIN')")
    BilanGeneration generer() {
        return service.executer();
    }
}
