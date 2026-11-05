// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'bail_compte.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$BailCompteCWProxy {
  BailCompte bailId(String? bailId);

  BailCompte numero(String? numero);

  BailCompte statut(String? statut);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `BailCompte(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// BailCompte(...).copyWith(id: 12, name: "My name")
  /// ````
  BailCompte call({String? bailId, String? numero, String? statut});
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfBailCompte.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfBailCompte.copyWith.fieldName(...)`
class _$BailCompteCWProxyImpl implements _$BailCompteCWProxy {
  const _$BailCompteCWProxyImpl(this._value);

  final BailCompte _value;

  @override
  BailCompte bailId(String? bailId) => this(bailId: bailId);

  @override
  BailCompte numero(String? numero) => this(numero: numero);

  @override
  BailCompte statut(String? statut) => this(statut: statut);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `BailCompte(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// BailCompte(...).copyWith(id: 12, name: "My name")
  /// ````
  BailCompte call({
    Object? bailId = const $CopyWithPlaceholder(),
    Object? numero = const $CopyWithPlaceholder(),
    Object? statut = const $CopyWithPlaceholder(),
  }) {
    return BailCompte(
      bailId: bailId == const $CopyWithPlaceholder()
          ? _value.bailId
          // ignore: cast_nullable_to_non_nullable
          : bailId as String?,
      numero: numero == const $CopyWithPlaceholder()
          ? _value.numero
          // ignore: cast_nullable_to_non_nullable
          : numero as String?,
      statut: statut == const $CopyWithPlaceholder()
          ? _value.statut
          // ignore: cast_nullable_to_non_nullable
          : statut as String?,
    );
  }
}

extension $BailCompteCopyWith on BailCompte {
  /// Returns a callable class that can be used as follows: `instanceOfBailCompte.copyWith(...)` or like so:`instanceOfBailCompte.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$BailCompteCWProxy get copyWith => _$BailCompteCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

BailCompte _$BailCompteFromJson(Map<String, dynamic> json) =>
    $checkedCreate('BailCompte', json, ($checkedConvert) {
      final val = BailCompte(
        bailId: $checkedConvert('bailId', (v) => v as String?),
        numero: $checkedConvert('numero', (v) => v as String?),
        statut: $checkedConvert('statut', (v) => v as String?),
      );
      return val;
    });

Map<String, dynamic> _$BailCompteToJson(BailCompte instance) =>
    <String, dynamic>{
      'bailId': ?instance.bailId,
      'numero': ?instance.numero,
      'statut': ?instance.statut,
    };
