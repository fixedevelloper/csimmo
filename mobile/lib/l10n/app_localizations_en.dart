// ignore: unused_import
import 'package:intl/intl.dart' as intl;
import 'app_localizations.dart';

// ignore_for_file: type=lint

/// The translations for English (`en`).
class AppLocalizationsEn extends AppLocalizations {
  AppLocalizationsEn([String locale = 'en']) : super(locale);

  @override
  String get appTitle => 'CSImmo';

  @override
  String get serveurTitre => 'Your CSImmo server';

  @override
  String get serveurExplication =>
      'Enter the address of your agency\'s or landlord\'s server, or scan the QR code you were given.';

  @override
  String get serveurUrlLabel => 'Server address';

  @override
  String get serveurUrlHint => 'https://immo.example.com';

  @override
  String get serveurContinuer => 'Continue';

  @override
  String get serveurVerification => 'Checking…';

  @override
  String get serveurScanner => 'Scan a QR code';

  @override
  String get serveurScanTitre => 'Scan the QR code';

  @override
  String get serveurScanAide => 'Place the server\'s QR code inside the frame.';

  @override
  String get serveurCameraRefusee =>
      'Camera access is denied. Enter the address manually.';

  @override
  String get serveurQrInvalide => 'This QR code is not an CSImmo address.';

  @override
  String get connexionTitre => 'Sign in';

  @override
  String connexionServeur(String url) {
    return 'Server: $url';
  }

  @override
  String get identifiantLabel => 'Phone or e-mail';

  @override
  String get motDePasseLabel => 'Password';

  @override
  String get seConnecter => 'Sign in';

  @override
  String get connexionEnCours => 'Signing in…';

  @override
  String get connexionParSms => 'Get a code by SMS';

  @override
  String get connexionParMotDePasse => 'Use a password';

  @override
  String get recevoirCode => 'Get the code';

  @override
  String get codeLabel => 'Code received by SMS';

  @override
  String get verifier => 'Verify';

  @override
  String get deuxFacteursInfo => 'A 6-digit code has been sent to you by SMS.';

  @override
  String get codeEnvoye =>
      'If this number is registered, a code has just been sent by SMS.';

  @override
  String get retour => 'Back';

  @override
  String get changerServeur => 'Change server';

  @override
  String get champRequis => 'This field is required';

  @override
  String get codeSixChiffres => 'The code has 6 digits';

  @override
  String get deconnexion => 'Sign out';

  @override
  String bienvenue(String nom) {
    return 'Welcome, $nom';
  }

  @override
  String get bientotDisponible => 'This area will be available soon.';

  @override
  String get espaceLocataire => 'My tenant area';

  @override
  String get espaceAgent => 'Management area';

  @override
  String get espacePrestataire => 'Contractor area';

  @override
  String get espaceProprietaire => 'Owner area';

  @override
  String get chargement => 'Loading…';

  @override
  String get reessayer => 'Try again';

  @override
  String get errInconnue => 'An unexpected error occurred. Try again.';

  @override
  String get errServeurIndisponible =>
      'Cannot reach the server. Check your connection and the address.';

  @override
  String get errServeurNonImmogest =>
      'This address does not respond like an CSImmo server.';

  @override
  String get errUrlInvalide =>
      'Invalid address. Example: https://immo.example.com';

  @override
  String get errUrlHttpRefusee =>
      'A public address must start with https:// (secure connection).';

  @override
  String get errIdentifiantsInvalides => 'Incorrect identifier or password.';

  @override
  String get errCompteVerrouille =>
      'Account temporarily locked after too many failures. Try again in 15 minutes.';

  @override
  String get errCompteDesactive => 'This account is disabled.';

  @override
  String get errOtpInvalide => 'Invalid or expired code.';

  @override
  String get errAccesRefuse => 'You do not have the required permissions.';

  @override
  String get errSessionExpiree =>
      'Your session has expired. Please sign in again.';

  @override
  String get errLicenceExpiree =>
      'The application licence has expired: read-only mode.';

  @override
  String get ongletAccueil => 'Home';

  @override
  String get ongletBaux => 'My leases';

  @override
  String get ongletDocuments => 'Receipts';

  @override
  String get ongletSignaler => 'Report';

