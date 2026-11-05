package com.immogest.app.relance;

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
import com.immogest.app.notification.TypeCanal;
import com.immogest.app.notification.infra.CanauxSimules.Registre;
import com.immogest.app.securite.Role;
import com.immogest.app.securite.UtilisateurService;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
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

/** Relances d'impayés : niveaux, rattrapage, idempotence, canal par préférence, arriérés, mise en demeure, droits. */
@IntegrationTest
@TestPropertySource(properties = {"immogest.finance.horizon-jours=45", "immogest.notification.delais-reessai=0s,0s,0s"})
class RelanceTest {

    private static final RequestPostProcessor ADMIN = jwtRole(Role.ADMIN, UUID.fromString("00000000-0000-0000-0000-0000000000a1"));
    private static final RequestPostProcessor GESTIONNAIRE = jwtRole(Role.GESTIONNAIRE);
    private static final AtomicLong TEL = new AtomicLong(640_000_000L);

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    JdbcTemplate jdbc;
    @Autowired
    Registre registre;
    @Autowired
    UtilisateurService utilisateurs;

    UUID immeuble;
    LocalDate aujourdhui;

    /** Début du test : on ne compte que les lignes d'audit créées depuis (le journal est immuable, jamais purgé entre deux tests : un compte global dépend de l'ordre d'exécution). */
    java.time.OffsetDateTime debutTest;

