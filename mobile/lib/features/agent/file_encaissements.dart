import 'package:drift/drift.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';

import '../../core/db/app_database.dart';
import '../../core/id/uuid.dart';
import '../../core/providers.dart';

part 'file_encaissements.g.dart';

const statutEnAttente = 'EN_ATTENTE';
const statutEnvoye = 'ENVOYE';
const statutRejete = 'REJETE';

/// File locale d'encaissements en espèces. Toute saisie passe par ici, en ligne ou non : un seul chemin, donc un seul comportement.
class FileEncaissements {
  FileEncaissements(this._db, {DateTime Function()? maintenant, String Function()? genererId})
      : _maintenant = maintenant ?? DateTime.now,
        _genererId = genererId ?? uuidV4;

  final AppDatabase _db;
  final DateTime Function() _maintenant;
  final String Function() _genererId;

  /// Enregistre un encaissement et lui attribue sa clé d'idempotence définitive.
  Future<String> ajouter({
    required String utilisateurId,
    required String bailId,
    required String bailLibelle,
    required String locataireNom,
    required int montant,
  }) async {
    if (montant <= 0) throw ArgumentError.value(montant, 'montant', 'doit être strictement positif');
    final maintenant = _maintenant();
    final id = _genererId();
    await _db.into(_db.encaissementsEnAttente).insert(EncaissementsEnAttenteCompanion.insert(
          id: id,
          utilisateurId: utilisateurId,
          bailId: bailId,
          bailLibelle: bailLibelle,
          locataireNom: locataireNom,
          montant: montant,
          datePaiement: _jour(maintenant),
          creeLe: maintenant,
        ));
    return id;
  }

  /// À envoyer, du plus ancien au plus récent, pour cet agent seulement.
  Future<List<EncaissementsEnAttenteData>> aEnvoyer(String utilisateurId) => (_db.select(_db.encaissementsEnAttente)
        ..where((e) => e.utilisateurId.equals(utilisateurId) & e.statut.equals(statutEnAttente))
        ..orderBy([(e) => OrderingTerm.asc(e.creeLe)]))
      .get();

  /// Tout ce que l'agent a saisi récemment (en attente, envoyé, rejeté), du plus récent au plus ancien.
  Stream<List<EncaissementsEnAttenteData>> suivre(String utilisateurId) => (_db.select(_db.encaissementsEnAttente)
        ..where((e) => e.utilisateurId.equals(utilisateurId))
        ..orderBy([(e) => OrderingTerm.desc(e.creeLe)]))
      .watch();

  Stream<int> suivreNombreEnAttente(String utilisateurId) {
    final nombre = _db.encaissementsEnAttente.id.count();
    final requete = _db.selectOnly(_db.encaissementsEnAttente)
      ..addColumns([nombre])
      ..where(_db.encaissementsEnAttente.utilisateurId.equals(utilisateurId) & _db.encaissementsEnAttente.statut.equals(statutEnAttente));
    return requete.watchSingle().map((r) => r.read(nombre) ?? 0);
  }

  Future<void> marquerEnvoye(String id) => _maj(id, EncaissementsEnAttenteCompanion(
        statut: const Value(statutEnvoye),
        envoyeLe: Value(_maintenant()),
        derniereErreur: const Value(null),
        tentatives: const Value.absent(),
      ), incrementer: true);

  /// Échec temporaire : reste en file pour un prochain passage.
  Future<void> noterEchecTemporaire(String id, String code) =>
      _maj(id, EncaissementsEnAttenteCompanion(derniereErreur: Value(code)), incrementer: true);

  /// Refus définitif du serveur : ne sera plus envoyé automatiquement.
  Future<void> marquerRejete(String id, String code) =>
      _maj(id, EncaissementsEnAttenteCompanion(statut: const Value(statutRejete), derniereErreur: Value(code)), incrementer: true);

  Future<void> _maj(String id, EncaissementsEnAttenteCompanion valeurs, {required bool incrementer}) async {
    final ligne = await (_db.select(_db.encaissementsEnAttente)..where((e) => e.id.equals(id))).getSingleOrNull();
    if (ligne == null) return;
    await (_db.update(_db.encaissementsEnAttente)..where((e) => e.id.equals(id))).write(
      valeurs.copyWith(tentatives: Value(ligne.tentatives + (incrementer ? 1 : 0))),
    );
  }

  /// Supprime une saisie jamais tentée (aucun risque qu'elle existe déjà sur le serveur) ou rejetée. Renvoie `false` sinon :
  /// une saisie déjà tentée a pu être créée côté serveur sans que la réponse nous parvienne.
  Future<bool> abandonner(String id) async {
    final ligne = await (_db.select(_db.encaissementsEnAttente)..where((e) => e.id.equals(id))).getSingleOrNull();
    if (ligne == null) return false;
    final possible = ligne.statut == statutRejete || (ligne.statut == statutEnAttente && ligne.tentatives == 0);
    if (!possible) return false;
    await (_db.delete(_db.encaissementsEnAttente)..where((e) => e.id.equals(id))).go();
    return true;
  }

  /// Retire les lignes envoyées depuis plus de [jours] jours (le serveur garde la trace complète).
  Future<int> purgerEnvoyes({int jours = 7}) {
    final limite = _maintenant().subtract(Duration(days: jours));
    return (_db.delete(_db.encaissementsEnAttente)..where((e) => e.statut.equals(statutEnvoye) & e.envoyeLe.isSmallerThanValue(limite))).go();
  }

  static String _jour(DateTime d) => '${d.year.toString().padLeft(4, '0')}-${d.month.toString().padLeft(2, '0')}-${d.day.toString().padLeft(2, '0')}';
}

@Riverpod(keepAlive: true)
FileEncaissements fileEncaissements(Ref ref) => FileEncaissements(ref.watch(appDatabaseProvider));
