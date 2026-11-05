// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'occupation.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$OccupationCWProxy {
  Occupation libres(int? libres);

  Occupation occupes(int? occupes);

  Occupation enTravaux(int? enTravaux);

  Occupation tauxBp(int? tauxBp);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `Occupation(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// Occupation(...).copyWith(id: 12, name: "My name")
  /// ````
  Occupation call({int? libres, int? occupes, int? enTravaux, int? tauxBp});
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfOccupation.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfOccupation.copyWith.fieldName(...)`
class _$OccupationCWProxyImpl implements _$OccupationCWProxy {
  const _$OccupationCWProxyImpl(this._value);

  final Occupation _value;

  @override
  Occupation libres(int? libres) => this(libres: libres);

  @override
  Occupation occupes(int? occupes) => this(occupes: occupes);

  @override
  Occupation enTravaux(int? enTravaux) => this(enTravaux: enTravaux);

  @override
  Occupation tauxBp(int? tauxBp) => this(tauxBp: tauxBp);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `Occupation(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// Occupation(...).copyWith(id: 12, name: "My name")
  /// ````
  Occupation call({
    Object? libres = const $CopyWithPlaceholder(),
    Object? occupes = const $CopyWithPlaceholder(),
    Object? enTravaux = const $CopyWithPlaceholder(),
    Object? tauxBp = const $CopyWithPlaceholder(),
  }) {
    return Occupation(
      libres: libres == const $CopyWithPlaceholder()
          ? _value.libres
          // ignore: cast_nullable_to_non_nullable
          : libres as int?,
      occupes: occupes == const $CopyWithPlaceholder()
          ? _value.occupes
          // ignore: cast_nullable_to_non_nullable
          : occupes as int?,
      enTravaux: enTravaux == const $CopyWithPlaceholder()
          ? _value.enTravaux
          // ignore: cast_nullable_to_non_nullable
          : enTravaux as int?,
      tauxBp: tauxBp == const $CopyWithPlaceholder()
          ? _value.tauxBp
          // ignore: cast_nullable_to_non_nullable
          : tauxBp as int?,
    );
  }
}

extension $OccupationCopyWith on Occupation {
  /// Returns a callable class that can be used as follows: `instanceOfOccupation.copyWith(...)` or like so:`instanceOfOccupation.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$OccupationCWProxy get copyWith => _$OccupationCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

Occupation _$OccupationFromJson(Map<String, dynamic> json) =>
    $checkedCreate('Occupation', json, ($checkedConvert) {
      final val = Occupation(
        libres: $checkedConvert('libres', (v) => (v as num?)?.toInt()),
        occupes: $checkedConvert('occupes', (v) => (v as num?)?.toInt()),
        enTravaux: $checkedConvert('enTravaux', (v) => (v as num?)?.toInt()),
        tauxBp: $checkedConvert('tauxBp', (v) => (v as num?)?.toInt()),
      );
      return val;
    });

Map<String, dynamic> _$OccupationToJson(Occupation instance) =>
    <String, dynamic>{
      'libres': ?instance.libres,
      'occupes': ?instance.occupes,
      'enTravaux': ?instance.enTravaux,
      'tauxBp': ?instance.tauxBp,
    };
