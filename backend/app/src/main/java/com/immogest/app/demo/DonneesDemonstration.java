package com.immogest.app.demo;

import com.immogest.app.bail.BailSaisie;
import com.immogest.app.bail.BailService;
import com.immogest.app.bail.Periodicite;
import com.immogest.app.finance.DepenseService;
import com.immogest.app.finance.EcheanceService;
import com.immogest.app.finance.ModePaiement;
import com.immogest.app.finance.PaiementSaisie;
import com.immogest.app.finance.PaiementService;
import com.immogest.app.finance.ReversementDtos.DepenseSaisie;
import com.immogest.app.finance.ReversementService;
import com.immogest.app.patrimoine.FiltreImmeubles;
import com.immogest.app.patrimoine.ImmeubleSaisie;
import com.immogest.app.patrimoine.LotSaisie;
import com.immogest.app.patrimoine.PatrimoineService;
import com.immogest.app.patrimoine.TypeLot;
import com.immogest.app.securite.AffectationService;
import com.immogest.app.securite.Role;
import com.immogest.app.securite.UtilisateurService;
import com.immogest.app.tiers.LocataireSaisie;
import com.immogest.app.tiers.LocataireService;
import com.immogest.app.tiers.MandatSaisie;
import com.immogest.app.tiers.ModeReversement;
import com.immogest.app.tiers.ProprietaireSaisie;
import com.immogest.app.tiers.ProprietaireService;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

/**
 * Charge un jeu de démonstration cohérent, une seule fois (rien n'est fait si les immeubles « (démo) » existent déjà).
 * Activation : {@code SPRING_PROFILES_ACTIVE=dev,demo}. Refusé en production ({@code prod}). Tout passe par les services métier, donc par
 * leurs règles (imputation oldest-first, échéances, mandats, périmètres) ; les encaissements du premier immeuble sont saisis AU NOM de
 * l'encaisseur pour que la caisse et la fiche employé soient parlantes.
 */
@Component
@Profile("demo & !prod")
class DonneesDemonstration {

    private static final Logger LOG = LoggerFactory.getLogger(DonneesDemonstration.class);
    private static final String MARQUE = "(démo)";
    static final String MOT_DE_PASSE = "Demo1234!";
    private static final ZoneId DOUALA = ZoneId.of("Africa/Douala");

    private enum Payeur { A_JOUR, PARTIEL, EN_RETARD }

    /** Un bail à encaisser : identifiant du bail, référence du lot, montants mensuels et profil de paiement. */
    private record BailDemo(UUID bailId, String reference, long loyer, long charges, Payeur payeur) {
    }

    private final PatrimoineService patrimoine;
    private final ProprietaireService proprietaires;
    private final LocataireService locataires;
    private final BailService baux;
    private final EcheanceService echeances;
    private final PaiementService paiements;
    private final DepenseService depenses;
    private final ReversementService reversements;
    private final UtilisateurService utilisateurs;
    private final AffectationService affectations;

    DonneesDemonstration(PatrimoineService patrimoine, ProprietaireService proprietaires, LocataireService locataires, BailService baux,
            EcheanceService echeances, PaiementService paiements, DepenseService depenses, ReversementService reversements,
            UtilisateurService utilisateurs, AffectationService affectations) {
        this.patrimoine = patrimoine;
        this.proprietaires = proprietaires;
        this.locataires = locataires;
        this.baux = baux;
        this.echeances = echeances;
        this.paiements = paiements;
        this.depenses = depenses;
        this.reversements = reversements;
        this.utilisateurs = utilisateurs;
        this.affectations = affectations;
    }

