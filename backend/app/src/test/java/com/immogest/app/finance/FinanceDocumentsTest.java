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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

/** Quittances et reçus : déclenchement, contenu PDF, numérotation continue, annulation, accès. */
@IntegrationTest
@TestPropertySource(properties = {"immogest.finance.horizon-jours=45", "immogest.finance.delai-grace-jours=5"})
class FinanceDocumentsTest {

    private static final RequestPostProcessor ADMIN = jwtRole(Role.ADMIN, UUID.fromString("00000000-0000-0000-0000-0000000000a1"));
    private static final AtomicLong TEL = new AtomicLong(660_000_000L);
    private static final long MENSUEL = 160_000;

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    JdbcTemplate jdbc;
    @Autowired
    UtilisateurService utilisateurs;
    @Autowired
    DocumentService documents;

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
                .content(json("nom", "Résidence Quittances", "adresse", "12 rue des Palmiers", "ville", "Douala"))));
        locataire = nouveauLocataire(null);
        bail = bailActif(locataire, "Q1", 0);
        await().atMost(Duration.ofSeconds(15)).until(() -> echeances(bail).size() >= 4);
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
                .content(json("nom", "Paul Quittance", "telephone", "+237" + TEL.incrementAndGet(), "utilisateurId", compte == null ? null : compte.toString())))
                .andExpect(status().isCreated()));
    }

    private UUID bailActif(UUID loc, String ref, int moisAvance) throws Exception {
        UUID lot = id(mvc.perform(post("/api/v1/lots").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("immeubleId", immeuble.toString(), "reference", ref, "type", "STUDIO"))));
        UUID b = id(mvc.perform(post("/api/v1/baux").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("lotId", lot.toString(), "locataireId", loc.toString(), "dateDebut", aujourdhui.withDayOfMonth(1).minusMonths(3).toString(),
                        "loyer", 150_000, "charges", 10_000, "periodicite", "MENSUELLE", "jourEcheance", 1, "moisAvance", moisAvance))).andExpect(status().isCreated()));
        mvc.perform(post("/api/v1/baux/{id}/activer", b).with(ADMIN)).andExpect(status().isOk());
        return b;
    }

    private JsonNode echeances(UUID b) throws Exception {
        return mapper.readTree(mvc.perform(get("/api/v1/echeances").param("bailId", b.toString()).param("size", "100").with(ADMIN))
                .andReturn().getResponse().getContentAsString()).path("content");
    }

    private JsonNode docs(UUID b, String type) throws Exception {
        return mapper.readTree(mvc.perform(get("/api/v1/documents-paiement").param("bailId", b.toString()).param("type", type).param("sort", "numero")
                .with(ADMIN)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString()).path("content");
    }

    private UUID payer(long montant) throws Exception {
        return id(mvc.perform(post("/api/v1/paiements").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("bailId", bail.toString(), "montant", montant, "mode", "ESPECES"))).andExpect(status().isCreated()));
    }

    private void attendre(String type, int n) {
        await().atMost(Duration.ofSeconds(20)).untilAsserted(() -> assertThat(docs(bail, type).size()).isEqualTo(n));
    }

    private String texte(UUID fichierId, RequestPostProcessor qui) throws Exception {
        byte[] pdf = mvc.perform(get("/api/v1/fichiers/{id}", fichierId).with(qui)).andExpect(status().isOk()).andReturn().getResponse().getContentAsByteArray();
        try (PDDocument d = PDDocument.load(pdf)) {
            return new PDFTextStripper().getText(d).replace(' ', ' ').replaceAll("\\s+", " ");
        }
    }

    // ================================================================== déclenchement

    @Test
    void paiementCompletDonneUneQuittanceEtPasDeRecu() throws Exception {
        payer(MENSUEL);
        attendre("QUITTANCE", 1);
        Thread.sleep(800);
        assertThat(docs(bail, "RECU")).isEmpty();

        JsonNode q = docs(bail, "QUITTANCE").get(0);
        assertThat(q.path("numero").asText()).isEqualTo("QUI-%d-000001".formatted(aujourdhui.getYear()));
        assertThat(q.path("statut").asText()).isEqualTo("EMIS");
        assertThat(q.path("montant").asLong()).isEqualTo(MENSUEL);
        assertThat(q.path("periodeDebut").asText()).isEqualTo(aujourdhui.withDayOfMonth(1).minusMonths(3).toString());
        String t = texte(UUID.fromString(q.path("fichierId").asText()), ADMIN);
        assertThat(t).contains("QUITTANCE DE LOYER").contains(q.path("numero").asText()).contains("Paul Quittance").contains("160 000 FCFA")
                .contains("150 000 FCFA").contains("10 000 FCFA").contains("Résidence Quittances").contains("espèces");
    }

    @Test
    void paiementPartielDonneUnRecuPuisLeSoldeUneQuittance() throws Exception {
        payer(100_000);
        attendre("RECU", 1);
        assertThat(docs(bail, "QUITTANCE")).isEmpty();
        JsonNode r = docs(bail, "RECU").get(0);
        assertThat(r.path("numero").asText()).isEqualTo("REC-%d-000001".formatted(aujourdhui.getYear()));
        String t = texte(UUID.fromString(r.path("fichierId").asText()), ADMIN);
        assertThat(t).contains("REÇU DE PAIEMENT").contains("100 000 FCFA").contains("reste dû 60 000 FCFA").contains("n'est pas une quittance");

        payer(60_000); // solde l'échéance : quittance, mais pas de second reçu
        attendre("QUITTANCE", 1);
        Thread.sleep(800);
        assertThat(docs(bail, "RECU")).hasSize(1);
        assertThat(docs(bail, "QUITTANCE").get(0).path("montant").asLong()).isEqualTo(MENSUEL);
    }

    @Test
    void unPaiementSurPlusieursEcheancesDonneUneQuittanceParEcheanceSansTrouDeNumero() throws Exception {
        payer(3 * MENSUEL + 20_000);
        attendre("QUITTANCE", 3);
        attendre("RECU", 1); // le reliquat de 20 000 sur la 4e échéance
        List<String> numeros = new ArrayList<>();
        docs(bail, "QUITTANCE").forEach(d -> numeros.add(d.path("numero").asText()));
        String p = "QUI-%d-".formatted(aujourdhui.getYear());
        assertThat(numeros).containsExactly(p + "000001", p + "000002", p + "000003");
        assertThat(jdbc.queryForObject("SELECT count(DISTINCT echeance_id) FROM fin_document WHERE type = 'QUITTANCE'", Long.class)).isEqualTo(3);
    }

    @Test
    void emissionIdempotente() throws Exception {
        payer(MENSUEL);
        attendre("QUITTANCE", 1);
        UUID echeance = UUID.fromString(echeances(bail).get(0).path("id").asText());
        documents.emettreQuittance(echeance);
        documents.emettreQuittance(echeance);
        assertThat(docs(bail, "QUITTANCE")).hasSize(1);
        assertThat(jdbc.queryForObject("SELECT dernier FROM fin_compteur_numero WHERE type = 'QUITTANCE'", Long.class)).isEqualTo(1);
    }

    @Test
    void lAvanceSoldeDesEcheancesEtDonneDesQuittances() throws Exception {
        UUID autre = nouveauLocataire(null);
        UUID b2 = bailActif(autre, "Q2", 2);
        await().atMost(Duration.ofSeconds(20)).untilAsserted(() -> assertThat(docs(b2, "QUITTANCE").size()).isEqualTo(2));
        assertThat(docs(b2, "RECU")).isEmpty();
        String t = texte(UUID.fromString(docs(b2, "QUITTANCE").get(0).path("fichierId").asText()), ADMIN);
        assertThat(t).contains("avance versée à la signature");
    }

    // ================================================================== contre-passation

    @Test
    void contrePassationAnnuleLesDocumentsPuisUneNouvelleQuittanceEstEmise() throws Exception {
        UUID p = payer(MENSUEL + 50_000);
        attendre("QUITTANCE", 1);
        attendre("RECU", 1);
        String ancien = docs(bail, "QUITTANCE").get(0).path("numero").asText();

        mvc.perform(post("/api/v1/paiements/{id}/contre-passation", p).with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("motif", "Erreur")))
                .andExpect(status().isCreated());
        assertThat(jdbc.queryForObject("SELECT count(*) FROM fin_document WHERE statut = 'ANNULE'", Long.class)).isEqualTo(2);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM fin_document", Long.class)).isEqualTo(2); // rien de supprimé
        assertThat(docs(bail, "QUITTANCE").get(0).path("motifAnnulation").asText()).contains("Contre-passation");

        payer(MENSUEL);
        await().atMost(Duration.ofSeconds(20)).untilAsserted(() -> assertThat(docs(bail, "QUITTANCE").size()).isEqualTo(2));
        JsonNode liste = docs(bail, "QUITTANCE");
        assertThat(liste.get(1).path("numero").asText()).isNotEqualTo(ancien).endsWith("000002");
        assertThat(liste.get(1).path("statut").asText()).isEqualTo("EMIS");
        // l'ancien PDF reste lisible par un gestionnaire
        assertThat(texte(UUID.fromString(liste.get(0).path("fichierId").asText()), ADMIN)).contains(ancien);
    }

    // ================================================================== accès

    @Test
    void acces() throws Exception {
        UUID compte = utilisateurs.creer(new UtilisateurService.Saisie("Compte", "+237" + TEL.incrementAndGet(), null, Role.LOCATAIRE, true, false), "Motdepasse123").id();
        UUID mien = nouveauLocataire(compte);
        UUID monBail = bailActif(mien, "Q3", 1);
        await().atMost(Duration.ofSeconds(20)).untilAsserted(() -> assertThat(docs(monBail, "QUITTANCE").size()).isEqualTo(1));
        payer(MENSUEL); // quittance d'un autre locataire
        attendre("QUITTANCE", 1);
        RequestPostProcessor loc = jwtRole(Role.LOCATAIRE, compte);

        JsonNode mesDocs = mapper.readTree(mvc.perform(get("/api/v1/documents-paiement").with(loc)).andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1)).andReturn().getResponse().getContentAsString()).path("content");
        UUID monFichier = UUID.fromString(mesDocs.get(0).path("fichierId").asText());
        mvc.perform(get("/api/v1/fichiers/{id}", monFichier).with(loc)).andExpect(status().isOk());
        UUID fichierAutre = UUID.fromString(docs(bail, "QUITTANCE").get(0).path("fichierId").asText());
        mvc.perform(get("/api/v1/fichiers/{id}", fichierAutre).with(loc)).andExpect(status().is4xxClientError());
        mvc.perform(get("/api/v1/documents-paiement").param("bailId", bail.toString()).with(loc)).andExpect(status().isNotFound());
        mvc.perform(get("/api/v1/documents-paiement").param("locataireId", locataire.toString()).with(loc)).andExpect(status().isNotFound());

        mvc.perform(get("/api/v1/documents-paiement").with(jwtRole(Role.PROPRIETAIRE_TIERS))).andExpect(status().isForbidden()).andExpect(jsonPath("$.code").value("ACCES_REFUSE"));
        mvc.perform(get("/api/v1/documents-paiement").with(jwtRole(Role.PRESTATAIRE))).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/documents-paiement")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/v1/fichiers/{id}", fichierAutre).with(jwtRole(Role.PROPRIETAIRE_TIERS))).andExpect(status().is4xxClientError());
        mvc.perform(get("/api/v1/documents-paiement").with(jwtRole(Role.GESTIONNAIRE))).andExpect(status().isOk());
    }

    @Test
    void evenementDEmissionAudite() throws Exception {
        payer(MENSUEL);
        attendre("QUITTANCE", 1);
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() ->
                assertThat(jdbc.queryForObject("SELECT count(*) FROM audit_log WHERE action = 'FINANCE.QUITTANCE_EMIS'", Long.class)).isEqualTo(1));
    }
}
