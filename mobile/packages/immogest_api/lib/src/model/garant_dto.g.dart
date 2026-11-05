// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'garant_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$GarantDtoCWProxy {
  GarantDto id(String? id);

  GarantDto locataireId(String? locataireId);

  GarantDto nom(String? nom);

  GarantDto lien(String? lien);

  GarantDto profession(String? profession);

  GarantDto telephone(String? telephone);

  GarantDto adresse(String? adresse);

  GarantDto numeroCni(String? numeroCni);

  GarantDto archive(bool? archive);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `GarantDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// GarantDto(...).copyWith(id: 12, name: "My name")
  /// ````
  GarantDto call({
    String? id,
    String? locataireId,
    String? nom,
    String? lien,
    String? profession,
    String? telephone,
    String? adresse,
    String? numeroCni,
    bool? archive,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfGarantDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfGarantDto.copyWith.fieldName(...)`
class _$GarantDtoCWProxyImpl implements _$GarantDtoCWProxy {
  const _$GarantDtoCWProxyImpl(this._value);

  final GarantDto _value;

  @override
  GarantDto id(String? id) => this(id: id);

  @override
  GarantDto locataireId(String? locataireId) => this(locataireId: locataireId);

  @override
  GarantDto nom(String? nom) => this(nom: nom);

  @override
  GarantDto lien(String? lien) => this(lien: lien);

  @override
  GarantDto profession(String? profession) => this(profession: profession);

  @override
  GarantDto telephone(String? telephone) => this(telephone: telephone);

  @override
  GarantDto adresse(String? adresse) => this(adresse: adresse);

  @override
  GarantDto numeroCni(String? numeroCni) => this(numeroCni: numeroCni);

  @override
  GarantDto archive(bool? archive) => this(archive: archive);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `GarantDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// GarantDto(...).copyWith(id: 12, name: "My name")
  /// ````
  GarantDto call({
    Object? id = const $CopyWithPlaceholder(),
    Object? locataireId = const $CopyWithPlaceholder(),
    Object? nom = const $CopyWithPlaceholder(),
    Object? lien = const $CopyWithPlaceholder(),
    Object? profession = const $CopyWithPlaceholder(),
    Object? telephone = const $CopyWithPlaceholder(),
    Object? adresse = const $CopyWithPlaceholder(),
    Object? numeroCni = const $CopyWithPlaceholder(),
    Object? archive = const $CopyWithPlaceholder(),
  }) {
    return GarantDto(
      id: id == const $CopyWithPlaceholder()
          ? _value.id
          // ignore: cast_nullable_to_non_nullable
          : id as String?,
      locataireId: locataireId == const $CopyWithPlaceholder()
          ? _value.locataireId
          // ignore: cast_nullable_to_non_nullable
          : locataireId as String?,
      nom: nom == const $CopyWithPlaceholder()
          ? _value.nom
          // ignore: cast_nullable_to_non_nullable
          : nom as String?,
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
          : telephone as String?,
      adresse: adresse == const $CopyWithPlaceholder()
          ? _value.adresse
          // ignore: cast_nullable_to_non_nullable
          : adresse as String?,
      numeroCni: numeroCni == const $CopyWithPlaceholder()
          ? _value.numeroCni
          // ignore: cast_nullable_to_non_nullable
          : numeroCni as String?,
      archive: archive == const $CopyWithPlaceholder()
          ? _value.archive
          // ignore: cast_nullable_to_non_nullable
          : archive as bool?,
    );
  }
}

extension $GarantDtoCopyWith on GarantDto {
  /// Returns a callable class that can be used as follows: `instanceOfGarantDto.copyWith(...)` or like so:`instanceOfGarantDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$GarantDtoCWProxy get copyWith => _$GarantDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

GarantDto _$GarantDtoFromJson(Map<String, dynamic> json) =>
    $checkedCreate('GarantDto', json, ($checkedConvert) {
      final val = GarantDto(
        id: $checkedConvert('id', (v) => v as String?),
        locataireId: $checkedConvert('locataireId', (v) => v as String?),
        nom: $checkedConvert('nom', (v) => v as String?),
        lien: $checkedConvert('lien', (v) => v as String?),
        profession: $checkedConvert('profession', (v) => v as String?),
        telephone: $checkedConvert('telephone', (v) => v as String?),
        adresse: $checkedConvert('adresse', (v) => v as String?),
        numeroCni: $checkedConvert('numeroCni', (v) => v as String?),
        archive: $checkedConvert('archive', (v) => v as bool?),
      );
      return val;
    });

Map<String, dynamic> _$GarantDtoToJson(GarantDto instance) => <String, dynamic>{
  'id': ?instance.id,
  'locataireId': ?instance.locataireId,
  'nom': ?instance.nom,
  'lien': ?instance.lien,
  'profession': ?instance.profession,
  'telephone': ?instance.telephone,
  'adresse': ?instance.adresse,
  'numeroCni': ?instance.numeroCni,
  'archive': ?instance.archive,
};
