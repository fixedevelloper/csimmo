// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'connexion_requete.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$ConnexionRequeteCWProxy {
  ConnexionRequete identifiant(String identifiant);

  ConnexionRequete motDePasse(String motDePasse);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ConnexionRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ConnexionRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  ConnexionRequete call({String identifiant, String motDePasse});
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfConnexionRequete.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfConnexionRequete.copyWith.fieldName(...)`
class _$ConnexionRequeteCWProxyImpl implements _$ConnexionRequeteCWProxy {
  const _$ConnexionRequeteCWProxyImpl(this._value);

  final ConnexionRequete _value;

  @override
  ConnexionRequete identifiant(String identifiant) =>
      this(identifiant: identifiant);

  @override
  ConnexionRequete motDePasse(String motDePasse) =>
      this(motDePasse: motDePasse);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ConnexionRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ConnexionRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  ConnexionRequete call({
    Object? identifiant = const $CopyWithPlaceholder(),
    Object? motDePasse = const $CopyWithPlaceholder(),
  }) {
    return ConnexionRequete(
      identifiant: identifiant == const $CopyWithPlaceholder()
          ? _value.identifiant
          // ignore: cast_nullable_to_non_nullable
          : identifiant as String,
      motDePasse: motDePasse == const $CopyWithPlaceholder()
          ? _value.motDePasse
          // ignore: cast_nullable_to_non_nullable
          : motDePasse as String,
    );
  }
}

extension $ConnexionRequeteCopyWith on ConnexionRequete {
  /// Returns a callable class that can be used as follows: `instanceOfConnexionRequete.copyWith(...)` or like so:`instanceOfConnexionRequete.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$ConnexionRequeteCWProxy get copyWith => _$ConnexionRequeteCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

ConnexionRequete _$ConnexionRequeteFromJson(Map<String, dynamic> json) =>
    $checkedCreate('ConnexionRequete', json, ($checkedConvert) {
      $checkKeys(json, requiredKeys: const ['identifiant', 'motDePasse']);
      final val = ConnexionRequete(
        identifiant: $checkedConvert('identifiant', (v) => v as String),
        motDePasse: $checkedConvert('motDePasse', (v) => v as String),
      );
      return val;
    });

Map<String, dynamic> _$ConnexionRequeteToJson(ConnexionRequete instance) =>
    <String, dynamic>{
      'identifiant': instance.identifiant,
      'motDePasse': instance.motDePasse,
    };
