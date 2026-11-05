import 'package:drift/native.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:immogest_mobile/core/auth/jwt.dart';
import 'package:immogest_mobile/core/db/app_database.dart';
import 'package:immogest_mobile/core/error/app_error.dart';
import 'package:immogest_mobile/features/agent/cache_terrain.dart';

import '../support/fake_adapter.dart';
import '../support/jwt_support.dart';
import '../support/locataire_support.dart';

void main() {
  group('sujetDuJwt', () {
    test('lit l\'identifiant du compte', () => expect(sujetDuJwt(jeton({'sub': 'u-123', 'exp': 1})), 'u-123'));
    test('jeton mal formé ou sans sub : null, jamais d\'exception', () {
      expect(sujetDuJwt('pas-un-jwt'), isNull);
      expect(sujetDuJwt('a.b.c'), isNull);
      expect(sujetDuJwt(jeton({'exp': 1})), isNull);
      expect(sujetDuJwt(jeton({'sub': 42})), isNull);
      expect(sujetDuJwt(''), isNull);
    });
  });

  group('CacheTerrain', () {
    late AppDatabase db;
    var maintenant = DateTime(2026, 11, 4, 8);
    setUp(() => db = AppDatabase(NativeDatabase.memory()));
    tearDown(() => db.close());

    Reponse Function(dynamic) serveur({bool lotsEnPanne = false, List<Map<String, Object?>>? locataires}) => (o) {
          switch (o.path) {
            case '/api/v1/locataires':
              return (status: 200, corps: page(locataires ?? [
                    {'id': 'l-1', 'nom': 'Paul Mbarga', 'telephone': '+237699000001', 'archive': false},
                    {'id': 'l-2', 'nom': 'Marie Ngo', 'telephone': '+237699000002', 'archive': false},
                    {'id': 'l-3', 'nom': 'Ancien Archivé', 'archive': true},
                  ]));
            case '/api/v1/baux':
              return (status: 200, corps: page([
                    {'id': 'b-1', 'numero': 'BAIL-1', 'locataireId': 'l-1', 'immeubleNom': 'Palmiers', 'lotReference': 'A1', 'statut': 'ACTIF', 'loyer': 150000, 'charges': 10000},
                    {'id': 'b-2', 'numero': 'BAIL-2', 'locataireId': 'l-2', 'immeubleNom': 'Palmiers', 'lotReference': 'B2', 'statut': 'BROUILLON', 'loyer': 1, 'charges': 0},
                  ]));
            case '/api/v1/lots':
              return lotsEnPanne
                  ? (status: 500, corps: {'code': 'ERREUR'})
                  : (status: 200, corps: page([
                      {'id': 'lot-1', 'reference': 'A1', 'immeubleNom': 'Palmiers', 'statut': 'OCCUPE'},
                      {'id': 'lot-2', 'reference': 'B2', 'immeubleNom': 'Baobabs', 'statut': 'LIBRE'},
                    ]));
          }
          return (status: 404, corps: null);
        };

    CacheTerrain cache(Reponse Function(dynamic) h) {
      final (api, _) = apiFactice((o) => h(o));
      return CacheTerrain(db, api, maintenant: () => maintenant);
    }

    test('actualiser copie lots, locataires et baux signés ; archivés et brouillons exclus', () async {
      final c = cache(serveur());
      await c.actualiser();
      expect((await c.chercherLocataires('')).map((l) => l.nom), ['Marie Ngo', 'Paul Mbarga']);
      expect((await c.bauxDuLocataire('l-1')).single.montantMensuel, 160000);
      expect(await c.bauxDuLocataire('l-2'), isEmpty); // brouillon exclu
      expect((await c.lots('')).map((l) => '${l.immeubleNom} ${l.reference}'), ['Baobabs B2', 'Palmiers A1']);
      expect(await c.derniereActualisation().first, maintenant);
    });

    test('recherche par nom ou par téléphone', () async {
      final c = cache(serveur());
      await c.actualiser();
      expect((await c.chercherLocataires('mbar')).single.id, 'l-1');
      expect((await c.chercherLocataires('0002')).single.id, 'l-2');
      expect(await c.chercherLocataires('zzz'), isEmpty);
      expect((await c.lots('baob')).single.id, 'lot-2');
    });

    test('un échec en cours d\'actualisation laisse l\'ancienne copie intacte', () async {
      await cache(serveur()).actualiser();
      await expectLater(cache(serveur(lotsEnPanne: true, locataires: [
        {'id': 'l-9', 'nom': 'Nouveau', 'archive': false},
      ])).actualiser(), throwsA(isA<AppError>()));
      final c = cache(serveur());
      expect((await c.chercherLocataires('')).map((l) => l.nom), ['Marie Ngo', 'Paul Mbarga']); // pas de « Nouveau »
      expect(await c.lots(''), hasLength(2));
    });

    test('les listes de plus de 100 éléments sont lues page par page', () async {
      final appels = <Object?>[];
      final c = cache((o) {
        if (o.path == '/api/v1/locataires') {
          appels.add(o.queryParameters['page']);
          final p = o.queryParameters['page'] as int;
          final n = p == 0 ? 100 : 30;
          return (status: 200, corps: page([for (var i = 0; i < n; i++) {'id': 'l-$p-$i', 'nom': 'Locataire $p-$i', 'archive': false}]));
        }
        return (status: 200, corps: page([]));
      });
      await c.actualiser();
      expect(appels, [0, 1]);
      expect((await c.chercherLocataires('', limite: 500)), hasLength(130));
    });

    test('vider supprime tout (données personnelles) sans toucher à la file d\'encaissements', () async {
      final c = cache(serveur());
      await c.actualiser();
      await db.into(db.encaissementsEnAttente).insert(EncaissementsEnAttenteCompanion.insert(
          id: 'e-1', utilisateurId: 'a', bailId: 'b', bailLibelle: 'x', locataireNom: 'y', montant: 1, datePaiement: '2026-11-04', creeLe: DateTime(2026)));
      await c.vider();
      expect(await c.chercherLocataires(''), isEmpty);
      expect(await c.lots(''), isEmpty);
      expect(await c.bauxDuLocataire('l-1'), isEmpty);
      expect(await c.derniereActualisation().first, isNull);
      expect(await db.select(db.encaissementsEnAttente).get(), hasLength(1));
    });
  });
}
