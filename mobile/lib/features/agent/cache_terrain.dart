import 'package:drift/drift.dart';
import 'package:immogest_api/immogest_api.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';

import '../../core/db/app_database.dart';
import '../../core/error/app_error.dart';
import '../../core/providers.dart';

part 'cache_terrain.g.dart';

/// Copie locale des lots, locataires et baux pour travailler sans réseau. Remplacée d'un bloc à chaque actualisation réussie
/// (un échec réseau laisse l'ancienne copie intacte) et vidée à la déconnexion : elle contient des données personnelles.
class CacheTerrain {
  CacheTerrain(this._db, this._api, {DateTime Function()? maintenant}) : _maintenant = maintenant ?? DateTime.now;

  final AppDatabase _db;
  final ImmogestApi _api;
  final DateTime Function() _maintenant;

  static const _taillePage = 100;
  static const _pagesMax = 30; // 3 000 éléments par liste au plus : au-delà, la recherche en ligne prendra le relais

  Future<void> actualiser() => guard(() async {
        final locataires = <LocataireDto>[];
        final baux = <BailDto>[];
        final lots = <LotDto>[];
        for (var p = 0; p < _pagesMax; p++) {
          final page = (await _api.getLocataireControllerApi().listerLocataires(page: p, size: _taillePage, sort: 'nom')).data;
          locataires.addAll(page?.content ?? const []);
          if ((page?.content?.length ?? 0) < _taillePage) break;
        }
        for (var p = 0; p < _pagesMax; p++) {
          final page = (await _api.getBailControllerApi().listerBaux(page: p, size: _taillePage, sort: 'numero')).data;
          baux.addAll(page?.content ?? const []);
          if ((page?.content?.length ?? 0) < _taillePage) break;
        }
        for (var p = 0; p < _pagesMax; p++) {
          final page = (await _api.getLotControllerApi().listerLots(page: p, size: _taillePage, sort: 'reference')).data;
          lots.addAll(page?.content ?? const []);
          if ((page?.content?.length ?? 0) < _taillePage) break;
        }
        // tout est lu avant d'écrire : une coupure en cours de route ne laisse jamais une copie à moitié remplacée
        await _db.transaction(() async {
          await _db.delete(_db.locatairesCache).go();
          await _db.delete(_db.bauxCache).go();
          await _db.delete(_db.lotsCache).go();
          await _db.batch((b) {
            b.insertAll(_db.locatairesCache,
                locataires.where((l) => l.id != null && l.archive != true).map((l) => LocatairesCacheCompanion.insert(id: l.id!, nom: l.nom ?? '', telephone: Value(l.telephone))));
            b.insertAll(
                _db.bauxCache,
                baux.where((x) => x.id != null && x.locataireId != null && x.statut != BailDtoStatutEnum.BROUILLON).map((x) => BauxCacheCompanion.insert(
                      id: x.id!,
                      numero: x.numero ?? '',
                      locataireId: x.locataireId!,
                      libelle: '${x.immeubleNom ?? ''} — ${x.lotReference ?? ''}',
                      statut: x.statut?.value ?? '',
                      montantMensuel: (x.loyer ?? 0) + (x.charges ?? 0),
                    )));
            b.insertAll(
                _db.lotsCache,
                lots.where((l) => l.id != null && l.archive != true).map((l) => LotsCacheCompanion.insert(
                      id: l.id!,
                      reference: l.reference ?? '',
                      immeubleNom: l.immeubleNom ?? '',
                      statut: l.statut?.value ?? '',
                    )));
          });
          await _db.into(_db.metaLocale).insertOnConflictUpdate(MetaLocaleCompanion.insert(cle: 'cache.maj', valeur: _maintenant().toIso8601String()));
        });
      });

  Stream<DateTime?> derniereActualisation() =>
      (_db.select(_db.metaLocale)..where((m) => m.cle.equals('cache.maj'))).watchSingleOrNull().map((m) => m == null ? null : DateTime.tryParse(m.valeur));

  Future<List<LocatairesCacheData>> chercherLocataires(String q, {int limite = 30}) {
    final terme = q.trim();
    return (_db.select(_db.locatairesCache)
          ..where((l) => terme.isEmpty ? const Constant(true) : l.nom.like('%$terme%') | l.telephone.like('%$terme%'))
          ..orderBy([(l) => OrderingTerm.asc(l.nom)])
          ..limit(limite))
        .get();
  }

  /// Baux signés d'un locataire sur lesquels un paiement peut être saisi (en cours, résiliés, terminés avec arriérés).
  Future<List<BauxCacheData>> bauxDuLocataire(String locataireId) =>
      (_db.select(_db.bauxCache)..where((b) => b.locataireId.equals(locataireId))..orderBy([(b) => OrderingTerm.desc(b.numero)])).get();

  Future<List<LotsCacheData>> lots(String q, {int limite = 100}) {
    final terme = q.trim();
    return (_db.select(_db.lotsCache)
          ..where((l) => terme.isEmpty ? const Constant(true) : l.reference.like('%$terme%') | l.immeubleNom.like('%$terme%'))
          ..orderBy([(l) => OrderingTerm.asc(l.immeubleNom), (l) => OrderingTerm.asc(l.reference)])
          ..limit(limite))
        .get();
  }

  Future<void> vider() async {
    await _db.delete(_db.locatairesCache).go();
    await _db.delete(_db.bauxCache).go();
    await _db.delete(_db.lotsCache).go();
    await (_db.delete(_db.metaLocale)..where((m) => m.cle.equals('cache.maj'))).go();
  }
}

@Riverpod(keepAlive: true)
CacheTerrain cacheTerrain(Ref ref) => CacheTerrain(ref.watch(appDatabaseProvider), ref.watch(apiProvider));
