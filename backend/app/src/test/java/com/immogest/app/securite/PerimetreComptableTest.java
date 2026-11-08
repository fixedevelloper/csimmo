package com.immogest.app.securite;

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
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
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

/**
 * COMPTABLE (docs/adr/0009-equipe-et-perimetres.md, étape 12.4) : paiements, dépenses et reversements de son périmètre en lecture, validation
 * des reversements. Un reversement n'est visible que si TOUS les immeubles de son propriétaire sont dans le périmètre.
 */
@IntegrationTest
@TestPropertySource(properties = "immogest.finance.horizon-jours=45")
class PerimetreComptableTest {

    private static final RequestPostProcessor ADMIN = jwtRole(Role.ADMIN, UUID.fromString("00000000-0000-0000-0000-0000000000d1"));
    private static final RequestPostProcessor GESTIONNAIRE = jwtRole(Role.GESTIONNAIRE);
    private static final AtomicLong TEL = new AtomicLong(640_000_000L);

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    JdbcTemplate jdbc;
    @Autowired
    UtilisateurService utilisateurs;

    LocalDate moisPrecedent;
    UUID proprietaire1;
    UUID proprietaire2;
    UUID immeubleX1;
    UUID immeubleX2;
    UUID immeubleY1;
    UUID bailX1;
    UUID bailX2;
    UUID bailY1;
    UUID reversement1;
    UUID reversement2;

