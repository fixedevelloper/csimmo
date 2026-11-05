package com.immogest.app.notification.web;

import com.immogest.app.notification.CanalParametresDto;
import com.immogest.app.notification.CanalSaisie;
import com.immogest.app.notification.MessageDto;
import com.immogest.app.notification.NotificationService;
import com.immogest.app.notification.ResultatTest;
import com.immogest.app.notification.StatutMessage;
import com.immogest.app.notification.TypeCanal;
import com.immogest.app.shared.ActeurCourant;
import com.immogest.app.shared.ErreurMetier;
import com.immogest.app.shared.PageReponse;
import com.immogest.app.shared.Pagination;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/v1/notifications")
class NotificationController {

    private static final Set<String> TRIS = Set.of("createdAt", "statut", "canal");

    record CanalRequete(boolean actif, Map<String, @Size(max = 2000) String> champs) {
    }

    record TestRequete(@NotBlank @Size(max = 500) String destinataire) {
    }

    record AppareilRequete(@NotBlank @Size(max = 4096) String token, @NotBlank String plateforme) {
    }

    private final NotificationService service;
    private final ActeurCourant acteurCourant;

    NotificationController(NotificationService service, ActeurCourant acteurCourant) {
        this.service = service;
        this.acteurCourant = acteurCourant;
    }

    @GetMapping("/canaux")
    @PreAuthorize("hasRole('ADMIN')")
    List<CanalParametresDto> canaux() {
        return service.parametres();
    }

    @PutMapping("/canaux/{canal}")
    @PreAuthorize("hasRole('ADMIN')")
    CanalParametresDto modifier(@PathVariable TypeCanal canal, @Valid @RequestBody CanalRequete r) {
        return service.modifier(canal, new CanalSaisie(r.actif(), r.champs()));
    }

    @PostMapping("/canaux/{canal}/test")
    @PreAuthorize("hasRole('ADMIN')")
    ResultatTest tester(@PathVariable TypeCanal canal, @Valid @RequestBody TestRequete r) {
        return service.tester(canal, r.destinataire());
    }

    @GetMapping("/journal")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE')")
    PageReponse<MessageDto> journal(@RequestParam(required = false) StatutMessage statut, @RequestParam(required = false) TypeCanal canal,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size, @RequestParam(required = false) String sort) {
        return PageReponse.de(service.journal(statut, canal, Pagination.de(page, size, sort, TRIS, "createdAt")));
    }

    @PostMapping("/journal/{id}/reessayer")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void reessayer(@PathVariable UUID id) {
        service.reessayer(id);
    }

    /** Identifiants publics à utiliser par l'application mobile (Firebase de l'installation), pour tout utilisateur connecté. */
    @GetMapping("/mobile-config")
    @PreAuthorize("isAuthenticated()")
    com.immogest.app.notification.ConfigMobileDto configMobile() {
        return service.configMobile();
    }

    /** Enregistrement de l'appareil de l'utilisateur connecté pour les notifications push (appelé par l'application mobile). */
    @PostMapping("/appareils")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void enregistrerAppareil(@Valid @RequestBody AppareilRequete r) {
        service.enregistrerAppareil(moi(), r.token(), r.plateforme());
    }

    @DeleteMapping("/appareils")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void retirerAppareil(@RequestParam String token) {
        service.retirerAppareil(moi(), token);
    }

    private UUID moi() {
        return acteurCourant.acteur().orElseThrow(() -> new ErreurMetier("NON_AUTHENTIFIE", "Authentification requise", HttpStatus.UNAUTHORIZED)).id();
    }
}
