package com.immogest.app.tableaudebord;

import com.immogest.app.bail.api.BailResume;
import com.immogest.app.bail.api.LecteurBaux;
import com.immogest.app.finance.ArriereBail;
import com.immogest.app.finance.EcheanceService;
import com.immogest.app.finance.MoisSynthese;
import com.immogest.app.patrimoine.api.LecteurLots;
import com.immogest.app.patrimoine.api.LotResume;
import com.immogest.app.shared.ErreurMetier;
import com.immogest.app.tableaudebord.DashboardDto.Arrieres;
import com.immogest.app.tableaudebord.DashboardDto.BailAEcheance;
import com.immogest.app.tableaudebord.DashboardDto.LigneArriere;
import com.immogest.app.tableaudebord.DashboardDto.Mois;
import com.immogest.app.tableaudebord.DashboardDto.Occupation;
import com.immogest.app.tableaudebord.DashboardDto.Synthese;
import com.immogest.app.tiers.api.LecteurTiers;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Indicateurs de pilotage, calculés à la demande à partir des API des modules propriétaires des données. */
@Service
public class DashboardService {

    static final int JOURS_ECHEANCE_BAIL = 60;

    private final LecteurLots lots;
    private final LecteurBaux baux;
    private final LecteurTiers tiers;
    private final EcheanceService finance;

    DashboardService(LecteurLots lots, LecteurBaux baux, LecteurTiers tiers, EcheanceService finance) {
        this.lots = lots;
        this.baux = baux;
        this.tiers = tiers;
        this.finance = finance;
    }

    @Transactional(readOnly = true)
    public Synthese synthese() {
        LocalDate aujourdhui = finance.aujourdhui();
        Map<String, Long> c = lots.compterParStatut();
        long libres = c.getOrDefault("LIBRE", 0L);
        long occupes = c.getOrDefault("OCCUPE", 0L);
        Occupation occupation = new Occupation(libres, occupes, c.getOrDefault("EN_TRAVAUX", 0L), taux(occupes, libres + occupes));
        MoisSynthese mois = finance.syntheseMensuelle(aujourdhui, aujourdhui).get(0);
        List<ArriereBail> arrieres = finance.arrieresParBail();
        return new Synthese(occupation, mois.attendu(), mois.encaisse(), taux(mois.encaisse(), mois.attendu()),
                arrieres.stream().mapToLong(ArriereBail::total).sum(), arrieres.stream().mapToInt(ArriereBail::nombre).sum(),
                baux.bauxArrivantAEcheance(aujourdhui, aujourdhui.plusDays(JOURS_ECHEANCE_BAIL)).size());
    }

    /** Les {@code mois} derniers mois, le mois courant compris, du plus ancien au plus récent. */
    @Transactional(readOnly = true)
    public List<Mois> attenduEncaisse(int mois) {
        if (mois < 1 || mois > 24) {
            throw new ErreurMetier("DASHBOARD_PERIODE_INVALIDE", "La période est de 1 à 24 mois", HttpStatus.BAD_REQUEST);
        }
        LocalDate courant = finance.aujourdhui().withDayOfMonth(1);
        return finance.syntheseMensuelle(courant.minusMonths(mois - 1L), courant).stream()
                .map(m -> new Mois(m.mois(), m.attendu(), m.encaisse(), taux(m.encaisse(), m.attendu()))).toList();
    }

