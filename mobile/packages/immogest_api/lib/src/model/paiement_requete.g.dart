// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'paiement_requete.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$PaiementRequeteCWProxy {
  PaiementRequete bailId(String bailId);

  PaiementRequete montant(int montant);

  PaiementRequete mode(PaiementRequeteModeEnum mode);

  PaiementRequete reference(String? reference);

  PaiementRequete datePaiement(DateTime? datePaiement);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PaiementRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PaiementRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  PaiementRequete call({
    String bailId,
    int montant,
    PaiementRequeteModeEnum mode,
    String? reference,
    DateTime? datePaiement,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfPaiementRequete.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfPaiementRequete.copyWith.fieldName(...)`
class _$PaiementRequeteCWProxyImpl implements _$PaiementRequeteCWProxy {
  const _$PaiementRequeteCWProxyImpl(this._value);

  final PaiementRequete _value;

  @override
  PaiementRequete bailId(String bailId) => this(bailId: bailId);

  @override
  PaiementRequete montant(int montant) => this(montant: montant);

  @override
  PaiementRequete mode(PaiementRequeteModeEnum mode) => this(mode: mode);

  @override
  PaiementRequete reference(String? reference) => this(reference: reference);

  @override
  PaiementRequete datePaiement(DateTime? datePaiement) =>
      this(datePaiement: datePaiement);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PaiementRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PaiementRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  PaiementRequete call({
    Object? bailId = const $CopyWithPlaceholder(),
    Object? montant = const $CopyWithPlaceholder(),
    Object? mode = const $CopyWithPlaceholder(),
    Object? reference = const $CopyWithPlaceholder(),
    Object? datePaiement = const $CopyWithPlaceholder(),
  }) {
    return PaiementRequete(
      bailId: bailId == const $CopyWithPlaceholder()
          ? _value.bailId
          // ignore: cast_nullable_to_non_nullable
          : bailId as String,
      montant: montant == const $CopyWithPlaceholder()
          ? _value.montant
          // ignore: cast_nullable_to_non_nullable
          : montant as int,
      mode: mode == const $CopyWithPlaceholder()
          ? _value.mode
          // ignore: cast_nullable_to_non_nullable
          : mode as PaiementRequeteModeEnum,
      reference: reference == const $CopyWithPlaceholder()
          ? _value.reference
          // ignore: cast_nullable_to_non_nullable
          : reference as String?,
      datePaiement: datePaiement == const $CopyWithPlaceholder()
          ? _value.datePaiement
          // ignore: cast_nullable_to_non_nullable
          : datePaiement as DateTime?,
    );
  }
}

extension $PaiementRequeteCopyWith on PaiementRequete {
  /// Returns a callable class that can be used as follows: `instanceOfPaiementRequete.copyWith(...)` or like so:`instanceOfPaiementRequete.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$PaiementRequeteCWProxy get copyWith => _$PaiementRequeteCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

PaiementRequete _$PaiementRequeteFromJson(Map<String, dynamic> json) =>
    $checkedCreate('PaiementRequete', json, ($checkedConvert) {
      $checkKeys(json, requiredKeys: const ['bailId', 'montant', 'mode']);
      final val = PaiementRequete(
        bailId: $checkedConvert('bailId', (v) => v as String),
        montant: $checkedConvert('montant', (v) => (v as num).toInt()),
        mode: $checkedConvert(
          'mode',
          (v) => $enumDecode(_$PaiementRequeteModeEnumEnumMap, v),
        ),
        reference: $checkedConvert('reference', (v) => v as String?),
        datePaiement: $checkedConvert(
          'datePaiement',
          (v) => v == null ? null : DateTime.parse(v as String),
        ),
      );
      return val;
    });

Map<String, dynamic> _$PaiementRequeteToJson(PaiementRequete instance) =>
    <String, dynamic>{
      'bailId': instance.bailId,
      'montant': instance.montant,
      'mode': _$PaiementRequeteModeEnumEnumMap[instance.mode]!,
      'reference': ?instance.reference,
      'datePaiement': ?instance.datePaiement?.toIso8601String(),
    };

const _$PaiementRequeteModeEnumEnumMap = {
  PaiementRequeteModeEnum.ESPECES: 'ESPECES',
  PaiementRequeteModeEnum.VIREMENT: 'VIREMENT',
  PaiementRequeteModeEnum.MTN_MOMO: 'MTN_MOMO',
  PaiementRequeteModeEnum.ORANGE_MONEY: 'ORANGE_MONEY',
};
