// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'depense_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$DepenseDtoCWProxy {
  DepenseDto id(String? id);

  DepenseDto immeubleId(String? immeubleId);

  DepenseDto immeubleNom(String? immeubleNom);

  DepenseDto lotId(String? lotId);

  DepenseDto proprietaireId(String? proprietaireId);

  DepenseDto dateDepense(DateTime? dateDepense);

  DepenseDto nature(String? nature);

  DepenseDto montant(int? montant);

  DepenseDto reference(String? reference);

  DepenseDto statut(String? statut);

  DepenseDto motifAnnulation(String? motifAnnulation);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `DepenseDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// DepenseDto(...).copyWith(id: 12, name: "My name")
  /// ````
  DepenseDto call({
    String? id,
    String? immeubleId,
    String? immeubleNom,
    String? lotId,
    String? proprietaireId,
    DateTime? dateDepense,
    String? nature,
    int? montant,
    String? reference,
    String? statut,
    String? motifAnnulation,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfDepenseDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfDepenseDto.copyWith.fieldName(...)`
class _$DepenseDtoCWProxyImpl implements _$DepenseDtoCWProxy {
  const _$DepenseDtoCWProxyImpl(this._value);

  final DepenseDto _value;

  @override
  DepenseDto id(String? id) => this(id: id);

  @override
  DepenseDto immeubleId(String? immeubleId) => this(immeubleId: immeubleId);

  @override
  DepenseDto immeubleNom(String? immeubleNom) => this(immeubleNom: immeubleNom);

  @override
  DepenseDto lotId(String? lotId) => this(lotId: lotId);

  @override
  DepenseDto proprietaireId(String? proprietaireId) =>
      this(proprietaireId: proprietaireId);

  @override
  DepenseDto dateDepense(DateTime? dateDepense) =>
      this(dateDepense: dateDepense);

  @override
  DepenseDto nature(String? nature) => this(nature: nature);

  @override
  DepenseDto montant(int? montant) => this(montant: montant);

  @override
  DepenseDto reference(String? reference) => this(reference: reference);

  @override
  DepenseDto statut(String? statut) => this(statut: statut);

  @override
  DepenseDto motifAnnulation(String? motifAnnulation) =>
      this(motifAnnulation: motifAnnulation);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `DepenseDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// DepenseDto(...).copyWith(id: 12, name: "My name")
  /// ````
  DepenseDto call({
    Object? id = const $CopyWithPlaceholder(),
    Object? immeubleId = const $CopyWithPlaceholder(),
    Object? immeubleNom = const $CopyWithPlaceholder(),
    Object? lotId = const $CopyWithPlaceholder(),
    Object? proprietaireId = const $CopyWithPlaceholder(),
    Object? dateDepense = const $CopyWithPlaceholder(),
    Object? nature = const $CopyWithPlaceholder(),
    Object? montant = const $CopyWithPlaceholder(),
    Object? reference = const $CopyWithPlaceholder(),
    Object? statut = const $CopyWithPlaceholder(),
    Object? motifAnnulation = const $CopyWithPlaceholder(),
  }) {
    return DepenseDto(
      id: id == const $CopyWithPlaceholder()
          ? _value.id
          // ignore: cast_nullable_to_non_nullable
          : id as String?,
      immeubleId: immeubleId == const $CopyWithPlaceholder()
          ? _value.immeubleId
          // ignore: cast_nullable_to_non_nullable
          : immeubleId as String?,
      immeubleNom: immeubleNom == const $CopyWithPlaceholder()
          ? _value.immeubleNom
          // ignore: cast_nullable_to_non_nullable
          : immeubleNom as String?,
      lotId: lotId == const $CopyWithPlaceholder()
          ? _value.lotId
          // ignore: cast_nullable_to_non_nullable
          : lotId as String?,
      proprietaireId: proprietaireId == const $CopyWithPlaceholder()
          ? _value.proprietaireId
          // ignore: cast_nullable_to_non_nullable
          : proprietaireId as String?,
      dateDepense: dateDepense == const $CopyWithPlaceholder()
          ? _value.dateDepense
          // ignore: cast_nullable_to_non_nullable
          : dateDepense as DateTime?,
      nature: nature == const $CopyWithPlaceholder()
          ? _value.nature
          // ignore: cast_nullable_to_non_nullable
          : nature as String?,
      montant: montant == const $CopyWithPlaceholder()
          ? _value.montant
          // ignore: cast_nullable_to_non_nullable
          : montant as int?,
      reference: reference == const $CopyWithPlaceholder()
          ? _value.reference
          // ignore: cast_nullable_to_non_nullable
          : reference as String?,
      statut: statut == const $CopyWithPlaceholder()
          ? _value.statut
          // ignore: cast_nullable_to_non_nullable
          : statut as String?,
      motifAnnulation: motifAnnulation == const $CopyWithPlaceholder()
          ? _value.motifAnnulation
          // ignore: cast_nullable_to_non_nullable
          : motifAnnulation as String?,
    );
  }
}

extension $DepenseDtoCopyWith on DepenseDto {
  /// Returns a callable class that can be used as follows: `instanceOfDepenseDto.copyWith(...)` or like so:`instanceOfDepenseDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$DepenseDtoCWProxy get copyWith => _$DepenseDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

DepenseDto _$DepenseDtoFromJson(Map<String, dynamic> json) => $checkedCreate(
  'DepenseDto',
  json,
  ($checkedConvert) {
    final val = DepenseDto(
      id: $checkedConvert('id', (v) => v as String?),
      immeubleId: $checkedConvert('immeubleId', (v) => v as String?),
      immeubleNom: $checkedConvert('immeubleNom', (v) => v as String?),
      lotId: $checkedConvert('lotId', (v) => v as String?),
      proprietaireId: $checkedConvert('proprietaireId', (v) => v as String?),
      dateDepense: $checkedConvert(
        'dateDepense',
        (v) => v == null ? null : DateTime.parse(v as String),
      ),
      nature: $checkedConvert('nature', (v) => v as String?),
      montant: $checkedConvert('montant', (v) => (v as num?)?.toInt()),
      reference: $checkedConvert('reference', (v) => v as String?),
      statut: $checkedConvert('statut', (v) => v as String?),
      motifAnnulation: $checkedConvert('motifAnnulation', (v) => v as String?),
    );
    return val;
  },
);

Map<String, dynamic> _$DepenseDtoToJson(DepenseDto instance) =>
    <String, dynamic>{
      'id': ?instance.id,
      'immeubleId': ?instance.immeubleId,
      'immeubleNom': ?instance.immeubleNom,
      'lotId': ?instance.lotId,
      'proprietaireId': ?instance.proprietaireId,
      'dateDepense': ?instance.dateDepense?.toIso8601String(),
      'nature': ?instance.nature,
      'montant': ?instance.montant,
      'reference': ?instance.reference,
      'statut': ?instance.statut,
      'motifAnnulation': ?instance.motifAnnulation,
    };
