// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'modification_requete.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$ModificationRequeteCWProxy {
  ModificationRequete nom(String nom);

  ModificationRequete telephone(String? telephone);

  ModificationRequete email(String? email);

  ModificationRequete role(ModificationRequeteRoleEnum role);

  ModificationRequete actif(bool? actif);

  ModificationRequete deuxFacteursActif(bool? deuxFacteursActif);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ModificationRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ModificationRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  ModificationRequete call({
    String nom,
    String? telephone,
    String? email,
    ModificationRequeteRoleEnum role,
    bool? actif,
    bool? deuxFacteursActif,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfModificationRequete.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfModificationRequete.copyWith.fieldName(...)`
class _$ModificationRequeteCWProxyImpl implements _$ModificationRequeteCWProxy {
  const _$ModificationRequeteCWProxyImpl(this._value);

  final ModificationRequete _value;

  @override
  ModificationRequete nom(String nom) => this(nom: nom);

  @override
  ModificationRequete telephone(String? telephone) =>
      this(telephone: telephone);

  @override
  ModificationRequete email(String? email) => this(email: email);

  @override
  ModificationRequete role(ModificationRequeteRoleEnum role) =>
      this(role: role);

  @override
  ModificationRequete actif(bool? actif) => this(actif: actif);

  @override
  ModificationRequete deuxFacteursActif(bool? deuxFacteursActif) =>
      this(deuxFacteursActif: deuxFacteursActif);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ModificationRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ModificationRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  ModificationRequete call({
    Object? nom = const $CopyWithPlaceholder(),
    Object? telephone = const $CopyWithPlaceholder(),
    Object? email = const $CopyWithPlaceholder(),
    Object? role = const $CopyWithPlaceholder(),
    Object? actif = const $CopyWithPlaceholder(),
    Object? deuxFacteursActif = const $CopyWithPlaceholder(),
  }) {
    return ModificationRequete(
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
          : role as ModificationRequeteRoleEnum,
      actif: actif == const $CopyWithPlaceholder()
          ? _value.actif
          // ignore: cast_nullable_to_non_nullable
          : actif as bool?,
      deuxFacteursActif: deuxFacteursActif == const $CopyWithPlaceholder()
          ? _value.deuxFacteursActif
          // ignore: cast_nullable_to_non_nullable
          : deuxFacteursActif as bool?,
    );
  }
}

extension $ModificationRequeteCopyWith on ModificationRequete {
  /// Returns a callable class that can be used as follows: `instanceOfModificationRequete.copyWith(...)` or like so:`instanceOfModificationRequete.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$ModificationRequeteCWProxy get copyWith =>
      _$ModificationRequeteCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

ModificationRequete _$ModificationRequeteFromJson(Map<String, dynamic> json) =>
    $checkedCreate('ModificationRequete', json, ($checkedConvert) {
      $checkKeys(json, requiredKeys: const ['nom', 'role']);
      final val = ModificationRequete(
        nom: $checkedConvert('nom', (v) => v as String),
        telephone: $checkedConvert('telephone', (v) => v as String?),
        email: $checkedConvert('email', (v) => v as String?),
        role: $checkedConvert(
          'role',
          (v) => $enumDecode(_$ModificationRequeteRoleEnumEnumMap, v),
        ),
        actif: $checkedConvert('actif', (v) => v as bool?),
        deuxFacteursActif: $checkedConvert(
          'deuxFacteursActif',
          (v) => v as bool?,
        ),
      );
      return val;
    });

Map<String, dynamic> _$ModificationRequeteToJson(
  ModificationRequete instance,
) => <String, dynamic>{
  'nom': instance.nom,
  'telephone': ?instance.telephone,
  'email': ?instance.email,
  'role': _$ModificationRequeteRoleEnumEnumMap[instance.role]!,
  'actif': ?instance.actif,
  'deuxFacteursActif': ?instance.deuxFacteursActif,
};

const _$ModificationRequeteRoleEnumEnumMap = {
  ModificationRequeteRoleEnum.ADMIN: 'ADMIN',
  ModificationRequeteRoleEnum.GESTIONNAIRE: 'GESTIONNAIRE',
  ModificationRequeteRoleEnum.PROPRIETAIRE_TIERS: 'PROPRIETAIRE_TIERS',
  ModificationRequeteRoleEnum.LOCATAIRE: 'LOCATAIRE',
  ModificationRequeteRoleEnum.PRESTATAIRE: 'PRESTATAIRE',
  ModificationRequeteRoleEnum.ENCAISSEUR: 'ENCAISSEUR',
  ModificationRequeteRoleEnum.GESTIONNAIRE_BIENS: 'GESTIONNAIRE_BIENS',
  ModificationRequeteRoleEnum.COMPTABLE: 'COMPTABLE',
};
