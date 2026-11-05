// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'app_database.dart';

// ignore_for_file: type=lint
class $EncaissementsEnAttenteTable extends EncaissementsEnAttente
    with TableInfo<$EncaissementsEnAttenteTable, EncaissementsEnAttenteData> {
  @override
  final GeneratedDatabase attachedDatabase;
  final String? _alias;
  $EncaissementsEnAttenteTable(this.attachedDatabase, [this._alias]);
  static const VerificationMeta _idMeta = const VerificationMeta('id');
  @override
  late final GeneratedColumn<String> id = GeneratedColumn<String>(
    'id',
    aliasedName,
    false,
    type: DriftSqlType.string,
    requiredDuringInsert: true,
  );
  static const VerificationMeta _utilisateurIdMeta = const VerificationMeta(
    'utilisateurId',
  );
  @override
  late final GeneratedColumn<String> utilisateurId = GeneratedColumn<String>(
    'utilisateur_id',
    aliasedName,
    false,
    type: DriftSqlType.string,
    requiredDuringInsert: true,
  );
  static const VerificationMeta _bailIdMeta = const VerificationMeta('bailId');
  @override
  late final GeneratedColumn<String> bailId = GeneratedColumn<String>(
    'bail_id',
    aliasedName,
    false,
    type: DriftSqlType.string,
    requiredDuringInsert: true,
  );
  static const VerificationMeta _bailLibelleMeta = const VerificationMeta(
    'bailLibelle',
  );
  @override
  late final GeneratedColumn<String> bailLibelle = GeneratedColumn<String>(
    'bail_libelle',
    aliasedName,
    false,
    type: DriftSqlType.string,
    requiredDuringInsert: true,
  );
  static const VerificationMeta _locataireNomMeta = const VerificationMeta(
    'locataireNom',
  );
  @override
  late final GeneratedColumn<String> locataireNom = GeneratedColumn<String>(
    'locataire_nom',
    aliasedName,
    false,
    type: DriftSqlType.string,
    requiredDuringInsert: true,
  );
  static const VerificationMeta _montantMeta = const VerificationMeta(
    'montant',
  );
  @override
  late final GeneratedColumn<int> montant = GeneratedColumn<int>(
    'montant',
    aliasedName,
    false,
    type: DriftSqlType.int,
    requiredDuringInsert: true,
  );
  static const VerificationMeta _datePaiementMeta = const VerificationMeta(
    'datePaiement',
  );
  @override
  late final GeneratedColumn<String> datePaiement = GeneratedColumn<String>(
    'date_paiement',
    aliasedName,
    false,
    type: DriftSqlType.string,
    requiredDuringInsert: true,
  );
  static const VerificationMeta _creeLeMeta = const VerificationMeta('creeLe');
  @override
  late final GeneratedColumn<DateTime> creeLe = GeneratedColumn<DateTime>(
    'cree_le',
    aliasedName,
    false,
    type: DriftSqlType.dateTime,
    requiredDuringInsert: true,
  );
  static const VerificationMeta _statutMeta = const VerificationMeta('statut');
  @override
  late final GeneratedColumn<String> statut = GeneratedColumn<String>(
    'statut',
    aliasedName,
    false,
    type: DriftSqlType.string,
    requiredDuringInsert: false,
    defaultValue: const Constant('EN_ATTENTE'),
  );
  static const VerificationMeta _tentativesMeta = const VerificationMeta(
    'tentatives',
  );
  @override
  late final GeneratedColumn<int> tentatives = GeneratedColumn<int>(
    'tentatives',
    aliasedName,
    false,
    type: DriftSqlType.int,
    requiredDuringInsert: false,
    defaultValue: const Constant(0),
  );
  static const VerificationMeta _derniereErreurMeta = const VerificationMeta(
    'derniereErreur',
  );
  @override
  late final GeneratedColumn<String> derniereErreur = GeneratedColumn<String>(
    'derniere_erreur',
    aliasedName,
    true,
    type: DriftSqlType.string,
    requiredDuringInsert: false,
  );
  static const VerificationMeta _envoyeLeMeta = const VerificationMeta(
    'envoyeLe',
  );
  @override
  late final GeneratedColumn<DateTime> envoyeLe = GeneratedColumn<DateTime>(
    'envoye_le',
    aliasedName,
    true,
    type: DriftSqlType.dateTime,
    requiredDuringInsert: false,
  );
  @override
  List<GeneratedColumn> get $columns => [
    id,
    utilisateurId,
    bailId,
    bailLibelle,
    locataireNom,
    montant,
    datePaiement,
    creeLe,
    statut,
    tentatives,
    derniereErreur,
    envoyeLe,
  ];
  @override
  String get aliasedName => _alias ?? actualTableName;
  @override
  String get actualTableName => $name;
  static const String $name = 'encaissements_en_attente';
  @override
  VerificationContext validateIntegrity(
    Insertable<EncaissementsEnAttenteData> instance, {
    bool isInserting = false,
  }) {
    final context = VerificationContext();
    final data = instance.toColumns(true);
    if (data.containsKey('id')) {
      context.handle(_idMeta, id.isAcceptableOrUnknown(data['id']!, _idMeta));
    } else if (isInserting) {
      context.missing(_idMeta);
    }
    if (data.containsKey('utilisateur_id')) {
      context.handle(
        _utilisateurIdMeta,
        utilisateurId.isAcceptableOrUnknown(
          data['utilisateur_id']!,
          _utilisateurIdMeta,
        ),
      );
    } else if (isInserting) {
      context.missing(_utilisateurIdMeta);
    }
    if (data.containsKey('bail_id')) {
      context.handle(
        _bailIdMeta,
        bailId.isAcceptableOrUnknown(data['bail_id']!, _bailIdMeta),
      );
    } else if (isInserting) {
      context.missing(_bailIdMeta);
    }
    if (data.containsKey('bail_libelle')) {
      context.handle(
        _bailLibelleMeta,
        bailLibelle.isAcceptableOrUnknown(
          data['bail_libelle']!,
          _bailLibelleMeta,
        ),
      );
    } else if (isInserting) {
      context.missing(_bailLibelleMeta);
    }
    if (data.containsKey('locataire_nom')) {
      context.handle(
        _locataireNomMeta,
        locataireNom.isAcceptableOrUnknown(
          data['locataire_nom']!,
          _locataireNomMeta,
        ),
      );
    } else if (isInserting) {
      context.missing(_locataireNomMeta);
    }
    if (data.containsKey('montant')) {
      context.handle(
        _montantMeta,
        montant.isAcceptableOrUnknown(data['montant']!, _montantMeta),
      );
    } else if (isInserting) {
      context.missing(_montantMeta);
    }
    if (data.containsKey('date_paiement')) {
      context.handle(
        _datePaiementMeta,
        datePaiement.isAcceptableOrUnknown(
          data['date_paiement']!,
          _datePaiementMeta,
        ),
      );
    } else if (isInserting) {
      context.missing(_datePaiementMeta);
    }
    if (data.containsKey('cree_le')) {
      context.handle(
        _creeLeMeta,
        creeLe.isAcceptableOrUnknown(data['cree_le']!, _creeLeMeta),
      );
    } else if (isInserting) {
      context.missing(_creeLeMeta);
    }
    if (data.containsKey('statut')) {
      context.handle(
        _statutMeta,
        statut.isAcceptableOrUnknown(data['statut']!, _statutMeta),
      );
    }
    if (data.containsKey('tentatives')) {
      context.handle(
        _tentativesMeta,
        tentatives.isAcceptableOrUnknown(data['tentatives']!, _tentativesMeta),
      );
    }
    if (data.containsKey('derniere_erreur')) {
      context.handle(
        _derniereErreurMeta,
        derniereErreur.isAcceptableOrUnknown(
          data['derniere_erreur']!,
          _derniereErreurMeta,
        ),
      );
    }
    if (data.containsKey('envoye_le')) {
      context.handle(
        _envoyeLeMeta,
        envoyeLe.isAcceptableOrUnknown(data['envoye_le']!, _envoyeLeMeta),
      );
    }
    return context;
  }

  @override
  Set<GeneratedColumn> get $primaryKey => {id};
  @override
  EncaissementsEnAttenteData map(
    Map<String, dynamic> data, {
    String? tablePrefix,
  }) {
    final effectivePrefix = tablePrefix != null ? '$tablePrefix.' : '';
    return EncaissementsEnAttenteData(
      id: attachedDatabase.typeMapping.read(
        DriftSqlType.string,
        data['${effectivePrefix}id'],
      )!,
      utilisateurId: attachedDatabase.typeMapping.read(
        DriftSqlType.string,
        data['${effectivePrefix}utilisateur_id'],
      )!,
      bailId: attachedDatabase.typeMapping.read(
        DriftSqlType.string,
        data['${effectivePrefix}bail_id'],
      )!,
      bailLibelle: attachedDatabase.typeMapping.read(
        DriftSqlType.string,
        data['${effectivePrefix}bail_libelle'],
      )!,
      locataireNom: attachedDatabase.typeMapping.read(
        DriftSqlType.string,
        data['${effectivePrefix}locataire_nom'],
      )!,
      montant: attachedDatabase.typeMapping.read(
        DriftSqlType.int,
        data['${effectivePrefix}montant'],
      )!,
      datePaiement: attachedDatabase.typeMapping.read(
        DriftSqlType.string,
        data['${effectivePrefix}date_paiement'],
      )!,
      creeLe: attachedDatabase.typeMapping.read(
        DriftSqlType.dateTime,
        data['${effectivePrefix}cree_le'],
      )!,
      statut: attachedDatabase.typeMapping.read(
        DriftSqlType.string,
        data['${effectivePrefix}statut'],
      )!,
      tentatives: attachedDatabase.typeMapping.read(
        DriftSqlType.int,
        data['${effectivePrefix}tentatives'],
      )!,
      derniereErreur: attachedDatabase.typeMapping.read(
        DriftSqlType.string,
        data['${effectivePrefix}derniere_erreur'],
      ),
      envoyeLe: attachedDatabase.typeMapping.read(
        DriftSqlType.dateTime,
        data['${effectivePrefix}envoye_le'],
      ),
    );
  }

  @override
  $EncaissementsEnAttenteTable createAlias(String alias) {
    return $EncaissementsEnAttenteTable(attachedDatabase, alias);
  }
}

