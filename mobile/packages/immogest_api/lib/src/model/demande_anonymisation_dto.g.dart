// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'demande_anonymisation_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$DemandeAnonymisationDtoCWProxy {
  DemandeAnonymisationDto id(String? id);

  DemandeAnonymisationDto type(DemandeAnonymisationDtoTypeEnum? type);

  DemandeAnonymisationDto personneId(String? personneId);

  DemandeAnonymisationDto statut(String? statut);

  DemandeAnonymisationDto motif(String? motif);

  DemandeAnonymisationDto motifRefus(String? motifRefus);

  DemandeAnonymisationDto recueLe(DateTime? recueLe);

  DemandeAnonymisationDto recuePar(String? recuePar);

  DemandeAnonymisationDto traiteeLe(DateTime? traiteeLe);

  DemandeAnonymisationDto traiteePar(String? traiteePar);

  DemandeAnonymisationDto obstacles(List<ObstacleDto>? obstacles);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `DemandeAnonymisationDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// DemandeAnonymisationDto(...).copyWith(id: 12, name: "My name")
  /// ````
  DemandeAnonymisationDto call({
    String? id,
    DemandeAnonymisationDtoTypeEnum? type,
    String? personneId,
    String? statut,
    String? motif,
    String? motifRefus,
    DateTime? recueLe,
    String? recuePar,
    DateTime? traiteeLe,
    String? traiteePar,
    List<ObstacleDto>? obstacles,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfDemandeAnonymisationDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfDemandeAnonymisationDto.copyWith.fieldName(...)`
