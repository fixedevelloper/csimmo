// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'immeuble_requete.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$ImmeubleRequeteCWProxy {
  ImmeubleRequete nom(String nom);

  ImmeubleRequete adresse(String adresse);

  ImmeubleRequete quartier(String? quartier);

  ImmeubleRequete ville(String ville);

  ImmeubleRequete latitude(num? latitude);

  ImmeubleRequete longitude(num? longitude);

  ImmeubleRequete description(String? description);

  ImmeubleRequete proprietaireTiersId(String? proprietaireTiersId);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ImmeubleRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ImmeubleRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  ImmeubleRequete call({
    String nom,
    String adresse,
    String? quartier,
    String ville,
    num? latitude,
    num? longitude,
    String? description,
    String? proprietaireTiersId,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfImmeubleRequete.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfImmeubleRequete.copyWith.fieldName(...)`
class _$ImmeubleRequeteCWProxyImpl implements _$ImmeubleRequeteCWProxy {
  const _$ImmeubleRequeteCWProxyImpl(this._value);

  final ImmeubleRequete _value;

  @override
  ImmeubleRequete nom(String nom) => this(nom: nom);

  @override
  ImmeubleRequete adresse(String adresse) => this(adresse: adresse);

  @override
  ImmeubleRequete quartier(String? quartier) => this(quartier: quartier);

  @override
  ImmeubleRequete ville(String ville) => this(ville: ville);

  @override
  ImmeubleRequete latitude(num? latitude) => this(latitude: latitude);

  @override
  ImmeubleRequete longitude(num? longitude) => this(longitude: longitude);

  @override
  ImmeubleRequete description(String? description) =>
      this(description: description);

  @override
  ImmeubleRequete proprietaireTiersId(String? proprietaireTiersId) =>
      this(proprietaireTiersId: proprietaireTiersId);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ImmeubleRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ImmeubleRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  ImmeubleRequete call({
    Object? nom = const $CopyWithPlaceholder(),
    Object? adresse = const $CopyWithPlaceholder(),
    Object? quartier = const $CopyWithPlaceholder(),
    Object? ville = const $CopyWithPlaceholder(),
    Object? latitude = const $CopyWithPlaceholder(),
    Object? longitude = const $CopyWithPlaceholder(),
    Object? description = const $CopyWithPlaceholder(),
    Object? proprietaireTiersId = const $CopyWithPlaceholder(),
  }) {
    return ImmeubleRequete(
      nom: nom == const $CopyWithPlaceholder()
          ? _value.nom
          // ignore: cast_nullable_to_non_nullable
          : nom as String,
      adresse: adresse == const $CopyWithPlaceholder()
          ? _value.adresse
          // ignore: cast_nullable_to_non_nullable
          : adresse as String,
      quartier: quartier == const $CopyWithPlaceholder()
          ? _value.quartier
          // ignore: cast_nullable_to_non_nullable
          : quartier as String?,
      ville: ville == const $CopyWithPlaceholder()
          ? _value.ville
          // ignore: cast_nullable_to_non_nullable
          : ville as String,
      latitude: latitude == const $CopyWithPlaceholder()
          ? _value.latitude
          // ignore: cast_nullable_to_non_nullable
          : latitude as num?,
      longitude: longitude == const $CopyWithPlaceholder()
          ? _value.longitude
          // ignore: cast_nullable_to_non_nullable
          : longitude as num?,
      description: description == const $CopyWithPlaceholder()
          ? _value.description
          // ignore: cast_nullable_to_non_nullable
          : description as String?,
      proprietaireTiersId: proprietaireTiersId == const $CopyWithPlaceholder()
          ? _value.proprietaireTiersId
          // ignore: cast_nullable_to_non_nullable
          : proprietaireTiersId as String?,
    );
  }
}

extension $ImmeubleRequeteCopyWith on ImmeubleRequete {
  /// Returns a callable class that can be used as follows: `instanceOfImmeubleRequete.copyWith(...)` or like so:`instanceOfImmeubleRequete.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$ImmeubleRequeteCWProxy get copyWith => _$ImmeubleRequeteCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

ImmeubleRequete _$ImmeubleRequeteFromJson(Map<String, dynamic> json) =>
    $checkedCreate('ImmeubleRequete', json, ($checkedConvert) {
      $checkKeys(json, requiredKeys: const ['nom', 'adresse', 'ville']);
      final val = ImmeubleRequete(
        nom: $checkedConvert('nom', (v) => v as String),
        adresse: $checkedConvert('adresse', (v) => v as String),
        quartier: $checkedConvert('quartier', (v) => v as String?),
        ville: $checkedConvert('ville', (v) => v as String),
        latitude: $checkedConvert('latitude', (v) => v as num?),
        longitude: $checkedConvert('longitude', (v) => v as num?),
        description: $checkedConvert('description', (v) => v as String?),
        proprietaireTiersId: $checkedConvert(
          'proprietaireTiersId',
          (v) => v as String?,
        ),
      );
      return val;
    });

Map<String, dynamic> _$ImmeubleRequeteToJson(ImmeubleRequete instance) =>
    <String, dynamic>{
      'nom': instance.nom,
      'adresse': instance.adresse,
      'quartier': ?instance.quartier,
      'ville': instance.ville,
      'latitude': ?instance.latitude,
      'longitude': ?instance.longitude,
      'description': ?instance.description,
      'proprietaireTiersId': ?instance.proprietaireTiersId,
    };
