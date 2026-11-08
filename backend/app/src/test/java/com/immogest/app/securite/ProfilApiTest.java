package com.immogest.app.securite;

import static com.immogest.app.securite.SecuriteTestSupport.MOT_DE_PASSE;
import static com.immogest.app.securite.SecuriteTestSupport.emailUnique;
import static com.immogest.app.securite.SecuriteTestSupport.json;
import static com.immogest.app.securite.SecuriteTestSupport.jwtRole;
import static com.immogest.app.securite.SecuriteTestSupport.postJson;
import static com.immogest.app.securite.SecuriteTestSupport.telephoneUnique;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.immogest.app.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

/** Profil de l'utilisateur connecté : infos personnelles, mot de passe actuel exigé, activation et désactivation de la double authentification. */
@IntegrationTest
@Import(SecuriteTestSupport.class)
class ProfilApiTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    UtilisateurService service;
    @Autowired
    SecuriteTestSupport.OtpCapture sms;

    private UtilisateurDto creer(Role role, String telephone, String email, boolean deuxFacteurs) {
        return service.creer(new UtilisateurService.Saisie("Profil " + role, telephone, email, role, true, deuxFacteurs), MOT_DE_PASSE);
    }

    private static RequestPostProcessor moi(UtilisateurDto u) {
        return jwtRole(u.role(), u.id());
    }

    private ResultActions modifier(UtilisateurDto u, String nom, String telephone, String email, String motDePasse) throws Exception {
        return mvc.perform(put("/api/v1/auth/moi").with(moi(u)).contentType(MediaType.APPLICATION_JSON)
                .content(json("nom", nom, "telephone", telephone, "email", email, "motDePasse", motDePasse)));
    }

    private ResultActions poster(UtilisateurDto u, String chemin, String corps) throws Exception {
        return mvc.perform(post(chemin).with(moi(u)).contentType(MediaType.APPLICATION_JSON).content(corps));
    }

    // ------------------------------------------------------------------ infos personnelles

    @Test
    void modifie_ses_infos_avec_le_mot_de_passe_actuel() throws Exception {
        UtilisateurDto u = creer(Role.GESTIONNAIRE, telephoneUnique(), null, false);
        String nouveauTel = telephoneUnique();
        String nouvelEmail = emailUnique();
        modifier(u, "  Nouveau Nom  ", nouveauTel, nouvelEmail, MOT_DE_PASSE).andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Nouveau Nom")).andExpect(jsonPath("$.telephone").value(nouveauTel))
                .andExpect(jsonPath("$.email").value(nouvelEmail)).andExpect(jsonPath("$.role").value("GESTIONNAIRE")).andExpect(jsonPath("$.actif").value(true));
        mvc.perform(get("/api/v1/auth/moi").with(moi(u))).andExpect(status().isOk()).andExpect(jsonPath("$.nom").value("Nouveau Nom"));
    }

    @Test
    void un_mot_de_passe_faux_ne_change_rien_et_compte_comme_un_echec_de_connexion() throws Exception {
        UtilisateurDto u = creer(Role.LOCATAIRE, telephoneUnique(), null, false);
        for (int i = 0; i < 4; i++) {
            modifier(u, "Pirate", u.telephone(), null, "faux-faux-faux").andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.code").value("MOT_DE_PASSE_ACTUEL_INCORRECT"));
        }
        mvc.perform(get("/api/v1/auth/moi").with(moi(u))).andExpect(jsonPath("$.nom").value("Profil LOCATAIRE"));
        modifier(u, "Pirate", u.telephone(), null, "faux-faux-faux").andExpect(status().isLocked()); // 5e échec : verrouillage, comme à la connexion
        modifier(u, "Légitime", u.telephone(), null, MOT_DE_PASSE).andExpect(status().isLocked());
    }

    @Test
    void refuse_un_profil_sans_contact_ou_avec_un_contact_deja_pris() throws Exception {
        UtilisateurDto autre = creer(Role.LOCATAIRE, telephoneUnique(), null, false);
        UtilisateurDto u = creer(Role.LOCATAIRE, telephoneUnique(), emailUnique(), false);
        modifier(u, "Sans contact", "", "", MOT_DE_PASSE).andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("UTILISATEUR_CONTACT_REQUIS"));
        modifier(u, "Doublon", autre.telephone(), u.email(), MOT_DE_PASSE).andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("UTILISATEUR_DEJA_EXISTANT"));
        modifier(u, "Téléphone invalide", "abc", u.email(), MOT_DE_PASSE).andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("UTILISATEUR_TELEPHONE_INVALIDE"));
    }

    @Test
    void peut_retirer_son_telephone_avec_la_double_authentification_car_le_code_vient_de_l_application() throws Exception {
        UtilisateurDto u = creer(Role.GESTIONNAIRE, telephoneUnique(), emailUnique(), true);
        modifier(u, "Sans téléphone", "", u.email(), MOT_DE_PASSE).andExpect(status().isOk()).andExpect(jsonPath("$.deuxFacteursActif").value(true));
    }

    @Test
    void le_profil_ne_peut_changer_ni_le_role_ni_l_activite() throws Exception {
        UtilisateurDto u = creer(Role.LOCATAIRE, telephoneUnique(), null, false);
        // les champs « role » et « actif » ne font pas partie de la requête : ils sont ignorés
        mvc.perform(put("/api/v1/auth/moi").with(moi(u)).contentType(MediaType.APPLICATION_JSON)
                .content(json("nom", "Moi", "telephone", u.telephone(), "motDePasse", MOT_DE_PASSE, "role", "ADMIN", "actif", false)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.role").value("LOCATAIRE")).andExpect(jsonPath("$.actif").value(true));
    }

    // ------------------------------------------------------------------ authentification

    @Test
    void sans_session_tout_est_refuse() throws Exception {
        mvc.perform(put("/api/v1/auth/moi").contentType(MediaType.APPLICATION_JSON).content(json("nom", "X", "motDePasse", "y"))).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/v1/auth/moi/double-authentification/activer").contentType(MediaType.APPLICATION_JSON).content(json("code", "123456")))
                .andExpect(status().isUnauthorized());
        mvc.perform(post("/api/v1/auth/moi/double-authentification/desactiver").contentType(MediaType.APPLICATION_JSON).content(json("motDePasse", "y")))
                .andExpect(status().isUnauthorized());
    }
}
