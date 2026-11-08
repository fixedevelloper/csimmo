import '../../l10n/app_localizations.dart';
import '../config/server_url.dart';
import 'app_error.dart';

/// Message affichable (traduit) pour une erreur : jamais de détail technique.
String errorMessage(AppLocalizations l, Object erreur) {
  if (erreur is ServerUrlException) {
    return switch (erreur.error) {
      ServerUrlError.invalide => l.errUrlInvalide,
      ServerUrlError.httpNonLocal => l.errUrlHttpRefusee,
    };
  }
  if (erreur is AppError) {
    return switch (erreur.code) {
      'SERVEUR_INDISPONIBLE' => l.errServeurIndisponible,
      'SERVEUR_NON_IMMOGEST' => l.errServeurNonImmogest,
      'IDENTIFIANTS_INVALIDES' => l.errIdentifiantsInvalides,
      'COMPTE_VERROUILLE' => l.errCompteVerrouille,
      'COMPTE_DESACTIVE' => l.errCompteDesactive,
      'OTP_INVALIDE' => l.errOtpInvalide,
      'ACCES_REFUSE' => l.errAccesRefuse,
      'NON_AUTHENTIFIE' || 'TOKEN_EXPIRE' || 'REFRESH_TOKEN_INVALIDE' || 'REFRESH_TOKEN_EXPIRE' ||
      'REFRESH_TOKEN_REUTILISE' =>
        l.errSessionExpiree,
      'LICENCE_EXPIREE' => l.errLicenceExpiree,
      'SIGNALEMENT_BAIL_REQUIS' => l.errSignalementBailRequis,
      'SIGNALEMENT_AUCUN_BAIL' => l.errSignalementAucunBail,
      'SIGNALEMENT_LIMITE' => l.errSignalementLimite,
      'SIGNALEMENT_DESCRIPTION_INVALIDE' => l.errSignalementDescription,
      'FICHIER_IMAGE_ILLISIBLE' || 'FICHIER_TYPE_INTERDIT' || 'FICHIER_TROP_VOLUMINEUX' || 'FICHIER_VIDE' => l.errFichierInvalide,
      'LOCATAIRE_INTROUVABLE' => l.errLocataireIntrouvable,
      'BAIL_INTROUVABLE' => l.errBailIntrouvable,
      'AGREGATEUR_INDISPONIBLE' => l.errAgregateur,
      'PAIEMENT_EN_LIGNE_INDISPONIBLE' => l.errPaiementEnLigneIndisponible,
      'PAIEMENT_EN_LIGNE_TROP_DE_TRANSACTIONS' => l.errTropDeTransactions,
      'PAIEMENT_EN_LIGNE_TELEPHONE_INVALIDE' => l.payerTelephoneInvalide,
      'PAIEMENT_EN_LIGNE_MONTANT_INVALIDE' => l.montantInvalide,
      'PAIEMENT_MONTANT_INVALIDE' || 'PAIEMENT_CHAMPS_REQUIS' || 'VALIDATION_ERREUR' => l.errPaiementInvalide,
      'PAIEMENT_DATE_FUTURE' => l.errPaiementDateFuture,
      'IDEMPOTENCE_CONFLIT' || 'IDEMPOTENCE_CLE_INVALIDE' => l.errIdempotence,
      'MOT_DE_PASSE_ACTUEL_INCORRECT' => l.errMotDePasseActuel,
      'DEUX_FACTEURS_OBLIGATOIRE' => l.errDeuxFacteursObligatoire,
      'DEUX_FACTEURS_DEJA_ACTIFS' => l.errDeuxFacteursDejaActifs,
      'OTP_TELEPHONE_REQUIS' => l.errTelephoneRequisCode,
      'UTILISATEUR_CONTACT_REQUIS' => l.profilContactRequis,
      'UTILISATEUR_DEJA_EXISTANT' => l.errContactDejaUtilise,
      'UTILISATEUR_TELEPHONE_INVALIDE' => l.errTelephoneInvalide,
      'UTILISATEUR_TELEPHONE_REQUIS_2FA' => l.errTelephoneRequis2fa,
      'TROP_DE_REQUETES' => l.errTropDeRequetes,
      _ => l.errInconnue,
    };
  }
  return l.errInconnue;
}
