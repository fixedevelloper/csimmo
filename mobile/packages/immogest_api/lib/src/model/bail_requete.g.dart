// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'bail_requete.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$BailRequeteCWProxy {
  BailRequete lotId(String lotId);

  BailRequete locataireId(String locataireId);

  BailRequete garantIds(List<String>? garantIds);

  BailRequete dateDebut(DateTime dateDebut);

  BailRequete dateFin(DateTime? dateFin);

  BailRequete loyer(int loyer);

  BailRequete charges(int? charges);

  BailRequete periodicite(BailRequetePeriodiciteEnum periodicite);

  BailRequete jourEcheance(int jourEcheance);

  BailRequete caution(int? caution);

  BailRequete moisAvance(int? moisAvance);

  BailRequete preavisMois(int? preavisMois);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `BailRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// BailRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  BailRequete call({
    String lotId,
    String locataireId,
    List<String>? garantIds,
    DateTime dateDebut,
    DateTime? dateFin,
    int loyer,
    int? charges,
    BailRequetePeriodiciteEnum periodicite,
    int jourEcheance,
    int? caution,
    int? moisAvance,
    int? preavisMois,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfBailRequete.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfBailRequete.copyWith.fieldName(...)`
class _$BailRequeteCWProxyImpl implements _$BailRequeteCWProxy {
  const _$BailRequeteCWProxyImpl(this._value);

  final BailRequete _value;

  @override
  BailRequete lotId(String lotId) => this(lotId: lotId);

  @override
  BailRequete locataireId(String locataireId) => this(locataireId: locataireId);

  @override
  BailRequete garantIds(List<String>? garantIds) => this(garantIds: garantIds);

  @override
  BailRequete dateDebut(DateTime dateDebut) => this(dateDebut: dateDebut);

  @override
  BailRequete dateFin(DateTime? dateFin) => this(dateFin: dateFin);

  @override
  BailRequete loyer(int loyer) => this(loyer: loyer);

  @override
  BailRequete charges(int? charges) => this(charges: charges);

  @override
  BailRequete periodicite(BailRequetePeriodiciteEnum periodicite) =>
      this(periodicite: periodicite);

  @override
  BailRequete jourEcheance(int jourEcheance) =>
      this(jourEcheance: jourEcheance);

  @override
  BailRequete caution(int? caution) => this(caution: caution);

  @override
  BailRequete moisAvance(int? moisAvance) => this(moisAvance: moisAvance);

