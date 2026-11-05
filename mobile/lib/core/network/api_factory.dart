import 'package:dio/dio.dart';
import 'package:immogest_api/immogest_api.dart';

import '../auth/token_store.dart';
import 'auth_interceptor.dart';

/// Délais adaptés à un réseau 3G instable (le client généré n'en prévoit que 3 s en lecture).
BaseOptions optionsReseau(String baseUrl) => BaseOptions(
      baseUrl: baseUrl,
      connectTimeout: const Duration(seconds: 10),
      sendTimeout: const Duration(seconds: 20),
      receiveTimeout: const Duration(seconds: 20),
    );

/// Construit le client de l'API pour [baseUrl] : jeton d'accès automatique et rafraîchissement transparent.
ImmogestApi buildApi({
  required String baseUrl,
  required TokenStore tokens,
  required Future<void> Function() onSessionLost,
}) {
  final dio = Dio(optionsReseau(baseUrl));
  final sansAuth = ImmogestApi(dio: Dio(optionsReseau(baseUrl)), interceptors: const []);
  dio.interceptors.add(AuthInterceptor(
    dio: dio,
    tokens: tokens,
    refreshApi: sansAuth.getAuthControllerApi(),
    onSessionLost: onSessionLost,
  ));
  return ImmogestApi(dio: dio, interceptors: const []);
}
