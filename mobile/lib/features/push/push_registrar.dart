import 'dart:async';
import 'dart:io';

import 'package:immogest_api/immogest_api.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';

import '../../core/providers.dart';
import 'push_service.dart';

part 'push_registrar.g.dart';

/// Enregistre l'appareil auprès du serveur pour recevoir les notifications push. Entièrement facultatif : si le canal push n'est pas
/// configuré côté serveur, si l'utilisateur refuse ou si quoi que ce soit échoue, l'application fonctionne sans (SMS et e-mail restent).
class PushRegistrar {
  PushRegistrar(this._ref);

  final Ref _ref;
  String? _jetonEnregistre;
  StreamSubscription<String>? _renouvellement;
  StreamSubscription<PushMessage>? _premierPlan;
  final _messages = StreamController<PushMessage>.broadcast();

  /// Messages reçus au premier plan, à afficher par l'application.
  Stream<PushMessage> get messages => _messages.stream;

  NotificationControllerApi get _api => _ref.read(apiProvider).getNotificationControllerApi();

  /// À appeler une fois la session ouverte.
  Future<void> enregistrer() async {
    try {
      final config = (await _api.configMobile()).data?.push;
      if (config == null) return; // push non configuré chez ce client
      final push = _ref.read(pushServiceProvider);
      if (!await push.demarrer(config)) return;
      final jeton = await push.jeton();
      if (jeton == null || jeton.isEmpty) return;
      await _envoyer(jeton);
      _renouvellement ??= push.jetonRenouvele.listen((j) => _envoyer(j).catchError((_) {}));
      _premierPlan ??= push.messages.listen(_messages.add);
    } catch (_) {
      // push facultatif : jamais d'erreur visible
    }
  }

  Future<void> _envoyer(String jeton) async {
    await _api.enregistrerAppareil(appareilRequete: AppareilRequete(token: jeton, plateforme: Platform.isIOS ? 'IOS' : 'ANDROID'));
    _jetonEnregistre = jeton;
  }

  /// À appeler AVANT la fermeture de session (l'appel exige encore le jeton d'accès) : l'appareil ne reçoit plus les messages du compte.
  Future<void> retirer() async {
    final jeton = _jetonEnregistre;
    if (jeton == null) return;
    _jetonEnregistre = null;
    try {
      await _api.retirerAppareil(token: jeton);
    } catch (_) {
      // meilleur effort : le serveur ignore de toute façon les jetons invalides
    }
  }

  Future<void> dispose() async {
    await _renouvellement?.cancel();
    await _premierPlan?.cancel();
    await _messages.close();
  }
}

@Riverpod(keepAlive: true)
PushRegistrar pushRegistrar(Ref ref) {
  final registrar = PushRegistrar(ref);
  ref.onDispose(registrar.dispose);
  // Dès qu'une session est ouverte (y compris au démarrage de l'application avec une session mémorisée)
  ref.listen(sessionProvider, (_, role) {
    if (role != null) unawaited(registrar.enregistrer());
  }, fireImmediately: true);
  return registrar;
}
