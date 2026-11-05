package com.immogest.app.conformite;

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
import com.immogest.app.tiers.CiblePiece;
import com.immogest.app.tiers.LocataireService;
import com.immogest.app.tiers.TypePiece;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

/** Conformité (loi 2024/017) : registre, modèles, consentements en ajout seul, demandes d'anonymisation et leurs obstacles, effacement lié, droits. */
@IntegrationTest
class ConformiteTest {

    private static final RequestPostProcessor ADMIN = jwtRole(Role.ADMIN, UUID.fromString("00000000-0000-0000-0000-0000000000a1"));
    private static final RequestPostProcessor GESTIONNAIRE = jwtRole(Role.GESTIONNAIRE);
    private static final AtomicLong N = new AtomicLong(660_000_000L);

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    JdbcTemplate jdbc;
    @Autowired
    UtilisateurService utilisateurs;
    @Autowired
    LocataireService locataires;
    @Autowired
    org.springframework.transaction.support.TransactionTemplate tx;

    LocalDate aujourdhui;

    @BeforeEach
    void preparer() {
        aujourdhui = LocalDate.now(ZoneId.of("Africa/Douala"));
        try {
            await().atMost(Duration.ofSeconds(20)).until(() -> jdbc.queryForObject("SELECT count(*) FROM event_publication WHERE completion_date IS NULL AND publication_date > now() - interval '30 seconds'", Long.class) == 0);
        } catch (org.awaitility.core.ConditionTimeoutException ignore) {
            // sans effet ici
        }
        jdbc.execute("TRUNCATE conf_demande, conf_consentement, imp_journal, ol_webhook_recu, ol_transaction, mnt_signalement, notif_message, fin_document, fin_compteur_numero, fin_imputation, fin_paiement, fin_echeance, bail_document, bail_garant, bail, patrimoine_photo, compteur, lot_equipement, lot, immeuble, garant, tiers_piece, locataire, mandat, proprietaire_tiers CASCADE");
    }

    private UUID id(ResultActions r) throws Exception {
        return UUID.fromString(mapper.readTree(r.andExpect(status().is2xxSuccessful()).andReturn().getResponse().getContentAsString()).path("id").asText());
    }

    private String tel() {
        return "+2376" + (N.incrementAndGet() % 100_000_000L + 10_000_000L);
    }

