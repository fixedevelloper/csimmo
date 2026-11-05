// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'consentement_saisie.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$ConsentementSaisieCWProxy {
  ConsentementSaisie type(ConsentementSaisieTypeEnum? type);

  ConsentementSaisie personneId(String? personneId);

  ConsentementSaisie finalite(ConsentementSaisieFinaliteEnum? finalite);

  ConsentementSaisie accorde(bool? accorde);

  ConsentementSaisie modeRecueil(
    ConsentementSaisieModeRecueilEnum? modeRecueil,
  );

  ConsentementSaisie referenceDocument(String? referenceDocument);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ConsentementSaisie(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ConsentementSaisie(...).copyWith(id: 12, name: "My name")
  /// ````
  ConsentementSaisie call({
    ConsentementSaisieTypeEnum? type,
    String? personneId,
    ConsentementSaisieFinaliteEnum? finalite,
    bool? accorde,
    ConsentementSaisieModeRecueilEnum? modeRecueil,
    String? referenceDocument,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfConsentementSaisie.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfConsentementSaisie.copyWith.fieldName(...)`
class _$ConsentementSaisieCWProxyImpl implements _$ConsentementSaisieCWProxy {
  const _$ConsentementSaisieCWProxyImpl(this._value);

  final ConsentementSaisie _value;

  @override
  ConsentementSaisie type(ConsentementSaisieTypeEnum? type) => this(type: type);

  @override
  ConsentementSaisie personneId(String? personneId) =>
      this(personneId: personneId);

  @override
  ConsentementSaisie finalite(ConsentementSaisieFinaliteEnum? finalite) =>
      this(finalite: finalite);

  @override
  ConsentementSaisie accorde(bool? accorde) => this(accorde: accorde);

  @override
  ConsentementSaisie modeRecueil(
    ConsentementSaisieModeRecueilEnum? modeRecueil,
  ) => this(modeRecueil: modeRecueil);

  @override
  ConsentementSaisie referenceDocument(String? referenceDocument) =>
      this(referenceDocument: referenceDocument);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ConsentementSaisie(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ConsentementSaisie(...).copyWith(id: 12, name: "My name")
  /// ````
  ConsentementSaisie call({
    Object? type = const $CopyWithPlaceholder(),
    Object? personneId = const $CopyWithPlaceholder(),
    Object? finalite = const $CopyWithPlaceholder(),
    Object? accorde = const $CopyWithPlaceholder(),
    Object? modeRecueil = const $CopyWithPlaceholder(),
    Object? referenceDocument = const $CopyWithPlaceholder(),
  }) {
    return ConsentementSaisie(
      type: type == const $CopyWithPlaceholder()
          ? _value.type
          // ignore: cast_nullable_to_non_nullable
          : type as ConsentementSaisieTypeEnum?,
      personneId: personneId == const $CopyWithPlaceholder()
          ? _value.personneId
          // ignore: cast_nullable_to_non_nullable
          : personneId as String?,
      finalite: finalite == const $CopyWithPlaceholder()
          ? _value.finalite
          // ignore: cast_nullable_to_non_nullable
          : finalite as ConsentementSaisieFinaliteEnum?,
      accorde: accorde == const $CopyWithPlaceholder()
          ? _value.accorde
          // ignore: cast_nullable_to_non_nullable
          : accorde as bool?,
      modeRecueil: modeRecueil == const $CopyWithPlaceholder()
          ? _value.modeRecueil
          // ignore: cast_nullable_to_non_nullable
          : modeRecueil as ConsentementSaisieModeRecueilEnum?,
      referenceDocument: referenceDocument == const $CopyWithPlaceholder()
          ? _value.referenceDocument
          // ignore: cast_nullable_to_non_nullable
          : referenceDocument as String?,
    );
  }
}

extension $ConsentementSaisieCopyWith on ConsentementSaisie {
  /// Returns a callable class that can be used as follows: `instanceOfConsentementSaisie.copyWith(...)` or like so:`instanceOfConsentementSaisie.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$ConsentementSaisieCWProxy get copyWith =>
      _$ConsentementSaisieCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

ConsentementSaisie _$ConsentementSaisieFromJson(Map<String, dynamic> json) =>
    $checkedCreate('ConsentementSaisie', json, ($checkedConvert) {
      final val = ConsentementSaisie(
        type: $checkedConvert(
          'type',
          (v) => $enumDecodeNullable(_$ConsentementSaisieTypeEnumEnumMap, v),
        ),
        personneId: $checkedConvert('personneId', (v) => v as String?),
        finalite: $checkedConvert(
          'finalite',
          (v) =>
              $enumDecodeNullable(_$ConsentementSaisieFinaliteEnumEnumMap, v),
        ),
        accorde: $checkedConvert('accorde', (v) => v as bool?),
        modeRecueil: $checkedConvert(
          'modeRecueil',
          (v) => $enumDecodeNullable(
            _$ConsentementSaisieModeRecueilEnumEnumMap,
            v,
          ),
        ),
        referenceDocument: $checkedConvert(
          'referenceDocument',
          (v) => v as String?,
        ),
      );
      return val;
    });

Map<String, dynamic> _$ConsentementSaisieToJson(ConsentementSaisie instance) =>
    <String, dynamic>{
      'type': ?_$ConsentementSaisieTypeEnumEnumMap[instance.type],
      'personneId': ?instance.personneId,
      'finalite': ?_$ConsentementSaisieFinaliteEnumEnumMap[instance.finalite],
      'accorde': ?instance.accorde,
      'modeRecueil':
          ?_$ConsentementSaisieModeRecueilEnumEnumMap[instance.modeRecueil],
      'referenceDocument': ?instance.referenceDocument,
    };

const _$ConsentementSaisieTypeEnumEnumMap = {
  ConsentementSaisieTypeEnum.LOCATAIRE: 'LOCATAIRE',
  ConsentementSaisieTypeEnum.GARANT: 'GARANT',
  ConsentementSaisieTypeEnum.PROPRIETAIRE: 'PROPRIETAIRE',
  ConsentementSaisieTypeEnum.PRESTATAIRE: 'PRESTATAIRE',
};

const _$ConsentementSaisieFinaliteEnumEnumMap = {
  ConsentementSaisieFinaliteEnum.NOTIFICATIONS: 'NOTIFICATIONS',
  ConsentementSaisieFinaliteEnum.PIECES_IDENTITE: 'PIECES_IDENTITE',
  ConsentementSaisieFinaliteEnum.TRANSMISSION_PROPRIETAIRE:
      'TRANSMISSION_PROPRIETAIRE',
  ConsentementSaisieFinaliteEnum.APPLICATION_MOBILE: 'APPLICATION_MOBILE',
};

const _$ConsentementSaisieModeRecueilEnumEnumMap = {
  ConsentementSaisieModeRecueilEnum.PAPIER: 'PAPIER',
  ConsentementSaisieModeRecueilEnum.ELECTRONIQUE: 'ELECTRONIQUE',
  ConsentementSaisieModeRecueilEnum.ORAL: 'ORAL',
};
