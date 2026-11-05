// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'resultat_test.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$ResultatTestCWProxy {
  ResultatTest succes(bool? succes);

  ResultatTest erreur(String? erreur);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ResultatTest(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ResultatTest(...).copyWith(id: 12, name: "My name")
  /// ````
  ResultatTest call({bool? succes, String? erreur});
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfResultatTest.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfResultatTest.copyWith.fieldName(...)`
class _$ResultatTestCWProxyImpl implements _$ResultatTestCWProxy {
  const _$ResultatTestCWProxyImpl(this._value);

  final ResultatTest _value;

  @override
  ResultatTest succes(bool? succes) => this(succes: succes);

  @override
  ResultatTest erreur(String? erreur) => this(erreur: erreur);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ResultatTest(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ResultatTest(...).copyWith(id: 12, name: "My name")
  /// ````
  ResultatTest call({
    Object? succes = const $CopyWithPlaceholder(),
    Object? erreur = const $CopyWithPlaceholder(),
  }) {
    return ResultatTest(
      succes: succes == const $CopyWithPlaceholder()
          ? _value.succes
          // ignore: cast_nullable_to_non_nullable
          : succes as bool?,
      erreur: erreur == const $CopyWithPlaceholder()
          ? _value.erreur
          // ignore: cast_nullable_to_non_nullable
          : erreur as String?,
    );
  }
}

extension $ResultatTestCopyWith on ResultatTest {
  /// Returns a callable class that can be used as follows: `instanceOfResultatTest.copyWith(...)` or like so:`instanceOfResultatTest.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$ResultatTestCWProxy get copyWith => _$ResultatTestCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

ResultatTest _$ResultatTestFromJson(Map<String, dynamic> json) =>
    $checkedCreate('ResultatTest', json, ($checkedConvert) {
      final val = ResultatTest(
        succes: $checkedConvert('succes', (v) => v as bool?),
        erreur: $checkedConvert('erreur', (v) => v as String?),
      );
      return val;
    });

Map<String, dynamic> _$ResultatTestToJson(ResultatTest instance) =>
    <String, dynamic>{'succes': ?instance.succes, 'erreur': ?instance.erreur};
