package com.immogest.app.paiementenligne;

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
import com.immogest.app.paiementenligne.infra.AgregateurSimule;
import com.immogest.app.paiementenligne.infra.RegistreSimule;
import com.immogest.app.securite.Role;
import com.immogest.app.securite.UtilisateurService;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

/** Paiement Mobile Money avec agrégateur simulé : succès, échec, rejeu de webhook, signature invalide, vérification de statut, droits. */
@IntegrationTest
class PaiementEnLigneTest {

    private static final RequestPostProcessor ADMIN = jwtRole(Role.ADMIN, UUID.fromString("00000000-0000-0000-0000-0000000000a1"));
    private static final RequestPostProcessor GESTIONNAIRE = jwtRole(Role.GESTIONNAIRE);
    private static final AtomicLong TEL = new AtomicLong(600_000_000L);
    private static final String SECRET = "secret-webhook-de-test";
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
    RegistreSimule registre;
    @Autowired
    PaiementEnLigneService service;

    LocalDate aujourdhui;
    UUID immeuble;
    UUID compte;
    UUID locataire;
    UUID bail;
    RequestPostProcessor moi;

    /** Début du test : on ne compte que les lignes d'audit créées depuis (le journal est immuable, jamais purgé entre deux tests : un compte global dépend de l'ordre d'exécution). */
    java.time.OffsetDateTime debutTest;

