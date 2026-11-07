package com.immogest.app.securite;

import com.immogest.app.securite.domain.Identifiants;
import com.immogest.app.securite.domain.Utilisateur;
import com.immogest.app.securite.infra.RefreshTokenRepository;
import com.immogest.app.securite.infra.UtilisateurRepository;
import com.immogest.app.shared.ActeurCourant;
import com.immogest.app.shared.ErreurMetier;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Gestion des comptes par l'ADMIN. Aucun mot de passe ni hash n'est jamais renvoyé. */
@Service
public class UtilisateurService {

    public record Saisie(String nom, String telephone, String email, Role role, boolean actif,
            boolean deuxFacteursActif) {
    }

    private final UtilisateurRepository utilisateurs;
    private final RefreshTokenRepository refreshTokens;
    private final PasswordEncoder encodeur;
    private final ApplicationEventPublisher evenements;
    private final ActeurCourant acteurCourant;
    private final Clock horloge;

    UtilisateurService(UtilisateurRepository utilisateurs, RefreshTokenRepository refreshTokens,
            PasswordEncoder encodeur, ApplicationEventPublisher evenements, ActeurCourant acteurCourant, Clock horloge) {
        this.utilisateurs = utilisateurs;
        this.refreshTokens = refreshTokens;
        this.encodeur = encodeur;
        this.evenements = evenements;
        this.acteurCourant = acteurCourant;
        this.horloge = horloge;
    }

    @Transactional
    public UtilisateurDto creer(Saisie saisie, String motDePasse) {
        String telephone = Identifiants.normaliserTelephone(saisie.telephone());
        String email = Identifiants.normaliserEmail(saisie.email());
        verifier(saisie, telephone, email);
        verifierUnicite(null, telephone, email);

        Utilisateur u = new Utilisateur(saisie.nom().strip(), telephone, email, saisie.role(),
                encodeur.encode(motDePasse), saisie.deuxFacteursActif());
        utilisateurs.save(u);
        evenements.publishEvent(new EvenementSecurite("SECURITE.UTILISATEUR_CREE", u.getId(), null, resume(u), auteur()));
        return dto(u);
    }

    @Transactional
    public UtilisateurDto modifier(UUID id, Saisie saisie) {
        Utilisateur u = charger(id);
        String telephone = Identifiants.normaliserTelephone(saisie.telephone());
        String email = Identifiants.normaliserEmail(saisie.email());
        verifier(saisie, telephone, email);
        verifierUnicite(id, telephone, email);
        interdireAutoRetrait(u, saisie);

        Map<String, Object> avant = resume(u);
        boolean etaitActif = u.isActif();
        u.modifier(saisie.nom().strip(), telephone, email, saisie.role(), saisie.actif(), saisie.deuxFacteursActif());
        utilisateurs.save(u);
        if (etaitActif && !u.isActif()) {
            revoquerSessions(u.getId());
        }
        evenements.publishEvent(new EvenementSecurite("SECURITE.UTILISATEUR_MODIFIE", u.getId(), avant, resume(u), auteur()));
        return dto(u);
    }

    /** « Suppression » = désactivation : le compte reste référencé par l'audit et l'historique. */
    @Transactional
    public void desactiver(UUID id) {
        Utilisateur u = charger(id);
        if (acteurCourant.acteur().map(a -> a.id().equals(id)).orElse(false)) {
            throw new ErreurMetier("UTILISATEUR_AUTO_DESACTIVATION", "Vous ne pouvez pas désactiver votre propre compte",
                    HttpStatus.CONFLICT);
        }
        Map<String, Object> avant = resume(u);
        u.desactiver();
        utilisateurs.save(u);
        revoquerSessions(id);
        evenements.publishEvent(new EvenementSecurite("SECURITE.UTILISATEUR_DESACTIVE", id, avant, resume(u), auteur()));
    }

    @Transactional
    public void deverrouiller(UUID id) {
        Utilisateur u = charger(id);
        u.deverrouiller();
        utilisateurs.save(u);
        evenements.publishEvent(new EvenementSecurite("SECURITE.COMPTE_DEVERROUILLE", id, null, null, auteur()));
    }

    @Transactional
    public void reinitialiserMotDePasse(UUID id, String nouveau) {
        Utilisateur u = charger(id);
        u.changerMotDePasse(encodeur.encode(nouveau));
        utilisateurs.save(u);
        revoquerSessions(id);
        evenements.publishEvent(new EvenementSecurite("SECURITE.MOT_DE_PASSE_REINITIALISE", id, null, null, auteur()));
    }

