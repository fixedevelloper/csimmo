package com.immogest.app.patrimoine.infra;

import com.immogest.app.patrimoine.api.LecteurLots;
import com.immogest.app.patrimoine.api.LotResume;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
class LecteurLotsImpl implements LecteurLots {

    private final LotRepository lots;
    private final ImmeubleRepository immeubles;

    LecteurLotsImpl(LotRepository lots, ImmeubleRepository immeubles) {
        this.lots = lots;
        this.immeubles = immeubles;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<com.immogest.app.patrimoine.api.ImmeubleResume> immeuble(UUID immeubleId) {
        return immeubles.findById(immeubleId).map(i -> new com.immogest.app.patrimoine.api.ImmeubleResume(i.getId(), i.getNom(), i.isArchive(),
                i.getProprietaireTiersId()));
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.Map<String, Long> compterParStatut() {
        java.util.Map<String, Long> r = new java.util.LinkedHashMap<>();
        for (var statut : com.immogest.app.patrimoine.StatutLot.values()) {
            r.put(statut.name(), 0L);
        }
        lots.compterParStatut().forEach(l -> r.put(((com.immogest.app.patrimoine.StatutLot) l[0]).name(), (Long) l[1]));
        return r;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LotResume> lot(UUID lotId) {
        return lots.findById(lotId).map(LecteurLotsImpl::resume);
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.Map<UUID, LotResume> lots(java.util.Collection<UUID> lotIds) {
        java.util.Map<UUID, LotResume> r = new java.util.HashMap<>();
        if (!lotIds.isEmpty()) {
            lots.findAllById(lotIds).forEach(l -> r.put(l.getId(), resume(l)));
        }
        return r;
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.Set<UUID> lotIdsDesImmeubles(java.util.Collection<UUID> immeubleIds) {
        return immeubleIds.isEmpty() ? java.util.Set.of() : new java.util.HashSet<>(lots.idsDesImmeubles(immeubleIds));
    }

    private static LotResume resume(com.immogest.app.patrimoine.domain.Lot l) {
        return new LotResume(l.getId(), l.getImmeuble().getId(), l.getReference(),
                l.getStatut().name(), l.getLoyerIndicatif(), l.isArchive(), l.getType().name(), l.getSurfaceM2(), l.getPieces(),
                l.getImmeuble().getNom(), l.getImmeuble().getAdresse(), l.getImmeuble().getQuartier(), l.getImmeuble().getVille(),
                l.getImmeuble().getProprietaireTiersId());
    }
}
