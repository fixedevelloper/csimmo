import 'dart:async';

import 'package:flutter/foundation.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:intl/intl.dart' as intl;

import 'app_localizations_en.dart';
import 'app_localizations_fr.dart';

// ignore_for_file: type=lint

/// Callers can lookup localized strings with an instance of AppLocalizations
/// returned by `AppLocalizations.of(context)`.
///
/// Applications need to include `AppLocalizations.delegate()` in their app's
/// `localizationDelegates` list, and the locales they support in the app's
/// `supportedLocales` list. For example:
///
/// ```dart
/// import 'l10n/app_localizations.dart';
///
/// return MaterialApp(
///   localizationsDelegates: AppLocalizations.localizationsDelegates,
///   supportedLocales: AppLocalizations.supportedLocales,
///   home: MyApplicationHome(),
/// );
/// ```
///
/// ## Update pubspec.yaml
///
/// Please make sure to update your pubspec.yaml to include the following
/// packages:
///
/// ```yaml
/// dependencies:
///   # Internationalization support.
///   flutter_localizations:
///     sdk: flutter
///   intl: any # Use the pinned version from flutter_localizations
///
///   # Rest of dependencies
/// ```
///
/// ## iOS Applications
///
/// iOS applications define key application metadata, including supported
/// locales, in an Info.plist file that is built into the application bundle.
/// To configure the locales supported by your app, you’ll need to edit this
/// file.
///
/// First, open your project’s ios/Runner.xcworkspace Xcode workspace file.
/// Then, in the Project Navigator, open the Info.plist file under the Runner
/// project’s Runner folder.
///
/// Next, select the Information Property List item, select Add Item from the
/// Editor menu, then select Localizations from the pop-up menu.
///
/// Select and expand the newly-created Localizations item then, for each
/// locale your application supports, add a new item and select the locale
/// you wish to add from the pop-up menu in the Value field. This list should
/// be consistent with the languages listed in the AppLocalizations.supportedLocales
/// property.
abstract class AppLocalizations {
  AppLocalizations(String locale)
    : localeName = intl.Intl.canonicalizedLocale(locale.toString());

  final String localeName;

  static AppLocalizations of(BuildContext context) {
    return Localizations.of<AppLocalizations>(context, AppLocalizations)!;
  }

  static const LocalizationsDelegate<AppLocalizations> delegate =
      _AppLocalizationsDelegate();

  /// A list of this localizations delegate along with the default localizations
  /// delegates.
  ///
  /// Returns a list of localizations delegates containing this delegate along with
  /// GlobalMaterialLocalizations.delegate, GlobalCupertinoLocalizations.delegate,
  /// and GlobalWidgetsLocalizations.delegate.
  ///
  /// Additional delegates can be added by appending to this list in
  /// MaterialApp. This list does not have to be used at all if a custom list
  /// of delegates is preferred or required.
  static const List<LocalizationsDelegate<dynamic>> localizationsDelegates =
      <LocalizationsDelegate<dynamic>>[
        delegate,
        GlobalMaterialLocalizations.delegate,
        GlobalCupertinoLocalizations.delegate,
        GlobalWidgetsLocalizations.delegate,
      ];

  /// A list of this localizations delegate's supported locales.
  static const List<Locale> supportedLocales = <Locale>[
    Locale('en'),
    Locale('fr'),
  ];

  /// No description provided for @appTitle.
  ///
  /// In fr, this message translates to:
  /// **'CSImmo'**
  String get appTitle;

  /// No description provided for @serveurTitre.
  ///
  /// In fr, this message translates to:
  /// **'Votre serveur CSImmo'**
  String get serveurTitre;

  /// No description provided for @serveurExplication.
  ///
  /// In fr, this message translates to:
  /// **'Saisissez l\'adresse du serveur de votre agence ou de votre propriétaire, ou scannez le QR code qui vous a été remis.'**
  String get serveurExplication;

  /// No description provided for @serveurUrlLabel.
  ///
  /// In fr, this message translates to:
  /// **'Adresse du serveur'**
  String get serveurUrlLabel;

  /// No description provided for @serveurUrlHint.
  ///
  /// In fr, this message translates to:
  /// **'https://immo.exemple.cm'**
  String get serveurUrlHint;