class EncaissementsEnAttenteData extends DataClass
    implements Insertable<EncaissementsEnAttenteData> {
  final String id;

  /// Agent qui a saisi (identifiant du compte, lu dans le jeton) : une file n'est jamais envoyée sous un autre compte.
  final String utilisateurId;
  final String bailId;
  final String bailLibelle;
  final String locataireNom;
  final int montant;

  /// Date du paiement, AAAA-MM-JJ (jour de la saisie, même si l'envoi a lieu plus tard).
  final String datePaiement;
  final DateTime creeLe;

  /// EN_ATTENTE, ENVOYE ou REJETE (refus définitif du serveur).
  final String statut;
  final int tentatives;

  /// Code métier stable du dernier échec (ex. SERVEUR_INDISPONIBLE, BAIL_INTROUVABLE).
  final String? derniereErreur;
  final DateTime? envoyeLe;
  const EncaissementsEnAttenteData({
    required this.id,
    required this.utilisateurId,
    required this.bailId,
    required this.bailLibelle,
    required this.locataireNom,
    required this.montant,
    required this.datePaiement,
    required this.creeLe,
    required this.statut,
    required this.tentatives,
    this.derniereErreur,
    this.envoyeLe,
  });
  @override
  Map<String, Expression> toColumns(bool nullToAbsent) {
    final map = <String, Expression>{};
    map['id'] = Variable<String>(id);
    map['utilisateur_id'] = Variable<String>(utilisateurId);
    map['bail_id'] = Variable<String>(bailId);
    map['bail_libelle'] = Variable<String>(bailLibelle);
    map['locataire_nom'] = Variable<String>(locataireNom);
    map['montant'] = Variable<int>(montant);
    map['date_paiement'] = Variable<String>(datePaiement);
    map['cree_le'] = Variable<DateTime>(creeLe);
    map['statut'] = Variable<String>(statut);
    map['tentatives'] = Variable<int>(tentatives);
    if (!nullToAbsent || derniereErreur != null) {
      map['derniere_erreur'] = Variable<String>(derniereErreur);
    }
    if (!nullToAbsent || envoyeLe != null) {
      map['envoye_le'] = Variable<DateTime>(envoyeLe);
    }
    return map;
  }

  EncaissementsEnAttenteCompanion toCompanion(bool nullToAbsent) {
    return EncaissementsEnAttenteCompanion(
      id: Value(id),
      utilisateurId: Value(utilisateurId),
      bailId: Value(bailId),
      bailLibelle: Value(bailLibelle),
      locataireNom: Value(locataireNom),
      montant: Value(montant),
      datePaiement: Value(datePaiement),
      creeLe: Value(creeLe),
      statut: Value(statut),
      tentatives: Value(tentatives),
      derniereErreur: derniereErreur == null && nullToAbsent
          ? const Value.absent()
          : Value(derniereErreur),
      envoyeLe: envoyeLe == null && nullToAbsent
          ? const Value.absent()
          : Value(envoyeLe),
    );
  }

  factory EncaissementsEnAttenteData.fromJson(
    Map<String, dynamic> json, {
    ValueSerializer? serializer,
  }) {
    serializer ??= driftRuntimeOptions.defaultSerializer;
    return EncaissementsEnAttenteData(
      id: serializer.fromJson<String>(json['id']),
      utilisateurId: serializer.fromJson<String>(json['utilisateurId']),
      bailId: serializer.fromJson<String>(json['bailId']),
      bailLibelle: serializer.fromJson<String>(json['bailLibelle']),
      locataireNom: serializer.fromJson<String>(json['locataireNom']),
      montant: serializer.fromJson<int>(json['montant']),
      datePaiement: serializer.fromJson<String>(json['datePaiement']),
      creeLe: serializer.fromJson<DateTime>(json['creeLe']),
      statut: serializer.fromJson<String>(json['statut']),
      tentatives: serializer.fromJson<int>(json['tentatives']),
      derniereErreur: serializer.fromJson<String?>(json['derniereErreur']),
      envoyeLe: serializer.fromJson<DateTime?>(json['envoyeLe']),
    );
  }
  @override
  Map<String, dynamic> toJson({ValueSerializer? serializer}) {
    serializer ??= driftRuntimeOptions.defaultSerializer;
    return <String, dynamic>{
      'id': serializer.toJson<String>(id),
      'utilisateurId': serializer.toJson<String>(utilisateurId),
      'bailId': serializer.toJson<String>(bailId),
      'bailLibelle': serializer.toJson<String>(bailLibelle),
      'locataireNom': serializer.toJson<String>(locataireNom),
      'montant': serializer.toJson<int>(montant),
      'datePaiement': serializer.toJson<String>(datePaiement),
      'creeLe': serializer.toJson<DateTime>(creeLe),
      'statut': serializer.toJson<String>(statut),
      'tentatives': serializer.toJson<int>(tentatives),
      'derniereErreur': serializer.toJson<String?>(derniereErreur),
      'envoyeLe': serializer.toJson<DateTime?>(envoyeLe),
    };
  }

  EncaissementsEnAttenteData copyWith({
    String? id,
    String? utilisateurId,
    String? bailId,
    String? bailLibelle,
    String? locataireNom,
    int? montant,
    String? datePaiement,
    DateTime? creeLe,
    String? statut,
    int? tentatives,
    Value<String?> derniereErreur = const Value.absent(),
    Value<DateTime?> envoyeLe = const Value.absent(),
  }) => EncaissementsEnAttenteData(
    id: id ?? this.id,
    utilisateurId: utilisateurId ?? this.utilisateurId,
    bailId: bailId ?? this.bailId,
    bailLibelle: bailLibelle ?? this.bailLibelle,
    locataireNom: locataireNom ?? this.locataireNom,
    montant: montant ?? this.montant,
    datePaiement: datePaiement ?? this.datePaiement,
    creeLe: creeLe ?? this.creeLe,
    statut: statut ?? this.statut,
    tentatives: tentatives ?? this.tentatives,
    derniereErreur: derniereErreur.present
        ? derniereErreur.value
        : this.derniereErreur,
    envoyeLe: envoyeLe.present ? envoyeLe.value : this.envoyeLe,
  );
  EncaissementsEnAttenteData copyWithCompanion(
    EncaissementsEnAttenteCompanion data,
  ) {
    return EncaissementsEnAttenteData(
      id: data.id.present ? data.id.value : this.id,
      utilisateurId: data.utilisateurId.present
          ? data.utilisateurId.value
          : this.utilisateurId,
      bailId: data.bailId.present ? data.bailId.value : this.bailId,
      bailLibelle: data.bailLibelle.present
          ? data.bailLibelle.value
          : this.bailLibelle,
      locataireNom: data.locataireNom.present
          ? data.locataireNom.value
          : this.locataireNom,
      montant: data.montant.present ? data.montant.value : this.montant,
      datePaiement: data.datePaiement.present
          ? data.datePaiement.value
          : this.datePaiement,
      creeLe: data.creeLe.present ? data.creeLe.value : this.creeLe,
      statut: data.statut.present ? data.statut.value : this.statut,
      tentatives: data.tentatives.present
          ? data.tentatives.value
          : this.tentatives,
      derniereErreur: data.derniereErreur.present
          ? data.derniereErreur.value
          : this.derniereErreur,
      envoyeLe: data.envoyeLe.present ? data.envoyeLe.value : this.envoyeLe,
    );
  }

  @override
  String toString() {
    return (StringBuffer('EncaissementsEnAttenteData(')
          ..write('id: $id, ')
          ..write('utilisateurId: $utilisateurId, ')
          ..write('bailId: $bailId, ')
          ..write('bailLibelle: $bailLibelle, ')
          ..write('locataireNom: $locataireNom, ')
          ..write('montant: $montant, ')
          ..write('datePaiement: $datePaiement, ')
          ..write('creeLe: $creeLe, ')
          ..write('statut: $statut, ')
          ..write('tentatives: $tentatives, ')
          ..write('derniereErreur: $derniereErreur, ')
          ..write('envoyeLe: $envoyeLe')
          ..write(')'))
        .toString();
  }

  @override
  int get hashCode => Object.hash(
    id,
    utilisateurId,
    bailId,
    bailLibelle,
    locataireNom,
    montant,
    datePaiement,
    creeLe,
    statut,
    tentatives,
    derniereErreur,
    envoyeLe,
  );
  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      (other is EncaissementsEnAttenteData &&
          other.id == this.id &&
          other.utilisateurId == this.utilisateurId &&
          other.bailId == this.bailId &&
          other.bailLibelle == this.bailLibelle &&
          other.locataireNom == this.locataireNom &&
          other.montant == this.montant &&
          other.datePaiement == this.datePaiement &&
          other.creeLe == this.creeLe &&
          other.statut == this.statut &&
          other.tentatives == this.tentatives &&
          other.derniereErreur == this.derniereErreur &&
          other.envoyeLe == this.envoyeLe);
}

class EncaissementsEnAttenteCompanion
    extends UpdateCompanion<EncaissementsEnAttenteData> {
  final Value<String> id;
  final Value<String> utilisateurId;
  final Value<String> bailId;
  final Value<String> bailLibelle;
  final Value<String> locataireNom;
  final Value<int> montant;
  final Value<String> datePaiement;
  final Value<DateTime> creeLe;
  final Value<String> statut;
  final Value<int> tentatives;
  final Value<String?> derniereErreur;
  final Value<DateTime?> envoyeLe;
  final Value<int> rowid;
  const EncaissementsEnAttenteCompanion({
    this.id = const Value.absent(),
    this.utilisateurId = const Value.absent(),
    this.bailId = const Value.absent(),
    this.bailLibelle = const Value.absent(),
    this.locataireNom = const Value.absent(),
    this.montant = const Value.absent(),
    this.datePaiement = const Value.absent(),
    this.creeLe = const Value.absent(),
    this.statut = const Value.absent(),
    this.tentatives = const Value.absent(),
    this.derniereErreur = const Value.absent(),
    this.envoyeLe = const Value.absent(),
    this.rowid = const Value.absent(),
  });
  EncaissementsEnAttenteCompanion.insert({
    required String id,
    required String utilisateurId,
    required String bailId,
    required String bailLibelle,
    required String locataireNom,
    required int montant,
    required String datePaiement,
    required DateTime creeLe,
    this.statut = const Value.absent(),
    this.tentatives = const Value.absent(),
    this.derniereErreur = const Value.absent(),
    this.envoyeLe = const Value.absent(),
    this.rowid = const Value.absent(),
  }) : id = Value(id),
       utilisateurId = Value(utilisateurId),
       bailId = Value(bailId),
       bailLibelle = Value(bailLibelle),
       locataireNom = Value(locataireNom),
       montant = Value(montant),
       datePaiement = Value(datePaiement),
       creeLe = Value(creeLe);
  static Insertable<EncaissementsEnAttenteData> custom({
    Expression<String>? id,
    Expression<String>? utilisateurId,
    Expression<String>? bailId,
    Expression<String>? bailLibelle,
    Expression<String>? locataireNom,
    Expression<int>? montant,
    Expression<String>? datePaiement,
    Expression<DateTime>? creeLe,
    Expression<String>? statut,
    Expression<int>? tentatives,
    Expression<String>? derniereErreur,
    Expression<DateTime>? envoyeLe,
    Expression<int>? rowid,
  }) {
    return RawValuesInsertable({
      if (id != null) 'id': id,
      if (utilisateurId != null) 'utilisateur_id': utilisateurId,
      if (bailId != null) 'bail_id': bailId,
      if (bailLibelle != null) 'bail_libelle': bailLibelle,
      if (locataireNom != null) 'locataire_nom': locataireNom,
      if (montant != null) 'montant': montant,
      if (datePaiement != null) 'date_paiement': datePaiement,
      if (creeLe != null) 'cree_le': creeLe,
      if (statut != null) 'statut': statut,
      if (tentatives != null) 'tentatives': tentatives,
      if (derniereErreur != null) 'derniere_erreur': derniereErreur,
      if (envoyeLe != null) 'envoye_le': envoyeLe,
      if (rowid != null) 'rowid': rowid,
    });
  }

  EncaissementsEnAttenteCompanion copyWith({
    Value<String>? id,
    Value<String>? utilisateurId,
    Value<String>? bailId,
    Value<String>? bailLibelle,
    Value<String>? locataireNom,
    Value<int>? montant,
    Value<String>? datePaiement,
    Value<DateTime>? creeLe,
    Value<String>? statut,
    Value<int>? tentatives,
    Value<String?>? derniereErreur,
    Value<DateTime?>? envoyeLe,
    Value<int>? rowid,
  }) {
    return EncaissementsEnAttenteCompanion(
      id: id ?? this.id,
      utilisateurId: utilisateurId ?? this.utilisateurId,
      bailId: bailId ?? this.bailId,
      bailLibelle: bailLibelle ?? this.bailLibelle,
      locataireNom: locataireNom ?? this.locataireNom,
      montant: montant ?? this.montant,
      datePaiement: datePaiement ?? this.datePaiement,
      creeLe: creeLe ?? this.creeLe,
      statut: statut ?? this.statut,
      tentatives: tentatives ?? this.tentatives,
      derniereErreur: derniereErreur ?? this.derniereErreur,
      envoyeLe: envoyeLe ?? this.envoyeLe,
      rowid: rowid ?? this.rowid,
    );
  }

  @override
  Map<String, Expression> toColumns(bool nullToAbsent) {
    final map = <String, Expression>{};
    if (id.present) {
      map['id'] = Variable<String>(id.value);
    }
    if (utilisateurId.present) {
      map['utilisateur_id'] = Variable<String>(utilisateurId.value);
    }
    if (bailId.present) {
      map['bail_id'] = Variable<String>(bailId.value);
    }
    if (bailLibelle.present) {
      map['bail_libelle'] = Variable<String>(bailLibelle.value);
    }
    if (locataireNom.present) {
      map['locataire_nom'] = Variable<String>(locataireNom.value);
    }
    if (montant.present) {
      map['montant'] = Variable<int>(montant.value);
    }
    if (datePaiement.present) {
      map['date_paiement'] = Variable<String>(datePaiement.value);
    }
    if (creeLe.present) {
      map['cree_le'] = Variable<DateTime>(creeLe.value);
    }
    if (statut.present) {
      map['statut'] = Variable<String>(statut.value);
    }
    if (tentatives.present) {
      map['tentatives'] = Variable<int>(tentatives.value);
    }
    if (derniereErreur.present) {
      map['derniere_erreur'] = Variable<String>(derniereErreur.value);
    }
    if (envoyeLe.present) {
      map['envoye_le'] = Variable<DateTime>(envoyeLe.value);
    }
    if (rowid.present) {
      map['rowid'] = Variable<int>(rowid.value);
    }
    return map;
  }

  @override
  String toString() {
    return (StringBuffer('EncaissementsEnAttenteCompanion(')
          ..write('id: $id, ')
          ..write('utilisateurId: $utilisateurId, ')
          ..write('bailId: $bailId, ')
          ..write('bailLibelle: $bailLibelle, ')
          ..write('locataireNom: $locataireNom, ')
          ..write('montant: $montant, ')
          ..write('datePaiement: $datePaiement, ')
          ..write('creeLe: $creeLe, ')
          ..write('statut: $statut, ')
          ..write('tentatives: $tentatives, ')
          ..write('derniereErreur: $derniereErreur, ')
          ..write('envoyeLe: $envoyeLe, ')
          ..write('rowid: $rowid')
          ..write(')'))
        .toString();
  }
}

