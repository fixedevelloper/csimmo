package com.immogest.app.tiers;

import static com.immogest.app.securite.SecuriteTestSupport.json;
import static com.immogest.app.securite.SecuriteTestSupport.jwtRole;
import static com.immogest.app.securite.SecuriteTestSupport.telephoneUnique;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.immogest.app.IntegrationTest;
import com.immogest.app.securite.Role;
import com.immogest.app.securite.UtilisateurService;
import com.immogest.app.shared.FileStorage;
import com.immogest.app.stockage.StockageTestSupport;
import com.immogest.app.tiers.api.LecteurTiers;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

@IntegrationTest
class TiersApiTest {

    private static final RequestPostProcessor ADMIN = jwtRole(Role.ADMIN, UUID.fromString("00000000-0000-0000-0000-0000000000b1"));
    private static final RequestPostProcessor GESTIONNAIRE = jwtRole(Role.GESTIONNAIRE);
    private static final String CNI = "CNI-123456789-X";

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    JdbcTemplate jdbc;
    @Autowired
    UtilisateurService utilisateurs;
    @Autowired
    LecteurTiers lecteur;
    @Autowired
    FileStorage stockage;

    @BeforeEach
    void vider() {
        jdbc.execute("TRUNCATE tiers_piece, garant, locataire, mandat, proprietaire_tiers, prestataire CASCADE");
    }

    private JsonNode corps(ResultActions r) throws Exception {
        return mapper.readTree(r.andReturn().getResponse().getContentAsString());
    }

