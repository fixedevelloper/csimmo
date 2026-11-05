import 'package:riverpod_annotation/riverpod_annotation.dart';

import '../../core/error/app_error.dart';
import '../../core/providers.dart';
import '../agent/cache_terrain.dart';
import '../push/push_registrar.dart';
import 'auth_repository.dart';

part 'auth_actions.g.dart';

/// Actions d'authentification qui modifient la session. Les écrans les appellent, sans logique métier.
class AuthActions {
  AuthActions(this._ref);

  final Ref _ref;

  AuthRepository get _repo => _ref.read(authRepositoryProvider);

  /// Retourne `true` si la session est ouverte, `false` si un code SMS est requis (2FA).
  Future<bool> login(String identifiant, String motDePasse) async {
    final tokens = await _repo.login(identifiant.trim(), motDePasse);
    if (tokens == null) return false;
    await _ref.read(sessionProvider.notifier).signIn(tokens);
    return true;
  }

  Future<void> demanderOtp(String identifiant) => _repo.demanderOtp(identifiant.trim());

  Future<void> verifierOtp(String identifiant, String code) async {
    final tokens = await _repo.verifierOtp(identifiant.trim(), code.trim());
    await _ref.read(sessionProvider.notifier).signIn(tokens);
  }

  Future<void> logout() async {
    await _ref.read(pushRegistrarProvider).retirer(); // exige encore la session : donc avant sa fermeture
    final tokens = await _ref.read(tokenStoreProvider).read();
    if (tokens != null) {
      // Meilleur effort : la session locale est fermée même si le serveur est injoignable.
      try {
        await _repo.deconnecter(tokens.refresh);
      } on AppError catch (_) {}
    }
    // La copie locale contient des noms et téléphones : elle ne reste pas sur le téléphone après la déconnexion.
    // La file d'encaissements non envoyés, elle, est conservée (argent saisi) et ne sera envoyée que sous le même compte.
    try {
      await _ref.read(cacheTerrainProvider).vider();
    } catch (_) {}
    await _ref.read(sessionProvider.notifier).signOut();
  }
}

@Riverpod(keepAlive: true)
AuthActions authActions(Ref ref) => AuthActions(ref);
