// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'initiation_requete.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$InitiationRequeteCWProxy {
  InitiationRequete bailId(String bailId);

  InitiationRequete montant(int montant);

  InitiationRequete operateur(InitiationRequeteOperateurEnum operateur);

  InitiationRequete telephone(String telephone);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `InitiationRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// InitiationRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  InitiationRequete call({
    String bailId,
    int montant,
    InitiationRequeteOperateurEnum operateur,
    String telephone,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfInitiationRequete.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfInitiationRequete.copyWith.fieldName(...)`
class _$InitiationRequeteCWProxyImpl implements _$InitiationRequeteCWProxy {
  const _$InitiationRequeteCWProxyImpl(this._value);

  final InitiationRequete _value;

  @override
  InitiationRequete bailId(String bailId) => this(bailId: bailId);

  @override
  InitiationRequete montant(int montant) => this(montant: montant);

  @override
  InitiationRequete operateur(InitiationRequeteOperateurEnum operateur) =>
      this(operateur: operateur);

  @override
  InitiationRequete telephone(String telephone) => this(telephone: telephone);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `InitiationRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// InitiationRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  InitiationRequete call({
    Object? bailId = const $CopyWithPlaceholder(),
    Object? montant = const $CopyWithPlaceholder(),
    Object? operateur = const $CopyWithPlaceholder(),
    Object? telephone = const $CopyWithPlaceholder(),
  }) {
    return InitiationRequete(
      bailId: bailId == const $CopyWithPlaceholder()
          ? _value.bailId
          // ignore: cast_nullable_to_non_nullable
          : bailId as String,
      montant: montant == const $CopyWithPlaceholder()
          ? _value.montant
          // ignore: cast_nullable_to_non_nullable
          : montant as int,
      operateur: operateur == const $CopyWithPlaceholder()
          ? _value.operateur
          // ignore: cast_nullable_to_non_nullable
          : operateur as InitiationRequeteOperateurEnum,
      telephone: telephone == const $CopyWithPlaceholder()
          ? _value.telephone
          // ignore: cast_nullable_to_non_nullable
          : telephone as String,
    );
  }
}

extension $InitiationRequeteCopyWith on InitiationRequete {
  /// Returns a callable class that can be used as follows: `instanceOfInitiationRequete.copyWith(...)` or like so:`instanceOfInitiationRequete.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$InitiationRequeteCWProxy get copyWith =>
      _$InitiationRequeteCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

InitiationRequete _$InitiationRequeteFromJson(Map<String, dynamic> json) =>
    $checkedCreate('InitiationRequete', json, ($checkedConvert) {
      $checkKeys(
        json,
        requiredKeys: const ['bailId', 'montant', 'operateur', 'telephone'],
      );
      final val = InitiationRequete(
        bailId: $checkedConvert('bailId', (v) => v as String),
        montant: $checkedConvert('montant', (v) => (v as num).toInt()),
        operateur: $checkedConvert(
          'operateur',
          (v) => $enumDecode(_$InitiationRequeteOperateurEnumEnumMap, v),
        ),
        telephone: $checkedConvert('telephone', (v) => v as String),
      );
      return val;
    });

Map<String, dynamic> _$InitiationRequeteToJson(InitiationRequete instance) =>
    <String, dynamic>{
      'bailId': instance.bailId,
      'montant': instance.montant,
      'operateur': _$InitiationRequeteOperateurEnumEnumMap[instance.operateur]!,
      'telephone': instance.telephone,
    };

const _$InitiationRequeteOperateurEnumEnumMap = {
  InitiationRequeteOperateurEnum.MTN_MOMO: 'MTN_MOMO',
  InitiationRequeteOperateurEnum.ORANGE_MONEY: 'ORANGE_MONEY',
};