  /// No description provided for @serveurContinuer.
  ///
  /// In fr, this message translates to:
  /// **'Continuer'**
  String get serveurContinuer;

  /// No description provided for @serveurVerification.
  ///
  /// In fr, this message translates to:
  /// **'Vérification…'**
  String get serveurVerification;

  /// No description provided for @serveurScanner.
  ///
  /// In fr, this message translates to:
  /// **'Scanner un QR code'**
  String get serveurScanner;

  /// No description provided for @serveurScanTitre.
  ///
  /// In fr, this message translates to:
  /// **'Scanner le QR code'**
  String get serveurScanTitre;

  /// No description provided for @serveurScanAide.
  ///
  /// In fr, this message translates to:
  /// **'Placez le QR code du serveur dans le cadre.'**
  String get serveurScanAide;

  /// No description provided for @serveurCameraRefusee.
  ///
  /// In fr, this message translates to:
  /// **'L\'accès à la caméra est refusé. Saisissez l\'adresse à la main.'**
  String get serveurCameraRefusee;

  /// No description provided for @serveurQrInvalide.
  ///
  /// In fr, this message translates to:
  /// **'Ce QR code n\'est pas une adresse CSImmo.'**
  String get serveurQrInvalide;

  /// No description provided for @connexionTitre.
  ///
  /// In fr, this message translates to:
  /// **'Connexion'**
  String get connexionTitre;

  /// No description provided for @connexionServeur.
  ///
  /// In fr, this message translates to:
  /// **'Serveur : {url}'**
  String connexionServeur(String url);

  /// No description provided for @identifiantLabel.
  ///
  /// In fr, this message translates to:
  /// **'Téléphone ou e-mail'**
  String get identifiantLabel;

  /// No description provided for @motDePasseLabel.
  ///
  /// In fr, this message translates to:
  /// **'Mot de passe'**
  String get motDePasseLabel;

  /// No description provided for @seConnecter.
  ///
  /// In fr, this message translates to:
  /// **'Se connecter'**
  String get seConnecter;

  /// No description provided for @connexionEnCours.
  ///
  /// In fr, this message translates to:
  /// **'Connexion…'**
  String get connexionEnCours;

  /// No description provided for @connexionParSms.
  ///
  /// In fr, this message translates to:
  /// **'Recevoir un code par SMS'**
  String get connexionParSms;

  /// No description provided for @connexionParMotDePasse.
  ///
  /// In fr, this message translates to:
  /// **'Utiliser un mot de passe'**
  String get connexionParMotDePasse;

  /// No description provided for @recevoirCode.
  ///
  /// In fr, this message translates to:
  /// **'Recevoir le code'**
  String get recevoirCode;

  /// No description provided for @codeLabel.
  ///
  /// In fr, this message translates to:
  /// **'Code reçu par SMS'**
  String get codeLabel;

  /// No description provided for @verifier.
  ///
  /// In fr, this message translates to:
  /// **'Vérifier'**
  String get verifier;

  /// No description provided for @deuxFacteursInfo.
  ///
  /// In fr, this message translates to:
  /// **'Un code à 6 chiffres vous a été envoyé par SMS.'**
  String get deuxFacteursInfo;

  /// No description provided for @codeEnvoye.
  ///
  /// In fr, this message translates to:
  /// **'Si ce numéro est enregistré, un code vient d\'être envoyé par SMS.'**
  String get codeEnvoye;

  /// No description provided for @retour.
  ///
  /// In fr, this message translates to:
  /// **'Retour'**
  String get retour;

  /// No description provided for @changerServeur.
  ///
  /// In fr, this message translates to:
  /// **'Changer de serveur'**
  String get changerServeur;

  /// No description provided for @champRequis.
  ///
  /// In fr, this message translates to:
  /// **'Ce champ est obligatoire'**
  String get champRequis;

  /// No description provided for @codeSixChiffres.
  ///
  /// In fr, this message translates to:
  /// **'Le code comporte 6 chiffres'**
  String get codeSixChiffres;

  /// No description provided for @deconnexion.
  ///
  /// In fr, this message translates to:
  /// **'Se déconnecter'**
  String get deconnexion;

