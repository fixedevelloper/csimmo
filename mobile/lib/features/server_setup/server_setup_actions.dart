import 'package:riverpod_annotation/riverpod_annotation.dart';

import '../../core/config/server_url.dart';
import '../../core/error/app_error.dart';
import '../../core/providers.dart';

part 'server_setup_actions.g.dart';

class ServerSetupActions {
  ServerSetupActions(this._ref);

  final Ref _ref;

  static const nonImmogest = 'SERVEUR_NON_IMMOGEST';

  /// Normalise l'adresse, vérifie qu'un serveur ImmoGest répond, puis l'enregistre.
  /// Lève [ServerUrlException] ou [AppError].
  Future<String> valider(String saisie) async {
    final url = ServerUrl.parse(saisie);
    final ok = await guard(() => _ref.read(serverProbeProvider).isImmoGest(url));
    if (!ok) throw const AppError(nonImmogest);
    await _ref.read(serverSettingsProvider.notifier).save(url);
    return url;
  }
}

@Riverpod(keepAlive: true)
ServerSetupActions serverSetupActions(Ref ref) => ServerSetupActions(ref);
