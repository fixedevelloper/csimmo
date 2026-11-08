import 'package:immogest_api/immogest_api.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';

import '../../core/auth/role.dart';
import '../../core/auth/token_store.dart';
import '../../core/error/app_error.dart';
import '../../core/providers.dart';

part 'auth_repository.g.dart';

/// Résultat d'une connexion par mot de passe.
sealed class ResultatConnexion {
  const ResultatConnexion();
}

class SessionOuverte extends ResultatConnexion {
  const SessionOuverte(this.tokens);

  final Tokens tokens;
}

/// Un code de l'application d'authentification est exigé ; [defi] est à renvoyer avec lui.
class DeuxFacteursRequis extends ResultatConnexion {
  const DeuxFacteursRequis(this.defi);

  final String defi;
}

/// L'application d'authentification doit d'abord être configurée ; [defi] est à renvoyer.
class EnrolementRequis extends ResultatConnexion {
  const EnrolementRequis(this.defi);

  final String defi;
}

/// Clé à saisir dans l'application d'authentification et adresse `otpauth://` (code QR).
class Enrolement {
  const Enrolement(this.secret, this.uri);

  final String secret;
  final String uri;
}

/// Appels d'authentification de l'API (aucune logique de session ici).
class AuthRepository {
  AuthRepository(this._api);

  final ImmogestApi _api;

  AuthControllerApi get _auth => _api.getAuthControllerApi();

  /// Connexion par mot de passe : session ouverte, ou second facteur (code de l'application) à fournir, ou application à configurer d'abord.
  Future<ResultatConnexion> login(String identifiant, String motDePasse) => guard(() async {
        final r = (await _auth.login(
          connexionRequete: ConnexionRequete(identifiant: identifiant, motDePasse: motDePasse),
        ))
            .data;
        if (r == null) throw const AppError(AppError.inconnue);
        if (r.deuxFacteursRequis == true && r.defi != null) return DeuxFacteursRequis(r.defi!);
        if (r.enrolementRequis == true && r.defi != null) return EnrolementRequis(r.defi!);
        final tokens = _enTokens(r);
        if (tokens == null) throw const AppError(AppError.inconnue);
        return SessionOuverte(tokens);
      });

  /// Deuxième étape : le code de l'application d'authentification, avec le défi reçu après le mot de passe.
  Future<Tokens> verifierDeuxFacteurs(String defi, String code) => guard(() async {
        final r = (await _auth.verifierDeuxFacteurs(deuxFacteursVerification: DeuxFacteursVerification(defi: defi, code: code))).data;
        final tokens = r == null ? null : _enTokens(r);
        if (tokens == null) throw const AppError(AppError.inconnue);
        return tokens;
      });

  /// Compte sans application : secret (à saisir dans l'application) et adresse `otpauth://`.
  Future<Enrolement> demarrerEnrolement(String defi) => guard(() async {
        final e = (await _auth.demarrerEnrolementDeuxFacteurs(deuxFacteursDefi: DeuxFacteursDefi(defi: defi))).data;
        if (e == null || e.secret == null) throw const AppError(AppError.inconnue);
        return Enrolement(e.secret!, e.uri ?? '');
      });

  /// Premier code correct : l'application est configurée et la session s'ouvre.
  Future<Tokens> confirmerEnrolement(String defi, String code) => guard(() async {
        final r = (await _auth.confirmerEnrolementDeuxFacteurs(deuxFacteursVerification: DeuxFacteursVerification(defi: defi, code: code))).data;
        final tokens = r == null ? null : _enTokens(r);
        if (tokens == null) throw const AppError(AppError.inconnue);
        return tokens;
      });

  Future<void> demanderOtp(String identifiant) =>
      guard(() => _auth.demanderOtp(otpDemande: OtpDemande(identifiant: identifiant)));

  Future<Tokens> verifierOtp(String identifiant, String code) => guard(() async {
        final r = (await _auth.verifierOtp(otpVerification: OtpVerification(identifiant: identifiant, code: code))).data;
        final tokens = r == null ? null : _enTokens(r);
        if (tokens == null) throw const AppError(AppError.inconnue);
        return tokens;
      });

  Future<UtilisateurDto> moi() => guard(() async {
        final data = (await _auth.moi()).data;
        if (data == null) throw const AppError(AppError.inconnue);
        return data;
      });

  /// Modifie son propre profil ; le mot de passe actuel est exigé.
  Future<UtilisateurDto> modifierProfil({required String nom, String? telephone, String? email, required String motDePasse}) => guard(() async {
        final data = (await _auth.modifierProfil(
          profilRequete: ProfilRequete(nom: nom, telephone: telephone, email: email, motDePasse: motDePasse),
        ))
            .data;
        if (data == null) throw const AppError(AppError.inconnue);
        return data;
      });

  /// Change le mot de passe : le serveur ferme ensuite toutes les sessions.
  Future<void> changerMotDePasse(String ancien, String nouveau) => guard(() =>
      _auth.changerMotDePasse(changementMotDePasse: ChangementMotDePasse(ancienMotDePasse: ancien, nouveauMotDePasse: nouveau)));

  /// Active depuis le profil, étape 1 : mot de passe confirmé, puis secret et adresse à enregistrer dans l'application.
  Future<Enrolement> demarrerDeuxFacteurs(String motDePasse) => guard(() async {
        final e = (await _auth.demarrerDeuxFacteurs(motDePasseRequete: MotDePasseRequete(motDePasse: motDePasse))).data;
        if (e == null || e.secret == null) throw const AppError(AppError.inconnue);
        return Enrolement(e.secret!, e.uri ?? '');
      });

  Future<UtilisateurDto> activerDeuxFacteurs(String code) => guard(() async {
        final data = (await _auth.activerDeuxFacteurs(codeRequete: CodeRequete(code: code))).data;
        if (data == null) throw const AppError(AppError.inconnue);
        return data;
      });

  Future<UtilisateurDto> desactiverDeuxFacteurs(String motDePasse, String code) => guard(() async {
        final data = (await _auth.desactiverDeuxFacteurs(desactivationRequete: DesactivationRequete(motDePasse: motDePasse, code: code))).data;
        if (data == null) throw const AppError(AppError.inconnue);
        return data;
      });

  Future<void> deconnecter(String refreshToken) =>
      guard(() => _auth.logout(refreshRequete: RefreshRequete(refreshToken: refreshToken)));

  static Tokens? _enTokens(ReponseConnexion r) {
    final role = Role.fromCode(r.role?.value);
    if (r.accessToken == null || r.refreshToken == null || role == null) return null;
    return Tokens(access: r.accessToken!, refresh: r.refreshToken!, role: role);
  }
}

@Riverpod(keepAlive: true)
AuthRepository authRepository(Ref ref) => AuthRepository(ref.watch(apiProvider));

/// Utilisateur connecté (GET /auth/moi) : exerce aussi le jeton et son renouvellement.
@riverpod
Future<UtilisateurDto> currentUser(Ref ref) => ref.watch(authRepositoryProvider).moi();