class $LocatairesCacheTable extends LocatairesCache
    with TableInfo<$LocatairesCacheTable, LocatairesCacheData> {
  @override
  final GeneratedDatabase attachedDatabase;
  final String? _alias;
  $LocatairesCacheTable(this.attachedDatabase, [this._alias]);
  static const VerificationMeta _idMeta = const VerificationMeta('id');
  @override
  late final GeneratedColumn<String> id = GeneratedColumn<String>(
    'id',
    aliasedName,
    false,
    type: DriftSqlType.string,
    requiredDuringInsert: true,
  );
  static const VerificationMeta _nomMeta = const VerificationMeta('nom');
  @override
  late final GeneratedColumn<String> nom = GeneratedColumn<String>(
    'nom',
    aliasedName,
    false,
    type: DriftSqlType.string,
    requiredDuringInsert: true,
  );
  static const VerificationMeta _telephoneMeta = const VerificationMeta(
    'telephone',
  );
  @override
  late final GeneratedColumn<String> telephone = GeneratedColumn<String>(
    'telephone',
    aliasedName,
    true,
    type: DriftSqlType.string,
    requiredDuringInsert: false,
  );
  @override
  List<GeneratedColumn> get $columns => [id, nom, telephone];
  @override
  String get aliasedName => _alias ?? actualTableName;
  @override
  String get actualTableName => $name;
  static const String $name = 'locataires_cache';
  @override
  VerificationContext validateIntegrity(
    Insertable<LocatairesCacheData> instance, {
    bool isInserting = false,
  }) {
    final context = VerificationContext();
    final data = instance.toColumns(true);
    if (data.containsKey('id')) {
      context.handle(_idMeta, id.isAcceptableOrUnknown(data['id']!, _idMeta));
    } else if (isInserting) {
      context.missing(_idMeta);
    }
    if (data.containsKey('nom')) {
      context.handle(
        _nomMeta,
        nom.isAcceptableOrUnknown(data['nom']!, _nomMeta),
      );
    } else if (isInserting) {
      context.missing(_nomMeta);
    }
    if (data.containsKey('telephone')) {
      context.handle(
        _telephoneMeta,
        telephone.isAcceptableOrUnknown(data['telephone']!, _telephoneMeta),
      );
    }
    return context;
  }

  @override
  Set<GeneratedColumn> get $primaryKey => {id};
  @override
  LocatairesCacheData map(Map<String, dynamic> data, {String? tablePrefix}) {
    final effectivePrefix = tablePrefix != null ? '$tablePrefix.' : '';
    return LocatairesCacheData(
      id: attachedDatabase.typeMapping.read(
        DriftSqlType.string,
        data['${effectivePrefix}id'],
      )!,
      nom: attachedDatabase.typeMapping.read(
        DriftSqlType.string,
        data['${effectivePrefix}nom'],
      )!,
      telephone: attachedDatabase.typeMapping.read(
        DriftSqlType.string,
        data['${effectivePrefix}telephone'],
      ),
    );
  }

  @override
  $LocatairesCacheTable createAlias(String alias) {
    return $LocatairesCacheTable(attachedDatabase, alias);
  }
}

class LocatairesCacheData extends DataClass
    implements Insertable<LocatairesCacheData> {
  final String id;
  final String nom;
  final String? telephone;
  const LocatairesCacheData({
    required this.id,
    required this.nom,
    this.telephone,
  });
  @override
  Map<String, Expression> toColumns(bool nullToAbsent) {
    final map = <String, Expression>{};
    map['id'] = Variable<String>(id);
    map['nom'] = Variable<String>(nom);
    if (!nullToAbsent || telephone != null) {
      map['telephone'] = Variable<String>(telephone);
    }
    return map;
  }

  LocatairesCacheCompanion toCompanion(bool nullToAbsent) {
    return LocatairesCacheCompanion(
      id: Value(id),
      nom: Value(nom),
      telephone: telephone == null && nullToAbsent
          ? const Value.absent()
          : Value(telephone),
    );
  }

  factory LocatairesCacheData.fromJson(
    Map<String, dynamic> json, {
    ValueSerializer? serializer,
  }) {
    serializer ??= driftRuntimeOptions.defaultSerializer;
    return LocatairesCacheData(
      id: serializer.fromJson<String>(json['id']),
      nom: serializer.fromJson<String>(json['nom']),
      telephone: serializer.fromJson<String?>(json['telephone']),
    );
  }
  @override
  Map<String, dynamic> toJson({ValueSerializer? serializer}) {
    serializer ??= driftRuntimeOptions.defaultSerializer;
    return <String, dynamic>{
      'id': serializer.toJson<String>(id),
      'nom': serializer.toJson<String>(nom),
      'telephone': serializer.toJson<String?>(telephone),
    };
  }

  LocatairesCacheData copyWith({
    String? id,
    String? nom,
    Value<String?> telephone = const Value.absent(),
  }) => LocatairesCacheData(
    id: id ?? this.id,
    nom: nom ?? this.nom,
    telephone: telephone.present ? telephone.value : this.telephone,
  );
  LocatairesCacheData copyWithCompanion(LocatairesCacheCompanion data) {
    return LocatairesCacheData(
      id: data.id.present ? data.id.value : this.id,
      nom: data.nom.present ? data.nom.value : this.nom,
      telephone: data.telephone.present ? data.telephone.value : this.telephone,
    );
  }

  @override
  String toString() {
    return (StringBuffer('LocatairesCacheData(')
          ..write('id: $id, ')
          ..write('nom: $nom, ')
          ..write('telephone: $telephone')
          ..write(')'))
        .toString();
  }

  @override
  int get hashCode => Object.hash(id, nom, telephone);
  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      (other is LocatairesCacheData &&
          other.id == this.id &&
          other.nom == this.nom &&
          other.telephone == this.telephone);
}

class LocatairesCacheCompanion extends UpdateCompanion<LocatairesCacheData> {
  final Value<String> id;
  final Value<String> nom;
  final Value<String?> telephone;
  final Value<int> rowid;
  const LocatairesCacheCompanion({
    this.id = const Value.absent(),
    this.nom = const Value.absent(),
    this.telephone = const Value.absent(),
    this.rowid = const Value.absent(),
  });
  LocatairesCacheCompanion.insert({
    required String id,
    required String nom,
    this.telephone = const Value.absent(),
    this.rowid = const Value.absent(),
  }) : id = Value(id),
       nom = Value(nom);
  static Insertable<LocatairesCacheData> custom({
    Expression<String>? id,
    Expression<String>? nom,
    Expression<String>? telephone,
    Expression<int>? rowid,
  }) {
    return RawValuesInsertable({
      if (id != null) 'id': id,
      if (nom != null) 'nom': nom,
      if (telephone != null) 'telephone': telephone,
      if (rowid != null) 'rowid': rowid,
    });
  }

  LocatairesCacheCompanion copyWith({
    Value<String>? id,
    Value<String>? nom,
    Value<String?>? telephone,
    Value<int>? rowid,
  }) {
    return LocatairesCacheCompanion(
      id: id ?? this.id,
      nom: nom ?? this.nom,
      telephone: telephone ?? this.telephone,
      rowid: rowid ?? this.rowid,
    );
  }

  @override
  Map<String, Expression> toColumns(bool nullToAbsent) {
    final map = <String, Expression>{};
    if (id.present) {
      map['id'] = Variable<String>(id.value);
    }
    if (nom.present) {
      map['nom'] = Variable<String>(nom.value);
    }
    if (telephone.present) {
      map['telephone'] = Variable<String>(telephone.value);
    }
    if (rowid.present) {
      map['rowid'] = Variable<int>(rowid.value);
    }
    return map;
  }

  @override
  String toString() {
    return (StringBuffer('LocatairesCacheCompanion(')
          ..write('id: $id, ')
          ..write('nom: $nom, ')
          ..write('telephone: $telephone, ')
          ..write('rowid: $rowid')
          ..write(')'))
        .toString();
  }
}

class $BauxCacheTable extends BauxCache
    with TableInfo<$BauxCacheTable, BauxCacheData> {
  @override
  final GeneratedDatabase attachedDatabase;
  final String? _alias;
  $BauxCacheTable(this.attachedDatabase, [this._alias]);
  static const VerificationMeta _idMeta = const VerificationMeta('id');
  @override
  late final GeneratedColumn<String> id = GeneratedColumn<String>(
    'id',
    aliasedName,
    false,
    type: DriftSqlType.string,
    requiredDuringInsert: true,
  );
  static const VerificationMeta _numeroMeta = const VerificationMeta('numero');
  @override
  late final GeneratedColumn<String> numero = GeneratedColumn<String>(
    'numero',
    aliasedName,
    false,
    type: DriftSqlType.string,
    requiredDuringInsert: true,
  );
  static const VerificationMeta _locataireIdMeta = const VerificationMeta(
    'locataireId',
  );
  @override
  late final GeneratedColumn<String> locataireId = GeneratedColumn<String>(
    'locataire_id',
    aliasedName,
    false,
    type: DriftSqlType.string,
    requiredDuringInsert: true,
  );
  static const VerificationMeta _libelleMeta = const VerificationMeta(
    'libelle',
  );
  @override
  late final GeneratedColumn<String> libelle = GeneratedColumn<String>(
    'libelle',
    aliasedName,
    false,
    type: DriftSqlType.string,
    requiredDuringInsert: true,
  );
  static const VerificationMeta _statutMeta = const VerificationMeta('statut');
  @override
  late final GeneratedColumn<String> statut = GeneratedColumn<String>(
    'statut',
    aliasedName,
    false,
    type: DriftSqlType.string,
    requiredDuringInsert: true,
  );
  static const VerificationMeta _montantMensuelMeta = const VerificationMeta(
    'montantMensuel',
  );
  @override
  late final GeneratedColumn<int> montantMensuel = GeneratedColumn<int>(
    'montant_mensuel',
    aliasedName,
    false,
    type: DriftSqlType.int,
    requiredDuringInsert: true,
  );
  @override
  List<GeneratedColumn> get $columns => [
    id,
    numero,
    locataireId,
    libelle,
    statut,
    montantMensuel,
  ];
  @override
  String get aliasedName => _alias ?? actualTableName;
  @override
  String get actualTableName => $name;
  static const String $name = 'baux_cache';
  @override
  VerificationContext validateIntegrity(
    Insertable<BauxCacheData> instance, {
    bool isInserting = false,
  }) {
    final context = VerificationContext();
    final data = instance.toColumns(true);
    if (data.containsKey('id')) {
      context.handle(_idMeta, id.isAcceptableOrUnknown(data['id']!, _idMeta));
    } else if (isInserting) {
      context.missing(_idMeta);
    }
    if (data.containsKey('numero')) {
      context.handle(
        _numeroMeta,
        numero.isAcceptableOrUnknown(data['numero']!, _numeroMeta),
      );
    } else if (isInserting) {
      context.missing(_numeroMeta);
    }
    if (data.containsKey('locataire_id')) {
      context.handle(
        _locataireIdMeta,
        locataireId.isAcceptableOrUnknown(
          data['locataire_id']!,
          _locataireIdMeta,
        ),
      );
    } else if (isInserting) {
      context.missing(_locataireIdMeta);
    }
    if (data.containsKey('libelle')) {
      context.handle(
        _libelleMeta,
        libelle.isAcceptableOrUnknown(data['libelle']!, _libelleMeta),
      );
    } else if (isInserting) {
      context.missing(_libelleMeta);
    }
    if (data.containsKey('statut')) {
      context.handle(
        _statutMeta,
        statut.isAcceptableOrUnknown(data['statut']!, _statutMeta),
      );
    } else if (isInserting) {
      context.missing(_statutMeta);
    }
    if (data.containsKey('montant_mensuel')) {
      context.handle(
        _montantMensuelMeta,
        montantMensuel.isAcceptableOrUnknown(
          data['montant_mensuel']!,
          _montantMensuelMeta,
        ),
      );
    } else if (isInserting) {
      context.missing(_montantMensuelMeta);
    }
    return context;
  }

  @override
  Set<GeneratedColumn> get $primaryKey => {id};
  @override
  BauxCacheData map(Map<String, dynamic> data, {String? tablePrefix}) {
    final effectivePrefix = tablePrefix != null ? '$tablePrefix.' : '';
    return BauxCacheData(
      id: attachedDatabase.typeMapping.read(
        DriftSqlType.string,
        data['${effectivePrefix}id'],
      )!,
      numero: attachedDatabase.typeMapping.read(
        DriftSqlType.string,
        data['${effectivePrefix}numero'],
      )!,
      locataireId: attachedDatabase.typeMapping.read(
        DriftSqlType.string,
        data['${effectivePrefix}locataire_id'],
      )!,
      libelle: attachedDatabase.typeMapping.read(
        DriftSqlType.string,
        data['${effectivePrefix}libelle'],
      )!,
      statut: attachedDatabase.typeMapping.read(
        DriftSqlType.string,
        data['${effectivePrefix}statut'],
      )!,
      montantMensuel: attachedDatabase.typeMapping.read(
        DriftSqlType.int,
        data['${effectivePrefix}montant_mensuel'],
      )!,
    );
  }

  @override
  $BauxCacheTable createAlias(String alias) {
    return $BauxCacheTable(attachedDatabase, alias);
  }
}

