// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'proprietaire_requete.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$ProprietaireRequeteCWProxy {
  ProprietaireRequete nom(String nom);

  ProprietaireRequete telephone(String? telephone);

  ProprietaireRequete email(String? email);

  ProprietaireRequete adresse(String? adresse);

  ProprietaireRequete modeReversement(
    ProprietaireRequeteModeReversementEnum? modeReversement,
  );

  ProprietaireRequete coordonneesReversement(String? coordonneesReversement);

  ProprietaireRequete utilisateurId(String? utilisateurId);

  ProprietaireRequete notes(String? notes);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ProprietaireRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ProprietaireRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  ProprietaireRequete call({
    String nom,
    String? telephone,
    String? email,
    String? adresse,
    ProprietaireRequeteModeReversementEnum? modeReversement,
    String? coordonneesReversement,
    String? utilisateurId,
    String? notes,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfProprietaireRequete.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfProprietaireRequete.copyWith.fieldName(...)`
class _$ProprietaireRequeteCWProxyImpl implements _$ProprietaireRequeteCWProxy {
  const _$ProprietaireRequeteCWProxyImpl(this._value);

  final ProprietaireRequete _value;

  @override
  ProprietaireRequete nom(String nom) => this(nom: nom);

  @override
  ProprietaireRequete telephone(String? telephone) =>
      this(telephone: telephone);

  @override
  ProprietaireRequete email(String? email) => this(email: email);

  @override
  ProprietaireRequete adresse(String? adresse) => this(adresse: adresse);

  @override
  ProprietaireRequete modeReversement(
    ProprietaireRequeteModeReversementEnum? modeReversement,
  ) => this(modeReversement: modeReversement);

  @override
  ProprietaireRequete coordonneesReversement(String? coordonneesReversement) =>
      this(coordonneesReversement: coordonneesReversement);

  @override
  ProprietaireRequete utilisateurId(String? utilisateurId) =>
      this(utilisateurId: utilisateurId);

  @override
  ProprietaireRequete notes(String? notes) => this(notes: notes);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ProprietaireRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ProprietaireRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  ProprietaireRequete call({
    Object? nom = const $CopyWithPlaceholder(),
    Object? telephone = const $CopyWithPlaceholder(),
    Object? email = const $CopyWithPlaceholder(),
    Object? adresse = const $CopyWithPlaceholder(),
    Object? modeReversement = const $CopyWithPlaceholder(),
    Object? coordonneesReversement = const $CopyWithPlaceholder(),
    Object? utilisateurId = const $CopyWithPlaceholder(),
    Object? notes = const $CopyWithPlaceholder(),
  }) {
    return ProprietaireRequete(
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
      adresse: adresse == const $CopyWithPlaceholder()
          ? _value.adresse
          // ignore: cast_nullable_to_non_nullable
          : adresse as String?,
      modeReversement: modeReversement == const $CopyWithPlaceholder()
          ? _value.modeReversement
          // ignore: cast_nullable_to_non_nullable
          : modeReversement as ProprietaireRequeteModeReversementEnum?,
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
    );
  }
}

extension $ProprietaireRequeteCopyWith on ProprietaireRequete {
  /// Returns a callable class that can be used as follows: `instanceOfProprietaireRequete.copyWith(...)` or like so:`instanceOfProprietaireRequete.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$ProprietaireRequeteCWProxy get copyWith =>
      _$ProprietaireRequeteCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

ProprietaireRequete _$ProprietaireRequeteFromJson(Map<String, dynamic> json) =>
    $checkedCreate('ProprietaireRequete', json, ($checkedConvert) {
      $checkKeys(json, requiredKeys: const ['nom']);
      final val = ProprietaireRequete(
        nom: $checkedConvert('nom', (v) => v as String),
        telephone: $checkedConvert('telephone', (v) => v as String?),
        email: $checkedConvert('email', (v) => v as String?),
        adresse: $checkedConvert('adresse', (v) => v as String?),
        modeReversement: $checkedConvert(
          'modeReversement',
          (v) => $enumDecodeNullable(
            _$ProprietaireRequeteModeReversementEnumEnumMap,
            v,
          ),
        ),
        coordonneesReversement: $checkedConvert(
          'coordonneesReversement',
          (v) => v as String?,
        ),
        utilisateurId: $checkedConvert('utilisateurId', (v) => v as String?),
        notes: $checkedConvert('notes', (v) => v as String?),
      );
      return val;
    });

Map<String, dynamic> _$ProprietaireRequeteToJson(
  ProprietaireRequete instance,
) => <String, dynamic>{
  'nom': instance.nom,
  'telephone': ?instance.telephone,
  'email': ?instance.email,
  'adresse': ?instance.adresse,
  'modeReversement':
      ?_$ProprietaireRequeteModeReversementEnumEnumMap[instance
          .modeReversement],
  'coordonneesReversement': ?instance.coordonneesReversement,
  'utilisateurId': ?instance.utilisateurId,
  'notes': ?instance.notes,
};

const _$ProprietaireRequeteModeReversementEnumEnumMap = {
  ProprietaireRequeteModeReversementEnum.VIREMENT: 'VIREMENT',
  ProprietaireRequeteModeReversementEnum.MOBILE_MONEY: 'MOBILE_MONEY',
  ProprietaireRequeteModeReversementEnum.ESPECES: 'ESPECES',
  ProprietaireRequeteModeReversementEnum.CHEQUE: 'CHEQUE',
};
