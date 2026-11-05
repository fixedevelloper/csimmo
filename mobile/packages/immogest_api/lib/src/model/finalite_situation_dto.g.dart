// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'finalite_situation_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$FinaliteSituationDtoCWProxy {
  FinaliteSituationDto finalite(FinaliteSituationDtoFinaliteEnum? finalite);

  FinaliteSituationDto statut(String? statut);

  FinaliteSituationDto derniere(ConsentementDto? derniere);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `FinaliteSituationDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// FinaliteSituationDto(...).copyWith(id: 12, name: "My name")
  /// ````
  FinaliteSituationDto call({
    FinaliteSituationDtoFinaliteEnum? finalite,
    String? statut,
    ConsentementDto? derniere,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfFinaliteSituationDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfFinaliteSituationDto.copyWith.fieldName(...)`
class _$FinaliteSituationDtoCWProxyImpl
    implements _$FinaliteSituationDtoCWProxy {
  const _$FinaliteSituationDtoCWProxyImpl(this._value);

  final FinaliteSituationDto _value;

  @override
  FinaliteSituationDto finalite(FinaliteSituationDtoFinaliteEnum? finalite) =>
      this(finalite: finalite);

  @override
  FinaliteSituationDto statut(String? statut) => this(statut: statut);

  @override
  FinaliteSituationDto derniere(ConsentementDto? derniere) =>
      this(derniere: derniere);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `FinaliteSituationDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// FinaliteSituationDto(...).copyWith(id: 12, name: "My name")
  /// ````
  FinaliteSituationDto call({
    Object? finalite = const $CopyWithPlaceholder(),
    Object? statut = const $CopyWithPlaceholder(),
    Object? derniere = const $CopyWithPlaceholder(),
  }) {
    return FinaliteSituationDto(
      finalite: finalite == const $CopyWithPlaceholder()
          ? _value.finalite
          // ignore: cast_nullable_to_non_nullable
          : finalite as FinaliteSituationDtoFinaliteEnum?,
      statut: statut == const $CopyWithPlaceholder()
          ? _value.statut
          // ignore: cast_nullable_to_non_nullable
          : statut as String?,
      derniere: derniere == const $CopyWithPlaceholder()
          ? _value.derniere
          // ignore: cast_nullable_to_non_nullable
          : derniere as ConsentementDto?,
    );
  }
}

extension $FinaliteSituationDtoCopyWith on FinaliteSituationDto {
  /// Returns a callable class that can be used as follows: `instanceOfFinaliteSituationDto.copyWith(...)` or like so:`instanceOfFinaliteSituationDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$FinaliteSituationDtoCWProxy get copyWith =>
      _$FinaliteSituationDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

FinaliteSituationDto _$FinaliteSituationDtoFromJson(
  Map<String, dynamic> json,
) => $checkedCreate('FinaliteSituationDto', json, ($checkedConvert) {
  final val = FinaliteSituationDto(
    finalite: $checkedConvert(
      'finalite',
      (v) => $enumDecodeNullable(_$FinaliteSituationDtoFinaliteEnumEnumMap, v),
    ),
    statut: $checkedConvert('statut', (v) => v as String?),
    derniere: $checkedConvert(
      'derniere',
      (v) => v == null
          ? null
          : ConsentementDto.fromJson(v as Map<String, dynamic>),
    ),
  );
  return val;
});

Map<String, dynamic> _$FinaliteSituationDtoToJson(
  FinaliteSituationDto instance,
) => <String, dynamic>{
  'finalite': ?_$FinaliteSituationDtoFinaliteEnumEnumMap[instance.finalite],
  'statut': ?instance.statut,
  'derniere': ?instance.derniere?.toJson(),
};

const _$FinaliteSituationDtoFinaliteEnumEnumMap = {
  FinaliteSituationDtoFinaliteEnum.NOTIFICATIONS: 'NOTIFICATIONS',
  FinaliteSituationDtoFinaliteEnum.PIECES_IDENTITE: 'PIECES_IDENTITE',
  FinaliteSituationDtoFinaliteEnum.TRANSMISSION_PROPRIETAIRE:
      'TRANSMISSION_PROPRIETAIRE',
  FinaliteSituationDtoFinaliteEnum.APPLICATION_MOBILE: 'APPLICATION_MOBILE',
};
