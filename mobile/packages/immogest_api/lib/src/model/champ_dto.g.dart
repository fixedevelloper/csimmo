// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'champ_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$ChampDtoCWProxy {
  ChampDto nom(String? nom);

  ChampDto secret(bool? secret);

  ChampDto requis(bool? requis);

  ChampDto valeur(String? valeur);

  ChampDto renseigne(bool? renseigne);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ChampDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ChampDto(...).copyWith(id: 12, name: "My name")
  /// ````
  ChampDto call({
    String? nom,
    bool? secret,
    bool? requis,
    String? valeur,
    bool? renseigne,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfChampDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfChampDto.copyWith.fieldName(...)`
class _$ChampDtoCWProxyImpl implements _$ChampDtoCWProxy {
  const _$ChampDtoCWProxyImpl(this._value);

  final ChampDto _value;

  @override
  ChampDto nom(String? nom) => this(nom: nom);

  @override
  ChampDto secret(bool? secret) => this(secret: secret);

  @override
  ChampDto requis(bool? requis) => this(requis: requis);

  @override
  ChampDto valeur(String? valeur) => this(valeur: valeur);

  @override
  ChampDto renseigne(bool? renseigne) => this(renseigne: renseigne);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ChampDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ChampDto(...).copyWith(id: 12, name: "My name")
  /// ````
  ChampDto call({
    Object? nom = const $CopyWithPlaceholder(),
    Object? secret = const $CopyWithPlaceholder(),
    Object? requis = const $CopyWithPlaceholder(),
    Object? valeur = const $CopyWithPlaceholder(),
    Object? renseigne = const $CopyWithPlaceholder(),
  }) {
    return ChampDto(
      nom: nom == const $CopyWithPlaceholder()
          ? _value.nom
          // ignore: cast_nullable_to_non_nullable
          : nom as String?,
      secret: secret == const $CopyWithPlaceholder()
          ? _value.secret
          // ignore: cast_nullable_to_non_nullable
          : secret as bool?,
      requis: requis == const $CopyWithPlaceholder()
          ? _value.requis
          // ignore: cast_nullable_to_non_nullable
          : requis as bool?,
      valeur: valeur == const $CopyWithPlaceholder()
          ? _value.valeur
          // ignore: cast_nullable_to_non_nullable
          : valeur as String?,
      renseigne: renseigne == const $CopyWithPlaceholder()
          ? _value.renseigne
          // ignore: cast_nullable_to_non_nullable
          : renseigne as bool?,
    );
  }
}

extension $ChampDtoCopyWith on ChampDto {
  /// Returns a callable class that can be used as follows: `instanceOfChampDto.copyWith(...)` or like so:`instanceOfChampDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$ChampDtoCWProxy get copyWith => _$ChampDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

ChampDto _$ChampDtoFromJson(Map<String, dynamic> json) =>
    $checkedCreate('ChampDto', json, ($checkedConvert) {
      final val = ChampDto(
        nom: $checkedConvert('nom', (v) => v as String?),
        secret: $checkedConvert('secret', (v) => v as bool?),
        requis: $checkedConvert('requis', (v) => v as bool?),
        valeur: $checkedConvert('valeur', (v) => v as String?),
        renseigne: $checkedConvert('renseigne', (v) => v as bool?),
      );
      return val;
    });

Map<String, dynamic> _$ChampDtoToJson(ChampDto instance) => <String, dynamic>{
  'nom': ?instance.nom,
  'secret': ?instance.secret,
  'requis': ?instance.requis,
  'valeur': ?instance.valeur,
  'renseigne': ?instance.renseigne,
};
