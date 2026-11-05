// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'garant_requete.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$GarantRequeteCWProxy {
  GarantRequete nom(String nom);

  GarantRequete lien(String? lien);

  GarantRequete profession(String? profession);

  GarantRequete telephone(String telephone);

  GarantRequete adresse(String? adresse);

  GarantRequete numeroCni(String? numeroCni);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `GarantRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// GarantRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  GarantRequete call({
    String nom,
    String? lien,
    String? profession,
    String telephone,
    String? adresse,
    String? numeroCni,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfGarantRequete.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfGarantRequete.copyWith.fieldName(...)`
class _$GarantRequeteCWProxyImpl implements _$GarantRequeteCWProxy {
  const _$GarantRequeteCWProxyImpl(this._value);

  final GarantRequete _value;

  @override
  GarantRequete nom(String nom) => this(nom: nom);

  @override
  GarantRequete lien(String? lien) => this(lien: lien);

  @override
  GarantRequete profession(String? profession) => this(profession: profession);

  @override
  GarantRequete telephone(String telephone) => this(telephone: telephone);

  @override
  GarantRequete adresse(String? adresse) => this(adresse: adresse);

  @override
  GarantRequete numeroCni(String? numeroCni) => this(numeroCni: numeroCni);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `GarantRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// GarantRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  GarantRequete call({
    Object? nom = const $CopyWithPlaceholder(),
    Object? lien = const $CopyWithPlaceholder(),
    Object? profession = const $CopyWithPlaceholder(),
    Object? telephone = const $CopyWithPlaceholder(),
    Object? adresse = const $CopyWithPlaceholder(),
    Object? numeroCni = const $CopyWithPlaceholder(),
  }) {
    return GarantRequete(
      nom: nom == const $CopyWithPlaceholder()
          ? _value.nom
          // ignore: cast_nullable_to_non_nullable
          : nom as String,
      lien: lien == const $CopyWithPlaceholder()
          ? _value.lien
          // ignore: cast_nullable_to_non_nullable
          : lien as String?,
      profession: profession == const $CopyWithPlaceholder()
          ? _value.profession
          // ignore: cast_nullable_to_non_nullable
          : profession as String?,
      telephone: telephone == const $CopyWithPlaceholder()
          ? _value.telephone
          // ignore: cast_nullable_to_non_nullable
          : telephone as String,
      adresse: adresse == const $CopyWithPlaceholder()
          ? _value.adresse
          // ignore: cast_nullable_to_non_nullable
          : adresse as String?,
      numeroCni: numeroCni == const $CopyWithPlaceholder()
          ? _value.numeroCni
          // ignore: cast_nullable_to_non_nullable
          : numeroCni as String?,
    );
  }
}

extension $GarantRequeteCopyWith on GarantRequete {
  /// Returns a callable class that can be used as follows: `instanceOfGarantRequete.copyWith(...)` or like so:`instanceOfGarantRequete.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$GarantRequeteCWProxy get copyWith => _$GarantRequeteCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

GarantRequete _$GarantRequeteFromJson(Map<String, dynamic> json) =>
    $checkedCreate('GarantRequete', json, ($checkedConvert) {
      $checkKeys(json, requiredKeys: const ['nom', 'telephone']);
      final val = GarantRequete(
        nom: $checkedConvert('nom', (v) => v as String),
        lien: $checkedConvert('lien', (v) => v as String?),
        profession: $checkedConvert('profession', (v) => v as String?),
        telephone: $checkedConvert('telephone', (v) => v as String),
        adresse: $checkedConvert('adresse', (v) => v as String?),
        numeroCni: $checkedConvert('numeroCni', (v) => v as String?),
      );
      return val;
    });

Map<String, dynamic> _$GarantRequeteToJson(GarantRequete instance) =>
    <String, dynamic>{
      'nom': instance.nom,
      'lien': ?instance.lien,
      'profession': ?instance.profession,
      'telephone': instance.telephone,
      'adresse': ?instance.adresse,
      'numeroCni': ?instance.numeroCni,
    };
