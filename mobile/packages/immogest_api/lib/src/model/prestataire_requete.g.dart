// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'prestataire_requete.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$PrestataireRequeteCWProxy {
  PrestataireRequete nom(String nom);

  PrestataireRequete metier(String? metier);

  PrestataireRequete telephone(String? telephone);

  PrestataireRequete email(String? email);

  PrestataireRequete adresse(String? adresse);

  PrestataireRequete utilisateurId(String? utilisateurId);

  PrestataireRequete notes(String? notes);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PrestataireRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PrestataireRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  PrestataireRequete call({
    String nom,
    String? metier,
    String? telephone,
    String? email,
    String? adresse,
    String? utilisateurId,
    String? notes,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfPrestataireRequete.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfPrestataireRequete.copyWith.fieldName(...)`
class _$PrestataireRequeteCWProxyImpl implements _$PrestataireRequeteCWProxy {
  const _$PrestataireRequeteCWProxyImpl(this._value);

  final PrestataireRequete _value;

  @override
  PrestataireRequete nom(String nom) => this(nom: nom);

  @override
  PrestataireRequete metier(String? metier) => this(metier: metier);

  @override
  PrestataireRequete telephone(String? telephone) => this(telephone: telephone);

  @override
  PrestataireRequete email(String? email) => this(email: email);

  @override
  PrestataireRequete adresse(String? adresse) => this(adresse: adresse);

  @override
  PrestataireRequete utilisateurId(String? utilisateurId) =>
      this(utilisateurId: utilisateurId);

  @override
  PrestataireRequete notes(String? notes) => this(notes: notes);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PrestataireRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PrestataireRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  PrestataireRequete call({
    Object? nom = const $CopyWithPlaceholder(),
    Object? metier = const $CopyWithPlaceholder(),
    Object? telephone = const $CopyWithPlaceholder(),
    Object? email = const $CopyWithPlaceholder(),
    Object? adresse = const $CopyWithPlaceholder(),
    Object? utilisateurId = const $CopyWithPlaceholder(),
    Object? notes = const $CopyWithPlaceholder(),
  }) {
    return PrestataireRequete(
      nom: nom == const $CopyWithPlaceholder()
          ? _value.nom
          // ignore: cast_nullable_to_non_nullable
          : nom as String,
      metier: metier == const $CopyWithPlaceholder()
          ? _value.metier
          // ignore: cast_nullable_to_non_nullable
          : metier as String?,
      telephone: telephone == const $CopyWithPlaceholder()
          ? _value.telephone
          // ignore: cast_nullable_to_non_nullable
          : telephone as String?,
      email: email == const $CopyWithPlaceholder()
          ? _value.email
          // ignore: cast_nullable_to_non_nullable
          : email as String?,
      adresse: adresse == const $CopyWithPlaceholder()
          ? _value.adresse
          // ignore: cast_nullable_to_non_nullable
          : adresse as String?,
      utilisateurId: utilisateurId == const $CopyWithPlaceholder()
          ? _value.utilisateurId
          // ignore: cast_nullable_to_non_nullable
          : utilisateurId as String?,
      notes: notes == const $CopyWithPlaceholder()
          ? _value.notes
          // ignore: cast_nullable_to_non_nullable
          : notes as String?,
    );
  }
}

extension $PrestataireRequeteCopyWith on PrestataireRequete {
  /// Returns a callable class that can be used as follows: `instanceOfPrestataireRequete.copyWith(...)` or like so:`instanceOfPrestataireRequete.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$PrestataireRequeteCWProxy get copyWith =>
      _$PrestataireRequeteCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

PrestataireRequete _$PrestataireRequeteFromJson(Map<String, dynamic> json) =>
    $checkedCreate('PrestataireRequete', json, ($checkedConvert) {
      $checkKeys(json, requiredKeys: const ['nom']);
      final val = PrestataireRequete(
        nom: $checkedConvert('nom', (v) => v as String),
        metier: $checkedConvert('metier', (v) => v as String?),
        telephone: $checkedConvert('telephone', (v) => v as String?),
        email: $checkedConvert('email', (v) => v as String?),
        adresse: $checkedConvert('adresse', (v) => v as String?),
        utilisateurId: $checkedConvert('utilisateurId', (v) => v as String?),
        notes: $checkedConvert('notes', (v) => v as String?),
      );
      return val;
    });

Map<String, dynamic> _$PrestataireRequeteToJson(PrestataireRequete instance) =>
    <String, dynamic>{
      'nom': instance.nom,
      'metier': ?instance.metier,
      'telephone': ?instance.telephone,
      'email': ?instance.email,
      'adresse': ?instance.adresse,
      'utilisateurId': ?instance.utilisateurId,
      'notes': ?instance.notes,
    };
