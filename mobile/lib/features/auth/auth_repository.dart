import 'package:immogest_api/immogest_api.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';

import '../../core/auth/role.dart';
import '../../core/auth/token_store.dart';
import '../../core/error/app_error.dart';
import '../../core/providers.dart';

part 'auth_repository.g.dart';

/// Appels d'authentification de l'API (aucune logique de session ici).
class AuthRepository {
  AuthRepository(this._api);

  final ImmogestApi _api;

  AuthControllerApi get _auth => _api.getAuthControllerApi();

  /// Connexion par mot de passe. Renvoie `null` si un code SMS est requis (2FA).
  Future<Tokens?> login(String identifiant, String motDePasse) => guard(() async {
        final r = (await _auth.login(
          connexionRequete: ConnexionRequete(identifiant: identifiant, motDePasse: motDePasse),
        ))
            .data;
        if (r == null || r.deuxFacteursRequis == true) return null;
        return _enTokens(r);
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
