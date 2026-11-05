package com.immogest.app.importation;

import static com.immogest.app.securite.SecuriteTestSupport.json;
import static com.immogest.app.securite.SecuriteTestSupport.jwtRole;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.immogest.app.IntegrationTest;
import com.immogest.app.securite.Role;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

/** Import Excel / CSV : modèles, prévisualisation, rapport d'erreurs ligne par ligne, import tout ou rien, reprise des soldes d'ouverture. */
@IntegrationTest
class ImportTest {

    private static final RequestPostProcessor ADMIN = jwtRole(Role.ADMIN, UUID.fromString("00000000-0000-0000-0000-0000000000a1"));
    private static final AtomicLong TEL = new AtomicLong(650_000_000L);
    private static final MediaType XLSX = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    JdbcTemplate jdbc;

    LocalDate aujourdhui;
    String immeuble;
    String tel1;
    String tel2;

    @BeforeEach
    void preparer() {
        aujourdhui = LocalDate.now(ZoneId.of("Africa/Douala"));
        try {
            await().atMost(Duration.ofSeconds(20)).until(() -> jdbc.queryForObject("SELECT count(*) FROM event_publication WHERE completion_date IS NULL AND publication_date > now() - interval '30 seconds'", Long.class) == 0);
        } catch (org.awaitility.core.ConditionTimeoutException ignore) {
            // sans effet ici
        }
        jdbc.execute("TRUNCATE imp_journal, ol_webhook_recu, ol_transaction, mnt_signalement, notif_message, fin_document, fin_compteur_numero, fin_imputation, fin_paiement, fin_echeance, bail_document, bail_garant, bail, patrimoine_photo, compteur, lot_equipement, lot, immeuble, garant, tiers_piece, locataire CASCADE");
        long n = TEL.incrementAndGet();
        immeuble = "Résidence Import " + n;
        tel1 = "+2376" + (n % 100_000_000L + 10_000_000L);
        tel2 = "+2376" + ((n + 7) % 100_000_000L + 10_000_000L);
    }

    // ------------------------------------------------------------------ construction de fichiers

