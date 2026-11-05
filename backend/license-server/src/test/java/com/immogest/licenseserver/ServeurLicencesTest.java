package com.immogest.licenseserver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.immogest.licensing.Ed25519;
import com.immogest.licensing.EmpreinteServeur;
import com.immogest.licensing.LicenceSignee;
import com.immogest.licensing.StatutLicence;
import com.immogest.licensing.VerificateurLicence;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.PublicKey;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class ServeurLicencesTest {

    static final KeyPair PAIRE = Ed25519.genererPaire();
    static final PublicKey PUBLIQUE = PAIRE.getPublic();
    static Path fichierCle;

    @DynamicPropertySource
    static void proprietes(DynamicPropertyRegistry r) throws IOException {
        fichierCle = Files.createTempFile("cle-privee", ".key");
        Files.writeString(fichierCle, Ed25519.encoder(PAIRE.getPrivate()));
        r.add("license-server.cle-privee-chemin", () -> fichierCle.toString());
        r.add("license-server.jwt-secret", () -> "secret-de-test-de-plus-de-trente-deux-caracteres");
        r.add("license-server.admin-email", () -> "editeur@immogest.test");
        r.add("license-server.admin-mot-de-passe", () -> "MotDePasseEditeur-123");
    }

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    JdbcTemplate jdbc;

    String jeton;
    final String empreinte = EmpreinteServeur.calculer("machine-A", "install-1");
    final String autreEmpreinte = EmpreinteServeur.calculer("machine-B", "install-2");

    @BeforeEach
    void connecter() throws Exception {
        jeton = "Bearer " + mapper.readTree(mvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(json("email", "editeur@immogest.test", "motDePasse", "MotDePasseEditeur-123"))).andExpect(status().isOk()).andReturn().getResponse().getContentAsString()).path("jeton").asText();
        jdbc.update("UPDATE ls_editeur SET echecs = 0, verrouille_jusqu = NULL");
    }

    String json(Object... kv) {
        java.util.Map<String, Object> m = new java.util.LinkedHashMap<>();
        for (int i = 0; i < kv.length; i += 2) {
            m.put((String) kv[i], kv[i + 1]);
        }
        try {
            return mapper.writeValueAsString(m);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    ResultActions envoyer(String methode, String url, String corps) throws Exception {
        var b = methode.equals("PUT") ? put(url) : post(url);
        return mvc.perform(b.header("Authorization", jeton).contentType(MediaType.APPLICATION_JSON).content(corps));
    }

    JsonNode lire(ResultActions r) throws Exception {
        return mapper.readTree(r.andReturn().getResponse().getContentAsString());
    }

    UUID client(String nom) throws Exception {
        return UUID.fromString(lire(envoyer("POST", "/api/v1/clients", json("nom", nom, "email", "c@x.cm", "pays", "CM")).andExpect(status().isCreated())).path("id").asText());
    }

    JsonNode emettre(UUID client, String empreinteServeur, LocalDate expiration) throws Exception {
        return lire(envoyer("POST", "/api/v1/licences", json("clientId", client.toString(), "palierLots", 50, "dateDebut", LocalDate.now().toString(), "dateExpiration", expiration.toString(),
                "empreinteServeur", empreinteServeur, "fonctionnalites", java.util.List.of("PAIEMENT_EN_LIGNE", "BASE"))).andExpect(status().isCreated()));
    }

    ResultActions verification(String licenseId, String emp) throws Exception {
        return mvc.perform(post("/api/v1/verification").contentType(MediaType.APPLICATION_JSON).content(json("licenseId", licenseId, "empreinteServeur", emp, "versionApplication", "0.9.0")));
    }

    // ------------------------------------------------------------------ vérification croisée avec le code de l'application

    @Test
    void lesFichiersEmisSontValidesPourLeVerificateurDeLApplication() throws Exception {
        UUID c = client("Agence Akwa");
        JsonNode e = emettre(c, empreinte, LocalDate.now().plusYears(1));
        String lic = e.path("fichier").asText();

        var ok = VerificateurLicence.verifier(lic, PUBLIQUE, empreinte, LocalDate.now());
        assertThat(ok.statut()).isEqualTo(StatutLicence.VALIDE);
        assertThat(ok.licence().licenseId()).isEqualTo(e.path("licence").path("id").asText());
        assertThat(ok.licence().client()).isEqualTo("Agence Akwa");
        assertThat(ok.licence().palierLots()).isEqualTo(50);
        assertThat(ok.licence().fonctionnalites()).containsExactly("BASE", "PAIEMENT_EN_LIGNE");
        // mauvaise machine, mauvaise clé publique, fichier retouché : rejetés
        assertThat(VerificateurLicence.verifier(lic, PUBLIQUE, autreEmpreinte, LocalDate.now()).estValide()).isFalse();
        assertThat(VerificateurLicence.verifier(lic, Ed25519.genererPaire().getPublic(), empreinte, LocalDate.now()).estValide()).isFalse();
        String falsifie = lic.replaceAll("\"palierLots\"\\s*:\\s*50", "\"palierLots\" : 5000");
        assertThat(falsifie).isNotEqualTo(lic); // la falsification a bien eu lieu
        assertThat(VerificateurLicence.verifier(falsifie, PUBLIQUE, empreinte, LocalDate.now()).estValide()).isFalse();
        // téléchargement du dernier fichier
        String telecharge = mvc.perform(get("/api/v1/licences/{id}/fichier", e.path("licence").path("id").asText()).header("Authorization", jeton)).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThat(telecharge).isEqualTo(lic);
        assertThat(LicenceSignee.lire(telecharge).signatureValide(PUBLIQUE)).isTrue();
    }

    // ------------------------------------------------------------------ vérification hebdomadaire

    @Test
    void verificationDesInstallations() throws Exception {
        String id = emettre(client("C1"), empreinte, LocalDate.now().plusMonths(6)).path("licence").path("id").asText();
        verification(id, empreinte).andExpect(status().isOk()).andExpect(jsonPath("$.statut").value("VALIDE")).andExpect(jsonPath("$.licenceRenouvelee").doesNotExist());
        verification(id, empreinte.toUpperCase()).andExpect(jsonPath("$.statut").value("VALIDE"));
        verification(UUID.randomUUID().toString(), empreinte).andExpect(jsonPath("$.statut").value("INCONNUE"));
        verification("pas-un-uuid", empreinte).andExpect(status().isOk()).andExpect(jsonPath("$.statut").value("INCONNUE"));
        // copie sur un autre serveur : rejetée
        verification(id, autreEmpreinte).andExpect(jsonPath("$.statut").value("REVOQUEE"));
        mvc.perform(post("/api/v1/verification").contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isBadRequest());
        // la dernière vérification et la version de l'application sont tracées
        mvc.perform(get("/api/v1/licences/{id}", id).header("Authorization", jeton)).andExpect(jsonPath("$.versionApplication").value("0.9.0")).andExpect(jsonPath("$.derniereVerification").isNotEmpty());
    }

    @Test
    void lesReponsesNeDivulguentRien() throws Exception {
        String id = emettre(client("C2"), empreinte, LocalDate.now().plusMonths(6)).path("licence").path("id").asText();
        String reponse = verification(id, autreEmpreinte).andReturn().getResponse().getContentAsString();
        assertThat(reponse).doesNotContain("C2").doesNotContain(empreinte);
        assertThat(mapper.readTree(reponse).fieldNames()).toIterable().containsExactlyInAnyOrder("statut", "licenceRenouvelee");
    }

    // ------------------------------------------------------------------ cycle de vie

    @Test
    void renouvellementLeNouveauFichierEstRemisUneFoisALInstallation() throws Exception {
        String id = emettre(client("C3"), empreinte, LocalDate.now().plusDays(20)).path("licence").path("id").asText();
        verification(id, empreinte).andExpect(jsonPath("$.licenceRenouvelee").doesNotExist());

        LocalDate nouvelle = LocalDate.now().plusYears(1);
        JsonNode r = lire(envoyer("POST", "/api/v1/licences/" + id + "/renouvellement", json("dateExpiration", nouvelle.toString(), "palierLots", 100)).andExpect(status().isOk()));
        assertThat(r.path("licence").path("dateExpiration").asText()).isEqualTo(nouvelle.toString());
        assertThat(r.path("licence").path("palierLots").asInt()).isEqualTo(100);

        String livre = lire(verification(id, empreinte).andExpect(jsonPath("$.statut").value("VALIDE"))).path("licenceRenouvelee").asText();
        assertThat(livre).isEqualTo(r.path("fichier").asText());
        var v = VerificateurLicence.verifier(livre, PUBLIQUE, empreinte, LocalDate.now());
        assertThat(v.estValide()).isTrue();
        assertThat(v.licence().dateExpiration()).isEqualTo(nouvelle);
        assertThat(v.licence().licenseId()).isEqualTo(id); // même identifiant : l'installation la reconnaît comme son renouvellement
        verification(id, empreinte).andExpect(jsonPath("$.licenceRenouvelee").doesNotExist());
        // historique complet, dans l'ordre
        mvc.perform(get("/api/v1/licences/{id}/emissions", id).header("Authorization", jeton)).andExpect(jsonPath("$.length()").value(2)).andExpect(jsonPath("$[0].motif").value("EMISSION"))
                .andExpect(jsonPath("$[1].motif").value("RENOUVELLEMENT"));
        // une expiration qui ne dépasse pas l'actuelle est refusée
        envoyer("POST", "/api/v1/licences/" + id + "/renouvellement", json("dateExpiration", nouvelle.toString())).andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("RENOUVELLEMENT_DATE_INVALIDE"));
    }

    @Test
    void transfertVersUnNouveauServeur() throws Exception {
        String id = emettre(client("C4"), empreinte, LocalDate.now().plusYears(1)).path("licence").path("id").asText();
        JsonNode t = lire(envoyer("POST", "/api/v1/licences/" + id + "/transfert", json("empreinteServeur", autreEmpreinte)).andExpect(status().isOk()));
        assertThat(t.path("licence").path("transferts").asInt()).isEqualTo(1);
        assertThat(VerificateurLicence.verifier(t.path("fichier").asText(), PUBLIQUE, autreEmpreinte, LocalDate.now()).estValide()).isTrue();
        assertThat(VerificateurLicence.verifier(t.path("fichier").asText(), PUBLIQUE, empreinte, LocalDate.now()).estValide()).isFalse();
        // l'ancien serveur n'est plus reconnu, le nouveau l'est
        verification(id, empreinte).andExpect(jsonPath("$.statut").value("REVOQUEE"));
        verification(id, autreEmpreinte).andExpect(jsonPath("$.statut").value("VALIDE"));
        envoyer("POST", "/api/v1/licences/" + id + "/transfert", json("empreinteServeur", autreEmpreinte)).andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("TRANSFERT_MEME_EMPREINTE"));
    }

    @Test
    void transfertsLimites() throws Exception {
        String id = emettre(client("C5"), empreinte, LocalDate.now().plusYears(1)).path("licence").path("id").asText();
        for (int i = 0; i < 3; i++) {
            envoyer("POST", "/api/v1/licences/" + id + "/transfert", json("empreinteServeur", EmpreinteServeur.calculer("m" + i, "i" + i))).andExpect(status().isOk());
        }
        envoyer("POST", "/api/v1/licences/" + id + "/transfert", json("empreinteServeur", EmpreinteServeur.calculer("m9", "i9"))).andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("TRANSFERT_LIMITE_ATTEINTE"));
    }

    @Test
    void revocation() throws Exception {
        String id = emettre(client("C6"), empreinte, LocalDate.now().plusYears(1)).path("licence").path("id").asText();
        envoyer("POST", "/api/v1/licences/" + id + "/revocation", json("motif", " ")).andExpect(status().isBadRequest());
        envoyer("POST", "/api/v1/licences/" + id + "/revocation", json("motif", "Impayé")).andExpect(status().isOk()).andExpect(jsonPath("$.statut").value("REVOQUEE")).andExpect(jsonPath("$.motifRevocation").value("Impayé"));
        verification(id, empreinte).andExpect(jsonPath("$.statut").value("REVOQUEE")).andExpect(jsonPath("$.licenceRenouvelee").doesNotExist());
        envoyer("POST", "/api/v1/licences/" + id + "/renouvellement", json("dateExpiration", LocalDate.now().plusYears(3).toString())).andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("LICENCE_REVOQUEE"));
        envoyer("POST", "/api/v1/licences/" + id + "/transfert", json("empreinteServeur", autreEmpreinte)).andExpect(status().isConflict());
        envoyer("POST", "/api/v1/licences/" + id + "/revocation", json("motif", "encore")).andExpect(status().isConflict());
    }

    @Test
    void emissionRefuseLesDonneesInvalides() throws Exception {
        UUID c = client("C7");
        String d = LocalDate.now().toString();
        envoyer("POST", "/api/v1/licences", json("clientId", c.toString(), "palierLots", 0, "dateDebut", d, "dateExpiration", d, "empreinteServeur", empreinte)).andExpect(status().isBadRequest());
        envoyer("POST", "/api/v1/licences", json("clientId", c.toString(), "palierLots", 5, "dateDebut", d, "dateExpiration", d, "empreinteServeur", "abc")).andExpect(status().isBadRequest());
        envoyer("POST", "/api/v1/licences", json("clientId", c.toString(), "palierLots", 5, "dateDebut", d, "dateExpiration", LocalDate.now().minusDays(1).toString(), "empreinteServeur", empreinte))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("LICENCE_INVALIDE"));
        envoyer("POST", "/api/v1/licences", json("clientId", UUID.randomUUID().toString(), "palierLots", 5, "dateDebut", d, "dateExpiration", d, "empreinteServeur", empreinte))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value("CLIENT_INTROUVABLE"));
    }

    @Test
    void licencesExpirantBientot() throws Exception {
        UUID c = client("C8 expirante");
        String proche = emettre(c, empreinte, LocalDate.now().plusDays(10)).path("licence").path("id").asText();
        String loin = emettre(c, autreEmpreinte, LocalDate.now().plusDays(200)).path("licence").path("id").asText();
        String reponse = mvc.perform(get("/api/v1/licences").param("clientId", c.toString()).param("expirantSous", "30").header("Authorization", jeton)).andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1)).andReturn().getResponse().getContentAsString();
        assertThat(reponse).contains(proche).doesNotContain(loin);
        assertThat(mapper.readTree(reponse).path("content").get(0).path("joursRestants").asLong()).isEqualTo(10);
        mvc.perform(get("/api/v1/licences").param("clientId", c.toString()).header("Authorization", jeton)).andExpect(jsonPath("$.totalElements").value(2));
    }

    // ------------------------------------------------------------------ paiements, versions, clients

    @Test
    void paiementsDeLicenceEnEcritureSeule() throws Exception {
        String id = emettre(client("C9"), empreinte, LocalDate.now().plusYears(1)).path("licence").path("id").asText();
        envoyer("POST", "/api/v1/licences/" + id + "/paiements", json("montant", 0, "mode", "VIREMENT", "datePaiement", "2026-01-10")).andExpect(status().isBadRequest());
        JsonNode p = lire(envoyer("POST", "/api/v1/licences/" + id + "/paiements", json("montant", 500000, "mode", "VIREMENT", "reference", "VIR-1", "datePaiement", "2026-01-10"))
                .andExpect(status().isCreated()));
        assertThat(p.path("enregistrePar").asText()).isEqualTo("editeur@immogest.test");
        mvc.perform(get("/api/v1/licences/{id}/paiements", id).header("Authorization", jeton)).andExpect(jsonPath("$.totalElements").value(1)).andExpect(jsonPath("$.content[0].montant").value(500000));
        // la base elle-même refuse la modification et la suppression
        assertThatThrownBy(() -> jdbc.update("UPDATE ls_paiement SET montant = 1")).hasMessageContaining("écriture seule");
        assertThatThrownBy(() -> jdbc.update("DELETE FROM ls_paiement")).hasMessageContaining("écriture seule");
        assertThatThrownBy(() -> jdbc.update("DELETE FROM ls_emission")).hasMessageContaining("écriture seule");
    }

    @Test
    void versionsPubliees() throws Exception {
        String sha = "a".repeat(64);
        String numero = "1.2." + System.nanoTime() % 100000;
        envoyer("POST", "/api/v1/versions", json("numero", numero, "notes", "Correctifs", "urlTelechargement", "http://insecure/x.tar", "sha256", sha)).andExpect(status().isBadRequest());
        envoyer("POST", "/api/v1/versions", json("numero", numero, "notes", "Correctifs", "urlTelechargement", "https://dl.immogest.example/" + numero + ".tar.gz", "sha256", "zz")).andExpect(status().isBadRequest());
        envoyer("POST", "/api/v1/versions", json("numero", numero, "notes", "Correctifs", "urlTelechargement", "https://dl.immogest.example/" + numero + ".tar.gz", "sha256", sha.toUpperCase()))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.sha256").value(sha));
        envoyer("POST", "/api/v1/versions", json("numero", numero, "urlTelechargement", "https://dl.immogest.example/x", "sha256", sha)).andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("VERSION_EXISTE"));
        // lisible sans authentification (update.sh des installations)
        mvc.perform(get("/api/v1/versions/derniere")).andExpect(status().isOk()).andExpect(jsonPath("$.numero").value(numero));
    }

    @Test
    void clients() throws Exception {
        UUID c = client("Société Zébra " + System.nanoTime());
        envoyer("PUT", "/api/v1/clients/" + c, json("nom", "Zébra renommée", "pays", "TD")).andExpect(status().isOk()).andExpect(jsonPath("$.nom").value("Zébra renommée"));
        mvc.perform(get("/api/v1/clients").param("q", "zébra renommée").header("Authorization", jeton)).andExpect(jsonPath("$.totalElements").value(1));
        envoyer("POST", "/api/v1/clients", json("nom", " ")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/v1/clients/{id}", UUID.randomUUID()).header("Authorization", jeton)).andExpect(status().isNotFound());
    }

    // ------------------------------------------------------------------ accès

    @Test
    void toutEstProtegeSaufLaVerificationEtLaDerniereVersion() throws Exception {
        for (String url : new String[] {"/api/v1/clients", "/api/v1/licences", "/api/v1/versions", "/api/v1/licences/" + UUID.randomUUID() + "/fichier"}) {
            mvc.perform(get(url)).andExpect(status().isUnauthorized());
        }
        mvc.perform(post("/api/v1/licences").contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/v1/clients").header("Authorization", "Bearer n.importe.quoi")).andExpect(status().isUnauthorized());
        // jeton signé avec un autre secret
        mvc.perform(get("/api/v1/clients").header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ4In0.AAAA")).andExpect(status().isUnauthorized());
    }

    @Test
    void connexionEtVerrouillage() throws Exception {
        mvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON).content(json("email", "inconnu@x.cm", "motDePasse", "x"))).andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("IDENTIFIANTS_INVALIDES"));
        for (int i = 0; i < 5; i++) {
            mvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON).content(json("email", "editeur@immogest.test", "motDePasse", "faux"))).andExpect(status().isUnauthorized());
        }
        mvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON).content(json("email", "editeur@immogest.test", "motDePasse", "MotDePasseEditeur-123")))
                .andExpect(status().isTooManyRequests()).andExpect(jsonPath("$.code").value("COMPTE_VERROUILLE"));
        jdbc.update("UPDATE ls_editeur SET echecs = 0, verrouille_jusqu = NULL"); // BeforeEach des autres tests le refait aussi
    }

    @Test
    void laClePriveeNeSortJamais() throws Exception {
        String id = emettre(client("C10"), empreinte, LocalDate.now().plusYears(1)).path("licence").path("id").asText();
        String cle = Ed25519.encoder(PAIRE.getPrivate());
        for (String url : new String[] {"/api/v1/licences/" + id, "/api/v1/licences/" + id + "/emissions", "/api/v1/licences/" + id + "/fichier"}) {
            assertThat(mvc.perform(get(url).header("Authorization", jeton)).andReturn().getResponse().getContentAsString()).doesNotContain(cle);
        }
    }
}
