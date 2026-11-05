// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'traitement_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$TraitementDtoCWProxy {
  TraitementDto id(String? id);

  TraitementDto nom(String? nom);

  TraitementDto finalite(String? finalite);

  TraitementDto baseLegale(String? baseLegale);

  TraitementDto donnees(List<String>? donnees);

  TraitementDto personnes(List<String>? personnes);

  TraitementDto destinataires(List<String>? destinataires);

  TraitementDto conservation(String? conservation);

  TraitementDto mesures(List<String>? mesures);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `TraitementDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// TraitementDto(...).copyWith(id: 12, name: "My name")
  /// ````
  TraitementDto call({
    String? id,
    String? nom,
    String? finalite,
    String? baseLegale,
    List<String>? donnees,
    List<String>? personnes,
    List<String>? destinataires,
    String? conservation,
    List<String>? mesures,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfTraitementDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfTraitementDto.copyWith.fieldName(...)`
class _$TraitementDtoCWProxyImpl implements _$TraitementDtoCWProxy {
  const _$TraitementDtoCWProxyImpl(this._value);

  final TraitementDto _value;

  @override
  TraitementDto id(String? id) => this(id: id);

  @override
  TraitementDto nom(String? nom) => this(nom: nom);

  @override
  TraitementDto finalite(String? finalite) => this(finalite: finalite);

  @override
  TraitementDto baseLegale(String? baseLegale) => this(baseLegale: baseLegale);

  @override
  TraitementDto donnees(List<String>? donnees) => this(donnees: donnees);

  @override
  TraitementDto personnes(List<String>? personnes) =>
      this(personnes: personnes);

  @override
  TraitementDto destinataires(List<String>? destinataires) =>
      this(destinataires: destinataires);

  @override
  TraitementDto conservation(String? conservation) =>
      this(conservation: conservation);

  @override
  TraitementDto mesures(List<String>? mesures) => this(mesures: mesures);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `TraitementDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// TraitementDto(...).copyWith(id: 12, name: "My name")
  /// ````
  TraitementDto call({
    Object? id = const $CopyWithPlaceholder(),
    Object? nom = const $CopyWithPlaceholder(),
    Object? finalite = const $CopyWithPlaceholder(),
    Object? baseLegale = const $CopyWithPlaceholder(),
    Object? donnees = const $CopyWithPlaceholder(),
    Object? personnes = const $CopyWithPlaceholder(),
    Object? destinataires = const $CopyWithPlaceholder(),
    Object? conservation = const $CopyWithPlaceholder(),
    Object? mesures = const $CopyWithPlaceholder(),
  }) {
    return TraitementDto(
      id: id == const $CopyWithPlaceholder()
          ? _value.id
          // ignore: cast_nullable_to_non_nullable
          : id as String?,
      nom: nom == const $CopyWithPlaceholder()
          ? _value.nom
          // ignore: cast_nullable_to_non_nullable
          : nom as String?,
      finalite: finalite == const $CopyWithPlaceholder()
          ? _value.finalite
          // ignore: cast_nullable_to_non_nullable
          : finalite as String?,
      baseLegale: baseLegale == const $CopyWithPlaceholder()
          ? _value.baseLegale
          // ignore: cast_nullable_to_non_nullable
          : baseLegale as String?,
      donnees: donnees == const $CopyWithPlaceholder()
          ? _value.donnees
          // ignore: cast_nullable_to_non_nullable
          : donnees as List<String>?,
      personnes: personnes == const $CopyWithPlaceholder()
          ? _value.personnes
          // ignore: cast_nullable_to_non_nullable
          : personnes as List<String>?,
      destinataires: destinataires == const $CopyWithPlaceholder()
          ? _value.destinataires
          // ignore: cast_nullable_to_non_nullable
          : destinataires as List<String>?,
      conservation: conservation == const $CopyWithPlaceholder()
          ? _value.conservation
          // ignore: cast_nullable_to_non_nullable
          : conservation as String?,
      mesures: mesures == const $CopyWithPlaceholder()
          ? _value.mesures
          // ignore: cast_nullable_to_non_nullable
          : mesures as List<String>?,
    );
  }
}

extension $TraitementDtoCopyWith on TraitementDto {
  /// Returns a callable class that can be used as follows: `instanceOfTraitementDto.copyWith(...)` or like so:`instanceOfTraitementDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$TraitementDtoCWProxy get copyWith => _$TraitementDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

TraitementDto _$TraitementDtoFromJson(Map<String, dynamic> json) =>
    $checkedCreate('TraitementDto', json, ($checkedConvert) {
      final val = TraitementDto(
        id: $checkedConvert('id', (v) => v as String?),
        nom: $checkedConvert('nom', (v) => v as String?),
        finalite: $checkedConvert('finalite', (v) => v as String?),
        baseLegale: $checkedConvert('baseLegale', (v) => v as String?),
        donnees: $checkedConvert(
          'donnees',
          (v) => (v as List<dynamic>?)?.map((e) => e as String).toList(),
        ),
        personnes: $checkedConvert(
          'personnes',
          (v) => (v as List<dynamic>?)?.map((e) => e as String).toList(),
        ),
        destinataires: $checkedConvert(
          'destinataires',
          (v) => (v as List<dynamic>?)?.map((e) => e as String).toList(),
        ),
        conservation: $checkedConvert('conservation', (v) => v as String?),
        mesures: $checkedConvert(
          'mesures',
          (v) => (v as List<dynamic>?)?.map((e) => e as String).toList(),
        ),
      );
      return val;
    });

Map<String, dynamic> _$TraitementDtoToJson(TraitementDto instance) =>
    <String, dynamic>{
      'id': ?instance.id,
      'nom': ?instance.nom,
      'finalite': ?instance.finalite,
      'baseLegale': ?instance.baseLegale,
      'donnees': ?instance.donnees,
      'personnes': ?instance.personnes,
      'destinataires': ?instance.destinataires,
      'conservation': ?instance.conservation,
      'mesures': ?instance.mesures,
    };