  /// No description provided for @bienvenue.
  ///
  /// In fr, this message translates to:
  /// **'Bienvenue, {nom}'**
  String bienvenue(String nom);

  /// No description provided for @bientotDisponible.
  ///
  /// In fr, this message translates to:
  /// **'Cet espace sera disponible prochainement.'**
  String get bientotDisponible;

  /// No description provided for @espaceLocataire.
  ///
  /// In fr, this message translates to:
  /// **'Mon espace locataire'**
  String get espaceLocataire;

  /// No description provided for @espaceAgent.
  ///
  /// In fr, this message translates to:
  /// **'Espace gestion'**
  String get espaceAgent;

  /// No description provided for @espacePrestataire.
  ///
  /// In fr, this message translates to:
  /// **'Espace prestataire'**
  String get espacePrestataire;

  /// No description provided for @espaceProprietaire.
  ///
  /// In fr, this message translates to:
  /// **'Espace propriétaire'**
  String get espaceProprietaire;

  /// No description provided for @chargement.
  ///
  /// In fr, this message translates to:
  /// **'Chargement…'**
  String get chargement;

  /// No description provided for @reessayer.
  ///
  /// In fr, this message translates to:
  /// **'Réessayer'**
  String get reessayer;

  /// No description provided for @errInconnue.
  ///
  /// In fr, this message translates to:
  /// **'Une erreur inattendue est survenue. Réessayez.'**
  String get errInconnue;

  /// No description provided for @errServeurIndisponible.
  ///
  /// In fr, this message translates to:
  /// **'Impossible de joindre le serveur. Vérifiez votre connexion et l\'adresse.'**
  String get errServeurIndisponible;

  /// No description provided for @errServeurNonImmogest.
  ///
  /// In fr, this message translates to:
  /// **'Cette adresse ne répond pas comme un serveur CSImmo.'**
  String get errServeurNonImmogest;

  /// No description provided for @errUrlInvalide.
  ///
  /// In fr, this message translates to:
  /// **'Adresse invalide. Exemple : https://immo.exemple.cm'**
  String get errUrlInvalide;

  /// No description provided for @errUrlHttpRefusee.
  ///
  /// In fr, this message translates to:
  /// **'Une adresse publique doit commencer par https:// (connexion sécurisée).'**
  String get errUrlHttpRefusee;

  /// No description provided for @errIdentifiantsInvalides.
  ///
  /// In fr, this message translates to:
  /// **'Identifiant ou mot de passe incorrect.'**
  String get errIdentifiantsInvalides;

  /// No description provided for @errCompteVerrouille.
  ///
  /// In fr, this message translates to:
  /// **'Compte temporairement verrouillé après trop d\'échecs. Réessayez dans 15 minutes.'**
  String get errCompteVerrouille;

  /// No description provided for @errCompteDesactive.
  ///
  /// In fr, this message translates to:
  /// **'Ce compte est désactivé.'**
  String get errCompteDesactive;

  /// No description provided for @errOtpInvalide.
  ///
  /// In fr, this message translates to:
  /// **'Code invalide ou expiré.'**
  String get errOtpInvalide;

  /// No description provided for @errAccesRefuse.
  ///
  /// In fr, this message translates to:
  /// **'Vous n\'avez pas les droits nécessaires.'**
  String get errAccesRefuse;

  /// No description provided for @errSessionExpiree.
  ///
  /// In fr, this message translates to:
  /// **'Votre session a expiré. Reconnectez-vous.'**
  String get errSessionExpiree;

  /// No description provided for @errLicenceExpiree.
  ///
  /// In fr, this message translates to:
  /// **'La licence de l\'application a expiré : consultation seulement.'**
  String get errLicenceExpiree;

  /// No description provided for @ongletAccueil.
  ///
  /// In fr, this message translates to:
  /// **'Accueil'**
  String get ongletAccueil;

  /// No description provided for @ongletBaux.
  ///
  /// In fr, this message translates to:
  /// **'Mes baux'**
  String get ongletBaux;

  /// No description provided for @ongletDocuments.
  ///
  /// In fr, this message translates to:
  /// **'Quittances'**
  String get ongletDocuments;