  @override
  String get soldeDu => 'Balance due';

  @override
  String get soldeAJour => 'You are up to date';

  @override
  String get arrieres => 'Overdue rent';

  @override
  String get aVenir => 'Upcoming';

  @override
  String get credit => 'Unapplied credit';

  @override
  String get payer => 'Pay';

  @override
  String get payerBientot => 'Mobile Money payment is coming soon.';

  @override
  String get aucunBail => 'No lease yet.';

  @override
  String get bailActif => 'Active';

  @override
  String get bailResilie => 'Terminated';

  @override
  String get bailTermine => 'Ended';

  @override
  String get bailBrouillon => 'Draft';

  @override
  String get echeances => 'Instalments';

  @override
  String get echeancePayee => 'Paid';

  @override
  String get echeanceEnRetard => 'Overdue';

  @override
  String get echeancePartielle => 'Partly paid';

  @override
  String get echeanceDue => 'Due';

  @override
  String get echeanceAVenir => 'Upcoming';

  @override
  String get loyerMensuel => 'Monthly rent';

  @override
  String get chargesMensuelles => 'Monthly charges';

  @override
  String get caution => 'Deposit';

  @override
  String get debutBail => 'Lease start';

  @override
  String get finBail => 'Lease end';

  @override
  String get sansDateFin => 'Open-ended';

  @override
  String get sortiePrevue => 'Planned move-out';

  @override
  String avantLe(String date) {
    return 'before $date';
  }

  @override
  String resteAPayer(String montant) {
    return '$montant left';
  }

  @override
  String get aucunDocument => 'No rent receipt or payment slip yet.';

  @override
  String get quittance => 'Rent receipt';

  @override
  String get recu => 'Payment slip';

  @override
  String get signalerTitre => 'Report a problem';

  @override
  String get signalerDescription => 'Describe the problem';

  @override
  String get signalerDescriptionCourte =>
      'Describe the problem in a few words (5 characters minimum).';

  @override
  String get photoCamera => 'Take a photo';

  @override
  String get photoGalerie => 'Gallery';

  @override
  String get retirerPhoto => 'Remove the photo';

  @override
  String get signalerEnvoyer => 'Send the report';

  @override
  String get envoiEnCours => 'Sending…';

  @override
  String get signalementEnvoye => 'Report sent. Your manager will handle it.';

  @override
  String get mesSignalements => 'My reports';

  @override
  String get aucunSignalement => 'No report yet.';

  @override
  String get signalementNouveau => 'Received';

  @override
  String get signalementEnCours => 'In progress';

  @override
  String get signalementResolu => 'Resolved';

  @override
  String get signalementRejete => 'Rejected';

  @override
  String get errSignalementBailRequis => 'Specify which lease it concerns.';

  @override
  String get errSignalementAucunBail => 'You have no current lease.';

  @override
  String get errSignalementLimite =>
      'Too many reports waiting: please wait until they are handled.';

  @override
  String get errSignalementDescription =>
      'Describe the problem in 5 to 2000 characters.';

  @override
  String get errFichierInvalide =>
      'This photo is not accepted (JPEG, PNG or WebP, 10 MB at most).';

  @override
  String get errLocataireIntrouvable =>
      'No tenant record is linked to your account. Contact your manager.';

  @override
  String get ongletEncaisser => 'Collect';

  @override
  String get ongletAEnvoyer => 'To send';

  @override
  String get ongletLots => 'Units';

  @override
  String get ongletLocataires => 'Tenants';

  @override
  String donneesDu(String date) {
    return 'Data from $date';
  }

  @override
  String get donneesAbsentes => 'No data stored on this phone.';

  @override
  String get donneesAbsentesAide =>
      'Refresh the data (with a network) so you can work offline afterwards.';

  @override
  String get actualiserDonnees => 'Refresh';

  @override
  String get donneesActualisees => 'Data refreshed.';

  @override
  String get donneesAnciennes => 'The previous data remains usable.';

  @override
  String get rechercherLocataire => 'Search a tenant';

  @override
  String get aucunLocataire => 'No tenant found.';

  @override
  String get changer => 'Change';

  @override
  String get aucunBailLocataire => 'This tenant has no lease on record.';