    /** Classeur : onglet → lignes (la première est l'en-tête). */
    private static byte[] classeur(Map<String, List<List<Object>>> onglets) throws Exception {
        try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            for (var e : onglets.entrySet()) {
                Sheet s = wb.createSheet(e.getKey());
                int i = 0;
                for (List<Object> ligne : e.getValue()) {
                    Row r = s.createRow(i++);
                    for (int c = 0; c < ligne.size(); c++) {
                        Object v = ligne.get(c);
                        if (v == null) {
                            continue;
                        }
                        if (v instanceof Number n) {
                            r.createCell(c).setCellValue(n.doubleValue());
                        } else {
                            r.createCell(c).setCellValue(v.toString());
                        }
                    }
                }
            }
            wb.write(out);
            return out.toByteArray();
        }
    }

    private static List<List<Object>> table(Object[]... lignes) {
        List<List<Object>> r = new ArrayList<>();
        for (Object[] l : lignes) {
            r.add(java.util.Arrays.asList(l));
        }
        return r;
    }

    /** Fichier de base : 1 immeuble, 2 lots, 2 locataires, 2 baux (l'un en retard de 2 mois), 1 solde. */
    private byte[] fichierValide() throws Exception {
        LocalDate debut = aujourdhui.minusMonths(5).withDayOfMonth(1);
        Map<String, List<List<Object>>> o = new java.util.LinkedHashMap<>();
        o.put("Immeubles", table(new Object[] {"nom", "adresse", "quartier", "ville", "description"}, new Object[] {immeuble, "12 rue de la Paix", "Akwa", "Douala", "R+3"}));
        o.put("Lots", table(new Object[] {"immeuble", "reference", "type", "surface_m2", "pieces", "loyer_indicatif"},
                new Object[] {immeuble, "A1", "APPARTEMENT", "45,5", 3, 150000}, new Object[] {immeuble, "A2", "STUDIO", 20, 1, 80000}));
        o.put("Locataires", table(new Object[] {"nom", "telephone", "email", "profession", "adresse", "date_naissance", "numero_cni"},
                new Object[] {"Paul Mbarga", tel1, "paul@exemple.cm", "Enseignant", null, "1985-04-12", "123456789"}, new Object[] {"Aïcha Njoya", tel2, null, null, null, null, null}));
        o.put("Baux", table(new Object[] {"immeuble", "lot", "locataire_telephone", "date_debut", "date_fin", "loyer", "charges", "periodicite", "jour_echeance", "caution", "preavis_mois"},
                new Object[] {immeuble, "A1", tel1, debut.toString(), null, 140000, 10000, "MENSUELLE", 1, 300000, 3},
                new Object[] {immeuble, "A2", tel2, debut.toString(), null, 80000, 0, null, null, null, null}));
        o.put("Soldes", table(new Object[] {"immeuble", "lot", "solde_du", "date_reprise"}, new Object[] {immeuble, "A1", 300000, null}));
        return classeur(o);
    }

    private ResultActions envoyer(String url, byte[] contenu, String nom, String type, String empreinte, RequestPostProcessor qui) throws Exception {
        var req = multipart(url).file(new MockMultipartFile("fichier", nom, nom.endsWith(".csv") ? "text/csv" : XLSX.toString(), contenu));
        if (type != null) {
            req.param("type", type);
        }
        if (empreinte != null) {
            req.param("empreinte", empreinte);
        }
        return mvc.perform(req.with(qui));
    }

    private JsonNode previsualiser(byte[] contenu, String nom, String type) throws Exception {
        return mapper.readTree(envoyer("/api/v1/import/previsualisation", contenu, nom, type, null, ADMIN).andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
    }

    private long compte(String table) {
        return jdbc.queryForObject("SELECT count(*) FROM " + table, Long.class);
    }

    private List<String> codes(JsonNode erreurs) {
        List<String> r = new ArrayList<>();
        erreurs.forEach(e -> r.add(e.path("feuille").asText() + ":" + e.path("ligne").asInt() + ":" + e.path("code").asText()));
        return r;
    }

    // ------------------------------------------------------------------ modèles

    @Test
    void leModeleExcelContientTousLesOngletsEtLesEntetes() throws Exception {
        byte[] corps = mvc.perform(get("/api/v1/import/modele").with(ADMIN)).andExpect(status().isOk()).andExpect(result ->
                assertThat(result.getResponse().getHeader("Content-Disposition")).contains("modele-import-immogest.xlsx")).andReturn().getResponse().getContentAsByteArray();
        try (XSSFWorkbook wb = new XSSFWorkbook(new ByteArrayInputStream(corps))) {
            assertThat(wb.getSheetName(0)).isEqualTo("Instructions");
            for (TypeImport t : TypeImport.values()) {
                Sheet s = wb.getSheet(t.feuille());
                assertThat(s).as(t.feuille()).isNotNull();
                List<String> entetes = new ArrayList<>();
                s.getRow(0).forEach(c -> entetes.add(c.getStringCellValue()));
                assertThat(entetes).isEqualTo(t.colonnes().stream().map(TypeImport.Colonne::nom).toList());
            }
        }
        // le modèle vide lui-même est refusé proprement (rien à importer), pas une erreur serveur
        JsonNode p = previsualiser(corps, "modele.xlsx", null);
        assertThat(p.path("importable").asBoolean()).isFalse();
        assertThat(codes(p.path("erreurs"))).containsExactly(":0:FICHIER_VIDE");
    }

    @Test
    void leModeleCsvNeContientQueLesEntetesDUnType() throws Exception {
        String csv = new String(mvc.perform(get("/api/v1/import/modele").param("format", "csv").param("type", "LOCATAIRES").with(ADMIN)).andExpect(status().isOk()).andReturn()
                .getResponse().getContentAsByteArray(), StandardCharsets.UTF_8);
        assertThat(csv).isEqualTo("﻿nom;telephone;email;profession;adresse;date_naissance;numero_cni\n");
        mvc.perform(get("/api/v1/import/modele").param("format", "csv").with(ADMIN)).andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("IMPORT_TYPE_REQUIS"));
        mvc.perform(get("/api/v1/import/modele").param("format", "pdf").with(ADMIN)).andExpect(status().isBadRequest());
    }

    // ------------------------------------------------------------------ prévisualisation

    @Test
    void previsualisationValideNEcritRien() throws Exception {
        JsonNode p = previsualiser(fichierValide(), "import.xlsx", null);
        assertThat(p.path("importable").asBoolean()).isTrue();
        assertThat(p.path("erreurs")).isEmpty();
        assertThat(p.path("empreinte").asText()).hasSize(64);
        assertThat(p.path("feuilles").size()).isEqualTo(5);
        assertThat(p.path("apercu").path("LOTS").size()).isEqualTo(2);
        assertThat(p.path("apercu").path("LOTS").get(0).path("surface_m2").asText()).isEqualTo("45,5");
        assertThat(compte("immeuble") + compte("lot") + compte("locataire") + compte("bail") + compte("fin_echeance")).isZero();
    }

    @Test
    void toutesLesErreursSontRapporteesLigneParLigne() throws Exception {
        Map<String, List<List<Object>>> o = new java.util.LinkedHashMap<>();
        o.put("Immeubles", table(new Object[] {"nom", "adresse", "ville"}, new Object[] {immeuble, "rue", "Douala"}, new Object[] {immeuble.toUpperCase(), "rue", "Douala"}, new Object[] {null, "x", "y"}));
        o.put("Lots", table(new Object[] {"immeuble", "reference", "type"}, new Object[] {immeuble, "A1", "VILLA"}, new Object[] {"Inconnu", "B1", "STUDIO"}, new Object[] {immeuble, "A1", "STUDIO"}));
        o.put("Locataires", table(new Object[] {"nom", "telephone", "email"}, new Object[] {"X", "12", "pas-un-email"}, new Object[] {"Y", tel1, null}, new Object[] {"Z", tel1, null}));
        o.put("Baux", table(new Object[] {"immeuble", "lot", "locataire_telephone", "date_debut", "date_fin", "loyer", "charges"},
                new Object[] {immeuble, "A1", tel1, "31/02/2025", null, 100000, null},
                new Object[] {immeuble, "A1", "+237699000001", aujourdhui.minusMonths(2).toString(), null, 100000, null},
                new Object[] {immeuble, "A1", tel1, aujourdhui.minusYears(2).toString(), aujourdhui.minusYears(1).toString(), 100000, null},
                new Object[] {immeuble, "A1", tel1, aujourdhui.minusMonths(2).toString(), null, "cent mille", null}));
        o.put("Soldes", table(new Object[] {"immeuble", "lot", "solde_du"}, new Object[] {immeuble, "Z9", 1000}));
        JsonNode p = previsualiser(classeur(o), "erreurs.xlsx", null);

        assertThat(p.path("importable").asBoolean()).isFalse();
        assertThat(codes(p.path("erreurs"))).contains(
                "Immeubles:3:DOUBLON_DANS_FICHIER", "Immeubles:4:VALEUR_REQUISE",
                "Lots:2:VALEUR_INVALIDE", "Lots:3:IMMEUBLE_INCONNU", "Lots:4:DOUBLON_DANS_FICHIER",
                "Locataires:2:VALEUR_INVALIDE", "Locataires:4:DOUBLON_DANS_FICHIER",
                "Baux:2:VALEUR_INVALIDE", "Baux:3:LOCATAIRE_INCONNU", "Baux:4:BAIL_TERMINE", "Baux:5:VALEUR_INVALIDE",
                "Soldes:2:BAIL_INCONNU");
        // les colonnes fautives sont nommées
        assertThat(p.path("erreurs").findValuesAsText("colonne")).contains("date_debut", "loyer", "telephone", "type", "immeuble");
        assertThat(compte("immeuble") + compte("locataire") + compte("bail")).isZero();
    }

    @Test
    void colonneObligatoireAbsente() throws Exception {
        Map<String, List<List<Object>>> o = new java.util.LinkedHashMap<>();
        o.put("Lots", table(new Object[] {"immeuble", "reference"}, new Object[] {immeuble, "A1"}));
        JsonNode p = previsualiser(classeur(o), "x.xlsx", null);
        assertThat(codes(p.path("erreurs"))).containsExactly("Lots:1:COLONNE_MANQUANTE");
        assertThat(p.path("erreurs").get(0).path("colonne").asText()).isEqualTo("type");
    }

    @Test
    void lesEntetesSontTolerants() throws Exception {
        Map<String, List<List<Object>>> o = new java.util.LinkedHashMap<>();
        o.put("immeubles", table(new Object[] {"  Nom ", "ADRESSE", "Ville"}, new Object[] {immeuble, "rue 1", "Douala"}));
        o.put("Onglet libre ignoré", table(new Object[] {"a"}, new Object[] {"b"}));
        JsonNode p = previsualiser(classeur(o), "x.xlsx", null);
        assertThat(p.path("erreurs")).isEmpty();
        assertThat(p.path("importable").asBoolean()).isTrue();
    }

    @Test
    void soldeSuperieurAuxLoyersDusEstRefuse() throws Exception {
        Map<String, List<List<Object>>> o = new java.util.LinkedHashMap<>();
        o.put("Immeubles", table(new Object[] {"nom", "adresse", "ville"}, new Object[] {immeuble, "rue", "Douala"}));
        o.put("Lots", table(new Object[] {"immeuble", "reference", "type"}, new Object[] {immeuble, "A1", "STUDIO"}));
        o.put("Locataires", table(new Object[] {"nom", "telephone"}, new Object[] {"P", tel1}));
        o.put("Baux", table(new Object[] {"immeuble", "lot", "locataire_telephone", "date_debut", "loyer"}, new Object[] {immeuble, "A1", tel1, aujourdhui.minusMonths(2).withDayOfMonth(1).toString(), 100000}));
        o.put("Soldes", table(new Object[] {"immeuble", "lot", "solde_du"}, new Object[] {immeuble, "A1", 9_000_000}));
        assertThat(codes(previsualiser(classeur(o), "x.xlsx", null).path("erreurs"))).containsExactly("Soldes:2:SOLDE_SUPERIEUR_AU_LOYER");
    }

    @Test
    void deuxBauxQuiSeChevauchentSurLeMemeLot() throws Exception {
        Map<String, List<List<Object>>> o = new java.util.LinkedHashMap<>();
        o.put("Immeubles", table(new Object[] {"nom", "adresse", "ville"}, new Object[] {immeuble, "rue", "Douala"}));
        o.put("Lots", table(new Object[] {"immeuble", "reference", "type"}, new Object[] {immeuble, "A1", "STUDIO"}));
        o.put("Locataires", table(new Object[] {"nom", "telephone"}, new Object[] {"P", tel1}, new Object[] {"Q", tel2}));
        o.put("Baux", table(new Object[] {"immeuble", "lot", "locataire_telephone", "date_debut", "loyer"},
                new Object[] {immeuble, "A1", tel1, aujourdhui.minusMonths(3).toString(), 100000}, new Object[] {immeuble, "A1", tel2, aujourdhui.minusMonths(1).toString(), 100000}));
        assertThat(codes(previsualiser(classeur(o), "x.xlsx", null).path("erreurs"))).containsExactly("Baux:3:CHEVAUCHEMENT");
    }

    // ------------------------------------------------------------------ import

    @Test
    void importCompletAvecReprisePuisRejeuRefuse() throws Exception {
        byte[] fichier = fichierValide();
        String empreinte = previsualiser(fichier, "import.xlsx", null).path("empreinte").asText();

        JsonNode r = mapper.readTree(envoyer("/api/v1/import", fichier, "import.xlsx", null, empreinte, ADMIN).andExpect(status().isOk()).andExpect(jsonPath("$.importe").value(true))
                .andReturn().getResponse().getContentAsString());
        assertThat(r.path("crees").path("IMMEUBLES").asInt()).isEqualTo(1);
        assertThat(r.path("crees").path("LOTS").asInt()).isEqualTo(2);
        assertThat(r.path("crees").path("LOCATAIRES").asInt()).isEqualTo(2);
        assertThat(r.path("crees").path("BAUX").asInt()).isEqualTo(2);
        assertThat(r.path("crees").path("SOLDES").asInt()).isEqualTo(1);
        assertThat(r.path("echeancesReprises").asInt()).isGreaterThanOrEqualTo(8);

        assertThat(compte("immeuble")).isEqualTo(1);
        assertThat(compte("lot")).isEqualTo(2);
        assertThat(compte("bail")).isEqualTo(2);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM bail WHERE statut = 'ACTIF'", Long.class)).isEqualTo(2);
        // le numéro de CNI n'est jamais en clair
        assertThat(jdbc.queryForObject("SELECT numero_cni_chiffre FROM locataire WHERE nom = 'Paul Mbarga'", String.class)).isNotEqualTo("123456789");

        // reprise : le solde (300 000) est exactement l'impayé des échéances d'historique du bail A1, et rien n'est compté comme encaissé
        UUID a1 = jdbc.queryForObject("SELECT b.id FROM bail b JOIN lot l ON l.id = b.lot_id WHERE l.reference = 'A1'", UUID.class);
        assertThat(jdbc.queryForObject("SELECT COALESCE(sum(montant - montant_paye), 0) FROM fin_echeance WHERE bail_id = ? AND reprise", Long.class, a1)).isEqualTo(300000L);
        List<Long> impayes = jdbc.queryForList("SELECT montant - montant_paye FROM fin_echeance WHERE bail_id = ? AND reprise ORDER BY periode_debut DESC", Long.class, a1);
        assertThat(impayes.get(0)).isEqualTo(150000L);   // l'échéance la plus récente est impayée en entier
        assertThat(impayes.get(1)).isEqualTo(150000L);   // la précédente aussi
        assertThat(impayes.get(2)).isZero();             // le reste de l'historique est réglé
        assertThat(compte("fin_paiement") + compte("fin_imputation")).isZero();
        // le bail A2 n'a pas de ligne dans Soldes : à jour
        UUID a2 = jdbc.queryForObject("SELECT b.id FROM bail b JOIN lot l ON l.id = b.lot_id WHERE l.reference = 'A2'", UUID.class);
        assertThat(jdbc.queryForObject("SELECT COALESCE(sum(montant - montant_paye), 0) FROM fin_echeance WHERE bail_id = ? AND reprise", Long.class, a2)).isZero();
        // aucune quittance n'est émise pour l'historique
        // le moteur crée ensuite les échéances à venir sans toucher à l'historique
        await().atMost(Duration.ofSeconds(20)).untilAsserted(() ->
                assertThat(jdbc.queryForObject("SELECT count(*) FROM fin_echeance WHERE bail_id = ? AND NOT reprise", Long.class, a1)).isGreaterThanOrEqualTo(0));
        Thread.sleep(1500);
        assertThat(jdbc.queryForObject("SELECT COALESCE(sum(montant - montant_paye), 0) FROM fin_echeance WHERE bail_id = ? AND reprise", Long.class, a1)).isEqualTo(300000L);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM fin_document", Long.class)).isZero();

        // journal et audit
        assertThat(compte("imp_journal")).isEqualTo(1);
        await().atMost(Duration.ofSeconds(30)).untilAsserted(() -> assertThat(jdbc.queryForObject("SELECT count(*) FROM audit_log WHERE action = 'IMPORT.EFFECTUE' AND entite_id = (SELECT id FROM imp_journal)", Long.class)).as("audit : %s ; publications en attente : %s", jdbc.queryForList("SELECT DISTINCT action FROM audit_log"), jdbc.queryForList("SELECT event_type FROM event_publication WHERE completion_date IS NULL")).isEqualTo(1));
        assertThat(jdbc.queryForObject("SELECT count(*) FROM audit_log WHERE action = 'FINANCE.REPRISE_EFFECTUEE'", Long.class)).isGreaterThanOrEqualTo(0);

        // le même fichier une seconde fois : tout est refusé (DEJA_EXISTANT), aucun doublon
        JsonNode second = mapper.readTree(envoyer("/api/v1/import", fichier, "import.xlsx", null, empreinte, ADMIN).andExpect(status().isUnprocessableEntity()).andExpect(jsonPath("$.importe").value(false))
                .andReturn().getResponse().getContentAsString());
        assertThat(codes(second.path("erreurs"))).contains("Immeubles:2:DEJA_EXISTANT", "Locataires:2:DEJA_EXISTANT", "Locataires:3:DEJA_EXISTANT"); // les lots d'un immeuble refusé ne sont pas jugés à part
        assertThat(compte("immeuble") + compte("lot") + compte("locataire") + compte("bail")).isEqualTo(7);
    }

    @Test
    void unFichierDifferentDeCeluiPrevisualiseEstRefuse() throws Exception {
        byte[] fichier = fichierValide();
        String empreinte = previsualiser(fichier, "import.xlsx", null).path("empreinte").asText();
        envoyer("/api/v1/import", fichier, "import.xlsx", null, "0".repeat(64), ADMIN).andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("IMPORT_FICHIER_MODIFIE"));
        envoyer("/api/v1/import", fichier, "import.xlsx", null, null, ADMIN).andExpect(status().isBadRequest());
        assertThat(compte("immeuble")).isZero();
        assertThat(empreinte).hasSize(64);
    }

    @Test
    void toutOuRienUneErreurALEcritureAnnuleTout() throws Exception {
        // un lot déjà loué (bail actif) : un bail importé sur ce lot est refusé par le service de baux, seulement à l'écriture
        String immExistant = "Existant " + TEL.incrementAndGet();
        UUID imm = id(mvc.perform(post("/api/v1/immeubles").with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("nom", immExistant, "adresse", "1 rue", "ville", "Douala"))));
        UUID lot = id(mvc.perform(post("/api/v1/lots").with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("immeubleId", imm.toString(), "reference", "L1", "type", "STUDIO"))));
        UUID loc = id(mvc.perform(post("/api/v1/locataires").with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("nom", "Déjà là", "telephone", "+237677000111"))));
        UUID bail = id(mvc.perform(post("/api/v1/baux").with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("lotId", lot.toString(), "locataireId", loc.toString(),
                "dateDebut", aujourdhui.minusMonths(1).toString(), "loyer", 90000, "charges", 0, "periodicite", "MENSUELLE", "jourEcheance", 1, "moisAvance", 0))));
        mvc.perform(post("/api/v1/baux/{id}/activer", bail).with(ADMIN)).andExpect(status().isOk());
        long avant = compte("immeuble") + compte("lot") + compte("locataire") + compte("bail");

        Map<String, List<List<Object>>> o = new java.util.LinkedHashMap<>();
        o.put("Immeubles", table(new Object[] {"nom", "adresse", "ville"}, new Object[] {immeuble, "rue", "Douala"}));
        o.put("Lots", table(new Object[] {"immeuble", "reference", "type"}, new Object[] {immeuble, "N1", "STUDIO"}));
        o.put("Locataires", table(new Object[] {"nom", "telephone"}, new Object[] {"Nouveau", tel1}));
        o.put("Baux", table(new Object[] {"immeuble", "lot", "locataire_telephone", "date_debut", "loyer"},
                new Object[] {immeuble, "N1", tel1, aujourdhui.minusMonths(2).toString(), 70000},
                new Object[] {immExistant, "L1", tel1, aujourdhui.minusMonths(1).toString(), 90000}));
        byte[] fichier = classeur(o);
        JsonNode p = previsualiser(fichier, "x.xlsx", null);
        assertThat(p.path("importable").asBoolean()).isTrue(); // la preview ne peut pas le savoir
        JsonNode r = mapper.readTree(envoyer("/api/v1/import", fichier, "x.xlsx", null, p.path("empreinte").asText(), ADMIN).andExpect(status().isUnprocessableEntity())
                .andReturn().getResponse().getContentAsString());
        assertThat(r.path("importe").asBoolean()).isFalse();
        assertThat(r.path("erreurs").get(0).path("code").asText()).isEqualTo("BAIL_LOT_DEJA_OCCUPE");
        assertThat(r.path("erreurs").get(0).path("feuille").asText()).isEqualTo("Baux");
        assertThat(r.path("erreurs").get(0).path("ligne").asInt()).isEqualTo(3);
        // rien n'a été créé : ni l'immeuble, ni le lot, ni le locataire, ni le premier bail déjà traité
        assertThat(compte("immeuble") + compte("lot") + compte("locataire") + compte("bail")).isEqualTo(avant);
        assertThat(compte("imp_journal")).isZero();
    }

    private UUID id(ResultActions r) throws Exception {
        return UUID.fromString(mapper.readTree(r.andExpect(status().is2xxSuccessful()).andReturn().getResponse().getContentAsString()).path("id").asText());
    }

    @Test
    void lotsAjoutesAUnImmeubleExistantEtBailAUnLocataireExistant() throws Exception {
        String imm = "Base " + TEL.incrementAndGet();
        UUID i = id(mvc.perform(post("/api/v1/immeubles").with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("nom", imm, "adresse", "1 rue", "ville", "Douala"))));
        id(mvc.perform(post("/api/v1/locataires").with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("nom", "Existant", "telephone", tel1))));
        Map<String, List<List<Object>>> o = new java.util.LinkedHashMap<>();
        o.put("Lots", table(new Object[] {"immeuble", "reference", "type"}, new Object[] {imm.toUpperCase(), "Z1", "BOUTIQUE"}));
        o.put("Baux", table(new Object[] {"immeuble", "lot", "locataire_telephone", "date_debut", "loyer"}, new Object[] {imm, "Z1", tel1, aujourdhui.minusDays(10).toString(), 120000}));
        byte[] fichier = classeur(o);
        String empreinte = previsualiser(fichier, "x.xlsx", null).path("empreinte").asText();
        envoyer("/api/v1/import", fichier, "x.xlsx", null, empreinte, ADMIN).andExpect(status().isOk());
        assertThat(jdbc.queryForObject("SELECT count(*) FROM lot WHERE immeuble_id = ?", Long.class, i)).isEqualTo(1);
        assertThat(compte("immeuble")).isEqualTo(1);
        assertThat(compte("locataire")).isEqualTo(1);
        assertThat(compte("bail")).isEqualTo(1);
    }

    // ------------------------------------------------------------------ CSV et formats

    @Test
    void importCsvAccentsGuillemetsEtPointVirgule() throws Exception {
        String csv = "nom;adresse;quartier;ville;description\n\"" + immeuble + "\";\"12 rue \"\"du Centre\"\"\";Bonanjo;Douala;\"ligne 1\nligne 2\"\n";
        byte[] octets = csv.getBytes(StandardCharsets.UTF_8);
        JsonNode p = previsualiser(octets, "immeubles.csv", "IMMEUBLES");
        assertThat(p.path("erreurs")).isEmpty();
        assertThat(p.path("apercu").path("IMMEUBLES").get(0).path("adresse").asText()).isEqualTo("12 rue \"du Centre\"");
        envoyer("/api/v1/import", octets, "immeubles.csv", "IMMEUBLES", p.path("empreinte").asText(), ADMIN).andExpect(status().isOk());
        assertThat(jdbc.queryForObject("SELECT adresse FROM immeuble", String.class)).isEqualTo("12 rue \"du Centre\"");
    }

    @Test
    void csvEnWindows1252SeparateurVirguleEtBom() throws Exception {
        String csv = "Nom,Adresse,Ville\n" + immeuble + ",Rue de l'Église,Yaoundé\n";
        byte[] octets = csv.getBytes(Charset.forName("windows-1252"));
        JsonNode p = previsualiser(octets, "i.csv", "IMMEUBLES");
        assertThat(p.path("erreurs")).isEmpty();
        assertThat(p.path("apercu").path("IMMEUBLES").get(0).path("ville").asText()).isEqualTo("Yaoundé");
        byte[] avecBom = ("﻿nom;adresse;ville\n" + immeuble + ";rue;Douala\n").getBytes(StandardCharsets.UTF_8);
        assertThat(previsualiser(avecBom, "i.csv", "IMMEUBLES").path("erreurs")).isEmpty();
    }

    @Test
    void csvSansTypeEtFichiersNonSupportes() throws Exception {
        envoyer("/api/v1/import/previsualisation", "nom;ville\nx;y\n".getBytes(), "a.csv", null, null, ADMIN).andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("IMPORT_TYPE_REQUIS"));
        envoyer("/api/v1/import/previsualisation", new byte[] {(byte) 0xD0, (byte) 0xCF, 0x11, (byte) 0xE0, 1, 2}, "a.xls", null, null, ADMIN).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("IMPORT_FORMAT_NON_SUPPORTE"));
        envoyer("/api/v1/import/previsualisation", new byte[] {1, 0, 2, 0, 3}, "a.bin", "LOTS", null, ADMIN).andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("IMPORT_FORMAT_NON_SUPPORTE"));
        envoyer("/api/v1/import/previsualisation", "PK pas un vrai zip".getBytes(), "a.xlsx", null, null, ADMIN).andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("IMPORT_FICHIER_ILLISIBLE"));
        envoyer("/api/v1/import/previsualisation", new byte[0], "a.csv", "LOTS", null, ADMIN).andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("IMPORT_FICHIER_VIDE"));
    }

    @Test
    void tropDeLignesEstRefuse() throws Exception {
        StringBuilder sb = new StringBuilder("nom;adresse;ville\n");
        for (int i = 0; i < LecteurFichier.LIGNES_MAX + 1; i++) {
            sb.append("Imm ").append(i).append(";rue;Douala\n");
        }
        envoyer("/api/v1/import/previsualisation", sb.toString().getBytes(StandardCharsets.UTF_8), "i.csv", "IMMEUBLES", null, ADMIN).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("IMPORT_TROP_DE_LIGNES"));
    }

    @Test
    void cellulesNumeriquesEtDatesExcelSontLuesCommeDesValeurs() throws Exception {
        try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet i = wb.createSheet("Immeubles");
            i.createRow(0).createCell(0).setCellValue("nom");
            i.getRow(0).createCell(1).setCellValue("adresse");
            i.getRow(0).createCell(2).setCellValue("ville");
            Row r = i.createRow(1);
            r.createCell(0).setCellValue(immeuble);
            r.createCell(1).setCellValue("rue");
            r.createCell(2).setCellValue("Douala");
            Sheet l = wb.createSheet("Locataires");
            Row h = l.createRow(0);
            h.createCell(0).setCellValue("nom");
            h.createCell(1).setCellValue("telephone");
            h.createCell(2).setCellValue("date_naissance");
            Row d = l.createRow(1);
            d.createCell(0).setCellValue("Dina");
            d.createCell(1).setCellValue(tel1);
            var cellule = d.createCell(2);
            cellule.setCellValue(java.sql.Date.valueOf("1990-06-15"));
            var style = wb.createCellStyle();
            style.setDataFormat(wb.getCreationHelper().createDataFormat().getFormat("dd/mm/yyyy"));
            cellule.setCellStyle(style);
            Sheet b = wb.createSheet("Lots");
            Row hb = b.createRow(0);
            hb.createCell(0).setCellValue("immeuble");
            hb.createCell(1).setCellValue("reference");
            hb.createCell(2).setCellValue("type");
            hb.createCell(3).setCellValue("loyer_indicatif");
            Row lb = b.createRow(1);
            lb.createCell(0).setCellValue(immeuble);
            lb.createCell(1).setCellValue("A1");
            lb.createCell(2).setCellValue("STUDIO");
            lb.createCell(3).setCellValue(150000d);
            wb.write(out);
            JsonNode p = previsualiser(out.toByteArray(), "n.xlsx", null);
            assertThat(p.path("erreurs")).isEmpty();
            assertThat(p.path("apercu").path("LOCATAIRES").get(0).path("date_naissance").asText()).isEqualTo("1990-06-15");
            assertThat(p.path("apercu").path("LOTS").get(0).path("loyer_indicatif").asText()).isEqualTo("150000");
        }
    }

    // ------------------------------------------------------------------ droits

    @Test
    void reserveALAdministrateur() throws Exception {
        byte[] fichier = fichierValide();
        for (RequestPostProcessor interdit : new RequestPostProcessor[] {jwtRole(Role.GESTIONNAIRE), jwtRole(Role.LOCATAIRE), jwtRole(Role.PROPRIETAIRE_TIERS), jwtRole(Role.PRESTATAIRE)}) {
            mvc.perform(get("/api/v1/import/modele").with(interdit)).andExpect(status().isForbidden());
            envoyer("/api/v1/import/previsualisation", fichier, "x.xlsx", null, null, interdit).andExpect(status().isForbidden());
            envoyer("/api/v1/import", fichier, "x.xlsx", null, "a".repeat(64), interdit).andExpect(status().isForbidden());
        }
        mvc.perform(get("/api/v1/import/modele")).andExpect(status().isUnauthorized());
        assertThat(compte("immeuble")).isZero();
    }

    @Test
    void leJournalDesImportsEstEnEcritureSeule() throws Exception {
        byte[] fichier = fichierValide();
        envoyer("/api/v1/import", fichier, "x.xlsx", null, previsualiser(fichier, "x.xlsx", null).path("empreinte").asText(), ADMIN).andExpect(status().isOk());
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> jdbc.update("UPDATE imp_journal SET par = 'x'")).hasMessageContaining("écriture seule");
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> jdbc.update("DELETE FROM imp_journal")).hasMessageContaining("écriture seule");
    }
}
