// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'lot_requete.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$LotRequeteCWProxy {
  LotRequete immeubleId(String? immeubleId);

  LotRequete reference(String reference);

  LotRequete type(LotRequeteTypeEnum type);

  LotRequete surfaceM2(num? surfaceM2);

  LotRequete pieces(int? pieces);

  LotRequete loyerIndicatif(int? loyerIndicatif);

  LotRequete equipements(List<String>? equipements);

  LotRequete compteurs(List<CompteurRequete>? compteurs);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `LotRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// LotRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  LotRequete call({
    String? immeubleId,
    String reference,
    LotRequeteTypeEnum type,
    num? surfaceM2,
    int? pieces,
    int? loyerIndicatif,
    List<String>? equipements,
    List<CompteurRequete>? compteurs,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfLotRequete.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfLotRequete.copyWith.fieldName(...)`
class _$LotRequeteCWProxyImpl implements _$LotRequeteCWProxy {
  const _$LotRequeteCWProxyImpl(this._value);

  final LotRequete _value;

  @override
  LotRequete immeubleId(String? immeubleId) => this(immeubleId: immeubleId);

  @override
  LotRequete reference(String reference) => this(reference: reference);

  @override
  LotRequete type(LotRequeteTypeEnum type) => this(type: type);

  @override
  LotRequete surfaceM2(num? surfaceM2) => this(surfaceM2: surfaceM2);

  @override
  LotRequete pieces(int? pieces) => this(pieces: pieces);

  @override
  LotRequete loyerIndicatif(int? loyerIndicatif) =>
      this(loyerIndicatif: loyerIndicatif);

  @override
  LotRequete equipements(List<String>? equipements) =>
      this(equipements: equipements);

  @override
  LotRequete compteurs(List<CompteurRequete>? compteurs) =>
      this(compteurs: compteurs);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `LotRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// LotRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  LotRequete call({
    Object? immeubleId = const $CopyWithPlaceholder(),
    Object? reference = const $CopyWithPlaceholder(),
    Object? type = const $CopyWithPlaceholder(),
    Object? surfaceM2 = const $CopyWithPlaceholder(),
    Object? pieces = const $CopyWithPlaceholder(),
    Object? loyerIndicatif = const $CopyWithPlaceholder(),
    Object? equipements = const $CopyWithPlaceholder(),
    Object? compteurs = const $CopyWithPlaceholder(),
  }) {
    return LotRequete(
      immeubleId: immeubleId == const $CopyWithPlaceholder()
          ? _value.immeubleId
          // ignore: cast_nullable_to_non_nullable
          : immeubleId as String?,
      reference: reference == const $CopyWithPlaceholder()
          ? _value.reference
          // ignore: cast_nullable_to_non_nullable
          : reference as String,
      type: type == const $CopyWithPlaceholder()
          ? _value.type
          // ignore: cast_nullable_to_non_nullable
          : type as LotRequeteTypeEnum,
      surfaceM2: surfaceM2 == const $CopyWithPlaceholder()
          ? _value.surfaceM2
          // ignore: cast_nullable_to_non_nullable
          : surfaceM2 as num?,
      pieces: pieces == const $CopyWithPlaceholder()
          ? _value.pieces
          // ignore: cast_nullable_to_non_nullable
          : pieces as int?,
      loyerIndicatif: loyerIndicatif == const $CopyWithPlaceholder()
          ? _value.loyerIndicatif
          // ignore: cast_nullable_to_non_nullable
          : loyerIndicatif as int?,
      equipements: equipements == const $CopyWithPlaceholder()
          ? _value.equipements
          // ignore: cast_nullable_to_non_nullable
          : equipements as List<String>?,
      compteurs: compteurs == const $CopyWithPlaceholder()
          ? _value.compteurs
          // ignore: cast_nullable_to_non_nullable
          : compteurs as List<CompteurRequete>?,
    );
  }
}

extension $LotRequeteCopyWith on LotRequete {
  /// Returns a callable class that can be used as follows: `instanceOfLotRequete.copyWith(...)` or like so:`instanceOfLotRequete.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$LotRequeteCWProxy get copyWith => _$LotRequeteCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

LotRequete _$LotRequeteFromJson(Map<String, dynamic> json) =>
    $checkedCreate('LotRequete', json, ($checkedConvert) {
      $checkKeys(json, requiredKeys: const ['reference', 'type']);
      final val = LotRequete(
        immeubleId: $checkedConvert('immeubleId', (v) => v as String?),
        reference: $checkedConvert('reference', (v) => v as String),
        type: $checkedConvert(
          'type',
          (v) => $enumDecode(_$LotRequeteTypeEnumEnumMap, v),
        ),
        surfaceM2: $checkedConvert('surfaceM2', (v) => v as num?),
        pieces: $checkedConvert('pieces', (v) => (v as num?)?.toInt()),
        loyerIndicatif: $checkedConvert(
          'loyerIndicatif',
          (v) => (v as num?)?.toInt(),
        ),
        equipements: $checkedConvert(
          'equipements',
          (v) => (v as List<dynamic>?)?.map((e) => e as String).toList(),
        ),
        compteurs: $checkedConvert(
          'compteurs',
          (v) => (v as List<dynamic>?)
              ?.map((e) => CompteurRequete.fromJson(e as Map<String, dynamic>))
              .toList(),
        ),
      );
      return val;
    });

Map<String, dynamic> _$LotRequeteToJson(LotRequete instance) =>
    <String, dynamic>{
      'immeubleId': ?instance.immeubleId,
      'reference': instance.reference,
      'type': _$LotRequeteTypeEnumEnumMap[instance.type]!,
      'surfaceM2': ?instance.surfaceM2,
      'pieces': ?instance.pieces,
      'loyerIndicatif': ?instance.loyerIndicatif,
      'equipements': ?instance.equipements,
      'compteurs': ?instance.compteurs?.map((e) => e.toJson()).toList(),
    };

const _$LotRequeteTypeEnumEnumMap = {
  LotRequeteTypeEnum.APPARTEMENT: 'APPARTEMENT',
  LotRequeteTypeEnum.STUDIO: 'STUDIO',
  LotRequeteTypeEnum.BOUTIQUE: 'BOUTIQUE',
  LotRequeteTypeEnum.BUREAU: 'BUREAU',
  LotRequeteTypeEnum.TERRAIN: 'TERRAIN',
};
