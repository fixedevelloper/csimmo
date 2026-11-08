package com.immogest.app.securite.web;

import com.immogest.app.securite.AuthService;
import com.immogest.app.securite.ReponseConnexion;
import com.immogest.app.securite.UtilisateurDto;
import com.immogest.app.securite.UtilisateurService;
import com.immogest.app.shared.ActeurCourant;
import com.immogest.app.shared.ErreurMetier;
import jakarta.validation.Valid;
import java.util.UUID;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
class AuthController {

    record ConnexionRequete(@NotBlank String identifiant, @NotBlank String motDePasse) {
    }

    record RefreshRequete(@NotBlank String refreshToken) {
    }

    record OtpDemande(@NotBlank String identifiant) {
    }

    record OtpVerification(@NotBlank String identifiant, @NotBlank @Size(min = 6, max = 6) String code) {
    }

    record ChangementMotDePasse(@NotBlank String ancienMotDePasse,
            @NotBlank @Size(min = 12, max = 100) String nouveauMotDePasse) {
    }

    record ProfilRequete(@NotBlank @Size(max = 200) String nom, @Size(max = 30) String telephone, @Size(max = 200) String email,
            @NotBlank String motDePasse) {
    }

    record MotDePasseRequete(@NotBlank String motDePasse) {
    }

    record CodeRequete(@NotBlank @Size(min = 6, max = 6) String code) {
    }

    private final AuthService auth;
    private final UtilisateurService utilisateurs;
    private final ActeurCourant acteurCourant;

    AuthController(AuthService auth, UtilisateurService utilisateurs, ActeurCourant acteurCourant) {
        this.auth = auth;
        this.utilisateurs = utilisateurs;
        this.acteurCourant = acteurCourant;
    }

    @PostMapping("/login")
    ReponseConnexion login(@Valid @RequestBody ConnexionRequete requete) {
        return auth.connecter(requete.identifiant(), requete.motDePasse());
    }

    @PostMapping("/refresh")
    ReponseConnexion refresh(@Valid @RequestBody RefreshRequete requete) {
        return auth.rafraichir(requete.refreshToken());
    }

    @PostMapping("/logout")
    ResponseEntity<Void> logout(@Valid @RequestBody RefreshRequete requete) {
        auth.deconnecter(requete.refreshToken());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/otp/demander")
    ResponseEntity<Void> demanderOtp(@Valid @RequestBody OtpDemande requete) {
        auth.demanderOtp(requete.identifiant());
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/otp/verifier")
    ReponseConnexion verifierOtp(@Valid @RequestBody OtpVerification requete) {
        return auth.verifierOtp(requete.identifiant(), requete.code());
    }

    @PostMapping("/mot-de-passe")
    ResponseEntity<Void> changerMotDePasse(@Valid @RequestBody ChangementMotDePasse requete) {
        auth.changerMotDePasse(acteur().id(), requete.ancienMotDePasse(), requete.nouveauMotDePasse());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/moi")
    UtilisateurDto moi() {
        return utilisateurs.obtenir(acteur().id());
    }

    /** Modifie SON profil (nom, téléphone, e-mail) ; le mot de passe actuel est exigé (le téléphone reçoit les codes de connexion). */
    @PutMapping("/moi")
    @org.springframework.security.access.prepost.PreAuthorize("isAuthenticated()")
    @io.swagger.v3.oas.annotations.Operation(operationId = "modifierProfil")
    UtilisateurDto modifierProfil(@Valid @RequestBody ProfilRequete requete) {
        UUID id = acteur().id();
        auth.verifierMotDePasseActuel(id, requete.motDePasse());
        return utilisateurs.modifierProfil(id, requete.nom(), requete.telephone(), requete.email());
    }

    /** Active la double authentification, étape 1 : confirme le mot de passe et envoie un code SMS. */
    @PostMapping("/moi/double-authentification/demander")
    @org.springframework.security.access.prepost.PreAuthorize("isAuthenticated()")
    @io.swagger.v3.oas.annotations.Operation(operationId = "demanderDeuxFacteurs")
    ResponseEntity<Void> demanderDeuxFacteurs(@Valid @RequestBody MotDePasseRequete requete) {
        auth.demanderActivationDeuxFacteurs(acteur().id(), requete.motDePasse());
        return ResponseEntity.accepted().build();
    }

    /** Étape 2 : le bon code active la double authentification. */
    @PostMapping("/moi/double-authentification/activer")
    @org.springframework.security.access.prepost.PreAuthorize("isAuthenticated()")
    @io.swagger.v3.oas.annotations.Operation(operationId = "activerDeuxFacteurs")
    UtilisateurDto activerDeuxFacteurs(@Valid @RequestBody CodeRequete requete) {
        auth.activerDeuxFacteurs(acteur().id(), requete.code());
        return utilisateurs.obtenir(acteur().id());
    }

    /** Désactive la double authentification (mot de passe exigé ; refusé pour les employés restreints). */
    @PostMapping("/moi/double-authentification/desactiver")
    @org.springframework.security.access.prepost.PreAuthorize("isAuthenticated()")
    @io.swagger.v3.oas.annotations.Operation(operationId = "desactiverDeuxFacteurs")
    UtilisateurDto desactiverDeuxFacteurs(@Valid @RequestBody MotDePasseRequete requete) {
        auth.desactiverDeuxFacteurs(acteur().id(), requete.motDePasse());
        return utilisateurs.obtenir(acteur().id());
    }

    private com.immogest.app.shared.Acteur acteur() {
        return acteurCourant.acteur().orElseThrow(() ->
                new ErreurMetier("NON_AUTHENTIFIE", "Authentification requise", HttpStatus.UNAUTHORIZED));
    }
}
