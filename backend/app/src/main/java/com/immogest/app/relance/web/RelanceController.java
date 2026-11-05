package com.immogest.app.relance.web;

import com.immogest.app.relance.ArriereDto;
import com.immogest.app.relance.BilanRelances;
import com.immogest.app.relance.MiseEnDemeureDto;
import com.immogest.app.relance.ParametresRelanceDto;
import com.immogest.app.relance.RelanceDto;
import com.immogest.app.relance.RelanceService;
import com.immogest.app.shared.PageReponse;
import com.immogest.app.shared.Pagination;
import com.immogest.app.tiers.api.LecteurTiers;
import com.immogest.app.shared.ActeurCourant;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
class RelanceController {

    private static final String GESTION = "hasAnyRole('ADMIN', 'GESTIONNAIRE')";

    record ParametresRequete(boolean actif, @NotNull List<Integer> niveaux, int delaiMiseEnDemeureJours) {
    }

    record EcheanceRequete(@NotNull UUID echeanceId) {
    }

    private final RelanceService service;
    private final ActeurCourant acteurCourant;
    private final LecteurTiers tiers;

    RelanceController(RelanceService service, ActeurCourant acteurCourant, LecteurTiers tiers) {
        this.service = service;
        this.acteurCourant = acteurCourant;
        this.tiers = tiers;
    }

    @GetMapping("/relances/parametres")
    @PreAuthorize(GESTION)
    ParametresRelanceDto parametres() {
        return service.parametres();
    }

    @PutMapping("/relances/parametres")
    @PreAuthorize("hasRole('ADMIN')")
    ParametresRelanceDto modifier(@Valid @RequestBody ParametresRequete r) {
        return service.modifier(new ParametresRelanceDto(r.actif(), r.niveaux(), r.delaiMiseEnDemeureJours()));
    }

    /** Lance le job tout de suite (il tourne déjà chaque matin). Idempotent. */
    @PostMapping("/relances/executer")
    @PreAuthorize("hasRole('ADMIN')")
    BilanRelances executer() {
        return service.executer();
    }

    @GetMapping("/relances")
    @PreAuthorize(GESTION)
    PageReponse<RelanceDto> historique(@RequestParam(required = false) UUID echeanceId, @RequestParam(required = false) UUID bailId,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return PageReponse.de(service.historique(echeanceId, bailId, Pagination.de(page, size, "createdAt,desc", Set.of("createdAt"), "createdAt")));
    }

    @PostMapping("/relances")
    @PreAuthorize(GESTION)
    @ResponseStatus(HttpStatus.CREATED)
    RelanceDto relancer(@Valid @RequestBody EcheanceRequete r) {
        return service.relancer(r.echeanceId());
    }

    @GetMapping("/arrieres")
    @PreAuthorize(GESTION)
    PageReponse<ArriereDto> arrieres(@RequestParam(required = false) String q, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sort) {
        return PageReponse.de(service.arrieres(q, Pagination.de(page, size, sort, Set.of("dateEcheance", "montant"), "dateEcheance")));
    }

    @PostMapping("/mises-en-demeure")
    @PreAuthorize(GESTION)
    @ResponseStatus(HttpStatus.CREATED)
    MiseEnDemeureDto miseEnDemeure(@Valid @RequestBody EcheanceRequete r) {
        return service.miseEnDemeure(r.echeanceId());
    }

    /** Gestionnaires : toutes (filtre par bail). Locataire : celles de ses baux. */
    @GetMapping("/mises-en-demeure")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE', 'LOCATAIRE')")
    PageReponse<MiseEnDemeureDto> lister(@RequestParam(required = false) UUID bailId, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        UUID locataire = acteurCourant.acteur().filter(a -> a.aLeRole("LOCATAIRE"))
                .map(a -> tiers.locataireDeUtilisateur(a.id()).orElse(new UUID(0, 0))).orElse(null);
        return PageReponse.de(service.miseEnDemeures(bailId, locataire, Pagination.de(page, size, "createdAt,desc", Set.of("createdAt"), "createdAt")));
    }
}
