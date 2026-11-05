// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'etat_sauvegardes_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$EtatSauvegardesDtoCWProxy {
  EtatSauvegardesDto sauvegardes(List<SauvegardeDto>? sauvegardes);

  EtatSauvegardesDto operation(OperationSauvegardeDto? operation);

  EtatSauvegardesDto heureQuotidienne(String? heureQuotidienne);

  EtatSauvegardesDto versionInstallee(String? versionInstallee);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `EtatSauvegardesDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// EtatSauvegardesDto(...).copyWith(id: 12, name: "My name")
  /// ````
  EtatSauvegardesDto call({
    List<SauvegardeDto>? sauvegardes,
    OperationSauvegardeDto? operation,
    String? heureQuotidienne,
    String? versionInstallee,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfEtatSauvegardesDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfEtatSauvegardesDto.copyWith.fieldName(...)`
class _$EtatSauvegardesDtoCWProxyImpl implements _$EtatSauvegardesDtoCWProxy {
  const _$EtatSauvegardesDtoCWProxyImpl(this._value);

  final EtatSauvegardesDto _value;

  @override
  EtatSauvegardesDto sauvegardes(List<SauvegardeDto>? sauvegardes) =>
      this(sauvegardes: sauvegardes);

  @override
  EtatSauvegardesDto operation(OperationSauvegardeDto? operation) =>
      this(operation: operation);

  @override
  EtatSauvegardesDto heureQuotidienne(String? heureQuotidienne) =>
      this(heureQuotidienne: heureQuotidienne);

  @override
  EtatSauvegardesDto versionInstallee(String? versionInstallee) =>
      this(versionInstallee: versionInstallee);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `EtatSauvegardesDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// EtatSauvegardesDto(...).copyWith(id: 12, name: "My name")
  /// ````
  EtatSauvegardesDto call({
    Object? sauvegardes = const $CopyWithPlaceholder(),
    Object? operation = const $CopyWithPlaceholder(),
    Object? heureQuotidienne = const $CopyWithPlaceholder(),
    Object? versionInstallee = const $CopyWithPlaceholder(),
  }) {
    return EtatSauvegardesDto(
      sauvegardes: sauvegardes == const $CopyWithPlaceholder()
          ? _value.sauvegardes
          // ignore: cast_nullable_to_non_nullable
          : sauvegardes as List<SauvegardeDto>?,
      operation: operation == const $CopyWithPlaceholder()
          ? _value.operation
          // ignore: cast_nullable_to_non_nullable
          : operation as OperationSauvegardeDto?,
      heureQuotidienne: heureQuotidienne == const $CopyWithPlaceholder()
          ? _value.heureQuotidienne
          // ignore: cast_nullable_to_non_nullable
          : heureQuotidienne as String?,
      versionInstallee: versionInstallee == const $CopyWithPlaceholder()
          ? _value.versionInstallee
          // ignore: cast_nullable_to_non_nullable
          : versionInstallee as String?,
    );
  }
}

extension $EtatSauvegardesDtoCopyWith on EtatSauvegardesDto {
  /// Returns a callable class that can be used as follows: `instanceOfEtatSauvegardesDto.copyWith(...)` or like so:`instanceOfEtatSauvegardesDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$EtatSauvegardesDtoCWProxy get copyWith =>
      _$EtatSauvegardesDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

EtatSauvegardesDto _$EtatSauvegardesDtoFromJson(
  Map<String, dynamic> json,
) => $checkedCreate('EtatSauvegardesDto', json, ($checkedConvert) {
  final val = EtatSauvegardesDto(
    sauvegardes: $checkedConvert(
      'sauvegardes',
      (v) => (v as List<dynamic>?)
          ?.map((e) => SauvegardeDto.fromJson(e as Map<String, dynamic>))
          .toList(),
    ),
    operation: $checkedConvert(
      'operation',
      (v) => v == null
          ? null
          : OperationSauvegardeDto.fromJson(v as Map<String, dynamic>),
    ),
    heureQuotidienne: $checkedConvert('heureQuotidienne', (v) => v as String?),
    versionInstallee: $checkedConvert('versionInstallee', (v) => v as String?),
  );
  return val;
});

Map<String, dynamic> _$EtatSauvegardesDtoToJson(EtatSauvegardesDto instance) =>
    <String, dynamic>{
      'sauvegardes': ?instance.sauvegardes?.map((e) => e.toJson()).toList(),
      'operation': ?instance.operation?.toJson(),
      'heureQuotidienne': ?instance.heureQuotidienne,
      'versionInstallee': ?instance.versionInstallee,
    };