  /// No description provided for @ongletSignaler.
  ///
  /// In fr, this message translates to:
  /// **'Signaler'**
  String get ongletSignaler;

  /// No description provided for @soldeDu.
  ///
  /// In fr, this message translates to:
  /// **'Solde à régler'**
  String get soldeDu;

  /// No description provided for @soldeAJour.
  ///
  /// In fr, this message translates to:
  /// **'Vous êtes à jour'**
  String get soldeAJour;

  /// No description provided for @arrieres.
  ///
  /// In fr, this message translates to:
  /// **'Loyers échus impayés'**
  String get arrieres;

  /// No description provided for @aVenir.
  ///
  /// In fr, this message translates to:
  /// **'À venir'**
  String get aVenir;

  /// No description provided for @credit.
  ///
  /// In fr, this message translates to:
  /// **'Crédit non imputé'**
  String get credit;

  /// No description provided for @payer.
  ///
  /// In fr, this message translates to:
  /// **'Payer'**
  String get payer;

  /// No description provided for @payerBientot.
  ///
  /// In fr, this message translates to:
  /// **'Le paiement par Mobile Money sera bientôt disponible.'**
  String get payerBientot;

  /// No description provided for @aucunBail.
  ///
  /// In fr, this message translates to:
  /// **'Aucun bail pour le moment.'**
  String get aucunBail;

  /// No description provided for @bailActif.
  ///
  /// In fr, this message translates to:
  /// **'En cours'**
  String get bailActif;

  /// No description provided for @bailResilie.
  ///
  /// In fr, this message translates to:
  /// **'Résilié'**
  String get bailResilie;

  /// No description provided for @bailTermine.
  ///
  /// In fr, this message translates to:
  /// **'Terminé'**
  String get bailTermine;

  /// No description provided for @bailBrouillon.
  ///
  /// In fr, this message translates to:
  /// **'Brouillon'**
  String get bailBrouillon;

  /// No description provided for @echeances.
  ///
  /// In fr, this message translates to:
  /// **'Échéances'**
  String get echeances;

  /// No description provided for @echeancePayee.
  ///
  /// In fr, this message translates to:
  /// **'Payée'**
  String get echeancePayee;

  /// No description provided for @echeanceEnRetard.
  ///
  /// In fr, this message translates to:
  /// **'En retard'**
  String get echeanceEnRetard;

  /// No description provided for @echeancePartielle.
  ///
  /// In fr, this message translates to:
  /// **'Partielle'**
  String get echeancePartielle;

  /// No description provided for @echeanceDue.
  ///
  /// In fr, this message translates to:
  /// **'À payer'**
  String get echeanceDue;

  /// No description provided for @echeanceAVenir.
  ///
  /// In fr, this message translates to:
  /// **'À venir'**
  String get echeanceAVenir;

  /// No description provided for @loyerMensuel.
  ///
  /// In fr, this message translates to:
  /// **'Loyer mensuel'**
  String get loyerMensuel;

  /// No description provided for @chargesMensuelles.
  ///
  /// In fr, this message translates to:
  /// **'Charges mensuelles'**
  String get chargesMensuelles;

  /// No description provided for @caution.
  ///
  /// In fr, this message translates to:
  /// **'Caution'**
  String get caution;

  /// No description provided for @debutBail.
  ///
  /// In fr, this message translates to:
  /// **'Début du bail'**
  String get debutBail;

  /// No description provided for @finBail.
  ///
  /// In fr, this message translates to:
  /// **'Fin du bail'**
  String get finBail;

  /// No description provided for @sansDateFin.
  ///
  /// In fr, this message translates to:
  /// **'Sans date de fin'**
  String get sansDateFin;

  /// No description provided for @sortiePrevue.
  ///
  /// In fr, this message translates to:
  /// **'Sortie prévue'**
  String get sortiePrevue;

  /// No description provided for @avantLe.
  ///
  /// In fr, this message translates to:
  /// **'avant le {date}'**
  String avantLe(String date);

  /// No description provided for @resteAPayer.
  ///
  /// In fr, this message translates to:
  /// **'reste {montant}'**
  String resteAPayer(String montant);

