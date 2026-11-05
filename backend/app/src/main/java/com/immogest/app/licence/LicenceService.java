package com.immogest.app.licence;

import com.immogest.app.licence.ServeurLicencesClient.DemandeVerification;
import com.immogest.app.licence.ServeurLicencesClient.ReponseVerification;
import com.immogest.app.licence.ServeurLicencesClient.ServeurLicencesIndisponibleException;
import com.immogest.app.licence.domain.LicenceInstallee;
import com.immogest.app.licence.infra.ClePubliqueEditeur;
import com.immogest.app.licence.infra.LicenceProperties;
import com.immogest.app.licence.infra.LicenceRepository;
import com.immogest.app.licence.infra.RegistreInstallation;
import com.immogest.app.shared.ActeurCourant;
import com.immogest.app.shared.ErreurMetier;
import com.immogest.licensing.Licence;
import com.immogest.licensing.ResultatVerification;
import com.immogest.licensing.StatutLicence;
import com.immogest.licensing.VerificateurLicence;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * État de la licence de cette installation : vérification hors ligne (signature, empreinte, dates), vérification en
 * ligne hebdomadaire avec délai de grâce, alertes avant expiration, import d'une nouvelle licence.
 *
 * <p>L'état est calculé à partir d'un instantané en mémoire, rechargé et re-vérifié (signature comprise) chaque jour,
 * au démarrage et après tout changement : aucune requête SQL ni vérification cryptographique par appel HTTP.
 */
@Service
public class LicenceService {

    private static final Logger LOG = LoggerFactory.getLogger(LicenceService.class);
    private static final ZoneId DOUALA = ZoneId.of("Africa/Douala");

    /** Photographie de la licence courante, valable pour un jour donné. */
    private record Instantane(LocalDate jour, UUID id, String licenseId, ResultatVerification verification,
            boolean revoquee, OffsetDateTime derniereVerificationEnLigne, String detail) {

        boolean present() {
            return verification != null;
        }
    }

    private final LicenceRepository repository;
    private final RegistreInstallation registre;
    private final ClePubliqueEditeur clePublique;
    private final LicenceProperties proprietes;
    private final ServeurLicencesClient serveur;
    private final ApplicationEventPublisher evenements;
    private final ActeurCourant acteurCourant;
    private final Clock horloge;
    private final TransactionTemplate transaction;

    private volatile Instantane cache;

    LicenceService(LicenceRepository repository, RegistreInstallation registre, ClePubliqueEditeur clePublique,
            LicenceProperties proprietes, ServeurLicencesClient serveur, ApplicationEventPublisher evenements,
            ActeurCourant acteurCourant, Clock horloge, PlatformTransactionManager gestionnaire) {
        this.repository = repository;
        this.registre = registre;
        this.clePublique = clePublique;
        this.proprietes = proprietes;
        this.serveur = serveur;
        this.evenements = evenements;
        this.acteurCourant = acteurCourant;
        this.horloge = horloge;
        this.transaction = new TransactionTemplate(gestionnaire);
    }

    // ------------------------------------------------------------------ état

    public EtatLicenceDto etat() {
        return construireDto(instantane(), maintenant());
    }

    public EtatLicence etatCourant() {
        return calculerEtat(instantane(), maintenant());
    }

    /** Vrai seulement si la licence est valide : sinon l'application est en lecture seule. */
    public boolean ecrituresAutorisees() {
        return etatCourant() == EtatLicence.VALIDE;
    }

    /** Oublie l'instantané : le prochain appel relit la base et re-vérifie la signature. */
    public void invalider() {
        cache = null;
    }

