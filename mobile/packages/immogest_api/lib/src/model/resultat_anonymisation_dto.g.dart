// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'resultat_anonymisation_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$ResultatAnonymisationDtoCWProxy {
  ResultatAnonymisationDto demandeId(String? demandeId);

  ResultatAnonymisationDto piecesSupprimees(int? piecesSupprimees);

  ResultatAnonymisationDto conserve(List<String>? conserve);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ResultatAnonymisationDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ResultatAnonymisationDto(...).copyWith(id: 12, name: "My name")
  /// ````
  ResultatAnonymisationDto call({
    String? demandeId,
    int? piecesSupprimees,
    List<String>? conserve,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfResultatAnonymisationDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfResultatAnonymisationDto.copyWith.fieldName(...)`
class _$ResultatAnonymisationDtoCWProxyImpl
    implements _$ResultatAnonymisationDtoCWProxy {
  const _$ResultatAnonymisationDtoCWProxyImpl(this._value);

  final ResultatAnonymisationDto _value;

  @override
  ResultatAnonymisationDto demandeId(String? demandeId) =>
      this(demandeId: demandeId);

  @override
  ResultatAnonymisationDto piecesSupprimees(int? piecesSupprimees) =>
      this(piecesSupprimees: piecesSupprimees);

  @override
  ResultatAnonymisationDto conserve(List<String>? conserve) =>
      this(conserve: conserve);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ResultatAnonymisationDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ResultatAnonymisationDto(...).copyWith(id: 12, name: "My name")
  /// ````
  ResultatAnonymisationDto call({
    Object? demandeId = const $CopyWithPlaceholder(),
    Object? piecesSupprimees = const $CopyWithPlaceholder(),
    Object? conserve = const $CopyWithPlaceholder(),
  }) {
    return ResultatAnonymisationDto(
      demandeId: demandeId == const $CopyWithPlaceholder()
          ? _value.demandeId
          // ignore: cast_nullable_to_non_nullable
          : demandeId as String?,
      piecesSupprimees: piecesSupprimees == const $CopyWithPlaceholder()
          ? _value.piecesSupprimees
          // ignore: cast_nullable_to_non_nullable
          : piecesSupprimees as int?,
      conserve: conserve == const $CopyWithPlaceholder()
          ? _value.conserve
          // ignore: cast_nullable_to_non_nullable
          : conserve as List<String>?,
    );
  }
}

extension $ResultatAnonymisationDtoCopyWith on ResultatAnonymisationDto {
  /// Returns a callable class that can be used as follows: `instanceOfResultatAnonymisationDto.copyWith(...)` or like so:`instanceOfResultatAnonymisationDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$ResultatAnonymisationDtoCWProxy get copyWith =>
      _$ResultatAnonymisationDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

ResultatAnonymisationDto _$ResultatAnonymisationDtoFromJson(
  Map<String, dynamic> json,
) => $checkedCreate('ResultatAnonymisationDto', json, ($checkedConvert) {
  final val = ResultatAnonymisationDto(
    demandeId: $checkedConvert('demandeId', (v) => v as String?),
    piecesSupprimees: $checkedConvert(
      'piecesSupprimees',
      (v) => (v as num?)?.toInt(),
    ),
    conserve: $checkedConvert(
      'conserve',
      (v) => (v as List<dynamic>?)?.map((e) => e as String).toList(),
    ),
  );
  return val;
});

Map<String, dynamic> _$ResultatAnonymisationDtoToJson(
  ResultatAnonymisationDto instance,
) => <String, dynamic>{
  'demandeId': ?instance.demandeId,
  'piecesSupprimees': ?instance.piecesSupprimees,
  'conserve': ?instance.conserve,
};
