import 'package:drift/drift.dart';

import 'tables.dart';

part 'app_database.g.dart';

/// Base locale SQLite : file d'encaissements hors ligne et caches de lecture de l'espace agent.
@DriftDatabase(tables: [EncaissementsEnAttente, LocatairesCache, BauxCache, LotsCache, MetaLocale])
class AppDatabase extends _$AppDatabase {
  AppDatabase(super.e);

  @override
  int get schemaVersion => 2;

  @override
  MigrationStrategy get migration => MigrationStrategy(
        onCreate: (m) => m.createAll(),
        // v1 : base vide (étape 0.5). v2 : file d'encaissements et caches.
        onUpgrade: (m, from, to) async {
          if (from < 2) await m.createAll();
        },
      );
}
