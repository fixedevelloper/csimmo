import 'package:drift/drift.dart';

/// Encaissements saisis sur le téléphone, en attente d'envoi au serveur. [id] est la clé d'idempotence (UUID v4) envoyée dans
/// l'en-tête `Idempotency-Key` : un nouvel envoi du même enregistrement ne peut jamais créer un second paiement.
class EncaissementsEnAttente extends Table {
  TextColumn get id => text()();

  /// Agent qui a saisi (identifiant du compte, lu dans le jeton) : une file n'est jamais envoyée sous un autre compte.
  TextColumn get utilisateurId => text()();
  TextColumn get bailId => text()();
  TextColumn get bailLibelle => text()();
  TextColumn get locataireNom => text()();
  IntColumn get montant => integer()();

  /// Date du paiement, AAAA-MM-JJ (jour de la saisie, même si l'envoi a lieu plus tard).
  TextColumn get datePaiement => text()();
  DateTimeColumn get creeLe => dateTime()();

  /// EN_ATTENTE, ENVOYE ou REJETE (refus définitif du serveur).
  TextColumn get statut => text().withDefault(const Constant('EN_ATTENTE'))();
  IntColumn get tentatives => integer().withDefault(const Constant(0))();

  /// Code métier stable du dernier échec (ex. SERVEUR_INDISPONIBLE, BAIL_INTROUVABLE).
  TextColumn get derniereErreur => text().nullable()();
  DateTimeColumn get envoyeLe => dateTime().nullable()();

  @override
  Set<Column> get primaryKey => {id};
}

/// Caches de lecture pour travailler sans réseau (remplacés à chaque actualisation, vidés à la déconnexion).
class LocatairesCache extends Table {
  TextColumn get id => text()();
  TextColumn get nom => text()();
  TextColumn get telephone => text().nullable()();

  @override
  Set<Column> get primaryKey => {id};
}

class BauxCache extends Table {
  TextColumn get id => text()();
  TextColumn get numero => text()();
  TextColumn get locataireId => text()();
  TextColumn get libelle => text()();
  TextColumn get statut => text()();

  /// Loyer + charges mensuels (FCFA).
  IntColumn get montantMensuel => integer()();

  @override
  Set<Column> get primaryKey => {id};
}

class LotsCache extends Table {
  TextColumn get id => text()();
  TextColumn get reference => text()();
  TextColumn get immeubleNom => text()();
  TextColumn get statut => text()();

  @override
  Set<Column> get primaryKey => {id};
}

/// Valeurs simples (date de la dernière actualisation des caches…).
class MetaLocale extends Table {
  TextColumn get cle => text()();
  TextColumn get valeur => text()();

  @override
  Set<Column> get primaryKey => {cle};
}