  /// No description provided for @aucunDocument.
  ///
  /// In fr, this message translates to:
  /// **'Aucune quittance ni aucun reçu pour le moment.'**
  String get aucunDocument;

  /// No description provided for @quittance.
  ///
  /// In fr, this message translates to:
  /// **'Quittance'**
  String get quittance;

  /// No description provided for @recu.
  ///
  /// In fr, this message translates to:
  /// **'Reçu'**
  String get recu;

  /// No description provided for @signalerTitre.
  ///
  /// In fr, this message translates to:
  /// **'Signaler une panne'**
  String get signalerTitre;

  /// No description provided for @signalerDescription.
  ///
  /// In fr, this message translates to:
  /// **'Décrivez la panne'**
  String get signalerDescription;

  /// No description provided for @signalerDescriptionCourte.
  ///
  /// In fr, this message translates to:
  /// **'Décrivez la panne en quelques mots (5 caractères minimum).'**
  String get signalerDescriptionCourte;

  /// No description provided for @photoCamera.
  ///
  /// In fr, this message translates to:
  /// **'Prendre une photo'**
  String get photoCamera;

  /// No description provided for @photoGalerie.
  ///
  /// In fr, this message translates to:
  /// **'Galerie'**
  String get photoGalerie;

  /// No description provided for @retirerPhoto.
  ///
  /// In fr, this message translates to:
  /// **'Retirer la photo'**
  String get retirerPhoto;

  /// No description provided for @signalerEnvoyer.
  ///
  /// In fr, this message translates to:
  /// **'Envoyer le signalement'**
  String get signalerEnvoyer;

  /// No description provided for @envoiEnCours.
  ///
  /// In fr, this message translates to:
  /// **'Envoi…'**
  String get envoiEnCours;

  /// No description provided for @signalementEnvoye.
  ///
  /// In fr, this message translates to:
  /// **'Signalement envoyé. Votre gestionnaire va le traiter.'**
  String get signalementEnvoye;

  /// No description provided for @mesSignalements.
  ///
  /// In fr, this message translates to:
  /// **'Mes signalements'**
  String get mesSignalements;

  /// No description provided for @aucunSignalement.
  ///
  /// In fr, this message translates to:
  /// **'Aucun signalement pour le moment.'**
  String get aucunSignalement;

  /// No description provided for @signalementNouveau.
  ///
  /// In fr, this message translates to:
  /// **'Reçu'**
  String get signalementNouveau;

  /// No description provided for @signalementEnCours.
  ///
  /// In fr, this message translates to:
  /// **'En cours'**
  String get signalementEnCours;

  /// No description provided for @signalementResolu.
  ///
  /// In fr, this message translates to:
  /// **'Résolu'**
  String get signalementResolu;

  /// No description provided for @signalementRejete.
  ///
  /// In fr, this message translates to:
  /// **'Rejeté'**
  String get signalementRejete;

  /// No description provided for @errSignalementBailRequis.
  ///
  /// In fr, this message translates to:
  /// **'Précisez le bail concerné.'**
  String get errSignalementBailRequis;

  /// No description provided for @errSignalementAucunBail.
  ///
  /// In fr, this message translates to:
  /// **'Vous n\'avez aucun bail en cours.'**
  String get errSignalementAucunBail;

  /// No description provided for @errSignalementLimite.
  ///
  /// In fr, this message translates to:
  /// **'Trop de signalements en attente : patientez jusqu\'à leur prise en charge.'**
  String get errSignalementLimite;

  /// No description provided for @errSignalementDescription.
  ///
  /// In fr, this message translates to:
  /// **'Décrivez la panne en 5 à 2000 caractères.'**
  String get errSignalementDescription;

  /// No description provided for @errFichierInvalide.
  ///
  /// In fr, this message translates to:
  /// **'Cette photo n\'est pas acceptée (formats JPEG, PNG ou WebP, 10 Mo au plus).'**
  String get errFichierInvalide;

  /// No description provided for @errLocataireIntrouvable.
  ///
  /// In fr, this message translates to:
  /// **'Aucune fiche locataire n\'est rattachée à votre compte. Contactez votre gestionnaire.'**
  String get errLocataireIntrouvable;

