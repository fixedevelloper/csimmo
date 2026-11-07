package com.immogest.app.securite;

import static com.immogest.app.securite.SecuriteTestSupport.json;
import static com.immogest.app.securite.SecuriteTestSupport.jwtRole;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.immogest.app.IntegrationTest;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

/** Fiche employé (ADMIN) : encaissements saisis par une personne et journal de son activité. */
@IntegrationTest
class FicheEmployeTest {

    private static final UUID ADMIN_ID = UUID.fromString("00000000-0000-0000-0000-0000000000e1");
    private static final RequestPostProcessor ADMIN = jwtRole(Role.ADMIN, ADMIN_ID);
    private static final AtomicLong TEL = new AtomicLong(630_000_000L);

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    UtilisateurService utilisateurs;

    UUID immeuble;
    UUID bail;
    UUID employe;
    RequestPostProcessor lui;

    @BeforeEach
    void preparer() throws Exception {
        String suffixe = Long.toString(System.nanoTime(), 36);
        immeuble = creer("/api/v1/immeubles", "nom", "Fiche " + suffixe, "adresse", "1 rue", "ville", "Douala");
        UUID lot = creer("/api/v1/lots", "immeubleId", immeuble.toString(), "reference", "F1-" + suffixe, "type", "STUDIO");
        UUID locataire = creer("/api/v1/locataires", "nom", "Locataire Fiche " + suffixe, "telephone", tel());
        bail = creer("/api/v1/baux", "lotId", lot.toString(), "locataireId", locataire.toString(), "dateDebut", LocalDate.now(ZoneId.of("Africa/Douala")).minusDays(40).toString(),
                "loyer", 100_000, "charges", 0, "periodicite", "MENSUELLE", "jourEcheance", 1, "moisAvance", 0);
        mvc.perform(post("/api/v1/baux/{id}/activer", bail).with(ADMIN)).andExpect(status().isOk());
        employe = nouvelEmploye();
        lui = jwtRole(Role.ENCAISSEUR, employe);
        affecter(employe, immeuble);
    }

    @Test
    void liste_les_encaissements_saisis_par_une_personne() throws Exception {
        mvc.perform(post("/api/v1/paiements").with(lui).contentType(MediaType.APPLICATION_JSON)
                .content(json("bailId", bail.toString(), "montant", 20_000, "mode", "ESPECES"))).andExpect(status().isCreated());
        mvc.perform(post("/api/v1/paiements").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("bailId", bail.toString(), "montant", 30_000, "mode", "VIREMENT", "reference", "V" + System.nanoTime()))).andExpect(status().isCreated());

        JsonNode siens = contenu(mvc.perform(get("/api/v1/paiements").param("saisiPar", employe.toString()).param("size", "100").with(ADMIN)));
        assertThat(siens).hasSize(1);
        assertThat(siens.get(0).path("montant").asLong()).isEqualTo(20_000);
        assertThat(siens.get(0).path("saisiPar").asText()).isEqualTo(employe.toString());
        assertThat(contenu(mvc.perform(get("/api/v1/paiements").param("bailId", bail.toString()).param("size", "100").with(ADMIN)))).hasSize(2);
    }

    @Test
    void le_filtre_par_saisisseur_est_reserve_a_l_administration() throws Exception {
        mvc.perform(get("/api/v1/paiements").param("saisiPar", employe.toString()).with(jwtRole(Role.GESTIONNAIRE))).andExpect(status().isOk());
        mvc.perform(get("/api/v1/paiements").param("saisiPar", employe.toString()).with(lui)).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/paiements").param("saisiPar", UUID.randomUUID().toString()).with(jwtRole(Role.GESTIONNAIRE_BIENS, nouvelEmploye())))
                .andExpect(status().isForbidden());
    }

    @Test
    void journal_d_activite_d_une_personne_sans_valeurs_avant_apres() throws Exception {
        // l'ADMIN modifie des affectations : l'action est journalisée à son nom
        mvc.perform(put("/api/v1/utilisateurs/{id}/affectations", employe).with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("immeubleIds", List.of(immeuble.toString())))).andExpect(status().isOk());

        await().atMost(Duration.ofSeconds(20)).until(() -> contenu(mvc.perform(get("/api/v1/audit").param("auteur", ADMIN_ID.toString()).with(ADMIN)))
                .findValuesAsText("action").contains("SECURITE.AFFECTATIONS_MODIFIEES"));

        JsonNode entrees = contenu(mvc.perform(get("/api/v1/audit").param("auteur", ADMIN_ID.toString()).param("size", "100").with(ADMIN)));
        assertThat(entrees.size()).isGreaterThan(0);
        entrees.forEach(e -> {
            assertThat(e.has("avant")).isFalse();
            assertThat(e.has("apres")).isFalse();
            assertThat(e.path("date").asText()).isNotBlank();
        });
        List<String> dates = new ArrayList<>();
        entrees.forEach(e -> dates.add(e.path("date").asText()));
        assertThat(dates).isSortedAccordingTo((a, b) -> java.time.OffsetDateTime.parse(b).compareTo(java.time.OffsetDateTime.parse(a))); // plus récentes d'abord
    }

    @Test
    void le_journal_est_reserve_a_l_administrateur() throws Exception {
        mvc.perform(get("/api/v1/audit").param("auteur", ADMIN_ID.toString()).with(jwtRole(Role.GESTIONNAIRE))).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/audit").param("auteur", ADMIN_ID.toString()).with(lui)).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/audit").param("auteur", ADMIN_ID.toString()).with(jwtRole(Role.PROPRIETAIRE_TIERS))).andExpect(status().isForbidden());
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
        return utilisateurs.creer(new UtilisateurService.Saisie("Employé " + System.nanoTime(), tel(), null, Role.ENCAISSEUR, true, true),
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
