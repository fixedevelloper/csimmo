// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'desactivation_requete.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$DesactivationRequeteCWProxy {
  DesactivationRequete motDePasse(String motDePasse);

  DesactivationRequete code(String? code);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `DesactivationRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// DesactivationRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  DesactivationRequete call({String motDePasse, String? code});
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfDesactivationRequete.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfDesactivationRequete.copyWith.fieldName(...)`
class _$DesactivationRequeteCWProxyImpl
    implements _$DesactivationRequeteCWProxy {
  const _$DesactivationRequeteCWProxyImpl(this._value);

  final DesactivationRequete _value;

  @override
  DesactivationRequete motDePasse(String motDePasse) =>
      this(motDePasse: motDePasse);

  @override
  DesactivationRequete code(String? code) => this(code: code);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `DesactivationRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// DesactivationRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  DesactivationRequete call({
    Object? motDePasse = const $CopyWithPlaceholder(),
    Object? code = const $CopyWithPlaceholder(),
  }) {
    return DesactivationRequete(
      motDePasse: motDePasse == const $CopyWithPlaceholder()
          ? _value.motDePasse
          // ignore: cast_nullable_to_non_nullable
          : motDePasse as String,
      code: code == const $CopyWithPlaceholder()
          ? _value.code
          // ignore: cast_nullable_to_non_nullable
          : code as String?,
    );
  }
}

extension $DesactivationRequeteCopyWith on DesactivationRequete {
  /// Returns a callable class that can be used as follows: `instanceOfDesactivationRequete.copyWith(...)` or like so:`instanceOfDesactivationRequete.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$DesactivationRequeteCWProxy get copyWith =>
      _$DesactivationRequeteCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

DesactivationRequete _$DesactivationRequeteFromJson(
  Map<String, dynamic> json,
) => $checkedCreate('DesactivationRequete', json, ($checkedConvert) {
  $checkKeys(json, requiredKeys: const ['motDePasse']);
  final val = DesactivationRequete(
    motDePasse: $checkedConvert('motDePasse', (v) => v as String),
    code: $checkedConvert('code', (v) => v as String?),
  );
  return val;
});

Map<String, dynamic> _$DesactivationRequeteToJson(
  DesactivationRequete instance,
) => <String, dynamic>{
  'motDePasse': instance.motDePasse,
  'code': ?instance.code,
};
