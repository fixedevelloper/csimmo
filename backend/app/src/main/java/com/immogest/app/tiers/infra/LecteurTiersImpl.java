package com.immogest.app.tiers.infra;

import com.immogest.app.tiers.api.LecteurTiers;
import com.immogest.app.stockage.ChiffrementDonnees;
import com.immogest.app.tiers.api.GarantContrat;
import com.immogest.app.tiers.api.LocataireContrat;
import com.immogest.app.tiers.api.LocataireResume;
import com.immogest.app.tiers.api.MandatResume;
import com.immogest.app.tiers.domain.ProprietaireTiers;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
class LecteurTiersImpl implements LecteurTiers {

    private final ProprietaireTiersRepository proprietaires;
    private final LocataireRepository locataires;
    private final MandatRepository mandats;
    private final GarantRepository garants;
    private final ChiffrementDonnees chiffrement;

    LecteurTiersImpl(ProprietaireTiersRepository proprietaires, LocataireRepository locataires, MandatRepository mandats,
            GarantRepository garants, ChiffrementDonnees chiffrement) {
        this.proprietaires = proprietaires;
        this.locataires = locataires;
        this.mandats = mandats;
        this.garants = garants;
        this.chiffrement = chiffrement;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UUID> proprietaireDeUtilisateur(UUID utilisateurId) {
        return proprietaires.findByUtilisateurId(utilisateurId).filter(p -> !p.isArchive()).map(ProprietaireTiers::getId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean proprietaireActif(UUID proprietaireId) {
        return proprietaires.findById(proprietaireId).map(p -> !p.isArchive()).orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LocataireResume> locataire(UUID locataireId) {
        return locataires.findById(locataireId)
                .map(l -> new LocataireResume(l.getId(), l.getNom(), l.getTelephone(), l.getEmail(), l.isArchive()));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UUID> locataireDeUtilisateur(UUID utilisateurId) {
        return locataires.findByUtilisateurId(utilisateurId).filter(l -> !l.isArchive()).map(l -> l.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<String> nomProprietaire(UUID proprietaireId) {
        return proprietaires.findById(proprietaireId).map(p -> p.getNom());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UUID> rechercherLocataires(String q) {
        return locataires.findAll(RechercheSpecifications.<com.immogest.app.tiers.domain.Locataire>parNomOuTelephone(q, false),
                org.springframework.data.domain.PageRequest.of(0, 200)).stream().map(l -> l.getId()).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UUID> utilisateurDuLocataire(UUID locataireId) {
        return locataires.findById(locataireId).map(l -> l.getUtilisateurId());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LocataireContrat> locatairePourContrat(UUID locataireId) {
        return locataires.findById(locataireId).filter(l -> !l.isArchive())
                .map(l -> new LocataireContrat(l.getId(), l.getNom(), l.getProfession(), l.getTelephone(), l.getEmail(), l.getAdresse(),
                        l.getDateNaissance(), chiffrement.dechiffrer(l.getNumeroCniChiffre())));
    }

    @Override
    @Transactional(readOnly = true)
    public List<GarantContrat> garantsPourContrat(UUID locataireId, Collection<UUID> garantIds) {
        return garants.findAllById(garantIds).stream()
                .filter(g -> g.getLocataireId().equals(locataireId) && !g.isArchive())
                .map(g -> new GarantContrat(g.getId(), g.getNom(), g.getLien(), g.getProfession(), g.getTelephone(), g.getAdresse(),
                        chiffrement.dechiffrer(g.getNumeroCniChiffre())))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MandatResume> mandatEnVigueur(UUID proprietaireId, LocalDate date) {
        return mandats.findByProprietaireIdOrderByDateDebutDesc(proprietaireId).stream().filter(m -> m.couvre(date)).findFirst()
                .map(m -> new MandatResume(m.getId(), m.getProprietaireId(), m.getTauxHonorairesBp(), m.getDateDebut(), m.getDateFin()));
    }
}
