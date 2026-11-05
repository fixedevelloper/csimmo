// ignore: unused_import
import 'package:intl/intl.dart' as intl;
import 'app_localizations.dart';

// ignore_for_file: type=lint

/// The translations for French (`fr`).
class AppLocalizationsFr extends AppLocalizations {
  AppLocalizationsFr([String locale = 'fr']) : super(locale);

  @override
  String get appTitle => 'CSImmo';

  @override
  String get serveurTitre => 'Votre serveur CSImmo';

  @override
  String get serveurExplication =>
      'Saisissez l\'adresse du serveur de votre agence ou de votre propriétaire, ou scannez le QR code qui vous a été remis.';

  @override
  String get serveurUrlLabel => 'Adresse du serveur';

  @override
  String get serveurUrlHint => 'https://immo.exemple.cm';

  @override
  String get serveurContinuer => 'Continuer';

  @override
  String get serveurVerification => 'Vérification…';

  @override
  String get serveurScanner => 'Scanner un QR code';

  @override
  String get serveurScanTitre => 'Scanner le QR code';

  @override
  String get serveurScanAide => 'Placez le QR code du serveur dans le cadre.';

  @override
  String get serveurCameraRefusee =>
      'L\'accès à la caméra est refusé. Saisissez l\'adresse à la main.';

  @override
  String get serveurQrInvalide => 'Ce QR code n\'est pas une adresse CSImmo.';

  @override
  String get connexionTitre => 'Connexion';

  @override
  String connexionServeur(String url) {
    return 'Serveur : $url';
  }

  @override
  String get identifiantLabel => 'Téléphone ou e-mail';

  @override
  String get motDePasseLabel => 'Mot de passe';

  @override
  String get seConnecter => 'Se connecter';

  @override
  String get connexionEnCours => 'Connexion…';

  @override
  String get connexionParSms => 'Recevoir un code par SMS';

  @override
  String get connexionParMotDePasse => 'Utiliser un mot de passe';

  @override
  String get recevoirCode => 'Recevoir le code';

  @override
  String get codeLabel => 'Code reçu par SMS';

  @override
  String get verifier => 'Vérifier';

  @override
  String get deuxFacteursInfo =>
      'Un code à 6 chiffres vous a été envoyé par SMS.';

  @override
  String get codeEnvoye =>
      'Si ce numéro est enregistré, un code vient d\'être envoyé par SMS.';

  @override
  String get retour => 'Retour';

  @override
  String get changerServeur => 'Changer de serveur';

  @override
  String get champRequis => 'Ce champ est obligatoire';

  @override
  String get codeSixChiffres => 'Le code comporte 6 chiffres';

  @override
  String get deconnexion => 'Se déconnecter';

  @override
  String bienvenue(String nom) {
    return 'Bienvenue, $nom';
  }

  @override
  String get bientotDisponible => 'Cet espace sera disponible prochainement.';

  @override
  String get espaceLocataire => 'Mon espace locataire';

  @override
  String get espaceAgent => 'Espace gestion';

  @override
  String get espacePrestataire => 'Espace prestataire';

  @override
  String get espaceProprietaire => 'Espace propriétaire';

  @override
  String get chargement => 'Chargement…';

  @override
  String get reessayer => 'Réessayer';

  @override
  String get errInconnue => 'Une erreur inattendue est survenue. Réessayez.';

  @override
  String get errServeurIndisponible =>
      'Impossible de joindre le serveur. Vérifiez votre connexion et l\'adresse.';

  @override
  String get errServeurNonImmogest =>
      'Cette adresse ne répond pas comme un serveur CSImmo.';

  @override
  String get errUrlInvalide =>
      'Adresse invalide. Exemple : https://immo.exemple.cm';

  @override
  String get errUrlHttpRefusee =>
      'Une adresse publique doit commencer par https:// (connexion sécurisée).';

  @override
  String get errIdentifiantsInvalides =>
      'Identifiant ou mot de passe incorrect.';

  @override
  String get errCompteVerrouille =>
      'Compte temporairement verrouillé après trop d\'échecs. Réessayez dans 15 minutes.';

  @override
  String get errCompteDesactive => 'Ce compte est désactivé.';

  @override
  String get errOtpInvalide => 'Code invalide ou expiré.';

  @override
  String get errAccesRefuse => 'Vous n\'avez pas les droits nécessaires.';

  @override
  String get errSessionExpiree => 'Votre session a expiré. Reconnectez-vous.';

  @override
  String get errLicenceExpiree =>
      'La licence de l\'application a expiré : consultation seulement.';

  @override
  String get ongletAccueil => 'Accueil';

  @override
  String get ongletBaux => 'Mes baux';

