// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'mot_de_passe_requete.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$MotDePasseRequeteCWProxy {
  MotDePasseRequete motDePasse(String motDePasse);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `MotDePasseRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// MotDePasseRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  MotDePasseRequete call({String motDePasse});
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfMotDePasseRequete.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfMotDePasseRequete.copyWith.fieldName(...)`
class _$MotDePasseRequeteCWProxyImpl implements _$MotDePasseRequeteCWProxy {
  const _$MotDePasseRequeteCWProxyImpl(this._value);

  final MotDePasseRequete _value;

  @override
  MotDePasseRequete motDePasse(String motDePasse) =>
      this(motDePasse: motDePasse);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `MotDePasseRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// MotDePasseRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  MotDePasseRequete call({Object? motDePasse = const $CopyWithPlaceholder()}) {
    return MotDePasseRequete(
      motDePasse: motDePasse == const $CopyWithPlaceholder()
          ? _value.motDePasse
          // ignore: cast_nullable_to_non_nullable
          : motDePasse as String,
    );
  }
}

extension $MotDePasseRequeteCopyWith on MotDePasseRequete {
  /// Returns a callable class that can be used as follows: `instanceOfMotDePasseRequete.copyWith(...)` or like so:`instanceOfMotDePasseRequete.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$MotDePasseRequeteCWProxy get copyWith =>
      _$MotDePasseRequeteCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

MotDePasseRequete _$MotDePasseRequeteFromJson(Map<String, dynamic> json) =>
    $checkedCreate('MotDePasseRequete', json, ($checkedConvert) {
      $checkKeys(json, requiredKeys: const ['motDePasse']);
      final val = MotDePasseRequete(
        motDePasse: $checkedConvert('motDePasse', (v) => v as String),
      );
      return val;
    });

Map<String, dynamic> _$MotDePasseRequeteToJson(MotDePasseRequete instance) =>
    <String, dynamic>{'motDePasse': instance.motDePasse};