    @Transactional(readOnly = true)
    public UtilisateurDto obtenir(UUID id) {
        return dto(charger(id));
    }

    /** Nom d'un compte, ou vide s'il n'existe pas (ne lève rien : sûr à appeler dans la transaction d'un autre module). */
    @Transactional(readOnly = true)
    public java.util.Optional<String> nom(UUID id) {
        return utilisateurs.findById(id).map(u -> u.getNom());
    }

    @Transactional(readOnly = true)
    public Page<UtilisateurDto> lister(Pageable pageable) {
        return utilisateurs.findAll(pageable).map(this::dto);
    }

    // ----------------------------------------------------------------

    private Utilisateur charger(UUID id) {
        return utilisateurs.findById(id).orElseThrow(() ->
                new ErreurMetier("UTILISATEUR_INTROUVABLE", "Utilisateur introuvable", HttpStatus.NOT_FOUND));
    }

    private void verifier(Saisie saisie, String telephone, String email) {
        if (telephone == null && email == null) {
            throw new ErreurMetier("UTILISATEUR_CONTACT_REQUIS", "Un téléphone ou un e-mail est obligatoire",
                    HttpStatus.BAD_REQUEST);
        }
        if (telephone != null && !Identifiants.telephoneValide(telephone)) {
            throw new ErreurMetier("UTILISATEUR_TELEPHONE_INVALIDE", "Numéro de téléphone invalide", HttpStatus.BAD_REQUEST);
        }
        if (saisie.role().deuxFacteursObligatoire() && !saisie.deuxFacteursActif()) {
            throw new ErreurMetier("UTILISATEUR_2FA_OBLIGATOIRE",
                    "La double authentification est obligatoire pour ce rôle", HttpStatus.BAD_REQUEST);
        }
        if (saisie.deuxFacteursActif() && telephone == null) {
            throw new ErreurMetier("UTILISATEUR_TELEPHONE_REQUIS_2FA",
                    "Un numéro de téléphone est requis pour la double authentification", HttpStatus.BAD_REQUEST);
        }
        if (saisie.deuxFacteursActif() && !saisie.role().motDePasseObligatoire()) {
            throw new ErreurMetier("UTILISATEUR_2FA_ROLE_NON_CONCERNE",
                    "La double authentification concerne les rôles ADMIN, GESTIONNAIRE et ENCAISSEUR", HttpStatus.BAD_REQUEST);
        }
    }

    private void verifierUnicite(UUID idCourant, String telephone, String email) {
        if (telephone != null && utilisateurs.findByTelephone(telephone).filter(x -> !x.getId().equals(idCourant)).isPresent()
                || email != null && utilisateurs.findByEmail(email).filter(x -> !x.getId().equals(idCourant)).isPresent()) {
            throw new ErreurMetier("UTILISATEUR_DEJA_EXISTANT", "Un utilisateur avec ce téléphone ou cet e-mail existe déjà",
                    HttpStatus.CONFLICT);
        }
    }

    private void interdireAutoRetrait(Utilisateur cible, Saisie saisie) {
        boolean soiMeme = acteurCourant.acteur().map(a -> a.id().equals(cible.getId())).orElse(false);
        if (soiMeme && (!saisie.actif() || saisie.role() != Role.ADMIN)) {
            throw new ErreurMetier("UTILISATEUR_AUTO_RETRAIT",
                    "Vous ne pouvez pas vous désactiver ni retirer votre propre rôle ADMIN", HttpStatus.CONFLICT);
        }
    }

    private void revoquerSessions(UUID utilisateurId) {
        OffsetDateTime maintenant = OffsetDateTime.now(horloge);
        var actifs = refreshTokens.findByUtilisateurIdAndRevoqueLeIsNull(utilisateurId);
        actifs.forEach(t -> t.revoquer(maintenant));
        refreshTokens.saveAll(actifs);
    }

    private String auteur() {
        return acteurCourant.acteur().map(a -> a.id().toString()).orElse("systeme");
    }

    private static Map<String, Object> resume(Utilisateur u) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("nom", u.getNom());
        m.put("telephone", u.getTelephone());
        m.put("email", u.getEmail());
        m.put("role", u.getRole().name());
        m.put("actif", u.isActif());
        m.put("deuxFacteursActif", u.isDeuxFacteursActif());
        return m;
    }

    private UtilisateurDto dto(Utilisateur u) {
        return new UtilisateurDto(u.getId(), u.getNom(), u.getTelephone(), u.getEmail(), u.getRole(), u.isActif(),
                u.isDeuxFacteursActif(), u.estVerrouille(OffsetDateTime.now(horloge)));
    }
}