  @override
  String get ongletDocuments => 'Quittances';

  @override
  String get ongletSignaler => 'Signaler';

  @override
  String get soldeDu => 'Solde à régler';

  @override
  String get soldeAJour => 'Vous êtes à jour';

  @override
  String get arrieres => 'Loyers échus impayés';

  @override
  String get aVenir => 'À venir';

  @override
  String get credit => 'Crédit non imputé';

  @override
  String get payer => 'Payer';

  @override
  String get payerBientot =>
      'Le paiement par Mobile Money sera bientôt disponible.';

  @override
  String get aucunBail => 'Aucun bail pour le moment.';

  @override
  String get bailActif => 'En cours';

  @override
  String get bailResilie => 'Résilié';

  @override
  String get bailTermine => 'Terminé';

  @override
  String get bailBrouillon => 'Brouillon';

  @override
  String get echeances => 'Échéances';

  @override
  String get echeancePayee => 'Payée';

  @override
  String get echeanceEnRetard => 'En retard';

  @override
  String get echeancePartielle => 'Partielle';

  @override
  String get echeanceDue => 'À payer';

  @override
  String get echeanceAVenir => 'À venir';

  @override
  String get loyerMensuel => 'Loyer mensuel';

  @override
  String get chargesMensuelles => 'Charges mensuelles';

  @override
  String get caution => 'Caution';

  @override
  String get debutBail => 'Début du bail';

  @override
  String get finBail => 'Fin du bail';

  @override
  String get sansDateFin => 'Sans date de fin';

  @override
  String get sortiePrevue => 'Sortie prévue';

  @override
  String avantLe(String date) {
    return 'avant le $date';
  }

  @override
  String resteAPayer(String montant) {
    return 'reste $montant';
  }

  @override
  String get aucunDocument => 'Aucune quittance ni aucun reçu pour le moment.';

  @override
  String get quittance => 'Quittance';

  @override
  String get recu => 'Reçu';

  @override
  String get signalerTitre => 'Signaler une panne';

  @override
  String get signalerDescription => 'Décrivez la panne';

  @override
  String get signalerDescriptionCourte =>
      'Décrivez la panne en quelques mots (5 caractères minimum).';

  @override
  String get photoCamera => 'Prendre une photo';

  @override
  String get photoGalerie => 'Galerie';

  @override
  String get retirerPhoto => 'Retirer la photo';

  @override
  String get signalerEnvoyer => 'Envoyer le signalement';

  @override
  String get envoiEnCours => 'Envoi…';

  @override
  String get signalementEnvoye =>
      'Signalement envoyé. Votre gestionnaire va le traiter.';

  @override
  String get mesSignalements => 'Mes signalements';

  @override
  String get aucunSignalement => 'Aucun signalement pour le moment.';

  @override
  String get signalementNouveau => 'Reçu';

  @override
  String get signalementEnCours => 'En cours';

  @override
  String get signalementResolu => 'Résolu';

  @override
  String get signalementRejete => 'Rejeté';

  @override
  String get errSignalementBailRequis => 'Précisez le bail concerné.';

  @override
  String get errSignalementAucunBail => 'Vous n\'avez aucun bail en cours.';

  @override
  String get errSignalementLimite =>
      'Trop de signalements en attente : patientez jusqu\'à leur prise en charge.';

  @override
  String get errSignalementDescription =>
      'Décrivez la panne en 5 à 2000 caractères.';

  @override
  String get errFichierInvalide =>
      'Cette photo n\'est pas acceptée (formats JPEG, PNG ou WebP, 10 Mo au plus).';

  @override
  String get errLocataireIntrouvable =>
      'Aucune fiche locataire n\'est rattachée à votre compte. Contactez votre gestionnaire.';

  @override
  String get ongletEncaisser => 'Encaisser';

  @override
  String get ongletAEnvoyer => 'À envoyer';

  @override
  String get ongletLots => 'Lots';

  @override
  String get ongletLocataires => 'Locataires';

  @override
  String donneesDu(String date) {
    return 'Données du $date';
  }

  @override
  String get donneesAbsentes => 'Aucune donnée enregistrée sur ce téléphone.';

  @override
  String get donneesAbsentesAide =>
      'Actualisez les données (avec du réseau) pour pouvoir saisir sans réseau ensuite.';

  @override
  String get actualiserDonnees => 'Actualiser';

  @override
  String get donneesActualisees => 'Données actualisées.';

  @override
  String get donneesAnciennes => 'Les données précédentes restent utilisables.';

  @override
  String get rechercherLocataire => 'Rechercher un locataire';

  @override
  String get aucunLocataire => 'Aucun locataire trouvé.';

