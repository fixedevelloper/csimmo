import 'package:dio/dio.dart';
import 'package:drift_flutter/drift_flutter.dart';
import 'package:immogest_api/immogest_api.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';

import 'auth/role.dart';
import 'auth/token_store.dart';
import 'db/app_database.dart';
import 'network/api_factory.dart';
import 'storage/key_value_store.dart';

part 'providers.g.dart';

/// État lu dans le stockage avant le premier affichage (évite un écran de chargement).
class BootState {
  const BootState({this.serverUrl, this.tokens});

  final String? serverUrl;
  final Tokens? tokens;
}

/// Surchargé dans `main()` après lecture du stockage sécurisé.
@Riverpod(keepAlive: true)
BootState boot(Ref ref) => const BootState();

@Riverpod(keepAlive: true)
KeyValueStore keyValueStore(Ref ref) => SecureKeyValueStore();

@Riverpod(keepAlive: true)
TokenStore tokenStore(Ref ref) => TokenStore(ref.watch(keyValueStoreProvider));

/// Adresse du serveur ImmoGest du client (`null` tant que non configurée).
@Riverpod(keepAlive: true)
class ServerSettings extends _$ServerSettings {
  static const _key = 'server.url';

  @override
  String? build() => ref.watch(bootProvider).serverUrl;

  Future<void> save(String url) async {
    await ref.read(keyValueStoreProvider).write(_key, url);
    state = url;
  }

  /// Oublie le serveur ET la session (changer de serveur = changer d'installation).
  Future<void> forget() async {
    await ref.read(tokenStoreProvider).clear();
    await ref.read(keyValueStoreProvider).delete(_key);
    ref.read(sessionProvider.notifier).clear();
    state = null;
  }

  static Future<BootState> load(KeyValueStore store) async {
    final url = await store.read(_key);
    return BootState(serverUrl: url, tokens: await TokenStore(store).read());
  }
}

/// Rôle de l'utilisateur connecté (`null` = déconnecté).
@Riverpod(keepAlive: true)
class Session extends _$Session {
  @override
  Role? build() => ref.watch(bootProvider).tokens?.role;

  Future<void> signIn(Tokens tokens) async {
    await ref.read(tokenStoreProvider).save(tokens);
    state = tokens.role;
  }

  /// Retire la session de l'état (les jetons sont déjà effacés ou le seront par l'appelant).
  void clear() => state = null;

  Future<void> signOut() async {
    await ref.read(tokenStoreProvider).clear();
    state = null;
  }
}

/// Client de l'API du serveur configuré. Reconstruit si l'adresse change.
@Riverpod(keepAlive: true)
ImmogestApi api(Ref ref) {
  final url = ref.watch(serverSettingsProvider);
  if (url == null) throw StateError('Aucun serveur configuré');
  return buildApi(
    baseUrl: url,
    tokens: ref.watch(tokenStoreProvider),
    onSessionLost: () async => ref.read(sessionProvider.notifier).clear(),
  );
}

@Riverpod(keepAlive: true)
AppDatabase appDatabase(Ref ref) {
  final db = AppDatabase(driftDatabase(name: 'immogest'));
  ref.onDispose(db.close);
  return db;
}

/// Vérifie qu'une adresse répond comme un serveur ImmoGest (GET /api/v1/health).
@Riverpod(keepAlive: true)
ServerProbe serverProbe(Ref ref) => ServerProbe();

class ServerProbe {
  Future<bool> isImmoGest(String baseUrl) async {
    final api = ImmogestApi(dio: Dio(optionsReseau(baseUrl)), interceptors: const []);
    final reponse = await api.getHealthControllerApi().health();
    final data = reponse.data;
    return data != null && data['status'] == 'UP';
  }
}
