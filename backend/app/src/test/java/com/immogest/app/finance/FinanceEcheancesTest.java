package com.immogest.app.finance;

import static com.immogest.app.securite.SecuriteTestSupport.json;
import static com.immogest.app.securite.SecuriteTestSupport.jwtRole;
import static org.assertj.core.api.Assertions.assertThat;
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
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

/** Moteur d'échéances de bout en bout : activation du bail → échéances, avance, idempotence, résiliation, droits. */
@IntegrationTest
@TestPropertySource(properties = {"immogest.finance.horizon-jours=45", "immogest.finance.delai-grace-jours=5"})
class FinanceEcheancesTest {

    private static final RequestPostProcessor ADMIN = jwtRole(Role.ADMIN, UUID.fromString("00000000-0000-0000-0000-0000000000a1"));
    private static final AtomicLong TEL = new AtomicLong(680_000_000L);
    private static final long LOYER = 150_000;
    private static final long CHARGES = 10_000;
    private static final long MENSUEL = LOYER + CHARGES;

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    JdbcTemplate jdbc;
    @Autowired
    UtilisateurService utilisateurs;
    @Autowired
    EcheanceService service;

    UUID immeuble;
    UUID locataire;
    LocalDate aujourdhui;

    @BeforeEach
    void preparer() throws Exception {
        aujourdhui = LocalDate.now(ZoneId.of("Africa/Douala"));
        attendreOutboxVide();
        jdbc.execute("TRUNCATE fin_document, fin_compteur_numero, fin_imputation, fin_paiement, fin_echeance, bail_document, bail_garant, bail, patrimoine_photo, compteur, lot_equipement, lot, immeuble, garant, tiers_piece, locataire CASCADE");
        immeuble = id(mvc.perform(post("/api/v1/immeubles").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("nom", "Résidence Finance", "adresse", "1 rue", "ville", "Douala"))));
        locataire = nouveauLocataire(null);
    }

    /** Laisse finir les listeners asynchrones du test précédent (quittances…) avant de vider les tables, sinon TRUNCATE attend leurs verrous. */
    private void attendreOutboxVide() {
        try {
            await().atMost(Duration.ofSeconds(20)).until(() -> jdbc.queryForObject("SELECT count(*) FROM event_publication WHERE completion_date IS NULL", Long.class) == 0);
        } catch (org.awaitility.core.ConditionTimeoutException ignore) {
            // des publications restées en échec ne doivent pas bloquer les tests suivants
        }
    }

    private UUID id(ResultActions r) throws Exception {
        return UUID.fromString(mapper.readTree(r.andReturn().getResponse().getContentAsString()).path("id").asText());
    }

    private UUID nouveauLocataire(UUID compte) throws Exception {
        return id(mvc.perform(post("/api/v1/locataires").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("nom", "Paul Finance", "telephone", "+237" + TEL.incrementAndGet(), "utilisateurId", compte == null ? null : compte.toString())))
                .andExpect(status().isCreated()));
    }

    private UUID lot(String ref) throws Exception {
        return id(mvc.perform(post("/api/v1/lots").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("immeubleId", immeuble.toString(), "reference", ref, "type", "STUDIO"))));
    }

    /** Crée et active un bail ; `Object... extra` écrase les valeurs par défaut (paires clé/valeur). */
    private UUID bailActif(UUID lotId, LocalDate debut, Object... extra) throws Exception {
        var corps = mapper.createObjectNode();
        corps.put("lotId", lotId.toString()).put("locataireId", locataire.toString()).put("dateDebut", debut.toString())
                .put("loyer", LOYER).put("charges", CHARGES).put("periodicite", "MENSUELLE").put("jourEcheance", 1).put("caution", 0).put("moisAvance", 0);
        for (int i = 0; i < extra.length; i += 2) {
            corps.putPOJO((String) extra[i], extra[i + 1]);
        }
        UUID bail = id(mvc.perform(post("/api/v1/baux").with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(corps.toString()))
                .andExpect(status().isCreated()));
        mvc.perform(post("/api/v1/baux/{id}/activer", bail).with(ADMIN)).andExpect(status().isOk());
        return bail;
    }

    private JsonNode echeances(UUID bail) throws Exception {
        return mapper.readTree(mvc.perform(get("/api/v1/echeances").param("bailId", bail.toString()).param("size", "100").with(ADMIN))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString()).path("content");
    }

    private void attendreEcheances(UUID bail) {
        await().atMost(Duration.ofSeconds(15)).until(() -> echeances(bail).size() > 0);
    }

    private void generer() throws Exception {
        mvc.perform(post("/api/v1/echeances/generer").with(ADMIN)).andExpect(status().isOk());
    }

    private long compter(UUID bail) {
        return jdbc.queryForObject("SELECT count(*) FROM fin_echeance WHERE bail_id = ?", Long.class, bail);
    }

    // ================================================================== génération et idempotence

    @Test
    void l_activationGenereLesEcheancesEtLeJobNeCreeAucunDoublon() throws Exception {
        LocalDate debut = aujourdhui.minusMonths(2).withDayOfMonth(1);
        UUID bail = bailActif(lot("A1"), debut);
        attendreEcheances(bail);
        generer();

        long n = compter(bail);
        assertThat(n).isGreaterThanOrEqualTo(3);
        assertThat(jdbc.queryForObject("SELECT count(DISTINCT periode_debut) FROM fin_echeance WHERE bail_id = ?", Long.class, bail)).isEqualTo(n);
        generer();
        generer();
        assertThat(compter(bail)).isEqualTo(n);

        JsonNode liste = echeances(bail);
        assertThat(liste.get(0).path("montant").asLong()).isEqualTo(MENSUEL);
        assertThat(liste.get(0).path("periodeDebut").asText()).isEqualTo(debut.toString());
        // anciennes impayées au-delà de la grâce → EN_RETARD
        assertThat(liste.get(0).path("statut").asText()).isEqualTo("EN_RETARD");
    }

    @Test
    void parallelesLeJobEtLActivationNeProduisentPasDeDoublon() throws Exception {
        UUID bail = bailActif(lot("A2"), aujourdhui.minusMonths(1).withDayOfMonth(1));
        var pool = java.util.concurrent.Executors.newFixedThreadPool(4);
        try {
            var taches = new java.util.ArrayList<java.util.concurrent.Future<?>>();
            for (int i = 0; i < 4; i++) {
                taches.add(pool.submit(() -> service.genererPourBail(bail)));
            }
            for (var t : taches) {
                t.get();
            }
        } finally {
            pool.shutdown();
        }
        assertThat(jdbc.queryForObject("SELECT count(*) = count(DISTINCT periode_debut) FROM fin_echeance WHERE bail_id = ?", Boolean.class, bail)).isTrue();
    }

    @Test
    void brouillonSansEcheance() throws Exception {
        UUID lot = lot("B1");
        UUID bail = id(mvc.perform(post("/api/v1/baux").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("lotId", lot.toString(), "locataireId", locataire.toString(), "dateDebut", aujourdhui.minusMonths(1).toString(),
                        "loyer", LOYER, "periodicite", "MENSUELLE", "jourEcheance", 1))).andExpect(status().isCreated()));
        generer();
        assertThat(compter(bail)).isZero();
    }

    // ================================================================== avance

    @Test
    void l_avanceSoldeLesPremieresEcheancesEtResteImputeeUneSeuleFois() throws Exception {
        LocalDate debut = aujourdhui.minusMonths(1).withDayOfMonth(1);
        UUID bail = bailActif(lot("C1"), debut, "moisAvance", 2);
        attendreEcheances(bail);
        generer();
        generer();

        JsonNode liste = echeances(bail);
        assertThat(liste.size()).isGreaterThanOrEqualTo(2);
        for (int i = 0; i < 2; i++) {
            assertThat(liste.get(i).path("statut").asText()).isEqualTo("PAYEE");
            assertThat(liste.get(i).path("montantPaye").asLong()).isEqualTo(MENSUEL);
            assertThat(liste.get(i).path("reste").asLong()).isZero();
        }
        if (liste.size() > 2) {
            assertThat(liste.get(2).path("montantPaye").asLong()).isZero();
        }
        assertThat(jdbc.queryForObject("SELECT count(*) FROM fin_paiement WHERE bail_id = ? AND type = 'AVANCE'", Long.class, bail)).isEqualTo(1);
        assertThat(jdbc.queryForObject("SELECT sum(i.montant) FROM fin_imputation i JOIN fin_paiement p ON p.id = i.paiement_id WHERE p.bail_id = ?",
                Long.class, bail)).isEqualTo(2 * MENSUEL);
    }

    @Test
    void avanceDeDeuxMoisSurUneEcheanceTrimestrielleLaLaisseEnPartie() throws Exception {
        LocalDate debut = aujourdhui.plusDays(10);
        UUID bail = bailActif(lot("C2"), debut, "periodicite", "TRIMESTRIELLE", "moisAvance", 2);
        attendreEcheances(bail);
        generer();
        JsonNode premiere = echeances(bail).get(0);
        assertThat(premiere.path("montant").asLong()).isEqualTo(3 * MENSUEL);
        assertThat(premiere.path("montantPaye").asLong()).isEqualTo(2 * MENSUEL);
        assertThat(premiere.path("reste").asLong()).isEqualTo(MENSUEL);
        assertThat(premiere.path("statut").asText()).isEqualTo("PARTIELLE");
    }

    @Test
    void avanceSuperieureAuxEcheancesCreeesSAppliqueAuxSuivantes() throws Exception {
        // 6 mois d'avance, bail démarrant aujourd'hui : seules 1 à 2 échéances existent d'abord
        UUID bail = bailActif(lot("C3"), aujourdhui, "moisAvance", 6);
        attendreEcheances(bail);
        generer();
        long deja = jdbc.queryForObject("SELECT sum(montant_paye) FROM fin_echeance WHERE bail_id = ?", Long.class, bail);
        assertThat(deja).isEqualTo(compter(bail) * MENSUEL);
        // un mois plus tard (simulé en reculant le début du bail) le crédit restant couvre les nouvelles échéances
        jdbc.update("UPDATE bail SET date_debut = date_debut - interval '3 months' WHERE id = ?", bail);
        generer();
        long total = jdbc.queryForObject("SELECT sum(montant_paye) FROM fin_echeance WHERE bail_id = ?", Long.class, bail);
        assertThat(total).isLessThanOrEqualTo(6 * MENSUEL);
        assertThat(total).isGreaterThan(deja);
    }

    // ================================================================== sortie

    @Test
    void sortieAnticipee_periodeEcourteeAuProrataEtPeriodesSuivantesAnnulees() throws Exception {
        LocalDate debut = aujourdhui.withDayOfMonth(1).minusMonths(1);
        UUID bail = bailActif(lot("D1"), debut);
        attendreEcheances(bail);
        generer();
        long avant = compter(bail);

        LocalDate sortie = aujourdhui;
        mvc.perform(post("/api/v1/baux/{id}/resilier", bail).with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("dateNotification", aujourdhui.toString(), "dateSortie", sortie.toString(), "preavisReduitAccepte", true, "motif", "Départ")))
                .andExpect(status().isOk());
        generer();
        generer();

        assertThat(compter(bail)).isEqualTo(avant); // rien n'est supprimé
        JsonNode liste = echeances(bail);
        for (JsonNode e : liste) {
            LocalDate pd = LocalDate.parse(e.path("periodeDebut").asText());
            if (pd.isAfter(sortie)) {
                assertThat(e.path("statut").asText()).isEqualTo("ANNULEE");
            } else {
                assertThat(LocalDate.parse(e.path("periodeFin").asText())).isBeforeOrEqualTo(sortie);
            }
        }
    }

    // ================================================================== droits

    @Test
    void droits() throws Exception {
        UUID compte = utilisateurs.creer(new UtilisateurService.Saisie("Compte", "+237" + TEL.incrementAndGet(), null, Role.LOCATAIRE, true, false), "Motdepasse123").id();
        locataire = nouveauLocataire(compte);
        UUID mien = bailActif(lot("E1"), aujourdhui.withDayOfMonth(1));
        UUID autreLocataire = nouveauLocataire(null);
        UUID locBak = locataire;
        locataire = autreLocataire;
        UUID autre = bailActif(lot("E2"), aujourdhui.withDayOfMonth(1));
        locataire = locBak;
        attendreEcheances(mien);
        attendreEcheances(autre);
        RequestPostProcessor loc = jwtRole(Role.LOCATAIRE, compte);

        mvc.perform(get("/api/v1/echeances").param("bailId", mien.toString()).with(loc)).andExpect(status().isOk());
        mvc.perform(get("/api/v1/echeances").param("bailId", autre.toString()).with(loc)).andExpect(status().isNotFound());
        mvc.perform(get("/api/v1/echeances").with(loc)).andExpect(status().isNotFound());
        mvc.perform(get("/api/v1/echeances").with(jwtRole(Role.PROPRIETAIRE_TIERS))).andExpect(status().isForbidden()).andExpect(jsonPath("$.code").value("ACCES_REFUSE"));
        mvc.perform(get("/api/v1/echeances").with(jwtRole(Role.GESTIONNAIRE))).andExpect(status().isOk());
        mvc.perform(post("/api/v1/echeances/generer").with(jwtRole(Role.GESTIONNAIRE))).andExpect(status().isForbidden());
        mvc.perform(post("/api/v1/echeances/generer").with(loc)).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/echeances")).andExpect(status().isUnauthorized());
    }
}