class BauxCacheData extends DataClass implements Insertable<BauxCacheData> {
  final String id;
  final String numero;
  final String locataireId;
  final String libelle;
  final String statut;

  /// Loyer + charges mensuels (FCFA).
  final int montantMensuel;
  const BauxCacheData({
    required this.id,
    required this.numero,
    required this.locataireId,
    required this.libelle,
    required this.statut,
    required this.montantMensuel,
  });
  @override
  Map<String, Expression> toColumns(bool nullToAbsent) {
    final map = <String, Expression>{};
    map['id'] = Variable<String>(id);
    map['numero'] = Variable<String>(numero);
    map['locataire_id'] = Variable<String>(locataireId);
    map['libelle'] = Variable<String>(libelle);
    map['statut'] = Variable<String>(statut);
    map['montant_mensuel'] = Variable<int>(montantMensuel);
    return map;
  }

  BauxCacheCompanion toCompanion(bool nullToAbsent) {
    return BauxCacheCompanion(
      id: Value(id),
      numero: Value(numero),
      locataireId: Value(locataireId),
      libelle: Value(libelle),
      statut: Value(statut),
      montantMensuel: Value(montantMensuel),
    );
  }

  factory BauxCacheData.fromJson(
    Map<String, dynamic> json, {
    ValueSerializer? serializer,
  }) {
    serializer ??= driftRuntimeOptions.defaultSerializer;
    return BauxCacheData(
      id: serializer.fromJson<String>(json['id']),
      numero: serializer.fromJson<String>(json['numero']),
      locataireId: serializer.fromJson<String>(json['locataireId']),
      libelle: serializer.fromJson<String>(json['libelle']),
      statut: serializer.fromJson<String>(json['statut']),
      montantMensuel: serializer.fromJson<int>(json['montantMensuel']),
    );
  }
  @override
  Map<String, dynamic> toJson({ValueSerializer? serializer}) {
    serializer ??= driftRuntimeOptions.defaultSerializer;
    return <String, dynamic>{
      'id': serializer.toJson<String>(id),
      'numero': serializer.toJson<String>(numero),
      'locataireId': serializer.toJson<String>(locataireId),
      'libelle': serializer.toJson<String>(libelle),
      'statut': serializer.toJson<String>(statut),
      'montantMensuel': serializer.toJson<int>(montantMensuel),
    };
  }

  BauxCacheData copyWith({
    String? id,
    String? numero,
    String? locataireId,
    String? libelle,
    String? statut,
    int? montantMensuel,
  }) => BauxCacheData(
    id: id ?? this.id,
    numero: numero ?? this.numero,
    locataireId: locataireId ?? this.locataireId,
    libelle: libelle ?? this.libelle,
    statut: statut ?? this.statut,
    montantMensuel: montantMensuel ?? this.montantMensuel,
  );
  BauxCacheData copyWithCompanion(BauxCacheCompanion data) {
    return BauxCacheData(
      id: data.id.present ? data.id.value : this.id,
      numero: data.numero.present ? data.numero.value : this.numero,
      locataireId: data.locataireId.present
          ? data.locataireId.value
          : this.locataireId,
      libelle: data.libelle.present ? data.libelle.value : this.libelle,
      statut: data.statut.present ? data.statut.value : this.statut,
      montantMensuel: data.montantMensuel.present
          ? data.montantMensuel.value
          : this.montantMensuel,
    );
  }

  @override
  String toString() {
    return (StringBuffer('BauxCacheData(')
          ..write('id: $id, ')
          ..write('numero: $numero, ')
          ..write('locataireId: $locataireId, ')
          ..write('libelle: $libelle, ')
          ..write('statut: $statut, ')
          ..write('montantMensuel: $montantMensuel')
          ..write(')'))
        .toString();
  }

  @override
  int get hashCode =>
      Object.hash(id, numero, locataireId, libelle, statut, montantMensuel);
  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      (other is BauxCacheData &&
          other.id == this.id &&
          other.numero == this.numero &&
          other.locataireId == this.locataireId &&
          other.libelle == this.libelle &&
          other.statut == this.statut &&
          other.montantMensuel == this.montantMensuel);
}

class BauxCacheCompanion extends UpdateCompanion<BauxCacheData> {
  final Value<String> id;
  final Value<String> numero;
  final Value<String> locataireId;
  final Value<String> libelle;
  final Value<String> statut;
  final Value<int> montantMensuel;
  final Value<int> rowid;
  const BauxCacheCompanion({
    this.id = const Value.absent(),
    this.numero = const Value.absent(),
    this.locataireId = const Value.absent(),
    this.libelle = const Value.absent(),
    this.statut = const Value.absent(),
    this.montantMensuel = const Value.absent(),
    this.rowid = const Value.absent(),
  });
  BauxCacheCompanion.insert({
    required String id,
    required String numero,
    required String locataireId,
    required String libelle,
    required String statut,
    required int montantMensuel,
    this.rowid = const Value.absent(),
  }) : id = Value(id),
       numero = Value(numero),
       locataireId = Value(locataireId),
       libelle = Value(libelle),
       statut = Value(statut),
       montantMensuel = Value(montantMensuel);
  static Insertable<BauxCacheData> custom({
    Expression<String>? id,
    Expression<String>? numero,
    Expression<String>? locataireId,
    Expression<String>? libelle,
    Expression<String>? statut,
    Expression<int>? montantMensuel,
    Expression<int>? rowid,
  }) {
    return RawValuesInsertable({
      if (id != null) 'id': id,
      if (numero != null) 'numero': numero,
      if (locataireId != null) 'locataire_id': locataireId,
      if (libelle != null) 'libelle': libelle,
      if (statut != null) 'statut': statut,
      if (montantMensuel != null) 'montant_mensuel': montantMensuel,
      if (rowid != null) 'rowid': rowid,
    });
  }

  BauxCacheCompanion copyWith({
    Value<String>? id,
    Value<String>? numero,
    Value<String>? locataireId,
    Value<String>? libelle,
    Value<String>? statut,
    Value<int>? montantMensuel,
    Value<int>? rowid,
  }) {
    return BauxCacheCompanion(
      id: id ?? this.id,
      numero: numero ?? this.numero,
      locataireId: locataireId ?? this.locataireId,
      libelle: libelle ?? this.libelle,
      statut: statut ?? this.statut,
      montantMensuel: montantMensuel ?? this.montantMensuel,
      rowid: rowid ?? this.rowid,
    );
  }

  @override
  Map<String, Expression> toColumns(bool nullToAbsent) {
    final map = <String, Expression>{};
    if (id.present) {
      map['id'] = Variable<String>(id.value);
    }
    if (numero.present) {
      map['numero'] = Variable<String>(numero.value);
    }
    if (locataireId.present) {
      map['locataire_id'] = Variable<String>(locataireId.value);
    }
    if (libelle.present) {
      map['libelle'] = Variable<String>(libelle.value);
    }
    if (statut.present) {
      map['statut'] = Variable<String>(statut.value);
    }
    if (montantMensuel.present) {
      map['montant_mensuel'] = Variable<int>(montantMensuel.value);
    }
    if (rowid.present) {
      map['rowid'] = Variable<int>(rowid.value);
    }
    return map;
  }

  @override
  String toString() {
    return (StringBuffer('BauxCacheCompanion(')
          ..write('id: $id, ')
          ..write('numero: $numero, ')
          ..write('locataireId: $locataireId, ')
          ..write('libelle: $libelle, ')
          ..write('statut: $statut, ')
          ..write('montantMensuel: $montantMensuel, ')
          ..write('rowid: $rowid')
          ..write(')'))
        .toString();
  }
}

