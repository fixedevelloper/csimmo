// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'resultat_recherche.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$ResultatRechercheCWProxy {
  ResultatRecherche type(String? type);

  ResultatRecherche id(String? id);

  ResultatRecherche nom(String? nom);

  ResultatRecherche telephone(String? telephone);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ResultatRecherche(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ResultatRecherche(...).copyWith(id: 12, name: "My name")
  /// ````
  ResultatRecherche call({
    String? type,
    String? id,
    String? nom,
    String? telephone,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfResultatRecherche.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfResultatRecherche.copyWith.fieldName(...)`
class _$ResultatRechercheCWProxyImpl implements _$ResultatRechercheCWProxy {
  const _$ResultatRechercheCWProxyImpl(this._value);

  final ResultatRecherche _value;

  @override
  ResultatRecherche type(String? type) => this(type: type);

  @override
  ResultatRecherche id(String? id) => this(id: id);

  @override
  ResultatRecherche nom(String? nom) => this(nom: nom);

  @override
  ResultatRecherche telephone(String? telephone) => this(telephone: telephone);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ResultatRecherche(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ResultatRecherche(...).copyWith(id: 12, name: "My name")
  /// ````
  ResultatRecherche call({
    Object? type = const $CopyWithPlaceholder(),
    Object? id = const $CopyWithPlaceholder(),
    Object? nom = const $CopyWithPlaceholder(),
    Object? telephone = const $CopyWithPlaceholder(),
  }) {
    return ResultatRecherche(
      type: type == const $CopyWithPlaceholder()
          ? _value.type
          // ignore: cast_nullable_to_non_nullable
          : type as String?,
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
    );
  }
}

extension $ResultatRechercheCopyWith on ResultatRecherche {
  /// Returns a callable class that can be used as follows: `instanceOfResultatRecherche.copyWith(...)` or like so:`instanceOfResultatRecherche.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$ResultatRechercheCWProxy get copyWith =>
      _$ResultatRechercheCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

ResultatRecherche _$ResultatRechercheFromJson(Map<String, dynamic> json) =>
    $checkedCreate('ResultatRecherche', json, ($checkedConvert) {
      final val = ResultatRecherche(
        type: $checkedConvert('type', (v) => v as String?),
        id: $checkedConvert('id', (v) => v as String?),
        nom: $checkedConvert('nom', (v) => v as String?),
        telephone: $checkedConvert('telephone', (v) => v as String?),
      );
      return val;
    });

Map<String, dynamic> _$ResultatRechercheToJson(ResultatRecherche instance) =>
    <String, dynamic>{
      'type': ?instance.type,
      'id': ?instance.id,
      'nom': ?instance.nom,
      'telephone': ?instance.telephone,
    };
