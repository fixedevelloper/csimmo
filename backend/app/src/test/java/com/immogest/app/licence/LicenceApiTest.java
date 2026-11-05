package com.immogest.app.licence;

import static com.immogest.app.securite.SecuriteTestSupport.jwtRole;
import static com.immogest.app.securite.SecuriteTestSupport.json;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.immogest.app.securite.Role;
import com.immogest.licensing.Licence;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

class LicenceApiTest extends BaseLicenceTest {

    private static final String UTILISATEUR = """
            {"nom":"Nouveau","telephone":"+237699000111","role":"LOCATAIRE","deuxFacteursActif":false,"motDePasse":"Motdepasse123"}""";

    private ResultActions creerUtilisateur() throws Exception {
        return mvc.perform(post("/api/v1/utilisateurs").with(jwtRole(Role.ADMIN)).contentType(MediaType.APPLICATION_JSON)
                .content(UTILISATEUR));
    }

    private ResultActions importer(Role role, String contenu) throws Exception {
        return mvc.perform(post("/api/v1/licence").with(jwtRole(role)).contentType(MediaType.APPLICATION_JSON)
                .content(json("contenu", contenu)));
    }

    // ------------------------------------------------------------------ GET /api/v1/licence

    @Test
    void ladministrateurEtLeGestionnaireVoientLEtat() throws Exception {
        licences.importer(contenuValide());

        for (Role role : new Role[] {Role.ADMIN, Role.GESTIONNAIRE}) {
            mvc.perform(get("/api/v1/licence").with(jwtRole(role)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.etat").value("VALIDE"))
                    .andExpect(jsonPath("$.lectureSeule").value(false))
                    .andExpect(jsonPath("$.client").value("Client Test"))
                    .andExpect(jsonPath("$.palierLots").value(50))
                    .andExpect(jsonPath("$.joursRestants").value(365))
                    .andExpect(jsonPath("$.empreinteServeur").value(LicenceTestSupport.EMPREINTE))
                    .andExpect(jsonPath("$.fonctionnalites[0]").value("mobile"));
        }
    }

    @Test
    void sansLicenceLEtatEstAucune() throws Exception {
        mvc.perform(get("/api/v1/licence").with(jwtRole(Role.ADMIN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.etat").value("AUCUNE"))
                .andExpect(jsonPath("$.lectureSeule").value(true));
    }

    @Test
    void lesAutresRolesEtLesAnonymesNeVoientPasLEtat() throws Exception {
        for (Role role : new Role[] {Role.LOCATAIRE, Role.PRESTATAIRE, Role.PROPRIETAIRE_TIERS}) {
            mvc.perform(get("/api/v1/licence").with(jwtRole(role)))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.code").value("ACCES_REFUSE"));
        }
        mvc.perform(get("/api/v1/licence"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("NON_AUTHENTIFIE"));
    }

    // ------------------------------------------------------------------ POST /api/v1/licence

    @Test
    void ladministrateurImporteUneLicence() throws Exception {
        importer(Role.ADMIN, contenuValide())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.etat").value("VALIDE"))
                .andExpect(jsonPath("$.licenseId").value("L-TEST-1"));
    }

    @Test
    void seulLAdministrateurPeutImporter() throws Exception {
        for (Role role : new Role[] {Role.GESTIONNAIRE, Role.LOCATAIRE, Role.PRESTATAIRE, Role.PROPRIETAIRE_TIERS}) {
            importer(role, contenuValide()).andExpect(status().isForbidden()).andExpect(jsonPath("$.code").value("ACCES_REFUSE"));
        }
        mvc.perform(post("/api/v1/licence").contentType(MediaType.APPLICATION_JSON).content(json("contenu", contenuValide())))
                .andExpect(status().isUnauthorized());
        assertThat(licences.etat().etat()).as("aucun de ces essais n'a importé la licence").isEqualTo(EtatLicence.AUCUNE);
    }

    @Test
    void unImportInvalideRenvoieUnCodeMetier() throws Exception {
        importer(Role.ADMIN, signerParUnFaussaire(licenceValide()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("LICENCE_SIGNATURE_INVALIDE"));
        importer(Role.ADMIN, "n'importe quoi")
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("LICENCE_FORMAT_INVALIDE"));
        mvc.perform(post("/api/v1/licence").with(jwtRole(Role.ADMIN)).contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERREUR"));
    }

    // ------------------------------------------------------------------ filtre lecture seule

    @Test
    void avecUneLicenceValideLesEcrituresPassent() throws Exception {
        licences.importer(contenuValide());

        creerUtilisateur().andExpect(status().isCreated());
    }

    @Test
    void licenceExpireeEcritureRefuseeAvecLeCodeLicenceExpiree() throws Exception {
        licences.importer(contenuValide());
        horloge.avancerJours(366);

        creerUtilisateur()
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("LICENCE_EXPIREE"))
                .andExpect(jsonPath("$.etat").value("EXPIREE"));
        UUID id = UUID.randomUUID();
        mvc.perform(put("/api/v1/utilisateurs/{id}", id).with(jwtRole(Role.ADMIN)).contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden()).andExpect(jsonPath("$.code").value("LICENCE_EXPIREE"));
        mvc.perform(delete("/api/v1/utilisateurs/{id}", id).with(jwtRole(Role.ADMIN)))
                .andExpect(status().isForbidden()).andExpect(jsonPath("$.code").value("LICENCE_EXPIREE"));
        mvc.perform(post("/api/v1/utilisateurs/{id}/deverrouiller", id).with(jwtRole(Role.ADMIN)))
                .andExpect(status().isForbidden()).andExpect(jsonPath("$.code").value("LICENCE_EXPIREE"));
    }

    @Test
    void licenceExpireeLesLecturesRestentPossibles() throws Exception {
        licences.importer(contenuValide());
        horloge.avancerJours(366);

        mvc.perform(get("/api/v1/utilisateurs").with(jwtRole(Role.ADMIN))).andExpect(status().isOk());
        mvc.perform(get("/api/v1/health")).andExpect(status().isOk());
        mvc.perform(get("/api/v1/licence").with(jwtRole(Role.ADMIN)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.etat").value("EXPIREE"));
    }

    @Test
    void licenceExpireeLAuthentificationEtLImportRestentPossibles() throws Exception {
        licences.importer(contenuValide());
        horloge.avancerJours(366);

        // Connexion : la requête atteint le contrôleur (401 pour des identifiants inconnus, pas 403 de licence)
        mvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(json("identifiant", "inconnu@test.local", "motDePasse", "x")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("IDENTIFIANTS_INVALIDES"));
        mvc.perform(post("/api/v1/auth/logout").contentType(MediaType.APPLICATION_JSON).content(json("refreshToken", "x")))
                .andExpect(status().isNoContent());

        // Renouvellement : l'administrateur importe une nouvelle licence alors que l'ancienne a expiré
        Licence renouvelee = licence("L-RENOUVELEE", 50, aujourdhui().minusDays(1), aujourdhui().plusDays(365));
        importer(Role.ADMIN, signer(renouvelee)).andExpect(status().isOk()).andExpect(jsonPath("$.etat").value("VALIDE"));
        creerUtilisateur().andExpect(status().isCreated());
    }

    @Test
    void sansLicenceLesEcrituresSontRefuseesAussi() throws Exception {
        creerUtilisateur()
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("LICENCE_EXPIREE"))
                .andExpect(jsonPath("$.etat").value("AUCUNE"));
    }

    @Test
    void uneLicenceFalsifieeMetEnLectureSeule() throws Exception {
        licences.importer(contenuValide());
        jdbc.update("UPDATE lic_licence SET contenu = replace(contenu, '\"palierLots\" : 50', '\"palierLots\" : 5000')");
        licences.controleQuotidien();

        creerUtilisateur()
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("LICENCE_EXPIREE"))
                .andExpect(jsonPath("$.etat").value("INVALIDE"));
    }

    @Test
    void lesWebhooksSontAussiBloquesEnEcriture() throws Exception {
        // Un encaissement ne s'enregistre pas sous une licence expirée ; l'opérateur renverra la confirmation
        mvc.perform(post("/api/v1/webhooks/fournisseur").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("LICENCE_EXPIREE"));
    }
}