    /**
     * Invalide l'instantané une fois la transaction en cours terminée (validée OU annulée). Sans cela, un autre thread
     * pourrait remettre en cache l'ancien état avant la validation, ou l'état d'une licence annulée resterait en mémoire.
     */
    private void invaliderApresTransaction() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCompletion(int statut) {
                    invalider();
                }
            });
        }
        invalider();
    }

    private Instantane instantane() {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            // Données éventuellement non validées : lues, jamais mises en cache
            return recharger(aujourdhui());
        }
        Instantane courant = cache;
        LocalDate jour = aujourdhui();
        if (courant != null && courant.jour().equals(jour)) {
            return courant;
        }
        synchronized (this) {
            courant = cache;
            if (courant == null || !courant.jour().equals(jour)) {
                courant = recharger(jour);
                cache = courant;
            }
            return courant;
        }
    }

    private Instantane recharger(LocalDate jour) {
        Optional<LicenceInstallee> courante = repository.findByActuelleTrue();
        if (courante.isEmpty()) {
            return new Instantane(jour, null, null, null, false, null, null);
        }
        LicenceInstallee l = courante.get();
        Optional<String> empreinte = registre.empreinte();
        ResultatVerification verification;
        String detail = null;
        if (clePublique.cle().isEmpty()) {
            verification = new ResultatVerification(StatutLicence.SIGNATURE_INVALIDE, null, null);
            detail = "Clé publique de l'éditeur absente";
        } else {
            verification = VerificateurLicence.verifier(l.getContenu(), clePublique.cle().get(),
                    empreinte.orElse(null), jour);
            detail = switch (verification.statut()) {
                case FORMAT_INVALIDE -> "Fichier de licence illisible";
                case SIGNATURE_INVALIDE -> "Signature invalide : le fichier a été modifié";
                case MAUVAISE_EMPREINTE -> empreinte.isEmpty() ? "Identifiant machine introuvable"
                        : "Licence émise pour un autre serveur";
                case PAS_ENCORE_VALIDE -> "La licence n'a pas encore commencé";
                default -> null;
            };
        }
        return new Instantane(jour, l.getId(), l.getLicenseId(), verification, l.isRevoquee(),
                l.getDerniereVerificationEnLigne(), detail);
    }

    private EtatLicence calculerEtat(Instantane i, Instant maintenant) {
        if (!i.present()) {
            return EtatLicence.AUCUNE;
        }
        EtatLicence parContenu = switch (i.verification().statut()) {
            case FORMAT_INVALIDE, SIGNATURE_INVALIDE, MAUVAISE_EMPREINTE, PAS_ENCORE_VALIDE -> EtatLicence.INVALIDE;
            case EXPIREE -> EtatLicence.EXPIREE;
            case VALIDE -> EtatLicence.VALIDE;
        };
        if (parContenu == EtatLicence.INVALIDE) {
            return parContenu;
        }
        if (i.revoquee()) {
            return EtatLicence.REVOQUEE;
        }
        if (parContenu == EtatLicence.EXPIREE) {
            return parContenu;
        }
        if (proprietes.verificationEnLigneActive() && maintenant.isAfter(limiteDeGrace(i))) {
            return EtatLicence.VERIFICATION_REQUISE;
        }
        return EtatLicence.VALIDE;
    }

    private Instant limiteDeGrace(Instantane i) {
        return i.derniereVerificationEnLigne().toInstant().plus(Duration.ofDays(proprietes.graceJours()));
    }

    private EtatLicenceDto construireDto(Instantane i, Instant maintenant) {
        EtatLicence etat = calculerEtat(i, maintenant);
        String empreinte = registre.empreinte().orElse(null);
        Licence l = i.present() ? i.verification().licence() : null;
        LocalDate jour = i.jour();
        Long joursRestants = l == null ? null : l.joursRestantsAu(jour);
        Long grace = i.present() && proprietes.verificationEnLigneActive()
                ? Math.max(0, Duration.between(maintenant, limiteDeGrace(i)).toDays()) : null;
        return new EtatLicenceDto(
                etat, etat.lectureSeule(), i.detail(),
                l == null ? i.licenseId() : l.licenseId(),
                l == null ? null : l.client(),
                l == null ? null : l.palierLots(),
                l == null ? null : l.dateDebut(),
                l == null ? null : l.dateExpiration(),
                joursRestants,
                (etat == EtatLicence.VALIDE || etat == EtatLicence.VERIFICATION_REQUISE) ? seuilAtteint(joursRestants) : null,
                l == null ? List.of() : l.fonctionnalites(),
                empreinte,
                proprietes.verificationEnLigneActive(),
                i.present() ? i.derniereVerificationEnLigne() : null,
                grace);
    }

    /** Plus petit seuil d'alerte (30, 15, 7) supérieur ou égal au nombre de jours restants. */
    private Integer seuilAtteint(Long joursRestants) {
        if (joursRestants == null || joursRestants < 0) {
            return null;
        }
        return proprietes.alertesJours().stream().sorted().filter(s -> joursRestants <= s).findFirst().orElse(null);
    }

    // ------------------------------------------------------------------ import

    /**
     * Importe une licence (installation, renouvellement) après vérification complète. Une licence plus ancienne que
     * la courante est refusée ; une licence identique à la courante ne change rien (et ne rallonge pas la grâce).
     */
    public EtatLicenceDto importer(String contenu) {
        return transaction.execute(status -> importerDansTransaction(contenu));
    }

    private EtatLicenceDto importerDansTransaction(String contenu) {
        LocalDate jour = aujourdhui();
        if (clePublique.cle().isEmpty()) {
            throw new ErreurMetier("LICENCE_CLE_EDITEUR_ABSENTE",
                    "Aucune clé publique de l'éditeur n'est configurée : impossible de vérifier une licence",
                    HttpStatus.SERVICE_UNAVAILABLE);
        }
        Optional<String> empreinte = registre.empreinte();
        ResultatVerification r = VerificateurLicence.verifier(contenu, clePublique.cle().get(),
                empreinte.orElse(null), jour);
        refuserSiInvalide(r, empreinte.isEmpty());
        Licence nouvelle = r.licence();

        Optional<LicenceInstallee> courante = repository.findByActuelleTrue();
        if (courante.isPresent()) {
            Licence authentique = instantane().verification() == null ? null : instantane().verification().licence();
            if (authentique != null) {
                if (authentique.equals(nouvelle)) {
                    return etat();
                }
                if (nouvelle.dateExpiration().isBefore(authentique.dateExpiration())) {
                    throw new ErreurMetier("LICENCE_PLUS_ANCIENNE",
                            "Cette licence expire avant la licence actuelle (%s) : import refusé"
                                    .formatted(authentique.dateExpiration()), HttpStatus.UNPROCESSABLE_ENTITY);
                }
            }
            courante.get().remplacer();
            repository.saveAndFlush(courante.get());
        }
        LicenceInstallee enregistree = repository.saveAndFlush(
                new LicenceInstallee(nouvelle.licenseId(), contenu.strip(), OffsetDateTime.now(horloge)));
        evenements.publishEvent(new LicenceImportee(enregistree.getId(), nouvelle.licenseId(), nouvelle.client(),
                nouvelle.dateExpiration(), auteur()));
        invaliderApresTransaction();
        return etat();
    }

    private static void refuserSiInvalide(ResultatVerification r, boolean machineIntrouvable) {
        HttpStatus statut = HttpStatus.UNPROCESSABLE_ENTITY;
        switch (r.statut()) {
            case VALIDE -> {
            }
            case FORMAT_INVALIDE -> throw new ErreurMetier("LICENCE_FORMAT_INVALIDE",
                    "Fichier de licence illisible : " + r.detail(), statut);
            case SIGNATURE_INVALIDE -> throw new ErreurMetier("LICENCE_SIGNATURE_INVALIDE",
                    "La signature de la licence est invalide : le fichier a été modifié ou n'a pas été émis par l'éditeur",
                    statut);
            case MAUVAISE_EMPREINTE -> throw new ErreurMetier("LICENCE_MAUVAISE_EMPREINTE", machineIntrouvable
                    ? "Identifiant machine introuvable : définir IMMOGEST_MACHINE_ID"
                    : "Cette licence a été émise pour un autre serveur", statut);
            case PAS_ENCORE_VALIDE -> throw new ErreurMetier("LICENCE_PAS_ENCORE_VALIDE",
                    "Cette licence n'a pas encore commencé (début le %s)".formatted(r.licence().dateDebut()), statut);
            case EXPIREE -> throw new ErreurMetier("LICENCE_FICHIER_EXPIRE",
                    "Cette licence est déjà expirée (le %s)".formatted(r.licence().dateExpiration()), statut);
        }
    }

    // ------------------------------------------------------------------ contrôle quotidien

    /**
     * Contrôle au démarrage et chaque jour : revérifie hors ligne (signature, empreinte, dates), interroge le serveur de
     * licences s'il est temps, puis envoie les alertes (30, 15, 7 jours) et l'événement d'expiration, une seule fois.
     */
    public void controleQuotidien() {
        invalider();
        try {
            verifierEnLigne();
        } catch (RuntimeException e) {
            LOG.warn("Vérification en ligne de la licence en erreur : {}", e.toString());
        }
        notifier();
    }

    /** Une vérification réussie tous les {@code verification-intervalle-jours} (7), nouvel essai le lendemain après un échec. */
    private boolean verificationDue(LicenceInstallee l, OffsetDateTime maintenant) {
        OffsetDateTime derniere = l.getDerniereTentativeEnLigne();
        if (derniere == null) {
            return true;
        }
        long jours = Boolean.TRUE.equals(l.getDerniereTentativeReussie()) ? proprietes.verificationIntervalleJours() : 1;
        return !maintenant.isBefore(derniere.plusDays(jours));
    }

    private void verifierEnLigne() {
        if (!proprietes.verificationEnLigneActive()) {
            return;
        }
        Optional<LicenceInstallee> courante = repository.findByActuelleTrue();
        OffsetDateTime maintenant = OffsetDateTime.now(horloge);
        if (courante.isEmpty() || !verificationDue(courante.get(), maintenant)) {
            return;
        }
        LicenceInstallee l = courante.get();
        ReponseVerification reponse;
        try {
            reponse = serveur.verifier(new DemandeVerification(l.getLicenseId(), registre.empreinte().orElse(""),
                    versionApplication()));
        } catch (ServeurLicencesIndisponibleException e) {
            LOG.warn("Serveur de licences injoignable ({}) : le délai de grâce continue de courir", e.getMessage());
            l.enregistrerTentative(maintenant, false);
            repository.save(l);
            invalider();
            return;
        }
        boolean etaitRevoquee = l.isRevoquee();
        switch (reponse.statut()) {
            case VALIDE -> {
                l.confirmerEnLigne(maintenant);
                l.enregistrerTentative(maintenant, true);
            }
            case REVOQUEE -> {
                l.revoquer();
                l.enregistrerTentative(maintenant, true);
            }
            case INCONNUE -> l.enregistrerTentative(maintenant, false);
        }
        repository.save(l);
        if (reponse.statut() == ReponseVerification.Statut.REVOQUEE && !etaitRevoquee) {
            evenements.publishEvent(new LicenceRevoquee(l.getId(), l.getLicenseId()));
        }
        invalider();
        if (reponse.licenceRenouvelee() != null && !reponse.licenceRenouvelee().isBlank()) {
            try {
                importer(reponse.licenceRenouvelee());
            } catch (ErreurMetier e) {
                LOG.warn("Licence renouvelée envoyée par le serveur refusée : {}", e.code());
            }
        }
    }

    private void notifier() {
        Optional<LicenceInstallee> courante = repository.findByActuelleTrue();
        if (courante.isEmpty()) {
            return;
        }
        LicenceInstallee l = courante.get();
        Instantane i = instantane();
        EtatLicence etat = calculerEtat(i, maintenant());
        Licence licence = i.verification() == null ? null : i.verification().licence();
        if (licence == null) {
            return;
        }
        if (etat == EtatLicence.EXPIREE && !l.isExpirationNotifiee()) {
            l.expirationNotifiee();
            repository.save(l);
            evenements.publishEvent(new LicenceExpiree(l.getId(), l.getLicenseId(), licence.dateExpiration()));
        } else if (etat == EtatLicence.VALIDE || etat == EtatLicence.VERIFICATION_REQUISE) {
            long jours = licence.joursRestantsAu(i.jour());
            Integer seuil = seuilAtteint(jours);
            if (seuil != null && (l.getSeuilAlerteEnvoye() == null || seuil < l.getSeuilAlerteEnvoye())) {
                l.alerteEnvoyee(seuil);
                repository.save(l);
                evenements.publishEvent(new LicenceBientotExpiree(l.getId(), l.getLicenseId(), seuil, jours,
                        licence.dateExpiration()));
            }
        }
    }

    // ------------------------------------------------------------------ divers

    private String auteur() {
        return acteurCourant.acteur().map(a -> a.id().toString()).orElse("systeme");
    }

    private static String versionApplication() {
        String version = LicenceService.class.getPackage().getImplementationVersion();
        return version == null ? "dev" : version;
    }

    private Instant maintenant() {
        return horloge.instant();
    }

    private LocalDate aujourdhui() {
        return LocalDate.now(horloge.withZone(DOUALA));
    }
}
