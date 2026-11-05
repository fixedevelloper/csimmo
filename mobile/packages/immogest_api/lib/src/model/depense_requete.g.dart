// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'depense_requete.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$DepenseRequeteCWProxy {
  DepenseRequete immeubleId(String immeubleId);

  DepenseRequete lotId(String? lotId);

  DepenseRequete dateDepense(DateTime dateDepense);

  DepenseRequete nature(String nature);

  DepenseRequete montant(int montant);

  DepenseRequete reference(String? reference);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `DepenseRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// DepenseRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  DepenseRequete call({
    String immeubleId,
    String? lotId,
    DateTime dateDepense,
    String nature,
    int montant,
    String? reference,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfDepenseRequete.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfDepenseRequete.copyWith.fieldName(...)`
class _$DepenseRequeteCWProxyImpl implements _$DepenseRequeteCWProxy {
  const _$DepenseRequeteCWProxyImpl(this._value);

  final DepenseRequete _value;

  @override
  DepenseRequete immeubleId(String immeubleId) => this(immeubleId: immeubleId);

  @override
  DepenseRequete lotId(String? lotId) => this(lotId: lotId);

  @override
  DepenseRequete dateDepense(DateTime dateDepense) =>
      this(dateDepense: dateDepense);

  @override
  DepenseRequete nature(String nature) => this(nature: nature);

  @override
  DepenseRequete montant(int montant) => this(montant: montant);

  @override
  DepenseRequete reference(String? reference) => this(reference: reference);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `DepenseRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// DepenseRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  DepenseRequete call({
    Object? immeubleId = const $CopyWithPlaceholder(),
    Object? lotId = const $CopyWithPlaceholder(),
    Object? dateDepense = const $CopyWithPlaceholder(),
    Object? nature = const $CopyWithPlaceholder(),
    Object? montant = const $CopyWithPlaceholder(),
    Object? reference = const $CopyWithPlaceholder(),
  }) {
    return DepenseRequete(
      immeubleId: immeubleId == const $CopyWithPlaceholder()
          ? _value.immeubleId
          // ignore: cast_nullable_to_non_nullable
          : immeubleId as String,
      lotId: lotId == const $CopyWithPlaceholder()
          ? _value.lotId
          // ignore: cast_nullable_to_non_nullable
          : lotId as String?,
      dateDepense: dateDepense == const $CopyWithPlaceholder()
          ? _value.dateDepense
          // ignore: cast_nullable_to_non_nullable
          : dateDepense as DateTime,
      nature: nature == const $CopyWithPlaceholder()
          ? _value.nature
          // ignore: cast_nullable_to_non_nullable
          : nature as String,
      montant: montant == const $CopyWithPlaceholder()
          ? _value.montant
          // ignore: cast_nullable_to_non_nullable
          : montant as int,
      reference: reference == const $CopyWithPlaceholder()
          ? _value.reference
          // ignore: cast_nullable_to_non_nullable
          : reference as String?,
    );
  }
}

extension $DepenseRequeteCopyWith on DepenseRequete {
  /// Returns a callable class that can be used as follows: `instanceOfDepenseRequete.copyWith(...)` or like so:`instanceOfDepenseRequete.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$DepenseRequeteCWProxy get copyWith => _$DepenseRequeteCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

DepenseRequete _$DepenseRequeteFromJson(Map<String, dynamic> json) =>
    $checkedCreate('DepenseRequete', json, ($checkedConvert) {
      $checkKeys(
        json,
        requiredKeys: const ['immeubleId', 'dateDepense', 'nature', 'montant'],
      );
      final val = DepenseRequete(
        immeubleId: $checkedConvert('immeubleId', (v) => v as String),
        lotId: $checkedConvert('lotId', (v) => v as String?),
        dateDepense: $checkedConvert(
          'dateDepense',
          (v) => DateTime.parse(v as String),
        ),
        nature: $checkedConvert('nature', (v) => v as String),
        montant: $checkedConvert('montant', (v) => (v as num).toInt()),
        reference: $checkedConvert('reference', (v) => v as String?),
      );
      return val;
    });

Map<String, dynamic> _$DepenseRequeteToJson(DepenseRequete instance) =>
    <String, dynamic>{
      'immeubleId': instance.immeubleId,
      'lotId': ?instance.lotId,
      'dateDepense': instance.dateDepense.toIso8601String(),
      'nature': instance.nature,
      'montant': instance.montant,
      'reference': ?instance.reference,
    };
