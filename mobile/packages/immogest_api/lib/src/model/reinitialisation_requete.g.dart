// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'reinitialisation_requete.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$ReinitialisationRequeteCWProxy {
  ReinitialisationRequete motDePasse(String motDePasse);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ReinitialisationRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ReinitialisationRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  ReinitialisationRequete call({String motDePasse});
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfReinitialisationRequete.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfReinitialisationRequete.copyWith.fieldName(...)`
class _$ReinitialisationRequeteCWProxyImpl
    implements _$ReinitialisationRequeteCWProxy {
  const _$ReinitialisationRequeteCWProxyImpl(this._value);

  final ReinitialisationRequete _value;

  @override
  ReinitialisationRequete motDePasse(String motDePasse) =>
      this(motDePasse: motDePasse);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ReinitialisationRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ReinitialisationRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  ReinitialisationRequete call({
    Object? motDePasse = const $CopyWithPlaceholder(),
  }) {
    return ReinitialisationRequete(
      motDePasse: motDePasse == const $CopyWithPlaceholder()
          ? _value.motDePasse
          // ignore: cast_nullable_to_non_nullable
          : motDePasse as String,
    );
  }
}

extension $ReinitialisationRequeteCopyWith on ReinitialisationRequete {
  /// Returns a callable class that can be used as follows: `instanceOfReinitialisationRequete.copyWith(...)` or like so:`instanceOfReinitialisationRequete.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$ReinitialisationRequeteCWProxy get copyWith =>
      _$ReinitialisationRequeteCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

ReinitialisationRequete _$ReinitialisationRequeteFromJson(
  Map<String, dynamic> json,
) => $checkedCreate('ReinitialisationRequete', json, ($checkedConvert) {
  $checkKeys(json, requiredKeys: const ['motDePasse']);
  final val = ReinitialisationRequete(
    motDePasse: $checkedConvert('motDePasse', (v) => v as String),
  );
  return val;
});

Map<String, dynamic> _$ReinitialisationRequeteToJson(
  ReinitialisationRequete instance,
) => <String, dynamic>{'motDePasse': instance.motDePasse};
