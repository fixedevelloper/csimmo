// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'situation_consentement_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$SituationConsentementDtoCWProxy {
  SituationConsentementDto type(SituationConsentementDtoTypeEnum? type);

  SituationConsentementDto personneId(String? personneId);

  SituationConsentementDto finalites(List<FinaliteSituationDto>? finalites);

  SituationConsentementDto historique(List<ConsentementDto>? historique);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `SituationConsentementDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// SituationConsentementDto(...).copyWith(id: 12, name: "My name")
  /// ````
  SituationConsentementDto call({
    SituationConsentementDtoTypeEnum? type,
    String? personneId,
    List<FinaliteSituationDto>? finalites,
    List<ConsentementDto>? historique,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfSituationConsentementDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfSituationConsentementDto.copyWith.fieldName(...)`
class _$SituationConsentementDtoCWProxyImpl
    implements _$SituationConsentementDtoCWProxy {
  const _$SituationConsentementDtoCWProxyImpl(this._value);

  final SituationConsentementDto _value;

  @override
  SituationConsentementDto type(SituationConsentementDtoTypeEnum? type) =>
      this(type: type);

  @override
  SituationConsentementDto personneId(String? personneId) =>
      this(personneId: personneId);

  @override
  SituationConsentementDto finalites(List<FinaliteSituationDto>? finalites) =>
      this(finalites: finalites);

  @override
  SituationConsentementDto historique(List<ConsentementDto>? historique) =>
      this(historique: historique);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `SituationConsentementDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// SituationConsentementDto(...).copyWith(id: 12, name: "My name")
  /// ````
  SituationConsentementDto call({
    Object? type = const $CopyWithPlaceholder(),
    Object? personneId = const $CopyWithPlaceholder(),
    Object? finalites = const $CopyWithPlaceholder(),
    Object? historique = const $CopyWithPlaceholder(),
  }) {
    return SituationConsentementDto(
      type: type == const $CopyWithPlaceholder()
          ? _value.type
          // ignore: cast_nullable_to_non_nullable
          : type as SituationConsentementDtoTypeEnum?,
      personneId: personneId == const $CopyWithPlaceholder()
          ? _value.personneId
          // ignore: cast_nullable_to_non_nullable
          : personneId as String?,
      finalites: finalites == const $CopyWithPlaceholder()
          ? _value.finalites
          // ignore: cast_nullable_to_non_nullable
          : finalites as List<FinaliteSituationDto>?,
      historique: historique == const $CopyWithPlaceholder()
          ? _value.historique
          // ignore: cast_nullable_to_non_nullable
          : historique as List<ConsentementDto>?,
    );
  }
}

extension $SituationConsentementDtoCopyWith on SituationConsentementDto {
  /// Returns a callable class that can be used as follows: `instanceOfSituationConsentementDto.copyWith(...)` or like so:`instanceOfSituationConsentementDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$SituationConsentementDtoCWProxy get copyWith =>
      _$SituationConsentementDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

SituationConsentementDto _$SituationConsentementDtoFromJson(
  Map<String, dynamic> json,
) => $checkedCreate('SituationConsentementDto', json, ($checkedConvert) {
  final val = SituationConsentementDto(
    type: $checkedConvert(
      'type',
      (v) => $enumDecodeNullable(_$SituationConsentementDtoTypeEnumEnumMap, v),
    ),
    personneId: $checkedConvert('personneId', (v) => v as String?),
    finalites: $checkedConvert(
      'finalites',
      (v) => (v as List<dynamic>?)
          ?.map((e) => FinaliteSituationDto.fromJson(e as Map<String, dynamic>))
          .toList(),
    ),
    historique: $checkedConvert(
      'historique',
      (v) => (v as List<dynamic>?)
          ?.map((e) => ConsentementDto.fromJson(e as Map<String, dynamic>))
          .toList(),
    ),
  );
  return val;
});

Map<String, dynamic> _$SituationConsentementDtoToJson(
  SituationConsentementDto instance,
) => <String, dynamic>{
  'type': ?_$SituationConsentementDtoTypeEnumEnumMap[instance.type],
  'personneId': ?instance.personneId,
  'finalites': ?instance.finalites?.map((e) => e.toJson()).toList(),
  'historique': ?instance.historique?.map((e) => e.toJson()).toList(),
};

const _$SituationConsentementDtoTypeEnumEnumMap = {
  SituationConsentementDtoTypeEnum.LOCATAIRE: 'LOCATAIRE',
  SituationConsentementDtoTypeEnum.GARANT: 'GARANT',
  SituationConsentementDtoTypeEnum.PROPRIETAIRE: 'PROPRIETAIRE',
  SituationConsentementDtoTypeEnum.PRESTATAIRE: 'PRESTATAIRE',
};