  @override
  String get choisirBailErreur => 'Choose the lease concerned.';

  @override
  String mensuel(String montant) {
    return '$montant per month';
  }

  @override
  String get montantFcfa => 'Amount collected (FCFA)';

  @override
  String get montantAPayerFcfa => 'Amount to pay (FCFA)';

  @override
  String get montantInvalide => 'Enter a whole amount in FCFA.';

  @override
  String get enregistrerEncaissement => 'Record the payment';

  @override
  String get encaissementEnregistre =>
      'Payment saved on the phone. It will be sent as soon as possible.';

  @override
  String get recuSmsInfo =>
      'The tenant gets an SMS receipt when the payment reaches the server.';

  @override
  String get synchroniserMaintenant => 'Send now';

  @override
  String get fileVide => 'No payment entered recently.';

  @override
  String get statutEnAttente => 'Waiting';

  @override
  String get statutEnvoye => 'Sent';

  @override
  String get statutRejete => 'Refused';

  @override
  String get supprimerSaisie => 'Delete the entry';

  @override
  String get saisieSupprimee => 'Entry deleted.';

  @override
  String get abandonImpossible =>
      'Not possible: this payment was already presented to the server and may be recorded there.';

  @override
  String synchroTerminee(int envoyes, int rejetes) {
    return 'Done: $envoyes sent, $rejetes refused.';
  }

  @override
  String synchroInterrompue(String raison, int restants) {
    return '$raison $restants payment(s) still waiting.';
  }

  @override
  String deconnexionAvecFile(int n) {
    return '$n unsent payment(s) will be kept on this phone and sent at your next sign-in with this account.';
  }

  @override
  String get annuler => 'Cancel';

  @override
  String get rechercherLot => 'Search a unit';

  @override
  String get aucunLot => 'No unit.';

  @override
  String get lotLibre => 'Vacant';

  @override
  String get lotOccupe => 'Occupied';

  @override
  String get lotEnTravaux => 'Under works';

  @override
  String get errBailIntrouvable =>
      'Lease not found: the payment cannot be recorded.';

  @override
  String get errPaiementInvalide => 'Invalid amount or details.';

  @override
  String get errPaiementDateFuture =>
      'The payment date is in the future: check the phone\'s clock.';

  @override
  String get errIdempotence =>
      'This entry was already used for a different amount.';

  @override
  String get payerTitre => 'Pay my rent';

  @override
  String get payerBail => 'Lease to pay';

  @override
  String get payerTelephone => 'Mobile Money number paying';

  @override
  String get payerTelephoneInvalide =>
      'Enter a valid number in international format.';

  @override
  String get payerConfirmer => 'Pay';

  @override
  String get operateurMtn => 'MTN MoMo';

  @override
  String get operateurOrange => 'Orange Money';

  @override
  String get payerAttenteTitre => 'Waiting for your approval';

  @override
  String get payerAttenteAide =>
      'Approve the payment on your phone (secret code). This page updates by itself.';

  @override
  String get payerDelai =>
      'Still no confirmation. If you approved it, the payment will be recorded as soon as the operator confirms it: you can close this page.';

  @override
  String get payerContinuerSuivi => 'Keep waiting';

  @override
  String get payerFermer => 'Close';

  @override
  String payerSucces(String montant) {
    return 'Payment of $montant confirmed. Thank you!';
  }

  @override
  String get payerRefusee =>
      'The payment did not go through. No amount was recorded.';

  @override
  String get payerExpiree =>
      'The payment expired without confirmation. No amount was recorded.';

  @override
  String get payerAnomalie =>
      'The amount confirmed by the operator differs from the one requested. Your manager will check.';

  @override
  String get errAgregateur =>
      'The payment service is temporarily unavailable. Try again in a moment.';

  @override
  String get errPaiementEnLigneIndisponible =>
      'Online payment is not available with your manager.';

  @override
  String get errTropDeTransactions =>
      'You already have pending payments: approve them or wait.';

  @override
  String bandeauFile(int n) {
    String _temp0 = intl.Intl.pluralLogic(
      n,
      locale: localeName,
      other: '$n payments waiting to be sent',
      one: '1 payment waiting to be sent',
    );
    return '$_temp0';
  }
}
