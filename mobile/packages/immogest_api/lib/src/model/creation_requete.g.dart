// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'creation_requete.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$CreationRequeteCWProxy {
  CreationRequete nom(String nom);

  CreationRequete telephone(String? telephone);

  CreationRequete email(String? email);

  CreationRequete role(CreationRequeteRoleEnum role);

  CreationRequete deuxFacteursActif(bool? deuxFacteursActif);

  CreationRequete motDePasse(String motDePasse);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `CreationRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// CreationRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  CreationRequete call({
    String nom,
    String? telephone,
    String? email,
    CreationRequeteRoleEnum role,
    bool? deuxFacteursActif,
    String motDePasse,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfCreationRequete.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfCreationRequete.copyWith.fieldName(...)`
class _$CreationRequeteCWProxyImpl implements _$CreationRequeteCWProxy {
  const _$CreationRequeteCWProxyImpl(this._value);

  final CreationRequete _value;

  @override
  CreationRequete nom(String nom) => this(nom: nom);

  @override
  CreationRequete telephone(String? telephone) => this(telephone: telephone);

  @override
  CreationRequete email(String? email) => this(email: email);

  @override
  CreationRequete role(CreationRequeteRoleEnum role) => this(role: role);

  @override
  CreationRequete deuxFacteursActif(bool? deuxFacteursActif) =>
      this(deuxFacteursActif: deuxFacteursActif);

  @override
  CreationRequete motDePasse(String motDePasse) => this(motDePasse: motDePasse);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `CreationRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// CreationRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  CreationRequete call({
    Object? nom = const $CopyWithPlaceholder(),
    Object? telephone = const $CopyWithPlaceholder(),
    Object? email = const $CopyWithPlaceholder(),
    Object? role = const $CopyWithPlaceholder(),
    Object? deuxFacteursActif = const $CopyWithPlaceholder(),
    Object? motDePasse = const $CopyWithPlaceholder(),
  }) {
    return CreationRequete(
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
      role: role == const $CopyWithPlaceholder()
          ? _value.role
          // ignore: cast_nullable_to_non_nullable
          : role as CreationRequeteRoleEnum,
      deuxFacteursActif: deuxFacteursActif == const $CopyWithPlaceholder()
          ? _value.deuxFacteursActif
          // ignore: cast_nullable_to_non_nullable
          : deuxFacteursActif as bool?,
      motDePasse: motDePasse == const $CopyWithPlaceholder()
          ? _value.motDePasse
          // ignore: cast_nullable_to_non_nullable
          : motDePasse as String,
    );
  }
}

extension $CreationRequeteCopyWith on CreationRequete {
  /// Returns a callable class that can be used as follows: `instanceOfCreationRequete.copyWith(...)` or like so:`instanceOfCreationRequete.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$CreationRequeteCWProxy get copyWith => _$CreationRequeteCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

CreationRequete _$CreationRequeteFromJson(Map<String, dynamic> json) =>
    $checkedCreate('CreationRequete', json, ($checkedConvert) {
      $checkKeys(json, requiredKeys: const ['nom', 'role', 'motDePasse']);
      final val = CreationRequete(
        nom: $checkedConvert('nom', (v) => v as String),
        telephone: $checkedConvert('telephone', (v) => v as String?),
        email: $checkedConvert('email', (v) => v as String?),
        role: $checkedConvert(
          'role',
          (v) => $enumDecode(_$CreationRequeteRoleEnumEnumMap, v),
        ),
        deuxFacteursActif: $checkedConvert(
          'deuxFacteursActif',
          (v) => v as bool?,
        ),
        motDePasse: $checkedConvert('motDePasse', (v) => v as String),
      );
      return val;
    });

Map<String, dynamic> _$CreationRequeteToJson(CreationRequete instance) =>
    <String, dynamic>{
      'nom': instance.nom,
      'telephone': ?instance.telephone,
      'email': ?instance.email,
      'role': _$CreationRequeteRoleEnumEnumMap[instance.role]!,
      'deuxFacteursActif': ?instance.deuxFacteursActif,
      'motDePasse': instance.motDePasse,
    };

const _$CreationRequeteRoleEnumEnumMap = {
  CreationRequeteRoleEnum.ADMIN: 'ADMIN',
  CreationRequeteRoleEnum.GESTIONNAIRE: 'GESTIONNAIRE',
  CreationRequeteRoleEnum.PROPRIETAIRE_TIERS: 'PROPRIETAIRE_TIERS',
  CreationRequeteRoleEnum.LOCATAIRE: 'LOCATAIRE',
  CreationRequeteRoleEnum.PRESTATAIRE: 'PRESTATAIRE',
};