  /// No description provided for @ongletEncaisser.
  ///
  /// In fr, this message translates to:
  /// **'Encaisser'**
  String get ongletEncaisser;

  /// No description provided for @ongletAEnvoyer.
  ///
  /// In fr, this message translates to:
  /// **'À envoyer'**
  String get ongletAEnvoyer;

  /// No description provided for @ongletLots.
  ///
  /// In fr, this message translates to:
  /// **'Lots'**
  String get ongletLots;

  /// No description provided for @ongletLocataires.
  ///
  /// In fr, this message translates to:
  /// **'Locataires'**
  String get ongletLocataires;

  /// No description provided for @donneesDu.
  ///
  /// In fr, this message translates to:
  /// **'Données du {date}'**
  String donneesDu(String date);

  /// No description provided for @donneesAbsentes.
  ///
  /// In fr, this message translates to:
  /// **'Aucune donnée enregistrée sur ce téléphone.'**
  String get donneesAbsentes;

  /// No description provided for @donneesAbsentesAide.
  ///
  /// In fr, this message translates to:
  /// **'Actualisez les données (avec du réseau) pour pouvoir saisir sans réseau ensuite.'**
  String get donneesAbsentesAide;

  /// No description provided for @actualiserDonnees.
  ///
  /// In fr, this message translates to:
  /// **'Actualiser'**
  String get actualiserDonnees;

  /// No description provided for @donneesActualisees.
  ///
  /// In fr, this message translates to:
  /// **'Données actualisées.'**
  String get donneesActualisees;

  /// No description provided for @donneesAnciennes.
  ///
  /// In fr, this message translates to:
  /// **'Les données précédentes restent utilisables.'**
  String get donneesAnciennes;

  /// No description provided for @rechercherLocataire.
  ///
  /// In fr, this message translates to:
  /// **'Rechercher un locataire'**
  String get rechercherLocataire;

  /// No description provided for @aucunLocataire.
  ///
  /// In fr, this message translates to:
  /// **'Aucun locataire trouvé.'**
  String get aucunLocataire;

  /// No description provided for @changer.
  ///
  /// In fr, this message translates to:
  /// **'Changer'**
  String get changer;

  /// No description provided for @aucunBailLocataire.
  ///
  /// In fr, this message translates to:
  /// **'Ce locataire n\'a aucun bail enregistré.'**
  String get aucunBailLocataire;

  /// No description provided for @choisirBailErreur.
  ///
  /// In fr, this message translates to:
  /// **'Choisissez le bail concerné.'**
  String get choisirBailErreur;

  /// No description provided for @mensuel.
  ///
  /// In fr, this message translates to:
  /// **'{montant} par mois'**
  String mensuel(String montant);

  /// No description provided for @montantFcfa.
  ///
  /// In fr, this message translates to:
  /// **'Montant encaissé (FCFA)'**
  String get montantFcfa;

  /// No description provided for @montantInvalide.
  ///
  /// In fr, this message translates to:
  /// **'Saisissez un montant entier en FCFA.'**
  String get montantInvalide;

  /// No description provided for @enregistrerEncaissement.
  ///
  /// In fr, this message translates to:
  /// **'Enregistrer l\'encaissement'**
  String get enregistrerEncaissement;

  /// No description provided for @encaissementEnregistre.
  ///
  /// In fr, this message translates to:
  /// **'Encaissement enregistré sur le téléphone. Il sera envoyé dès que possible.'**
  String get encaissementEnregistre;

  /// No description provided for @recuSmsInfo.
  ///
  /// In fr, this message translates to:
  /// **'Le reçu SMS est envoyé au locataire quand l\'encaissement arrive sur le serveur.'**
  String get recuSmsInfo;

  /// No description provided for @synchroniserMaintenant.
  ///
  /// In fr, this message translates to:
  /// **'Envoyer maintenant'**
  String get synchroniserMaintenant;

  /// No description provided for @fileVide.
  ///
  /// In fr, this message translates to:
  /// **'Aucun encaissement saisi récemment.'**
  String get fileVide;

  /// No description provided for @statutEnAttente.
  ///
  /// In fr, this message translates to:
  /// **'En attente'**
  String get statutEnAttente;