class $LotsCacheTable extends LotsCache
    with TableInfo<$LotsCacheTable, LotsCacheData> {
  @override
  final GeneratedDatabase attachedDatabase;
  final String? _alias;
  $LotsCacheTable(this.attachedDatabase, [this._alias]);
  static const VerificationMeta _idMeta = const VerificationMeta('id');
  @override
  late final GeneratedColumn<String> id = GeneratedColumn<String>(
    'id',
    aliasedName,
    false,
    type: DriftSqlType.string,
    requiredDuringInsert: true,
  );
  static const VerificationMeta _referenceMeta = const VerificationMeta(
    'reference',
  );
  @override
  late final GeneratedColumn<String> reference = GeneratedColumn<String>(
    'reference',
    aliasedName,
    false,
    type: DriftSqlType.string,
    requiredDuringInsert: true,
  );
  static const VerificationMeta _immeubleNomMeta = const VerificationMeta(
    'immeubleNom',
  );
  @override
  late final GeneratedColumn<String> immeubleNom = GeneratedColumn<String>(
    'immeuble_nom',
    aliasedName,
    false,
    type: DriftSqlType.string,
    requiredDuringInsert: true,
  );
  static const VerificationMeta _statutMeta = const VerificationMeta('statut');
  @override
  late final GeneratedColumn<String> statut = GeneratedColumn<String>(
    'statut',
    aliasedName,
    false,
    type: DriftSqlType.string,
    requiredDuringInsert: true,
  );
  @override
  List<GeneratedColumn> get $columns => [id, reference, immeubleNom, statut];
  @override
  String get aliasedName => _alias ?? actualTableName;
  @override
  String get actualTableName => $name;
  static const String $name = 'lots_cache';
  @override
  VerificationContext validateIntegrity(
    Insertable<LotsCacheData> instance, {
    bool isInserting = false,
  }) {
    final context = VerificationContext();
    final data = instance.toColumns(true);
    if (data.containsKey('id')) {
      context.handle(_idMeta, id.isAcceptableOrUnknown(data['id']!, _idMeta));
    } else if (isInserting) {
      context.missing(_idMeta);
    }
    if (data.containsKey('reference')) {
      context.handle(
        _referenceMeta,
        reference.isAcceptableOrUnknown(data['reference']!, _referenceMeta),
      );
    } else if (isInserting) {
      context.missing(_referenceMeta);
    }
    if (data.containsKey('immeuble_nom')) {
      context.handle(
        _immeubleNomMeta,
        immeubleNom.isAcceptableOrUnknown(
          data['immeuble_nom']!,
          _immeubleNomMeta,
        ),
      );
    } else if (isInserting) {
      context.missing(_immeubleNomMeta);
    }
    if (data.containsKey('statut')) {
      context.handle(
        _statutMeta,
        statut.isAcceptableOrUnknown(data['statut']!, _statutMeta),
      );
    } else if (isInserting) {
      context.missing(_statutMeta);
    }
    return context;
  }

  @override
  Set<GeneratedColumn> get $primaryKey => {id};
  @override
  LotsCacheData map(Map<String, dynamic> data, {String? tablePrefix}) {
    final effectivePrefix = tablePrefix != null ? '$tablePrefix.' : '';
    return LotsCacheData(
      id: attachedDatabase.typeMapping.read(
        DriftSqlType.string,
        data['${effectivePrefix}id'],
      )!,
      reference: attachedDatabase.typeMapping.read(
        DriftSqlType.string,
        data['${effectivePrefix}reference'],
      )!,
      immeubleNom: attachedDatabase.typeMapping.read(
        DriftSqlType.string,
        data['${effectivePrefix}immeuble_nom'],
      )!,
      statut: attachedDatabase.typeMapping.read(
        DriftSqlType.string,
        data['${effectivePrefix}statut'],
      )!,
    );
  }

  @override
  $LotsCacheTable createAlias(String alias) {
    return $LotsCacheTable(attachedDatabase, alias);
  }
}

class LotsCacheData extends DataClass implements Insertable<LotsCacheData> {
  final String id;
  final String reference;
  final String immeubleNom;
  final String statut;
  const LotsCacheData({
    required this.id,
    required this.reference,
    required this.immeubleNom,
    required this.statut,
  });
  @override
  Map<String, Expression> toColumns(bool nullToAbsent) {
    final map = <String, Expression>{};
    map['id'] = Variable<String>(id);
    map['reference'] = Variable<String>(reference);
    map['immeuble_nom'] = Variable<String>(immeubleNom);
    map['statut'] = Variable<String>(statut);
    return map;
  }

  LotsCacheCompanion toCompanion(bool nullToAbsent) {
    return LotsCacheCompanion(
      id: Value(id),
      reference: Value(reference),
      immeubleNom: Value(immeubleNom),
      statut: Value(statut),
    );
  }

  factory LotsCacheData.fromJson(
    Map<String, dynamic> json, {
    ValueSerializer? serializer,
  }) {
    serializer ??= driftRuntimeOptions.defaultSerializer;
    return LotsCacheData(
      id: serializer.fromJson<String>(json['id']),
      reference: serializer.fromJson<String>(json['reference']),
      immeubleNom: serializer.fromJson<String>(json['immeubleNom']),
      statut: serializer.fromJson<String>(json['statut']),
    );
  }
  @override
  Map<String, dynamic> toJson({ValueSerializer? serializer}) {
    serializer ??= driftRuntimeOptions.defaultSerializer;
    return <String, dynamic>{
      'id': serializer.toJson<String>(id),
      'reference': serializer.toJson<String>(reference),
      'immeubleNom': serializer.toJson<String>(immeubleNom),
      'statut': serializer.toJson<String>(statut),
    };
  }

  LotsCacheData copyWith({
    String? id,
    String? reference,
    String? immeubleNom,
    String? statut,
  }) => LotsCacheData(
    id: id ?? this.id,
    reference: reference ?? this.reference,
    immeubleNom: immeubleNom ?? this.immeubleNom,
    statut: statut ?? this.statut,
  );
  LotsCacheData copyWithCompanion(LotsCacheCompanion data) {
    return LotsCacheData(
      id: data.id.present ? data.id.value : this.id,
      reference: data.reference.present ? data.reference.value : this.reference,
      immeubleNom: data.immeubleNom.present
          ? data.immeubleNom.value
          : this.immeubleNom,
      statut: data.statut.present ? data.statut.value : this.statut,
    );
  }

  @override
  String toString() {
    return (StringBuffer('LotsCacheData(')
          ..write('id: $id, ')
          ..write('reference: $reference, ')
          ..write('immeubleNom: $immeubleNom, ')
          ..write('statut: $statut')
          ..write(')'))
        .toString();
  }

  @override
  int get hashCode => Object.hash(id, reference, immeubleNom, statut);
  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      (other is LotsCacheData &&
          other.id == this.id &&
          other.reference == this.reference &&
          other.immeubleNom == this.immeubleNom &&
          other.statut == this.statut);
}

class LotsCacheCompanion extends UpdateCompanion<LotsCacheData> {
  final Value<String> id;
  final Value<String> reference;
  final Value<String> immeubleNom;
  final Value<String> statut;
  final Value<int> rowid;
  const LotsCacheCompanion({
    this.id = const Value.absent(),
    this.reference = const Value.absent(),
    this.immeubleNom = const Value.absent(),
    this.statut = const Value.absent(),
    this.rowid = const Value.absent(),
  });
  LotsCacheCompanion.insert({
    required String id,
    required String reference,
    required String immeubleNom,
    required String statut,
    this.rowid = const Value.absent(),
  }) : id = Value(id),
       reference = Value(reference),
       immeubleNom = Value(immeubleNom),
       statut = Value(statut);
  static Insertable<LotsCacheData> custom({
    Expression<String>? id,
    Expression<String>? reference,
    Expression<String>? immeubleNom,
    Expression<String>? statut,
    Expression<int>? rowid,
  }) {
    return RawValuesInsertable({
      if (id != null) 'id': id,
      if (reference != null) 'reference': reference,
      if (immeubleNom != null) 'immeuble_nom': immeubleNom,
      if (statut != null) 'statut': statut,
      if (rowid != null) 'rowid': rowid,
    });
  }

  LotsCacheCompanion copyWith({
    Value<String>? id,
    Value<String>? reference,
    Value<String>? immeubleNom,
    Value<String>? statut,
    Value<int>? rowid,
  }) {
    return LotsCacheCompanion(
      id: id ?? this.id,
      reference: reference ?? this.reference,
      immeubleNom: immeubleNom ?? this.immeubleNom,
      statut: statut ?? this.statut,
      rowid: rowid ?? this.rowid,
    );
  }

  @override
  Map<String, Expression> toColumns(bool nullToAbsent) {
    final map = <String, Expression>{};
    if (id.present) {
      map['id'] = Variable<String>(id.value);
    }
    if (reference.present) {
      map['reference'] = Variable<String>(reference.value);
    }
    if (immeubleNom.present) {
      map['immeuble_nom'] = Variable<String>(immeubleNom.value);
    }
    if (statut.present) {
      map['statut'] = Variable<String>(statut.value);
    }
    if (rowid.present) {
      map['rowid'] = Variable<int>(rowid.value);
    }
    return map;
  }

  @override
  String toString() {
    return (StringBuffer('LotsCacheCompanion(')
          ..write('id: $id, ')
          ..write('reference: $reference, ')
          ..write('immeubleNom: $immeubleNom, ')
          ..write('statut: $statut, ')
          ..write('rowid: $rowid')
          ..write(')'))
        .toString();
  }
}

class $MetaLocaleTable extends MetaLocale
    with TableInfo<$MetaLocaleTable, MetaLocaleData> {
  @override
  final GeneratedDatabase attachedDatabase;
  final String? _alias;
  $MetaLocaleTable(this.attachedDatabase, [this._alias]);
  static const VerificationMeta _cleMeta = const VerificationMeta('cle');
  @override
  late final GeneratedColumn<String> cle = GeneratedColumn<String>(
    'cle',
    aliasedName,
    false,
    type: DriftSqlType.string,
    requiredDuringInsert: true,
  );
  static const VerificationMeta _valeurMeta = const VerificationMeta('valeur');
  @override
  late final GeneratedColumn<String> valeur = GeneratedColumn<String>(
    'valeur',
    aliasedName,
    false,
    type: DriftSqlType.string,
    requiredDuringInsert: true,
  );
  @override
  List<GeneratedColumn> get $columns => [cle, valeur];
  @override
  String get aliasedName => _alias ?? actualTableName;
  @override
  String get actualTableName => $name;
  static const String $name = 'meta_locale';
  @override
  VerificationContext validateIntegrity(
    Insertable<MetaLocaleData> instance, {
    bool isInserting = false,
  }) {
    final context = VerificationContext();
    final data = instance.toColumns(true);
    if (data.containsKey('cle')) {
      context.handle(
        _cleMeta,
        cle.isAcceptableOrUnknown(data['cle']!, _cleMeta),
      );
    } else if (isInserting) {
      context.missing(_cleMeta);
    }
    if (data.containsKey('valeur')) {
      context.handle(
        _valeurMeta,
        valeur.isAcceptableOrUnknown(data['valeur']!, _valeurMeta),
      );
    } else if (isInserting) {
      context.missing(_valeurMeta);
    }
    return context;
  }

  @override
  Set<GeneratedColumn> get $primaryKey => {cle};
  @override
  MetaLocaleData map(Map<String, dynamic> data, {String? tablePrefix}) {
    final effectivePrefix = tablePrefix != null ? '$tablePrefix.' : '';
    return MetaLocaleData(
      cle: attachedDatabase.typeMapping.read(
        DriftSqlType.string,
        data['${effectivePrefix}cle'],
      )!,
      valeur: attachedDatabase.typeMapping.read(
        DriftSqlType.string,
        data['${effectivePrefix}valeur'],
      )!,
    );
  }

  @override
  $MetaLocaleTable createAlias(String alias) {
    return $MetaLocaleTable(attachedDatabase, alias);
  }
}

class MetaLocaleData extends DataClass implements Insertable<MetaLocaleData> {
  final String cle;
  final String valeur;
  const MetaLocaleData({required this.cle, required this.valeur});
  @override
  Map<String, Expression> toColumns(bool nullToAbsent) {
    final map = <String, Expression>{};
    map['cle'] = Variable<String>(cle);
    map['valeur'] = Variable<String>(valeur);
    return map;
  }

  MetaLocaleCompanion toCompanion(bool nullToAbsent) {
    return MetaLocaleCompanion(cle: Value(cle), valeur: Value(valeur));
  }

  factory MetaLocaleData.fromJson(
    Map<String, dynamic> json, {
    ValueSerializer? serializer,
  }) {
    serializer ??= driftRuntimeOptions.defaultSerializer;
    return MetaLocaleData(
      cle: serializer.fromJson<String>(json['cle']),
      valeur: serializer.fromJson<String>(json['valeur']),
    );
  }
  @override
  Map<String, dynamic> toJson({ValueSerializer? serializer}) {
    serializer ??= driftRuntimeOptions.defaultSerializer;
    return <String, dynamic>{
      'cle': serializer.toJson<String>(cle),
      'valeur': serializer.toJson<String>(valeur),
    };
  }

  MetaLocaleData copyWith({String? cle, String? valeur}) =>
      MetaLocaleData(cle: cle ?? this.cle, valeur: valeur ?? this.valeur);
  MetaLocaleData copyWithCompanion(MetaLocaleCompanion data) {
    return MetaLocaleData(
      cle: data.cle.present ? data.cle.value : this.cle,
      valeur: data.valeur.present ? data.valeur.value : this.valeur,
    );
  }

  @override
  String toString() {
    return (StringBuffer('MetaLocaleData(')
          ..write('cle: $cle, ')
          ..write('valeur: $valeur')
          ..write(')'))
        .toString();
  }

  @override
  int get hashCode => Object.hash(cle, valeur);
  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      (other is MetaLocaleData &&
          other.cle == this.cle &&
          other.valeur == this.valeur);
}

