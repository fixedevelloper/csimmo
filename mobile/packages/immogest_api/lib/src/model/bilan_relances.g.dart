// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'bilan_relances.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$BilanRelancesCWProxy {
  BilanRelances examinees(int? examinees);

  BilanRelances envoyees(int? envoyees);

  BilanRelances sansCanal(int? sansCanal);

  BilanRelances ignorees(int? ignorees);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `BilanRelances(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// BilanRelances(...).copyWith(id: 12, name: "My name")
  /// ````
  BilanRelances call({
    int? examinees,
    int? envoyees,
    int? sansCanal,
    int? ignorees,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfBilanRelances.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfBilanRelances.copyWith.fieldName(...)`
class _$BilanRelancesCWProxyImpl implements _$BilanRelancesCWProxy {
  const _$BilanRelancesCWProxyImpl(this._value);

  final BilanRelances _value;

  @override
  BilanRelances examinees(int? examinees) => this(examinees: examinees);

  @override
  BilanRelances envoyees(int? envoyees) => this(envoyees: envoyees);

  @override
  BilanRelances sansCanal(int? sansCanal) => this(sansCanal: sansCanal);

  @override
  BilanRelances ignorees(int? ignorees) => this(ignorees: ignorees);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `BilanRelances(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// BilanRelances(...).copyWith(id: 12, name: "My name")
  /// ````
  BilanRelances call({
    Object? examinees = const $CopyWithPlaceholder(),
    Object? envoyees = const $CopyWithPlaceholder(),
    Object? sansCanal = const $CopyWithPlaceholder(),
    Object? ignorees = const $CopyWithPlaceholder(),
  }) {
    return BilanRelances(
      examinees: examinees == const $CopyWithPlaceholder()
          ? _value.examinees
          // ignore: cast_nullable_to_non_nullable
          : examinees as int?,
      envoyees: envoyees == const $CopyWithPlaceholder()
          ? _value.envoyees
          // ignore: cast_nullable_to_non_nullable
          : envoyees as int?,
      sansCanal: sansCanal == const $CopyWithPlaceholder()
          ? _value.sansCanal
          // ignore: cast_nullable_to_non_nullable
          : sansCanal as int?,
      ignorees: ignorees == const $CopyWithPlaceholder()
          ? _value.ignorees
          // ignore: cast_nullable_to_non_nullable
          : ignorees as int?,
    );
  }
}

extension $BilanRelancesCopyWith on BilanRelances {
  /// Returns a callable class that can be used as follows: `instanceOfBilanRelances.copyWith(...)` or like so:`instanceOfBilanRelances.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$BilanRelancesCWProxy get copyWith => _$BilanRelancesCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

BilanRelances _$BilanRelancesFromJson(Map<String, dynamic> json) =>
    $checkedCreate('BilanRelances', json, ($checkedConvert) {
      final val = BilanRelances(
        examinees: $checkedConvert('examinees', (v) => (v as num?)?.toInt()),
        envoyees: $checkedConvert('envoyees', (v) => (v as num?)?.toInt()),
        sansCanal: $checkedConvert('sansCanal', (v) => (v as num?)?.toInt()),
        ignorees: $checkedConvert('ignorees', (v) => (v as num?)?.toInt()),
      );
      return val;
    });

Map<String, dynamic> _$BilanRelancesToJson(BilanRelances instance) =>
    <String, dynamic>{
      'examinees': ?instance.examinees,
      'envoyees': ?instance.envoyees,
      'sansCanal': ?instance.sansCanal,
      'ignorees': ?instance.ignorees,
    };
