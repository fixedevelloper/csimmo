package com.immogest.app.tableaudebord.web;

import com.immogest.app.tableaudebord.DashboardDto.Arrieres;
import com.immogest.app.tableaudebord.DashboardDto.BailAEcheance;
import com.immogest.app.tableaudebord.DashboardDto.Mois;
import com.immogest.app.tableaudebord.DashboardDto.Synthese;
import com.immogest.app.tableaudebord.DashboardService;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Réservé aux gestionnaires : ces chiffres couvrent tout le patrimoine (la vue propriétaire viendra avec le relevé de gérance). */
@RestController
@RequestMapping("/api/v1/tableau-de-bord")
@PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE')")
class DashboardController {

    private final DashboardService service;

    DashboardController(DashboardService service) {
        this.service = service;
    }

    @GetMapping("/synthese")
    @io.swagger.v3.oas.annotations.Operation(operationId = "syntheseTableauDeBord")
    Synthese synthese() {
        return service.synthese();
    }

    @GetMapping("/attendu-encaisse")
    List<Mois> attenduEncaisse(@RequestParam(defaultValue = "12") int mois) {
        return service.attenduEncaisse(mois);
    }

    @GetMapping("/arrieres")
    Arrieres arrieres(@RequestParam(defaultValue = "10") int limite) {
        return service.arrieres(limite);
    }

    @GetMapping("/baux-a-echeance")
    List<BailAEcheance> bauxAEcheance(@RequestParam(defaultValue = "60") int jours) {
        return service.bauxAEcheance(jours);
    }
}