    @BeforeEach
    void preparer() throws Exception {
        // même remise à zéro que ReversementTest : numéros de relevé et de quittance continus, donc compteurs et documents vont ensemble
        try {
            await().atMost(Duration.ofSeconds(20)).until(() -> jdbc.queryForObject("SELECT count(*) FROM event_publication WHERE completion_date IS NULL AND publication_date > now() - interval '30 seconds'", Long.class) == 0);
        } catch (org.awaitility.core.ConditionTimeoutException ignore) {
            // publications en échec : sans effet ici
        }
        jdbc.execute("TRUNCATE fin_reversement, fin_depense, fin_compteur_numero, rel_relance, rel_mise_en_demeure, notif_message, fin_document, fin_imputation, fin_paiement, fin_echeance, bail_document, bail_garant, bail, patrimoine_photo, compteur, lot_equipement, lot, immeuble, garant, tiers_piece, mandat, proprietaire_tiers, locataire CASCADE");
        String suffixe = Long.toString(System.nanoTime(), 36);
        LocalDate aujourdhui = LocalDate.now(ZoneId.of("Africa/Douala"));
        moisPrecedent = aujourdhui.withDayOfMonth(1).minusMonths(1);
        proprietaire1 = proprietaire("Proprio Un " + suffixe);
        proprietaire2 = proprietaire("Proprio Deux " + suffixe);
        immeubleX1 = creer("/api/v1/immeubles", "nom", "X1 " + suffixe, "adresse", "1 rue", "ville", "Douala", "proprietaireTiersId", proprietaire1.toString());
        immeubleX2 = creer("/api/v1/immeubles", "nom", "X2 " + suffixe, "adresse", "2 rue", "ville", "Douala", "proprietaireTiersId", proprietaire1.toString());
        immeubleY1 = creer("/api/v1/immeubles", "nom", "Y1 " + suffixe, "adresse", "3 rue", "ville", "Douala", "proprietaireTiersId", proprietaire2.toString());
        bailX1 = bailPaye(immeubleX1, "X1-" + suffixe, 150_000);
        bailX2 = bailPaye(immeubleX2, "X2-" + suffixe, 100_000);
        bailY1 = bailPaye(immeubleY1, "Y1-" + suffixe, 90_000);
        for (UUID immeuble : List.of(immeubleX1, immeubleY1)) {
            mvc.perform(post("/api/v1/depenses").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                    .content(json("immeubleId", immeuble.toString(), "dateDepense", moisPrecedent.withDayOfMonth(15).toString(), "nature", "Plomberie", "montant", 10_000, "reference", "FAC-" + suffixe)))
                    .andExpect(status().isCreated());
        }
        JsonNode calcules = mapper.readTree(mvc.perform(post("/api/v1/reversements/calculer").with(GESTIONNAIRE).contentType(MediaType.APPLICATION_JSON)
                .content(json("mois", moisPrecedent.format(DateTimeFormatter.ofPattern("yyyy-MM"))))).andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
        reversement1 = reversementDe(calcules, proprietaire1);
        reversement2 = reversementDe(calcules, proprietaire2);
    }

    // ------------------------------------------------------------------ périmètre complet / partiel

    @Test
    void un_reversement_n_est_visible_que_si_tous_les_immeubles_du_proprietaire_sont_dans_le_perimetre() throws Exception {
        UUID partiel = nouvelEmploye();
        affecter(partiel, immeubleX1); // X2 manque : le reversement du propriétaire 1 révélerait X2
        RequestPostProcessor p = jwtRole(Role.COMPTABLE, partiel);
        assertThat(ids(mvc.perform(get("/api/v1/reversements").param("size", "100").with(p)))).doesNotContain(reversement1, reversement2);
        mvc.perform(get("/api/v1/reversements/{id}", reversement1).with(p)).andExpect(status().isNotFound());
        mvc.perform(get("/api/v1/reversements").param("proprietaireId", proprietaire1.toString()).with(p)).andExpect(status().isNotFound());

        UUID complet = nouvelEmploye();
        affecter(complet, immeubleX1, immeubleX2);
        RequestPostProcessor c = jwtRole(Role.COMPTABLE, complet);
        assertThat(ids(mvc.perform(get("/api/v1/reversements").param("size", "100").with(c)))).contains(reversement1).doesNotContain(reversement2);
        mvc.perform(get("/api/v1/reversements/{id}", reversement1).with(c)).andExpect(status().isOk());
        mvc.perform(get("/api/v1/reversements/{id}", reversement2).with(c)).andExpect(status().isNotFound());
        mvc.perform(get("/api/v1/reversements/{id}/apercu", reversement1).with(c)).andExpect(status().isOk());
        mvc.perform(get("/api/v1/reversements/{id}/apercu", reversement2).with(c)).andExpect(status().isNotFound());
    }

    @Test
    void les_depenses_se_limitent_aux_immeubles_du_perimetre() throws Exception {
        UUID employe = nouvelEmploye();
        affecter(employe, immeubleX1);
        RequestPostProcessor lui = jwtRole(Role.COMPTABLE, employe);
        List<String> immeubles = new ArrayList<>();
        contenu(mvc.perform(get("/api/v1/depenses").param("size", "100").with(lui))).forEach(n -> immeubles.add(n.path("immeubleId").asText()));
        assertThat(immeubles).isNotEmpty().containsOnly(immeubleX1.toString());
        mvc.perform(get("/api/v1/depenses").param("immeubleId", immeubleY1.toString()).with(lui)).andExpect(status().isNotFound());
    }

    @Test
    void les_paiements_se_limitent_au_perimetre() throws Exception {
        UUID employe = nouvelEmploye();
        affecter(employe, immeubleX1, immeubleX2);
        RequestPostProcessor lui = jwtRole(Role.COMPTABLE, employe);
        List<String> baux = new ArrayList<>();
        contenu(mvc.perform(get("/api/v1/paiements").param("size", "100").with(lui))).forEach(n -> baux.add(n.path("bailId").asText()));
        assertThat(baux).isNotEmpty().containsOnly(bailX1.toString(), bailX2.toString());
        mvc.perform(get("/api/v1/paiements").param("bailId", bailY1.toString()).with(lui)).andExpect(status().isNotFound());
    }

    // ------------------------------------------------------------------ validation

    @Test
    void valide_un_reversement_de_son_perimetre_et_telecharge_le_releve() throws Exception {
        UUID employe = nouvelEmploye();
        affecter(employe, immeubleX1, immeubleX2);
        RequestPostProcessor lui = jwtRole(Role.COMPTABLE, employe);

        JsonNode valide = mapper.readTree(mvc.perform(post("/api/v1/reversements/{id}/valider", reversement1).with(lui))
                .andExpect(status().isOk()).andExpect(jsonPath("$.statut").value("VALIDE")).andReturn().getResponse().getContentAsString());
        UUID fichier = UUID.fromString(valide.path("fichierId").asText());
        mvc.perform(get("/api/v1/fichiers/{id}", fichier).with(lui)).andExpect(status().isOk());

        // un reversement hors périmètre : introuvable à la validation comme au téléchargement du relevé
        mvc.perform(post("/api/v1/reversements/{id}/valider", reversement2).with(lui)).andExpect(status().isNotFound());
        JsonNode valide2 = mapper.readTree(mvc.perform(post("/api/v1/reversements/{id}/valider", reversement2).with(GESTIONNAIRE))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
        mvc.perform(get("/api/v1/fichiers/{id}", UUID.fromString(valide2.path("fichierId").asText())).with(lui)).andExpect(status().isForbidden());
    }

    @Test
    void sans_affectation_il_ne_voit_rien() throws Exception {
        RequestPostProcessor sansRien = jwtRole(Role.COMPTABLE, nouvelEmploye());
        assertThat(ids(mvc.perform(get("/api/v1/reversements").with(sansRien)))).isEmpty();
        assertThat(ids(mvc.perform(get("/api/v1/depenses").with(sansRien)))).isEmpty();
        assertThat(ids(mvc.perform(get("/api/v1/paiements").with(sansRien)))).isEmpty();
        mvc.perform(post("/api/v1/reversements/{id}/valider", reversement1).with(sansRien)).andExpect(status().isNotFound());
    }

    // ------------------------------------------------------------------ refus par défaut

    @Test
    void les_routes_non_ouvertes_sont_refusees() throws Exception {
        UUID employe = nouvelEmploye();
        affecter(employe, immeubleX1, immeubleX2);
        RequestPostProcessor lui = jwtRole(Role.COMPTABLE, employe);
        mvc.perform(post("/api/v1/reversements/calculer").with(lui).contentType(MediaType.APPLICATION_JSON).content(json("mois", "2026-01"))).andExpect(status().isForbidden());
        mvc.perform(post("/api/v1/reversements/{id}/payer", reversement1).with(lui).contentType(MediaType.APPLICATION_JSON)
                .content(json("mode", "VIREMENT", "reference", "R1"))).andExpect(status().isForbidden());
        mvc.perform(post("/api/v1/depenses").with(lui).contentType(MediaType.APPLICATION_JSON)
                .content(json("immeubleId", immeubleX1.toString(), "dateDepense", moisPrecedent.toString(), "nature", "X", "montant", 1_000, "reference", "R")))
                .andExpect(status().isForbidden());
        mvc.perform(post("/api/v1/paiements").with(lui).contentType(MediaType.APPLICATION_JSON)
                .content(json("bailId", bailX1.toString(), "montant", 1_000, "mode", "ESPECES"))).andExpect(status().isForbidden());
        mvc.perform(post("/api/v1/paiements/{id}/contre-passation", UUID.randomUUID()).with(lui).contentType(MediaType.APPLICATION_JSON)
                .content(json("motif", "erreur"))).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/paiements/caisse").with(lui)).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/baux").with(lui)).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/locataires").with(lui)).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/immeubles").with(lui)).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/arrieres").with(lui)).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/utilisateurs").with(lui)).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/audit").param("auteur", employe.toString()).with(lui)).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/tableau-de-bord/synthese").with(lui)).andExpect(status().isForbidden());
    }

    @Test
    void la_double_authentification_est_facultative() throws Exception {
        mvc.perform(post("/api/v1/utilisateurs").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("nom", "Sans 2FA", "telephone", tel(), "role", "COMPTABLE", "deuxFacteursActif", false, "motDePasse", SecuriteTestSupport.MOT_DE_PASSE)))
                .andExpect(status().isCreated());
    }

    // ------------------------------------------------------------------ outils métier

    private UUID proprietaire(String nom) throws Exception {
        UUID p = creer("/api/v1/proprietaires-tiers", "nom", nom, "telephone", tel());
        mvc.perform(post("/api/v1/proprietaires-tiers/{id}/mandats", p).with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("tauxHonorairesBp", 1000, "dateDebut", "2020-01-01"))).andExpect(status().isCreated());
        return p;
    }

    /** Bail activé sur un nouveau lot, loyer payé en entier puis imputé au milieu du mois précédent. */
    private UUID bailPaye(UUID immeuble, String reference, long loyer) throws Exception {
        UUID lot = creer("/api/v1/lots", "immeubleId", immeuble.toString(), "reference", reference, "type", "STUDIO");
        UUID locataire = creer("/api/v1/locataires", "nom", "Locataire " + reference, "telephone", tel());
        UUID bail = creer("/api/v1/baux", "lotId", lot.toString(), "locataireId", locataire.toString(), "dateDebut", LocalDate.now(ZoneId.of("Africa/Douala")).minusDays(70).toString(),
                "loyer", loyer, "charges", 0, "periodicite", "MENSUELLE", "jourEcheance", 1, "moisAvance", 0);
        mvc.perform(post("/api/v1/baux/{id}/activer", bail).with(ADMIN)).andExpect(status().isOk());
        await().atMost(Duration.ofSeconds(15)).until(() -> jdbc.queryForObject("SELECT count(*) FROM fin_echeance WHERE bail_id = ?", Long.class, bail) >= 3);
        mvc.perform(post("/api/v1/paiements").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("bailId", bail.toString(), "montant", loyer * 2, "mode", "VIREMENT", "reference", "V" + System.nanoTime()))).andExpect(status().isCreated());
        jdbc.execute("ALTER TABLE fin_imputation DISABLE TRIGGER fin_imputation_immuable");
        try {
            jdbc.update("UPDATE fin_imputation SET created_at = ?::timestamptz WHERE echeance_id IN (SELECT id FROM fin_echeance WHERE bail_id = ?)",
                    moisPrecedent.withDayOfMonth(15) + "T12:00:00+01:00", bail);
        } finally {
            jdbc.execute("ALTER TABLE fin_imputation ENABLE TRIGGER fin_imputation_immuable");
        }
        return bail;
    }

    private UUID reversementDe(JsonNode liste, UUID proprietaire) {
        for (JsonNode r : liste) {
            if (r.path("proprietaireId").asText().equals(proprietaire.toString())) {
                return UUID.fromString(r.path("id").asText());
            }
        }
        throw new AssertionError("reversement introuvable pour " + proprietaire);
    }

    private JsonNode contenu(ResultActions r) throws Exception {
        return mapper.readTree(r.andExpect(status().isOk()).andReturn().getResponse().getContentAsString()).path("content");
    }

    // ------------------------------------------------------------------ outils

    private static String tel() {
        return "+237" + TEL.incrementAndGet();
    }

    private UUID creer(String chemin, Object... champs) throws Exception {
        String reponse = mvc.perform(post(chemin).with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json(champs)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return UUID.fromString(mapper.readTree(reponse).path("id").asText());
    }

    private UUID bailActif(UUID lot, UUID locataire, LocalDate debut) throws Exception {
        UUID bail = creer("/api/v1/baux", "lotId", lot.toString(), "locataireId", locataire.toString(), "dateDebut", debut.toString(),
                "loyer", 150_000, "charges", 10_000, "periodicite", "MENSUELLE", "jourEcheance", 1, "moisAvance", 0);
        mvc.perform(post("/api/v1/baux/{id}/activer", bail).with(ADMIN)).andExpect(status().isOk());
        return bail;
    }

    private UUID nouvelEmploye() {
        return utilisateurs.creer(new UtilisateurService.Saisie("Employé " + System.nanoTime(), tel(), null, Role.COMPTABLE, true, true),
                SecuriteTestSupport.MOT_DE_PASSE).id();
    }

    private void affecter(UUID utilisateur, UUID... immeubles) throws Exception {
        List<String> ids = new ArrayList<>();
        for (UUID i : immeubles) {
            ids.add(i.toString());
        }
        mvc.perform(put("/api/v1/utilisateurs/{id}/affectations", utilisateur).with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("immeubleIds", ids))).andExpect(status().isOk());
    }

    private List<UUID> ids(ResultActions r) throws Exception {
        JsonNode contenu = mapper.readTree(r.andExpect(status().isOk()).andReturn().getResponse().getContentAsString()).path("content");
        List<UUID> ids = new ArrayList<>();
        contenu.forEach(n -> ids.add(UUID.fromString(n.path("id").asText())));
        return ids;
    }
}
