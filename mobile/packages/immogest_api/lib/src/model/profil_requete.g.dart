// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'profil_requete.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$ProfilRequeteCWProxy {
  ProfilRequete nom(String nom);

  ProfilRequete telephone(String? telephone);

  ProfilRequete email(String? email);

  ProfilRequete motDePasse(String motDePasse);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ProfilRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ProfilRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  ProfilRequete call({
    String nom,
    String? telephone,
    String? email,
    String motDePasse,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfProfilRequete.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfProfilRequete.copyWith.fieldName(...)`
class _$ProfilRequeteCWProxyImpl implements _$ProfilRequeteCWProxy {
  const _$ProfilRequeteCWProxyImpl(this._value);

  final ProfilRequete _value;

  @override
  ProfilRequete nom(String nom) => this(nom: nom);

  @override
  ProfilRequete telephone(String? telephone) => this(telephone: telephone);

  @override
  ProfilRequete email(String? email) => this(email: email);

  @override
  ProfilRequete motDePasse(String motDePasse) => this(motDePasse: motDePasse);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ProfilRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ProfilRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  ProfilRequete call({
    Object? nom = const $CopyWithPlaceholder(),
    Object? telephone = const $CopyWithPlaceholder(),
    Object? email = const $CopyWithPlaceholder(),
    Object? motDePasse = const $CopyWithPlaceholder(),
  }) {
    return ProfilRequete(
      nom: nom == const $CopyWithPlaceholder()
          ? _value.nom
          // ignore: cast_nullable_to_non_nullable
          : nom as String,
      telephone: telephone == const $CopyWithPlaceholder()
          ? _value.telephone
          // ignore: cast_nullable_to_non_nullable
          : telephone as String?,
      email: email == const $CopyWithPlaceholder()
          ? _value.email
          // ignore: cast_nullable_to_non_nullable
          : email as String?,
      motDePasse: motDePasse == const $CopyWithPlaceholder()
          ? _value.motDePasse
          // ignore: cast_nullable_to_non_nullable
          : motDePasse as String,
    );
  }
}

extension $ProfilRequeteCopyWith on ProfilRequete {
  /// Returns a callable class that can be used as follows: `instanceOfProfilRequete.copyWith(...)` or like so:`instanceOfProfilRequete.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$ProfilRequeteCWProxy get copyWith => _$ProfilRequeteCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

ProfilRequete _$ProfilRequeteFromJson(Map<String, dynamic> json) =>
    $checkedCreate('ProfilRequete', json, ($checkedConvert) {
      $checkKeys(json, requiredKeys: const ['nom', 'motDePasse']);
      final val = ProfilRequete(
        nom: $checkedConvert('nom', (v) => v as String),
        telephone: $checkedConvert('telephone', (v) => v as String?),
        email: $checkedConvert('email', (v) => v as String?),
        motDePasse: $checkedConvert('motDePasse', (v) => v as String),
      );
      return val;
    });

Map<String, dynamic> _$ProfilRequeteToJson(ProfilRequete instance) =>
    <String, dynamic>{
      'nom': instance.nom,
      'telephone': ?instance.telephone,
      'email': ?instance.email,
      'motDePasse': instance.motDePasse,
    };
