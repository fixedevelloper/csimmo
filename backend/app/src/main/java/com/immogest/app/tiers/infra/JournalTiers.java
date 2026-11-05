package com.immogest.app.tiers.infra;

import com.immogest.app.shared.ActeurCourant;
import com.immogest.app.tiers.EvenementTiers;
import java.util.Map;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/** Publie les événements d'audit du module (jamais de donnée d'identité en clair). */
@Component
public class JournalTiers {

    private final ApplicationEventPublisher evenements;
    private final ActeurCourant acteurCourant;

    JournalTiers(ApplicationEventPublisher evenements, ActeurCourant acteurCourant) {
        this.evenements = evenements;
        this.acteurCourant = acteurCourant;
    }

    public void publier(String action, String entite, UUID id, Map<String, Object> avant, Map<String, Object> apres) {
        String auteur = acteurCourant.acteur().map(a -> a.id().toString()).orElse("systeme");
        evenements.publishEvent(new EvenementTiers(action, entite, id, avant, apres, auteur));
    }
}
