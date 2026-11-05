package com.immogest.app.finance.web;

import com.immogest.app.finance.DocumentDto;
import com.immogest.app.finance.DocumentService;
import com.immogest.app.shared.PageReponse;
import com.immogest.app.shared.Pagination;
import java.util.Set;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Quittances et reçus. Le PDF se télécharge par {@code GET /api/v1/fichiers/{fichierId}}. */
@RestController
@RequestMapping("/api/v1/documents-paiement")
class DocumentController {

    private static final Set<String> TRIS = Set.of("numero", "dateEmission", "montant", "createdAt");

    private final DocumentService service;

    DocumentController(DocumentService service) {
        this.service = service;
    }

    /** Gestionnaires : tout (filtres). Locataire : les siens (documents annulés exclus). */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE', 'LOCATAIRE')")
    @io.swagger.v3.oas.annotations.Operation(operationId = "listerDocumentsPaiement")
    PageReponse<DocumentDto> lister(@RequestParam(required = false) UUID bailId, @RequestParam(required = false) UUID locataireId,
            @RequestParam(required = false) String type, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size, @RequestParam(required = false) String sort) {
        String t = type == null ? null : type.toUpperCase();
        if (t != null && !t.equals("QUITTANCE") && !t.equals("RECU")) {
            t = "?"; // valeur inconnue : aucun résultat plutôt qu'une erreur
        }
        return PageReponse.de(service.lister(bailId, locataireId, t, Pagination.de(page, size, sort, TRIS, "createdAt")));
    }
}
