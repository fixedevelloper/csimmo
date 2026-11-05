// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'lot_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$LotDtoCWProxy {
  LotDto id(String? id);

  LotDto immeubleId(String? immeubleId);

  LotDto immeubleNom(String? immeubleNom);

  LotDto ville(String? ville);

  LotDto quartier(String? quartier);

  LotDto reference(String? reference);

  LotDto type(LotDtoTypeEnum? type);

  LotDto surfaceM2(num? surfaceM2);

  LotDto pieces(int? pieces);

  LotDto loyerIndicatif(int? loyerIndicatif);

  LotDto statut(LotDtoStatutEnum? statut);

  LotDto equipements(List<String>? equipements);

  LotDto compteurs(List<CompteurDto>? compteurs);

  LotDto archive(bool? archive);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `LotDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// LotDto(...).copyWith(id: 12, name: "My name")
  /// ````
  LotDto call({
    String? id,
    String? immeubleId,
    String? immeubleNom,
    String? ville,
    String? quartier,
    String? reference,
    LotDtoTypeEnum? type,
    num? surfaceM2,
    int? pieces,
    int? loyerIndicatif,
    LotDtoStatutEnum? statut,
    List<String>? equipements,
    List<CompteurDto>? compteurs,
    bool? archive,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfLotDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfLotDto.copyWith.fieldName(...)`
class _$LotDtoCWProxyImpl implements _$LotDtoCWProxy {
  const _$LotDtoCWProxyImpl(this._value);

  final LotDto _value;

  @override
  LotDto id(String? id) => this(id: id);

  @override
  LotDto immeubleId(String? immeubleId) => this(immeubleId: immeubleId);

  @override
  LotDto immeubleNom(String? immeubleNom) => this(immeubleNom: immeubleNom);

  @override
  LotDto ville(String? ville) => this(ville: ville);

  @override
  LotDto quartier(String? quartier) => this(quartier: quartier);

  @override
  LotDto reference(String? reference) => this(reference: reference);

  @override
  LotDto type(LotDtoTypeEnum? type) => this(type: type);

  @override
  LotDto surfaceM2(num? surfaceM2) => this(surfaceM2: surfaceM2);

  @override
  LotDto pieces(int? pieces) => this(pieces: pieces);

  @override
  LotDto loyerIndicatif(int? loyerIndicatif) =>
      this(loyerIndicatif: loyerIndicatif);

  @override
  LotDto statut(LotDtoStatutEnum? statut) => this(statut: statut);

  @override
  LotDto equipements(List<String>? equipements) =>
      this(equipements: equipements);

  @override
  LotDto compteurs(List<CompteurDto>? compteurs) => this(compteurs: compteurs);