class MetaLocaleCompanion extends UpdateCompanion<MetaLocaleData> {
  final Value<String> cle;
  final Value<String> valeur;
  final Value<int> rowid;
  const MetaLocaleCompanion({
    this.cle = const Value.absent(),
    this.valeur = const Value.absent(),
    this.rowid = const Value.absent(),
  });
  MetaLocaleCompanion.insert({
    required String cle,
    required String valeur,
    this.rowid = const Value.absent(),
  }) : cle = Value(cle),
       valeur = Value(valeur);
  static Insertable<MetaLocaleData> custom({
    Expression<String>? cle,
    Expression<String>? valeur,
    Expression<int>? rowid,
  }) {
    return RawValuesInsertable({
      if (cle != null) 'cle': cle,
      if (valeur != null) 'valeur': valeur,
      if (rowid != null) 'rowid': rowid,
    });
  }

  MetaLocaleCompanion copyWith({
    Value<String>? cle,
    Value<String>? valeur,
    Value<int>? rowid,
  }) {
    return MetaLocaleCompanion(
      cle: cle ?? this.cle,
      valeur: valeur ?? this.valeur,
      rowid: rowid ?? this.rowid,
    );
  }

  @override
  Map<String, Expression> toColumns(bool nullToAbsent) {
    final map = <String, Expression>{};
    if (cle.present) {
      map['cle'] = Variable<String>(cle.value);
    }
    if (valeur.present) {
      map['valeur'] = Variable<String>(valeur.value);
    }
    if (rowid.present) {
      map['rowid'] = Variable<int>(rowid.value);
    }
    return map;
  }

  @override
  String toString() {
    return (StringBuffer('MetaLocaleCompanion(')
          ..write('cle: $cle, ')
          ..write('valeur: $valeur, ')
          ..write('rowid: $rowid')
          ..write(')'))
        .toString();
  }
}

abstract class _$AppDatabase extends GeneratedDatabase {
  _$AppDatabase(QueryExecutor e) : super(e);
  $AppDatabaseManager get managers => $AppDatabaseManager(this);
  late final $EncaissementsEnAttenteTable encaissementsEnAttente =
      $EncaissementsEnAttenteTable(this);
  late final $LocatairesCacheTable locatairesCache = $LocatairesCacheTable(
    this,
  );
  late final $BauxCacheTable bauxCache = $BauxCacheTable(this);
  late final $LotsCacheTable lotsCache = $LotsCacheTable(this);
  late final $MetaLocaleTable metaLocale = $MetaLocaleTable(this);
  @override
  Iterable<TableInfo<Table, Object?>> get allTables =>
      allSchemaEntities.whereType<TableInfo<Table, Object?>>();
  @override
  List<DatabaseSchemaEntity> get allSchemaEntities => [
    encaissementsEnAttente,
    locatairesCache,
    bauxCache,
    lotsCache,
    metaLocale,
  ];
}

typedef $$EncaissementsEnAttenteTableCreateCompanionBuilder =
    EncaissementsEnAttenteCompanion Function({
      required String id,
      required String utilisateurId,
      required String bailId,
      required String bailLibelle,
      required String locataireNom,
      required int montant,
      required String datePaiement,
      required DateTime creeLe,
      Value<String> statut,
      Value<int> tentatives,
      Value<String?> derniereErreur,
      Value<DateTime?> envoyeLe,
      Value<int> rowid,
    });
typedef $$EncaissementsEnAttenteTableUpdateCompanionBuilder =
    EncaissementsEnAttenteCompanion Function({
      Value<String> id,
      Value<String> utilisateurId,
      Value<String> bailId,
      Value<String> bailLibelle,
      Value<String> locataireNom,
      Value<int> montant,
      Value<String> datePaiement,
      Value<DateTime> creeLe,
      Value<String> statut,
      Value<int> tentatives,
      Value<String?> derniereErreur,
      Value<DateTime?> envoyeLe,
      Value<int> rowid,
    });

