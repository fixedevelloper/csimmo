package com.immogest.app.securite;

import com.immogest.app.securite.domain.Identifiants;
import com.immogest.app.securite.domain.OtpCode;
import com.immogest.app.securite.domain.RefreshToken;
import com.immogest.app.securite.domain.Utilisateur;
import com.immogest.app.securite.infra.JwtService;
import com.immogest.app.securite.infra.OtpCodeRepository;
import com.immogest.app.securite.infra.RefreshTokenRepository;
import com.immogest.app.securite.infra.SecuriteProperties;
import com.immogest.app.securite.infra.UtilisateurRepository;
import com.immogest.app.shared.ErreurMetier;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Connexion (mot de passe, OTP, 2FA), rotation des jetons et changement de mot de passe. */
@Service
public class AuthService {

    private static final SecureRandom ALEATOIRE = new SecureRandom();
    /** Hash factice : égalise le temps de réponse quand l'identifiant est inconnu. */
    private final String hashFactice;

    private final UtilisateurRepository utilisateurs;
    private final RefreshTokenRepository refreshTokens;
    private final OtpCodeRepository otps;
    private final PasswordEncoder encodeur;
    private final JwtService jwt;
    private final OtpSender otpSender;
    private final SecuriteProperties proprietes;
    private final ApplicationEventPublisher evenements;
    private final Clock horloge;

    AuthService(UtilisateurRepository utilisateurs, RefreshTokenRepository refreshTokens, OtpCodeRepository otps,
            PasswordEncoder encodeur, JwtService jwt, OtpSender otpSender, SecuriteProperties proprietes,
            ApplicationEventPublisher evenements, Clock horloge) {
        this.utilisateurs = utilisateurs;
        this.refreshTokens = refreshTokens;
        this.otps = otps;
        this.encodeur = encodeur;
        this.jwt = jwt;
        this.otpSender = otpSender;
        this.proprietes = proprietes;
        this.evenements = evenements;
        this.horloge = horloge;
        this.hashFactice = encodeur.encode("mot-de-passe-factice");
    }

    // ---------------------------------------------------------------- connexion par mot de passe

    @Transactional(noRollbackFor = ErreurMetier.class)
    public ReponseConnexion connecter(String identifiant, String motDePasse) {
        OffsetDateTime maintenant = maintenant();
        Optional<Utilisateur> trouve = trouver(identifiant);
        if (trouve.isEmpty()) {
            encodeur.matches(motDePasse, hashFactice);
            throw identifiantsInvalides();
        }
        Utilisateur u = trouve.get();
        if (u.estVerrouille(maintenant)) {
            throw compteVerrouille();
        }
        if (!encodeur.matches(motDePasse, u.getMotDePasseHash())) {
            boolean verrouille = u.enregistrerEchec(proprietes.echecsMax(),
                    maintenant.plusMinutes(proprietes.verrouillageMinutes()));
            utilisateurs.save(u);
            if (verrouille) {
                evenements.publishEvent(new EvenementSecurite("SECURITE.COMPTE_VERROUILLE", u.getId(), null,
                        Map.of("verrouilleJusqua", u.getVerrouilleJusqua().toString()), "systeme"));
                throw compteVerrouille();
            }
            throw identifiantsInvalides();
        }
        if (!u.isActif()) {
            throw new ErreurMetier("COMPTE_DESACTIVE", "Ce compte est désactivé", HttpStatus.FORBIDDEN);
        }
        if (u.isDeuxFacteursActif()) {
            envoyerOtp(u, maintenant);
            return ReponseConnexion.deuxFacteurs();
        }
        return ouvrirSession(u, maintenant);
    }

    // ---------------------------------------------------------------- OTP

    /** Envoie un code par SMS. Répond toujours pareil : ne révèle pas si le compte existe. */
    @Transactional
    public void demanderOtp(String identifiant) {
        OffsetDateTime maintenant = maintenant();
        trouver(identifiant)
                .filter(u -> u.isActif() && !u.getRole().motDePasseObligatoire() && u.getTelephone() != null)
                .filter(u -> !u.estVerrouille(maintenant))
                .ifPresent(u -> envoyerOtp(u, maintenant));
    }