    private UUID locataire(UUID utilisateur) throws Exception {
        return id(mvc.perform(post("/api/v1/locataires").with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("nom", "Paul Secret", "telephone", tel(), "email", "paul@secret.cm",
                "profession", "Médecin", "adresse", "12 rue Cachée", "dateNaissance", "1980-05-05", "numeroCni", "CNI-998877", "notes", "bon payeur", "utilisateurId", utilisateur == null ? null : utilisateur.toString()))));
    }

    private UUID lotEtBail(UUID loc, int moisEnArriere) throws Exception {
        UUID imm = id(mvc.perform(post("/api/v1/immeubles").with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("nom", "Imm " + N.incrementAndGet(), "adresse", "1 rue", "ville", "Douala"))));
        UUID lot = id(mvc.perform(post("/api/v1/lots").with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("immeubleId", imm.toString(), "reference", "L1", "type", "STUDIO"))));
        UUID bail = id(mvc.perform(post("/api/v1/baux").with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("lotId", lot.toString(), "locataireId", loc.toString(),
                "dateDebut", aujourdhui.minusMonths(moisEnArriere).withDayOfMonth(1).toString(), "loyer", 100000, "charges", 0, "periodicite", "MENSUELLE", "jourEcheance", 1, "moisAvance", 0))));
        mvc.perform(post("/api/v1/baux/{id}/activer", bail).with(ADMIN)).andExpect(status().isOk());
        return bail;
    }

    private JsonNode demander(String type, UUID personne, RequestPostProcessor qui) throws Exception {
        return mapper.readTree(mvc.perform(post("/api/v1/conformite/demandes").with(qui).contentType(MediaType.APPLICATION_JSON).content(json("type", type, "personneId", personne.toString(), "motif", "Demande écrite du 4 novembre")))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString());
    }

    private static byte[] png() throws Exception {
        BufferedImage img = new BufferedImage(4, 4, BufferedImage.TYPE_INT_RGB);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ImageIO.write(img, "png", out);
            return out.toByteArray();
        }
    }

    // ------------------------------------------------------------------ registre et modèles

    @Test
    void leRegistreDesTraitementsEstCompletEtLisible() throws Exception {
        JsonNode r = mapper.readTree(mvc.perform(get("/api/v1/conformite/registre").with(GESTIONNAIRE)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
        assertThat(r.path("traitements").size()).isEqualTo(9);
        for (JsonNode t : r.path("traitements")) {
            for (String champ : new String[] {"id", "nom", "finalite", "baseLegale", "conservation"}) {
                assertThat(t.path(champ).asText()).as(t.path("id").asText() + "." + champ).isNotBlank();
            }
            for (String liste : new String[] {"donnees", "personnes", "destinataires", "mesures"}) {
                assertThat(t.path(liste).size()).as(t.path("id").asText() + "." + liste).isGreaterThan(0);
            }
        }
        assertThat(r.path("note").asText()).contains("conseil juridique");
        // la vérification de licence ne transmet aucune donnée de locataire
        assertThat(r.path("traitements").findValuesAsText("id")).contains("T8");
    }

    @Test
    void lesModelesDeConsentementExistentEnFrancaisEtEnAnglais() throws Exception {
        for (String langue : new String[] {"fr", "en"}) {
            JsonNode m = mapper.readTree(mvc.perform(get("/api/v1/conformite/modeles-consentement").param("langue", langue).with(GESTIONNAIRE)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
            assertThat(m.size()).isEqualTo(4);
            assertThat(m.findValuesAsText("finalite")).containsExactlyInAnyOrder("NOTIFICATIONS", "PIECES_IDENTITE", "TRANSMISSION_PROPRIETAIRE", "APPLICATION_MOBILE");
            m.forEach(x -> assertThat(x.path("texte").asText()).contains("{RESPONSABLE}").hasSizeGreaterThan(60));
        }
        String fr = mvc.perform(get("/api/v1/conformite/modeles-consentement").with(GESTIONNAIRE)).andReturn().getResponse().getContentAsString();
        assertThat(fr).contains("J'accepte");
    }

    // ------------------------------------------------------------------ consentements

    @Test
    void lePlusRecentConsentementFaitFoiEtLHistoriqueEstEnAjoutSeul() throws Exception {
        UUID loc = locataire(null);
        JsonNode s0 = mapper.readTree(mvc.perform(get("/api/v1/conformite/consentements/LOCATAIRE/{id}", loc).with(GESTIONNAIRE)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
        assertThat(s0.path("finalites").findValuesAsText("statut")).containsOnly("NON_RECUEILLI");

        for (boolean accorde : new boolean[] {true, false, true}) {
            mvc.perform(post("/api/v1/conformite/consentements").with(GESTIONNAIRE).contentType(MediaType.APPLICATION_JSON).content(json("type", "LOCATAIRE", "personneId", loc.toString(),
                    "finalite", "NOTIFICATIONS", "accorde", accorde, "modeRecueil", "PAPIER", "referenceDocument", "Formulaire n°12"))).andExpect(status().isCreated())
                    .andExpect(jsonPath("$.versionModele").value("1.0")).andExpect(jsonPath("$.accorde").value(accorde));
        }
        mvc.perform(post("/api/v1/conformite/consentements").with(GESTIONNAIRE).contentType(MediaType.APPLICATION_JSON).content(json("type", "LOCATAIRE", "personneId", loc.toString(),
                "finalite", "PIECES_IDENTITE", "accorde", false, "modeRecueil", "ORAL"))).andExpect(status().isCreated());
        JsonNode s = mapper.readTree(mvc.perform(get("/api/v1/conformite/consentements/LOCATAIRE/{id}", loc).with(GESTIONNAIRE)).andReturn().getResponse().getContentAsString());
        assertThat(s.path("historique").size()).isEqualTo(4);
        assertThat(s.path("finalites").get(0).path("finalite").asText()).isEqualTo("NOTIFICATIONS");
        assertThat(s.path("finalites").get(0).path("statut").asText()).isEqualTo("ACCORDE"); // le dernier acte (accord) l'emporte sur le retrait précédent
        assertThat(s.path("finalites").get(1).path("statut").asText()).isEqualTo("REFUSE");
        assertThat(s.path("finalites").get(2).path("statut").asText()).isEqualTo("NON_RECUEILLI");
        // ajout seul : la base refuse toute modification ou suppression
        assertThatThrownBy(() -> jdbc.update("UPDATE conf_consentement SET accorde = true")).hasMessageContaining("ajout seul");
        assertThatThrownBy(() -> jdbc.update("DELETE FROM conf_consentement")).hasMessageContaining("ajout seul");
    }

    @Test
    void consentementInvalide() throws Exception {
        mvc.perform(post("/api/v1/conformite/consentements").with(GESTIONNAIRE).contentType(MediaType.APPLICATION_JSON).content(json("type", "LOCATAIRE", "personneId", UUID.randomUUID().toString(),
                "finalite", "NOTIFICATIONS", "accorde", true, "modeRecueil", "PAPIER"))).andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value("PERSONNE_INTROUVABLE"));
        UUID loc = locataire(null);
        mvc.perform(post("/api/v1/conformite/consentements").with(GESTIONNAIRE).contentType(MediaType.APPLICATION_JSON).content(json("type", "LOCATAIRE", "personneId", loc.toString(), "accorde", true)))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("CONSENTEMENT_INCOMPLET"));
        mvc.perform(post("/api/v1/conformite/consentements").with(GESTIONNAIRE).contentType(MediaType.APPLICATION_JSON).content(json("type", "AUTRE", "personneId", loc.toString()))).andExpect(status().isBadRequest());
    }

    // ------------------------------------------------------------------ anonymisation

    @Test
    void anonymisationCompleteEffaceLaFicheLeCompteLesMessagesLesPiecesEtLAudit() throws Exception {
        UUID compte = utilisateurs.creer(new UtilisateurService.Saisie("Paul Secret", tel(), "paul-compte@secret.cm", Role.LOCATAIRE, true, false), "Motdepasse123").id();
        UUID loc = locataire(compte);
        locataires.ajouterPiece(CiblePiece.LOCATAIRE, loc, TypePiece.CNI_RECTO, "cni.png", new ByteArrayInputStream(png()));
        UUID garant = id(mvc.perform(post("/api/v1/locataires/{id}/garants", loc).with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("nom", "Garant Secret", "telephone", tel(), "lien", "Frère", "numeroCni", "G-123"))));
        jdbc.update("INSERT INTO notif_message (id, canal, type_message, destinataire, sujet, corps, statut, prochaine_tentative, locataire_id, cle_dedoublonnage, created_at, updated_at, version) "
                + "VALUES (?, 'SMS', 'RELANCE', '+237600000000', 'Sujet', 'Bonjour Paul Secret, vous devez 100000 FCFA', 'ENVOYE', now(), ?, ?, now(), now(), 0)", UUID.randomUUID(), loc, "k-" + loc);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM tiers_piece", Long.class)).isEqualTo(1);
        long auditAvant = jdbc.queryForObject("SELECT count(*) FROM audit_log WHERE entite_id = ?", Long.class, loc);
        await().atMost(Duration.ofSeconds(20)).until(() -> jdbc.queryForObject("SELECT count(*) FROM audit_log WHERE entite_id = ? AND apres ->> 'nom' = 'Paul Secret'", Long.class, loc) > 0);

        JsonNode d = demander("LOCATAIRE", loc, GESTIONNAIRE);
        assertThat(d.path("statut").asText()).isEqualTo("RECUE");
        assertThat(d.path("obstacles")).isEmpty();
        JsonNode r = mapper.readTree(mvc.perform(post("/api/v1/conformite/demandes/{id}/execution", d.path("id").asText()).with(ADMIN)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
        assertThat(r.path("piecesSupprimees").asInt()).isEqualTo(1);
        assertThat(r.path("conserve").size()).isEqualTo(3); // on dit à la personne ce qui est gardé, et pourquoi

        // la fiche reste (les documents comptables y renvoient) mais ne contient plus rien de personnel
        var l = jdbc.queryForMap("SELECT * FROM locataire WHERE id = ?", loc);
        assertThat(l.get("nom")).isEqualTo("Locataire anonymisé");
        for (String c : new String[] {"telephone", "email", "profession", "adresse", "date_naissance", "numero_cni_chiffre", "notes", "utilisateur_id"}) {
            assertThat(l.get(c)).as(c).isNull();
        }
        assertThat(l.get("archive")).isEqualTo(true);
        assertThat(l.get("anonymise_le")).isNotNull();
        var g = jdbc.queryForMap("SELECT * FROM garant WHERE id = ?", garant);
        assertThat(g.get("nom")).isEqualTo("Garant anonymisé");
        assertThat(g.get("telephone")).isNull();
        assertThat(g.get("numero_cni_chiffre")).isNull();
        assertThat(jdbc.queryForObject("SELECT count(*) FROM tiers_piece", Long.class)).isZero();

        // effacements liés, par événement (livré même après redémarrage)
        await().atMost(Duration.ofSeconds(30)).untilAsserted(() -> {
            var u = jdbc.queryForMap("SELECT * FROM utilisateur WHERE id = ?", compte);
            assertThat(u.get("nom")).isEqualTo("Utilisateur anonymisé");
            assertThat(u.get("email")).isNull();
            assertThat((String) u.get("telephone")).startsWith("anonyme-");
            assertThat(u.get("actif")).isEqualTo(false);
            assertThat(u.get("mot_de_passe_hash")).isEqualTo("!");
            var m = jdbc.queryForMap("SELECT * FROM notif_message WHERE locataire_id = ?", loc);
            assertThat(m.get("corps")).isEqualTo("[message anonymisé]");
            assertThat(m.get("destinataire")).isEqualTo("anonymisé");
            assertThat(jdbc.queryForObject("SELECT count(*) FROM audit_log WHERE entite_id = ? AND (apres ->> 'nom' = 'Paul Secret' OR avant ->> 'nom' = 'Paul Secret')", Long.class, loc)).isZero();
        });
        // le journal d'audit n'a perdu aucune ligne : seules les données personnelles ont été remplacées
        assertThat(jdbc.queryForObject("SELECT count(*) FROM audit_log WHERE entite_id = ?", Long.class, loc)).isGreaterThanOrEqualTo(auditAvant);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM audit_log WHERE entite_id = ? AND apres ->> 'nom' = 'ANONYME'", Long.class, loc)).isGreaterThan(0);
        // la personne ne peut plus se connecter
        mvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON).content(json("identifiant", "paul-compte@secret.cm", "motDePasse", "Motdepasse123"))).andExpect(status().isUnauthorized());
        // l'opération elle-même est tracée
        await().atMost(Duration.ofSeconds(20)).untilAsserted(() -> assertThat(jdbc.queryForObject("SELECT count(*) FROM audit_log WHERE action = 'CONFORMITE.PERSONNE_ANONYMISEE' AND entite_id = ?", Long.class, loc)).isEqualTo(1));
        // une seconde anonymisation est refusée
        mvc.perform(post("/api/v1/conformite/demandes").with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("type", "LOCATAIRE", "personneId", loc.toString()))).andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("PERSONNE_DEJA_ANONYMISEE"));
        mvc.perform(post("/api/v1/conformite/consentements").with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("type", "LOCATAIRE", "personneId", loc.toString(), "finalite", "NOTIFICATIONS",
                "accorde", true, "modeRecueil", "PAPIER"))).andExpect(status().isConflict());
    }

    @Test
    void leJournalDAuditResteProtegeEnDehorsDeLAnonymisation() throws Exception {
        UUID loc = locataire(null);
        await().atMost(Duration.ofSeconds(20)).until(() -> jdbc.queryForObject("SELECT count(*) FROM audit_log WHERE entite_id = ?", Long.class, loc) > 0);
        UUID un = jdbc.queryForObject("SELECT id FROM audit_log WHERE entite_id = ? LIMIT 1", UUID.class, loc);
        assertThatThrownBy(() -> jdbc.update("UPDATE audit_log SET apres = '{}'::jsonb WHERE id = ?", un)).hasMessageContaining("ajout seul");
        assertThatThrownBy(() -> jdbc.update("DELETE FROM audit_log WHERE id = ?", un)).hasMessageContaining("ajout seul");
        // même avec l'indicateur : on ne peut ni changer l'action, ni supprimer
        assertThatThrownBy(() -> tx.executeWithoutResult(s -> {
            jdbc.execute("SET LOCAL immogest.anonymisation = 'on'");
            jdbc.update("UPDATE audit_log SET action = 'X' WHERE id = ?", un);
        })).hasMessageContaining("ajout seul");
        assertThatThrownBy(() -> tx.executeWithoutResult(s -> {
            jdbc.execute("SET LOCAL immogest.anonymisation = 'on'");
            jdbc.update("DELETE FROM audit_log WHERE id = ?", un);
        })).hasMessageContaining("ajout seul");
        // avec l'indicateur, seules les colonnes avant/apres peuvent changer
        tx.executeWithoutResult(s -> {
            jdbc.execute("SET LOCAL immogest.anonymisation = 'on'");
            assertThat(jdbc.update("UPDATE audit_log SET apres = apres WHERE id = ?", un)).isEqualTo(1);
        });
    }

    @Test
    void unBailEnCoursEtDesImpayesEmpechentLAnonymisation() throws Exception {
        UUID loc = locataire(null);
        lotEtBail(loc, 3); // commencé il y a 3 mois, jamais payé : en cours ET en retard
        await().atMost(Duration.ofSeconds(30)).until(() -> jdbc.queryForObject("SELECT count(*) FROM fin_echeance", Long.class) >= 3);
        JsonNode d = demander("LOCATAIRE", loc, GESTIONNAIRE);
        assertThat(d.path("obstacles").findValuesAsText("code")).contains("BAIL_EN_COURS", "IMPAYES");
        mvc.perform(post("/api/v1/conformite/demandes/{id}/execution", d.path("id").asText()).with(ADMIN)).andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("ANONYMISATION_IMPOSSIBLE"));
        assertThat(jdbc.queryForObject("SELECT nom FROM locataire WHERE id = ?", String.class, loc)).isEqualTo("Paul Secret");
        // le garant du locataire est bloqué aussi
        UUID garant = id(mvc.perform(post("/api/v1/locataires/{id}/garants", loc).with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("nom", "G", "telephone", tel()))));
        assertThat(demander("GARANT", garant, GESTIONNAIRE).path("obstacles").findValuesAsText("code")).contains("BAIL_EN_COURS");
    }

    @Test
    void unProprietaireAvecDesBiensNePeutPasEtreAnonymise() throws Exception {
        UUID prop = id(mvc.perform(post("/api/v1/proprietaires-tiers").with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("nom", "Chef Propriétaire", "telephone", tel(), "modeReversement", "VIREMENT",
                "coordonneesReversement", "IBAN CM21 0000"))));
        id(mvc.perform(post("/api/v1/immeubles").with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("nom", "Imm P " + N.incrementAndGet(), "adresse", "1 rue", "ville", "Douala", "proprietaireTiersId", prop.toString()))));
        JsonNode d = demander("PROPRIETAIRE", prop, ADMIN);
        assertThat(d.path("obstacles").findValuesAsText("code")).containsExactly("BIENS_GERES");
        mvc.perform(post("/api/v1/conformite/demandes/{id}/refus", d.path("id").asText()).with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("motif", "Biens encore gérés"))).andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("REFUSEE"));
        // sans biens : possible, et les coordonnées de reversement sont effacées
        UUID libre = id(mvc.perform(post("/api/v1/proprietaires-tiers").with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("nom", "Autre", "telephone", tel(), "modeReversement", "VIREMENT", "coordonneesReversement", "IBAN CM99"))));
        JsonNode d2 = demander("PROPRIETAIRE", libre, ADMIN);
        mvc.perform(post("/api/v1/conformite/demandes/{id}/execution", d2.path("id").asText()).with(ADMIN)).andExpect(status().isOk());
        var p = jdbc.queryForMap("SELECT * FROM proprietaire_tiers WHERE id = ?", libre);
        assertThat(p.get("nom")).isEqualTo("Propriétaire anonymisé");
        assertThat(p.get("coordonnees_reversement")).isNull();
        assertThat(p.get("telephone")).isNull();
    }

    @Test
    void refusEtDemandeUnique() throws Exception {
        UUID loc = locataire(null);
        JsonNode d = demander("LOCATAIRE", loc, GESTIONNAIRE);
        mvc.perform(post("/api/v1/conformite/demandes").with(GESTIONNAIRE).contentType(MediaType.APPLICATION_JSON).content(json("type", "LOCATAIRE", "personneId", loc.toString()))).andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DEMANDE_DEJA_OUVERTE"));
        mvc.perform(post("/api/v1/conformite/demandes/{id}/refus", d.path("id").asText()).with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("motif", " "))).andExpect(status().isBadRequest());
        mvc.perform(post("/api/v1/conformite/demandes/{id}/refus", d.path("id").asText()).with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("motif", "Identité non vérifiée"))).andExpect(status().isOk())
                .andExpect(jsonPath("$.motifRefus").value("Identité non vérifiée")).andExpect(jsonPath("$.traiteePar").isNotEmpty());
        // une demande traitée ne se rejoue pas ; une nouvelle demande reste possible
        mvc.perform(post("/api/v1/conformite/demandes/{id}/execution", d.path("id").asText()).with(ADMIN)).andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("DEMANDE_DEJA_TRAITEE"));
        mvc.perform(post("/api/v1/conformite/demandes/{id}/refus", d.path("id").asText()).with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("motif", "encore"))).andExpect(status().isConflict());
        demander("LOCATAIRE", loc, GESTIONNAIRE);
        mvc.perform(get("/api/v1/conformite/demandes").param("statut", "REFUSEE").with(GESTIONNAIRE)).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
        assertThat(jdbc.queryForObject("SELECT nom FROM locataire WHERE id = ?", String.class, loc)).isEqualTo("Paul Secret"); // un refus ne touche à rien
    }

    // ------------------------------------------------------------------ droits

    @Test
    void droits() throws Exception {
        UUID loc = locataire(null);
        JsonNode d = demander("LOCATAIRE", loc, GESTIONNAIRE); // un gestionnaire enregistre la demande reçue...
        // ... mais seul un administrateur l'exécute ou la refuse
        mvc.perform(post("/api/v1/conformite/demandes/{id}/execution", d.path("id").asText()).with(GESTIONNAIRE)).andExpect(status().isForbidden());
        mvc.perform(post("/api/v1/conformite/demandes/{id}/refus", d.path("id").asText()).with(GESTIONNAIRE).contentType(MediaType.APPLICATION_JSON).content(json("motif", "x"))).andExpect(status().isForbidden());
        for (RequestPostProcessor interdit : new RequestPostProcessor[] {jwtRole(Role.LOCATAIRE, UUID.randomUUID()), jwtRole(Role.PROPRIETAIRE_TIERS), jwtRole(Role.PRESTATAIRE)}) {
            mvc.perform(get("/api/v1/conformite/registre").with(interdit)).andExpect(status().isForbidden());
            mvc.perform(get("/api/v1/conformite/modeles-consentement").with(interdit)).andExpect(status().isForbidden());
            mvc.perform(get("/api/v1/conformite/consentements/LOCATAIRE/{id}", loc).with(interdit)).andExpect(status().isForbidden());
            mvc.perform(get("/api/v1/conformite/demandes").with(interdit)).andExpect(status().isForbidden());
            mvc.perform(post("/api/v1/conformite/demandes").with(interdit).contentType(MediaType.APPLICATION_JSON).content(json("type", "LOCATAIRE", "personneId", loc.toString()))).andExpect(status().isForbidden());
            mvc.perform(post("/api/v1/conformite/demandes/{id}/execution", d.path("id").asText()).with(interdit)).andExpect(status().isForbidden());
        }
        mvc.perform(get("/api/v1/conformite/registre")).andExpect(status().isUnauthorized());
        assertThat(jdbc.queryForObject("SELECT nom FROM locataire WHERE id = ?", String.class, loc)).isEqualTo("Paul Secret");
    }
}
