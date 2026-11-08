package com.immogest.app.securite;

import com.immogest.app.securite.domain.DefiDeuxFacteurs;
import com.immogest.app.securite.domain.DefiDeuxFacteurs.Type;
import com.immogest.app.securite.domain.Utilisateur;
import com.immogest.app.securite.infra.DefiDeuxFacteursRepository;
import com.immogest.app.securite.infra.RefreshTokenRepository;
import com.immogest.app.securite.infra.SecuriteProperties;
import com.immogest.app.securite.infra.Totp;
import com.immogest.app.securite.infra.UtilisateurRepository;
import com.immogest.app.shared.ErreurMetier;
import com.immogest.app.shared.ActeurCourant;
import com.immogest.app.stockage.ChiffrementDonnees;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Map;
import java.util.OptionalLong;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Double authentification par application (TOTP : Google Authenticator, Microsoft Authenticator, Authy…), à la place du code SMS.
 * Après un bon mot de passe, un compte qui l'exige reçoit un <em>défi</em> (jeton opaque de quelques minutes) : sans lui, un code TOTP seul
 * ne donne aucune session. Un compte sans application configurée la configure d'abord (enrôlement). Les secrets sont chiffrés (AES-GCM).
 */
@Service
public class DeuxFacteursService {

    private static final SecureRandom ALEATOIRE = new SecureRandom();
    private static final int DUREE_DEFI_MINUTES = 10;
    static final String EMETTEUR_PAR_DEFAUT = "ImmoGest";

    /** Secret à saisir dans l'application d'authentification et adresse {@code otpauth://} à encoder en code QR. */
    public record Enrolement(String secret, String uri) {
    }

    /** Défi émis après un bon mot de passe. */
    public record Defi(String jeton, boolean enrolement) {
    }

    private final UtilisateurRepository utilisateurs;
    private final DefiDeuxFacteursRepository defis;
    private final RefreshTokenRepository refreshTokens;
    private final ChiffrementDonnees chiffrement;
    private final SecuriteProperties proprietes;
    private final ApplicationEventPublisher evenements;
    private final ActeurCourant acteurCourant;
    private final Clock horloge;

    DeuxFacteursService(UtilisateurRepository utilisateurs, DefiDeuxFacteursRepository defis, RefreshTokenRepository refreshTokens,
            ChiffrementDonnees chiffrement, SecuriteProperties proprietes, ApplicationEventPublisher evenements, ActeurCourant acteurCourant,
            Clock horloge) {
        this.utilisateurs = utilisateurs;
        this.defis = defis;
        this.refreshTokens = refreshTokens;
        this.chiffrement = chiffrement;
        this.proprietes = proprietes;
        this.evenements = evenements;
        this.acteurCourant = acteurCourant;
        this.horloge = horloge;
    }

    // ------------------------------------------------------------------ connexion

    /** Un compte doit-il passer par la double authentification à la connexion ? */
    static boolean exigee(Utilisateur u) {
        return u.isDeuxFacteursActif();
    }

    /** Défi à remettre au client après un bon mot de passe : vérification d'un code, ou enrôlement si l'application n'est pas encore configurée. */
    @Transactional
    public Defi creerDefi(Utilisateur u) {
        Type type = u.aUneApplicationDAuthentification() ? Type.VERIFICATION : Type.ENROLEMENT;
        String jeton = jetonOpaque();
        defis.save(new DefiDeuxFacteurs(u.getId(), type, hash(jeton), maintenant().plusMinutes(DUREE_DEFI_MINUTES)));
        return new Defi(jeton, type == Type.ENROLEMENT);
    }

    /** Vérifie le code d'un défi de vérification et renvoie le compte (le défi est consommé). L'appelant ouvre la session. */
    @Transactional(noRollbackFor = ErreurMetier.class)
    public Utilisateur verifierDefi(String jeton, String code) {
        DefiDeuxFacteurs defi = defiValide(jeton, Type.VERIFICATION);
        Utilisateur u = compte(defi);
        verifierCode(u, defi, code);
        defi.consommer();
        defis.save(defi);
        return u;
    }

    /** Étape 1 de l'enrôlement à la connexion : génère un secret en attente (régénéré à chaque appel) et l'adresse à scanner. */
    @Transactional(noRollbackFor = ErreurMetier.class)
    public Enrolement demarrerEnrolement(String jeton) {
        DefiDeuxFacteurs defi = defiValide(jeton, Type.ENROLEMENT);
        Utilisateur u = compte(defi);
        return genererEnAttente(defi, u);
    }

