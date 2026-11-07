package com.immogest.app.bail.infra;

import com.immogest.app.bail.StatutBail;
import com.immogest.app.bail.api.BailResume;
import com.immogest.app.bail.api.LecteurBaux;
import com.immogest.app.bail.domain.Bail;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
class LecteurBauxImpl implements LecteurBaux, com.immogest.app.shared.LocatairesDuPerimetre {

    private final BailRepository baux;
    private final com.immogest.app.bail.ModeleBailService modele;
    private final com.immogest.app.shared.PerimetreAcces perimetre;
    private final com.immogest.app.patrimoine.api.LecteurLots lots;

    LecteurBauxImpl(BailRepository baux, com.immogest.app.bail.ModeleBailService modele, com.immogest.app.shared.PerimetreAcces perimetre,
            com.immogest.app.patrimoine.api.LecteurLots lots) {
        this.baux = baux;
        this.modele = modele;
        this.perimetre = perimetre;
        this.lots = lots;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<java.util.Set<UUID>> bailIdsVisibles() {
        return perimetre.immeublesVisibles().map(immeubles -> {
            var lotIds = lots.lotIdsDesImmeubles(immeubles);
            return lotIds.isEmpty() ? java.util.Set.<UUID>of() : new java.util.HashSet<>(baux.idsDesLots(lotIds));
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<java.util.Set<UUID>> locatairesVisibles() {
        return perimetre.immeublesVisibles().map(immeubles -> {
            var lotIds = lots.lotIdsDesImmeubles(immeubles);
            return lotIds.isEmpty() ? java.util.Set.<UUID>of() : new java.util.HashSet<>(baux.locataireIdsDesLots(lotIds));
        });
    }

    @Override
    @Transactional(readOnly = true)
    public String nomBailleur() {
        return modele.obtenir().bailleurNom();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BailResume> bail(UUID bailId) {
        return baux.findById(bailId).map(LecteurBauxImpl::resume);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BailResume> baux(java.util.Collection<UUID> bailIds) {
        return bailIds.isEmpty() ? List.of() : baux.findAllById(bailIds).stream().map(LecteurBauxImpl::resume).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BailResume> bauxEnVigueur(LocalDate date) {
        return baux.enVigueur(EnumSet.of(StatutBail.ACTIF, StatutBail.RESILIE), date).stream().map(LecteurBauxImpl::resume).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BailResume> bauxArrivantAEcheance(LocalDate du, LocalDate au) {
        return baux.arrivantAEcheance(EnumSet.of(StatutBail.ACTIF, StatutBail.RESILIE), du, au).stream().map(LecteurBauxImpl::resume).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BailResume> bauxDuLocataire(UUID locataireId) {
        return baux.signesDuLocataire(locataireId).stream().map(LecteurBauxImpl::resume).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BailResume> bauxSignesCommencesAvant(LocalDate date) {
        return baux.signesCommencesAvant(date).stream().map(LecteurBauxImpl::resume).toList();
    }

    private static BailResume resume(Bail b) {
        return new BailResume(b.getId(), b.getNumero(), b.getLotId(), b.getLocataireId(), b.getStatut().name(), b.getDateDebut(),
                b.getDateFinEffective(), b.getLoyer(), b.getCharges(), b.getPeriodicite().name(), b.getJourEcheance(), b.getCaution(),
                b.getMoisAvance());
    }
}
