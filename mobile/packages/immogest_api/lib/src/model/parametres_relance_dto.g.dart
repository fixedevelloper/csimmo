// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'parametres_relance_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$ParametresRelanceDtoCWProxy {
  ParametresRelanceDto actif(bool? actif);

  ParametresRelanceDto niveaux(List<int>? niveaux);

  ParametresRelanceDto delaiMiseEnDemeureJours(int? delaiMiseEnDemeureJours);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ParametresRelanceDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ParametresRelanceDto(...).copyWith(id: 12, name: "My name")
  /// ````
  ParametresRelanceDto call({
    bool? actif,
    List<int>? niveaux,
    int? delaiMiseEnDemeureJours,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfParametresRelanceDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfParametresRelanceDto.copyWith.fieldName(...)`
class _$ParametresRelanceDtoCWProxyImpl
    implements _$ParametresRelanceDtoCWProxy {
  const _$ParametresRelanceDtoCWProxyImpl(this._value);

  final ParametresRelanceDto _value;

  @override
  ParametresRelanceDto actif(bool? actif) => this(actif: actif);

  @override
  ParametresRelanceDto niveaux(List<int>? niveaux) => this(niveaux: niveaux);

  @override
  ParametresRelanceDto delaiMiseEnDemeureJours(int? delaiMiseEnDemeureJours) =>
      this(delaiMiseEnDemeureJours: delaiMiseEnDemeureJours);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ParametresRelanceDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ParametresRelanceDto(...).copyWith(id: 12, name: "My name")
  /// ````
  ParametresRelanceDto call({
    Object? actif = const $CopyWithPlaceholder(),
    Object? niveaux = const $CopyWithPlaceholder(),
    Object? delaiMiseEnDemeureJours = const $CopyWithPlaceholder(),
  }) {
    return ParametresRelanceDto(
      actif: actif == const $CopyWithPlaceholder()
          ? _value.actif
          // ignore: cast_nullable_to_non_nullable
          : actif as bool?,
      niveaux: niveaux == const $CopyWithPlaceholder()
          ? _value.niveaux
          // ignore: cast_nullable_to_non_nullable
          : niveaux as List<int>?,
      delaiMiseEnDemeureJours:
          delaiMiseEnDemeureJours == const $CopyWithPlaceholder()
          ? _value.delaiMiseEnDemeureJours
          // ignore: cast_nullable_to_non_nullable
          : delaiMiseEnDemeureJours as int?,
    );
  }
}

extension $ParametresRelanceDtoCopyWith on ParametresRelanceDto {
  /// Returns a callable class that can be used as follows: `instanceOfParametresRelanceDto.copyWith(...)` or like so:`instanceOfParametresRelanceDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$ParametresRelanceDtoCWProxy get copyWith =>
      _$ParametresRelanceDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

ParametresRelanceDto _$ParametresRelanceDtoFromJson(
  Map<String, dynamic> json,
) => $checkedCreate('ParametresRelanceDto', json, ($checkedConvert) {
  final val = ParametresRelanceDto(
    actif: $checkedConvert('actif', (v) => v as bool?),
    niveaux: $checkedConvert(
      'niveaux',
      (v) => (v as List<dynamic>?)?.map((e) => (e as num).toInt()).toList(),
    ),
    delaiMiseEnDemeureJours: $checkedConvert(
      'delaiMiseEnDemeureJours',
      (v) => (v as num?)?.toInt(),
    ),
  );
  return val;
});

Map<String, dynamic> _$ParametresRelanceDtoToJson(
  ParametresRelanceDto instance,
) => <String, dynamic>{
  'actif': ?instance.actif,
  'niveaux': ?instance.niveaux,
  'delaiMiseEnDemeureJours': ?instance.delaiMiseEnDemeureJours,
};