    /** Étape 2 : un premier code correct de l'application confirme le secret, active la double authentification et renvoie le compte. */
    @Transactional(noRollbackFor = ErreurMetier.class)
    public Utilisateur confirmerEnrolement(String jeton, String code) {
        DefiDeuxFacteurs defi = defiValide(jeton, Type.ENROLEMENT);
        Utilisateur u = compte(defi);
        confirmerSecretEnAttente(u, defi, code);
        return u;
    }

    // ------------------------------------------------------------------ profil (utilisateur connecté, mot de passe déjà confirmé par l'appelant)

    /** Démarre l'activation depuis le profil : secret en attente, à confirmer avec un premier code. */
    @Transactional
    public Enrolement demarrerActivation(UUID utilisateurId) {
        Utilisateur u = charger(utilisateurId);
        verifierConcerne(u);
        if (u.isDeuxFacteursActif() && u.aUneApplicationDAuthentification()) {
            throw new ErreurMetier("DEUX_FACTEURS_DEJA_ACTIFS", "La double authentification est déjà activée", HttpStatus.CONFLICT);
        }
        DefiDeuxFacteurs defi = new DefiDeuxFacteurs(u.getId(), Type.PROFIL, hash(jetonOpaque()), maintenant().plusMinutes(DUREE_DEFI_MINUTES));
        return genererEnAttente(defi, u);
    }

    @Transactional(noRollbackFor = ErreurMetier.class)
    public void activer(UUID utilisateurId, String code) {
        Utilisateur u = charger(utilisateurId);
        verifierConcerne(u);
        DefiDeuxFacteurs defi = defis.findFirstByUtilisateurIdAndTypeOrderByCreatedAtDesc(utilisateurId, Type.PROFIL)
                .filter(d -> d.estValide(maintenant(), proprietes.otpTentativesMax()))
                .orElseThrow(DeuxFacteursService::defiInvalide);
        confirmerSecretEnAttente(u, defi, code);
    }

    /** Désactivation : le code courant est exigé quand l'application est configurée. */
    @Transactional(noRollbackFor = ErreurMetier.class)
    public void desactiver(UUID utilisateurId, String code) {
        Utilisateur u = charger(utilisateurId);
        if (u.aUneApplicationDAuthentification()) {
            verifierCode(u, null, code);
        }
        u.retirerApplication();
        u.definirDeuxFacteurs(false);
        utilisateurs.save(u);
        evenements.publishEvent(new EvenementSecurite("SECURITE.DEUX_FACTEURS_DESACTIVES", u.getId(), null, null, u.getId().toString()));
    }

    // ------------------------------------------------------------------ administration

    /**
     * Téléphone perdu ou changé : l'ADMIN retire l'application du compte et ferme ses sessions. Si la double authentification est exigée
     * (employé restreint) ou était active, l'utilisateur en configure une nouvelle à sa prochaine connexion.
     */
    @Transactional
    public void reinitialiser(UUID utilisateurId) {
        Utilisateur u = charger(utilisateurId);
        u.retirerApplication();
        utilisateurs.save(u);
        OffsetDateTime maintenant = maintenant();
        var actifs = refreshTokens.findByUtilisateurIdAndRevoqueLeIsNull(utilisateurId);
        actifs.forEach(t -> t.revoquer(maintenant));
        refreshTokens.saveAll(actifs);
        String auteur = acteurCourant.acteur().map(a -> a.id().toString()).orElse("systeme");
        evenements.publishEvent(new EvenementSecurite("SECURITE.DEUX_FACTEURS_REINITIALISES", utilisateurId, null, null, auteur));
    }

    // ------------------------------------------------------------------ interne

    private Enrolement genererEnAttente(DefiDeuxFacteurs defi, Utilisateur u) {
        String secret = Totp.genererSecret();
        defi.definirSecretEnAttente(chiffrement.chiffrer(secret));
        defis.save(defi);
        String compte = u.getEmail() != null ? u.getEmail() : u.getTelephone() != null ? u.getTelephone() : u.getNom();
        return new Enrolement(secret, Totp.uri(emetteur(), compte, secret));
    }

