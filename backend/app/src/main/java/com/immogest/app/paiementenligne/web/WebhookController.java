package com.immogest.app.paiementenligne.web;

import com.immogest.app.paiementenligne.PaiementEnLigneService;
import com.immogest.app.paiementenligne.ResultatWebhook;
import com.immogest.app.shared.ErreurMetier;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Webhook des agrégateurs : route publique (pas de JWT, c'est l'agrégateur qui appelle), authentifiée par la signature de la requête.
 * Le corps est lu brut (octets exacts signés) et plafonné.
 */
@RestController
@RequestMapping("/api/v1/webhooks")
class WebhookController {

    static final int TAILLE_MAX = 64 * 1024;

    record Accuse(ResultatWebhook resultat) {
    }

    private final PaiementEnLigneService service;

    WebhookController(PaiementEnLigneService service) {
        this.service = service;
    }

    @PostMapping("/{fournisseur}")
    Accuse recevoir(@PathVariable String fournisseur, HttpServletRequest requete) throws IOException {
        if (requete.getContentLengthLong() > TAILLE_MAX) {
            throw new ErreurMetier("WEBHOOK_TROP_VOLUMINEUX", "Corps trop volumineux", HttpStatus.PAYLOAD_TOO_LARGE);
        }
        byte[] corps = requete.getInputStream().readNBytes(TAILLE_MAX + 1);
        if (corps.length > TAILLE_MAX) {
            throw new ErreurMetier("WEBHOOK_TROP_VOLUMINEUX", "Corps trop volumineux", HttpStatus.PAYLOAD_TOO_LARGE);
        }
        Map<String, String> entetes = new HashMap<>();
        for (String nom : Collections.list(requete.getHeaderNames())) {
            entetes.put(nom.toLowerCase(), requete.getHeader(nom));
        }
        return new Accuse(service.recevoirWebhook(fournisseur, entetes, corps));
    }
}
