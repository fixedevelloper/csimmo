import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:immogest_api/immogest_api.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';

part 'push_service.g.dart';

/// Message reçu quand l'application est au premier plan.
class PushMessage {
  const PushMessage({this.titre, this.corps});

  final String? titre;
  final String? corps;
}

/// Notifications push. Abstrait : les tests utilisent une version factice, sans Firebase.
abstract class PushService {
  /// Initialise Firebase avec les identifiants publics donnés par le serveur de l'installation et demande l'autorisation
  /// d'afficher des notifications. `false` si l'utilisateur refuse.
  Future<bool> demarrer(PushMobile config);

  Future<String?> jeton();

  Stream<String> get jetonRenouvele;

  Stream<PushMessage> get messages;
}

/// Firebase Cloud Messaging, initialisé à l'exécution : chaque installation a son propre projet Firebase, dont le serveur
/// communique les identifiants publics (aucun google-services.json embarqué dans l'application).
class FirebasePushService implements PushService {
  @override
  Future<bool> demarrer(PushMobile config) async {
    if (Firebase.apps.isEmpty) {
      await Firebase.initializeApp(
        options: FirebaseOptions(
          apiKey: config.apiKey ?? '',
          appId: config.appId ?? '',
          messagingSenderId: config.messagingSenderId ?? '',
          projectId: config.projectId ?? '',
        ),
      );
    }
    final autorisation = await FirebaseMessaging.instance.requestPermission();
    return autorisation.authorizationStatus != AuthorizationStatus.denied;
  }

  // getToken/onTokenRefresh sont marqués obsolètes au profit de register()/onRegistered, qui fournissent un identifiant d'installation
  // (FID) et non le jeton d'enregistrement FCM attendu par l'API HTTP v1 utilisée par le serveur : on garde donc l'ancienne API.
  @override
  // ignore: deprecated_member_use
  Future<String?> jeton() => FirebaseMessaging.instance.getToken();

  @override
  // ignore: deprecated_member_use
  Stream<String> get jetonRenouvele => FirebaseMessaging.instance.onTokenRefresh;

  @override
  Stream<PushMessage> get messages =>
      FirebaseMessaging.onMessage.map((m) => PushMessage(titre: m.notification?.title, corps: m.notification?.body));
}

@Riverpod(keepAlive: true)
PushService pushService(Ref ref) => FirebasePushService();