    @EventListener(ApplicationReadyEvent.class)
    void charger() {
        if (patrimoine.listerImmeubles(new FiltreImmeubles(null, null, MARQUE, true, null), PageRequest.of(0, 1)).getTotalElements() > 0) {
            LOG.info("Données de démonstration déjà présentes : rien à charger");
            return;
        }
        try {
            LocalDate aujourdhui = LocalDate.now(DOUALA);
            LOG.warn("Chargement des données de démonstration (profil demo) — ne jamais utiliser en production");

            UUID ngono = proprietaire("Mme Béatrice Ngono", "+237690100001", 1000);
            UUID fotso = proprietaire("M. Armand Fotso", "+237690100002", 800);

            UUID palmiers = immeuble("Résidence Les Palmiers", "Bonanjo", "Douala", ngono);
            UUID bonamoussadi = immeuble("Immeuble Bonamoussadi", "Bonamoussadi", "Douala", ngono);
            UUID bastos = immeuble("Villa Santa Barbara", "Bastos", "Yaoundé", fotso);
            UUID akwa = immeuble("Centre Commercial Akwa", "Akwa", "Douala", null);

            List<BailDemo> lotsPalmiers = new ArrayList<>();
            List<BailDemo> autres = new ArrayList<>();
            Payeur[] profils = {Payeur.A_JOUR, Payeur.A_JOUR, Payeur.PARTIEL, Payeur.EN_RETARD, Payeur.A_JOUR, Payeur.PARTIEL, Payeur.A_JOUR};
            String[] noms = {"Marie Tenant", "Joseph Mbarga", "Esther Kamga", "Paul Tchoumi", "Rose Essomba", "David Njoya", "Sylvie Ateba",
                    "Hervé Manga", "Nadine Fouda", "Claude Biyong", "Aïcha Bello", "Pauline Abena"};
            int n = 0;
            for (int i = 1; i <= 5; i++) {
                lotsPalmiers.add(bail(palmiers, "A" + i, i <= 3 ? TypeLot.APPARTEMENT : TypeLot.STUDIO, i <= 3 ? 150_000 : 80_000, i <= 3 ? 15_000 : 5_000,
                        noms[n], profils[n % profils.length], 5 + (n % 3), n == 0));
                n++;
            }
            for (int i = 1; i <= 3; i++) {
                autres.add(bail(bonamoussadi, "B" + i, TypeLot.APPARTEMENT, 120_000, 10_000, noms[n], profils[n % profils.length], 5 + (n % 3), false));
                n++;
            }
            for (int i = 1; i <= 2; i++) {
                autres.add(bail(bastos, "V" + i, TypeLot.APPARTEMENT, 250_000, 25_000, noms[n], profils[n % profils.length], 5 + (n % 3), false));
                n++;
            }
            for (int i = 1; i <= 2; i++) {
                autres.add(bail(akwa, "BTQ" + i, TypeLot.BOUTIQUE, 200_000, 20_000, noms[n], profils[n % profils.length], 5 + (n % 3), false));
                n++;
            }
            // lots libres et un bail en brouillon
            lot(palmiers, "A6", TypeLot.STUDIO, 80_000L);
            lot(bonamoussadi, "B4", TypeLot.APPARTEMENT, 120_000L);
            lot(bastos, "V3", TypeLot.APPARTEMENT, 250_000L);
            lot(akwa, "BTQ3", TypeLot.BOUTIQUE, 200_000L);
            UUID lotBrouillon = lot(akwa, "BUR1", TypeLot.BUREAU, 300_000L);
            UUID futur = locataires.creer(new LocataireSaisie("Isabelle Ondoa", "Avocate", "+237690200099", null, "Akwa, Douala", null, null, null, null)).id();
            baux.creer(new BailSaisie(lotBrouillon, futur, List.of(), aujourdhui.plusDays(20), null, 300_000L, 30_000L, Periodicite.MENSUELLE, 5, 600_000L, 2, 3));

            employes(palmiers, bonamoussadi);

            // encaissements : ceux du premier immeuble sont saisis par l'encaisseur, les autres par le système
            UUID encaisseur = utilisateurs.lister(PageRequest.of(0, 100)).getContent().stream()
                    .filter(u -> "Awa Encaisseuse".equals(u.nom())).map(u -> u.id()).findFirst().orElse(null);
            for (BailDemo l : lotsPalmiers) {
                comme(encaisseur, Role.ENCAISSEUR, () -> encaisser(l, aujourdhui));
            }
            for (BailDemo l : autres) {
                encaisser(l, aujourdhui);
            }

            LocalDate moisPrecedent = aujourdhui.withDayOfMonth(1).minusMonths(1);
            depenses.enregistrer(new DepenseSaisie(palmiers, null, moisPrecedent.withDayOfMonth(12), "Réparation plomberie", 45_000L, "FAC-2026-014"));
            depenses.enregistrer(new DepenseSaisie(bastos, null, moisPrecedent.withDayOfMonth(20), "Peinture des parties communes", 120_000L, "FAC-2026-021"));
            depenses.enregistrer(new DepenseSaisie(bonamoussadi, null, aujourdhui.withDayOfMonth(3), "Remplacement du compteur d'eau", 30_000L, "FAC-2026-030"));
            reversements.calculer(aujourdhui);

            LOG.warn("Démonstration prête. Connexions (mot de passe {} pour tous, codes SMS affichés dans la console) :", MOT_DE_PASSE);
            LOG.warn("  admin@immogest.local / Admin1234! (ADMIN) · +237690000001 gestionnaire · +237690000002 encaisseur (Palmiers)");
            LOG.warn("  +237690000003 gestionnaire de biens (Palmiers, Bonamoussadi) · +237690000004 comptable (Palmiers, Bonamoussadi) · +237690000010 locataire (mobile)");
        } catch (RuntimeException e) {
            LOG.error("Chargement des données de démonstration interrompu : videz la base avant de relancer le profil demo", e);
        }
    }

    // ------------------------------------------------------------------ construction

    private UUID proprietaire(String nom, String telephone, int tauxBp) {
        UUID id = proprietaires.creer(new ProprietaireSaisie(nom, telephone, null, "Douala", ModeReversement.VIREMENT, null, null, null)).id();
        proprietaires.creerMandat(id, new MandatSaisie(tauxBp, LocalDate.of(2025, 1, 1), null, null));
        return id;
    }

