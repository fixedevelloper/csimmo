// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'consentement_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$ConsentementDtoCWProxy {
  ConsentementDto id(String? id);

  ConsentementDto finalite(ConsentementDtoFinaliteEnum? finalite);

  ConsentementDto accorde(bool? accorde);

  ConsentementDto versionModele(String? versionModele);

  ConsentementDto modeRecueil(ConsentementDtoModeRecueilEnum? modeRecueil);

  ConsentementDto referenceDocument(String? referenceDocument);

  ConsentementDto recueilliPar(String? recueilliPar);

  ConsentementDto decideLe(DateTime? decideLe);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ConsentementDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ConsentementDto(...).copyWith(id: 12, name: "My name")
  /// ````
  ConsentementDto call({
    String? id,
    ConsentementDtoFinaliteEnum? finalite,
    bool? accorde,
    String? versionModele,
    ConsentementDtoModeRecueilEnum? modeRecueil,
    String? referenceDocument,
    String? recueilliPar,
    DateTime? decideLe,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfConsentementDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfConsentementDto.copyWith.fieldName(...)`
class _$ConsentementDtoCWProxyImpl implements _$ConsentementDtoCWProxy {
  const _$ConsentementDtoCWProxyImpl(this._value);

  final ConsentementDto _value;

  @override
  ConsentementDto id(String? id) => this(id: id);

  @override
  ConsentementDto finalite(ConsentementDtoFinaliteEnum? finalite) =>
      this(finalite: finalite);

  @override
  ConsentementDto accorde(bool? accorde) => this(accorde: accorde);

  @override
  ConsentementDto versionModele(String? versionModele) =>
      this(versionModele: versionModele);

  @override
  ConsentementDto modeRecueil(ConsentementDtoModeRecueilEnum? modeRecueil) =>
      this(modeRecueil: modeRecueil);

  @override
  ConsentementDto referenceDocument(String? referenceDocument) =>
      this(referenceDocument: referenceDocument);

  @override
  ConsentementDto recueilliPar(String? recueilliPar) =>
      this(recueilliPar: recueilliPar);

  @override
  ConsentementDto decideLe(DateTime? decideLe) => this(decideLe: decideLe);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ConsentementDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ConsentementDto(...).copyWith(id: 12, name: "My name")
  /// ````
  ConsentementDto call({
    Object? id = const $CopyWithPlaceholder(),
    Object? finalite = const $CopyWithPlaceholder(),
    Object? accorde = const $CopyWithPlaceholder(),
    Object? versionModele = const $CopyWithPlaceholder(),
    Object? modeRecueil = const $CopyWithPlaceholder(),
    Object? referenceDocument = const $CopyWithPlaceholder(),
    Object? recueilliPar = const $CopyWithPlaceholder(),
    Object? decideLe = const $CopyWithPlaceholder(),
  }) {
    return ConsentementDto(
      id: id == const $CopyWithPlaceholder()
          ? _value.id
          // ignore: cast_nullable_to_non_nullable
          : id as String?,
      finalite: finalite == const $CopyWithPlaceholder()
          ? _value.finalite
          // ignore: cast_nullable_to_non_nullable
          : finalite as ConsentementDtoFinaliteEnum?,
      accorde: accorde == const $CopyWithPlaceholder()
          ? _value.accorde
          // ignore: cast_nullable_to_non_nullable
          : accorde as bool?,
      versionModele: versionModele == const $CopyWithPlaceholder()
          ? _value.versionModele
          // ignore: cast_nullable_to_non_nullable
          : versionModele as String?,
      modeRecueil: modeRecueil == const $CopyWithPlaceholder()
          ? _value.modeRecueil
          // ignore: cast_nullable_to_non_nullable
          : modeRecueil as ConsentementDtoModeRecueilEnum?,
      referenceDocument: referenceDocument == const $CopyWithPlaceholder()
          ? _value.referenceDocument
          // ignore: cast_nullable_to_non_nullable
          : referenceDocument as String?,
      recueilliPar: recueilliPar == const $CopyWithPlaceholder()
          ? _value.recueilliPar
          // ignore: cast_nullable_to_non_nullable
          : recueilliPar as String?,
      decideLe: decideLe == const $CopyWithPlaceholder()
          ? _value.decideLe
          // ignore: cast_nullable_to_non_nullable
          : decideLe as DateTime?,
    );
  }
}

extension $ConsentementDtoCopyWith on ConsentementDto {
  /// Returns a callable class that can be used as follows: `instanceOfConsentementDto.copyWith(...)` or like so:`instanceOfConsentementDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$ConsentementDtoCWProxy get copyWith => _$ConsentementDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

ConsentementDto _$ConsentementDtoFromJson(Map<String, dynamic> json) =>
    $checkedCreate('ConsentementDto', json, ($checkedConvert) {
      final val = ConsentementDto(
        id: $checkedConvert('id', (v) => v as String?),
        finalite: $checkedConvert(
          'finalite',
          (v) => $enumDecodeNullable(_$ConsentementDtoFinaliteEnumEnumMap, v),
        ),
        accorde: $checkedConvert('accorde', (v) => v as bool?),
        versionModele: $checkedConvert('versionModele', (v) => v as String?),
        modeRecueil: $checkedConvert(
          'modeRecueil',
          (v) =>
              $enumDecodeNullable(_$ConsentementDtoModeRecueilEnumEnumMap, v),
        ),
        referenceDocument: $checkedConvert(
          'referenceDocument',
          (v) => v as String?,
        ),
        recueilliPar: $checkedConvert('recueilliPar', (v) => v as String?),
        decideLe: $checkedConvert(
          'decideLe',
          (v) => v == null ? null : DateTime.parse(v as String),
        ),
      );
      return val;
    });

Map<String, dynamic> _$ConsentementDtoToJson(
  ConsentementDto instance,
) => <String, dynamic>{
  'id': ?instance.id,
  'finalite': ?_$ConsentementDtoFinaliteEnumEnumMap[instance.finalite],
  'accorde': ?instance.accorde,
  'versionModele': ?instance.versionModele,
  'modeRecueil': ?_$ConsentementDtoModeRecueilEnumEnumMap[instance.modeRecueil],
  'referenceDocument': ?instance.referenceDocument,
  'recueilliPar': ?instance.recueilliPar,
  'decideLe': ?instance.decideLe?.toIso8601String(),
};

const _$ConsentementDtoFinaliteEnumEnumMap = {
  ConsentementDtoFinaliteEnum.NOTIFICATIONS: 'NOTIFICATIONS',
  ConsentementDtoFinaliteEnum.PIECES_IDENTITE: 'PIECES_IDENTITE',
  ConsentementDtoFinaliteEnum.TRANSMISSION_PROPRIETAIRE:
      'TRANSMISSION_PROPRIETAIRE',
  ConsentementDtoFinaliteEnum.APPLICATION_MOBILE: 'APPLICATION_MOBILE',
};

const _$ConsentementDtoModeRecueilEnumEnumMap = {
  ConsentementDtoModeRecueilEnum.PAPIER: 'PAPIER',
  ConsentementDtoModeRecueilEnum.ELECTRONIQUE: 'ELECTRONIQUE',
  ConsentementDtoModeRecueilEnum.ORAL: 'ORAL',
};
