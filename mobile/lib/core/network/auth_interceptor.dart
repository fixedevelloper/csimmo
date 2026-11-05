import 'dart:async';

import 'package:dio/dio.dart';
import 'package:immogest_api/immogest_api.dart';

import '../auth/role.dart';
import '../auth/token_store.dart';

/// Ajoute le jeton d'accès aux requêtes et le renouvelle quand l'API répond 401.
///
/// Le refresh token tourne à chaque usage et son rejeu révoque toute la session : le rafraîchissement est donc
/// **mutualisé** (un seul appel pour toutes les requêtes qui échouent en même temps), et une requête qui a échoué
/// avec un ancien jeton est simplement rejouée avec le jeton déjà renouvelé.
class AuthInterceptor extends Interceptor {
  AuthInterceptor({
    required this.dio,
    required this.tokens,
    required this.refreshApi,
    required this.onSessionLost,
  });

  /// Client sur lequel rejouer les requêtes.
  final Dio dio;
  final TokenStore tokens;

  /// Client SANS cet intercepteur, pour appeler /auth/refresh.
  final AuthControllerApi refreshApi;

  /// Appelé quand le serveur refuse définitivement la session (refresh invalide, expiré, réutilisé).
  final Future<void> Function() onSessionLost;

  static const _publicPaths = {
    '/api/v1/health',
    '/api/v1/auth/login',
    '/api/v1/auth/refresh',
    '/api/v1/auth/logout',
    '/api/v1/auth/otp/demander',
    '/api/v1/auth/otp/verifier',
  };
  static const _extraToken = 'immogest.token';
  static const _extraRetried = 'immogest.retried';

  Future<_RefreshResult>? _enCours;

  @override
  Future<void> onRequest(RequestOptions options, RequestInterceptorHandler handler) async {
    if (!_publicPaths.contains(options.path)) {
      final courant = await tokens.read();
      if (courant != null) {
        options.headers['Authorization'] = 'Bearer ${courant.access}';
        options.extra[_extraToken] = courant.access;
      }
    }
    handler.next(options);
  }

  @override
  Future<void> onError(DioException err, ErrorInterceptorHandler handler) async {
    final requete = err.requestOptions;
    if (err.response?.statusCode != 401 ||
        _publicPaths.contains(requete.path) ||
        requete.extra[_extraRetried] == true) {
      return handler.next(err);
    }
    final courant = await tokens.read();
    if (courant == null) return handler.next(err);

    String? nouveauJeton;
    if (requete.extra[_extraToken] != courant.access) {
      nouveauJeton = courant.access; // déjà renouvelé par une autre requête
    } else {
      final resultat = await (_enCours ??= _rafraichir(courant).whenComplete(() => _enCours = null));
      switch (resultat) {
        case _Renouvele(:final access):
          nouveauJeton = access;
        case _Refuse():
          await onSessionLost();
          return handler.next(err);
        case _Injoignable():
          return handler.next(err); // réseau coupé : on garde la session
      }
    }

    requete.headers['Authorization'] = 'Bearer $nouveauJeton';
    requete.extra[_extraToken] = nouveauJeton;
    requete.extra[_extraRetried] = true;
    try {
      handler.resolve(await dio.fetch<dynamic>(requete));
    } on DioException catch (e) {
      handler.next(e);
    }
  }

  Future<_RefreshResult> _rafraichir(Tokens courant) async {
    try {
      final reponse = await refreshApi.refresh(refreshRequete: RefreshRequete(refreshToken: courant.refresh));
      final donnees = reponse.data;
      final access = donnees?.accessToken;
      final refresh = donnees?.refreshToken;
      if (access == null || refresh == null) {
        await tokens.clear();
        return const _Refuse();
      }
      await tokens.save(Tokens(
        access: access,
        refresh: refresh,
        role: Role.fromCode(donnees?.role?.value) ?? courant.role,
      ));
      return _Renouvele(access);
    } on DioException catch (e) {
      final status = e.response?.statusCode;
      if (status == 401 || status == 403) {
        await tokens.clear();
        return const _Refuse();
      }
      return const _Injoignable();
    }
  }
}

sealed class _RefreshResult {
  const _RefreshResult();
}

class _Renouvele extends _RefreshResult {
  const _Renouvele(this.access);
  final String access;
}

class _Refuse extends _RefreshResult {
  const _Refuse();
}

class _Injoignable extends _RefreshResult {
  const _Injoignable();
}
