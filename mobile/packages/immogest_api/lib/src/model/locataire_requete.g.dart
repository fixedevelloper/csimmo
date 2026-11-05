// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'locataire_requete.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$LocataireRequeteCWProxy {
  LocataireRequete nom(String nom);

  LocataireRequete profession(String? profession);

  LocataireRequete telephone(String? telephone);

  LocataireRequete email(String? email);

  LocataireRequete adresse(String? adresse);

  LocataireRequete dateNaissance(DateTime? dateNaissance);

  LocataireRequete numeroCni(String? numeroCni);

  LocataireRequete utilisateurId(String? utilisateurId);

  LocataireRequete notes(String? notes);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `LocataireRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// LocataireRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  LocataireRequete call({
    String nom,
    String? profession,
    String? telephone,
    String? email,
    String? adresse,
    DateTime? dateNaissance,
    String? numeroCni,
    String? utilisateurId,
    String? notes,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfLocataireRequete.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfLocataireRequete.copyWith.fieldName(...)`
class _$LocataireRequeteCWProxyImpl implements _$LocataireRequeteCWProxy {
  const _$LocataireRequeteCWProxyImpl(this._value);

  final LocataireRequete _value;

  @override
  LocataireRequete nom(String nom) => this(nom: nom);

  @override
  LocataireRequete profession(String? profession) =>
      this(profession: profession);

  @override
  LocataireRequete telephone(String? telephone) => this(telephone: telephone);

  @override
  LocataireRequete email(String? email) => this(email: email);

  @override
  LocataireRequete adresse(String? adresse) => this(adresse: adresse);

  @override
  LocataireRequete dateNaissance(DateTime? dateNaissance) =>
      this(dateNaissance: dateNaissance);

  @override
  LocataireRequete numeroCni(String? numeroCni) => this(numeroCni: numeroCni);

  @override
  LocataireRequete utilisateurId(String? utilisateurId) =>
      this(utilisateurId: utilisateurId);

  @override
  LocataireRequete notes(String? notes) => this(notes: notes);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `LocataireRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// LocataireRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  LocataireRequete call({
    Object? nom = const $CopyWithPlaceholder(),
    Object? profession = const $CopyWithPlaceholder(),
    Object? telephone = const $CopyWithPlaceholder(),
    Object? email = const $CopyWithPlaceholder(),
    Object? adresse = const $CopyWithPlaceholder(),
    Object? dateNaissance = const $CopyWithPlaceholder(),
    Object? numeroCni = const $CopyWithPlaceholder(),
    Object? utilisateurId = const $CopyWithPlaceholder(),
    Object? notes = const $CopyWithPlaceholder(),
  }) {
    return LocataireRequete(
      nom: nom == const $CopyWithPlaceholder()
          ? _value.nom
          // ignore: cast_nullable_to_non_nullable
          : nom as String,
      profession: profession == const $CopyWithPlaceholder()
          ? _value.profession
          // ignore: cast_nullable_to_non_nullable
          : profession as String?,
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
      dateNaissance: dateNaissance == const $CopyWithPlaceholder()
          ? _value.dateNaissance
          // ignore: cast_nullable_to_non_nullable
          : dateNaissance as DateTime?,
      numeroCni: numeroCni == const $CopyWithPlaceholder()
          ? _value.numeroCni
          // ignore: cast_nullable_to_non_nullable
          : numeroCni as String?,
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

extension $LocataireRequeteCopyWith on LocataireRequete {
  /// Returns a callable class that can be used as follows: `instanceOfLocataireRequete.copyWith(...)` or like so:`instanceOfLocataireRequete.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$LocataireRequeteCWProxy get copyWith => _$LocataireRequeteCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

LocataireRequete _$LocataireRequeteFromJson(Map<String, dynamic> json) =>
    $checkedCreate('LocataireRequete', json, ($checkedConvert) {
      $checkKeys(json, requiredKeys: const ['nom']);
      final val = LocataireRequete(
        nom: $checkedConvert('nom', (v) => v as String),
        profession: $checkedConvert('profession', (v) => v as String?),
        telephone: $checkedConvert('telephone', (v) => v as String?),
        email: $checkedConvert('email', (v) => v as String?),
        adresse: $checkedConvert('adresse', (v) => v as String?),
        dateNaissance: $checkedConvert(
          'dateNaissance',
          (v) => v == null ? null : DateTime.parse(v as String),
        ),
        numeroCni: $checkedConvert('numeroCni', (v) => v as String?),
        utilisateurId: $checkedConvert('utilisateurId', (v) => v as String?),
        notes: $checkedConvert('notes', (v) => v as String?),
      );
      return val;
    });

Map<String, dynamic> _$LocataireRequeteToJson(LocataireRequete instance) =>
    <String, dynamic>{
      'nom': instance.nom,
      'profession': ?instance.profession,
      'telephone': ?instance.telephone,
      'email': ?instance.email,
      'adresse': ?instance.adresse,
      'dateNaissance': ?instance.dateNaissance?.toIso8601String(),
      'numeroCni': ?instance.numeroCni,
      'utilisateurId': ?instance.utilisateurId,
      'notes': ?instance.notes,
    };