    private ResultActions creerProprietaire(String nom, String telephone, Object... extra) throws Exception {
        Object[] champs = new Object[4 + extra.length];
        champs[0] = "nom";
        champs[1] = nom;
        champs[2] = "telephone";
        champs[3] = telephone;
        System.arraycopy(extra, 0, champs, 4, extra.length);
        return mvc.perform(post("/api/v1/proprietaires-tiers").with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json(champs)));
    }

    private UUID proprietaire(String nom) throws Exception {
        return UUID.fromString(corps(creerProprietaire(nom, telephoneUnique())).path("id").asText());
    }

    private ResultActions creerLocataireBrut(String nom, String telephone, Object... extra) throws Exception {
        Object[] champs = new Object[4 + extra.length];
        champs[0] = "nom";
        champs[1] = nom;
        champs[2] = "telephone";
        champs[3] = telephone;
        System.arraycopy(extra, 0, champs, 4, extra.length);
        return mvc.perform(post("/api/v1/locataires").with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json(champs)));
    }

    private UUID locataire(String nom) throws Exception {
        return UUID.fromString(corps(creerLocataireBrut(nom, telephoneUnique())).path("id").asText());
    }

    private ResultActions mandat(UUID proprietaire, int taux, String debut, String fin) throws Exception {
        return mvc.perform(post("/api/v1/proprietaires-tiers/{id}/mandats", proprietaire).with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(fin == null ? json("tauxHonorairesBp", taux, "dateDebut", debut) : json("tauxHonorairesBp", taux, "dateDebut", debut, "dateFin", fin)));
    }

    private UUID utilisateur(Role role) {
        return utilisateurs.creer(new UtilisateurService.Saisie("Compte " + role, telephoneUnique(), null, role, true, false), "Motdepasse123").id();
    }

    // ================================================================== propriétaires

    @Test
    void creerLireModifierArchiverUnProprietaire() throws Exception {
        String reponse = creerProprietaire("  Mme Ngono ", " +237 699 11 22 33 ", "email", "NGONO@Exemple.CM", "modeReversement", "MOBILE_MONEY",
                "coordonneesReversement", "MTN 677000111")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nom").value("Mme Ngono"))
                .andExpect(jsonPath("$.telephone").value("+237699112233"))
                .andExpect(jsonPath("$.email").value("ngono@exemple.cm"))
                .andExpect(jsonPath("$.modeReversement").value("MOBILE_MONEY"))
                .andReturn().getResponse().getContentAsString();
        UUID id = UUID.fromString(mapper.readTree(reponse).path("id").asText());

        mvc.perform(put("/api/v1/proprietaires-tiers/{id}", id).with(GESTIONNAIRE).contentType(MediaType.APPLICATION_JSON)
                        .content(json("nom", "Mme Ngono Épouse Biya", "email", "ngono@exemple.cm")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.nom").value("Mme Ngono Épouse Biya"))
                .andExpect(jsonPath("$.modeReversement").value("VIREMENT"));

        mvc.perform(delete("/api/v1/proprietaires-tiers/{id}", id).with(ADMIN)).andExpect(status().isNoContent());
        mvc.perform(get("/api/v1/proprietaires-tiers").with(ADMIN)).andExpect(jsonPath("$.totalElements").value(0));
        mvc.perform(get("/api/v1/proprietaires-tiers").with(ADMIN).param("inclureArchives", "true")).andExpect(jsonPath("$.totalElements").value(1));
        mvc.perform(put("/api/v1/proprietaires-tiers/{id}", id).with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(json("nom", "X", "email", "x@x.cm")))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("PROPRIETAIRE_ARCHIVE"));
        assertThat(jdbc.queryForObject("SELECT count(*) FROM proprietaire_tiers", Integer.class)).isEqualTo(1);
    }

    @Test
    void donneesInvalidesDUnProprietaire() throws Exception {
        mvc.perform(post("/api/v1/proprietaires-tiers").with(ADMIN).contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("VALIDATION_ERREUR"));
        mvc.perform(post("/api/v1/proprietaires-tiers").with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("nom", "X")))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("CONTACT_REQUIS"));
        creerProprietaire("X", "abc").andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("TELEPHONE_INVALIDE"));
        creerProprietaire("X", telephoneUnique(), "email", "pas-un-email").andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("EMAIL_INVALIDE"));
        mvc.perform(get("/api/v1/proprietaires-tiers/{id}", UUID.randomUUID()).with(ADMIN))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value("PROPRIETAIRE_INTROUVABLE"));
        assertThat(jdbc.queryForObject("SELECT count(*) FROM proprietaire_tiers", Integer.class)).isZero();
    }

    @Test
    void rattacherUnCompteExigeLeBonRoleEtUneSeuleFiche() throws Exception {
        UUID compte = utilisateur(Role.PROPRIETAIRE_TIERS);

        creerProprietaire("Mauvais rôle", telephoneUnique(), "utilisateurId", utilisateur(Role.LOCATAIRE).toString())
                .andExpect(status().isUnprocessableEntity()).andExpect(jsonPath("$.code").value("TIERS_UTILISATEUR_ROLE_INVALIDE"));
        creerProprietaire("Inconnu", telephoneUnique(), "utilisateurId", UUID.randomUUID().toString())
                .andExpect(status().isNotFound());
        creerProprietaire("Bon", telephoneUnique(), "utilisateurId", compte.toString()).andExpect(status().isCreated())
                .andExpect(jsonPath("$.utilisateurId").value(compte.toString()));
        creerProprietaire("Doublon", telephoneUnique(), "utilisateurId", compte.toString())
                .andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("TIERS_UTILISATEUR_DEJA_LIE"));
        assertThat(lecteur.proprietaireDeUtilisateur(compte)).isPresent();
    }

    // ================================================================== mandats

    @Test
    void creerEtModifierUnMandatEnPointsDeBase() throws Exception {
        UUID p = proprietaire("Mandant");

        String reponse = mandat(p, 1000, "2026-01-01", "2026-12-31").andExpect(status().isCreated())
                .andExpect(jsonPath("$.tauxHonorairesBp").value(1000)).andExpect(jsonPath("$.dateFin").value("2026-12-31"))
                .andReturn().getResponse().getContentAsString();
        UUID id = UUID.fromString(mapper.readTree(reponse).path("id").asText());

        mvc.perform(put("/api/v1/mandats/{id}", id).with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(json("tauxHonorairesBp", 750, "dateDebut", "2026-01-01")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.tauxHonorairesBp").value(750)).andExpect(jsonPath("$.dateFin").doesNotExist());
        mvc.perform(get("/api/v1/proprietaires-tiers/{id}/mandats", p).with(ADMIN)).andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void tauxEtDatesInvalidesRefuses() throws Exception {
        UUID p = proprietaire("Mandant");

        mandat(p, 10_001, "2026-01-01", null).andExpect(status().isBadRequest());
        mandat(p, -1, "2026-01-01", null).andExpect(status().isBadRequest());
        mandat(p, 1000, "2026-06-01", "2026-05-31").andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("MANDAT_DATES_INVALIDES"));
        mvc.perform(post("/api/v1/proprietaires-tiers/{id}/mandats", p).with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("tauxHonorairesBp", 1000))).andExpect(status().isBadRequest());
        mandat(p, 0, "2026-01-01", null).andExpect(status().isCreated()); // 0 % et 100 % sont valides
        assertThat(jdbc.queryForObject("SELECT count(*) FROM mandat", Integer.class)).isEqualTo(1);
    }

    @Test
    void deuxMandatsDUnMemeProprietaireNeSeChevauchentJamais() throws Exception {
        UUID p = proprietaire("Mandant");
        mandat(p, 1000, "2026-01-01", "2026-06-30").andExpect(status().isCreated());

        mandat(p, 800, "2026-06-30", "2026-12-31").andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("MANDAT_CHEVAUCHEMENT"));
        mandat(p, 800, "2025-12-01", "2026-01-01").andExpect(status().isConflict());
        mandat(p, 800, "2026-03-01", "2026-04-01").andExpect(status().isConflict());
        mandat(p, 800, "2025-01-01", null).andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("MANDAT_CHEVAUCHEMENT"));

        mandat(p, 800, "2026-07-01", null).andExpect(status().isCreated()); // le lendemain : accepté, sans limite de fin
        mandat(p, 600, "2030-01-01", null).andExpect(status().isConflict()); // un mandat sans fin couvre tout le futur
        mandat(proprietaire("Autre mandant"), 800, "2026-03-01", null).andExpect(status().isCreated()); // autre propriétaire : libre
    }

    @Test
    void modifierUnMandatNeSeChevauchePasLuiMemeMaisPasUnAutre() throws Exception {
        UUID p = proprietaire("Mandant");
        UUID premier = UUID.fromString(corps(mandat(p, 1000, "2026-01-01", "2026-06-30")).path("id").asText());
        mandat(p, 900, "2026-07-01", "2026-12-31").andExpect(status().isCreated());

        mvc.perform(put("/api/v1/mandats/{id}", premier).with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(json("tauxHonorairesBp", 1100, "dateDebut", "2026-01-01", "dateFin", "2026-06-30")))
                .andExpect(status().isOk());
        mvc.perform(put("/api/v1/mandats/{id}", premier).with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(json("tauxHonorairesBp", 1100, "dateDebut", "2026-01-01", "dateFin", "2026-07-15")))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("MANDAT_CHEVAUCHEMENT"));
    }

    @Test
    void laBaseRefuseElleAussiUnChevauchement() throws Exception {
        UUID p = proprietaire("Mandant");
        mandat(p, 1000, "2026-01-01", "2026-06-30").andExpect(status().isCreated());

        assertThatThrownBy(() -> jdbc.update("INSERT INTO mandat (id, proprietaire_id, taux_honoraires_bp, date_debut, date_fin, created_at, updated_at) "
                + "VALUES (gen_random_uuid(), ?, 500, DATE '2026-03-01', NULL, now(), now())", p))
                .isInstanceOf(DataIntegrityViolationException.class).hasRootCauseInstanceOf(SQLException.class);
    }

    @Test
    void lApiDesAutresModulesDonneLeMandatEnVigueur() throws Exception {
        UUID p = proprietaire("Mandant");
        mandat(p, 1000, "2026-01-01", "2026-06-30");
        mandat(p, 800, "2026-07-01", null);

        assertThat(lecteur.mandatEnVigueur(p, LocalDate.of(2026, 3, 15))).get().extracting("tauxHonorairesBp").isEqualTo(1000);
        assertThat(lecteur.mandatEnVigueur(p, LocalDate.of(2026, 6, 30))).get().extracting("tauxHonorairesBp").isEqualTo(1000);
        assertThat(lecteur.mandatEnVigueur(p, LocalDate.of(2026, 7, 1))).get().extracting("tauxHonorairesBp").isEqualTo(800);
        assertThat(lecteur.mandatEnVigueur(p, LocalDate.of(2040, 1, 1))).get().extracting("tauxHonorairesBp").isEqualTo(800);
        assertThat(lecteur.mandatEnVigueur(p, LocalDate.of(2025, 12, 31))).isEmpty();
    }

    @Test
    void pasDeMandatPourUnProprietaireArchiveOuInconnu() throws Exception {
        UUID p = proprietaire("Mandant");
        mvc.perform(delete("/api/v1/proprietaires-tiers/{id}", p).with(ADMIN)).andExpect(status().isNoContent());

        mandat(p, 1000, "2026-01-01", null).andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("PROPRIETAIRE_ARCHIVE"));
        mandat(UUID.randomUUID(), 1000, "2026-01-01", null).andExpect(status().isNotFound());
        mvc.perform(get("/api/v1/mandats/{id}", UUID.randomUUID()).with(ADMIN)).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("MANDAT_INTROUVABLE"));
    }

    // ================================================================== locataires, CNI chiffrée

    @Test
    void leNumeroDeCniEstChiffreEnBaseEtRestitueSurLaFicheSeulement() throws Exception {
        String reponse = creerLocataireBrut("Paul Mbarga", telephoneUnique(), "numeroCni", CNI, "profession", "Enseignant", "dateNaissance", "1990-05-12")
                .andExpect(status().isCreated()).andExpect(jsonPath("$.numeroCni").value(CNI))
                .andReturn().getResponse().getContentAsString();
        UUID id = UUID.fromString(mapper.readTree(reponse).path("id").asText());

        String enBase = jdbc.queryForObject("SELECT numero_cni_chiffre FROM locataire WHERE id = ?", String.class, id);
        assertThat(enBase).isNotBlank().doesNotContain(CNI).doesNotContain("123456789");
        assertThat(jdbc.queryForObject("SELECT count(*) FROM locataire WHERE numero_cni_chiffre LIKE '%123456789%'", Integer.class)).isZero();

        mvc.perform(get("/api/v1/locataires/{id}", id).with(GESTIONNAIRE)).andExpect(jsonPath("$.numeroCni").value(CNI));
        String liste = mvc.perform(get("/api/v1/locataires").with(GESTIONNAIRE)).andReturn().getResponse().getContentAsString();
        assertThat(liste).contains("Paul Mbarga").doesNotContain(CNI).doesNotContain("numeroCniChiffre");
        assertThat(mapper.readTree(liste).path("content").get(0).path("numeroCni").isNull()).isTrue();

        // deux chiffrements du même numéro donnent deux textes différents (IV aléatoire)
        UUID autre = UUID.fromString(corps(creerLocataireBrut("Autre", telephoneUnique(), "numeroCni", CNI)).path("id").asText());
        assertThat(jdbc.queryForObject("SELECT numero_cni_chiffre FROM locataire WHERE id = ?", String.class, autre)).isNotEqualTo(enBase);
    }

    @Test
    void modifierEffacerOuRemplacerLeNumeroDeCni() throws Exception {
        UUID id = UUID.fromString(corps(creerLocataireBrut("Paul", telephoneUnique(), "numeroCni", CNI)).path("id").asText());

        mvc.perform(put("/api/v1/locataires/{id}", id).with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(json("nom", "Paul", "telephone", "+237699000111", "numeroCni", "NOUVEAU-77")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.numeroCni").value("NOUVEAU-77"));
        mvc.perform(put("/api/v1/locataires/{id}", id).with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(json("nom", "Paul", "telephone", "+237699000111")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.numeroCni").doesNotExist());
        assertThat(jdbc.queryForObject("SELECT numero_cni_chiffre FROM locataire WHERE id = ?", String.class, id)).isNull();
    }

    @Test
    void validationEtArchivageDUnLocataire() throws Exception {
        creerLocataireBrut("", telephoneUnique()).andExpect(status().isBadRequest());
        mvc.perform(post("/api/v1/locataires").with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("nom", "Sans contact")))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("CONTACT_REQUIS"));
        creerLocataireBrut("Futur", telephoneUnique(), "dateNaissance", LocalDate.now().plusDays(1).toString())
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("LOCATAIRE_DATE_NAISSANCE_INVALIDE"));
        creerLocataireBrut("Mauvais compte", telephoneUnique(), "utilisateurId", utilisateur(Role.PRESTATAIRE).toString())
                .andExpect(status().isUnprocessableEntity());

        UUID id = locataire("À archiver");
        mvc.perform(delete("/api/v1/locataires/{id}", id).with(ADMIN)).andExpect(status().isNoContent());
        mvc.perform(get("/api/v1/locataires/{id}", id).with(ADMIN)).andExpect(jsonPath("$.archive").value(true));
        mvc.perform(delete("/api/v1/locataires/{id}", id).with(ADMIN)).andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("LOCATAIRE_ARCHIVE"));
    }

    // ================================================================== garants

    @Test
    void garantsDUnLocataire() throws Exception {
        UUID loc = locataire("Paul");

        String reponse = mvc.perform(post("/api/v1/locataires/{id}/garants", loc).with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(json("nom", "Marie Mbarga", "lien", "Mère", "telephone", "+237 677 00 11 22", "numeroCni", "GAR-555")))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.telephone").value("+237677001122"))
                .andExpect(jsonPath("$.numeroCni").value("GAR-555")).andReturn().getResponse().getContentAsString();
        UUID garant = UUID.fromString(mapper.readTree(reponse).path("id").asText());
        assertThat(jdbc.queryForObject("SELECT numero_cni_chiffre FROM garant WHERE id = ?", String.class, garant)).doesNotContain("GAR-555");

        mvc.perform(post("/api/v1/locataires/{id}/garants", loc).with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("nom", "Sans téléphone"))).andExpect(status().isBadRequest());
        mvc.perform(get("/api/v1/locataires/{id}/garants", loc).with(ADMIN)).andExpect(jsonPath("$.length()").value(1));

        mvc.perform(put("/api/v1/garants/{id}", garant).with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(json("nom", "Marie M.", "telephone", "+237677001122")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.nom").value("Marie M."));
        mvc.perform(delete("/api/v1/garants/{id}", garant).with(ADMIN)).andExpect(status().isNoContent());
        mvc.perform(get("/api/v1/locataires/{id}/garants", loc).with(ADMIN)).andExpect(jsonPath("$.length()").value(0));
        mvc.perform(post("/api/v1/locataires/{id}/garants", UUID.randomUUID()).with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("nom", "X", "telephone", "+237677001122"))).andExpect(status().isNotFound());
    }

    // ================================================================== pièces d'identité chiffrées

    private ResultActions envoyerPiece(String cible, UUID id, byte[] contenu, String nom, String type, RequestPostProcessor auth) throws Exception {
        return mvc.perform(multipart("/api/v1/{cible}/{id}/pieces", cible, id)
                .file(new MockMultipartFile("fichier", nom, "image/png", contenu)).param("type", type).with(auth));
    }

    @Test
    void laPieceEstChiffreeDansLeBucketEtRestitueeParLApiAuxPersonnesAutorisees() throws Exception {
        UUID compte = utilisateur(Role.LOCATAIRE);
        UUID loc = UUID.fromString(corps(creerLocataireBrut("Paul", telephoneUnique(), "utilisateurId", compte.toString())).path("id").asText());
        byte[] png = StockageTestSupport.png(300, 200);

        JsonNode piece = corps(envoyerPiece("locataires", loc, png, "cni-recto.png", "CNI_RECTO", ADMIN)
                .andExpect(status().isCreated()).andExpect(jsonPath("$.typePiece").value("CNI_RECTO")).andExpect(jsonPath("$.cibleType").value("LOCATAIRE")));
        String fichierId = piece.path("fichierId").asText();

        // Dans le bucket : illisible (pas d'en-tête PNG), alors que la base le marque chiffré
        assertThat(jdbc.queryForObject("SELECT chiffre FROM fichier WHERE id = ?::uuid", Boolean.class, fichierId)).isTrue();
        String cle = jdbc.queryForObject("SELECT cle FROM fichier WHERE id = ?::uuid", String.class, fichierId);
        assertThat(cle).startsWith("tiers/locataire/" + loc + "/").endsWith(".png").doesNotContain("cni-recto");
        try (var flux = stockage.get(cle)) {
            byte[] brut = flux.readAllBytes();
            assertThat(brut).isNotEqualTo(png);
            assertThat(new String(brut, 1, 3, java.nio.charset.StandardCharsets.ISO_8859_1)).isNotEqualTo("PNG");
        }

        // Restituée en clair par l'API : gestionnaire, administrateur et le locataire concerné
        for (RequestPostProcessor auth : new RequestPostProcessor[] {GESTIONNAIRE, ADMIN, jwtRole(Role.LOCATAIRE, compte)}) {
            byte[] recu = mvc.perform(get("/api/v1/fichiers/{id}", fichierId).with(auth)).andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsByteArray();
            assertThat(recu).isEqualTo(png);
        }
    }

    @Test
    void personneDAutreNePeutLirelaPieceDUnLocataire() throws Exception {
        UUID compte = utilisateur(Role.LOCATAIRE);
        UUID loc = UUID.fromString(corps(creerLocataireBrut("Paul", telephoneUnique(), "utilisateurId", compte.toString())).path("id").asText());
        String fichierId = corps(envoyerPiece("locataires", loc, StockageTestSupport.png(20, 20), "p.png", "CNI_VERSO", ADMIN)).path("fichierId").asText();
        UUID garant = UUID.fromString(corps(mvc.perform(post("/api/v1/locataires/{id}/garants", loc).with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("nom", "G", "telephone", "+237677001122")))).path("id").asText());
        String fichierGarant = corps(envoyerPiece("garants", garant, StockageTestSupport.png(20, 20), "g.png", "CNI_RECTO", ADMIN)).path("fichierId").asText();

        for (RequestPostProcessor intrus : new RequestPostProcessor[] {jwtRole(Role.LOCATAIRE), jwtRole(Role.PROPRIETAIRE_TIERS), jwtRole(Role.PRESTATAIRE)}) {
            mvc.perform(get("/api/v1/fichiers/{id}", fichierId).with(intrus)).andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.code").value("FICHIER_ACCES_REFUSE"));
            mvc.perform(get("/api/v1/fichiers/{id}", fichierGarant).with(intrus)).andExpect(status().isForbidden());
        }
        mvc.perform(get("/api/v1/fichiers/{id}", fichierId)).andExpect(status().isUnauthorized());
        // Le locataire concerné lit aussi la pièce de son garant
        mvc.perform(get("/api/v1/fichiers/{id}", fichierGarant).with(jwtRole(Role.LOCATAIRE, compte))).andExpect(status().isOk());
    }

    @Test
    void piecesRefuseesOuSupprimees() throws Exception {
        UUID loc = locataire("Paul");
        int fichiersAvant = jdbc.queryForObject("SELECT count(*) FROM fichier", Integer.class);

        envoyerPiece("locataires", loc, "texte déguisé".getBytes(), "faux.png", "CNI_RECTO", ADMIN)
                .andExpect(status().isUnsupportedMediaType()).andExpect(jsonPath("$.code").value("FICHIER_TYPE_INTERDIT"));
        envoyerPiece("locataires", loc, StockageTestSupport.png(8, 8), "p.png", "AUTRE_CHOSE", ADMIN).andExpect(status().isBadRequest());
        envoyerPiece("locataires", UUID.randomUUID(), StockageTestSupport.png(8, 8), "p.png", "CNI_RECTO", ADMIN).andExpect(status().isNotFound());
        assertThat(jdbc.queryForObject("SELECT count(*) FROM fichier", Integer.class)).isEqualTo(fichiersAvant);

        JsonNode piece = corps(envoyerPiece("locataires", loc, StockageTestSupport.png(8, 8), "p.png", "PASSEPORT", ADMIN));
        mvc.perform(get("/api/v1/locataires/{id}/pieces", loc).with(ADMIN)).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(delete("/api/v1/pieces/{id}", piece.path("id").asText()).with(ADMIN)).andExpect(status().isNoContent());
        mvc.perform(get("/api/v1/fichiers/{id}", piece.path("fichierId").asText()).with(ADMIN)).andExpect(status().isNotFound());
        assertThat(jdbc.queryForObject("SELECT count(*) FROM fichier", Integer.class)).isEqualTo(fichiersAvant);
        mvc.perform(delete("/api/v1/pieces/{id}", piece.path("id").asText()).with(ADMIN)).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PIECE_INTROUVABLE"));
    }

    @Test
    void lAuditNeContientNiCniNiTelephone() throws Exception {
        UUID id = UUID.fromString(corps(creerLocataireBrut("Audité", "+237699555444", "numeroCni", CNI, "email", "audite@exemple.cm")).path("id").asText());
        envoyerPiece("locataires", id, StockageTestSupport.png(8, 8), "p.png", "CNI_RECTO", ADMIN);

        await().atMost(Duration.ofSeconds(15)).untilAsserted(() -> {
            var lignes = jdbc.queryForList("SELECT action, coalesce(avant::text, '') || coalesce(apres::text, '') AS contenu FROM audit_log WHERE entite_id = ?", id);
            assertThat(lignes).extracting(l -> l.get("action")).contains("TIERS.LOCATAIRE_CREE", "TIERS.PIECE_AJOUTEE");
            assertThat(lignes).allSatisfy(l -> assertThat((String) l.get("contenu"))
                    .doesNotContain(CNI).doesNotContain("699555444").doesNotContain("audite@exemple.cm"));
        });
    }

    // ================================================================== prestataires et recherche

    @Test
    void prestataires() throws Exception {
        String reponse = mvc.perform(post("/api/v1/prestataires").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(json("nom", "Plomberie Express", "metier", "Plombier", "telephone", "+237677123456")))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.metier").value("Plombier")).andReturn().getResponse().getContentAsString();
        UUID id = UUID.fromString(mapper.readTree(reponse).path("id").asText());

        mvc.perform(post("/api/v1/prestataires").with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json("nom", "Sans contact")))
                .andExpect(status().isBadRequest());
        mvc.perform(put("/api/v1/prestataires/{id}", id).with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(json("nom", "Plomberie Express 2", "metier", "Plombier", "telephone", "+237677123456")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.nom").value("Plomberie Express 2"));
        mvc.perform(get("/api/v1/prestataires").with(GESTIONNAIRE).param("q", "plomb")).andExpect(jsonPath("$.totalElements").value(1));
        mvc.perform(delete("/api/v1/prestataires/{id}", id).with(ADMIN)).andExpect(status().isNoContent());
        mvc.perform(get("/api/v1/prestataires").with(ADMIN)).andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void rechercheRapideParNomOuTelephone() throws Exception {
        creerProprietaire("Dupont Immobilier", "+237699111222").andExpect(status().isCreated());
        creerLocataireBrut("Jean Dupont", "+237677888999").andExpect(status().isCreated());
        mvc.perform(post("/api/v1/prestataires").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("nom", "Électricité Martin", "telephone", "+237655000111")));
        UUID archive = locataire("Dupont Archivé");
        mvc.perform(delete("/api/v1/locataires/{id}", archive).with(ADMIN));

        JsonNode parNom = corps(mvc.perform(get("/api/v1/tiers/recherche").with(GESTIONNAIRE).param("q", "dupont")));
        assertThat(parNom).extracting(n -> n.path("type").asText()).containsExactlyInAnyOrder("PROPRIETAIRE", "LOCATAIRE");

        JsonNode parTelephone = corps(mvc.perform(get("/api/v1/tiers/recherche").with(GESTIONNAIRE).param("q", "677 888")));
        assertThat(parTelephone).hasSize(1);
        assertThat(parTelephone.get(0).path("nom").asText()).isEqualTo("Jean Dupont");
        assertThat(corps(mvc.perform(get("/api/v1/tiers/recherche").with(GESTIONNAIRE).param("q", "655000")))).hasSize(1);
        assertThat(corps(mvc.perform(get("/api/v1/tiers/recherche").with(GESTIONNAIRE).param("q", "d")))).as("1 caractère : rien").isEmpty();
        assertThat(corps(mvc.perform(get("/api/v1/tiers/recherche").with(GESTIONNAIRE).param("q", "100%")))).isEmpty();
        assertThat(corps(mvc.perform(get("/api/v1/locataires").with(ADMIN).param("q", "0000")))).isNotNull();
    }

    // ================================================================== rôles

    @Test
    void seulsAdminEtGestionnaireAccedentAuxTiers() throws Exception {
        UUID p = proprietaire("P");
        UUID l = locataire("L");
        for (Role role : new Role[] {Role.LOCATAIRE, Role.PRESTATAIRE, Role.PROPRIETAIRE_TIERS}) {
            RequestPostProcessor auth = jwtRole(role);
            mvc.perform(get("/api/v1/proprietaires-tiers").with(auth)).andExpect(status().isForbidden()).andExpect(jsonPath("$.code").value("ACCES_REFUSE"));
            mvc.perform(get("/api/v1/proprietaires-tiers/{id}", p).with(auth)).andExpect(status().isForbidden());
            mvc.perform(post("/api/v1/proprietaires-tiers").with(auth).contentType(MediaType.APPLICATION_JSON).content(json("nom", "x", "email", "x@x.cm")))
                    .andExpect(status().isForbidden());
            mvc.perform(get("/api/v1/proprietaires-tiers/{id}/mandats", p).with(auth)).andExpect(status().isForbidden());
            mvc.perform(post("/api/v1/proprietaires-tiers/{id}/mandats", p).with(auth).contentType(MediaType.APPLICATION_JSON)
                    .content(json("tauxHonorairesBp", 1, "dateDebut", "2026-01-01"))).andExpect(status().isForbidden());
            mvc.perform(get("/api/v1/locataires").with(auth)).andExpect(status().isForbidden());
            mvc.perform(get("/api/v1/locataires/{id}", l).with(auth)).andExpect(status().isForbidden());
            mvc.perform(delete("/api/v1/locataires/{id}", l).with(auth)).andExpect(status().isForbidden());
            mvc.perform(get("/api/v1/locataires/{id}/garants", l).with(auth)).andExpect(status().isForbidden());
            mvc.perform(get("/api/v1/locataires/{id}/pieces", l).with(auth)).andExpect(status().isForbidden());
            mvc.perform(multipart("/api/v1/locataires/{id}/pieces", l).file(new MockMultipartFile("fichier", "p.png", "image/png", StockageTestSupport.png(8, 8)))
                    .param("type", "CNI_RECTO").with(auth)).andExpect(status().isForbidden());
            mvc.perform(delete("/api/v1/pieces/{id}", UUID.randomUUID()).with(auth)).andExpect(status().isForbidden());
            mvc.perform(get("/api/v1/prestataires").with(auth)).andExpect(status().isForbidden());
            mvc.perform(get("/api/v1/tiers/recherche").with(auth).param("q", "abc")).andExpect(status().isForbidden());
        }
        mvc.perform(get("/api/v1/proprietaires-tiers")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/v1/locataires")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/v1/tiers/recherche").param("q", "abc")).andExpect(status().isUnauthorized());
        assertThat(jdbc.queryForObject("SELECT count(*) FROM proprietaire_tiers", Integer.class)).isEqualTo(1);
        mvc.perform(get("/api/v1/locataires").with(GESTIONNAIRE)).andExpect(status().isOk());
    }

    @Test
    void lApiPourLesAutresModulesExposeLesLocataires() throws Exception {
        UUID compte = utilisateur(Role.LOCATAIRE);
        UUID loc = UUID.fromString(corps(creerLocataireBrut("Paul", "+237699000111", "utilisateurId", compte.toString())).path("id").asText());

        var resume = lecteur.locataire(loc).orElseThrow();

        assertThat(resume.nom()).isEqualTo("Paul");
        assertThat(resume.telephone()).isEqualTo("+237699000111");
        assertThat(resume.archive()).isFalse();
        assertThat(lecteur.locataireDeUtilisateur(compte)).contains(loc);
        assertThat(lecteur.locataire(UUID.randomUUID())).isEmpty();
        mvc.perform(delete("/api/v1/locataires/{id}", loc).with(ADMIN));
        assertThat(lecteur.locataireDeUtilisateur(compte)).as("un locataire archivé n'a plus d'accès").isEmpty();
    }
}
