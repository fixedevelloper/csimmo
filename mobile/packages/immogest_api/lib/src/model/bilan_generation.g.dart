// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'bilan_generation.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$BilanGenerationCWProxy {
  BilanGeneration baux(int? baux);

  BilanGeneration echeancesCreees(int? echeancesCreees);

  BilanGeneration echeancesAjustees(int? echeancesAjustees);

  BilanGeneration avancesImputees(int? avancesImputees);

  BilanGeneration statutsMisAJour(int? statutsMisAJour);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `BilanGeneration(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// BilanGeneration(...).copyWith(id: 12, name: "My name")
  /// ````
  BilanGeneration call({
    int? baux,
    int? echeancesCreees,
    int? echeancesAjustees,
    int? avancesImputees,
    int? statutsMisAJour,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfBilanGeneration.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfBilanGeneration.copyWith.fieldName(...)`
class _$BilanGenerationCWProxyImpl implements _$BilanGenerationCWProxy {
  const _$BilanGenerationCWProxyImpl(this._value);

  final BilanGeneration _value;

  @override
  BilanGeneration baux(int? baux) => this(baux: baux);

  @override
  BilanGeneration echeancesCreees(int? echeancesCreees) =>
      this(echeancesCreees: echeancesCreees);

  @override
  BilanGeneration echeancesAjustees(int? echeancesAjustees) =>
      this(echeancesAjustees: echeancesAjustees);

  @override
  BilanGeneration avancesImputees(int? avancesImputees) =>
      this(avancesImputees: avancesImputees);

  @override
  BilanGeneration statutsMisAJour(int? statutsMisAJour) =>
      this(statutsMisAJour: statutsMisAJour);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `BilanGeneration(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// BilanGeneration(...).copyWith(id: 12, name: "My name")
  /// ````
  BilanGeneration call({
    Object? baux = const $CopyWithPlaceholder(),
    Object? echeancesCreees = const $CopyWithPlaceholder(),
    Object? echeancesAjustees = const $CopyWithPlaceholder(),
    Object? avancesImputees = const $CopyWithPlaceholder(),
    Object? statutsMisAJour = const $CopyWithPlaceholder(),
  }) {
    return BilanGeneration(
      baux: baux == const $CopyWithPlaceholder()
          ? _value.baux
          // ignore: cast_nullable_to_non_nullable
          : baux as int?,
      echeancesCreees: echeancesCreees == const $CopyWithPlaceholder()
          ? _value.echeancesCreees
          // ignore: cast_nullable_to_non_nullable
          : echeancesCreees as int?,
      echeancesAjustees: echeancesAjustees == const $CopyWithPlaceholder()
          ? _value.echeancesAjustees
          // ignore: cast_nullable_to_non_nullable
          : echeancesAjustees as int?,
      avancesImputees: avancesImputees == const $CopyWithPlaceholder()
          ? _value.avancesImputees
          // ignore: cast_nullable_to_non_nullable
          : avancesImputees as int?,
      statutsMisAJour: statutsMisAJour == const $CopyWithPlaceholder()
          ? _value.statutsMisAJour
          // ignore: cast_nullable_to_non_nullable
          : statutsMisAJour as int?,
    );
  }
}

extension $BilanGenerationCopyWith on BilanGeneration {
  /// Returns a callable class that can be used as follows: `instanceOfBilanGeneration.copyWith(...)` or like so:`instanceOfBilanGeneration.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$BilanGenerationCWProxy get copyWith => _$BilanGenerationCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

BilanGeneration _$BilanGenerationFromJson(Map<String, dynamic> json) =>
    $checkedCreate('BilanGeneration', json, ($checkedConvert) {
      final val = BilanGeneration(
        baux: $checkedConvert('baux', (v) => (v as num?)?.toInt()),
        echeancesCreees: $checkedConvert(
          'echeancesCreees',
          (v) => (v as num?)?.toInt(),
        ),
        echeancesAjustees: $checkedConvert(
          'echeancesAjustees',
          (v) => (v as num?)?.toInt(),
        ),
        avancesImputees: $checkedConvert(
          'avancesImputees',
          (v) => (v as num?)?.toInt(),
        ),
        statutsMisAJour: $checkedConvert(
          'statutsMisAJour',
          (v) => (v as num?)?.toInt(),
        ),
      );
      return val;
    });

Map<String, dynamic> _$BilanGenerationToJson(BilanGeneration instance) =>
    <String, dynamic>{
      'baux': ?instance.baux,
      'echeancesCreees': ?instance.echeancesCreees,
      'echeancesAjustees': ?instance.echeancesAjustees,
      'avancesImputees': ?instance.avancesImputees,
      'statutsMisAJour': ?instance.statutsMisAJour,
    };
