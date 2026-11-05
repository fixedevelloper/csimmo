package com.immogest.app.bail;

import static com.immogest.app.securite.SecuriteTestSupport.json;
import static com.immogest.app.securite.SecuriteTestSupport.jwtRole;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.immogest.app.IntegrationTest;
import com.immogest.app.securite.Role;
import com.immogest.app.securite.UtilisateurService;
import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

/** Cycle de vie du bail, chevauchements (service et base), PDF, modèle, rôles. */
@IntegrationTest
class BailApiTest {

    private static final RequestPostProcessor ADMIN = jwtRole(Role.ADMIN, UUID.fromString("00000000-0000-0000-0000-0000000000a1"));
    private static final RequestPostProcessor GESTIONNAIRE = jwtRole(Role.GESTIONNAIRE);
    private static final AtomicLong TEL = new AtomicLong(690_000_000L);

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    JdbcTemplate jdbc;
    @Autowired
    UtilisateurService utilisateurs;
    @Autowired
    BailService service;

    UUID lot;
    UUID locataire;

    @BeforeEach
    void preparer() throws Exception {
        jdbc.execute("TRUNCATE bail_document, bail_garant, bail, bail_modele, patrimoine_photo, compteur, lot_equipement, lot, immeuble, garant, tiers_piece, locataire CASCADE");
        UUID immeuble = id(mvc.perform(post("/api/v1/immeubles").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("nom", "Résidence Les Palmiers", "adresse", "12 rue des Palmiers", "ville", "Douala", "quartier", "Bonanjo"))));
        lot = id(mvc.perform(post("/api/v1/lots").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("immeubleId", immeuble.toString(), "reference", "A1", "type", "APPARTEMENT", "surfaceM2", 55))));
        locataire = nouveauLocataire(null);
    }

    private UUID id(ResultActions r) throws Exception {
        return UUID.fromString(corps(r).path("id").asText());
    }

    private JsonNode corps(ResultActions r) throws Exception {
        return mapper.readTree(r.andReturn().getResponse().getContentAsString());
    }

    private UUID nouveauLocataire(UUID compte) throws Exception {
        return id(mvc.perform(post("/api/v1/locataires").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("nom", "Paul Mbarga", "telephone", "+237" + TEL.incrementAndGet(), "numeroCni", "CNI-998877", "profession", "Enseignant",
                        "utilisateurId", compte == null ? null : compte.toString()))).andExpect(status().isCreated()));
    }

    private String bail(UUID lotId, UUID locataireId, String debut, String fin, Object... extra) {
        Object[] base = {"lotId", lotId.toString(), "locataireId", locataireId.toString(), "dateDebut", debut, "dateFin", fin, "loyer", 150000,
                "charges", 10000, "periodicite", "MENSUELLE", "jourEcheance", 5, "caution", 300000, "moisAvance", 2};
        Object[] tout = new Object[base.length + extra.length];
        System.arraycopy(base, 0, tout, 0, base.length);
        System.arraycopy(extra, 0, tout, base.length, extra.length);
        return json(tout);
    }

    private ResultActions creerBrut(String corps) throws Exception {
        return mvc.perform(post("/api/v1/baux").with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(corps));
    }

    private UUID creer(UUID lotId, String debut, String fin) throws Exception {
        return id(creerBrut(bail(lotId, locataire, debut, fin)).andExpect(status().isCreated()));
    }

    private ResultActions activer(UUID id) throws Exception {
        return mvc.perform(post("/api/v1/baux/{id}/activer", id).with(ADMIN));
    }

    private String statutLot() throws Exception {
        return corps(mvc.perform(get("/api/v1/lots/{id}", lot).with(ADMIN))).path("statut").asText();
    }

    // ================================================================== création / activation

    @Test
    void creerUnBrouillonPuisL_activerOccupeLeLot() throws Exception {
        JsonNode b = corps(creerBrut(bail(lot, locataire, "2026-01-01", "2026-12-31")).andExpect(status().isCreated())
                .andExpect(jsonPath("$.statut").value("BROUILLON")).andExpect(jsonPath("$.numero").value(org.hamcrest.Matchers.matchesPattern("BAIL-\\d{4}-\\d{4}")))
                .andExpect(jsonPath("$.lotReference").value("A1")).andExpect(jsonPath("$.locataireNom").value("Paul Mbarga"))
                .andExpect(jsonPath("$.preavisMois").value(3)));
        UUID id = UUID.fromString(b.path("id").asText());
        assertThat(statutLot()).isEqualTo("LIBRE");

        activer(id).andExpect(status().isOk()).andExpect(jsonPath("$.statut").value("ACTIF"));
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> assertThat(statutLot()).isEqualTo("OCCUPE"));

        // un exemplaire PDF a été généré à l'activation
        mvc.perform(get("/api/v1/baux/{id}/documents", id).with(GESTIONNAIRE)).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].version").value(1));
        mvc.perform(put("/api/v1/baux/{id}", id).with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(bail(lot, locataire, "2026-01-01", "2026-12-31")))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("BAIL_NON_MODIFIABLE"));
        mvc.perform(delete("/api/v1/baux/{id}", id).with(ADMIN)).andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("BAIL_NON_SUPPRIMABLE"));
        activer(id).andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("BAIL_NON_ACTIVABLE"));
    }

    @Test
    void brouillonModifiableEtSupprimable() throws Exception {
        UUID id = creer(lot, "2026-01-01", "2026-12-31");
        mvc.perform(put("/api/v1/baux/{id}", id).with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(bail(lot, locataire, "2026-02-01", null, "loyer", 200000)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.loyer").value(200000)).andExpect(jsonPath("$.dateFin").doesNotExist());
        mvc.perform(delete("/api/v1/baux/{id}", id).with(ADMIN)).andExpect(status().isNoContent());
        mvc.perform(get("/api/v1/baux/{id}", id).with(ADMIN)).andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value("BAIL_INTROUVABLE"));
    }

    @Test
    void chevauchementDeDatesRefuseALActivation() throws Exception {
        UUID premier = creer(lot, "2026-01-01", "2026-06-30");
        activer(premier).andExpect(status().isOk());

        // chevauche partiellement
        UUID b2 = creer(lot, "2026-06-30", "2026-12-31");
        activer(b2).andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("BAIL_LOT_DEJA_OCCUPE"));
        // englobe
        UUID b3 = creer(lot, "2025-01-01", null);
        activer(b3).andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("BAIL_LOT_DEJA_OCCUPE"));
        // contigu : accepté
        UUID b4 = creer(lot, "2026-07-01", "2026-12-31");
        activer(b4).andExpect(status().isOk());
        // avant : accepté
        UUID b5 = creer(lot, "2025-01-01", "2025-12-31");
        activer(b5).andExpect(status().isOk());
    }

    @Test
    void laBaseRefuseUnChevauchementMemeSansPasserParLeService() throws Exception {
        UUID a = creer(lot, "2026-01-01", "2026-06-30");
        UUID b = creer(lot, "2026-03-01", "2026-09-30");
        jdbc.update("UPDATE bail SET statut = 'ACTIF' WHERE id = ?", a);
        assertThatThrownBy(() -> jdbc.update("UPDATE bail SET statut = 'ACTIF' WHERE id = ?", b)).isInstanceOf(DataIntegrityViolationException.class);
        // un autre lot n'est pas concerné
        UUID autreLot = id(mvc.perform(post("/api/v1/lots").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("immeubleId", jdbc.queryForObject("SELECT immeuble_id FROM lot WHERE id = ?", UUID.class, lot).toString(), "reference", "B2", "type", "STUDIO"))));
        UUID c = creer(autreLot, "2026-03-01", "2026-09-30");
        assertThat(jdbc.update("UPDATE bail SET statut = 'ACTIF' WHERE id = ?", c)).isEqualTo(1);
    }

    @Test
    void validations() throws Exception {
        creerBrut(bail(lot, locataire, "2026-05-01", "2026-04-01")).andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("BAIL_DATES_INVALIDES"));
        creerBrut(bail(lot, locataire, "2026-01-01", null, "loyer", 0)).andExpect(status().isBadRequest());
        creerBrut(bail(lot, locataire, "2026-01-01", null, "jourEcheance", 32)).andExpect(status().isBadRequest());
        creerBrut(bail(UUID.randomUUID(), locataire, "2026-01-01", null)).andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value("LOT_INTROUVABLE"));
        creerBrut(bail(lot, UUID.randomUUID(), "2026-01-01", null)).andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value("LOCATAIRE_INTROUVABLE"));
        // garant d'un autre locataire
        UUID autre = nouveauLocataire(null);
        UUID garantAutre = id(mvc.perform(post("/api/v1/locataires/{id}/garants", autre).with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("nom", "Marie", "lien", "Mère", "telephone", "+237" + TEL.incrementAndGet()))).andExpect(status().isCreated()));
        creerBrut(bail(lot, locataire, "2026-01-01", null, "garantIds", java.util.List.of(garantAutre.toString())))
                .andExpect(status().isUnprocessableEntity()).andExpect(jsonPath("$.code").value("BAIL_GARANT_INVALIDE"));
    }

    // ================================================================== résiliation

    @Test
    void resiliationAvecPreavis() throws Exception {
        UUID id = creer(lot, "2026-01-01", "2027-12-31");
        mvc.perform(post("/api/v1/baux/{id}/resilier", id).with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("motif", "Départ"))).andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("BAIL_NON_RESILIABLE"));
        activer(id).andExpect(status().isOk());

        mvc.perform(post("/api/v1/baux/{id}/resilier", id).with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("dateNotification", "2026-03-01")))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("BAIL_MOTIF_REQUIS"));
        mvc.perform(post("/api/v1/baux/{id}/resilier", id).with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(json("dateNotification", "2026-03-01", "dateSortie", "2026-04-01", "motif", "Départ")))
                .andExpect(status().isUnprocessableEntity()).andExpect(jsonPath("$.code").value("BAIL_PREAVIS_NON_RESPECTE"));
        mvc.perform(post("/api/v1/baux/{id}/resilier", id).with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(json("dateNotification", "2026-03-01", "dateSortie", "2026-04-01", "preavisReduitAccepte", true, "motif", "Accord amiable")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.statut").value("RESILIE")).andExpect(jsonPath("$.dateFinEffective").value("2026-04-01"));
        // la période libérée peut accueillir un autre bail
        UUID suivant = creer(lot, "2026-04-02", null);
        activer(suivant).andExpect(status().isOk());
    }

    @Test
    void sortieParDefautEgaleNotificationPlusPreavis() throws Exception {
        UUID id = creer(lot, "2026-01-01", null);
        activer(id).andExpect(status().isOk());
        mvc.perform(post("/api/v1/baux/{id}/resilier", id).with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(json("dateNotification", "2026-03-15", "motif", "Départ")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.dateSortie").value("2026-06-15"));
    }

    // ================================================================== renouvellement

    @Test
    void renouvellementCommenceLeLendemainSansChevauchement() throws Exception {
        UUID id = creer(lot, "2026-01-01", "2026-12-31");
        mvc.perform(post("/api/v1/baux/{id}/renouveler", id).with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json()))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("BAIL_NON_RENOUVELABLE"));
        activer(id).andExpect(status().isOk());

        JsonNode nouveau = corps(mvc.perform(post("/api/v1/baux/{id}/renouveler", id).with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(json("dateFin", "2027-12-31", "loyer", 160000)))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.statut").value("BROUILLON")).andExpect(jsonPath("$.dateDebut").value("2027-01-01"))
                .andExpect(jsonPath("$.loyer").value(160000)).andExpect(jsonPath("$.charges").value(10000))
                .andExpect(jsonPath("$.bailPrecedentId").value(id.toString())));
        mvc.perform(post("/api/v1/baux/{id}/renouveler", id).with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json()))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("BAIL_DEJA_RENOUVELE"));
        activer(UUID.fromString(nouveau.path("id").asText())).andExpect(status().isOk());
    }

    @Test
    void finDeBailTermineEtLibereLeLotSaufRenouvellement() throws Exception {
        UUID ancien = creer(lot, "2020-01-01", "2020-12-31");
        activer(ancien).andExpect(status().isOk());
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> assertThat(statutLot()).isEqualTo("OCCUPE"));

        assertThat(service.mettreAJourStatuts()).isEqualTo(1);
        assertThat(service.mettreAJourStatuts()).isZero(); // idempotent
        mvc.perform(get("/api/v1/baux/{id}", ancien).with(ADMIN)).andExpect(jsonPath("$.statut").value("TERMINE"));
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> assertThat(statutLot()).isEqualTo("LIBRE"));
    }

    @Test
    void finDeBailAvecSuccesseurGardeLeLotOccupe() throws Exception {
        UUID ancien = creer(lot, "2020-01-01", "2020-12-31");
        activer(ancien).andExpect(status().isOk());
        UUID suivant = creer(lot, "2021-01-01", null);
        activer(suivant).andExpect(status().isOk());
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> assertThat(statutLot()).isEqualTo("OCCUPE"));
        service.mettreAJourStatuts();
        Thread.sleep(1500);
        assertThat(statutLot()).isEqualTo("OCCUPE");
    }

    // ================================================================== PDF

    @Test
    void apercuPdfContientLesConditionsEtLeModeleEstModifiable() throws Exception {
        UUID id = creer(lot, "2026-01-01", "2026-12-31");
        byte[] pdf = mvc.perform(get("/api/v1/baux/{id}/apercu", id).with(GESTIONNAIRE)).andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_PDF))
                .andReturn().getResponse().getContentAsByteArray();
        String texte = texte(pdf);
        assertThat(texte).contains("CONTRAT DE BAIL").contains("PROJET DE BAIL").contains("Paul Mbarga").contains("CNI-998877")
                .contains("Résidence Les Palmiers").contains("150 000 FCFA".replace(' ', ' ')).contains("Article 3");

        mvc.perform(put("/api/v1/baux/modele").with(GESTIONNAIRE).contentType(MediaType.APPLICATION_JSON).content(json("bailleurNom", "X", "preambule", "P", "clauses", "C")))
                .andExpect(status().isForbidden());
        mvc.perform(put("/api/v1/baux/modele").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(json("bailleurNom", "SCI Les Baobabs", "preambule", "Préambule maison.", "clauses", "Clause unique : loyer {{LOYER}} pour {{LOCATAIRE}}. <b>{{INCONNUE_OK}}</b>")))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("BAIL_MODELE_VARIABLE_INCONNUE"));
        mvc.perform(put("/api/v1/baux/modele").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(json("bailleurNom", "SCI Les Baobabs", "preambule", "Préambule maison.", "clauses", "Clause unique : loyer {{LOYER}} pour {{LOCATAIRE}}. <b>& ${7*7}</b>")))
                .andExpect(status().isOk());
        String apres = texte(mvc.perform(get("/api/v1/baux/{id}/apercu", id).with(ADMIN)).andReturn().getResponse().getContentAsByteArray());
        assertThat(apres).contains("SCI Les Baobabs").contains("Clause unique : loyer 150 000 FCFA pour Paul Mbarga.")
                .contains("<b>& ${7*7}</b>").doesNotContain("Article 3");
    }

    @Test
    void exemplairePdfEnregistreChiffreEtTelechargeable() throws Exception {
        UUID id = creer(lot, "2026-01-01", "2026-12-31");
        mvc.perform(post("/api/v1/baux/{id}/documents", id).with(ADMIN)).andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("BAIL_BROUILLON_SANS_DOCUMENT"));
        activer(id).andExpect(status().isOk());
        JsonNode v2 = corps(mvc.perform(post("/api/v1/baux/{id}/documents", id).with(ADMIN)).andExpect(status().isCreated()).andExpect(jsonPath("$.version").value(2)));
        UUID fichier = UUID.fromString(v2.path("fichierId").asText());

        byte[] pdf = mvc.perform(get("/api/v1/fichiers/{id}", fichier).with(GESTIONNAIRE)).andExpect(status().isOk()).andReturn().getResponse().getContentAsByteArray();
        assertThat(texte(pdf)).contains("Paul Mbarga").doesNotContain("PROJET DE BAIL");
        // locataire sans lien : refusé
        mvc.perform(get("/api/v1/fichiers/{id}", fichier).with(jwtRole(Role.LOCATAIRE))).andExpect(status().is4xxClientError());
    }

    private static String texte(byte[] pdf) throws Exception {
        try (var doc = PDDocument.load(pdf)) {
            return new PDFTextStripper().getText(doc).replace(' ', ' ');
        }
    }

    // ================================================================== rôles

    @Test
    void roles() throws Exception {
        UUID id = creer(lot, "2026-01-01", "2026-12-31");
        RequestPostProcessor proprio = jwtRole(Role.PROPRIETAIRE_TIERS);
        RequestPostProcessor presta = jwtRole(Role.PRESTATAIRE);
        for (RequestPostProcessor interdit : new RequestPostProcessor[] {proprio, presta, jwtRole(Role.LOCATAIRE)}) {
            mvc.perform(post("/api/v1/baux").with(interdit).contentType(MediaType.APPLICATION_JSON).content(bail(lot, locataire, "2027-01-01", null)))
                    .andExpect(status().isForbidden()).andExpect(jsonPath("$.code").value("ACCES_REFUSE"));
            mvc.perform(post("/api/v1/baux/{id}/activer", id).with(interdit)).andExpect(status().isForbidden());
            mvc.perform(get("/api/v1/baux/{id}/apercu", id).with(interdit)).andExpect(status().isForbidden());
        }
        mvc.perform(get("/api/v1/baux").with(proprio)).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/baux").with(GESTIONNAIRE)).andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(1));
        mvc.perform(get("/api/v1/baux")).andExpect(status().isUnauthorized());
    }

    @Test
    void unLocataireNeVoitQueSesBaux() throws Exception {
        UUID compte = utilisateurs.creer(new UtilisateurService.Saisie("Compte loc", "+237" + TEL.incrementAndGet(), null, Role.LOCATAIRE, true, false), "Motdepasse123").id();
        UUID mien = nouveauLocataire(compte);
        UUID baileMien = id(creerBrut(bail(lot, mien, "2026-01-01", "2026-12-31")).andExpect(status().isCreated()));
        UUID autreLot = id(mvc.perform(post("/api/v1/lots").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("immeubleId", jdbc.queryForObject("SELECT immeuble_id FROM lot WHERE id = ?", UUID.class, lot).toString(), "reference", "C3", "type", "STUDIO"))));
        UUID baiAutre = creer(autreLot, "2026-01-01", "2026-12-31");
        RequestPostProcessor loc = jwtRole(Role.LOCATAIRE, compte);

        mvc.perform(get("/api/v1/baux").with(loc)).andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].id").value(baileMien.toString()));
        mvc.perform(get("/api/v1/baux/{id}", baileMien).with(loc)).andExpect(status().isOk());
        mvc.perform(get("/api/v1/baux/{id}", baiAutre).with(loc)).andExpect(status().isNotFound());
        mvc.perform(get("/api/v1/baux?locataireId=" + locataire).with(loc)).andExpect(jsonPath("$.totalElements").value(1));

        // il télécharge son bail signé, pas celui des autres
        activer(baileMien).andExpect(status().isOk());
        UUID fichier = UUID.fromString(corps(mvc.perform(get("/api/v1/baux/{id}/documents", baileMien).with(loc)).andExpect(status().isOk())).path(0).path("fichierId").asText());
        mvc.perform(get("/api/v1/fichiers/{id}", fichier).with(loc)).andExpect(status().isOk());
        mvc.perform(get("/api/v1/baux/{id}/documents", baiAutre).with(loc)).andExpect(status().isNotFound());
    }
}
