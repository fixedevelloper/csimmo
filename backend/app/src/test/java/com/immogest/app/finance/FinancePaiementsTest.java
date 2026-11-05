package com.immogest.app.finance;

import static com.immogest.app.securite.SecuriteTestSupport.json;
import static com.immogest.app.securite.SecuriteTestSupport.jwtRole;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.immogest.app.IntegrationTest;
import com.immogest.app.securite.Role;
import com.immogest.app.securite.UtilisateurService;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

/** Encaissements manuels : imputation oldest-first, partiels, crédit, contre-passation, idempotence, caisse, droits. */
@IntegrationTest
@TestPropertySource(properties = {"immogest.finance.horizon-jours=45", "immogest.finance.delai-grace-jours=5"})
class FinancePaiementsTest {

    private static final RequestPostProcessor ADMIN = jwtRole(Role.ADMIN, UUID.fromString("00000000-0000-0000-0000-0000000000a1"));
    private static final AtomicLong TEL = new AtomicLong(670_000_000L);
    private static final long MENSUEL = 160_000;

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    JdbcTemplate jdbc;
    @Autowired
    UtilisateurService utilisateurs;

    UUID immeuble;
    UUID locataire;
    UUID bail;
    LocalDate aujourdhui;

    @BeforeEach
    void preparer() throws Exception {
        aujourdhui = LocalDate.now(ZoneId.of("Africa/Douala"));
        attendreOutboxVide();
        jdbc.execute("TRUNCATE fin_document, fin_compteur_numero, fin_imputation, fin_paiement, fin_echeance, bail_document, bail_garant, bail, patrimoine_photo, compteur, lot_equipement, lot, immeuble, garant, tiers_piece, locataire CASCADE");
        immeuble = id(mvc.perform(post("/api/v1/immeubles").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("nom", "Résidence Paiements", "adresse", "1 rue", "ville", "Douala"))));
        locataire = nouveauLocataire(null);
        bail = bailActif(locataire, "P1", aujourdhui.withDayOfMonth(1).minusMonths(3));
        await().atMost(Duration.ofSeconds(15)).until(() -> echeances(bail).size() >= 4);
    }

    /** Laisse finir les listeners asynchrones du test précédent (quittances…) avant de vider les tables, sinon TRUNCATE attend leurs verrous. */
    private void attendreOutboxVide() {
        try {
            await().atMost(Duration.ofSeconds(20)).until(() -> jdbc.queryForObject("SELECT count(*) FROM event_publication WHERE completion_date IS NULL AND publication_date > now() - interval '30 seconds'", Long.class) == 0);
        } catch (org.awaitility.core.ConditionTimeoutException ignore) {
            // des publications restées en échec ne doivent pas bloquer les tests suivants
        }
    }

    private UUID id(ResultActions r) throws Exception {
        return UUID.fromString(mapper.readTree(r.andReturn().getResponse().getContentAsString()).path("id").asText());
    }

    private UUID nouveauLocataire(UUID compte) throws Exception {
        return id(mvc.perform(post("/api/v1/locataires").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("nom", "Paul Paiements", "telephone", "+237" + TEL.incrementAndGet(), "utilisateurId", compte == null ? null : compte.toString())))
                .andExpect(status().isCreated()));
    }

    private UUID bailActif(UUID loc, String ref, LocalDate debut) throws Exception {
        UUID lot = id(mvc.perform(post("/api/v1/lots").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("immeubleId", immeuble.toString(), "reference", ref, "type", "STUDIO"))));
        UUID b = id(mvc.perform(post("/api/v1/baux").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("lotId", lot.toString(), "locataireId", loc.toString(), "dateDebut", debut.toString(), "loyer", 150_000, "charges", 10_000,
                        "periodicite", "MENSUELLE", "jourEcheance", 1, "moisAvance", 0))).andExpect(status().isCreated()));
        mvc.perform(post("/api/v1/baux/{id}/activer", b).with(ADMIN)).andExpect(status().isOk());
        return b;
    }

    private JsonNode echeances(UUID b) throws Exception {
        return mapper.readTree(mvc.perform(get("/api/v1/echeances").param("bailId", b.toString()).param("size", "100").with(ADMIN))
                .andReturn().getResponse().getContentAsString()).path("content");
    }

    private ResultActions payer(RequestPostProcessor qui, long montant, String mode, Object... extra) throws Exception {
        Object[] base = {"bailId", bail.toString(), "montant", montant, "mode", mode};
        Object[] tout = new Object[base.length + extra.length];
        System.arraycopy(base, 0, tout, 0, base.length);
        System.arraycopy(extra, 0, tout, base.length, extra.length);
        return mvc.perform(post("/api/v1/paiements").with(qui).contentType(MediaType.APPLICATION_JSON).content(json(tout)));
    }

    private UUID payerOk(long montant, String mode) throws Exception {
        return id(payer(ADMIN, montant, mode).andExpect(status().isCreated()));
    }

    // ================================================================== imputation

    @Test
    void imputationSurLEcheanceLaPlusAncienneEtPaiementPartiel() throws Exception {
        payer(ADMIN, MENSUEL + MENSUEL / 2, "ESPECES").andExpect(status().isCreated()).andExpect(jsonPath("$.type").value("ENCAISSEMENT"))
                .andExpect(jsonPath("$.locataireNom").value("Paul Paiements")).andExpect(jsonPath("$.contrePasse").value(false));
        JsonNode e = echeances(bail);
        assertThat(e.get(0).path("statut").asText()).isEqualTo("PAYEE");
        assertThat(e.get(0).path("montantPaye").asLong()).isEqualTo(MENSUEL);
        assertThat(e.get(1).path("montantPaye").asLong()).isEqualTo(MENSUEL / 2);
        assertThat(e.get(1).path("reste").asLong()).isEqualTo(MENSUEL / 2);
        assertThat(e.get(1).path("statut").asText()).isEqualTo("EN_RETARD"); // partielle mais au-delà de la grâce : reste en relance
        assertThat(e.get(2).path("montantPaye").asLong()).isZero();

        payer(ADMIN, MENSUEL / 2, "VIREMENT", "reference", "VIR-001").andExpect(status().isCreated());
        e = echeances(bail);
        assertThat(e.get(1).path("statut").asText()).isEqualTo("PAYEE");
        assertThat(jdbc.queryForObject("SELECT count(*) FROM fin_imputation", Long.class)).isEqualTo(3);
    }

    @Test
    void surplusRestantEnCreditEtCompteLocataire() throws Exception {
        long nb = echeances(bail).size();
        long tout = nb * MENSUEL;
        payer(ADMIN, tout + 50_000, "MTN_MOMO", "reference", "MP123").andExpect(status().isCreated());
        for (JsonNode e : echeances(bail)) {
            assertThat(e.path("statut").asText()).isEqualTo("PAYEE");
        }
        mvc.perform(get("/api/v1/locataires/{id}/compte", locataire).with(ADMIN)).andExpect(status().isOk())
                .andExpect(jsonPath("$.arrieres").value(0)).andExpect(jsonPath("$.credit").value(50_000))
                .andExpect(jsonPath("$.solde").value(-50_000)).andExpect(jsonPath("$.baux[0].bailId").value(bail.toString()));
    }

    @Test
    void compteAvecArrieres() throws Exception {
        mvc.perform(get("/api/v1/locataires/{id}/compte", locataire).with(ADMIN))
                .andExpect(jsonPath("$.arrieres").value(org.hamcrest.Matchers.greaterThanOrEqualTo(3 * (int) MENSUEL)))
                .andExpect(jsonPath("$.credit").value(0));
        payer(ADMIN, 100_000, "ESPECES").andExpect(status().isCreated());
        long avant = mapper.readTree(mvc.perform(get("/api/v1/locataires/{id}/compte", locataire).with(ADMIN)).andReturn().getResponse().getContentAsString()).path("solde").asLong();
        assertThat(avant).isPositive();
    }

    // ================================================================== contre-passation

    @Test
    void contrePassationRetablitLesStatutsSansRienSupprimer() throws Exception {
        UUID p = payerOk(2 * MENSUEL, "ESPECES");
        assertThat(echeances(bail).get(1).path("statut").asText()).isEqualTo("PAYEE");

        mvc.perform(post("/api/v1/paiements/{id}/contre-passation", p).with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("motif", " ")))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/v1/paiements/{id}/contre-passation", p).with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(json("motif", "Erreur de saisie")))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.type").value("CONTRE_PASSATION"))
                .andExpect(jsonPath("$.contrePassationDe").value(p.toString())).andExpect(jsonPath("$.motif").value("Erreur de saisie"));

        JsonNode e = echeances(bail);
        for (int i = 0; i < 2; i++) {
            assertThat(e.get(i).path("montantPaye").asLong()).isZero();
            assertThat(e.get(i).path("statut").asText()).isEqualTo("EN_RETARD");
        }
        // aucune ligne supprimée : paiement, contre-passation, 2 imputations + 2 imputations inverses
        assertThat(jdbc.queryForObject("SELECT count(*) FROM fin_paiement", Long.class)).isEqualTo(2);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM fin_imputation", Long.class)).isEqualTo(4);
        assertThat(jdbc.queryForObject("SELECT sum(montant) FROM fin_imputation", Long.class)).isZero();

        mvc.perform(get("/api/v1/paiements").param("bailId", bail.toString()).with(ADMIN)).andExpect(jsonPath("$.totalElements").value(2));
        mvc.perform(post("/api/v1/paiements/{id}/contre-passation", p).with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("motif", "Encore")))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("PAIEMENT_DEJA_CONTRE_PASSE"));
        UUID inverse = jdbc.queryForObject("SELECT id FROM fin_paiement WHERE type = 'CONTRE_PASSATION'", UUID.class);
        mvc.perform(post("/api/v1/paiements/{id}/contre-passation", inverse).with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("motif", "x")))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("PAIEMENT_NON_CONTRE_PASSABLE"));
        // un paiement contre-passé n'impute plus rien, même après un passage du moteur
        mvc.perform(post("/api/v1/echeances/generer").with(ADMIN)).andExpect(status().isOk());
        assertThat(echeances(bail).get(0).path("montantPaye").asLong()).isZero();
    }

    @Test
    void lAvanceNEstPasContrePassable() throws Exception {
        jdbc.update("INSERT INTO fin_paiement (id, bail_id, type, montant, date_paiement, created_at, updated_at, version) VALUES (?, ?, 'AVANCE', 1000, ?, now(), now(), 0)",
                UUID.randomUUID(), bail, aujourdhui);
        UUID avance = jdbc.queryForObject("SELECT id FROM fin_paiement WHERE type = 'AVANCE'", UUID.class);
        mvc.perform(post("/api/v1/paiements/{id}/contre-passation", avance).with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("motif", "x")))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("PAIEMENT_NON_CONTRE_PASSABLE"));
    }

    @Test
    void paiementsEtImputationsSontImmuablesEnBase() throws Exception {
        payerOk(MENSUEL, "ESPECES");
        assertThatThrownBy(() -> jdbc.update("UPDATE fin_paiement SET montant = 1")).isInstanceOf(DataAccessException.class);
        assertThatThrownBy(() -> jdbc.update("DELETE FROM fin_paiement")).isInstanceOf(DataAccessException.class);
        assertThatThrownBy(() -> jdbc.update("UPDATE fin_imputation SET montant = 1")).isInstanceOf(DataAccessException.class);
        assertThatThrownBy(() -> jdbc.update("DELETE FROM fin_imputation")).isInstanceOf(DataAccessException.class);
    }

    // ================================================================== idempotence et validation

    @Test
    void idempotencyKeyRejeuRenvoieLeMemePaiementSansDoublon() throws Exception {
        String corps = json("bailId", bail.toString(), "montant", 50_000, "mode", "ESPECES");
        String cle = "cle-" + UUID.randomUUID();
        UUID premier = id(mvc.perform(post("/api/v1/paiements").with(ADMIN).header("Idempotency-Key", cle).contentType(MediaType.APPLICATION_JSON).content(corps))
                .andExpect(status().isCreated()));
        UUID rejeu = id(mvc.perform(post("/api/v1/paiements").with(ADMIN).header("Idempotency-Key", cle).contentType(MediaType.APPLICATION_JSON).content(corps))
                .andExpect(status().isOk()));
        assertThat(rejeu).isEqualTo(premier);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM fin_paiement WHERE type = 'ENCAISSEMENT'", Long.class)).isEqualTo(1);
        assertThat(jdbc.queryForObject("SELECT sum(montant_paye) FROM fin_echeance", Long.class)).isEqualTo(50_000);

        mvc.perform(post("/api/v1/paiements").with(ADMIN).header("Idempotency-Key", cle).contentType(MediaType.APPLICATION_JSON)
                        .content(json("bailId", bail.toString(), "montant", 60_000, "mode", "ESPECES")))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("IDEMPOTENCE_CONFLIT"));
    }

    @Test
    void idempotenceSousConcurrence() throws Exception {
        String corps = json("bailId", bail.toString(), "montant", 40_000, "mode", "ESPECES");
        String cle = "conc-" + UUID.randomUUID();
        var pool = java.util.concurrent.Executors.newFixedThreadPool(4);
        try {
            var taches = new java.util.ArrayList<java.util.concurrent.Future<Integer>>();
            for (int i = 0; i < 4; i++) {
                taches.add(pool.submit(() -> mvc.perform(post("/api/v1/paiements").with(ADMIN).header("Idempotency-Key", cle)
                        .contentType(MediaType.APPLICATION_JSON).content(corps)).andReturn().getResponse().getStatus()));
            }
            int crees = 0;
            for (var t : taches) {
                int s = t.get();
                assertThat(s).isIn(200, 201);
                crees += s == 201 ? 1 : 0;
            }
            assertThat(crees).isEqualTo(1);
        } finally {
            pool.shutdown();
        }
        assertThat(jdbc.queryForObject("SELECT count(*) FROM fin_paiement WHERE type = 'ENCAISSEMENT'", Long.class)).isEqualTo(1);
    }

    @Test
    void laDateEnvoyeeParLeClientMobileEstLueCommeLeBonJour() throws Exception {
        // le client Dart généré sérialise une date en minuit UTC : « 2026-11-04T00:00:00.000Z » doit rester le 4 novembre
        LocalDate hier = aujourdhui.minusDays(1);
        UUID p = id(payer(ADMIN, 10_000, "ESPECES", "datePaiement", hier + "T00:00:00.000Z").andExpect(status().isCreated()));
        assertThat(jdbc.queryForObject("SELECT date_paiement FROM fin_paiement WHERE id = ?", java.sql.Date.class, p).toLocalDate()).isEqualTo(hier);
    }

    @Test
    void validations() throws Exception {
        payer(ADMIN, 0, "ESPECES").andExpect(status().isBadRequest());
        payer(ADMIN, 1000, "MTN_MOMO").andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("PAIEMENT_REFERENCE_REQUISE"));
        payer(ADMIN, 1000, "VIREMENT", "reference", "  ").andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("PAIEMENT_REFERENCE_REQUISE"));
        payer(ADMIN, 1000, "ESPECES", "datePaiement", aujourdhui.plusDays(1).toString()).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("PAIEMENT_DATE_FUTURE"));
        payer(ADMIN, 1000, "CHEQUE").andExpect(status().isBadRequest());
        mvc.perform(post("/api/v1/paiements").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("bailId", UUID.randomUUID().toString(), "montant", 1000, "mode", "ESPECES"))).andExpect(status().isNotFound());
        assertThat(jdbc.queryForObject("SELECT count(*) FROM fin_paiement", Long.class)).isZero();
    }

    @Test
    void auditEtEvenementPourChaqueEncaissement() throws Exception {
        UUID p = payerOk(MENSUEL, "ESPECES");
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() ->
                assertThat(jdbc.queryForObject("SELECT count(*) FROM audit_log WHERE action = 'FINANCE.PAIEMENT_ENREGISTRE' AND entite_id = ?", Long.class, p)).isEqualTo(1));
        mvc.perform(post("/api/v1/paiements/{id}/contre-passation", p).with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("motif", "Doublon")))
                .andExpect(status().isCreated());
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() ->
                assertThat(jdbc.queryForObject("SELECT count(*) FROM audit_log WHERE action = 'FINANCE.PAIEMENT_CONTRE_PASSE' AND entite_id = ?", Long.class, p)).isEqualTo(1));
    }

    // ================================================================== caisse

    @Test
    void rapprochementDeCaisseParAgentNetDesContrePassations() throws Exception {
        RequestPostProcessor agent = jwtRole(Role.GESTIONNAIRE, UUID.randomUUID());
        payer(ADMIN, 30_000, "ESPECES").andExpect(status().isCreated());
        UUID annule = id(payer(ADMIN, 20_000, "ESPECES").andExpect(status().isCreated()));
        payer(ADMIN, 99_000, "VIREMENT", "reference", "V1").andExpect(status().isCreated()); // hors caisse
        payer(agent, 7_000, "ESPECES").andExpect(status().isCreated());
        mvc.perform(post("/api/v1/paiements/{id}/contre-passation", annule).with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("motif", "Erreur")))
                .andExpect(status().isCreated());

        JsonNode admin = mapper.readTree(mvc.perform(get("/api/v1/paiements/caisse").with(ADMIN)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
        assertThat(admin).hasSize(2);
        long totalAdmin = 0;
        for (JsonNode l : admin) {
            if ("00000000-0000-0000-0000-0000000000a1".equals(l.path("agentId").asText())) {
                totalAdmin = l.path("total").asLong();
                assertThat(l.path("operations").asInt()).isEqualTo(3);
            } else {
                assertThat(l.path("total").asLong()).isEqualTo(7_000);
            }
        }
        assertThat(totalAdmin).isEqualTo(30_000);

        JsonNode seul = mapper.readTree(mvc.perform(get("/api/v1/paiements/caisse").with(agent)).andReturn().getResponse().getContentAsString());
        assertThat(seul).hasSize(1);
        assertThat(seul.get(0).path("total").asLong()).isEqualTo(7_000);
        mvc.perform(get("/api/v1/paiements/caisse").param("jour", aujourdhui.minusDays(1).toString()).with(ADMIN)).andExpect(jsonPath("$.length()").value(0));
    }

    // ================================================================== droits

    @Test
    void droits() throws Exception {
        UUID compte = utilisateurs.creer(new UtilisateurService.Saisie("Compte", "+237" + TEL.incrementAndGet(), null, Role.LOCATAIRE, true, false), "Motdepasse123").id();
        UUID mien = nouveauLocataire(compte);
        UUID monBail = bailActif(mien, "P2", aujourdhui.withDayOfMonth(1));
        RequestPostProcessor loc = jwtRole(Role.LOCATAIRE, compte);
        UUID p = payerOk(1000, "ESPECES"); // paiement d'un AUTRE locataire

        mvc.perform(post("/api/v1/paiements").with(loc).contentType(MediaType.APPLICATION_JSON).content(json("bailId", monBail.toString(), "montant", 1000, "mode", "ESPECES")))
                .andExpect(status().isForbidden()).andExpect(jsonPath("$.code").value("ACCES_REFUSE"));
        mvc.perform(post("/api/v1/paiements/{id}/contre-passation", p).with(loc).contentType(MediaType.APPLICATION_JSON).content(json("motif", "x"))).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/paiements/caisse").with(loc)).andExpect(status().isForbidden());
        for (Role r : new Role[] {Role.PROPRIETAIRE_TIERS, Role.PRESTATAIRE}) {
            mvc.perform(get("/api/v1/paiements").with(jwtRole(r))).andExpect(status().isForbidden());
            mvc.perform(get("/api/v1/locataires/{id}/compte", locataire).with(jwtRole(r))).andExpect(status().isForbidden());
        }
        mvc.perform(get("/api/v1/paiements")).andExpect(status().isUnauthorized());

        // lecture limitée à soi
        mvc.perform(get("/api/v1/paiements").with(loc)).andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(0));
        mvc.perform(get("/api/v1/paiements").param("bailId", bail.toString()).with(loc)).andExpect(status().isNotFound());
        mvc.perform(get("/api/v1/paiements").param("locataireId", locataire.toString()).with(loc)).andExpect(status().isNotFound());
        mvc.perform(get("/api/v1/locataires/{id}/compte", locataire).with(loc)).andExpect(status().isNotFound());
        mvc.perform(get("/api/v1/locataires/{id}/compte", mien).with(loc)).andExpect(status().isOk());
        await().atMost(Duration.ofSeconds(15)).until(() -> echeances(monBail).size() > 0);
        mvc.perform(post("/api/v1/baux/{id}/documents", monBail).with(ADMIN)).andExpect(status().isCreated());
    }
}
