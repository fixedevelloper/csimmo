package com.immogest.app.finance;

import com.immogest.app.bail.api.BailResume;
import com.immogest.app.bail.api.LecteurBaux;
import com.immogest.app.finance.domain.Echeance;
import com.immogest.app.finance.infra.EcheanceRepository;
import com.immogest.app.shared.ErreurMetier;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Reprise d'un bail déjà en cours avant la mise en service (import de données). Les échéances dont la date d'échéance précède la date de
 * reprise sont créées <b>déjà réglées</b>, sauf {@code soldeDu} FCFA d'impayés, imputés aux échéances les plus récentes (un locataire en retard
 * doit ses derniers mois). Aucun paiement ni imputation n'est créé : ni la caisse ni les reversements ne comptent l'historique comme encaissé
 * aujourd'hui. Le moteur d'échéances (inchangé) crée ensuite les périodes à venir ; il laisse intactes celles qui existent.
 */
@Service
public class RepriseService {

    private final LecteurBaux baux;
    private final EcheanceRepository echeances;
    private final EcheanceService moteur;
    private final FinanceProperties proprietes;
    private final ApplicationEventPublisher evenements;

    RepriseService(LecteurBaux baux, EcheanceRepository echeances, EcheanceService moteur, FinanceProperties proprietes, ApplicationEventPublisher evenements) {
        this.baux = baux;
        this.echeances = echeances;
        this.moteur = moteur;
        this.proprietes = proprietes;
        this.evenements = evenements;
    }

    /** @return nombre d'échéances d'historique créées */
    @Transactional
    public int reprendre(UUID bailId, LocalDate dateReprise, long soldeDu) {
        if (soldeDu < 0) {
            throw new ErreurMetier("REPRISE_SOLDE_NEGATIF", "Le solde d'ouverture ne peut pas être négatif", HttpStatus.BAD_REQUEST);
        }
        BailResume bail = baux.bail(bailId).orElseThrow(() -> new ErreurMetier("BAIL_INTROUVABLE", "Bail introuvable", HttpStatus.NOT_FOUND));
        moteur.verrouiller(bailId);
        if (!echeances.findByBailIdOrderByPeriodeDebutAsc(bailId).isEmpty()) {
            throw new ErreurMetier("REPRISE_DEJA_FAITE", "Ce bail a déjà des échéances", HttpStatus.CONFLICT);
        }
        List<CalculEcheances.Periode> historique = CalculEcheances.periodes(bail.dateDebut(), bail.dateFinEffective(), EcheanceService.Periodicite.mois(bail.periodicite()),
                bail.loyer() + bail.charges(), bail.jourEcheance(), dateReprise.minusDays(1));
        long total = historique.stream().mapToLong(CalculEcheances.Periode::montant).sum();
        if (soldeDu > total) {
            throw new ErreurMetier("REPRISE_SOLDE_SUPERIEUR_AU_LOYER", "Le solde (%d FCFA) dépasse les loyers dus depuis le début du bail (%d FCFA)".formatted(soldeDu, total),
                    HttpStatus.BAD_REQUEST);
        }
        LocalDate aujourdhui = moteur.aujourdhui();
        long restant = soldeDu;
        List<CalculEcheances.Periode> recentesDabord = new ArrayList<>(historique);
        Collections.reverse(recentesDabord);
        for (CalculEcheances.Periode p : recentesDabord) {
            long impaye = Math.min(restant, p.montant());
            restant -= impaye;
            long paye = p.montant() - impaye;
            Echeance e = new Echeance(bailId, p.debut(), p.fin(), p.dateEcheance(), p.montant(),
                    CalculEcheances.statut(p.montant(), paye, p.dateEcheance(), aujourdhui, proprietes.delaiGraceJours()));
            e.reprendre(paye);
            echeances.save(e);
        }
        echeances.flush();
        evenements.publishEvent(new EvenementsReprise.RepriseEffectuee(bailId, dateReprise, historique.size(), soldeDu));
        return historique.size();
    }
}
