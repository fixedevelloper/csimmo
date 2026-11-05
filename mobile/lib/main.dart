import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import 'app.dart';
import 'core/providers.dart';
import 'core/storage/key_value_store.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  final store = SecureKeyValueStore();
  final boot = await ServerSettings.load(store);
  runApp(ProviderScope(
    // Pas de nouvel essai automatique : les erreurs réseau sont affichées avec un bouton « Réessayer ».
    retry: (_, _) => null,
    overrides: [
      keyValueStoreProvider.overrideWithValue(store),
      bootProvider.overrideWithValue(boot),
    ],
    child: const ImmoGestApp(),
  ));
}