    @BeforeEach
    void preparer() throws Exception {
        aujourdhui = LocalDate.now(ZoneId.of("Africa/Douala"));
        try {
            await().atMost(Duration.ofSeconds(20)).until(() -> jdbc.queryForObject("SELECT count(*) FROM event_publication WHERE completion_date IS NULL AND publication_date > now() - interval '30 seconds'", Long.class) == 0);
        } catch (org.awaitility.core.ConditionTimeoutException ignore) {
            // sans effet ici
        }
        jdbc.execute("TRUNCATE ol_webhook_recu, ol_transaction, ol_fournisseur, mnt_signalement, notif_message, fin_document, fin_compteur_numero, fin_imputation, fin_paiement, fin_echeance, bail_document, bail_garant, bail, patrimoine_photo, compteur, lot_equipement, lot, immeuble, garant, tiers_piece, locataire CASCADE");
        debutTest = java.time.OffsetDateTime.now();
        registre.reinitialiser();
        immeuble = id(mvc.perform(post("/api/v1/immeubles").with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("nom", "Résidence MoMo", "adresse", "1 rue", "ville", "Douala"))));
        compte = utilisateurs.creer(new UtilisateurService.Saisie("Compte", "+237" + TEL.incrementAndGet(), null, Role.LOCATAIRE, true, false), "Motdepasse123").id();
        locataire = locataire(compte);
        bail = bail(locataire, "M1");
        moi = jwtRole(Role.LOCATAIRE, compte);
        activerFournisseur();
    }

    private UUID id(ResultActions r) throws Exception {
        return UUID.fromString(mapper.readTree(r.andReturn().getResponse().getContentAsString()).path("id").asText());
    }

    private UUID locataire(UUID cpt) throws Exception {
        return id(mvc.perform(post("/api/v1/locataires").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("nom", "Paul Momo", "telephone", "+237" + TEL.incrementAndGet(), "utilisateurId", cpt == null ? null : cpt.toString()))).andExpect(status().isCreated()));
    }

    private UUID bail(UUID loc, String ref) throws Exception {
        UUID lot = id(mvc.perform(post("/api/v1/lots").with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("immeubleId", immeuble.toString(), "reference", ref, "type", "STUDIO"))));
        UUID b = id(mvc.perform(post("/api/v1/baux").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("lotId", lot.toString(), "locataireId", loc.toString(), "dateDebut", aujourdhui.minusDays(40).toString(), "loyer", 150_000, "charges", 10_000,
                        "periodicite", "MENSUELLE", "jourEcheance", 1, "moisAvance", 0))).andExpect(status().isCreated()));
        mvc.perform(post("/api/v1/baux/{id}/activer", b).with(ADMIN)).andExpect(status().isOk());
        await().atMost(Duration.ofSeconds(15)).until(() -> jdbc.queryForObject("SELECT count(*) FROM fin_echeance WHERE bail_id = ?", Long.class, b) >= 2);
        return b;
    }

    private void activerFournisseur() throws Exception {
        mvc.perform(put("/api/v1/paiements-en-ligne/fournisseurs/simule").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("actif", true, "champs", Map.of("identifiantMarchand", "MARCHAND-1", "secretWebhook", SECRET)))).andExpect(status().isOk());
    }

    private ResultActions payer(RequestPostProcessor qui, String cle, Object... extra) throws Exception {
        Object[] base = {"bailId", bail.toString(), "montant", MENSUEL, "operateur", "MTN_MOMO", "telephone", "+237 6 99 11 22 33"};
        Object[] tout = base.clone();
        for (int i = 0; i < extra.length; i += 2) {
            for (int j = 0; j < tout.length; j += 2) {
                if (tout[j].equals(extra[i])) {
                    tout[j + 1] = extra[i + 1];
                }
            }
        }
        var requete = post("/api/v1/paiements-en-ligne").with(qui).contentType(MediaType.APPLICATION_JSON).content(json(tout));
        return mvc.perform(cle == null ? requete : requete.header("Idempotency-Key", cle));
    }

    private JsonNode initier(String cle) throws Exception {
        return mapper.readTree(payer(moi, cle).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString());
    }

    private ResultActions webhook(String evenement, String reference, String statut, Long montant, String secret, long horodatage) throws Exception {
        String corps = json("id", evenement, "reference", reference, "statut", statut, "montant", montant, "referenceOperateur", "OPERATEUR-" + evenement);
        return webhookBrut(corps.getBytes(StandardCharsets.UTF_8), secret, horodatage);
    }

    private ResultActions webhookBrut(byte[] corps, String secret, long horodatage) throws Exception {
        return mvc.perform(post("/api/v1/webhooks/simule").content(corps).contentType(MediaType.APPLICATION_JSON)
                .header("X-Sim-Timestamp", String.valueOf(horodatage)).header("X-Sim-Signature", AgregateurSimule.hmac(secret, String.valueOf(horodatage), corps)));
    }

    private long maintenant() {
        return Instant.now().getEpochSecond();
    }

    private long paiementsCrees() {
        return jdbc.queryForObject("SELECT count(*) FROM fin_paiement WHERE type = 'ENCAISSEMENT'", Long.class);
    }

    private String statut(String transaction) {
        return jdbc.queryForObject("SELECT statut FROM ol_transaction WHERE id = ?::uuid", String.class, transaction);
    }

    // ================================================================== configuration

    @Test
    void identifiantsMarchandsSecretsMasquesEtChiffres() throws Exception {
        jdbc.execute("TRUNCATE ol_fournisseur CASCADE");
        mvc.perform(get("/api/v1/paiements-en-ligne/fournisseurs").with(ADMIN)).andExpect(status().isOk()).andExpect(jsonPath("$[0].code").value("simule"))
                .andExpect(jsonPath("$[0].actif").value(false)).andExpect(jsonPath("$[0].configure").value(false));
        mvc.perform(put("/api/v1/paiements-en-ligne/fournisseurs/simule").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(json("actif", true, "champs", Map.of("identifiantMarchand", "M-1")))).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("PAIEMENT_EN_LIGNE_CONFIG_INCOMPLETE"));
        mvc.perform(put("/api/v1/paiements-en-ligne/fournisseurs/simule").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("actif", false, "champs", Map.of("inconnu", "x")))).andExpect(status().isBadRequest());
        mvc.perform(put("/api/v1/paiements-en-ligne/fournisseurs/autre").with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("actif", false, "champs", Map.of())))
                .andExpect(status().isNotFound());

        String reponse = mvc.perform(put("/api/v1/paiements-en-ligne/fournisseurs/simule").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(json("actif", true, "champs", Map.of("identifiantMarchand", "M-1", "secretWebhook", "SUPER-SECRET-XYZ"))))
                .andExpect(status().isOk()).andExpect(jsonPath("$.configure").value(true)).andReturn().getResponse().getContentAsString();
        assertThat(reponse).doesNotContain("SUPER-SECRET-XYZ");
        assertThat(jdbc.queryForObject("SELECT secrets FROM ol_fournisseur WHERE code = 'simule'", String.class)).isNotBlank().doesNotContain("SUPER-SECRET-XYZ");
        assertThat(jdbc.queryForObject("SELECT config FROM ol_fournisseur WHERE code = 'simule'", String.class)).doesNotContain("SUPER-SECRET-XYZ");
        // secret laissé vide : conservé
        mvc.perform(put("/api/v1/paiements-en-ligne/fournisseurs/simule").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("actif", true, "champs", Map.of("identifiantMarchand", "M-2", "secretWebhook", "")))).andExpect(jsonPath("$.configure").value(true));
        for (RequestPostProcessor interdit : new RequestPostProcessor[] {GESTIONNAIRE, moi, jwtRole(Role.PROPRIETAIRE_TIERS)}) {
            mvc.perform(get("/api/v1/paiements-en-ligne/fournisseurs").with(interdit)).andExpect(status().isForbidden());
            mvc.perform(put("/api/v1/paiements-en-ligne/fournisseurs/simule").with(interdit).contentType(MediaType.APPLICATION_JSON).content(json("actif", false, "champs", Map.of())))
                    .andExpect(status().isForbidden());
        }
    }

    // ================================================================== initiation

    @Test
    void initierNeCreeAucunPaiement() throws Exception {
        JsonNode t = initier("cle-1");
        assertThat(t.path("statut").asText()).isEqualTo("EN_ATTENTE");
        assertThat(t.path("instructions").asText()).contains("téléphone");
        assertThat(t.path("telephone").asText()).endsWith("233").doesNotContain("699"); // numéro masqué
        assertThat(t.path("montant").asLong()).isEqualTo(MENSUEL);
        assertThat(paiementsCrees()).isZero(); // règle 8 : rien tant que l'opérateur n'a pas confirmé
        assertThat(registre.demandes()).hasSize(1);
        assertThat(registre.demandes().get(0).telephone()).isEqualTo("237699112233");
        assertThat(registre.demandes().get(0).montant()).isEqualTo(MENSUEL);
        assertThat(registre.demandes().get(0).reference()).isEqualTo(t.path("id").asText());
    }

    @Test
    void validationsDeLInitiation() throws Exception {
        payer(moi, null).andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("IDEMPOTENCE_CLE_REQUISE"));
        payer(moi, "c", "montant", 0).andExpect(status().isBadRequest());
        payer(moi, "c", "montant", 20_000_001L).andExpect(status().isBadRequest());
        payer(moi, "c", "telephone", "12ab").andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("PAIEMENT_EN_LIGNE_TELEPHONE_INVALIDE"));
        payer(moi, "c", "operateur", "WAVE").andExpect(status().isBadRequest());
        payer(moi, "c", "bailId", UUID.randomUUID().toString()).andExpect(status().isNotFound());
        // le bail d'un autre locataire n'est pas payable par moi
        UUID autre = locataire(null);
        UUID baileAutre = bail(autre, "M2");
        payer(moi, "c", "bailId", baileAutre.toString()).andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value("BAIL_INTROUVABLE"));
        assertThat(registre.demandes()).isEmpty();
        assertThat(jdbc.queryForObject("SELECT count(*) FROM ol_transaction", Long.class)).isZero();
    }

    @Test
    void rejeuDeLaMemeCleNeCreeQuUneTransaction() throws Exception {
        String premier = mapper.readTree(payer(moi, "cle-x").andExpect(status().isCreated()).andReturn().getResponse().getContentAsString()).path("id").asText();
        String rejeu = mapper.readTree(payer(moi, "cle-x").andExpect(status().isOk()).andReturn().getResponse().getContentAsString()).path("id").asText();
        assertThat(rejeu).isEqualTo(premier);
        assertThat(registre.demandes()).hasSize(1); // l'agrégateur n'est appelé qu'une fois
        payer(moi, "cle-x", "montant", 5000).andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("IDEMPOTENCE_CONFLIT"));
    }

    @Test
    void pasPlusDeTroisPaiementsEnAttente() throws Exception {
        for (int i = 0; i < 3; i++) {
            initier("c" + i);
        }
        payer(moi, "c-trop").andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("PAIEMENT_EN_LIGNE_TROP_DE_TRANSACTIONS"));
    }

    @Test
    void fournisseurInactifOuAgregateurEnPanne() throws Exception {
        registre.echouerProchainesInitiations(1);
        payer(moi, "c1").andExpect(status().isBadGateway()).andExpect(jsonPath("$.code").value("AGREGATEUR_INDISPONIBLE"));
        assertThat(jdbc.queryForObject("SELECT statut FROM ol_transaction", String.class)).isEqualTo("ECHOUEE");
        payer(moi, "c2").andExpect(status().isCreated()); // la transaction échouée ne compte pas parmi les attentes

        mvc.perform(put("/api/v1/paiements-en-ligne/fournisseurs/simule").with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("actif", false, "champs", Map.of())))
                .andExpect(status().isOk());
        payer(moi, "c3").andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("PAIEMENT_EN_LIGNE_INDISPONIBLE"));
    }

    // ================================================================== confirmation par webhook

    @Test
    void webhookSigneDeSuccesCreeLePaiementEtImputeLEcheance() throws Exception {
        JsonNode t = initier("cle-1");
        String ref = registre.derniereReference();
        registre.confirmer(ref);

        webhook("evt-1", ref, "SUCCES", MENSUEL, SECRET, maintenant()).andExpect(status().isOk()).andExpect(jsonPath("$.resultat").value("TRAITE"));

        assertThat(statut(t.path("id").asText())).isEqualTo("CONFIRMEE");
        assertThat(paiementsCrees()).isEqualTo(1);
        Map<String, Object> p = jdbc.queryForMap("SELECT montant, mode, reference, bail_id FROM fin_paiement WHERE type = 'ENCAISSEMENT'");
        assertThat(((Number) p.get("montant")).longValue()).isEqualTo(MENSUEL);
        assertThat(p.get("mode")).isEqualTo("MTN_MOMO");
        assertThat(p.get("reference")).isEqualTo("OPERATEUR-evt-1");
        assertThat(jdbc.queryForObject("SELECT paiement_id::text FROM ol_transaction WHERE id = ?::uuid", String.class, t.path("id").asText())).isNotBlank();
        // imputé sur l'échéance la plus ancienne
        assertThat(jdbc.queryForObject("SELECT statut FROM fin_echeance WHERE bail_id = ? ORDER BY periode_debut LIMIT 1", String.class, bail)).isEqualTo("PAYEE");
        // vu par le locataire
        mvc.perform(get("/api/v1/paiements-en-ligne/{id}", t.path("id").asText()).with(moi)).andExpect(jsonPath("$.statut").value("CONFIRMEE")).andExpect(jsonPath("$.paiementId").isNotEmpty());
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() ->
                assertThat(jdbc.queryForObject("SELECT count(*) FROM audit_log WHERE action = 'PAIEMENT_EN_LIGNE.CONFIRMEE' AND entite_id = ?::uuid", Long.class, t.path("id").asText())).isEqualTo(1));
    }

    @Test
    void webhookRejoueNeCreeAucunDoublon() throws Exception {
        initier("cle-1");
        String ref = registre.derniereReference();
        long ts = maintenant();
        webhook("evt-1", ref, "SUCCES", MENSUEL, SECRET, ts).andExpect(jsonPath("$.resultat").value("TRAITE"));
        // l'agrégateur réessaie : même événement
        webhook("evt-1", ref, "SUCCES", MENSUEL, SECRET, ts).andExpect(status().isOk()).andExpect(jsonPath("$.resultat").value("REJOUE"));
        webhook("evt-1", ref, "SUCCES", MENSUEL, SECRET, maintenant()).andExpect(jsonPath("$.resultat").value("REJOUE"));
        // un autre événement annonçant le même succès : traité, mais le paiement n'existe qu'une fois
        webhook("evt-2", ref, "SUCCES", MENSUEL, SECRET, maintenant()).andExpect(status().isOk());
        assertThat(paiementsCrees()).isEqualTo(1);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM ol_webhook_recu", Long.class)).isEqualTo(2);
    }

    @Test
    void webhooksSimultanesDuMemeSuccesNeCreentQuUnPaiement() throws Exception {
        initier("cle-1");
        String ref = registre.derniereReference();
        var pool = Executors.newFixedThreadPool(6);
        try {
            List<Future<Integer>> taches = new ArrayList<>();
            for (int i = 0; i < 6; i++) {
                String evenement = "evt-" + i;
                taches.add(pool.submit(() -> webhook(evenement, ref, "SUCCES", MENSUEL, SECRET, maintenant()).andReturn().getResponse().getStatus()));
            }
            for (Future<Integer> f : taches) {
                assertThat(f.get()).isEqualTo(200);
            }
        } finally {
            pool.shutdown();
        }
        assertThat(paiementsCrees()).isEqualTo(1);
    }

    @Test
    void signatureInvalideEstRefuseeSansRienTraiter() throws Exception {
        JsonNode t = initier("cle-1");
        String ref = registre.derniereReference();
        // mauvais secret
        webhook("evt-1", ref, "SUCCES", MENSUEL, "pas-le-bon-secret", maintenant()).andExpect(status().isUnauthorized()).andExpect(jsonPath("$.code").value("WEBHOOK_SIGNATURE_INVALIDE"));
        // corps modifié après signature
        long ts = maintenant();
        byte[] vrai = json("id", "evt-2", "reference", ref, "statut", "SUCCES", "montant", MENSUEL).getBytes(StandardCharsets.UTF_8);
        byte[] falsifie = json("id", "evt-2", "reference", ref, "statut", "SUCCES", "montant", 1).getBytes(StandardCharsets.UTF_8);
        mvc.perform(post("/api/v1/webhooks/simule").content(falsifie).contentType(MediaType.APPLICATION_JSON).header("X-Sim-Timestamp", String.valueOf(ts))
                .header("X-Sim-Signature", AgregateurSimule.hmac(SECRET, String.valueOf(ts), vrai))).andExpect(status().isUnauthorized());
        // horodatage périmé (rejeu d'une requête capturée)
        webhook("evt-3", ref, "SUCCES", MENSUEL, SECRET, maintenant() - 3600).andExpect(status().isUnauthorized());
        // en-têtes absents ou signature vide
        mvc.perform(post("/api/v1/webhooks/simule").content(vrai).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/v1/webhooks/simule").content(vrai).contentType(MediaType.APPLICATION_JSON).header("X-Sim-Timestamp", String.valueOf(ts)).header("X-Sim-Signature", ""))
                .andExpect(status().isUnauthorized());
        mvc.perform(post("/api/v1/webhooks/simule").content(vrai).contentType(MediaType.APPLICATION_JSON).header("X-Sim-Timestamp", "pas-un-nombre").header("X-Sim-Signature", "abc"))
                .andExpect(status().isUnauthorized());

        assertThat(statut(t.path("id").asText())).isEqualTo("EN_ATTENTE");
        assertThat(paiementsCrees()).isZero();
        assertThat(jdbc.queryForObject("SELECT count(*) FROM ol_webhook_recu", Long.class)).isZero(); // un webhook rejeté n'est pas « reçu »
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() ->
                assertThat(jdbc.queryForObject("SELECT count(*) FROM audit_log WHERE action = 'PAIEMENT_EN_LIGNE.WEBHOOK_REJETE'", Long.class)).isGreaterThanOrEqualTo(5));
    }

    @Test
    void fournisseurInconnuOuInactifEtCorpsTropGros() throws Exception {
        mvc.perform(post("/api/v1/webhooks/inexistant").content("{}".getBytes()).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
        mvc.perform(put("/api/v1/paiements-en-ligne/fournisseurs/simule").with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("actif", false, "champs", Map.of())))
                .andExpect(status().isOk());
        webhook("evt-1", "SIM-x", "SUCCES", 1L, SECRET, maintenant()).andExpect(status().isNotFound());
        activerFournisseur();
        mvc.perform(post("/api/v1/webhooks/simule").content(new byte[70 * 1024]).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isPayloadTooLarge());
    }

    @Test
    void webhookAuthentiqueSurTransactionInconnueEstIgnore() throws Exception {
        webhook("evt-1", "SIM-inconnue", "SUCCES", MENSUEL, SECRET, maintenant()).andExpect(status().isOk()).andExpect(jsonPath("$.resultat").value("IGNORE"));
        assertThat(paiementsCrees()).isZero();
    }

    @Test
    void echecAnnonceParLOperateur() throws Exception {
        JsonNode t = initier("cle-1");
        String ref = registre.derniereReference();
        webhook("evt-1", ref, "ECHEC", null, SECRET, maintenant()).andExpect(status().isOk());
        assertThat(statut(t.path("id").asText())).isEqualTo("ECHOUEE");
        assertThat(paiementsCrees()).isZero();
        mvc.perform(get("/api/v1/paiements-en-ligne/{id}", t.path("id").asText()).with(moi)).andExpect(jsonPath("$.statut").value("ECHOUEE")).andExpect(jsonPath("$.erreur").value("REFUSEE_PAR_OPERATEUR"));
    }

    @Test
    void montantDifferentDeCeluiDemandeNeCreeAucunPaiement() throws Exception {
        JsonNode t = initier("cle-1");
        String ref = registre.derniereReference();
        webhook("evt-1", ref, "SUCCES", MENSUEL - 1000, SECRET, maintenant()).andExpect(status().isOk());
        assertThat(statut(t.path("id").asText())).isEqualTo("ANOMALIE");
        assertThat(paiementsCrees()).isZero();
        // un second événement ne « répare » pas l'anomalie tout seul
        webhook("evt-2", ref, "SUCCES", MENSUEL, SECRET, maintenant()).andExpect(status().isOk());
        assertThat(statut(t.path("id").asText())).isEqualTo("ANOMALIE");
        assertThat(paiementsCrees()).isZero();
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() ->
                assertThat(jdbc.queryForObject("SELECT count(*) FROM audit_log WHERE action = 'PAIEMENT_EN_LIGNE.ANOMALIE' AND date >= ?", Long.class, debutTest)).isEqualTo(1));
    }

    // ================================================================== vérification de statut (filet de sécurité)

    private void vieillir(String transaction, String intervalle) {
        jdbc.update("UPDATE ol_transaction SET created_at = created_at - interval '" + intervalle + "' WHERE id = ?::uuid", transaction);
    }

    @Test
    void sansWebhookLeJobConfirmeApresVerificationAupresDeLAgregateur() throws Exception {
        JsonNode t = initier("cle-1");
        String id = t.path("id").asText();
        registre.confirmer(registre.derniereReference());

        assertThat(service.verifierEnAttente()).isZero(); // trop récente : on laisse le temps au webhook
        vieillir(id, "2 minutes");
        assertThat(service.verifierEnAttente()).isEqualTo(1);
        assertThat(statut(id)).isEqualTo("CONFIRMEE");
        assertThat(paiementsCrees()).isEqualTo(1);
        assertThat(service.verifierEnAttente()).isZero(); // idempotent : plus rien à vérifier
        assertThat(paiementsCrees()).isEqualTo(1);
    }

    @Test
    void jobEtWebhookEnCourseNeCreentQuUnPaiement() throws Exception {
        JsonNode t = initier("cle-1");
        String id = t.path("id").asText();
        String ref = registre.derniereReference();
        registre.confirmer(ref);
        vieillir(id, "2 minutes");
        var pool = Executors.newFixedThreadPool(2);
        try {
            Future<?> job = pool.submit(() -> service.verifierEnAttente());
            Future<Integer> hook = pool.submit(() -> webhook("evt-1", ref, "SUCCES", MENSUEL, SECRET, maintenant()).andReturn().getResponse().getStatus());
            job.get();
            assertThat(hook.get()).isEqualTo(200);
        } finally {
            pool.shutdown();
        }
        assertThat(paiementsCrees()).isEqualTo(1);
        assertThat(statut(id)).isEqualTo("CONFIRMEE");
    }

    @Test
    void agregateurInjoignableLeJobReessaieAuPassageSuivant() throws Exception {
        JsonNode t = initier("cle-1");
        String id = t.path("id").asText();
        registre.confirmer(registre.derniereReference());
        vieillir(id, "2 minutes");
        registre.echouerProchainesVerifications(1);
        service.verifierEnAttente();
        assertThat(statut(id)).isEqualTo("EN_ATTENTE");
        assertThat(jdbc.queryForObject("SELECT verifications FROM ol_transaction WHERE id = ?::uuid", Integer.class, id)).isEqualTo(1);
        jdbc.update("UPDATE ol_transaction SET derniere_verification = now() - interval '5 minutes' WHERE id = ?::uuid", id);
        service.verifierEnAttente();
        assertThat(statut(id)).isEqualTo("CONFIRMEE");
    }

    @Test
    void echecDetecteParLeJob() throws Exception {
        JsonNode t = initier("cle-1");
        String id = t.path("id").asText();
        registre.echouer(registre.derniereReference());
        vieillir(id, "2 minutes");
        service.verifierEnAttente();
        assertThat(statut(id)).isEqualTo("ECHOUEE");
        assertThat(paiementsCrees()).isZero();
    }

    @Test
    void montantAnnonceDifferentDetecteParLeJob() throws Exception {
        JsonNode t = initier("cle-1");
        String id = t.path("id").asText();
        registre.confirmerAvecMontant(registre.derniereReference(), 1);
        vieillir(id, "2 minutes");
        service.verifierEnAttente();
        assertThat(statut(id)).isEqualTo("ANOMALIE");
        assertThat(paiementsCrees()).isZero();
    }

    @Test
    void transactionSansConfirmationExpireMaisUneConfirmationTardiveEstEnregistree() throws Exception {
        JsonNode t = initier("cle-1");
        String id = t.path("id").asText();
        String ref = registre.derniereReference();
        vieillir(id, "25 hours");
        service.verifierEnAttente();
        assertThat(statut(id)).isEqualTo("EXPIREE");
        assertThat(paiementsCrees()).isZero();

        // l'opérateur confirme finalement : l'argent a bien été débité, le paiement doit exister
        webhook("evt-tardif", ref, "SUCCES", MENSUEL, SECRET, maintenant()).andExpect(status().isOk());
        assertThat(statut(id)).isEqualTo("CONFIRMEE");
        assertThat(paiementsCrees()).isEqualTo(1);
    }

    @Test
    void initiationJamaisReferenceeEstAbandonnee() throws Exception {
        UUID orpheline = UUID.randomUUID();
        jdbc.update("INSERT INTO ol_transaction (id, bail_id, locataire_id, fournisseur, operateur, telephone, montant, statut, cle_idempotence, empreinte, created_at, updated_at, version) "
                + "VALUES (?, ?, ?, 'simule', 'MTN_MOMO', '237699000000', 1000, 'EN_ATTENTE', 'k', 'e', now() - interval '20 minutes', now(), 0)", orpheline, bail, locataire);
        service.verifierEnAttente();
        assertThat(statut(orpheline.toString())).isEqualTo("ECHOUEE");
        assertThat(registre.verifications()).isZero(); // sans référence, rien à demander à l'agrégateur
    }

    @Test
    void baseInterditUnePaiementConfirmeSansPaiementEtInversement() {
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> jdbc.update(
                "INSERT INTO ol_transaction (id, bail_id, locataire_id, fournisseur, operateur, telephone, montant, statut, cle_idempotence, empreinte, created_at, updated_at, version) "
                        + "VALUES (?, ?, ?, 'simule', 'MTN_MOMO', '1', 1000, 'CONFIRMEE', 'k2', 'e', now(), now(), 0)", UUID.randomUUID(), bail, locataire))
                .isInstanceOf(org.springframework.dao.DataAccessException.class);
    }

    // ================================================================== droits

    @Test
    void droits() throws Exception {
        JsonNode t = initier("cle-1");
        String id = t.path("id").asText();
        UUID compte2 = utilisateurs.creer(new UtilisateurService.Saisie("Voisin", "+237" + TEL.incrementAndGet(), null, Role.LOCATAIRE, true, false), "Motdepasse123").id();
        locataire(compte2);
        RequestPostProcessor voisin = jwtRole(Role.LOCATAIRE, compte2);

        mvc.perform(get("/api/v1/paiements-en-ligne/{id}", id).with(voisin)).andExpect(status().isNotFound());
        mvc.perform(get("/api/v1/paiements-en-ligne").with(voisin)).andExpect(jsonPath("$.totalElements").value(0));
        mvc.perform(get("/api/v1/paiements-en-ligne").with(moi)).andExpect(jsonPath("$.totalElements").value(1));
        mvc.perform(get("/api/v1/paiements-en-ligne").with(GESTIONNAIRE)).andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(1));
        mvc.perform(get("/api/v1/paiements-en-ligne/{id}", id).with(GESTIONNAIRE)).andExpect(status().isOk());
        // initier est réservé aux locataires
        for (RequestPostProcessor interdit : new RequestPostProcessor[] {ADMIN, GESTIONNAIRE, jwtRole(Role.PROPRIETAIRE_TIERS), jwtRole(Role.PRESTATAIRE)}) {
            payer(interdit, "k").andExpect(status().isForbidden());
        }
        mvc.perform(get("/api/v1/paiements-en-ligne").with(jwtRole(Role.PROPRIETAIRE_TIERS))).andExpect(status().isForbidden());
        payer(request -> request, "k").andExpect(status().isUnauthorized());
        mvc.perform(get("/api/v1/paiements-en-ligne")).andExpect(status().isUnauthorized());
        // le webhook, lui, n'exige pas de jeton (c'est la signature qui authentifie)
        webhook("evt-1", "SIM-x", "SUCCES", 1L, SECRET, maintenant()).andExpect(status().isOk());
    }
}
