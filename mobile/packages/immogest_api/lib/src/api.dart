//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

import 'package:dio/dio.dart';
import 'package:immogest_api/src/auth/api_key_auth.dart';
import 'package:immogest_api/src/auth/basic_auth.dart';
import 'package:immogest_api/src/auth/bearer_auth.dart';
import 'package:immogest_api/src/auth/oauth.dart';
import 'package:immogest_api/src/api/auth_controller_api.dart';
import 'package:immogest_api/src/api/bail_controller_api.dart';
import 'package:immogest_api/src/api/conformite_controller_api.dart';
import 'package:immogest_api/src/api/dashboard_controller_api.dart';
import 'package:immogest_api/src/api/document_controller_api.dart';
import 'package:immogest_api/src/api/echeance_controller_api.dart';
import 'package:immogest_api/src/api/fichier_controller_api.dart';
import 'package:immogest_api/src/api/health_controller_api.dart';
import 'package:immogest_api/src/api/immeuble_controller_api.dart';
import 'package:immogest_api/src/api/import_controller_api.dart';
import 'package:immogest_api/src/api/installation_controller_api.dart';
import 'package:immogest_api/src/api/licence_controller_api.dart';
import 'package:immogest_api/src/api/locataire_controller_api.dart';
import 'package:immogest_api/src/api/lot_controller_api.dart';
import 'package:immogest_api/src/api/notification_controller_api.dart';
import 'package:immogest_api/src/api/paiement_controller_api.dart';
import 'package:immogest_api/src/api/paiement_en_ligne_controller_api.dart';
import 'package:immogest_api/src/api/photo_controller_api.dart';
import 'package:immogest_api/src/api/prestataire_controller_api.dart';
import 'package:immogest_api/src/api/proprietaire_controller_api.dart';
import 'package:immogest_api/src/api/relance_controller_api.dart';
import 'package:immogest_api/src/api/reversement_controller_api.dart';
import 'package:immogest_api/src/api/sauvegarde_controller_api.dart';
import 'package:immogest_api/src/api/signalement_controller_api.dart';
import 'package:immogest_api/src/api/utilisateur_controller_api.dart';
import 'package:immogest_api/src/api/webhook_controller_api.dart';

class ImmogestApi {
  static const String basePath = r'http://localhost';

  final Dio dio;
  ImmogestApi({
    Dio? dio,
    String? basePathOverride,
    List<Interceptor>? interceptors,
  })  : 
        this.dio = dio ??
            Dio(BaseOptions(
              baseUrl: basePathOverride ?? basePath,
              connectTimeout: const Duration(milliseconds: 5000),
              receiveTimeout: const Duration(milliseconds: 3000),
            )) {
    if (interceptors == null) {
      this.dio.interceptors.addAll([
        OAuthInterceptor(),
        BasicAuthInterceptor(),
        BearerAuthInterceptor(),
        ApiKeyAuthInterceptor(),
      ]);
    } else {
      this.dio.interceptors.addAll(interceptors);
    }
  }

  void setOAuthToken(String name, String token) {
    if (this.dio.interceptors.any((i) => i is OAuthInterceptor)) {
      (this.dio.interceptors.firstWhere((i) => i is OAuthInterceptor) as OAuthInterceptor).tokens[name] = token;
    }
  }

  /// Removes the OAuth token associated with the given [name].
  ///
  /// If no [OAuthInterceptor] is registered or no token exists for the given
  /// [name], this method has no effect.
  void removeOAuthToken(String name) {
    if (this.dio.interceptors.any((i) => i is OAuthInterceptor)) {
      (this.dio.interceptors.firstWhere((i) => i is OAuthInterceptor) as OAuthInterceptor).tokens.remove(name);
    }
  }

  void setBearerAuth(String name, String token) {
    if (this.dio.interceptors.any((i) => i is BearerAuthInterceptor)) {
      (this.dio.interceptors.firstWhere((i) => i is BearerAuthInterceptor) as BearerAuthInterceptor).tokens[name] = token;
    }
  }

