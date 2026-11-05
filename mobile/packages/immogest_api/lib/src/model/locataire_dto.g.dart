// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'locataire_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$LocataireDtoCWProxy {
  LocataireDto id(String? id);

  LocataireDto nom(String? nom);

  LocataireDto profession(String? profession);

  LocataireDto telephone(String? telephone);

  LocataireDto email(String? email);

  LocataireDto adresse(String? adresse);

  LocataireDto dateNaissance(DateTime? dateNaissance);

  LocataireDto numeroCni(String? numeroCni);

  LocataireDto utilisateurId(String? utilisateurId);

  LocataireDto notes(String? notes);

  LocataireDto archive(bool? archive);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `LocataireDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// LocataireDto(...).copyWith(id: 12, name: "My name")
  /// ````
  LocataireDto call({
    String? id,
    String? nom,
    String? profession,
    String? telephone,
    String? email,
    String? adresse,
    DateTime? dateNaissance,
    String? numeroCni,
    String? utilisateurId,
    String? notes,
    bool? archive,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfLocataireDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfLocataireDto.copyWith.fieldName(...)`
class _$LocataireDtoCWProxyImpl implements _$LocataireDtoCWProxy {
  const _$LocataireDtoCWProxyImpl(this._value);

  final LocataireDto _value;

  @override
  LocataireDto id(String? id) => this(id: id);

  @override
  LocataireDto nom(String? nom) => this(nom: nom);

  @override
  LocataireDto profession(String? profession) => this(profession: profession);

  @override
  LocataireDto telephone(String? telephone) => this(telephone: telephone);

  @override
  LocataireDto email(String? email) => this(email: email);

  @override
  LocataireDto adresse(String? adresse) => this(adresse: adresse);

  @override
  LocataireDto dateNaissance(DateTime? dateNaissance) =>
      this(dateNaissance: dateNaissance);

  @override
  LocataireDto numeroCni(String? numeroCni) => this(numeroCni: numeroCni);

  @override
  LocataireDto utilisateurId(String? utilisateurId) =>
      this(utilisateurId: utilisateurId);

  @override
  LocataireDto notes(String? notes) => this(notes: notes);

  @override
  LocataireDto archive(bool? archive) => this(archive: archive);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `LocataireDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// LocataireDto(...).copyWith(id: 12, name: "My name")
  /// ````
  LocataireDto call({
    Object? id = const $CopyWithPlaceholder(),
    Object? nom = const $CopyWithPlaceholder(),
    Object? profession = const $CopyWithPlaceholder(),
    Object? telephone = const $CopyWithPlaceholder(),
    Object? email = const $CopyWithPlaceholder(),
    Object? adresse = const $CopyWithPlaceholder(),
    Object? dateNaissance = const $CopyWithPlaceholder(),
    Object? numeroCni = const $CopyWithPlaceholder(),
    Object? utilisateurId = const $CopyWithPlaceholder(),
    Object? notes = const $CopyWithPlaceholder(),
    Object? archive = const $CopyWithPlaceholder(),
  }) {
    return LocataireDto(
      id: id == const $CopyWithPlaceholder()
          ? _value.id
          // ignore: cast_nullable_to_non_nullable
          : id as String?,
      nom: nom == const $CopyWithPlaceholder()
          ? _value.nom
          // ignore: cast_nullable_to_non_nullable
          : nom as String?,
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
      archive: archive == const $CopyWithPlaceholder()
          ? _value.archive
          // ignore: cast_nullable_to_non_nullable
          : archive as bool?,
    );
  }
}

extension $LocataireDtoCopyWith on LocataireDto {
  /// Returns a callable class that can be used as follows: `instanceOfLocataireDto.copyWith(...)` or like so:`instanceOfLocataireDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$LocataireDtoCWProxy get copyWith => _$LocataireDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

LocataireDto _$LocataireDtoFromJson(Map<String, dynamic> json) =>
    $checkedCreate('LocataireDto', json, ($checkedConvert) {
      final val = LocataireDto(
        id: $checkedConvert('id', (v) => v as String?),
        nom: $checkedConvert('nom', (v) => v as String?),
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
        archive: $checkedConvert('archive', (v) => v as bool?),
      );
      return val;
    });

Map<String, dynamic> _$LocataireDtoToJson(LocataireDto instance) =>
    <String, dynamic>{
      'id': ?instance.id,
      'nom': ?instance.nom,
      'profession': ?instance.profession,
      'telephone': ?instance.telephone,
      'email': ?instance.email,
      'adresse': ?instance.adresse,
      'dateNaissance': ?instance.dateNaissance?.toIso8601String(),
      'numeroCni': ?instance.numeroCni,
      'utilisateurId': ?instance.utilisateurId,
      'notes': ?instance.notes,
      'archive': ?instance.archive,
    };
