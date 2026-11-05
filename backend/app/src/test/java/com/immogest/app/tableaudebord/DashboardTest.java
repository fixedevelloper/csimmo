package com.immogest.app.tableaudebord;

import static com.immogest.app.securite.SecuriteTestSupport.json;
import static com.immogest.app.securite.SecuriteTestSupport.jwtRole;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.immogest.app.IntegrationTest;
import com.immogest.app.securite.Role;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

/** Tableaux de bord : occupation, attendu vs encaissé, arriérés par bien et par locataire, baux à échéance, droits. */
@IntegrationTest
@TestPropertySource(properties = "immogest.finance.horizon-jours=45")
class DashboardTest {

    private static final RequestPostProcessor ADMIN = jwtRole(Role.ADMIN, UUID.fromString("00000000-0000-0000-0000-0000000000a1"));
    private static final RequestPostProcessor GESTIONNAIRE = jwtRole(Role.GESTIONNAIRE);
    private static final AtomicLong TEL = new AtomicLong(630_000_000L);

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    JdbcTemplate jdbc;

    LocalDate aujourdhui;
    UUID imm1;
    UUID imm2;

    @BeforeEach
    void preparer() throws Exception {
        aujourdhui = LocalDate.now(ZoneId.of("Africa/Douala"));
        try {
            await().atMost(Duration.ofSeconds(20)).until(() -> jdbc.queryForObject("SELECT count(*) FROM event_publication WHERE completion_date IS NULL AND publication_date > now() - interval '30 seconds'", Long.class) == 0);
        } catch (org.awaitility.core.ConditionTimeoutException ignore) {
            // publications en échec : sans effet ici
        }
        jdbc.execute("TRUNCATE rel_relance, rel_mise_en_demeure, notif_message, fin_document, fin_compteur_numero, fin_imputation, fin_paiement, fin_echeance, bail_document, bail_garant, bail, patrimoine_photo, compteur, lot_equipement, lot, immeuble, garant, tiers_piece, locataire CASCADE");
        imm1 = id(mvc.perform(post("/api/v1/immeubles").with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("nom", "Immeuble Un", "adresse", "1 rue", "ville", "Douala"))));
        imm2 = id(mvc.perform(post("/api/v1/immeubles").with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("nom", "Immeuble Deux", "adresse", "2 rue", "ville", "Douala"))));
    }

    private UUID id(ResultActions r) throws Exception {
        return UUID.fromString(mapper.readTree(r.andReturn().getResponse().getContentAsString()).path("id").asText());
    }

    private UUID locataire(String nom) throws Exception {
        return id(mvc.perform(post("/api/v1/locataires").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("nom", nom, "telephone", "+237" + TEL.incrementAndGet()))).andExpect(status().isCreated()));
    }

    private UUID lot(UUID immeuble, String ref) throws Exception {
        return id(mvc.perform(post("/api/v1/lots").with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("immeubleId", immeuble.toString(), "reference", ref, "type", "STUDIO"))));
    }

    private UUID bail(UUID lot, UUID loc, LocalDate debut, LocalDate fin, long loyer) throws Exception {
        UUID b = id(mvc.perform(post("/api/v1/baux").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("lotId", lot.toString(), "locataireId", loc.toString(), "dateDebut", debut.toString(), "dateFin", fin == null ? null : fin.toString(),
                        "loyer", loyer, "charges", 0, "periodicite", "MENSUELLE", "jourEcheance", 1, "moisAvance", 0))).andExpect(status().isCreated()));
        mvc.perform(post("/api/v1/baux/{id}/activer", b).with(ADMIN)).andExpect(status().isOk());
        await().atMost(Duration.ofSeconds(15)).until(() -> jdbc.queryForObject("SELECT count(*) FROM fin_echeance WHERE bail_id = ?", Long.class, b) >= 1);
        return b;
    }

    private JsonNode lire(String chemin, RequestPostProcessor qui) throws Exception {
        return mapper.readTree(mvc.perform(get(chemin).with(qui)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
    }

    private long somme(String sql, Object... args) {
        return jdbc.queryForObject(sql, Long.class, args);
    }

    // ================================================================== données

    private void jeuDeDonnees() throws Exception {
        UUID paul = locataire("Paul Gros Débiteur");
        UUID marie = locataire("Marie Petit Débit");
        UUID luc = locataire("Luc À Jour");
        UUID a1 = lot(imm1, "A1");
        UUID a2 = lot(imm1, "A2");
        UUID b1 = lot(imm2, "B1");
        UUID t1 = lot(imm2, "T1");
        lot(imm2, "L1"); // libre
        mvc.perform(put("/api/v1/lots/{id}/statut", t1).with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("statut", "EN_TRAVAUX"))).andExpect(status().isOk());
        bail(a1, paul, aujourdhui.minusDays(70), null, 300_000);
        bail(a2, marie, aujourdhui.minusDays(40), null, 100_000);
        bail(b1, luc, aujourdhui.minusDays(5), null, 200_000);
        await().atMost(Duration.ofSeconds(15)).untilAsserted(() ->
                assertThat(lire("/api/v1/lots?statut=OCCUPE&size=50", ADMIN).path("totalElements").asInt()).isEqualTo(3));
    }

    @Test
    void occupationHorsLotsEnTravaux() throws Exception {
        jeuDeDonnees();
        JsonNode o = lire("/api/v1/tableau-de-bord/synthese", GESTIONNAIRE).path("occupation");
        assertThat(o.path("occupes").asInt()).isEqualTo(3);
        assertThat(o.path("libres").asInt()).isEqualTo(1);
        assertThat(o.path("enTravaux").asInt()).isEqualTo(1);
        assertThat(o.path("tauxBp").asInt()).isEqualTo(7500); // 3 / (3 + 1), les lots en travaux ne sont pas louables
    }

    @Test
    void syntheseCoherenteAvecLesEcheances() throws Exception {
        jeuDeDonnees();
        // Luc paie son échéance (200 000), Paul règle 100 000 sur la sienne
        UUID bailLuc = jdbc.queryForObject("SELECT b.id FROM bail b JOIN locataire l ON l.id = b.locataire_id WHERE l.nom = 'Luc À Jour'", UUID.class);
        mvc.perform(post("/api/v1/paiements").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("bailId", bailLuc.toString(), "montant", 200_000, "mode", "ESPECES"))).andExpect(status().isCreated());

        JsonNode s = lire("/api/v1/tableau-de-bord/synthese", ADMIN);
        LocalDate debutMois = aujourdhui.withDayOfMonth(1);
        long attendu = somme("SELECT COALESCE(SUM(montant),0) FROM fin_echeance WHERE statut <> 'ANNULEE' AND date_echeance BETWEEN ? AND ?", debutMois, debutMois.plusMonths(1).minusDays(1));
        long paye = somme("SELECT COALESCE(SUM(montant_paye),0) FROM fin_echeance WHERE statut <> 'ANNULEE' AND date_echeance BETWEEN ? AND ?", debutMois, debutMois.plusMonths(1).minusDays(1));
        assertThat(s.path("attenduMois").asLong()).isEqualTo(attendu).isPositive();
        assertThat(s.path("encaisseMois").asLong()).isEqualTo(paye);
        if (attendu > 0) {
            assertThat(s.path("tauxRecouvrementMoisBp").asInt()).isEqualTo(DashboardService.taux(paye, attendu));
        }
        long arrieres = somme("SELECT COALESCE(SUM(montant - montant_paye),0) FROM fin_echeance WHERE statut <> 'ANNULEE' AND montant_paye < montant AND date_echeance < ?", aujourdhui);
        assertThat(s.path("arrieresTotal").asLong()).isEqualTo(arrieres).isPositive();
        assertThat(s.path("echeancesEnRetard").asInt()).isEqualTo((int) somme("SELECT COUNT(*) FROM fin_echeance WHERE statut <> 'ANNULEE' AND montant_paye < montant AND date_echeance < ?", aujourdhui));
    }

    @Test
    void serieMensuelleAttenduEtEncaisse() throws Exception {
        jeuDeDonnees();
        JsonNode serie = lire("/api/v1/tableau-de-bord/attendu-encaisse?mois=12", ADMIN);
        assertThat(serie).hasSize(12);
        assertThat(serie.get(11).path("mois").asText()).isEqualTo(aujourdhui.withDayOfMonth(1).toString());
        assertThat(serie.get(0).path("mois").asText()).isEqualTo(aujourdhui.withDayOfMonth(1).minusMonths(11).toString());
        // mois sans échéance : zéros et pas de taux ; le total attendu retombe sur la base
        assertThat(serie.get(0).path("attendu").asLong()).isZero();
        assertThat(serie.get(0).path("tauxBp").isNull()).isTrue();
        long total = 0;
        for (JsonNode m : serie) {
            total += m.path("attendu").asLong();
        }
        LocalDate debut = aujourdhui.withDayOfMonth(1).minusMonths(11);
        assertThat(total).isEqualTo(somme("SELECT COALESCE(SUM(montant),0) FROM fin_echeance WHERE statut <> 'ANNULEE' AND date_echeance BETWEEN ? AND ?",
                debut, aujourdhui.withDayOfMonth(1).plusMonths(1).minusDays(1)));
        assertThat(lire("/api/v1/tableau-de-bord/attendu-encaisse?mois=3", ADMIN)).hasSize(3);
        mvc.perform(get("/api/v1/tableau-de-bord/attendu-encaisse?mois=0").with(ADMIN)).andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("DASHBOARD_PERIODE_INVALIDE"));
        mvc.perform(get("/api/v1/tableau-de-bord/attendu-encaisse?mois=25").with(ADMIN)).andExpect(status().isBadRequest());
    }

    @Test
    void arrieresParBienEtParLocataire() throws Exception {
        jeuDeDonnees();
        JsonNode a = lire("/api/v1/tableau-de-bord/arrieres", ADMIN);
        long attenduTotal = somme("SELECT COALESCE(SUM(montant - montant_paye),0) FROM fin_echeance WHERE statut <> 'ANNULEE' AND montant_paye < montant AND date_echeance < ?", aujourdhui);
        assertThat(a.path("total").asLong()).isEqualTo(attenduTotal);

        JsonNode locs = a.path("parLocataire");
        assertThat(locs.get(0).path("libelle").asText()).isEqualTo("Paul Gros Débiteur"); // trié du plus gros au plus petit
        assertThat(locs.findValuesAsText("libelle")).contains("Marie Petit Débit");
        long paul = somme("SELECT SUM(e.montant - e.montant_paye) FROM fin_echeance e JOIN bail b ON b.id = e.bail_id JOIN locataire l ON l.id = b.locataire_id WHERE l.nom = 'Paul Gros Débiteur' AND e.montant_paye < e.montant AND e.date_echeance < ?", aujourdhui);
        assertThat(locs.get(0).path("total").asLong()).isEqualTo(paul);
        long somLocs = 0;
        for (JsonNode l : locs) {
            somLocs += l.path("total").asLong();
        }
        assertThat(somLocs).isEqualTo(attenduTotal);

        JsonNode biens = a.path("parBien");
        assertThat(biens.get(0).path("libelle").asText()).isEqualTo("Immeuble Un"); // A1 + A2
        long somBiens = 0;
        for (JsonNode b : biens) {
            somBiens += b.path("total").asLong();
        }
        assertThat(somBiens).isEqualTo(attenduTotal);

        assertThat(lire("/api/v1/tableau-de-bord/arrieres?limite=1", ADMIN).path("parLocataire")).hasSize(1);
        mvc.perform(get("/api/v1/tableau-de-bord/arrieres?limite=0").with(ADMIN)).andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("DASHBOARD_LIMITE_INVALIDE"));
    }

    @Test
    void bauxArrivantAEcheanceSousSoixanteJours() throws Exception {
        UUID l1 = locataire("Fin Proche");
        UUID l2 = locataire("Fin Lointaine");
        UUID l3 = locataire("Sortie Annoncée");
        UUID proche = bail(lot(imm1, "E1"), l1, aujourdhui.minusMonths(6), aujourdhui.plusDays(30), 100_000);
        bail(lot(imm1, "E2"), l2, aujourdhui.minusMonths(6), aujourdhui.plusDays(90), 100_000);
        UUID sortie = bail(lot(imm1, "E3"), l3, aujourdhui.minusMonths(6), null, 100_000);
        mvc.perform(post("/api/v1/baux/{id}/resilier", sortie).with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("dateNotification", aujourdhui.toString(), "dateSortie", aujourdhui.plusDays(10).toString(), "preavisReduitAccepte", true, "motif", "Départ"))).andExpect(status().isOk());

        JsonNode liste = lire("/api/v1/tableau-de-bord/baux-a-echeance", GESTIONNAIRE);
        assertThat(liste).hasSize(2);
        assertThat(liste.get(0).path("locataireNom").asText()).isEqualTo("Sortie Annoncée"); // la plus proche d'abord
        assertThat(liste.get(0).path("joursRestants").asInt()).isEqualTo(10);
        assertThat(liste.get(0).path("statut").asText()).isEqualTo("RESILIE");
        assertThat(liste.get(1).path("bailId").asText()).isEqualTo(proche.toString());
        assertThat(liste.get(1).path("joursRestants").asInt()).isEqualTo(30);
        assertThat(liste.get(1).path("logement").asText()).isEqualTo("Immeuble Un — E1");
        assertThat(lire("/api/v1/tableau-de-bord/baux-a-echeance?jours=120", ADMIN)).hasSize(3);
        assertThat(lire("/api/v1/tableau-de-bord/synthese", ADMIN).path("bauxAEcheance").asInt()).isEqualTo(2);
        mvc.perform(get("/api/v1/tableau-de-bord/baux-a-echeance?jours=0").with(ADMIN)).andExpect(status().isBadRequest());
    }

    @Test
    void baseVideSansDivisionParZero() throws Exception {
        JsonNode s = lire("/api/v1/tableau-de-bord/synthese", ADMIN);
        assertThat(s.path("occupation").path("tauxBp").isNull()).isTrue();
        assertThat(s.path("tauxRecouvrementMoisBp").isNull()).isTrue();
        assertThat(s.path("arrieresTotal").asLong()).isZero();
        assertThat(lire("/api/v1/tableau-de-bord/arrieres", ADMIN).path("parBien")).isEmpty();
        assertThat(lire("/api/v1/tableau-de-bord/baux-a-echeance", ADMIN)).isEmpty();
    }

    @Test
    void tauxEnPointsDeBase() {
        assertThat(DashboardService.taux(1, 3)).isEqualTo(3333);
        assertThat(DashboardService.taux(2, 3)).isEqualTo(6667);
        assertThat(DashboardService.taux(5, 5)).isEqualTo(10000);
        assertThat(DashboardService.taux(0, 100)).isZero();
        assertThat(DashboardService.taux(5, 0)).isNull();
    }

    @Test
    void droits() throws Exception {
        for (String chemin : new String[] {"synthese", "attendu-encaisse", "arrieres", "baux-a-echeance"}) {
            for (Role r : new Role[] {Role.LOCATAIRE, Role.PROPRIETAIRE_TIERS, Role.PRESTATAIRE}) {
                mvc.perform(get("/api/v1/tableau-de-bord/" + chemin).with(jwtRole(r))).andExpect(status().isForbidden()).andExpect(jsonPath("$.code").value("ACCES_REFUSE"));
            }
            mvc.perform(get("/api/v1/tableau-de-bord/" + chemin)).andExpect(status().isUnauthorized());
            mvc.perform(get("/api/v1/tableau-de-bord/" + chemin).with(GESTIONNAIRE)).andExpect(status().isOk());
        }
    }
}