  /// Removes the bearer authentication token associated with the given [name].
  ///
  /// If no [BearerAuthInterceptor] is registered or no token exists for the
  /// given [name], this method has no effect.
  void removeBearerAuth(String name) {
    if (this.dio.interceptors.any((i) => i is BearerAuthInterceptor)) {
      (this.dio.interceptors.firstWhere((i) => i is BearerAuthInterceptor) as BearerAuthInterceptor).tokens.remove(name);
    }
  }

  void setBasicAuth(String name, String username, String password) {
    if (this.dio.interceptors.any((i) => i is BasicAuthInterceptor)) {
      (this.dio.interceptors.firstWhere((i) => i is BasicAuthInterceptor) as BasicAuthInterceptor).authInfo[name] = BasicAuthInfo(username, password);
    }
  }

  /// Removes the basic authentication credentials associated with the given [name].
  ///
  /// If no [BasicAuthInterceptor] is registered or no credentials exist for the
  /// given [name], this method has no effect.
  void removeBasicAuth(String name) {
    if (this.dio.interceptors.any((i) => i is BasicAuthInterceptor)) {
      (this.dio.interceptors.firstWhere((i) => i is BasicAuthInterceptor) as BasicAuthInterceptor).authInfo.remove(name);
    }
  }

  void setApiKey(String name, String apiKey) {
    if (this.dio.interceptors.any((i) => i is ApiKeyAuthInterceptor)) {
      (this.dio.interceptors.firstWhere((element) => element is ApiKeyAuthInterceptor) as ApiKeyAuthInterceptor).apiKeys[name] = apiKey;
    }
  }

  /// Removes the API key associated with the given [name].
  ///
  /// If no [ApiKeyAuthInterceptor] is registered or no API key exists for the
  /// given [name], this method has no effect.
  void removeApiKey(String name) {
    if (this.dio.interceptors.any((i) => i is ApiKeyAuthInterceptor)) {
      (this.dio.interceptors.firstWhere((element) => element is ApiKeyAuthInterceptor) as ApiKeyAuthInterceptor).apiKeys.remove(name);
    }
  }

  /// Get AuthControllerApi instance, base route and serializer can be overridden by a given but be careful,
  /// by doing that all interceptors will not be executed
  AuthControllerApi getAuthControllerApi() {
    return AuthControllerApi(dio);
  }

  /// Get BailControllerApi instance, base route and serializer can be overridden by a given but be careful,
  /// by doing that all interceptors will not be executed
  BailControllerApi getBailControllerApi() {
    return BailControllerApi(dio);
  }

  /// Get ConformiteControllerApi instance, base route and serializer can be overridden by a given but be careful,
  /// by doing that all interceptors will not be executed
  ConformiteControllerApi getConformiteControllerApi() {
    return ConformiteControllerApi(dio);
  }

  /// Get DashboardControllerApi instance, base route and serializer can be overridden by a given but be careful,
  /// by doing that all interceptors will not be executed
  DashboardControllerApi getDashboardControllerApi() {
    return DashboardControllerApi(dio);
  }

  /// Get DocumentControllerApi instance, base route and serializer can be overridden by a given but be careful,
  /// by doing that all interceptors will not be executed
  DocumentControllerApi getDocumentControllerApi() {
    return DocumentControllerApi(dio);
  }

  /// Get EcheanceControllerApi instance, base route and serializer can be overridden by a given but be careful,
  /// by doing that all interceptors will not be executed
  EcheanceControllerApi getEcheanceControllerApi() {
    return EcheanceControllerApi(dio);
  }

  /// Get FichierControllerApi instance, base route and serializer can be overridden by a given but be careful,
  /// by doing that all interceptors will not be executed
  FichierControllerApi getFichierControllerApi() {
    return FichierControllerApi(dio);
  }

  /// Get HealthControllerApi instance, base route and serializer can be overridden by a given but be careful,
  /// by doing that all interceptors will not be executed
  HealthControllerApi getHealthControllerApi() {
    return HealthControllerApi(dio);
  }

  /// Get ImmeubleControllerApi instance, base route and serializer can be overridden by a given but be careful,
  /// by doing that all interceptors will not be executed
  ImmeubleControllerApi getImmeubleControllerApi() {
    return ImmeubleControllerApi(dio);
  }