    @Transactional(noRollbackFor = ErreurMetier.class)
    public ReponseConnexion verifierOtp(String identifiant, String code) {
        OffsetDateTime maintenant = maintenant();
        Utilisateur u = trouver(identifiant).orElseThrow(AuthService::otpInvalide);
        if (u.estVerrouille(maintenant)) {
            throw compteVerrouille();
        }
        OtpCode otp = otps.findFirstByUtilisateurIdOrderByCreatedAtDesc(u.getId())
                .filter(o -> o.estValide(maintenant, proprietes.otpTentativesMax()))
                .orElseThrow(AuthService::otpInvalide);
        if (!hash(u.getId() + ":" + code).equals(otp.getCodeHash())) {
            otp.compterTentative();
            otps.save(otp);
            throw otpInvalide();
        }
        otp.consommer();
        otps.save(otp);
        if (!u.isActif()) {
            throw new ErreurMetier("COMPTE_DESACTIVE", "Ce compte est désactivé", HttpStatus.FORBIDDEN);
        }
        return ouvrirSession(u, maintenant);
    }

    private void envoyerOtp(Utilisateur u, OffsetDateTime maintenant) {
        if (u.getTelephone() == null) {
            throw new ErreurMetier("OTP_TELEPHONE_REQUIS", "Aucun numéro de téléphone pour envoyer le code",
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }
        boolean tropRecent = otps.findFirstByUtilisateurIdOrderByCreatedAtDesc(u.getId())
                .filter(o -> o.getCreatedAt().plusSeconds(proprietes.otpIntervalleSecondes()).isAfter(maintenant))
                .isPresent();
        if (tropRecent) {
            return;
        }
        String code = "%06d".formatted(ALEATOIRE.nextInt(1_000_000));
        otps.save(new OtpCode(u.getId(), hash(u.getId() + ":" + code),
                maintenant.plusMinutes(proprietes.otpDureeMinutes())));
        otpSender.envoyer(u.getTelephone(), code);
    }

    // ---------------------------------------------------------------- jetons

    @Transactional(noRollbackFor = ErreurMetier.class)
    public ReponseConnexion rafraichir(String refreshToken) {
        OffsetDateTime maintenant = maintenant();
        RefreshToken ancien = refreshTokens.findByTokenHash(hash(refreshToken))
                .orElseThrow(() -> new ErreurMetier("REFRESH_TOKEN_INVALIDE", "Jeton invalide", HttpStatus.UNAUTHORIZED));
        if (ancien.estRevoque()) {
            // Réutilisation d'un jeton déjà tourné : vol probable, on coupe toutes les sessions de l'utilisateur.
            revoquerTout(ancien.getUtilisateurId(), maintenant);
            throw new ErreurMetier("REFRESH_TOKEN_REUTILISE", "Jeton déjà utilisé, session révoquée",
                    HttpStatus.UNAUTHORIZED);
        }
        if (ancien.estExpire(maintenant)) {
            throw new ErreurMetier("REFRESH_TOKEN_EXPIRE", "Jeton expiré", HttpStatus.UNAUTHORIZED);
        }
        Utilisateur u = utilisateurs.findById(ancien.getUtilisateurId())
                .filter(Utilisateur::isActif)
                .orElseThrow(() -> new ErreurMetier("REFRESH_TOKEN_INVALIDE", "Jeton invalide", HttpStatus.UNAUTHORIZED));
        ancien.revoquer(maintenant);
        refreshTokens.save(ancien);
        return emettre(u, maintenant);
    }

    @Transactional
    public void deconnecter(String refreshToken) {
        refreshTokens.findByTokenHash(hash(refreshToken)).ifPresent(t -> {
            t.revoquer(maintenant());
            refreshTokens.save(t);
        });
    }

    // ---------------------------------------------------------------- mot de passe

    @Transactional(noRollbackFor = ErreurMetier.class)
    public void changerMotDePasse(UUID utilisateurId, String ancien, String nouveau) {
        Utilisateur u = utilisateurs.findById(utilisateurId).orElseThrow(AuthService::identifiantsInvalides);
        if (!encodeur.matches(ancien, u.getMotDePasseHash())) {
            throw new ErreurMetier("MOT_DE_PASSE_ACTUEL_INCORRECT", "Mot de passe actuel incorrect",
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }
        u.changerMotDePasse(encodeur.encode(nouveau));
        utilisateurs.save(u);
        revoquerTout(u.getId(), maintenant());
        evenements.publishEvent(new EvenementSecurite("SECURITE.MOT_DE_PASSE_CHANGE", u.getId(), null, null,
                u.getId().toString()));
    }

    // ---------------------------------------------------------------- profil : mot de passe actuel et double authentification

    /**
     * Confirme l'identité d'un utilisateur déjà connecté avant une opération sensible sur son profil. Chaque mot de passe faux compte comme à
     * la connexion (verrouillage au-delà du seuil) : un jeton volé ne permet pas de deviner le mot de passe ici.
     */
    @Transactional(noRollbackFor = ErreurMetier.class)
    public void verifierMotDePasseActuel(UUID utilisateurId, String motDePasse) {
        Utilisateur u = utilisateurs.findById(utilisateurId).orElseThrow(AuthService::identifiantsInvalides);
        OffsetDateTime maintenant = maintenant();
        if (u.estVerrouille(maintenant)) {
            throw compteVerrouille();
        }
        if (!encodeur.matches(motDePasse, u.getMotDePasseHash())) {
            boolean verrouille = u.enregistrerEchec(proprietes.echecsMax(), maintenant.plusMinutes(proprietes.verrouillageMinutes()));
            utilisateurs.save(u);
            if (verrouille) {
                evenements.publishEvent(new EvenementSecurite("SECURITE.COMPTE_VERROUILLE", u.getId(), null,
                        Map.of("verrouilleJusqua", u.getVerrouilleJusqua().toString()), "systeme"));
                throw compteVerrouille();
            }
            throw new ErreurMetier("MOT_DE_PASSE_ACTUEL_INCORRECT", "Mot de passe actuel incorrect", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    /** Étape 1 de l'activation : mot de passe confirmé, puis code SMS envoyé au numéro du compte (preuve qu'on le reçoit avant de l'exiger à la connexion). */
    @Transactional(noRollbackFor = ErreurMetier.class)
    public void demanderActivationDeuxFacteurs(UUID utilisateurId, String motDePasse) {
        verifierMotDePasseActuel(utilisateurId, motDePasse);
        Utilisateur u = utilisateurs.findById(utilisateurId).orElseThrow(AuthService::identifiantsInvalides);
        verifierDeuxFacteursPossible(u);
        envoyerOtp(u, maintenant());
    }

    /** Étape 2 : le bon code active la double authentification. */
    @Transactional(noRollbackFor = ErreurMetier.class)
    public void activerDeuxFacteurs(UUID utilisateurId, String code) {
        OffsetDateTime maintenant = maintenant();
        Utilisateur u = utilisateurs.findById(utilisateurId).orElseThrow(AuthService::identifiantsInvalides);
        verifierDeuxFacteursPossible(u);
        OtpCode otp = otps.findFirstByUtilisateurIdOrderByCreatedAtDesc(u.getId())
                .filter(o -> o.estValide(maintenant, proprietes.otpTentativesMax()))
                .orElseThrow(AuthService::otpInvalide);
        if (!hash(u.getId() + ":" + code).equals(otp.getCodeHash())) {
            otp.compterTentative();
            otps.save(otp);
            throw otpInvalide();
        }
        otp.consommer();
        otps.save(otp);
        u.definirDeuxFacteurs(true);
        utilisateurs.save(u);
        evenements.publishEvent(new EvenementSecurite("SECURITE.DEUX_FACTEURS_ACTIVES", u.getId(), null, null, u.getId().toString()));
    }

    /** Désactivation : mot de passe exigé ; impossible pour les rôles qui doivent toujours avoir la double authentification. */
    @Transactional(noRollbackFor = ErreurMetier.class)
    public void desactiverDeuxFacteurs(UUID utilisateurId, String motDePasse) {
        verifierMotDePasseActuel(utilisateurId, motDePasse);
        Utilisateur u = utilisateurs.findById(utilisateurId).orElseThrow(AuthService::identifiantsInvalides);
        if (u.getRole().deuxFacteursObligatoire()) {
            throw new ErreurMetier("DEUX_FACTEURS_OBLIGATOIRE", "La double authentification est obligatoire pour votre rôle", HttpStatus.CONFLICT);
        }
        if (!u.isDeuxFacteursActif()) {
            return;
        }
        u.definirDeuxFacteurs(false);
        utilisateurs.save(u);
        evenements.publishEvent(new EvenementSecurite("SECURITE.DEUX_FACTEURS_DESACTIVES", u.getId(), null, null, u.getId().toString()));
    }

    private static void verifierDeuxFacteursPossible(Utilisateur u) {
        if (!u.getRole().motDePasseObligatoire()) {
            throw new ErreurMetier("UTILISATEUR_2FA_ROLE_NON_CONCERNE",
                    "La double authentification concerne les rôles ADMIN, GESTIONNAIRE, ENCAISSEUR, GESTIONNAIRE_BIENS et COMPTABLE", HttpStatus.CONFLICT);
        }
        if (u.isDeuxFacteursActif()) {
            throw new ErreurMetier("DEUX_FACTEURS_DEJA_ACTIFS", "La double authentification est déjà activée", HttpStatus.CONFLICT);
        }
    }

    // ---------------------------------------------------------------- interne

    private ReponseConnexion ouvrirSession(Utilisateur u, OffsetDateTime maintenant) {
        u.enregistrerConnexion(maintenant);
        utilisateurs.save(u);
        return emettre(u, maintenant);
    }

    private ReponseConnexion emettre(Utilisateur u, OffsetDateTime maintenant) {
        String brut = jetonOpaque();
        refreshTokens.save(new RefreshToken(u.getId(), hash(brut), maintenant.plusDays(proprietes.refreshDureeJours())));
        String acces = jwt.creerAcces(u.getId(), u.getRole(), maintenant.toInstant());
        return ReponseConnexion.tokens(acces, brut, jwt.dureeAccesSecondes(), u.getRole());
    }

    private void revoquerTout(UUID utilisateurId, OffsetDateTime maintenant) {
        var actifs = refreshTokens.findByUtilisateurIdAndRevoqueLeIsNull(utilisateurId);
        actifs.forEach(t -> t.revoquer(maintenant));
        refreshTokens.saveAll(actifs);
    }

    private Optional<Utilisateur> trouver(String identifiant) {
        if (identifiant == null || identifiant.isBlank()) {
            return Optional.empty();
        }
        return Identifiants.estEmail(identifiant)
                ? utilisateurs.findByEmail(Identifiants.normaliserEmail(identifiant))
                : utilisateurs.findByTelephone(Identifiants.normaliserTelephone(identifiant));
    }

    private OffsetDateTime maintenant() {
        return OffsetDateTime.now(horloge);
    }

    private static String jetonOpaque() {
        byte[] octets = new byte[32];
        ALEATOIRE.nextBytes(octets);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(octets);
    }

    private static String hash(String valeur) {
        try {
            return HexFormat.of().formatHex(
                    MessageDigest.getInstance("SHA-256").digest(valeur.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private static ErreurMetier identifiantsInvalides() {
        return new ErreurMetier("IDENTIFIANTS_INVALIDES", "Identifiant ou mot de passe incorrect", HttpStatus.UNAUTHORIZED);
    }

    private static ErreurMetier compteVerrouille() {
        return new ErreurMetier("COMPTE_VERROUILLE", "Compte temporairement verrouillé", HttpStatus.LOCKED);
    }

    private static ErreurMetier otpInvalide() {
        return new ErreurMetier("OTP_INVALIDE", "Code invalide ou expiré", HttpStatus.UNAUTHORIZED);
    }
}