  @override
  LotDto archive(bool? archive) => this(archive: archive);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `LotDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// LotDto(...).copyWith(id: 12, name: "My name")
  /// ````
  LotDto call({
    Object? id = const $CopyWithPlaceholder(),
    Object? immeubleId = const $CopyWithPlaceholder(),
    Object? immeubleNom = const $CopyWithPlaceholder(),
    Object? ville = const $CopyWithPlaceholder(),
    Object? quartier = const $CopyWithPlaceholder(),
    Object? reference = const $CopyWithPlaceholder(),
    Object? type = const $CopyWithPlaceholder(),
    Object? surfaceM2 = const $CopyWithPlaceholder(),
    Object? pieces = const $CopyWithPlaceholder(),
    Object? loyerIndicatif = const $CopyWithPlaceholder(),
    Object? statut = const $CopyWithPlaceholder(),
    Object? equipements = const $CopyWithPlaceholder(),
    Object? compteurs = const $CopyWithPlaceholder(),
    Object? archive = const $CopyWithPlaceholder(),
  }) {
    return LotDto(
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
      ville: ville == const $CopyWithPlaceholder()
          ? _value.ville
          // ignore: cast_nullable_to_non_nullable
          : ville as String?,
      quartier: quartier == const $CopyWithPlaceholder()
          ? _value.quartier
          // ignore: cast_nullable_to_non_nullable
          : quartier as String?,
      reference: reference == const $CopyWithPlaceholder()
          ? _value.reference
          // ignore: cast_nullable_to_non_nullable
          : reference as String?,
      type: type == const $CopyWithPlaceholder()
          ? _value.type
          // ignore: cast_nullable_to_non_nullable
          : type as LotDtoTypeEnum?,
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
      statut: statut == const $CopyWithPlaceholder()
          ? _value.statut
          // ignore: cast_nullable_to_non_nullable
          : statut as LotDtoStatutEnum?,
      equipements: equipements == const $CopyWithPlaceholder()
          ? _value.equipements
          // ignore: cast_nullable_to_non_nullable
          : equipements as List<String>?,
      compteurs: compteurs == const $CopyWithPlaceholder()
          ? _value.compteurs
          // ignore: cast_nullable_to_non_nullable
          : compteurs as List<CompteurDto>?,
      archive: archive == const $CopyWithPlaceholder()
          ? _value.archive
          // ignore: cast_nullable_to_non_nullable
          : archive as bool?,
    );
  }
}

extension $LotDtoCopyWith on LotDto {
  /// Returns a callable class that can be used as follows: `instanceOfLotDto.copyWith(...)` or like so:`instanceOfLotDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$LotDtoCWProxy get copyWith => _$LotDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

LotDto _$LotDtoFromJson(Map<String, dynamic> json) =>
    $checkedCreate('LotDto', json, ($checkedConvert) {
      final val = LotDto(
        id: $checkedConvert('id', (v) => v as String?),
        immeubleId: $checkedConvert('immeubleId', (v) => v as String?),
        immeubleNom: $checkedConvert('immeubleNom', (v) => v as String?),
        ville: $checkedConvert('ville', (v) => v as String?),
        quartier: $checkedConvert('quartier', (v) => v as String?),
        reference: $checkedConvert('reference', (v) => v as String?),
        type: $checkedConvert(
          'type',
          (v) => $enumDecodeNullable(_$LotDtoTypeEnumEnumMap, v),
        ),
        surfaceM2: $checkedConvert('surfaceM2', (v) => v as num?),
        pieces: $checkedConvert('pieces', (v) => (v as num?)?.toInt()),
        loyerIndicatif: $checkedConvert(
          'loyerIndicatif',
          (v) => (v as num?)?.toInt(),
        ),
        statut: $checkedConvert(
          'statut',
          (v) => $enumDecodeNullable(_$LotDtoStatutEnumEnumMap, v),
        ),
        equipements: $checkedConvert(
          'equipements',
          (v) => (v as List<dynamic>?)?.map((e) => e as String).toList(),
        ),
        compteurs: $checkedConvert(
          'compteurs',
          (v) => (v as List<dynamic>?)
              ?.map((e) => CompteurDto.fromJson(e as Map<String, dynamic>))
              .toList(),
        ),
        archive: $checkedConvert('archive', (v) => v as bool?),
      );
      return val;
    });

Map<String, dynamic> _$LotDtoToJson(LotDto instance) => <String, dynamic>{
  'id': ?instance.id,
  'immeubleId': ?instance.immeubleId,
  'immeubleNom': ?instance.immeubleNom,
  'ville': ?instance.ville,
  'quartier': ?instance.quartier,
  'reference': ?instance.reference,
  'type': ?_$LotDtoTypeEnumEnumMap[instance.type],
  'surfaceM2': ?instance.surfaceM2,
  'pieces': ?instance.pieces,
  'loyerIndicatif': ?instance.loyerIndicatif,
  'statut': ?_$LotDtoStatutEnumEnumMap[instance.statut],
  'equipements': ?instance.equipements,
  'compteurs': ?instance.compteurs?.map((e) => e.toJson()).toList(),
  'archive': ?instance.archive,
};

const _$LotDtoTypeEnumEnumMap = {
  LotDtoTypeEnum.APPARTEMENT: 'APPARTEMENT',
  LotDtoTypeEnum.STUDIO: 'STUDIO',
  LotDtoTypeEnum.BOUTIQUE: 'BOUTIQUE',
  LotDtoTypeEnum.BUREAU: 'BUREAU',
  LotDtoTypeEnum.TERRAIN: 'TERRAIN',
};

const _$LotDtoStatutEnumEnumMap = {
  LotDtoStatutEnum.LIBRE: 'LIBRE',
  LotDtoStatutEnum.OCCUPE: 'OCCUPE',
  LotDtoStatutEnum.EN_TRAVAUX: 'EN_TRAVAUX',
};