class _$DemandeAnonymisationDtoCWProxyImpl
    implements _$DemandeAnonymisationDtoCWProxy {
  const _$DemandeAnonymisationDtoCWProxyImpl(this._value);

  final DemandeAnonymisationDto _value;

  @override
  DemandeAnonymisationDto id(String? id) => this(id: id);

  @override
  DemandeAnonymisationDto type(DemandeAnonymisationDtoTypeEnum? type) =>
      this(type: type);

  @override
  DemandeAnonymisationDto personneId(String? personneId) =>
      this(personneId: personneId);

  @override
  DemandeAnonymisationDto statut(String? statut) => this(statut: statut);

  @override
  DemandeAnonymisationDto motif(String? motif) => this(motif: motif);

  @override
  DemandeAnonymisationDto motifRefus(String? motifRefus) =>
      this(motifRefus: motifRefus);

  @override
  DemandeAnonymisationDto recueLe(DateTime? recueLe) => this(recueLe: recueLe);

  @override
  DemandeAnonymisationDto recuePar(String? recuePar) =>
      this(recuePar: recuePar);

  @override
  DemandeAnonymisationDto traiteeLe(DateTime? traiteeLe) =>
      this(traiteeLe: traiteeLe);

  @override
  DemandeAnonymisationDto traiteePar(String? traiteePar) =>
      this(traiteePar: traiteePar);

  @override
  DemandeAnonymisationDto obstacles(List<ObstacleDto>? obstacles) =>
      this(obstacles: obstacles);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `DemandeAnonymisationDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// DemandeAnonymisationDto(...).copyWith(id: 12, name: "My name")
  /// ````
  DemandeAnonymisationDto call({
    Object? id = const $CopyWithPlaceholder(),
    Object? type = const $CopyWithPlaceholder(),
    Object? personneId = const $CopyWithPlaceholder(),
    Object? statut = const $CopyWithPlaceholder(),
    Object? motif = const $CopyWithPlaceholder(),
    Object? motifRefus = const $CopyWithPlaceholder(),
    Object? recueLe = const $CopyWithPlaceholder(),
    Object? recuePar = const $CopyWithPlaceholder(),
    Object? traiteeLe = const $CopyWithPlaceholder(),
    Object? traiteePar = const $CopyWithPlaceholder(),
    Object? obstacles = const $CopyWithPlaceholder(),
  }) {
    return DemandeAnonymisationDto(
      id: id == const $CopyWithPlaceholder()
          ? _value.id
          // ignore: cast_nullable_to_non_nullable
          : id as String?,
      type: type == const $CopyWithPlaceholder()
          ? _value.type
          // ignore: cast_nullable_to_non_nullable
          : type as DemandeAnonymisationDtoTypeEnum?,
      personneId: personneId == const $CopyWithPlaceholder()
          ? _value.personneId
          // ignore: cast_nullable_to_non_nullable
          : personneId as String?,
      statut: statut == const $CopyWithPlaceholder()
          ? _value.statut
          // ignore: cast_nullable_to_non_nullable
          : statut as String?,
      motif: motif == const $CopyWithPlaceholder()
          ? _value.motif
          // ignore: cast_nullable_to_non_nullable
          : motif as String?,
      motifRefus: motifRefus == const $CopyWithPlaceholder()
          ? _value.motifRefus
          // ignore: cast_nullable_to_non_nullable
          : motifRefus as String?,
      recueLe: recueLe == const $CopyWithPlaceholder()
          ? _value.recueLe
          // ignore: cast_nullable_to_non_nullable
          : recueLe as DateTime?,
      recuePar: recuePar == const $CopyWithPlaceholder()
          ? _value.recuePar
          // ignore: cast_nullable_to_non_nullable
          : recuePar as String?,
      traiteeLe: traiteeLe == const $CopyWithPlaceholder()
          ? _value.traiteeLe
          // ignore: cast_nullable_to_non_nullable
          : traiteeLe as DateTime?,
      traiteePar: traiteePar == const $CopyWithPlaceholder()
          ? _value.traiteePar
          // ignore: cast_nullable_to_non_nullable
          : traiteePar as String?,
      obstacles: obstacles == const $CopyWithPlaceholder()
          ? _value.obstacles
          // ignore: cast_nullable_to_non_nullable
          : obstacles as List<ObstacleDto>?,
    );
  }
}

extension $DemandeAnonymisationDtoCopyWith on DemandeAnonymisationDto {
  /// Returns a callable class that can be used as follows: `instanceOfDemandeAnonymisationDto.copyWith(...)` or like so:`instanceOfDemandeAnonymisationDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$DemandeAnonymisationDtoCWProxy get copyWith =>
      _$DemandeAnonymisationDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

DemandeAnonymisationDto _$DemandeAnonymisationDtoFromJson(
  Map<String, dynamic> json,
) => $checkedCreate('DemandeAnonymisationDto', json, ($checkedConvert) {
  final val = DemandeAnonymisationDto(
    id: $checkedConvert('id', (v) => v as String?),
    type: $checkedConvert(
      'type',
      (v) => $enumDecodeNullable(_$DemandeAnonymisationDtoTypeEnumEnumMap, v),
    ),
    personneId: $checkedConvert('personneId', (v) => v as String?),
    statut: $checkedConvert('statut', (v) => v as String?),
    motif: $checkedConvert('motif', (v) => v as String?),
    motifRefus: $checkedConvert('motifRefus', (v) => v as String?),
    recueLe: $checkedConvert(
      'recueLe',
      (v) => v == null ? null : DateTime.parse(v as String),
    ),
    recuePar: $checkedConvert('recuePar', (v) => v as String?),
    traiteeLe: $checkedConvert(
      'traiteeLe',
      (v) => v == null ? null : DateTime.parse(v as String),
    ),
    traiteePar: $checkedConvert('traiteePar', (v) => v as String?),
    obstacles: $checkedConvert(
      'obstacles',
      (v) => (v as List<dynamic>?)
          ?.map((e) => ObstacleDto.fromJson(e as Map<String, dynamic>))
          .toList(),
    ),
  );
  return val;
});

Map<String, dynamic> _$DemandeAnonymisationDtoToJson(
  DemandeAnonymisationDto instance,
) => <String, dynamic>{
  'id': ?instance.id,
  'type': ?_$DemandeAnonymisationDtoTypeEnumEnumMap[instance.type],
  'personneId': ?instance.personneId,
  'statut': ?instance.statut,
  'motif': ?instance.motif,
  'motifRefus': ?instance.motifRefus,
  'recueLe': ?instance.recueLe?.toIso8601String(),
  'recuePar': ?instance.recuePar,
  'traiteeLe': ?instance.traiteeLe?.toIso8601String(),
  'traiteePar': ?instance.traiteePar,
  'obstacles': ?instance.obstacles?.map((e) => e.toJson()).toList(),
};

const _$DemandeAnonymisationDtoTypeEnumEnumMap = {
  DemandeAnonymisationDtoTypeEnum.LOCATAIRE: 'LOCATAIRE',
  DemandeAnonymisationDtoTypeEnum.GARANT: 'GARANT',
  DemandeAnonymisationDtoTypeEnum.PROPRIETAIRE: 'PROPRIETAIRE',
  DemandeAnonymisationDtoTypeEnum.PRESTATAIRE: 'PRESTATAIRE',
};