    private void confirmerSecretEnAttente(Utilisateur u, DefiDeuxFacteurs defi, String code) {
        if (defi.getSecretEnAttenteChiffre() == null) {
            throw defiInvalide();
        }
        String secret = chiffrement.dechiffrer(defi.getSecretEnAttenteChiffre());
        OptionalLong pas = Totp.verifier(secret, code, Instant.now(horloge), 0);
        if (pas.isEmpty()) {
            throw echec(u, defi);
        }
        defi.consommer();
        defis.save(defi);
        u.configurerApplication(chiffrement.chiffrer(secret), pas.getAsLong());
        utilisateurs.save(u);
        evenements.publishEvent(new EvenementSecurite("SECURITE.DEUX_FACTEURS_ACTIVES", u.getId(), null, null, u.getId().toString()));
    }

    /** Vérifie un code TOTP du compte : un code faux compte comme un mot de passe faux (verrouillage) et dans le défi ; un code déjà utilisé est refusé. */
    private void verifierCode(Utilisateur u, DefiDeuxFacteurs defi, String code) {
        if (!u.aUneApplicationDAuthentification()) {
            throw new ErreurMetier("DEUX_FACTEURS_NON_CONFIGURES", "La double authentification n'est pas configurée", HttpStatus.CONFLICT);
        }
        if (u.estVerrouille(maintenant())) {
            throw new ErreurMetier("COMPTE_VERROUILLE", "Compte temporairement verrouillé", HttpStatus.LOCKED);
        }
        OptionalLong pas = Totp.verifier(chiffrement.dechiffrer(u.getTotpSecretChiffre()), code, Instant.now(horloge), u.getTotpDernierPas());
        if (pas.isEmpty()) {
            throw echec(u, defi);
        }
        u.memoriserPasTotp(pas.getAsLong());
        utilisateurs.save(u);
    }

    /** Enregistre l'échec (défi, verrouillage du compte) et renvoie l'erreur à lever. */
    private ErreurMetier echec(Utilisateur u, DefiDeuxFacteurs defi) {
        if (defi != null) {
            defi.compterTentative();
            defis.save(defi);
        }
        boolean verrouille = u.enregistrerEchec(proprietes.echecsMax(), maintenant().plusMinutes(proprietes.verrouillageMinutes()));
        utilisateurs.save(u);
        if (verrouille) {
            evenements.publishEvent(new EvenementSecurite("SECURITE.COMPTE_VERROUILLE", u.getId(), null,
                    Map.of("verrouilleJusqua", u.getVerrouilleJusqua().toString()), "systeme"));
            return new ErreurMetier("COMPTE_VERROUILLE", "Compte temporairement verrouillé", HttpStatus.LOCKED);
        }
        // Utilisateur déjà connecté (profil) : 422, comme un mauvais mot de passe actuel. Un 401 serait lu par le web comme une session perdue et le déconnecterait.
        boolean connecte = defi == null ? true : defi.getType() == Type.PROFIL;
        return new ErreurMetier("OTP_INVALIDE", "Code invalide ou expiré", connecte ? HttpStatus.UNPROCESSABLE_ENTITY : HttpStatus.UNAUTHORIZED);
    }

    private DefiDeuxFacteurs defiValide(String jeton, Type type) {
        return defis.findByTokenHash(hash(jeton == null ? "" : jeton))
                .filter(d -> d.getType() == type && d.estValide(maintenant(), proprietes.otpTentativesMax()))
                .orElseThrow(DeuxFacteursService::defiInvalide);
    }

    private Utilisateur compte(DefiDeuxFacteurs defi) {
        return utilisateurs.findById(defi.getUtilisateurId()).filter(Utilisateur::isActif).orElseThrow(DeuxFacteursService::defiInvalide);
    }

    private Utilisateur charger(UUID id) {
        return utilisateurs.findById(id).orElseThrow(() -> new ErreurMetier("UTILISATEUR_INTROUVABLE", "Utilisateur introuvable", HttpStatus.NOT_FOUND));
    }

    private static void verifierConcerne(Utilisateur u) {
        if (!u.getRole().motDePasseObligatoire()) {
            throw new ErreurMetier("UTILISATEUR_2FA_ROLE_NON_CONCERNE",
                    "La double authentification concerne les rôles ADMIN, GESTIONNAIRE et les employés restreints", HttpStatus.CONFLICT);
        }
    }

    private String emetteur() {
        return proprietes.totpEmetteur();
    }

    private static ErreurMetier defiInvalide() {
        return new ErreurMetier("DEFI_DEUX_FACTEURS_INVALIDE", "Session de connexion expirée : reconnectez-vous", HttpStatus.UNAUTHORIZED);
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
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(valeur.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
