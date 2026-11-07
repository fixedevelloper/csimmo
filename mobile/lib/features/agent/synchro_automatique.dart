import 'dart:async';

import 'package:flutter/widgets.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';

import '../../core/auth/role.dart';
import '../../core/providers.dart';
import 'synchro_encaissements.dart';

part 'synchro_automatique.g.dart';

/// Relance l'envoi de la file : toutes les 30 secondes tant que l'application est ouverte, au retour au premier plan, et à la demande
/// (après une saisie). Sans réseau le passage échoue sans bruit et la file attend ; le retour du réseau est détecté par le passage suivant.
class SynchroAutomatique with WidgetsBindingObserver {
  SynchroAutomatique(this._ref, {this.intervalle = const Duration(seconds: 30)});

  final Ref _ref;
  final Duration intervalle;
  Timer? _minuteur;

  bool get _agent => switch (_ref.read(sessionProvider)) {
        Role.gestionnaire || Role.admin || Role.encaisseur => true,
        _ => false,
      };

  void demarrer() {
    WidgetsBinding.instance.addObserver(this);
    _minuteur ??= Timer.periodic(intervalle, (_) => declencher());
  }

  /// Un passage maintenant (sans effet si l'utilisateur n'est pas un agent ou si un passage est déjà en cours).
  Future<void> declencher() async {
    if (!_agent) return;
    try {
      await _ref.read(synchroEncaissementsProvider).synchroniser();
    } catch (_) {
      // un passage raté ne doit jamais faire planter l'application : le suivant réessaiera
    }
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    if (state == AppLifecycleState.resumed) unawaited(declencher());
  }

  void arreter() {
    WidgetsBinding.instance.removeObserver(this);
    _minuteur?.cancel();
    _minuteur = null;
  }
}

@Riverpod(keepAlive: true)
SynchroAutomatique synchroAutomatique(Ref ref) {
  final s = SynchroAutomatique(ref);
  ref.onDispose(s.arreter);
  s.demarrer();
  // au démarrage avec une session d'agent déjà ouverte, et à chaque ouverture de session
  ref.listen(sessionProvider, (_, role) => unawaited(s.declencher()), fireImmediately: true);
  return s;
}
