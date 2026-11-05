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
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
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

/** Reversements : calcul (encaissé − honoraires − dépenses), cycle CALCULE → VALIDE → PAYE, relevé PDF, dépenses, droits. */
@IntegrationTest
@TestPropertySource(properties = "immogest.finance.horizon-jours=45")
class ReversementTest {

    private static final RequestPostProcessor ADMIN = jwtRole(Role.ADMIN, UUID.fromString("00000000-0000-0000-0000-0000000000a1"));
    private static final RequestPostProcessor GESTIONNAIRE = jwtRole(Role.GESTIONNAIRE);
    private static final AtomicLong TEL = new AtomicLong(620_000_000L);

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    JdbcTemplate jdbc;
    @Autowired
    UtilisateurService utilisateurs;

    LocalDate aujourdhui;
    LocalDate moisPrecedent;
    UUID proprietaireA;
    UUID proprietaireB;
    UUID immeubleA;
    UUID immeubleB;
    UUID immeubleDirect;
    UUID bailA;
    UUID bailB;
    UUID compteA;

    @BeforeEach
    void preparer() throws Exception {
        aujourdhui = LocalDate.now(ZoneId.of("Africa/Douala"));
        moisPrecedent = aujourdhui.withDayOfMonth(1).minusMonths(1);
        try {
            await().atMost(Duration.ofSeconds(20)).until(() -> jdbc.queryForObject("SELECT count(*) FROM event_publication WHERE completion_date IS NULL", Long.class) == 0);
        } catch (org.awaitility.core.ConditionTimeoutException ignore) {
            // publications en échec : sans effet ici
        }
        jdbc.execute("TRUNCATE fin_reversement, fin_depense, fin_compteur_numero, rel_relance, rel_mise_en_demeure, notif_message, fin_document, fin_imputation, fin_paiement, fin_echeance, bail_document, bail_garant, bail, patrimoine_photo, compteur, lot_equipement, lot, immeuble, garant, tiers_piece, mandat, proprietaire_tiers, locataire CASCADE");
        compteA = utilisateurs.creer(new UtilisateurService.Saisie("Compte A", "+237" + TEL.incrementAndGet(), null, Role.PROPRIETAIRE_TIERS, true, false), "Motdepasse123").id();
        proprietaireA = proprietaire("Mme Ngono", 1000, compteA);
        proprietaireB = proprietaire("M. Fotso", 500, null);
        immeubleA = immeuble("Résidence A", proprietaireA);
        immeubleB = immeuble("Résidence B", proprietaireB);
        immeubleDirect = immeuble("Immeuble direct", null);
        bailA = bail(lot(immeubleA, "A1"), 150_000, 50_000);
        bailB = bail(lot(immeubleB, "B1"), 100_000, 0);
        bail(lot(immeubleDirect, "D1"), 90_000, 0);
    }

    private UUID id(ResultActions r) throws Exception {
        return UUID.fromString(mapper.readTree(r.andReturn().getResponse().getContentAsString()).path("id").asText());
    }

