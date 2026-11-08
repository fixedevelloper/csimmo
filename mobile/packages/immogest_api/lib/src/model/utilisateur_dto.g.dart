// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'utilisateur_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$UtilisateurDtoCWProxy {
  UtilisateurDto id(String? id);

  UtilisateurDto nom(String? nom);

  UtilisateurDto telephone(String? telephone);

  UtilisateurDto email(String? email);

  UtilisateurDto role(UtilisateurDtoRoleEnum? role);

  UtilisateurDto actif(bool? actif);

  UtilisateurDto deuxFacteursActif(bool? deuxFacteursActif);

  UtilisateurDto deuxFacteursConfigure(bool? deuxFacteursConfigure);

  UtilisateurDto verrouille(bool? verrouille);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `UtilisateurDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// UtilisateurDto(...).copyWith(id: 12, name: "My name")
  /// ````
  UtilisateurDto call({
    String? id,
    String? nom,
    String? telephone,
    String? email,
    UtilisateurDtoRoleEnum? role,
    bool? actif,
    bool? deuxFacteursActif,
    bool? deuxFacteursConfigure,
    bool? verrouille,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfUtilisateurDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfUtilisateurDto.copyWith.fieldName(...)`
class _$UtilisateurDtoCWProxyImpl implements _$UtilisateurDtoCWProxy {
  const _$UtilisateurDtoCWProxyImpl(this._value);

  final UtilisateurDto _value;

  @override
  UtilisateurDto id(String? id) => this(id: id);

  @override
  UtilisateurDto nom(String? nom) => this(nom: nom);

  @override
  UtilisateurDto telephone(String? telephone) => this(telephone: telephone);

  @override
  UtilisateurDto email(String? email) => this(email: email);

  @override
  UtilisateurDto role(UtilisateurDtoRoleEnum? role) => this(role: role);

  @override
  UtilisateurDto actif(bool? actif) => this(actif: actif);

  @override
  UtilisateurDto deuxFacteursActif(bool? deuxFacteursActif) =>
      this(deuxFacteursActif: deuxFacteursActif);

  @override
  UtilisateurDto deuxFacteursConfigure(bool? deuxFacteursConfigure) =>
      this(deuxFacteursConfigure: deuxFacteursConfigure);

  @override
  UtilisateurDto verrouille(bool? verrouille) => this(verrouille: verrouille);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `UtilisateurDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// UtilisateurDto(...).copyWith(id: 12, name: "My name")
  /// ````
  UtilisateurDto call({
    Object? id = const $CopyWithPlaceholder(),
    Object? nom = const $CopyWithPlaceholder(),
    Object? telephone = const $CopyWithPlaceholder(),
    Object? email = const $CopyWithPlaceholder(),
    Object? role = const $CopyWithPlaceholder(),
    Object? actif = const $CopyWithPlaceholder(),
    Object? deuxFacteursActif = const $CopyWithPlaceholder(),
    Object? deuxFacteursConfigure = const $CopyWithPlaceholder(),
    Object? verrouille = const $CopyWithPlaceholder(),
  }) {
    return UtilisateurDto(
      id: id == const $CopyWithPlaceholder()
          ? _value.id
          // ignore: cast_nullable_to_non_nullable
          : id as String?,
      nom: nom == const $CopyWithPlaceholder()
          ? _value.nom
          // ignore: cast_nullable_to_non_nullable
          : nom as String?,
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
          : role as UtilisateurDtoRoleEnum?,
      actif: actif == const $CopyWithPlaceholder()
          ? _value.actif
          // ignore: cast_nullable_to_non_nullable
          : actif as bool?,
      deuxFacteursActif: deuxFacteursActif == const $CopyWithPlaceholder()
          ? _value.deuxFacteursActif
          // ignore: cast_nullable_to_non_nullable
          : deuxFacteursActif as bool?,
      deuxFacteursConfigure:
          deuxFacteursConfigure == const $CopyWithPlaceholder()
          ? _value.deuxFacteursConfigure
          // ignore: cast_nullable_to_non_nullable
          : deuxFacteursConfigure as bool?,
      verrouille: verrouille == const $CopyWithPlaceholder()
          ? _value.verrouille
          // ignore: cast_nullable_to_non_nullable
          : verrouille as bool?,
    );
  }
}

extension $UtilisateurDtoCopyWith on UtilisateurDto {
  /// Returns a callable class that can be used as follows: `instanceOfUtilisateurDto.copyWith(...)` or like so:`instanceOfUtilisateurDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$UtilisateurDtoCWProxy get copyWith => _$UtilisateurDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

UtilisateurDto _$UtilisateurDtoFromJson(Map<String, dynamic> json) =>
    $checkedCreate('UtilisateurDto', json, ($checkedConvert) {
      final val = UtilisateurDto(
        id: $checkedConvert('id', (v) => v as String?),
        nom: $checkedConvert('nom', (v) => v as String?),
        telephone: $checkedConvert('telephone', (v) => v as String?),
        email: $checkedConvert('email', (v) => v as String?),
        role: $checkedConvert(
          'role',
          (v) => $enumDecodeNullable(_$UtilisateurDtoRoleEnumEnumMap, v),
        ),
        actif: $checkedConvert('actif', (v) => v as bool?),
        deuxFacteursActif: $checkedConvert(
          'deuxFacteursActif',
          (v) => v as bool?,
        ),
        deuxFacteursConfigure: $checkedConvert(
          'deuxFacteursConfigure',
          (v) => v as bool?,
        ),
        verrouille: $checkedConvert('verrouille', (v) => v as bool?),
      );
      return val;
    });

Map<String, dynamic> _$UtilisateurDtoToJson(UtilisateurDto instance) =>
    <String, dynamic>{
      'id': ?instance.id,
      'nom': ?instance.nom,
      'telephone': ?instance.telephone,
      'email': ?instance.email,
      'role': ?_$UtilisateurDtoRoleEnumEnumMap[instance.role],
      'actif': ?instance.actif,
      'deuxFacteursActif': ?instance.deuxFacteursActif,
      'deuxFacteursConfigure': ?instance.deuxFacteursConfigure,
      'verrouille': ?instance.verrouille,
    };

const _$UtilisateurDtoRoleEnumEnumMap = {
  UtilisateurDtoRoleEnum.ADMIN: 'ADMIN',
  UtilisateurDtoRoleEnum.GESTIONNAIRE: 'GESTIONNAIRE',
  UtilisateurDtoRoleEnum.PROPRIETAIRE_TIERS: 'PROPRIETAIRE_TIERS',
  UtilisateurDtoRoleEnum.LOCATAIRE: 'LOCATAIRE',
  UtilisateurDtoRoleEnum.PRESTATAIRE: 'PRESTATAIRE',
  UtilisateurDtoRoleEnum.ENCAISSEUR: 'ENCAISSEUR',
  UtilisateurDtoRoleEnum.GESTIONNAIRE_BIENS: 'GESTIONNAIRE_BIENS',
  UtilisateurDtoRoleEnum.COMPTABLE: 'COMPTABLE',
};
