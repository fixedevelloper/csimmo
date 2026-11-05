// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'administrateur.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$AdministrateurCWProxy {
  Administrateur nom(String nom);

  Administrateur telephone(String? telephone);

  Administrateur email(String? email);

  Administrateur motDePasse(String motDePasse);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `Administrateur(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// Administrateur(...).copyWith(id: 12, name: "My name")
  /// ````
  Administrateur call({
    String nom,
    String? telephone,
    String? email,
    String motDePasse,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfAdministrateur.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfAdministrateur.copyWith.fieldName(...)`
class _$AdministrateurCWProxyImpl implements _$AdministrateurCWProxy {
  const _$AdministrateurCWProxyImpl(this._value);

  final Administrateur _value;

  @override
  Administrateur nom(String nom) => this(nom: nom);

  @override
  Administrateur telephone(String? telephone) => this(telephone: telephone);

  @override
  Administrateur email(String? email) => this(email: email);

  @override
  Administrateur motDePasse(String motDePasse) => this(motDePasse: motDePasse);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `Administrateur(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// Administrateur(...).copyWith(id: 12, name: "My name")
  /// ````
  Administrateur call({
    Object? nom = const $CopyWithPlaceholder(),
    Object? telephone = const $CopyWithPlaceholder(),
    Object? email = const $CopyWithPlaceholder(),
    Object? motDePasse = const $CopyWithPlaceholder(),
  }) {
    return Administrateur(
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

extension $AdministrateurCopyWith on Administrateur {
  /// Returns a callable class that can be used as follows: `instanceOfAdministrateur.copyWith(...)` or like so:`instanceOfAdministrateur.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$AdministrateurCWProxy get copyWith => _$AdministrateurCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

Administrateur _$AdministrateurFromJson(Map<String, dynamic> json) =>
    $checkedCreate('Administrateur', json, ($checkedConvert) {
      $checkKeys(json, requiredKeys: const ['nom', 'motDePasse']);
      final val = Administrateur(
        nom: $checkedConvert('nom', (v) => v as String),
        telephone: $checkedConvert('telephone', (v) => v as String?),
        email: $checkedConvert('email', (v) => v as String?),
        motDePasse: $checkedConvert('motDePasse', (v) => v as String),
      );
      return val;
    });

Map<String, dynamic> _$AdministrateurToJson(Administrateur instance) =>
    <String, dynamic>{
      'nom': instance.nom,
      'telephone': ?instance.telephone,
      'email': ?instance.email,
      'motDePasse': instance.motDePasse,
    };
