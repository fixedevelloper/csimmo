// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'prestataire_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$PrestataireDtoCWProxy {
  PrestataireDto id(String? id);

  PrestataireDto nom(String? nom);

  PrestataireDto metier(String? metier);

  PrestataireDto telephone(String? telephone);

  PrestataireDto email(String? email);

  PrestataireDto adresse(String? adresse);

  PrestataireDto utilisateurId(String? utilisateurId);

  PrestataireDto notes(String? notes);

  PrestataireDto archive(bool? archive);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PrestataireDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PrestataireDto(...).copyWith(id: 12, name: "My name")
  /// ````
  PrestataireDto call({
    String? id,
    String? nom,
    String? metier,
    String? telephone,
    String? email,
    String? adresse,
    String? utilisateurId,
    String? notes,
    bool? archive,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfPrestataireDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfPrestataireDto.copyWith.fieldName(...)`
class _$PrestataireDtoCWProxyImpl implements _$PrestataireDtoCWProxy {
  const _$PrestataireDtoCWProxyImpl(this._value);

  final PrestataireDto _value;

  @override
  PrestataireDto id(String? id) => this(id: id);

  @override
  PrestataireDto nom(String? nom) => this(nom: nom);

  @override
  PrestataireDto metier(String? metier) => this(metier: metier);

  @override
  PrestataireDto telephone(String? telephone) => this(telephone: telephone);

  @override
  PrestataireDto email(String? email) => this(email: email);

  @override
  PrestataireDto adresse(String? adresse) => this(adresse: adresse);

  @override
  PrestataireDto utilisateurId(String? utilisateurId) =>
      this(utilisateurId: utilisateurId);

  @override
  PrestataireDto notes(String? notes) => this(notes: notes);

  @override
  PrestataireDto archive(bool? archive) => this(archive: archive);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PrestataireDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PrestataireDto(...).copyWith(id: 12, name: "My name")
  /// ````
  PrestataireDto call({
    Object? id = const $CopyWithPlaceholder(),
    Object? nom = const $CopyWithPlaceholder(),
    Object? metier = const $CopyWithPlaceholder(),
    Object? telephone = const $CopyWithPlaceholder(),
    Object? email = const $CopyWithPlaceholder(),
    Object? adresse = const $CopyWithPlaceholder(),
    Object? utilisateurId = const $CopyWithPlaceholder(),
    Object? notes = const $CopyWithPlaceholder(),
    Object? archive = const $CopyWithPlaceholder(),
  }) {
    return PrestataireDto(
      id: id == const $CopyWithPlaceholder()
          ? _value.id
          // ignore: cast_nullable_to_non_nullable
          : id as String?,
      nom: nom == const $CopyWithPlaceholder()
          ? _value.nom
          // ignore: cast_nullable_to_non_nullable
          : nom as String?,
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
      archive: archive == const $CopyWithPlaceholder()
          ? _value.archive
          // ignore: cast_nullable_to_non_nullable
          : archive as bool?,
    );
  }
}

extension $PrestataireDtoCopyWith on PrestataireDto {
  /// Returns a callable class that can be used as follows: `instanceOfPrestataireDto.copyWith(...)` or like so:`instanceOfPrestataireDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$PrestataireDtoCWProxy get copyWith => _$PrestataireDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

PrestataireDto _$PrestataireDtoFromJson(Map<String, dynamic> json) =>
    $checkedCreate('PrestataireDto', json, ($checkedConvert) {
      final val = PrestataireDto(
        id: $checkedConvert('id', (v) => v as String?),
        nom: $checkedConvert('nom', (v) => v as String?),
        metier: $checkedConvert('metier', (v) => v as String?),
        telephone: $checkedConvert('telephone', (v) => v as String?),
        email: $checkedConvert('email', (v) => v as String?),
        adresse: $checkedConvert('adresse', (v) => v as String?),
        utilisateurId: $checkedConvert('utilisateurId', (v) => v as String?),
        notes: $checkedConvert('notes', (v) => v as String?),
        archive: $checkedConvert('archive', (v) => v as bool?),
      );
      return val;
    });

Map<String, dynamic> _$PrestataireDtoToJson(PrestataireDto instance) =>
    <String, dynamic>{
      'id': ?instance.id,
      'nom': ?instance.nom,
      'metier': ?instance.metier,
      'telephone': ?instance.telephone,
      'email': ?instance.email,
      'adresse': ?instance.adresse,
      'utilisateurId': ?instance.utilisateurId,
      'notes': ?instance.notes,
      'archive': ?instance.archive,
    };