    private UUID proprietaire(String nom, int tauxBp, UUID compte) throws Exception {
        UUID p = id(mvc.perform(post("/api/v1/proprietaires-tiers").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("nom", nom, "telephone", "+237" + TEL.incrementAndGet(), "utilisateurId", compte == null ? null : compte.toString()))).andExpect(status().isCreated()));
        mvc.perform(post("/api/v1/proprietaires-tiers/{id}/mandats", p).with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("tauxHonorairesBp", tauxBp, "dateDebut", "2020-01-01"))).andExpect(status().isCreated());
        return p;
    }

    private UUID immeuble(String nom, UUID proprietaire) throws Exception {
        return id(mvc.perform(post("/api/v1/immeubles").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("nom", nom, "adresse", "1 rue", "ville", "Douala", "proprietaireTiersId", proprietaire == null ? null : proprietaire.toString()))));
    }

    private UUID lot(UUID immeuble, String ref) throws Exception {
        return id(mvc.perform(post("/api/v1/lots").with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("immeubleId", immeuble.toString(), "reference", ref, "type", "STUDIO"))));
    }

    private UUID bail(UUID lot, long loyer, long charges) throws Exception {
        UUID loc = id(mvc.perform(post("/api/v1/locataires").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("nom", "Locataire " + TEL.incrementAndGet(), "telephone", "+237" + TEL.incrementAndGet()))).andExpect(status().isCreated()));
        UUID b = id(mvc.perform(post("/api/v1/baux").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("lotId", lot.toString(), "locataireId", loc.toString(), "dateDebut", aujourdhui.minusDays(70).toString(), "loyer", loyer, "charges", charges,
                        "periodicite", "MENSUELLE", "jourEcheance", 1, "moisAvance", 0))).andExpect(status().isCreated()));
        mvc.perform(post("/api/v1/baux/{id}/activer", b).with(ADMIN)).andExpect(status().isOk());
        await().atMost(Duration.ofSeconds(15)).until(() -> jdbc.queryForObject("SELECT count(*) FROM fin_echeance WHERE bail_id = ?", Long.class, b) >= 3);
        return b;
    }

    private UUID payer(UUID bail, long montant) throws Exception {
        return id(mvc.perform(post("/api/v1/paiements").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("bailId", bail.toString(), "montant", montant, "mode", "VIREMENT", "reference", "V" + System.nanoTime()))).andExpect(status().isCreated()));
    }

    /** Date d'imputation reculée (le trigger d'immutabilité est suspendu le temps de l'opération de test). */
    private void imputerLe(UUID bail, LocalDate jour) {
        jdbc.execute("ALTER TABLE fin_imputation DISABLE TRIGGER fin_imputation_immuable");
        try {
            jdbc.update("UPDATE fin_imputation SET created_at = ?::timestamptz WHERE echeance_id IN (SELECT id FROM fin_echeance WHERE bail_id = ?)",
                    jour + "T12:00:00+01:00", bail);
        } finally {
            jdbc.execute("ALTER TABLE fin_imputation ENABLE TRIGGER fin_imputation_immuable");
        }
    }

    private LocalDate milieuDuMoisPrecedent() {
        return moisPrecedent.withDayOfMonth(15);
    }

    private String mois(LocalDate d) {
        return d.format(DateTimeFormatter.ofPattern("yyyy-MM"));
    }

    private JsonNode calculer(LocalDate mois) throws Exception {
        return mapper.readTree(mvc.perform(post("/api/v1/reversements/calculer").with(GESTIONNAIRE).contentType(MediaType.APPLICATION_JSON).content(json("mois", mois(mois))))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
    }

    private JsonNode pour(JsonNode liste, UUID proprietaire) {
        for (JsonNode r : liste) {
            if (r.path("proprietaireId").asText().equals(proprietaire.toString())) {
                return r;
            }
        }
        return null;
    }

    private void donneesDuMoisPrecedent() throws Exception {
        payer(bailA, 400_000); // 2 échéances de 200 000 (150 000 de loyer + 50 000 de charges)
        payer(bailB, 100_000);
        imputerLe(bailA, milieuDuMoisPrecedent());
        imputerLe(bailB, milieuDuMoisPrecedent());
        mvc.perform(post("/api/v1/depenses").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("immeubleId", immeubleA.toString(), "dateDepense", milieuDuMoisPrecedent().toString(), "nature", "Plomberie", "montant", 50_000, "reference", "FAC-12")))
                .andExpect(status().isCreated());
    }

    // ================================================================== calcul

    @Test
    void calculEncaisseMoinsHonorairesMoinsDepenses() throws Exception {
        donneesDuMoisPrecedent();
        JsonNode liste = calculer(moisPrecedent);
        assertThat(liste).hasSize(2); // l'immeuble géré en direct n'a aucun reversement
        JsonNode a = pour(liste, proprietaireA);
        assertThat(a.path("statut").asText()).isEqualTo("CALCULE");
        assertThat(a.path("loyersEncaisses").asLong()).isEqualTo(300_000);
        assertThat(a.path("chargesEncaissees").asLong()).isEqualTo(100_000);
        assertThat(a.path("brut").asLong()).isEqualTo(400_000);
        assertThat(a.path("honoraires").asLong()).isEqualTo(30_000); // 10 % des seuls loyers
        assertThat(a.path("tauxMoyenBp").asInt()).isEqualTo(1000);
        assertThat(a.path("depenses").asLong()).isEqualTo(50_000);
        assertThat(a.path("net").asLong()).isEqualTo(320_000);
        assertThat(a.path("proprietaireNom").asText()).isEqualTo("Mme Ngono");
        JsonNode b = pour(liste, proprietaireB);
        assertThat(b.path("honoraires").asLong()).isEqualTo(5_000); // taux de 5 % propre à son mandat
        assertThat(b.path("net").asLong()).isEqualTo(95_000);
        assertThat(b.path("depenses").asLong()).isZero();
    }

    @Test
    void lesEncaissementsSontRattachesAuMoisDeLImputation() throws Exception {
        donneesDuMoisPrecedent();
        payer(bailA, 200_000); // ce mois-ci
        JsonNode courant = calculer(aujourdhui);
        JsonNode a = pour(courant, proprietaireA);
        assertThat(a.path("brut").asLong()).isEqualTo(200_000);
        assertThat(a.path("depenses").asLong()).isZero();
        assertThat(pour(calculer(moisPrecedent), proprietaireA).path("brut").asLong()).isEqualTo(400_000); // le mois précédent est inchangé
    }

    @Test
    void contrePassationRetireLeMontantDuMoisOuElleALieuSansToucherLesMoisClos() throws Exception {
        donneesDuMoisPrecedent();
        UUID courant = payer(bailA, 200_000);
        assertThat(pour(calculer(aujourdhui), proprietaireA).path("brut").asLong()).isEqualTo(200_000);
        mvc.perform(post("/api/v1/paiements/{id}/contre-passation", courant).with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("motif", "Erreur")))
                .andExpect(status().isCreated());
        JsonNode apres = pour(calculer(aujourdhui), proprietaireA);
        assertThat(apres.path("brut").asLong()).isZero();
        assertThat(apres.path("net").asLong()).isZero();
        assertThat(pour(calculer(moisPrecedent), proprietaireA).path("brut").asLong()).isEqualTo(400_000);
    }

    @Test
    void calculIdempotentEtMoisFuturRefuse() throws Exception {
        donneesDuMoisPrecedent();
        calculer(moisPrecedent);
        calculer(moisPrecedent);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM fin_reversement WHERE periode = ?", Long.class, moisPrecedent)).isEqualTo(2);
        mvc.perform(post("/api/v1/reversements/calculer").with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("mois", mois(aujourdhui.plusMonths(1)))))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("REVERSEMENT_MOIS_FUTUR"));
        mvc.perform(post("/api/v1/reversements/calculer").with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("mois", "10/2026")))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("MOIS_INVALIDE"));
    }

    @Test
    void arrondiSymetriqueEtProrata() {
        assertThat(ReversementService.arrondi(5, 2)).isEqualTo(3);
        assertThat(ReversementService.arrondi(-5, 2)).isEqualTo(-3);
        assertThat(ReversementService.arrondi(1, 3)).isEqualTo(0);
        assertThat(ReversementService.arrondi(2, 3)).isEqualTo(1);
        assertThat(ReversementService.arrondi(-2, 3)).isEqualTo(-1);
    }

    // ================================================================== cycle de vie

    @Test
    void validerFigeLeReversementEtProduitLeReleveNumerote() throws Exception {
        donneesDuMoisPrecedent();
        JsonNode liste = calculer(moisPrecedent);
        UUID a = UUID.fromString(pour(liste, proprietaireA).path("id").asText());
        UUID b = UUID.fromString(pour(liste, proprietaireB).path("id").asText());

        JsonNode valide = mapper.readTree(mvc.perform(post("/api/v1/reversements/{id}/valider", a).with(GESTIONNAIRE)).andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("VALIDE")).andExpect(jsonPath("$.numero").value("REV-%d-000001".formatted(aujourdhui.getYear())))
                .andReturn().getResponse().getContentAsString());
        mvc.perform(post("/api/v1/reversements/{id}/valider", b).with(GESTIONNAIRE)).andExpect(jsonPath("$.numero").value("REV-%d-000002".formatted(aujourdhui.getYear())));
        mvc.perform(post("/api/v1/reversements/{id}/valider", a).with(GESTIONNAIRE)).andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("REVERSEMENT_DEJA_VALIDE"));

        byte[] contenu = mvc.perform(get("/api/v1/fichiers/{id}", valide.path("fichierId").asText()).with(GESTIONNAIRE)).andExpect(status().isOk()).andReturn().getResponse().getContentAsByteArray();
        try (PDDocument d = PDDocument.load(contenu)) {
            String t = new PDFTextStripper().getText(d).replace(' ', ' ').replaceAll("\\s+", " ");
            assertThat(t).contains("RELEVÉ DE GÉRANCE").contains(valide.path("numero").asText()).contains("Mme Ngono").contains("Résidence A")
                    .contains("Plomberie").contains("300 000 FCFA").contains("100 000 FCFA").contains("400 000 FCFA").contains("- 30 000 FCFA")
                    .contains("- 50 000 FCFA").contains("NET À REVERSER").contains("320 000 FCFA").contains("10,00");
            assertThat(t).doesNotContain("PROJET");
        }

        // figé : le recalcul ne le touche plus, la base le refuse, les dépenses du mois sont closes
        payer(bailA, 200_000);
        imputerLe(bailA, milieuDuMoisPrecedent().plusDays(1));
        JsonNode apres = pour(calculer(moisPrecedent), proprietaireA);
        assertThat(apres.path("net").asLong()).isEqualTo(320_000);
        assertThat(apres.path("statut").asText()).isEqualTo("VALIDE");
        assertThatThrownBy(() -> jdbc.update("UPDATE fin_reversement SET net = 1, brut = 1, loyers_encaisses = 1, charges_encaissees = 0, honoraires = 0, depenses = 0 WHERE id = ?", a))
                .isInstanceOf(DataAccessException.class);
        assertThatThrownBy(() -> jdbc.update("DELETE FROM fin_reversement WHERE id = ?", a)).isInstanceOf(DataAccessException.class);
        mvc.perform(post("/api/v1/depenses").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(json("immeubleId", immeubleA.toString(), "dateDepense", milieuDuMoisPrecedent().toString(), "nature", "Tardive", "montant", 1_000)))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("REVERSEMENT_MOIS_CLOS"));
        UUID depense = jdbc.queryForObject("SELECT id FROM fin_depense LIMIT 1", UUID.class);
        mvc.perform(post("/api/v1/depenses/{id}/annuler", depense).with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("motif", "Erreur")))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("REVERSEMENT_MOIS_CLOS"));
    }

    @Test
    void moisEnCoursNonValidable() throws Exception {
        payer(bailA, 200_000);
        UUID id = UUID.fromString(pour(calculer(aujourdhui), proprietaireA).path("id").asText());
        mvc.perform(post("/api/v1/reversements/{id}/valider", id).with(ADMIN)).andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("REVERSEMENT_MOIS_EN_COURS"));
        mvc.perform(get("/api/v1/reversements/{id}/apercu", id).with(ADMIN)).andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_PDF));
    }

    @Test
    void apercuProvisoireMarqueProjet() throws Exception {
        donneesDuMoisPrecedent();
        UUID a = UUID.fromString(pour(calculer(moisPrecedent), proprietaireA).path("id").asText());
        byte[] pdf = mvc.perform(get("/api/v1/reversements/{id}/apercu", a).with(GESTIONNAIRE)).andExpect(status().isOk()).andReturn().getResponse().getContentAsByteArray();
        try (PDDocument d = PDDocument.load(pdf)) {
            assertThat(new PDFTextStripper().getText(d)).contains("PROJET").contains("RELEVÉ DE GÉRANCE");
        }
    }

    @Test
    void validationRefuseeSansMandatPourLaDateDEncaissement() throws Exception {
        // le mandat de B ne commence que ce mois-ci : un encaissement du mois précédent n'a pas de taux
        UUID sansMandat = proprietaire("Mme Sans", 1000, null);
        jdbc.update("UPDATE mandat SET date_debut = ? WHERE proprietaire_id = ?", aujourdhui.withDayOfMonth(1), sansMandat);
        UUID imm = immeuble("Résidence C", sansMandat);
        UUID bail = bail(lot(imm, "C1"), 100_000, 0);
        payer(bail, 100_000);
        imputerLe(bail, milieuDuMoisPrecedent());
        JsonNode c = pour(calculer(moisPrecedent), sansMandat);
        assertThat(c.path("sansMandat").asBoolean()).isTrue();
        mvc.perform(post("/api/v1/reversements/{id}/valider", c.path("id").asText()).with(ADMIN)).andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("REVERSEMENT_SANS_MANDAT"));
    }

    @Test
    void payerExigeLaValidationEtUneReference() throws Exception {
        donneesDuMoisPrecedent();
        UUID a = UUID.fromString(pour(calculer(moisPrecedent), proprietaireA).path("id").asText());
        mvc.perform(post("/api/v1/reversements/{id}/payer", a).with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("mode", "VIREMENT", "reference", "V1")))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("REVERSEMENT_NON_PAYABLE"));
        mvc.perform(post("/api/v1/reversements/{id}/valider", a).with(ADMIN)).andExpect(status().isOk());
        mvc.perform(post("/api/v1/reversements/{id}/payer", a).with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("mode", "VIREMENT")))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("REVERSEMENT_REFERENCE_REQUISE"));
        mvc.perform(post("/api/v1/reversements/{id}/payer", a).with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("mode", "TROC", "reference", "x")))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("REVERSEMENT_MODE_INVALIDE"));
        mvc.perform(post("/api/v1/reversements/{id}/payer", a).with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("mode", "VIREMENT", "reference", "V1", "date", aujourdhui.plusDays(1).toString()))).andExpect(status().isBadRequest());
        mvc.perform(post("/api/v1/reversements/{id}/payer", a).with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("mode", "VIREMENT", "reference", "VIR-889")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.statut").value("PAYE")).andExpect(jsonPath("$.referencePaiement").value("VIR-889"));
        mvc.perform(post("/api/v1/reversements/{id}/payer", a).with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("mode", "ESPECES")))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("REVERSEMENT_NON_PAYABLE"));
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> assertThat(jdbc.queryForObject(
                "SELECT count(*) FROM audit_log WHERE action IN ('FINANCE.REVERSEMENT_VALIDE', 'FINANCE.REVERSEMENT_PAYE')", Long.class)).isEqualTo(2));
    }

    // ================================================================== dépenses

    @Test
    void validationsDesDepenses() throws Exception {
        UUID lotB = jdbc.queryForObject("SELECT id FROM lot WHERE reference = 'B1'", UUID.class);
        Map<String, Object> base = Map.of("immeubleId", immeubleA.toString(), "dateDepense", aujourdhui.toString(), "nature", "Gardiennage", "montant", 20_000);
        mvc.perform(post("/api/v1/depenses").with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("immeubleId", immeubleDirect.toString(), "dateDepense", aujourdhui.toString(), "nature", "x", "montant", 1)))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("DEPENSE_SANS_PROPRIETAIRE"));
        mvc.perform(post("/api/v1/depenses").with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("immeubleId", immeubleA.toString(), "lotId", lotB.toString(), "dateDepense", aujourdhui.toString(), "nature", "x", "montant", 1)))
                .andExpect(status().isUnprocessableEntity()).andExpect(jsonPath("$.code").value("DEPENSE_LOT_INVALIDE"));
        mvc.perform(post("/api/v1/depenses").with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("immeubleId", immeubleA.toString(), "dateDepense", aujourdhui.plusDays(1).toString(), "nature", "x", "montant", 1)))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("DEPENSE_DATE_FUTURE"));
        mvc.perform(post("/api/v1/depenses").with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("immeubleId", immeubleA.toString(), "dateDepense", aujourdhui.toString(), "nature", "x", "montant", 0)))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/v1/depenses").with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("immeubleId", UUID.randomUUID().toString(), "dateDepense", aujourdhui.toString(), "nature", "x", "montant", 1)))
                .andExpect(status().isNotFound());

        UUID d = id(mvc.perform(post("/api/v1/depenses").with(GESTIONNAIRE).contentType(MediaType.APPLICATION_JSON).content(json(base.keySet().stream().flatMap(k -> java.util.stream.Stream.of(k, base.get(k))).toArray())))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.proprietaireId").value(proprietaireA.toString())).andExpect(jsonPath("$.immeubleNom").value("Résidence A")));
        assertThat(pour(calculer(aujourdhui), proprietaireA).path("depenses").asLong()).isEqualTo(20_000);
        mvc.perform(post("/api/v1/depenses/{id}/annuler", d).with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("motif", " "))).andExpect(status().isBadRequest());
        mvc.perform(post("/api/v1/depenses/{id}/annuler", d).with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("motif", "Doublon")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.statut").value("ANNULEE"));
        mvc.perform(post("/api/v1/depenses/{id}/annuler", d).with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("motif", "Encore")))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("DEPENSE_DEJA_ANNULEE"));
        assertThat(pour(calculer(aujourdhui), proprietaireA).path("depenses").asLong()).isZero(); // annulée : retirée du calcul, mais conservée
        assertThat(jdbc.queryForObject("SELECT count(*) FROM fin_depense", Long.class)).isEqualTo(1);
        mvc.perform(get("/api/v1/depenses").param("mois", mois(aujourdhui)).param("immeubleId", immeubleA.toString()).with(GESTIONNAIRE))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(1));
    }

    // ================================================================== droits

    @Test
    void leProprietaireNeVoitQueSesReversementsValides() throws Exception {
        donneesDuMoisPrecedent();
        JsonNode liste = calculer(moisPrecedent);
        UUID a = UUID.fromString(pour(liste, proprietaireA).path("id").asText());
        UUID b = UUID.fromString(pour(liste, proprietaireB).path("id").asText());
        RequestPostProcessor moi = jwtRole(Role.PROPRIETAIRE_TIERS, compteA);

        // provisoire : invisible
        mvc.perform(get("/api/v1/reversements").with(moi)).andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(0));
        mvc.perform(get("/api/v1/reversements/{id}", a).with(moi)).andExpect(status().isNotFound());

        JsonNode valide = mapper.readTree(mvc.perform(post("/api/v1/reversements/{id}/valider", a).with(ADMIN)).andReturn().getResponse().getContentAsString());
        mvc.perform(post("/api/v1/reversements/{id}/valider", b).with(ADMIN)).andExpect(status().isOk());
        mvc.perform(get("/api/v1/reversements").with(moi)).andExpect(jsonPath("$.totalElements").value(1)).andExpect(jsonPath("$.content[0].id").value(a.toString()));
        mvc.perform(get("/api/v1/reversements/{id}", a).with(moi)).andExpect(status().isOk());
        mvc.perform(get("/api/v1/reversements/{id}", b).with(moi)).andExpect(status().isNotFound());
        mvc.perform(get("/api/v1/reversements").param("proprietaireId", proprietaireB.toString()).with(moi)).andExpect(status().isNotFound());
        mvc.perform(get("/api/v1/fichiers/{id}", valide.path("fichierId").asText()).with(moi)).andExpect(status().isOk());
        String fichierB = jdbc.queryForObject("SELECT fichier_id::text FROM fin_reversement WHERE id = ?", String.class, b);
        mvc.perform(get("/api/v1/fichiers/{id}", fichierB).with(moi)).andExpect(status().is4xxClientError());

        // actions de gestion interdites au propriétaire
        mvc.perform(post("/api/v1/reversements/calculer").with(moi).contentType(MediaType.APPLICATION_JSON).content(json("mois", mois(moisPrecedent)))).andExpect(status().isForbidden());
        mvc.perform(post("/api/v1/reversements/{id}/payer", a).with(moi).contentType(MediaType.APPLICATION_JSON).content(json("mode", "ESPECES"))).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/reversements/{id}/apercu", a).with(moi)).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/depenses").with(moi)).andExpect(status().isForbidden());
    }

    @Test
    void droitsParRole() throws Exception {
        for (Role r : new Role[] {Role.LOCATAIRE, Role.PRESTATAIRE}) {
            RequestPostProcessor qui = jwtRole(r);
            mvc.perform(get("/api/v1/reversements").with(qui)).andExpect(status().isForbidden()).andExpect(jsonPath("$.code").value("ACCES_REFUSE"));
            mvc.perform(post("/api/v1/reversements/calculer").with(qui).contentType(MediaType.APPLICATION_JSON).content(json("mois", mois(moisPrecedent)))).andExpect(status().isForbidden());
            mvc.perform(post("/api/v1/depenses").with(qui).contentType(MediaType.APPLICATION_JSON).content(json("immeubleId", immeubleA.toString(), "dateDepense", aujourdhui.toString(), "nature", "x", "montant", 1)))
                    .andExpect(status().isForbidden());
            mvc.perform(get("/api/v1/depenses").with(qui)).andExpect(status().isForbidden());
        }
        mvc.perform(get("/api/v1/reversements")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/v1/reversements").with(GESTIONNAIRE)).andExpect(status().isOk());
    }
}