  /// Get ImportControllerApi instance, base route and serializer can be overridden by a given but be careful,
  /// by doing that all interceptors will not be executed
  ImportControllerApi getImportControllerApi() {
    return ImportControllerApi(dio);
  }

  /// Get InstallationControllerApi instance, base route and serializer can be overridden by a given but be careful,
  /// by doing that all interceptors will not be executed
  InstallationControllerApi getInstallationControllerApi() {
    return InstallationControllerApi(dio);
  }

  /// Get LicenceControllerApi instance, base route and serializer can be overridden by a given but be careful,
  /// by doing that all interceptors will not be executed
  LicenceControllerApi getLicenceControllerApi() {
    return LicenceControllerApi(dio);
  }

  /// Get LocataireControllerApi instance, base route and serializer can be overridden by a given but be careful,
  /// by doing that all interceptors will not be executed
  LocataireControllerApi getLocataireControllerApi() {
    return LocataireControllerApi(dio);
  }

  /// Get LotControllerApi instance, base route and serializer can be overridden by a given but be careful,
  /// by doing that all interceptors will not be executed
  LotControllerApi getLotControllerApi() {
    return LotControllerApi(dio);
  }

  /// Get NotificationControllerApi instance, base route and serializer can be overridden by a given but be careful,
  /// by doing that all interceptors will not be executed
  NotificationControllerApi getNotificationControllerApi() {
    return NotificationControllerApi(dio);
  }

  /// Get PaiementControllerApi instance, base route and serializer can be overridden by a given but be careful,
  /// by doing that all interceptors will not be executed
  PaiementControllerApi getPaiementControllerApi() {
    return PaiementControllerApi(dio);
  }

  /// Get PaiementEnLigneControllerApi instance, base route and serializer can be overridden by a given but be careful,
  /// by doing that all interceptors will not be executed
  PaiementEnLigneControllerApi getPaiementEnLigneControllerApi() {
    return PaiementEnLigneControllerApi(dio);
  }

  /// Get PhotoControllerApi instance, base route and serializer can be overridden by a given but be careful,
  /// by doing that all interceptors will not be executed
  PhotoControllerApi getPhotoControllerApi() {
    return PhotoControllerApi(dio);
  }

  /// Get PrestataireControllerApi instance, base route and serializer can be overridden by a given but be careful,
  /// by doing that all interceptors will not be executed
  PrestataireControllerApi getPrestataireControllerApi() {
    return PrestataireControllerApi(dio);
  }

  /// Get ProprietaireControllerApi instance, base route and serializer can be overridden by a given but be careful,
  /// by doing that all interceptors will not be executed
  ProprietaireControllerApi getProprietaireControllerApi() {
    return ProprietaireControllerApi(dio);
  }

  /// Get RelanceControllerApi instance, base route and serializer can be overridden by a given but be careful,
  /// by doing that all interceptors will not be executed
  RelanceControllerApi getRelanceControllerApi() {
    return RelanceControllerApi(dio);
  }

  /// Get ReversementControllerApi instance, base route and serializer can be overridden by a given but be careful,
  /// by doing that all interceptors will not be executed
  ReversementControllerApi getReversementControllerApi() {
    return ReversementControllerApi(dio);
  }

  /// Get SauvegardeControllerApi instance, base route and serializer can be overridden by a given but be careful,
  /// by doing that all interceptors will not be executed
  SauvegardeControllerApi getSauvegardeControllerApi() {
    return SauvegardeControllerApi(dio);
  }

  /// Get SignalementControllerApi instance, base route and serializer can be overridden by a given but be careful,
  /// by doing that all interceptors will not be executed
  SignalementControllerApi getSignalementControllerApi() {
    return SignalementControllerApi(dio);
  }

  /// Get UtilisateurControllerApi instance, base route and serializer can be overridden by a given but be careful,
  /// by doing that all interceptors will not be executed
  UtilisateurControllerApi getUtilisateurControllerApi() {
    return UtilisateurControllerApi(dio);
  }

  /// Get WebhookControllerApi instance, base route and serializer can be overridden by a given but be careful,
  /// by doing that all interceptors will not be executed
  WebhookControllerApi getWebhookControllerApi() {
    return WebhookControllerApi(dio);
  }
}
