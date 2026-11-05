// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'proprietaire_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$ProprietaireDtoCWProxy {
  ProprietaireDto id(String? id);

  ProprietaireDto nom(String? nom);

  ProprietaireDto telephone(String? telephone);

  ProprietaireDto email(String? email);

  ProprietaireDto adresse(String? adresse);

  ProprietaireDto modeReversement(
    ProprietaireDtoModeReversementEnum? modeReversement,
  );

  ProprietaireDto coordonneesReversement(String? coordonneesReversement);

  ProprietaireDto utilisateurId(String? utilisateurId);

  ProprietaireDto notes(String? notes);

  ProprietaireDto archive(bool? archive);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ProprietaireDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ProprietaireDto(...).copyWith(id: 12, name: "My name")
  /// ````
  ProprietaireDto call({
    String? id,
    String? nom,
    String? telephone,
    String? email,
    String? adresse,
    ProprietaireDtoModeReversementEnum? modeReversement,
    String? coordonneesReversement,
    String? utilisateurId,
    String? notes,
    bool? archive,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfProprietaireDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfProprietaireDto.copyWith.fieldName(...)`
class _$ProprietaireDtoCWProxyImpl implements _$ProprietaireDtoCWProxy {
  const _$ProprietaireDtoCWProxyImpl(this._value);

  final ProprietaireDto _value;

  @override
  ProprietaireDto id(String? id) => this(id: id);

  @override
  ProprietaireDto nom(String? nom) => this(nom: nom);

  @override
  ProprietaireDto telephone(String? telephone) => this(telephone: telephone);

  @override
  ProprietaireDto email(String? email) => this(email: email);

  @override
  ProprietaireDto adresse(String? adresse) => this(adresse: adresse);

  @override
  ProprietaireDto modeReversement(
    ProprietaireDtoModeReversementEnum? modeReversement,
  ) => this(modeReversement: modeReversement);

  @override
  ProprietaireDto coordonneesReversement(String? coordonneesReversement) =>
      this(coordonneesReversement: coordonneesReversement);

  @override
  ProprietaireDto utilisateurId(String? utilisateurId) =>
      this(utilisateurId: utilisateurId);

  @override
  ProprietaireDto notes(String? notes) => this(notes: notes);

  @override
  ProprietaireDto archive(bool? archive) => this(archive: archive);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ProprietaireDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ProprietaireDto(...).copyWith(id: 12, name: "My name")
  /// ````
  ProprietaireDto call({
    Object? id = const $CopyWithPlaceholder(),
    Object? nom = const $CopyWithPlaceholder(),
    Object? telephone = const $CopyWithPlaceholder(),
    Object? email = const $CopyWithPlaceholder(),
    Object? adresse = const $CopyWithPlaceholder(),
    Object? modeReversement = const $CopyWithPlaceholder(),
    Object? coordonneesReversement = const $CopyWithPlaceholder(),
    Object? utilisateurId = const $CopyWithPlaceholder(),
    Object? notes = const $CopyWithPlaceholder(),
    Object? archive = const $CopyWithPlaceholder(),
  }) {
    return ProprietaireDto(
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
      adresse: adresse == const $CopyWithPlaceholder()
          ? _value.adresse
          // ignore: cast_nullable_to_non_nullable
          : adresse as String?,
      modeReversement: modeReversement == const $CopyWithPlaceholder()
          ? _value.modeReversement
          // ignore: cast_nullable_to_non_nullable
          : modeReversement as ProprietaireDtoModeReversementEnum?,
      coordonneesReversement:
          coordonneesReversement == const $CopyWithPlaceholder()
          ? _value.coordonneesReversement
          // ignore: cast_nullable_to_non_nullable
          : coordonneesReversement as String?,
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

extension $ProprietaireDtoCopyWith on ProprietaireDto {
  /// Returns a callable class that can be used as follows: `instanceOfProprietaireDto.copyWith(...)` or like so:`instanceOfProprietaireDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$ProprietaireDtoCWProxy get copyWith => _$ProprietaireDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

ProprietaireDto _$ProprietaireDtoFromJson(Map<String, dynamic> json) =>
    $checkedCreate('ProprietaireDto', json, ($checkedConvert) {
      final val = ProprietaireDto(
        id: $checkedConvert('id', (v) => v as String?),
        nom: $checkedConvert('nom', (v) => v as String?),
        telephone: $checkedConvert('telephone', (v) => v as String?),
        email: $checkedConvert('email', (v) => v as String?),
        adresse: $checkedConvert('adresse', (v) => v as String?),
        modeReversement: $checkedConvert(
          'modeReversement',
          (v) => $enumDecodeNullable(
            _$ProprietaireDtoModeReversementEnumEnumMap,
            v,
          ),
        ),
        coordonneesReversement: $checkedConvert(
          'coordonneesReversement',
          (v) => v as String?,
        ),
        utilisateurId: $checkedConvert('utilisateurId', (v) => v as String?),
        notes: $checkedConvert('notes', (v) => v as String?),
        archive: $checkedConvert('archive', (v) => v as bool?),
      );
      return val;
    });

Map<String, dynamic> _$ProprietaireDtoToJson(
  ProprietaireDto instance,
) => <String, dynamic>{
  'id': ?instance.id,
  'nom': ?instance.nom,
  'telephone': ?instance.telephone,
  'email': ?instance.email,
  'adresse': ?instance.adresse,
  'modeReversement':
      ?_$ProprietaireDtoModeReversementEnumEnumMap[instance.modeReversement],
  'coordonneesReversement': ?instance.coordonneesReversement,
  'utilisateurId': ?instance.utilisateurId,
  'notes': ?instance.notes,
  'archive': ?instance.archive,
};

const _$ProprietaireDtoModeReversementEnumEnumMap = {
  ProprietaireDtoModeReversementEnum.VIREMENT: 'VIREMENT',
  ProprietaireDtoModeReversementEnum.MOBILE_MONEY: 'MOBILE_MONEY',
  ProprietaireDtoModeReversementEnum.ESPECES: 'ESPECES',
  ProprietaireDtoModeReversementEnum.CHEQUE: 'CHEQUE',
};