  @override
  String get changer => 'Changer';

  @override
  String get aucunBailLocataire => 'Ce locataire n\'a aucun bail enregistré.';

  @override
  String get choisirBailErreur => 'Choisissez le bail concerné.';

  @override
  String mensuel(String montant) {
    return '$montant par mois';
  }

  @override
  String get montantFcfa => 'Montant encaissé (FCFA)';

  @override
  String get montantInvalide => 'Saisissez un montant entier en FCFA.';

  @override
  String get enregistrerEncaissement => 'Enregistrer l\'encaissement';

  @override
  String get encaissementEnregistre =>
      'Encaissement enregistré sur le téléphone. Il sera envoyé dès que possible.';

  @override
  String get recuSmsInfo =>
      'Le reçu SMS est envoyé au locataire quand l\'encaissement arrive sur le serveur.';

  @override
  String get synchroniserMaintenant => 'Envoyer maintenant';

  @override
  String get fileVide => 'Aucun encaissement saisi récemment.';

  @override
  String get statutEnAttente => 'En attente';

  @override
  String get statutEnvoye => 'Envoyé';

  @override
  String get statutRejete => 'Refusé';

  @override
  String get supprimerSaisie => 'Supprimer la saisie';

  @override
  String get saisieSupprimee => 'Saisie supprimée.';

  @override
  String get abandonImpossible =>
      'Impossible : cet encaissement a déjà été présenté au serveur et a pu y être enregistré.';

  @override
  String synchroTerminee(int envoyes, int rejetes) {
    return 'Envoi terminé : $envoyes envoyé(s), $rejetes refusé(s).';
  }

  @override
  String synchroInterrompue(String raison, int restants) {
    return '$raison $restants encaissement(s) restent en attente.';
  }

  @override
  String deconnexionAvecFile(int n) {
    return '$n encaissement(s) non envoyé(s) seront conservés sur ce téléphone et envoyés à votre prochaine connexion avec ce compte.';
  }

  @override
  String get annuler => 'Annuler';

  @override
  String get rechercherLot => 'Rechercher un lot';

  @override
  String get aucunLot => 'Aucun lot.';

  @override
  String get lotLibre => 'Libre';

  @override
  String get lotOccupe => 'Occupé';

  @override
  String get lotEnTravaux => 'En travaux';

  @override
  String get errBailIntrouvable =>
      'Bail introuvable : l\'encaissement ne peut pas être enregistré.';

  @override
  String get errPaiementInvalide => 'Montant ou informations invalides.';

  @override
  String get errPaiementDateFuture =>
      'La date du paiement est dans le futur : vérifiez l\'heure du téléphone.';

  @override
  String get errIdempotence =>
      'Cette saisie a déjà été utilisée pour un autre montant.';

  @override
  String get payerTitre => 'Payer mon loyer';

  @override
  String get payerBail => 'Bail à payer';

  @override
  String get payerTelephone => 'Numéro Mobile Money qui paie';

  @override
  String get payerTelephoneInvalide =>
      'Saisissez un numéro valide, au format international.';

  @override
  String get payerConfirmer => 'Payer';

  @override
  String get operateurMtn => 'MTN MoMo';

  @override
  String get operateurOrange => 'Orange Money';

  @override
  String get payerAttenteTitre => 'En attente de votre validation';

  @override
  String get payerAttenteAide =>
      'Validez le paiement sur votre téléphone (code secret). Cette page se met à jour toute seule.';

  @override
  String get payerDelai =>
      'Toujours pas de confirmation. Si vous avez validé, le paiement sera enregistré dès que l\'opérateur le confirmera : vous pouvez fermer cette page.';

  @override
  String get payerContinuerSuivi => 'Continuer à attendre';

  @override
  String get payerFermer => 'Fermer';

  @override
  String payerSucces(String montant) {
    return 'Paiement de $montant confirmé. Merci !';
  }

  @override
  String get payerRefusee =>
      'Le paiement n\'a pas abouti. Aucun montant n\'a été enregistré.';

  @override
  String get payerExpiree =>
      'Le paiement a expiré sans confirmation. Aucun montant n\'a été enregistré.';

  @override
  String get payerAnomalie =>
      'Le montant confirmé par l\'opérateur diffère de celui demandé. Votre gestionnaire va vérifier.';

  @override
  String get errAgregateur =>
      'Le service de paiement est momentanément indisponible. Réessayez dans quelques instants.';

  @override
  String get errPaiementEnLigneIndisponible =>
      'Le paiement en ligne n\'est pas disponible chez votre gestionnaire.';

  @override
  String get errTropDeTransactions =>
      'Vous avez déjà des paiements en attente : validez-les ou patientez.';
}