  @override
  BailRequete preavisMois(int? preavisMois) => this(preavisMois: preavisMois);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `BailRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// BailRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  BailRequete call({
    Object? lotId = const $CopyWithPlaceholder(),
    Object? locataireId = const $CopyWithPlaceholder(),
    Object? garantIds = const $CopyWithPlaceholder(),
    Object? dateDebut = const $CopyWithPlaceholder(),
    Object? dateFin = const $CopyWithPlaceholder(),
    Object? loyer = const $CopyWithPlaceholder(),
    Object? charges = const $CopyWithPlaceholder(),
    Object? periodicite = const $CopyWithPlaceholder(),
    Object? jourEcheance = const $CopyWithPlaceholder(),
    Object? caution = const $CopyWithPlaceholder(),
    Object? moisAvance = const $CopyWithPlaceholder(),
    Object? preavisMois = const $CopyWithPlaceholder(),
  }) {
    return BailRequete(
      lotId: lotId == const $CopyWithPlaceholder()
          ? _value.lotId
          // ignore: cast_nullable_to_non_nullable
          : lotId as String,
      locataireId: locataireId == const $CopyWithPlaceholder()
          ? _value.locataireId
          // ignore: cast_nullable_to_non_nullable
          : locataireId as String,
      garantIds: garantIds == const $CopyWithPlaceholder()
          ? _value.garantIds
          // ignore: cast_nullable_to_non_nullable
          : garantIds as List<String>?,
      dateDebut: dateDebut == const $CopyWithPlaceholder()
          ? _value.dateDebut
          // ignore: cast_nullable_to_non_nullable
          : dateDebut as DateTime,
      dateFin: dateFin == const $CopyWithPlaceholder()
          ? _value.dateFin
          // ignore: cast_nullable_to_non_nullable
          : dateFin as DateTime?,
      loyer: loyer == const $CopyWithPlaceholder()
          ? _value.loyer
          // ignore: cast_nullable_to_non_nullable
          : loyer as int,
      charges: charges == const $CopyWithPlaceholder()
          ? _value.charges
          // ignore: cast_nullable_to_non_nullable
          : charges as int?,
      periodicite: periodicite == const $CopyWithPlaceholder()
          ? _value.periodicite
          // ignore: cast_nullable_to_non_nullable
          : periodicite as BailRequetePeriodiciteEnum,
      jourEcheance: jourEcheance == const $CopyWithPlaceholder()
          ? _value.jourEcheance
          // ignore: cast_nullable_to_non_nullable
          : jourEcheance as int,
      caution: caution == const $CopyWithPlaceholder()
          ? _value.caution
          // ignore: cast_nullable_to_non_nullable
          : caution as int?,
      moisAvance: moisAvance == const $CopyWithPlaceholder()
          ? _value.moisAvance
          // ignore: cast_nullable_to_non_nullable
          : moisAvance as int?,
      preavisMois: preavisMois == const $CopyWithPlaceholder()
          ? _value.preavisMois
          // ignore: cast_nullable_to_non_nullable
          : preavisMois as int?,
    );
  }
}

extension $BailRequeteCopyWith on BailRequete {
  /// Returns a callable class that can be used as follows: `instanceOfBailRequete.copyWith(...)` or like so:`instanceOfBailRequete.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$BailRequeteCWProxy get copyWith => _$BailRequeteCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

BailRequete _$BailRequeteFromJson(
  Map<String, dynamic> json,
) => $checkedCreate('BailRequete', json, ($checkedConvert) {
  $checkKeys(
    json,
    requiredKeys: const [
      'lotId',
      'locataireId',
      'dateDebut',
      'loyer',
      'periodicite',
      'jourEcheance',
    ],
  );
  final val = BailRequete(
    lotId: $checkedConvert('lotId', (v) => v as String),
    locataireId: $checkedConvert('locataireId', (v) => v as String),
    garantIds: $checkedConvert(
      'garantIds',
      (v) => (v as List<dynamic>?)?.map((e) => e as String).toList(),
    ),
    dateDebut: $checkedConvert('dateDebut', (v) => DateTime.parse(v as String)),
    dateFin: $checkedConvert(
      'dateFin',
      (v) => v == null ? null : DateTime.parse(v as String),
    ),
    loyer: $checkedConvert('loyer', (v) => (v as num).toInt()),
    charges: $checkedConvert('charges', (v) => (v as num?)?.toInt()),
    periodicite: $checkedConvert(
      'periodicite',
      (v) => $enumDecode(_$BailRequetePeriodiciteEnumEnumMap, v),
    ),
    jourEcheance: $checkedConvert('jourEcheance', (v) => (v as num).toInt()),
    caution: $checkedConvert('caution', (v) => (v as num?)?.toInt()),
    moisAvance: $checkedConvert('moisAvance', (v) => (v as num?)?.toInt()),
    preavisMois: $checkedConvert('preavisMois', (v) => (v as num?)?.toInt()),
  );
  return val;
});

Map<String, dynamic> _$BailRequeteToJson(BailRequete instance) =>
    <String, dynamic>{
      'lotId': instance.lotId,
      'locataireId': instance.locataireId,
      'garantIds': ?instance.garantIds,
      'dateDebut': instance.dateDebut.toIso8601String(),
      'dateFin': ?instance.dateFin?.toIso8601String(),
      'loyer': instance.loyer,
      'charges': ?instance.charges,
      'periodicite': _$BailRequetePeriodiciteEnumEnumMap[instance.periodicite]!,
      'jourEcheance': instance.jourEcheance,
      'caution': ?instance.caution,
      'moisAvance': ?instance.moisAvance,
      'preavisMois': ?instance.preavisMois,
    };

const _$BailRequetePeriodiciteEnumEnumMap = {
  BailRequetePeriodiciteEnum.MENSUELLE: 'MENSUELLE',
  BailRequetePeriodiciteEnum.TRIMESTRIELLE: 'TRIMESTRIELLE',
  BailRequetePeriodiciteEnum.ANNUELLE: 'ANNUELLE',
};
