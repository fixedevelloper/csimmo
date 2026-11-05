// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'demande_saisie.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$DemandeSaisieCWProxy {
  DemandeSaisie type(DemandeSaisieTypeEnum? type);

  DemandeSaisie personneId(String? personneId);

  DemandeSaisie motif(String? motif);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `DemandeSaisie(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// DemandeSaisie(...).copyWith(id: 12, name: "My name")
  /// ````
  DemandeSaisie call({
    DemandeSaisieTypeEnum? type,
    String? personneId,
    String? motif,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfDemandeSaisie.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfDemandeSaisie.copyWith.fieldName(...)`
class _$DemandeSaisieCWProxyImpl implements _$DemandeSaisieCWProxy {
  const _$DemandeSaisieCWProxyImpl(this._value);

  final DemandeSaisie _value;

  @override
  DemandeSaisie type(DemandeSaisieTypeEnum? type) => this(type: type);

  @override
  DemandeSaisie personneId(String? personneId) => this(personneId: personneId);

  @override
  DemandeSaisie motif(String? motif) => this(motif: motif);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `DemandeSaisie(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// DemandeSaisie(...).copyWith(id: 12, name: "My name")
  /// ````
  DemandeSaisie call({
    Object? type = const $CopyWithPlaceholder(),
    Object? personneId = const $CopyWithPlaceholder(),
    Object? motif = const $CopyWithPlaceholder(),
  }) {
    return DemandeSaisie(
      type: type == const $CopyWithPlaceholder()
          ? _value.type
          // ignore: cast_nullable_to_non_nullable
          : type as DemandeSaisieTypeEnum?,
      personneId: personneId == const $CopyWithPlaceholder()
          ? _value.personneId
          // ignore: cast_nullable_to_non_nullable
          : personneId as String?,
      motif: motif == const $CopyWithPlaceholder()
          ? _value.motif
          // ignore: cast_nullable_to_non_nullable
          : motif as String?,
    );
  }
}

extension $DemandeSaisieCopyWith on DemandeSaisie {
  /// Returns a callable class that can be used as follows: `instanceOfDemandeSaisie.copyWith(...)` or like so:`instanceOfDemandeSaisie.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$DemandeSaisieCWProxy get copyWith => _$DemandeSaisieCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

DemandeSaisie _$DemandeSaisieFromJson(Map<String, dynamic> json) =>
    $checkedCreate('DemandeSaisie', json, ($checkedConvert) {
      final val = DemandeSaisie(
        type: $checkedConvert(
          'type',
          (v) => $enumDecodeNullable(_$DemandeSaisieTypeEnumEnumMap, v),
        ),
        personneId: $checkedConvert('personneId', (v) => v as String?),
        motif: $checkedConvert('motif', (v) => v as String?),
      );
      return val;
    });

Map<String, dynamic> _$DemandeSaisieToJson(DemandeSaisie instance) =>
    <String, dynamic>{
      'type': ?_$DemandeSaisieTypeEnumEnumMap[instance.type],
      'personneId': ?instance.personneId,
      'motif': ?instance.motif,
    };

const _$DemandeSaisieTypeEnumEnumMap = {
  DemandeSaisieTypeEnum.LOCATAIRE: 'LOCATAIRE',
  DemandeSaisieTypeEnum.GARANT: 'GARANT',
  DemandeSaisieTypeEnum.PROPRIETAIRE: 'PROPRIETAIRE',
  DemandeSaisieTypeEnum.PRESTATAIRE: 'PRESTATAIRE',
};