  /// No description provided for @statutEnvoye.
  ///
  /// In fr, this message translates to:
  /// **'Envoyé'**
  String get statutEnvoye;

  /// No description provided for @statutRejete.
  ///
  /// In fr, this message translates to:
  /// **'Refusé'**
  String get statutRejete;

  /// No description provided for @supprimerSaisie.
  ///
  /// In fr, this message translates to:
  /// **'Supprimer la saisie'**
  String get supprimerSaisie;

  /// No description provided for @saisieSupprimee.
  ///
  /// In fr, this message translates to:
  /// **'Saisie supprimée.'**
  String get saisieSupprimee;

  /// No description provided for @abandonImpossible.
  ///
  /// In fr, this message translates to:
  /// **'Impossible : cet encaissement a déjà été présenté au serveur et a pu y être enregistré.'**
  String get abandonImpossible;

  /// No description provided for @synchroTerminee.
  ///
  /// In fr, this message translates to:
  /// **'Envoi terminé : {envoyes} envoyé(s), {rejetes} refusé(s).'**
  String synchroTerminee(int envoyes, int rejetes);

  /// No description provided for @synchroInterrompue.
  ///
  /// In fr, this message translates to:
  /// **'{raison} {restants} encaissement(s) restent en attente.'**
  String synchroInterrompue(String raison, int restants);

  /// No description provided for @deconnexionAvecFile.
  ///
  /// In fr, this message translates to:
  /// **'{n} encaissement(s) non envoyé(s) seront conservés sur ce téléphone et envoyés à votre prochaine connexion avec ce compte.'**
  String deconnexionAvecFile(int n);

  /// No description provided for @annuler.
  ///
  /// In fr, this message translates to:
  /// **'Annuler'**
  String get annuler;

  /// No description provided for @rechercherLot.
  ///
  /// In fr, this message translates to:
  /// **'Rechercher un lot'**
  String get rechercherLot;

  /// No description provided for @aucunLot.
  ///
  /// In fr, this message translates to:
  /// **'Aucun lot.'**
  String get aucunLot;

  /// No description provided for @lotLibre.
  ///
  /// In fr, this message translates to:
  /// **'Libre'**
  String get lotLibre;

  /// No description provided for @lotOccupe.
  ///
  /// In fr, this message translates to:
  /// **'Occupé'**
  String get lotOccupe;

  /// No description provided for @lotEnTravaux.
  ///
  /// In fr, this message translates to:
  /// **'En travaux'**
  String get lotEnTravaux;

  /// No description provided for @errBailIntrouvable.
  ///
  /// In fr, this message translates to:
  /// **'Bail introuvable : l\'encaissement ne peut pas être enregistré.'**
  String get errBailIntrouvable;

  /// No description provided for @errPaiementInvalide.
  ///
  /// In fr, this message translates to:
  /// **'Montant ou informations invalides.'**
  String get errPaiementInvalide;

  /// No description provided for @errPaiementDateFuture.
  ///
  /// In fr, this message translates to:
  /// **'La date du paiement est dans le futur : vérifiez l\'heure du téléphone.'**
  String get errPaiementDateFuture;

  /// No description provided for @errIdempotence.
  ///
  /// In fr, this message translates to:
  /// **'Cette saisie a déjà été utilisée pour un autre montant.'**
  String get errIdempotence;

  /// No description provided for @payerTitre.
  ///
  /// In fr, this message translates to:
  /// **'Payer mon loyer'**
  String get payerTitre;

  /// No description provided for @payerBail.
  ///
  /// In fr, this message translates to:
  /// **'Bail à payer'**
  String get payerBail;

  /// No description provided for @payerTelephone.
  ///
  /// In fr, this message translates to:
  /// **'Numéro Mobile Money qui paie'**
  String get payerTelephone;

  /// No description provided for @payerTelephoneInvalide.
  ///
  /// In fr, this message translates to:
  /// **'Saisissez un numéro valide, au format international.'**
  String get payerTelephoneInvalide;

  /// No description provided for @payerConfirmer.
  ///
  /// In fr, this message translates to:
  /// **'Payer'**
  String get payerConfirmer;

