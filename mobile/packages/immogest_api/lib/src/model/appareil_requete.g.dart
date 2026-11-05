// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'appareil_requete.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$AppareilRequeteCWProxy {
  AppareilRequete token(String token);

  AppareilRequete plateforme(String plateforme);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `AppareilRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// AppareilRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  AppareilRequete call({String token, String plateforme});
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfAppareilRequete.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfAppareilRequete.copyWith.fieldName(...)`
class _$AppareilRequeteCWProxyImpl implements _$AppareilRequeteCWProxy {
  const _$AppareilRequeteCWProxyImpl(this._value);

  final AppareilRequete _value;

  @override
  AppareilRequete token(String token) => this(token: token);

  @override
  AppareilRequete plateforme(String plateforme) => this(plateforme: plateforme);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `AppareilRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// AppareilRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  AppareilRequete call({
    Object? token = const $CopyWithPlaceholder(),
    Object? plateforme = const $CopyWithPlaceholder(),
  }) {
    return AppareilRequete(
      token: token == const $CopyWithPlaceholder()
          ? _value.token
          // ignore: cast_nullable_to_non_nullable
          : token as String,
      plateforme: plateforme == const $CopyWithPlaceholder()
          ? _value.plateforme
          // ignore: cast_nullable_to_non_nullable
          : plateforme as String,
    );
  }
}

extension $AppareilRequeteCopyWith on AppareilRequete {
  /// Returns a callable class that can be used as follows: `instanceOfAppareilRequete.copyWith(...)` or like so:`instanceOfAppareilRequete.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$AppareilRequeteCWProxy get copyWith => _$AppareilRequeteCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

AppareilRequete _$AppareilRequeteFromJson(Map<String, dynamic> json) =>
    $checkedCreate('AppareilRequete', json, ($checkedConvert) {
      $checkKeys(json, requiredKeys: const ['token', 'plateforme']);
      final val = AppareilRequete(
        token: $checkedConvert('token', (v) => v as String),
        plateforme: $checkedConvert('plateforme', (v) => v as String),
      );
      return val;
    });

Map<String, dynamic> _$AppareilRequeteToJson(AppareilRequete instance) =>
    <String, dynamic>{
      'token': instance.token,
      'plateforme': instance.plateforme,
    };