    private UUID immeuble(String nom, String quartier, String ville, UUID proprietaire) {
        return patrimoine.creerImmeuble(new ImmeubleSaisie(nom + " " + MARQUE, quartier + ", " + ville, quartier, ville, null, null, null, proprietaire)).id();
    }

    private UUID lot(UUID immeuble, String reference, TypeLot type, Long loyer) {
        return patrimoine.creerLot(new LotSaisie(immeuble, reference, type, null, null, loyer, List.of(), List.of())).id();
    }

    /** Lot + locataire + bail activé, départ entre 5 et 8 mois en arrière. */
    private BailDemo bail(UUID immeuble, String reference, TypeLot type, long loyer, long charges, String locataire, Payeur payeur, int jour, boolean avecCompte) {
        LocalDate aujourdhui = LocalDate.now(DOUALA);
        UUID lot = lot(immeuble, reference, type, loyer);
        UUID compte = null;
        if (avecCompte) {
            compte = utilisateurs.creer(new UtilisateurService.Saisie(locataire, "+237690000010", null, Role.LOCATAIRE, true, false), MOT_DE_PASSE).id();
        }
        String telephone = "+23769" + String.format("%07d", 3_000_000 + Math.abs(locataire.hashCode()) % 6_000_000);
        UUID loc = locataires.creer(new LocataireSaisie(locataire, null, telephone, null, "Douala", null, null, compte, null)).id();
        LocalDate debut = aujourdhui.withDayOfMonth(1).minusMonths(5 + Math.abs(locataire.hashCode()) % 4);
        UUID bail = baux.creer(new BailSaisie(lot, loc, List.of(), debut, null, loyer, charges, Periodicite.MENSUELLE, jour, loyer * 2, 0, 3)).id();
        baux.activer(bail);
        echeances.genererPourBail(bail);
        return new BailDemo(bail, reference, loyer, charges, payeur);
    }

    private void employes(UUID... immeublesDeLaDirection) {
        creerEmploye("Boris Chef", "+237690000001", Role.GESTIONNAIRE);
        UUID encaisseur = creerEmploye("Awa Encaisseuse", "+237690000002", Role.ENCAISSEUR);
        UUID biens = creerEmploye("Carine Biens", "+237690000003", Role.GESTIONNAIRE_BIENS);
        UUID comptable = creerEmploye("Carl Comptable", "+237690000004", Role.COMPTABLE);
        affectations.definir(encaisseur, List.of(immeublesDeLaDirection[0]));
        affectations.definir(biens, List.of(immeublesDeLaDirection));
        affectations.definir(comptable, List.of(immeublesDeLaDirection));
    }

    private UUID creerEmploye(String nom, String telephone, Role role) {
        return utilisateurs.creer(new UtilisateurService.Saisie(nom, telephone, nom.split(" ")[0].toLowerCase() + "@demo.local", role, true, true), MOT_DE_PASSE).id();
    }

    // ------------------------------------------------------------------ encaissements

    /** Un paiement par mois écoulé, selon le profil du payeur (à jour, partiel, en retard). */
    private void encaisser(BailDemo l, LocalDate aujourdhui) {
        UUID bail = l.bailId();
        var echeancesDuBail = echeances.lister(bail, null, PageRequest.of(0, 100, org.springframework.data.domain.Sort.by("periodeDebut"))).getContent();
        long du = l.loyer() + l.charges();
        int rang = 0;
        ModePaiement[] modes = {ModePaiement.ESPECES, ModePaiement.MTN_MOMO, ModePaiement.ORANGE_MONEY, ModePaiement.VIREMENT};
        for (var e : echeancesDuBail) {
            if (e.dateEcheance().isAfter(aujourdhui)) {
                break;
            }
            rang++;
            long montant = switch (l.payeur()) {
                case A_JOUR -> du;
                case PARTIEL -> du * 6 / 10;
                case EN_RETARD -> rang <= 2 ? du : 0;
            };
            if (montant <= 0) {
                continue;
            }
            ModePaiement mode = modes[(rang + l.reference().length()) % modes.length];
            String reference = mode == ModePaiement.ESPECES ? null : "DEMO-" + l.reference() + "-" + rang;
            paiements.enregistrer(new PaiementSaisie(bail, montant, mode, reference, e.dateEcheance()), "demo-" + bail + "-" + rang);
        }
    }

    /** Exécute l'action au nom d'un compte (jeton simulé dans le contexte de sécurité), pour que `saisi_par` et le périmètre soient réels. */
    private void comme(UUID compte, Role role, Runnable action) {
        if (compte == null) {
            action.run();
            return;
        }
        Jwt jwt = Jwt.withTokenValue("demo").header("alg", "none").subject(compte.toString()).build();
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt, List.of(new SimpleGrantedAuthority("ROLE_" + role.name())), compte.toString()));
        try {
            action.run();
        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}