  /// No description provided for @operateurMtn.
  ///
  /// In fr, this message translates to:
  /// **'MTN MoMo'**
  String get operateurMtn;

  /// No description provided for @operateurOrange.
  ///
  /// In fr, this message translates to:
  /// **'Orange Money'**
  String get operateurOrange;

  /// No description provided for @payerAttenteTitre.
  ///
  /// In fr, this message translates to:
  /// **'En attente de votre validation'**
  String get payerAttenteTitre;

  /// No description provided for @payerAttenteAide.
  ///
  /// In fr, this message translates to:
  /// **'Validez le paiement sur votre téléphone (code secret). Cette page se met à jour toute seule.'**
  String get payerAttenteAide;

  /// No description provided for @payerDelai.
  ///
  /// In fr, this message translates to:
  /// **'Toujours pas de confirmation. Si vous avez validé, le paiement sera enregistré dès que l\'opérateur le confirmera : vous pouvez fermer cette page.'**
  String get payerDelai;

  /// No description provided for @payerContinuerSuivi.
  ///
  /// In fr, this message translates to:
  /// **'Continuer à attendre'**
  String get payerContinuerSuivi;

  /// No description provided for @payerFermer.
  ///
  /// In fr, this message translates to:
  /// **'Fermer'**
  String get payerFermer;

  /// No description provided for @payerSucces.
  ///
  /// In fr, this message translates to:
  /// **'Paiement de {montant} confirmé. Merci !'**
  String payerSucces(String montant);

  /// No description provided for @payerRefusee.
  ///
  /// In fr, this message translates to:
  /// **'Le paiement n\'a pas abouti. Aucun montant n\'a été enregistré.'**
  String get payerRefusee;

  /// No description provided for @payerExpiree.
  ///
  /// In fr, this message translates to:
  /// **'Le paiement a expiré sans confirmation. Aucun montant n\'a été enregistré.'**
  String get payerExpiree;

  /// No description provided for @payerAnomalie.
  ///
  /// In fr, this message translates to:
  /// **'Le montant confirmé par l\'opérateur diffère de celui demandé. Votre gestionnaire va vérifier.'**
  String get payerAnomalie;

  /// No description provided for @errAgregateur.
  ///
  /// In fr, this message translates to:
  /// **'Le service de paiement est momentanément indisponible. Réessayez dans quelques instants.'**
  String get errAgregateur;

  /// No description provided for @errPaiementEnLigneIndisponible.
  ///
  /// In fr, this message translates to:
  /// **'Le paiement en ligne n\'est pas disponible chez votre gestionnaire.'**
  String get errPaiementEnLigneIndisponible;

  /// No description provided for @errTropDeTransactions.
  ///
  /// In fr, this message translates to:
  /// **'Vous avez déjà des paiements en attente : validez-les ou patientez.'**
  String get errTropDeTransactions;

  /// No description provided for @bandeauFile.
  ///
  /// In fr, this message translates to:
  /// **'{n, plural, =1{1 encaissement en attente d\'envoi} other{{n} encaissements en attente d\'envoi}}'**
  String bandeauFile(int n);
}

class _AppLocalizationsDelegate
    extends LocalizationsDelegate<AppLocalizations> {
  const _AppLocalizationsDelegate();

  @override
  Future<AppLocalizations> load(Locale locale) {
    return SynchronousFuture<AppLocalizations>(lookupAppLocalizations(locale));
  }

  @override
  bool isSupported(Locale locale) =>
      <String>['en', 'fr'].contains(locale.languageCode);

  @override
  bool shouldReload(_AppLocalizationsDelegate old) => false;
}

AppLocalizations lookupAppLocalizations(Locale locale) {
  // Lookup logic when only language code is specified.
  switch (locale.languageCode) {
    case 'en':
      return AppLocalizationsEn();
    case 'fr':
      return AppLocalizationsFr();
  }

  throw FlutterError(
    'AppLocalizations.delegate failed to load unsupported locale "$locale". This is likely '
    'an issue with the localizations generation tool. Please file an issue '
    'on GitHub with a reproducible sample app and the gen-l10n configuration '
    'that was used.',
  );
}