    /** Arriérés par immeuble et par locataire (les {@code limite} plus gros de chaque liste), total général sur tous les baux. */
    @Transactional(readOnly = true)
    public Arrieres arrieres(int limite) {
        if (limite < 1 || limite > 100) {
            throw new ErreurMetier("DASHBOARD_LIMITE_INVALIDE", "La limite est de 1 à 100", HttpStatus.BAD_REQUEST);
        }
        List<ArriereBail> liste = finance.arrieresParBail();
        // lecture groupée : un bail par ligne d'arriéré, un lot par bail, c'était 2 requêtes par bail en retard (≈ 1,4 s pour 2 000 baux en retard)
        Map<UUID, BailResume> cacheBaux = new HashMap<>();
        baux.baux(liste.stream().map(ArriereBail::bailId).toList()).forEach(b -> cacheBaux.put(b.id(), b));
        Map<UUID, LotResume> cacheLots = lots.lots(cacheBaux.values().stream().map(BailResume::lotId).distinct().toList());
        Map<UUID, long[]> parImmeuble = new LinkedHashMap<>();
        Map<UUID, String> nomsImmeubles = new HashMap<>();
        Map<UUID, long[]> parLocataire = new LinkedHashMap<>();
        for (ArriereBail a : liste) {
            BailResume bail = cacheBaux.get(a.bailId());
            if (bail == null) {
                continue;
            }
            LotResume lot = cacheLots.get(bail.lotId());
            if (lot != null) {
                nomsImmeubles.putIfAbsent(lot.immeubleId(), lot.immeubleNom());
                cumuler(parImmeuble, lot.immeubleId(), a);
            }
            cumuler(parLocataire, bail.locataireId(), a);
        }
        List<LigneArriere> biens = lignes(parImmeuble, nomsImmeubles::get, limite);
        Map<UUID, String> noms = new HashMap<>();
        List<LigneArriere> locataires = lignes(parLocataire, id -> noms.computeIfAbsent(id, i -> tiers.locataire(i).map(l -> l.nom()).orElse(null)), limite);
        return new Arrieres(liste.stream().mapToLong(ArriereBail::total).sum(), biens, locataires);
    }

    /** Baux dont le dernier jour d'effet tombe dans les {@code jours} prochains jours (fin prévue ou sortie annoncée). */
    @Transactional(readOnly = true)
    public List<BailAEcheance> bauxAEcheance(int jours) {
        if (jours < 1 || jours > 365) {
            throw new ErreurMetier("DASHBOARD_PERIODE_INVALIDE", "La période est de 1 à 365 jours", HttpStatus.BAD_REQUEST);
        }
        LocalDate aujourdhui = finance.aujourdhui();
        Map<UUID, String> noms = new HashMap<>();
        return baux.bauxArrivantAEcheance(aujourdhui, aujourdhui.plusDays(jours)).stream().map(b -> {
            LotResume lot = lots.lot(b.lotId()).orElse(null);
            return new BailAEcheance(b.id(), b.numero(), noms.computeIfAbsent(b.locataireId(), i -> tiers.locataire(i).map(l -> l.nom()).orElse(null)),
                    lot == null ? null : lot.immeubleNom() + " — " + lot.reference(), b.dateFinEffective(),
                    ChronoUnit.DAYS.between(aujourdhui, b.dateFinEffective()), b.statut());
        }).toList();
    }

    // ------------------------------------------------------------------

    private static void cumuler(Map<UUID, long[]> m, UUID cle, ArriereBail a) {
        long[] v = m.computeIfAbsent(cle, k -> new long[2]);
        v[0] += a.total();
        v[1] += a.nombre();
    }

    private static List<LigneArriere> lignes(Map<UUID, long[]> m, java.util.function.Function<UUID, String> nom, int limite) {
        return m.entrySet().stream().sorted(Comparator.comparingLong((Map.Entry<UUID, long[]> e) -> e.getValue()[0]).reversed())
                .limit(limite).map(e -> new LigneArriere(e.getKey(), nom.apply(e.getKey()), e.getValue()[0], (int) e.getValue()[1])).toList();
    }

    /** Pourcentage en points de base, arrondi à l'entier ; nul si le dénominateur est nul. */
    static Integer taux(long numerateur, long denominateur) {
        return denominateur <= 0 ? null : (int) ((numerateur * 10_000 * 2 + denominateur) / (denominateur * 2));
    }
}
