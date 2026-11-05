package com.immogest.app.licence;

import static com.immogest.app.securite.SecuriteTestSupport.json;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.immogest.licensing.Licence;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

class InstallationApiTest extends BaseLicenceTest {

    private String requete(String telephone, String email, String motDePasse, String licence) throws Exception {
        Map<String, Object> admin = new LinkedHashMap<>();
        admin.put("nom", "Directeur Agence");
        if (telephone != null) {
            admin.put("telephone", telephone);
        }
        if (email != null) {
            admin.put("email", email);
        }
        admin.put("motDePasse", motDePasse);
        return json.writeValueAsString(Map.of("admin", admin, "licence", licence));
    }

    private ResultActions installer(String corps) throws Exception {
        return mvc.perform(post("/api/v1/installation").contentType(MediaType.APPLICATION_JSON).content(corps));
    }

    private int utilisateurs() {
        return jdbc.queryForObject("SELECT count(*) FROM utilisateur", Integer.class);
    }

    // ------------------------------------------------------------------ état

    @Test
    void avantLInstallationOnVoitLEmpreinteAFournirALEditeur() throws Exception {
        mvc.perform(get("/api/v1/installation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.installee").value(false))
                .andExpect(jsonPath("$.empreinteServeur").value(LicenceTestSupport.EMPREINTE));
    }

    // ------------------------------------------------------------------ parcours complet

    @Test
    void lAssistantCreeLAdminEtImporteLaLicence() throws Exception {
        installer(requete("+237699111222", "directeur@agence.cm", "Motdepasse123", contenuValide()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.installee").value(true))
                .andExpect(jsonPath("$.empreinteServeur").doesNotExist());

        assertThat(licences.etat().etat()).isEqualTo(EtatLicence.VALIDE);
        assertThat(utilisateurs()).isEqualTo(1);
        assertThat(jdbc.queryForObject("SELECT role FROM utilisateur", String.class)).isEqualTo("ADMIN");
        assertThat(nombreD(InstallationTerminee.class)).isEqualTo(1);

        // L'administrateur créé peut se connecter, et voit la licence
        String reponse = mvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(json("identifiant", "directeur@agence.cm", "motDePasse", "Motdepasse123")))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        String acces = json.readTree(reponse).path("accessToken").asText();
        mvc.perform(get("/api/v1/licence").header("Authorization", "Bearer " + acces))
                .andExpect(status().isOk()).andExpect(jsonPath("$.etat").value("VALIDE"));
    }

    @Test
    void apresLInstallationLEtatNeRevelePlusL_empreinte() throws Exception {
        installer(requete("+237699111222", null, "Motdepasse123", contenuValide())).andExpect(status().isCreated());

        mvc.perform(get("/api/v1/installation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.installee").value(true))
                .andExpect(jsonPath("$.empreinteServeur").doesNotExist());
    }

    @Test
    void lAssistantNEstUtilisableQuUneFois() throws Exception {
        String corps = requete("+237699111222", null, "Motdepasse123", contenuValide());
        installer(corps).andExpect(status().isCreated());

        installer(requete("+237699333444", "autre@agence.cm", "Autremotdepasse1", contenuValide()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("INSTALLATION_DEJA_FAITE"));
        assertThat(utilisateurs()).as("aucun second ADMIN").isEqualTo(1);
    }

    // ------------------------------------------------------------------ atomicité

    @Test
    void uneLicenceRefuseeNeCreeNiAdminNiInstallation() throws Exception {
        installer(requete("+237699111222", null, "Motdepasse123", signerParUnFaussaire(licenceValide())))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("LICENCE_SIGNATURE_INVALIDE"));

        assertThat(utilisateurs()).isZero();
        mvc.perform(get("/api/v1/installation")).andExpect(jsonPath("$.installee").value(false));
    }

    @Test
    void uneLicenceExpireeOuPourUnAutreServeurEstRefusee() throws Exception {
        installer(requete("+237699111222", null, "Motdepasse123",
                signer(licence("L-OLD", 50, aujourdhui().minusDays(400), aujourdhui().minusDays(35)))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("LICENCE_FICHIER_EXPIRE"));
        installer(requete("+237699111222", null, "Motdepasse123", signer(new Licence("L-X", "Autre", 50,
                aujourdhui().minusDays(1), aujourdhui().plusDays(100), "cd".repeat(32), List.of()))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("LICENCE_MAUVAISE_EMPREINTE"));

        assertThat(utilisateurs()).isZero();
        mvc.perform(get("/api/v1/installation")).andExpect(jsonPath("$.installee").value(false));
    }

    @Test
    void unAdminInvalideAnnuleAussiLImportDeLaLicence() throws Exception {
        // La licence est valide mais le compte ne peut pas être créé : tout est annulé, l'assistant reste ouvert
        installer(requete("pas-un-numero", null, "Motdepasse123", contenuValide()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("UTILISATEUR_TELEPHONE_INVALIDE"));

        assertThat(licences.etat().etat()).isEqualTo(EtatLicence.AUCUNE);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM lic_licence", Integer.class)).isZero();
        assertThat(utilisateurs()).isZero();
        mvc.perform(get("/api/v1/installation")).andExpect(jsonPath("$.installee").value(false));

        // Après correction, l'assistant réussit
        installer(requete("+237699111222", null, "Motdepasse123", contenuValide())).andExpect(status().isCreated());
    }

    // ------------------------------------------------------------------ validation

    @Test
    void donneesInvalidesRefusees() throws Exception {
        installer("{}").andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("VALIDATION_ERREUR"));
        installer(requete("+237699111222", null, "court", contenuValide()))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("VALIDATION_ERREUR"));
        installer(requete("+237699111222", null, "Motdepasse123", ""))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("VALIDATION_ERREUR"));
        installer(requete(null, null, "Motdepasse123", contenuValide()))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("UTILISATEUR_CONTACT_REQUIS"));

        assertThat(licences.etat().etat()).isEqualTo(EtatLicence.AUCUNE);
    }

    // ------------------------------------------------------------------ concurrence

    @Test
    void deuxAssistantsSimultanesNeReussissentPasTousLesDeux() throws Exception {
        String a = requete("+237699111222", "a@agence.cm", "Motdepasse123", contenuValide());
        String b = requete("+237699333444", "b@agence.cm", "Motdepasse456", contenuValide());
        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            List<Callable<Integer>> appels = new ArrayList<>();
            for (String corps : List.of(a, b)) {
                appels.add(() -> installer(corps).andReturn().getResponse().getStatus());
            }
            List<Integer> statuts = new ArrayList<>();
            for (Future<Integer> f : pool.invokeAll(appels)) {
                statuts.add(f.get());
            }

            assertThat(statuts).containsExactlyInAnyOrder(201, 409);
            assertThat(utilisateurs()).isEqualTo(1);
            assertThat(jdbc.queryForObject("SELECT count(*) FROM lic_licence", Integer.class)).isEqualTo(1);
        } finally {
            pool.shutdownNow();
        }
    }

    @Test
    void lEndpointInstallationEstPublic() throws Exception {
        // Aucun jeton : ni 401 ni 403 (le contrôle de licence n'a pas à bloquer l'assistant lui-même)
        mvc.perform(get("/api/v1/installation")).andExpect(status().isOk());
        installer(requete("+237699111222", null, "Motdepasse123", contenuValide())).andExpect(status().isCreated());
    }
}