class $$EncaissementsEnAttenteTableFilterComposer
    extends Composer<_$AppDatabase, $EncaissementsEnAttenteTable> {
  $$EncaissementsEnAttenteTableFilterComposer({
    required super.$db,
    required super.$table,
    super.joinBuilder,
    super.$addJoinBuilderToRootComposer,
    super.$removeJoinBuilderFromRootComposer,
  });
  ColumnFilters<String> get id => $composableBuilder(
    column: $table.id,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<String> get utilisateurId => $composableBuilder(
    column: $table.utilisateurId,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<String> get bailId => $composableBuilder(
    column: $table.bailId,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<String> get bailLibelle => $composableBuilder(
    column: $table.bailLibelle,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<String> get locataireNom => $composableBuilder(
    column: $table.locataireNom,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<int> get montant => $composableBuilder(
    column: $table.montant,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<String> get datePaiement => $composableBuilder(
    column: $table.datePaiement,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<DateTime> get creeLe => $composableBuilder(
    column: $table.creeLe,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<String> get statut => $composableBuilder(
    column: $table.statut,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<int> get tentatives => $composableBuilder(
    column: $table.tentatives,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<String> get derniereErreur => $composableBuilder(
    column: $table.derniereErreur,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<DateTime> get envoyeLe => $composableBuilder(
    column: $table.envoyeLe,
    builder: (column) => ColumnFilters(column),
  );
}

class $$EncaissementsEnAttenteTableOrderingComposer
    extends Composer<_$AppDatabase, $EncaissementsEnAttenteTable> {
  $$EncaissementsEnAttenteTableOrderingComposer({
    required super.$db,
    required super.$table,
    super.joinBuilder,
    super.$addJoinBuilderToRootComposer,
    super.$removeJoinBuilderFromRootComposer,
  });
  ColumnOrderings<String> get id => $composableBuilder(
    column: $table.id,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<String> get utilisateurId => $composableBuilder(
    column: $table.utilisateurId,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<String> get bailId => $composableBuilder(
    column: $table.bailId,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<String> get bailLibelle => $composableBuilder(
    column: $table.bailLibelle,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<String> get locataireNom => $composableBuilder(
    column: $table.locataireNom,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<int> get montant => $composableBuilder(
    column: $table.montant,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<String> get datePaiement => $composableBuilder(
    column: $table.datePaiement,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<DateTime> get creeLe => $composableBuilder(
    column: $table.creeLe,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<String> get statut => $composableBuilder(
    column: $table.statut,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<int> get tentatives => $composableBuilder(
    column: $table.tentatives,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<String> get derniereErreur => $composableBuilder(
    column: $table.derniereErreur,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<DateTime> get envoyeLe => $composableBuilder(
    column: $table.envoyeLe,
    builder: (column) => ColumnOrderings(column),
  );
}

class $$EncaissementsEnAttenteTableAnnotationComposer
    extends Composer<_$AppDatabase, $EncaissementsEnAttenteTable> {
  $$EncaissementsEnAttenteTableAnnotationComposer({
    required super.$db,
    required super.$table,
    super.joinBuilder,
    super.$addJoinBuilderToRootComposer,
    super.$removeJoinBuilderFromRootComposer,
  });
  GeneratedColumn<String> get id =>
      $composableBuilder(column: $table.id, builder: (column) => column);

  GeneratedColumn<String> get utilisateurId => $composableBuilder(
    column: $table.utilisateurId,
    builder: (column) => column,
  );

  GeneratedColumn<String> get bailId =>
      $composableBuilder(column: $table.bailId, builder: (column) => column);

  GeneratedColumn<String> get bailLibelle => $composableBuilder(
    column: $table.bailLibelle,
    builder: (column) => column,
  );

  GeneratedColumn<String> get locataireNom => $composableBuilder(
    column: $table.locataireNom,
    builder: (column) => column,
  );

  GeneratedColumn<int> get montant =>
      $composableBuilder(column: $table.montant, builder: (column) => column);

  GeneratedColumn<String> get datePaiement => $composableBuilder(
    column: $table.datePaiement,
    builder: (column) => column,
  );

  GeneratedColumn<DateTime> get creeLe =>
      $composableBuilder(column: $table.creeLe, builder: (column) => column);

  GeneratedColumn<String> get statut =>
      $composableBuilder(column: $table.statut, builder: (column) => column);

  GeneratedColumn<int> get tentatives => $composableBuilder(
    column: $table.tentatives,
    builder: (column) => column,
  );

  GeneratedColumn<String> get derniereErreur => $composableBuilder(
    column: $table.derniereErreur,
    builder: (column) => column,
  );

  GeneratedColumn<DateTime> get envoyeLe =>
      $composableBuilder(column: $table.envoyeLe, builder: (column) => column);
}

class $$EncaissementsEnAttenteTableTableManager
    extends
        RootTableManager<
          _$AppDatabase,
          $EncaissementsEnAttenteTable,
          EncaissementsEnAttenteData,
          $$EncaissementsEnAttenteTableFilterComposer,
          $$EncaissementsEnAttenteTableOrderingComposer,
          $$EncaissementsEnAttenteTableAnnotationComposer,
          $$EncaissementsEnAttenteTableCreateCompanionBuilder,
          $$EncaissementsEnAttenteTableUpdateCompanionBuilder,
          (
            EncaissementsEnAttenteData,
            BaseReferences<
              _$AppDatabase,
              $EncaissementsEnAttenteTable,
              EncaissementsEnAttenteData
            >,
          ),
          EncaissementsEnAttenteData,
          PrefetchHooks Function()
        > {
  $$EncaissementsEnAttenteTableTableManager(
    _$AppDatabase db,
    $EncaissementsEnAttenteTable table,
  ) : super(
        TableManagerState(
          db: db,
          table: table,
          createFilteringComposer: () =>
              $$EncaissementsEnAttenteTableFilterComposer(
                $db: db,
                $table: table,
              ),
          createOrderingComposer: () =>
              $$EncaissementsEnAttenteTableOrderingComposer(
                $db: db,
                $table: table,
              ),
          createComputedFieldComposer: () =>
              $$EncaissementsEnAttenteTableAnnotationComposer(
                $db: db,
                $table: table,
              ),
          updateCompanionCallback:
              ({
                Value<String> id = const Value.absent(),
                Value<String> utilisateurId = const Value.absent(),
                Value<String> bailId = const Value.absent(),
                Value<String> bailLibelle = const Value.absent(),
                Value<String> locataireNom = const Value.absent(),
                Value<int> montant = const Value.absent(),
                Value<String> datePaiement = const Value.absent(),
                Value<DateTime> creeLe = const Value.absent(),
                Value<String> statut = const Value.absent(),
                Value<int> tentatives = const Value.absent(),
                Value<String?> derniereErreur = const Value.absent(),
                Value<DateTime?> envoyeLe = const Value.absent(),
                Value<int> rowid = const Value.absent(),
              }) => EncaissementsEnAttenteCompanion(
                id: id,
                utilisateurId: utilisateurId,
                bailId: bailId,
                bailLibelle: bailLibelle,
                locataireNom: locataireNom,
                montant: montant,
                datePaiement: datePaiement,
                creeLe: creeLe,
                statut: statut,
                tentatives: tentatives,
                derniereErreur: derniereErreur,
                envoyeLe: envoyeLe,
                rowid: rowid,
              ),
          createCompanionCallback:
              ({
                required String id,
                required String utilisateurId,
                required String bailId,
                required String bailLibelle,
                required String locataireNom,
                required int montant,
                required String datePaiement,
                required DateTime creeLe,
                Value<String> statut = const Value.absent(),
                Value<int> tentatives = const Value.absent(),
                Value<String?> derniereErreur = const Value.absent(),
                Value<DateTime?> envoyeLe = const Value.absent(),
                Value<int> rowid = const Value.absent(),
              }) => EncaissementsEnAttenteCompanion.insert(
                id: id,
                utilisateurId: utilisateurId,
                bailId: bailId,
                bailLibelle: bailLibelle,
                locataireNom: locataireNom,
                montant: montant,
                datePaiement: datePaiement,
                creeLe: creeLe,
                statut: statut,
                tentatives: tentatives,
                derniereErreur: derniereErreur,
                envoyeLe: envoyeLe,
                rowid: rowid,
              ),
          withReferenceMapper: (p0) => p0
              .map(
                (e) => (
                  e.readTable<
                    $EncaissementsEnAttenteTable,
                    EncaissementsEnAttenteData
                  >(table),
                  BaseReferences<
                    _$AppDatabase,
                    $EncaissementsEnAttenteTable,
                    EncaissementsEnAttenteData
                  >(db, table, e),
                ),
              )
              .toList(),
          prefetchHooksCallback: null,
        ),
      );
}

typedef $$EncaissementsEnAttenteTableProcessedTableManager =
    ProcessedTableManager<
      _$AppDatabase,
      $EncaissementsEnAttenteTable,
      EncaissementsEnAttenteData,
      $$EncaissementsEnAttenteTableFilterComposer,
      $$EncaissementsEnAttenteTableOrderingComposer,
      $$EncaissementsEnAttenteTableAnnotationComposer,
      $$EncaissementsEnAttenteTableCreateCompanionBuilder,
      $$EncaissementsEnAttenteTableUpdateCompanionBuilder,
      (
        EncaissementsEnAttenteData,
        BaseReferences<
          _$AppDatabase,
          $EncaissementsEnAttenteTable,
          EncaissementsEnAttenteData
        >,
      ),
      EncaissementsEnAttenteData,
      PrefetchHooks Function()
    >;
typedef $$LocatairesCacheTableCreateCompanionBuilder =
    LocatairesCacheCompanion Function({
      required String id,
      required String nom,
      Value<String?> telephone,
      Value<int> rowid,
    });
typedef $$LocatairesCacheTableUpdateCompanionBuilder =
    LocatairesCacheCompanion Function({
      Value<String> id,
      Value<String> nom,
      Value<String?> telephone,
      Value<int> rowid,
    });

class $$LocatairesCacheTableFilterComposer
    extends Composer<_$AppDatabase, $LocatairesCacheTable> {
  $$LocatairesCacheTableFilterComposer({
    required super.$db,
    required super.$table,
    super.joinBuilder,
    super.$addJoinBuilderToRootComposer,
    super.$removeJoinBuilderFromRootComposer,
  });
  ColumnFilters<String> get id => $composableBuilder(
    column: $table.id,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<String> get nom => $composableBuilder(
    column: $table.nom,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<String> get telephone => $composableBuilder(
    column: $table.telephone,
    builder: (column) => ColumnFilters(column),
  );
}

class $$LocatairesCacheTableOrderingComposer
    extends Composer<_$AppDatabase, $LocatairesCacheTable> {
  $$LocatairesCacheTableOrderingComposer({
    required super.$db,
    required super.$table,
    super.joinBuilder,
    super.$addJoinBuilderToRootComposer,
    super.$removeJoinBuilderFromRootComposer,
  });
  ColumnOrderings<String> get id => $composableBuilder(
    column: $table.id,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<String> get nom => $composableBuilder(
    column: $table.nom,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<String> get telephone => $composableBuilder(
    column: $table.telephone,
    builder: (column) => ColumnOrderings(column),
  );
}

class $$LocatairesCacheTableAnnotationComposer
    extends Composer<_$AppDatabase, $LocatairesCacheTable> {
  $$LocatairesCacheTableAnnotationComposer({
    required super.$db,
    required super.$table,
    super.joinBuilder,
    super.$addJoinBuilderToRootComposer,
    super.$removeJoinBuilderFromRootComposer,
  });
  GeneratedColumn<String> get id =>
      $composableBuilder(column: $table.id, builder: (column) => column);

  GeneratedColumn<String> get nom =>
      $composableBuilder(column: $table.nom, builder: (column) => column);

  GeneratedColumn<String> get telephone =>
      $composableBuilder(column: $table.telephone, builder: (column) => column);
}

class $$LocatairesCacheTableTableManager
    extends
        RootTableManager<
          _$AppDatabase,
          $LocatairesCacheTable,
          LocatairesCacheData,
          $$LocatairesCacheTableFilterComposer,
          $$LocatairesCacheTableOrderingComposer,
          $$LocatairesCacheTableAnnotationComposer,
          $$LocatairesCacheTableCreateCompanionBuilder,
          $$LocatairesCacheTableUpdateCompanionBuilder,
          (
            LocatairesCacheData,
            BaseReferences<
              _$AppDatabase,
              $LocatairesCacheTable,
              LocatairesCacheData
            >,
          ),
          LocatairesCacheData,
          PrefetchHooks Function()
        > {
  $$LocatairesCacheTableTableManager(
    _$AppDatabase db,
    $LocatairesCacheTable table,
  ) : super(
        TableManagerState(
          db: db,
          table: table,
          createFilteringComposer: () =>
              $$LocatairesCacheTableFilterComposer($db: db, $table: table),
          createOrderingComposer: () =>
              $$LocatairesCacheTableOrderingComposer($db: db, $table: table),
          createComputedFieldComposer: () =>
              $$LocatairesCacheTableAnnotationComposer($db: db, $table: table),
          updateCompanionCallback:
              ({
                Value<String> id = const Value.absent(),
                Value<String> nom = const Value.absent(),
                Value<String?> telephone = const Value.absent(),
                Value<int> rowid = const Value.absent(),
              }) => LocatairesCacheCompanion(
                id: id,
                nom: nom,
                telephone: telephone,
                rowid: rowid,
              ),
          createCompanionCallback:
              ({
                required String id,
                required String nom,
                Value<String?> telephone = const Value.absent(),
                Value<int> rowid = const Value.absent(),
              }) => LocatairesCacheCompanion.insert(
                id: id,
                nom: nom,
                telephone: telephone,
                rowid: rowid,
              ),
          withReferenceMapper: (p0) => p0
              .map(
                (e) => (
                  e.readTable<$LocatairesCacheTable, LocatairesCacheData>(
                    table,
                  ),
                  BaseReferences<
                    _$AppDatabase,
                    $LocatairesCacheTable,
                    LocatairesCacheData
                  >(db, table, e),
                ),
              )
              .toList(),
          prefetchHooksCallback: null,
        ),
      );
}

typedef $$LocatairesCacheTableProcessedTableManager =
    ProcessedTableManager<
      _$AppDatabase,
      $LocatairesCacheTable,
      LocatairesCacheData,
      $$LocatairesCacheTableFilterComposer,
      $$LocatairesCacheTableOrderingComposer,
      $$LocatairesCacheTableAnnotationComposer,
      $$LocatairesCacheTableCreateCompanionBuilder,
      $$LocatairesCacheTableUpdateCompanionBuilder,
      (
        LocatairesCacheData,
        BaseReferences<
          _$AppDatabase,
          $LocatairesCacheTable,
          LocatairesCacheData
        >,
      ),
      LocatairesCacheData,
      PrefetchHooks Function()
    >;
typedef $$BauxCacheTableCreateCompanionBuilder =
    BauxCacheCompanion Function({
      required String id,
      required String numero,
      required String locataireId,
      required String libelle,
      required String statut,
      required int montantMensuel,
      Value<int> rowid,
    });
typedef $$BauxCacheTableUpdateCompanionBuilder =
    BauxCacheCompanion Function({
      Value<String> id,
      Value<String> numero,
      Value<String> locataireId,
      Value<String> libelle,
      Value<String> statut,
      Value<int> montantMensuel,
      Value<int> rowid,
    });

class $$BauxCacheTableFilterComposer
    extends Composer<_$AppDatabase, $BauxCacheTable> {
  $$BauxCacheTableFilterComposer({
    required super.$db,
    required super.$table,
    super.joinBuilder,
    super.$addJoinBuilderToRootComposer,
    super.$removeJoinBuilderFromRootComposer,
  });
  ColumnFilters<String> get id => $composableBuilder(
    column: $table.id,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<String> get numero => $composableBuilder(
    column: $table.numero,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<String> get locataireId => $composableBuilder(
    column: $table.locataireId,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<String> get libelle => $composableBuilder(
    column: $table.libelle,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<String> get statut => $composableBuilder(
    column: $table.statut,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<int> get montantMensuel => $composableBuilder(
    column: $table.montantMensuel,
    builder: (column) => ColumnFilters(column),
  );
}

class $$BauxCacheTableOrderingComposer
    extends Composer<_$AppDatabase, $BauxCacheTable> {
  $$BauxCacheTableOrderingComposer({
    required super.$db,
    required super.$table,
    super.joinBuilder,
    super.$addJoinBuilderToRootComposer,
    super.$removeJoinBuilderFromRootComposer,
  });
  ColumnOrderings<String> get id => $composableBuilder(
    column: $table.id,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<String> get numero => $composableBuilder(
    column: $table.numero,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<String> get locataireId => $composableBuilder(
    column: $table.locataireId,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<String> get libelle => $composableBuilder(
    column: $table.libelle,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<String> get statut => $composableBuilder(
    column: $table.statut,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<int> get montantMensuel => $composableBuilder(
    column: $table.montantMensuel,
    builder: (column) => ColumnOrderings(column),
  );
}

class $$BauxCacheTableAnnotationComposer
    extends Composer<_$AppDatabase, $BauxCacheTable> {
  $$BauxCacheTableAnnotationComposer({
    required super.$db,
    required super.$table,
    super.joinBuilder,
    super.$addJoinBuilderToRootComposer,
    super.$removeJoinBuilderFromRootComposer,
  });
  GeneratedColumn<String> get id =>
      $composableBuilder(column: $table.id, builder: (column) => column);

  GeneratedColumn<String> get numero =>
      $composableBuilder(column: $table.numero, builder: (column) => column);

  GeneratedColumn<String> get locataireId => $composableBuilder(
    column: $table.locataireId,
    builder: (column) => column,
  );

  GeneratedColumn<String> get libelle =>
      $composableBuilder(column: $table.libelle, builder: (column) => column);

  GeneratedColumn<String> get statut =>
      $composableBuilder(column: $table.statut, builder: (column) => column);

  GeneratedColumn<int> get montantMensuel => $composableBuilder(
    column: $table.montantMensuel,
    builder: (column) => column,
  );
}

class $$BauxCacheTableTableManager
    extends
        RootTableManager<
          _$AppDatabase,
          $BauxCacheTable,
          BauxCacheData,
          $$BauxCacheTableFilterComposer,
          $$BauxCacheTableOrderingComposer,
          $$BauxCacheTableAnnotationComposer,
          $$BauxCacheTableCreateCompanionBuilder,
          $$BauxCacheTableUpdateCompanionBuilder,
          (
            BauxCacheData,
            BaseReferences<_$AppDatabase, $BauxCacheTable, BauxCacheData>,
          ),
          BauxCacheData,
          PrefetchHooks Function()
        > {
  $$BauxCacheTableTableManager(_$AppDatabase db, $BauxCacheTable table)
    : super(
        TableManagerState(
          db: db,
          table: table,
          createFilteringComposer: () =>
              $$BauxCacheTableFilterComposer($db: db, $table: table),
          createOrderingComposer: () =>
              $$BauxCacheTableOrderingComposer($db: db, $table: table),
          createComputedFieldComposer: () =>
              $$BauxCacheTableAnnotationComposer($db: db, $table: table),
          updateCompanionCallback:
              ({
                Value<String> id = const Value.absent(),
                Value<String> numero = const Value.absent(),
                Value<String> locataireId = const Value.absent(),
                Value<String> libelle = const Value.absent(),
                Value<String> statut = const Value.absent(),
                Value<int> montantMensuel = const Value.absent(),
                Value<int> rowid = const Value.absent(),
              }) => BauxCacheCompanion(
                id: id,
                numero: numero,
                locataireId: locataireId,
                libelle: libelle,
                statut: statut,
                montantMensuel: montantMensuel,
                rowid: rowid,
              ),
          createCompanionCallback:
              ({
                required String id,
                required String numero,
                required String locataireId,
                required String libelle,
                required String statut,
                required int montantMensuel,
                Value<int> rowid = const Value.absent(),
              }) => BauxCacheCompanion.insert(
                id: id,
                numero: numero,
                locataireId: locataireId,
                libelle: libelle,
                statut: statut,
                montantMensuel: montantMensuel,
                rowid: rowid,
              ),
          withReferenceMapper: (p0) => p0
              .map(
                (e) => (
                  e.readTable<$BauxCacheTable, BauxCacheData>(table),
                  BaseReferences<_$AppDatabase, $BauxCacheTable, BauxCacheData>(
                    db,
                    table,
                    e,
                  ),
                ),
              )
              .toList(),
          prefetchHooksCallback: null,
        ),
      );
}

typedef $$BauxCacheTableProcessedTableManager =
    ProcessedTableManager<
      _$AppDatabase,
      $BauxCacheTable,
      BauxCacheData,
      $$BauxCacheTableFilterComposer,
      $$BauxCacheTableOrderingComposer,
      $$BauxCacheTableAnnotationComposer,
      $$BauxCacheTableCreateCompanionBuilder,
      $$BauxCacheTableUpdateCompanionBuilder,
      (
        BauxCacheData,
        BaseReferences<_$AppDatabase, $BauxCacheTable, BauxCacheData>,
      ),
      BauxCacheData,
      PrefetchHooks Function()
    >;
typedef $$LotsCacheTableCreateCompanionBuilder =
    LotsCacheCompanion Function({
      required String id,
      required String reference,
      required String immeubleNom,
      required String statut,
      Value<int> rowid,
    });
typedef $$LotsCacheTableUpdateCompanionBuilder =
    LotsCacheCompanion Function({
      Value<String> id,
      Value<String> reference,
      Value<String> immeubleNom,
      Value<String> statut,
      Value<int> rowid,
    });

class $$LotsCacheTableFilterComposer
    extends Composer<_$AppDatabase, $LotsCacheTable> {
  $$LotsCacheTableFilterComposer({
    required super.$db,
    required super.$table,
    super.joinBuilder,
    super.$addJoinBuilderToRootComposer,
    super.$removeJoinBuilderFromRootComposer,
  });
  ColumnFilters<String> get id => $composableBuilder(
    column: $table.id,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<String> get reference => $composableBuilder(
    column: $table.reference,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<String> get immeubleNom => $composableBuilder(
    column: $table.immeubleNom,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<String> get statut => $composableBuilder(
    column: $table.statut,
    builder: (column) => ColumnFilters(column),
  );
}

class $$LotsCacheTableOrderingComposer
    extends Composer<_$AppDatabase, $LotsCacheTable> {
  $$LotsCacheTableOrderingComposer({
    required super.$db,
    required super.$table,
    super.joinBuilder,
    super.$addJoinBuilderToRootComposer,
    super.$removeJoinBuilderFromRootComposer,
  });
  ColumnOrderings<String> get id => $composableBuilder(
    column: $table.id,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<String> get reference => $composableBuilder(
    column: $table.reference,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<String> get immeubleNom => $composableBuilder(
    column: $table.immeubleNom,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<String> get statut => $composableBuilder(
    column: $table.statut,
    builder: (column) => ColumnOrderings(column),
  );
}

class $$LotsCacheTableAnnotationComposer
    extends Composer<_$AppDatabase, $LotsCacheTable> {
  $$LotsCacheTableAnnotationComposer({
    required super.$db,
    required super.$table,
    super.joinBuilder,
    super.$addJoinBuilderToRootComposer,
    super.$removeJoinBuilderFromRootComposer,
  });
  GeneratedColumn<String> get id =>
      $composableBuilder(column: $table.id, builder: (column) => column);

  GeneratedColumn<String> get reference =>
      $composableBuilder(column: $table.reference, builder: (column) => column);

  GeneratedColumn<String> get immeubleNom => $composableBuilder(
    column: $table.immeubleNom,
    builder: (column) => column,
  );

  GeneratedColumn<String> get statut =>
      $composableBuilder(column: $table.statut, builder: (column) => column);
}

class $$LotsCacheTableTableManager
    extends
        RootTableManager<
          _$AppDatabase,
          $LotsCacheTable,
          LotsCacheData,
          $$LotsCacheTableFilterComposer,
          $$LotsCacheTableOrderingComposer,
          $$LotsCacheTableAnnotationComposer,
          $$LotsCacheTableCreateCompanionBuilder,
          $$LotsCacheTableUpdateCompanionBuilder,
          (
            LotsCacheData,
            BaseReferences<_$AppDatabase, $LotsCacheTable, LotsCacheData>,
          ),
          LotsCacheData,
          PrefetchHooks Function()
        > {
  $$LotsCacheTableTableManager(_$AppDatabase db, $LotsCacheTable table)
    : super(
        TableManagerState(
          db: db,
          table: table,
          createFilteringComposer: () =>
              $$LotsCacheTableFilterComposer($db: db, $table: table),
          createOrderingComposer: () =>
              $$LotsCacheTableOrderingComposer($db: db, $table: table),
          createComputedFieldComposer: () =>
              $$LotsCacheTableAnnotationComposer($db: db, $table: table),
          updateCompanionCallback:
              ({
                Value<String> id = const Value.absent(),
                Value<String> reference = const Value.absent(),
                Value<String> immeubleNom = const Value.absent(),
                Value<String> statut = const Value.absent(),
                Value<int> rowid = const Value.absent(),
              }) => LotsCacheCompanion(
                id: id,
                reference: reference,
                immeubleNom: immeubleNom,
                statut: statut,
                rowid: rowid,
              ),
          createCompanionCallback:
              ({
                required String id,
                required String reference,
                required String immeubleNom,
                required String statut,
                Value<int> rowid = const Value.absent(),
              }) => LotsCacheCompanion.insert(
                id: id,
                reference: reference,
                immeubleNom: immeubleNom,
                statut: statut,
                rowid: rowid,
              ),
          withReferenceMapper: (p0) => p0
              .map(
                (e) => (
                  e.readTable<$LotsCacheTable, LotsCacheData>(table),
                  BaseReferences<_$AppDatabase, $LotsCacheTable, LotsCacheData>(
                    db,
                    table,
                    e,
                  ),
                ),
              )
              .toList(),
          prefetchHooksCallback: null,
        ),
      );
}

typedef $$LotsCacheTableProcessedTableManager =
    ProcessedTableManager<
      _$AppDatabase,
      $LotsCacheTable,
      LotsCacheData,
      $$LotsCacheTableFilterComposer,
      $$LotsCacheTableOrderingComposer,
      $$LotsCacheTableAnnotationComposer,
      $$LotsCacheTableCreateCompanionBuilder,
      $$LotsCacheTableUpdateCompanionBuilder,
      (
        LotsCacheData,
        BaseReferences<_$AppDatabase, $LotsCacheTable, LotsCacheData>,
      ),
      LotsCacheData,
      PrefetchHooks Function()
    >;
typedef $$MetaLocaleTableCreateCompanionBuilder =
    MetaLocaleCompanion Function({
      required String cle,
      required String valeur,
      Value<int> rowid,
    });
typedef $$MetaLocaleTableUpdateCompanionBuilder =
    MetaLocaleCompanion Function({
      Value<String> cle,
      Value<String> valeur,
      Value<int> rowid,
    });

class $$MetaLocaleTableFilterComposer
    extends Composer<_$AppDatabase, $MetaLocaleTable> {
  $$MetaLocaleTableFilterComposer({
    required super.$db,
    required super.$table,
    super.joinBuilder,
    super.$addJoinBuilderToRootComposer,
    super.$removeJoinBuilderFromRootComposer,
  });
  ColumnFilters<String> get cle => $composableBuilder(
    column: $table.cle,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<String> get valeur => $composableBuilder(
    column: $table.valeur,
    builder: (column) => ColumnFilters(column),
  );
}

class $$MetaLocaleTableOrderingComposer
    extends Composer<_$AppDatabase, $MetaLocaleTable> {
  $$MetaLocaleTableOrderingComposer({
    required super.$db,
    required super.$table,
    super.joinBuilder,
    super.$addJoinBuilderToRootComposer,
    super.$removeJoinBuilderFromRootComposer,
  });
  ColumnOrderings<String> get cle => $composableBuilder(
    column: $table.cle,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<String> get valeur => $composableBuilder(
    column: $table.valeur,
    builder: (column) => ColumnOrderings(column),
  );
}

class $$MetaLocaleTableAnnotationComposer
    extends Composer<_$AppDatabase, $MetaLocaleTable> {
  $$MetaLocaleTableAnnotationComposer({
    required super.$db,
    required super.$table,
    super.joinBuilder,
    super.$addJoinBuilderToRootComposer,
    super.$removeJoinBuilderFromRootComposer,
  });
  GeneratedColumn<String> get cle =>
      $composableBuilder(column: $table.cle, builder: (column) => column);

  GeneratedColumn<String> get valeur =>
      $composableBuilder(column: $table.valeur, builder: (column) => column);
}

class $$MetaLocaleTableTableManager
    extends
        RootTableManager<
          _$AppDatabase,
          $MetaLocaleTable,
          MetaLocaleData,
          $$MetaLocaleTableFilterComposer,
          $$MetaLocaleTableOrderingComposer,
          $$MetaLocaleTableAnnotationComposer,
          $$MetaLocaleTableCreateCompanionBuilder,
          $$MetaLocaleTableUpdateCompanionBuilder,
          (
            MetaLocaleData,
            BaseReferences<_$AppDatabase, $MetaLocaleTable, MetaLocaleData>,
          ),
          MetaLocaleData,
          PrefetchHooks Function()
        > {
  $$MetaLocaleTableTableManager(_$AppDatabase db, $MetaLocaleTable table)
    : super(
        TableManagerState(
          db: db,
          table: table,
          createFilteringComposer: () =>
              $$MetaLocaleTableFilterComposer($db: db, $table: table),
          createOrderingComposer: () =>
              $$MetaLocaleTableOrderingComposer($db: db, $table: table),
          createComputedFieldComposer: () =>
              $$MetaLocaleTableAnnotationComposer($db: db, $table: table),
          updateCompanionCallback:
              ({
                Value<String> cle = const Value.absent(),
                Value<String> valeur = const Value.absent(),
                Value<int> rowid = const Value.absent(),
              }) => MetaLocaleCompanion(cle: cle, valeur: valeur, rowid: rowid),
          createCompanionCallback:
              ({
                required String cle,
                required String valeur,
                Value<int> rowid = const Value.absent(),
              }) => MetaLocaleCompanion.insert(
                cle: cle,
                valeur: valeur,
                rowid: rowid,
              ),
          withReferenceMapper: (p0) => p0
              .map(
                (e) => (
                  e.readTable<$MetaLocaleTable, MetaLocaleData>(table),
                  BaseReferences<
                    _$AppDatabase,
                    $MetaLocaleTable,
                    MetaLocaleData
                  >(db, table, e),
                ),
              )
              .toList(),
          prefetchHooksCallback: null,
        ),
      );
}

typedef $$MetaLocaleTableProcessedTableManager =
    ProcessedTableManager<
      _$AppDatabase,
      $MetaLocaleTable,
      MetaLocaleData,
      $$MetaLocaleTableFilterComposer,
      $$MetaLocaleTableOrderingComposer,
      $$MetaLocaleTableAnnotationComposer,
      $$MetaLocaleTableCreateCompanionBuilder,
      $$MetaLocaleTableUpdateCompanionBuilder,
      (
        MetaLocaleData,
        BaseReferences<_$AppDatabase, $MetaLocaleTable, MetaLocaleData>,
      ),
      MetaLocaleData,
      PrefetchHooks Function()
    >;

class $AppDatabaseManager {
  final _$AppDatabase _db;
  $AppDatabaseManager(this._db);
  $$EncaissementsEnAttenteTableTableManager get encaissementsEnAttente =>
      $$EncaissementsEnAttenteTableTableManager(
        _db,
        _db.encaissementsEnAttente,
      );
  $$LocatairesCacheTableTableManager get locatairesCache =>
      $$LocatairesCacheTableTableManager(_db, _db.locatairesCache);
  $$BauxCacheTableTableManager get bauxCache =>
      $$BauxCacheTableTableManager(_db, _db.bauxCache);
  $$LotsCacheTableTableManager get lotsCache =>
      $$LotsCacheTableTableManager(_db, _db.lotsCache);
  $$MetaLocaleTableTableManager get metaLocale =>
      $$MetaLocaleTableTableManager(_db, _db.metaLocale);
}
