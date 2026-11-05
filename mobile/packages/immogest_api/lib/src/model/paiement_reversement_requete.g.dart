// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'paiement_reversement_requete.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$PaiementReversementRequeteCWProxy {
  PaiementReversementRequete mode(String mode);

  PaiementReversementRequete reference(String? reference);

  PaiementReversementRequete date(DateTime? date);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PaiementReversementRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PaiementReversementRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  PaiementReversementRequete call({
    String mode,
    String? reference,
    DateTime? date,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfPaiementReversementRequete.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfPaiementReversementRequete.copyWith.fieldName(...)`
class _$PaiementReversementRequeteCWProxyImpl
    implements _$PaiementReversementRequeteCWProxy {
  const _$PaiementReversementRequeteCWProxyImpl(this._value);

  final PaiementReversementRequete _value;

  @override
  PaiementReversementRequete mode(String mode) => this(mode: mode);

  @override
  PaiementReversementRequete reference(String? reference) =>
      this(reference: reference);

  @override
  PaiementReversementRequete date(DateTime? date) => this(date: date);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PaiementReversementRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PaiementReversementRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  PaiementReversementRequete call({
    Object? mode = const $CopyWithPlaceholder(),
    Object? reference = const $CopyWithPlaceholder(),
    Object? date = const $CopyWithPlaceholder(),
  }) {
    return PaiementReversementRequete(
      mode: mode == const $CopyWithPlaceholder()
          ? _value.mode
          // ignore: cast_nullable_to_non_nullable
          : mode as String,
      reference: reference == const $CopyWithPlaceholder()
          ? _value.reference
          // ignore: cast_nullable_to_non_nullable
          : reference as String?,
      date: date == const $CopyWithPlaceholder()
          ? _value.date
          // ignore: cast_nullable_to_non_nullable
          : date as DateTime?,
    );
  }
}

extension $PaiementReversementRequeteCopyWith on PaiementReversementRequete {
  /// Returns a callable class that can be used as follows: `instanceOfPaiementReversementRequete.copyWith(...)` or like so:`instanceOfPaiementReversementRequete.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$PaiementReversementRequeteCWProxy get copyWith =>
      _$PaiementReversementRequeteCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

PaiementReversementRequete _$PaiementReversementRequeteFromJson(
  Map<String, dynamic> json,
) => $checkedCreate('PaiementReversementRequete', json, ($checkedConvert) {
  $checkKeys(json, requiredKeys: const ['mode']);
  final val = PaiementReversementRequete(
    mode: $checkedConvert('mode', (v) => v as String),
    reference: $checkedConvert('reference', (v) => v as String?),
    date: $checkedConvert(
      'date',
      (v) => v == null ? null : DateTime.parse(v as String),
    ),
  );
  return val;
});

Map<String, dynamic> _$PaiementReversementRequeteToJson(
  PaiementReversementRequete instance,
) => <String, dynamic>{
  'mode': instance.mode,
  'reference': ?instance.reference,
  'date': ?instance.date?.toIso8601String(),
};