    @BeforeEach
    void preparer() throws Exception {
        aujourdhui = LocalDate.now(ZoneId.of("Africa/Douala"));
        attendreOutboxVide();
        jdbc.execute("TRUNCATE rel_mise_en_demeure, rel_relance, rel_parametres, rel_compteur_numero, notif_message, notif_appareil, notif_canal, fin_document, fin_compteur_numero, fin_imputation, fin_paiement, fin_echeance, bail_document, bail_garant, bail, patrimoine_photo, compteur, lot_equipement, lot, immeuble, garant, tiers_piece, locataire CASCADE");
        debutTest = java.time.OffsetDateTime.now();
        registre.reinitialiser();
        immeuble = id(mvc.perform(post("/api/v1/immeubles").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("nom", "Résidence Relance", "adresse", "5 rue des Rappels", "ville", "Douala"))));
    }

    private void attendreOutboxVide() {
        try {
            await().atMost(Duration.ofSeconds(20)).until(() -> jdbc.queryForObject("SELECT count(*) FROM event_publication WHERE completion_date IS NULL", Long.class) == 0);
        } catch (org.awaitility.core.ConditionTimeoutException ignore) {
            // publications en échec : sans effet sur ces tests
        }
    }

    private UUID id(ResultActions r) throws Exception {
        return UUID.fromString(mapper.readTree(r.andReturn().getResponse().getContentAsString()).path("id").asText());
    }

    private UUID locataire(UUID compte) throws Exception {
        return id(mvc.perform(post("/api/v1/locataires").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("nom", "Paul Relance", "telephone", "+237" + TEL.incrementAndGet(), "utilisateurId", compte == null ? null : compte.toString())))
                .andExpect(status().isCreated()));
    }

    /** Bail dont la première échéance tombe le {@code debut} (jour d'échéance 1 → échéance = début de période). */
    private UUID bail(UUID loc, String ref, LocalDate debut) throws Exception {
        UUID lot = id(mvc.perform(post("/api/v1/lots").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("immeubleId", immeuble.toString(), "reference", ref, "type", "STUDIO"))));
        UUID b = id(mvc.perform(post("/api/v1/baux").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("lotId", lot.toString(), "locataireId", loc.toString(), "dateDebut", debut.toString(), "loyer", 150_000, "charges", 10_000,
                        "periodicite", "MENSUELLE", "jourEcheance", 1, "moisAvance", 0))).andExpect(status().isCreated()));
        mvc.perform(post("/api/v1/baux/{id}/activer", b).with(ADMIN)).andExpect(status().isOk());
        await().atMost(Duration.ofSeconds(15)).until(() -> jdbc.queryForObject("SELECT count(*) FROM fin_echeance WHERE bail_id = ?", Long.class, b) >= 1);
        return b;
    }

    private void activerCanal(String canal, Map<String, String> champs) throws Exception {
        mvc.perform(put("/api/v1/notifications/canaux/{c}", canal).with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("actif", true, "champs", champs))).andExpect(status().isOk());
    }

    private JsonNode executer() throws Exception {
        return mapper.readTree(mvc.perform(post("/api/v1/relances/executer").with(ADMIN)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
    }

    private JsonNode historique(UUID bail) throws Exception {
        return mapper.readTree(mvc.perform(get("/api/v1/relances").param("bailId", bail.toString()).with(ADMIN)).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString()).path("content");
    }

    private UUID premiereEcheance(UUID bail) {
        return jdbc.queryForObject("SELECT id FROM fin_echeance WHERE bail_id = ? ORDER BY periode_debut LIMIT 1", UUID.class, bail);
    }

    // ================================================================== niveaux et canal

    @Test
    void relanceJPlus3AuPremierCanalDisponibleDansLOrdreWhatsappPushSms() throws Exception {
        activerCanal("SMS", Map.of("url", "https://sms.exemple.cm/send"));
        activerCanal("WHATSAPP", Map.of("phoneNumberId", "1", "token", "tok"));
        UUID loc = locataire(null);
        UUID bail = bail(loc, "R1", aujourdhui.minusDays(3));

        JsonNode bilan = executer();
        assertThat(bilan.path("envoyees").asInt()).isEqualTo(1);

        // J-5 est dépassé mais J+3 est le plus haut niveau atteint : lui seul part, J-5 est historisé « ignoré »
        JsonNode h = historique(bail);
        assertThat(h).hasSize(2);
        JsonNode envoyee = null;
        JsonNode ignoree = null;
        for (JsonNode r : h) {
            if (r.path("resultat").asText().equals("IGNOREE")) {
                ignoree = r;
            } else {
                envoyee = r;
            }
        }
        assertThat(ignoree.path("niveau").asInt()).isEqualTo(-5);
        assertThat(envoyee.path("niveau").asInt()).isEqualTo(3);
        assertThat(envoyee.path("resultat").asText()).isEqualTo("EN_FILE");
        assertThat(envoyee.path("canal").asText()).isEqualTo("WHATSAPP"); // pas SMS : WhatsApp d'abord
        assertThat(envoyee.path("joursRetard").asInt()).isEqualTo(3);
        assertThat(envoyee.path("resteDu").asLong()).isEqualTo(160_000);
        assertThat(envoyee.path("statutEnvoi").asText()).isEqualTo("ENVOYE");

        assertThat(registre.envois()).hasSize(1);
        assertThat(registre.envois().get(0).canal()).isEqualTo(TypeCanal.WHATSAPP);
        assertThat(registre.envois().get(0).envoi().corps()).contains("160 000 FCFA").contains("3 jours de retard").contains("Paul Relance");
    }

    @Test
    void smsEnDernierRecoursEtAucunCanalHistorise() throws Exception {
        UUID loc = locataire(null);
        UUID bail = bail(loc, "R2", aujourdhui.minusDays(3));
        executer();
        JsonNode r = historique(bail).get(0).path("resultat").asText().equals("IGNOREE") ? historique(bail).get(1) : historique(bail).get(0);
        assertThat(r.path("resultat").asText()).isEqualTo("AUCUN_CANAL"); // rien d'actif : visible plutôt que perdu
        assertThat(registre.envois()).isEmpty();

        activerCanal("SMS", Map.of("url", "https://sms.exemple.cm/send"));
        UUID loc2 = locataire(null);
        UUID bail2 = bail(loc2, "R3", aujourdhui.minusDays(10));
        executer();
        assertThat(registre.envois()).hasSize(1);
        assertThat(registre.envois().get(0).canal()).isEqualTo(TypeCanal.SMS);
        assertThat(registre.envois().get(0).envoi().corps()).contains("10 j de retard");
    }

    @Test
    void rappelJMoins5AvantLEcheance() throws Exception {
        activerCanal("SMS", Map.of("url", "https://sms.exemple.cm/send"));
        UUID loc = locataire(null);
        UUID bail = bail(loc, "R4", aujourdhui.plusDays(5));
        executer();
        assertThat(registre.envois()).hasSize(1);
        assertThat(registre.envois().get(0).envoi().corps()).contains("rappel").contains("160 000 FCFA");
        JsonNode h = historique(bail);
        assertThat(h).hasSize(1);
        assertThat(h.get(0).path("niveau").asInt()).isEqualTo(-5);
        assertThat(h.get(0).path("joursRetard").asInt()).isZero();
    }

    @Test
    void pasDeRelanceAvantLeNiveauNiPourUneEcheanceSoldee() throws Exception {
        activerCanal("SMS", Map.of("url", "https://sms.exemple.cm/send"));
        UUID loc = locataire(null);
        UUID tropTot = bail(loc, "R5", aujourdhui.plusDays(20)); // échéance dans 20 jours : J-5 pas encore atteint
        UUID loc2 = locataire(null);
        UUID payee = bail(loc2, "R6", aujourdhui.minusDays(3));
        mvc.perform(post("/api/v1/paiements").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("bailId", payee.toString(), "montant", 160_000, "mode", "VIREMENT", "reference", "V1"))).andExpect(status().isCreated());
        registre.reinitialiser();
        executer();
        assertThat(historique(tropTot)).isEmpty();
        assertThat(historique(payee)).isEmpty();
        assertThat(registre.envois()).isEmpty();
    }

    @Test
    void jobIdempotentEtNiveauSuivantQuandLeTempsPasse() throws Exception {
        activerCanal("SMS", Map.of("url", "https://sms.exemple.cm/send"));
        UUID loc = locataire(null);
        UUID bail = bail(loc, "R7", aujourdhui.minusDays(3));
        executer();
        executer();
        executer();
        assertThat(registre.envois()).hasSize(1);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM rel_relance WHERE bail_id = ?", Long.class, bail)).isEqualTo(2);

        // une semaine plus tard (échéance reculée) : le niveau J+10 part, une seule fois
        jdbc.update("UPDATE fin_echeance SET date_echeance = date_echeance - 7 WHERE bail_id = ?", bail);
        executer();
        executer();
        assertThat(registre.envois()).hasSize(2);
        assertThat(registre.envois().get(1).envoi().corps()).contains("10 j de retard");
        assertThat(jdbc.queryForObject("SELECT count(*) FROM rel_relance WHERE bail_id = ? AND niveau = 10", Long.class, bail)).isEqualTo(1);
    }

    @Test
    void arriereTropAncienNEstPasRelanceAutomatiquementMaisLesRecentsLeSont() throws Exception {
        activerCanal("SMS", Map.of("url", "https://sms.exemple.cm/send"));
        UUID loc = locataire(null);
        UUID bail = bail(loc, "R8", aujourdhui.minusDays(100));
        executer();
        // la 1re échéance (100 jours) est historisée « ignorée » : aucun message ; les suivantes (≈ 38 et 8 jours) sont relancées
        UUID ancienne = premiereEcheance(bail);
        JsonNode h = mapper.readTree(mvc.perform(get("/api/v1/relances").param("echeanceId", ancienne.toString()).with(ADMIN)).andReturn().getResponse().getContentAsString()).path("content");
        assertThat(h).isNotEmpty();
        for (JsonNode r : h) {
            assertThat(r.path("resultat").asText()).isEqualTo("IGNOREE");
        }
        assertThat(registre.envois()).isNotEmpty();
        assertThat(registre.envois().stream().noneMatch(t -> t.envoi().corps().contains("100 j"))).isTrue();
    }

    @Test
    void relanceManuelle() throws Exception {
        activerCanal("SMS", Map.of("url", "https://sms.exemple.cm/send"));
        UUID loc = locataire(null);
        UUID bail = bail(loc, "R9", aujourdhui.minusDays(1));
        UUID e = premiereEcheance(bail);
        mvc.perform(post("/api/v1/relances").with(GESTIONNAIRE).contentType(MediaType.APPLICATION_JSON).content(json("echeanceId", e.toString())))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.type").value("MANUELLE")).andExpect(jsonPath("$.niveau").doesNotExist())
                .andExpect(jsonPath("$.resultat").value("EN_FILE")).andExpect(jsonPath("$.canal").value("SMS"));
        mvc.perform(post("/api/v1/relances").with(GESTIONNAIRE).contentType(MediaType.APPLICATION_JSON).content(json("echeanceId", e.toString())))
                .andExpect(status().isCreated()); // plusieurs relances manuelles possibles
        assertThat(registre.envois()).hasSize(2);

        mvc.perform(post("/api/v1/paiements").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("bailId", bail.toString(), "montant", 160_000, "mode", "VIREMENT", "reference", "V2"))).andExpect(status().isCreated());
        mvc.perform(post("/api/v1/relances").with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("echeanceId", e.toString())))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("RELANCE_ECHEANCE_SOLDEE"));
        mvc.perform(post("/api/v1/relances").with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("echeanceId", UUID.randomUUID().toString())))
                .andExpect(status().isNotFound());
    }

    // ================================================================== paramètres

    @Test
    void parametresValidesEtPrisEnCompte() throws Exception {
        mvc.perform(get("/api/v1/relances/parametres").with(GESTIONNAIRE)).andExpect(status().isOk()).andExpect(jsonPath("$.actif").value(true))
                .andExpect(jsonPath("$.niveaux[0]").value(-5)).andExpect(jsonPath("$.niveaux.length()").value(4)).andExpect(jsonPath("$.delaiMiseEnDemeureJours").value(8));
        for (Object[] invalide : new Object[][] {{List.of(), 8}, {List.of(3, 3), 8}, {List.of(-5, -2), 8}, {List.of(500), 8}, {List.of(3), 0}}) {
            mvc.perform(put("/api/v1/relances/parametres").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                    .content(json("actif", true, "niveaux", invalide[0], "delaiMiseEnDemeureJours", invalide[1]))).andExpect(status().isBadRequest());
        }
        mvc.perform(put("/api/v1/relances/parametres").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(json("actif", true, "niveaux", List.of(7, 2), "delaiMiseEnDemeureJours", 15)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.niveaux[0]").value(2)).andExpect(jsonPath("$.niveaux[1]").value(7));

        activerCanal("SMS", Map.of("url", "https://sms.exemple.cm/send"));
        UUID loc = locataire(null);
        UUID bail = bail(loc, "P1", aujourdhui.minusDays(2)); // niveau 2 atteint, 7 non
        executer();
        JsonNode h = historique(bail);
        assertThat(h).hasSize(1);
        assertThat(h.get(0).path("niveau").asInt()).isEqualTo(2);

        mvc.perform(put("/api/v1/relances/parametres").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("actif", false, "niveaux", List.of(2, 7), "delaiMiseEnDemeureJours", 15))).andExpect(status().isOk());
        jdbc.update("UPDATE fin_echeance SET date_echeance = date_echeance - 6 WHERE bail_id = ?", bail);
        registre.reinitialiser();
        assertThat(executer().path("examinees").asInt()).isZero(); // désactivé : aucun envoi
        assertThat(registre.envois()).isEmpty();
    }

    // ================================================================== arriérés et mise en demeure

    @Test
    void listeDesArrieres() throws Exception {
        activerCanal("SMS", Map.of("url", "https://sms.exemple.cm/send"));
        UUID loc = locataire(null);
        UUID bail = bail(loc, "A1", aujourdhui.minusDays(40));
        executer();
        JsonNode liste = mapper.readTree(mvc.perform(get("/api/v1/arrieres").with(GESTIONNAIRE)).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString()).path("content");
        assertThat(liste.size()).isGreaterThanOrEqualTo(1);
        JsonNode a = liste.get(0);
        assertThat(a.path("locataireNom").asText()).isEqualTo("Paul Relance");
        assertThat(a.path("logement").asText()).isEqualTo("Résidence Relance — A1");
        assertThat(a.path("joursRetard").asInt()).isEqualTo(40);
        assertThat(a.path("reste").asLong()).isEqualTo(160_000);
        assertThat(a.path("peutMiseEnDemeure").asBoolean()).isTrue();
        assertThat(a.path("derniereRelance").path("niveau").asInt()).isEqualTo(30);
        assertThat(a.path("miseEnDemeureId").isNull()).isTrue();

        // recherche par locataire
        UUID autre = locataire(null);
        bail(autre, "A2", aujourdhui.minusDays(40));
        jdbc.update("UPDATE locataire SET nom = 'Zoe Autre' WHERE id = ?", autre);
        JsonNode filtre = mapper.readTree(mvc.perform(get("/api/v1/arrieres").param("q", "Zoe").with(GESTIONNAIRE)).andReturn().getResponse().getContentAsString());
        assertThat(filtre.path("totalElements").asInt()).isGreaterThanOrEqualTo(1);
        assertThat(filtre.path("content").findValuesAsText("locataireNom")).containsOnly("Zoe Autre");
        mvc.perform(get("/api/v1/arrieres").param("q", "introuvable-xyz").with(GESTIONNAIRE)).andExpect(jsonPath("$.totalElements").value(0));

        // un paiement solde la 1re échéance : elle sort de la liste
        mvc.perform(post("/api/v1/paiements").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("bailId", bail.toString(), "montant", 160_000, "mode", "VIREMENT", "reference", "V3"))).andExpect(status().isCreated());
        JsonNode apres = mapper.readTree(mvc.perform(get("/api/v1/arrieres").with(GESTIONNAIRE)).andReturn().getResponse().getContentAsString()).path("content");
        assertThat(apres.findValuesAsText("echeanceId")).doesNotContain(a.path("echeanceId").asText());
    }

    @Test
    void miseEnDemeureNumeroteeAvecPdfTelechargeable() throws Exception {
        UUID compte = utilisateurs.creer(new UtilisateurService.Saisie("Compte", "+237" + TEL.incrementAndGet(), null, Role.LOCATAIRE, true, false), "Motdepasse123").id();
        UUID loc = locataire(compte);
        UUID bail = bail(loc, "M1", aujourdhui.minusDays(40));
        UUID e = premiereEcheance(bail);
        activerCanal("SMS", Map.of("url", "https://sms.exemple.cm/send"));

        String reponse = mvc.perform(post("/api/v1/mises-en-demeure").with(GESTIONNAIRE).contentType(MediaType.APPLICATION_JSON).content(json("echeanceId", e.toString())))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.numero").value("MED-%d-000001".formatted(aujourdhui.getYear())))
                .andExpect(jsonPath("$.delaiJours").value(8)).andReturn().getResponse().getContentAsString();
        JsonNode med = mapper.readTree(reponse);
        long total = med.path("montantArrieres").asLong();
        assertThat(total).isGreaterThanOrEqualTo(2 * 160_000L); // au moins 2 échéances échues (J-40 et J-9)

        byte[] contenu = mvc.perform(get("/api/v1/fichiers/{id}", med.path("fichierId").asText()).with(GESTIONNAIRE)).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();
        try (PDDocument d = PDDocument.load(contenu)) {
            String t = new PDFTextStripper().getText(d).replace(' ', ' ').replaceAll("\\s+", " ");
            assertThat(t).contains("MISE EN DEMEURE DE PAYER").contains(med.path("numero").asText()).contains("Paul Relance").contains("TOTAL À RÉGLER")
                    .contains("dans un délai de 8 jours").contains("Résidence Relance");
        }
        // le locataire peut la télécharger ; un autre non
        mvc.perform(get("/api/v1/fichiers/{id}", med.path("fichierId").asText()).with(jwtRole(Role.LOCATAIRE, compte))).andExpect(status().isOk());
        mvc.perform(get("/api/v1/fichiers/{id}", med.path("fichierId").asText()).with(jwtRole(Role.LOCATAIRE))).andExpect(status().is4xxClientError());
        mvc.perform(get("/api/v1/mises-en-demeure").with(jwtRole(Role.LOCATAIRE, compte))).andExpect(jsonPath("$.totalElements").value(1));
        mvc.perform(get("/api/v1/mises-en-demeure").with(jwtRole(Role.LOCATAIRE))).andExpect(jsonPath("$.totalElements").value(0));
        // une seule par échéance ; l'échéance est marquée dans la liste des arriérés
        mvc.perform(post("/api/v1/mises-en-demeure").with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("echeanceId", e.toString())))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("RELANCE_MED_EXISTANTE"));
        JsonNode arrieres = mapper.readTree(mvc.perform(get("/api/v1/arrieres").with(ADMIN)).andReturn().getResponse().getContentAsString()).path("content");
        assertThat(arrieres.get(0).path("miseEnDemeureId").asText()).isEqualTo(med.path("id").asText());
        assertThat(arrieres.get(0).path("peutMiseEnDemeure").asBoolean()).isFalse();
        // le locataire en est informé (canal SMS ici)
        await().atMost(Duration.ofSeconds(15)).untilAsserted(() -> assertThat(registre.envois().stream()
                .anyMatch(x -> x.envoi().corps().contains("mise en demeure"))).isTrue());
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() ->
                assertThat(jdbc.queryForObject("SELECT count(*) FROM audit_log WHERE action = 'RELANCE.MISE_EN_DEMEURE_EMISE' AND date >= ?", Long.class, debutTest)).isEqualTo(1));
    }

    @Test
    void miseEnDemeureRefuseSiTropTotOuSoldee() throws Exception {
        UUID loc = locataire(null);
        UUID recent = bail(loc, "M2", aujourdhui.minusDays(10));
        mvc.perform(post("/api/v1/mises-en-demeure").with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("echeanceId", premiereEcheance(recent).toString())))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("RELANCE_MED_TROP_TOT"));
        UUID loc2 = locataire(null);
        UUID ancien = bail(loc2, "M3", aujourdhui.minusDays(40));
        UUID e = premiereEcheance(ancien);
        mvc.perform(post("/api/v1/paiements").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("bailId", ancien.toString(), "montant", 160_000, "mode", "VIREMENT", "reference", "V4"))).andExpect(status().isCreated());
        mvc.perform(post("/api/v1/mises-en-demeure").with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("echeanceId", e.toString())))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("RELANCE_ECHEANCE_SOLDEE"));
        assertThat(jdbc.queryForObject("SELECT count(*) FROM rel_mise_en_demeure", Long.class)).isZero();
        assertThat(jdbc.queryForObject("SELECT count(*) FROM fichier WHERE module_proprietaire = 'relance'", Long.class)).isZero();
    }

    // ================================================================== droits

    @Test
    void droits() throws Exception {
        UUID loc = locataire(null);
        UUID e = premiereEcheance(bail(loc, "D1", aujourdhui.minusDays(40)));
        for (RequestPostProcessor interdit : new RequestPostProcessor[] {jwtRole(Role.LOCATAIRE), jwtRole(Role.PROPRIETAIRE_TIERS), jwtRole(Role.PRESTATAIRE)}) {
            mvc.perform(get("/api/v1/arrieres").with(interdit)).andExpect(status().isForbidden()).andExpect(jsonPath("$.code").value("ACCES_REFUSE"));
            mvc.perform(get("/api/v1/relances").with(interdit)).andExpect(status().isForbidden());
            mvc.perform(get("/api/v1/relances/parametres").with(interdit)).andExpect(status().isForbidden());
            mvc.perform(post("/api/v1/relances").with(interdit).contentType(MediaType.APPLICATION_JSON).content(json("echeanceId", e.toString()))).andExpect(status().isForbidden());
            mvc.perform(post("/api/v1/mises-en-demeure").with(interdit).contentType(MediaType.APPLICATION_JSON).content(json("echeanceId", e.toString()))).andExpect(status().isForbidden());
        }
        mvc.perform(get("/api/v1/mises-en-demeure").with(jwtRole(Role.PROPRIETAIRE_TIERS))).andExpect(status().isForbidden());
        mvc.perform(put("/api/v1/relances/parametres").with(GESTIONNAIRE).contentType(MediaType.APPLICATION_JSON)
                .content(json("actif", true, "niveaux", List.of(3), "delaiMiseEnDemeureJours", 8))).andExpect(status().isForbidden());
        mvc.perform(post("/api/v1/relances/executer").with(GESTIONNAIRE)).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/arrieres")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/v1/arrieres").with(GESTIONNAIRE)).andExpect(status().isOk());
    }
}
