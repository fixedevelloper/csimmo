import 'package:drift/native.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:immogest_mobile/core/db/app_database.dart';

void main() {
  test('la base locale s\'ouvre (version 2 : file d\'encaissements et caches) et répond aux requêtes', () async {
    final db = AppDatabase(NativeDatabase.memory());
    addTearDown(db.close);

    expect(db.schemaVersion, 2);
    expect(db.allTables.map((t) => t.actualTableName), containsAll(['encaissements_en_attente', 'locataires_cache', 'baux_cache', 'lots_cache', 'meta_locale']));
    final lignes = await db.customSelect('SELECT 1 AS un').get();
    expect(lignes.single.read<int>('un'), 1);
  });
}
