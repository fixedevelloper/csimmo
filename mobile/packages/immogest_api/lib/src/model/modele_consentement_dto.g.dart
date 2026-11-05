// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'modele_consentement_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$ModeleConsentementDtoCWProxy {
  ModeleConsentementDto finalite(ModeleConsentementDtoFinaliteEnum? finalite);

  ModeleConsentementDto version(String? version);

  ModeleConsentementDto titre(String? titre);

  ModeleConsentementDto texte(String? texte);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ModeleConsentementDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ModeleConsentementDto(...).copyWith(id: 12, name: "My name")
  /// ````
  ModeleConsentementDto call({
    ModeleConsentementDtoFinaliteEnum? finalite,
    String? version,
    String? titre,
    String? texte,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfModeleConsentementDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfModeleConsentementDto.copyWith.fieldName(...)`
class _$ModeleConsentementDtoCWProxyImpl
    implements _$ModeleConsentementDtoCWProxy {
  const _$ModeleConsentementDtoCWProxyImpl(this._value);

  final ModeleConsentementDto _value;

  @override
  ModeleConsentementDto finalite(ModeleConsentementDtoFinaliteEnum? finalite) =>
      this(finalite: finalite);

  @override
  ModeleConsentementDto version(String? version) => this(version: version);

  @override
  ModeleConsentementDto titre(String? titre) => this(titre: titre);

  @override
  ModeleConsentementDto texte(String? texte) => this(texte: texte);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ModeleConsentementDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ModeleConsentementDto(...).copyWith(id: 12, name: "My name")
  /// ````
  ModeleConsentementDto call({
    Object? finalite = const $CopyWithPlaceholder(),
    Object? version = const $CopyWithPlaceholder(),
    Object? titre = const $CopyWithPlaceholder(),
    Object? texte = const $CopyWithPlaceholder(),
  }) {
    return ModeleConsentementDto(
      finalite: finalite == const $CopyWithPlaceholder()
          ? _value.finalite
          // ignore: cast_nullable_to_non_nullable
          : finalite as ModeleConsentementDtoFinaliteEnum?,
      version: version == const $CopyWithPlaceholder()
          ? _value.version
          // ignore: cast_nullable_to_non_nullable
          : version as String?,
      titre: titre == const $CopyWithPlaceholder()
          ? _value.titre
          // ignore: cast_nullable_to_non_nullable
          : titre as String?,
      texte: texte == const $CopyWithPlaceholder()
          ? _value.texte
          // ignore: cast_nullable_to_non_nullable
          : texte as String?,
    );
  }
}

extension $ModeleConsentementDtoCopyWith on ModeleConsentementDto {
  /// Returns a callable class that can be used as follows: `instanceOfModeleConsentementDto.copyWith(...)` or like so:`instanceOfModeleConsentementDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$ModeleConsentementDtoCWProxy get copyWith =>
      _$ModeleConsentementDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

ModeleConsentementDto _$ModeleConsentementDtoFromJson(
  Map<String, dynamic> json,
) => $checkedCreate('ModeleConsentementDto', json, ($checkedConvert) {
  final val = ModeleConsentementDto(
    finalite: $checkedConvert(
      'finalite',
      (v) => $enumDecodeNullable(_$ModeleConsentementDtoFinaliteEnumEnumMap, v),
    ),
    version: $checkedConvert('version', (v) => v as String?),
    titre: $checkedConvert('titre', (v) => v as String?),
    texte: $checkedConvert('texte', (v) => v as String?),
  );
  return val;
});

Map<String, dynamic> _$ModeleConsentementDtoToJson(
  ModeleConsentementDto instance,
) => <String, dynamic>{
  'finalite': ?_$ModeleConsentementDtoFinaliteEnumEnumMap[instance.finalite],
  'version': ?instance.version,
  'titre': ?instance.titre,
  'texte': ?instance.texte,
};

const _$ModeleConsentementDtoFinaliteEnumEnumMap = {
  ModeleConsentementDtoFinaliteEnum.NOTIFICATIONS: 'NOTIFICATIONS',
  ModeleConsentementDtoFinaliteEnum.PIECES_IDENTITE: 'PIECES_IDENTITE',
  ModeleConsentementDtoFinaliteEnum.TRANSMISSION_PROPRIETAIRE:
      'TRANSMISSION_PROPRIETAIRE',
  ModeleConsentementDtoFinaliteEnum.APPLICATION_MOBILE: 'APPLICATION_MOBILE',
};
