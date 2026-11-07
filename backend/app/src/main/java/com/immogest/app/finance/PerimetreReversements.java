package com.immogest.app.finance;

import com.immogest.app.patrimoine.api.LecteurLots;
import com.immogest.app.shared.PerimetreAcces;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * Ce qu'un employé restreint voit des reversements et dépenses (docs/adr/0009-equipe-et-perimetres.md) : une dépense se rattache à un
 * immeuble ; un reversement se rattache à un propriétaire, donc à tous ses immeubles : il n'est visible que si TOUS les immeubles du
 * propriétaire sont dans le périmètre (sinon ses montants révéleraient un immeuble hors périmètre).
 */
@Component
public class PerimetreReversements {

    private final PerimetreAcces perimetre;
    private final LecteurLots lots;

    PerimetreReversements(PerimetreAcces perimetre, LecteurLots lots) {
        this.perimetre = perimetre;
        this.lots = lots;
    }

    /** Immeubles visibles ; vide = aucune restriction. */
    public Optional<Set<UUID>> immeubles() {
        return perimetre.immeublesVisibles();
    }

    /** Propriétaires dont les reversements sont visibles ; vide = aucune restriction. */
    public Optional<Set<UUID>> proprietaires() {
        return perimetre.immeublesVisibles().map(lots::proprietairesEntierementDans);
    }
}
