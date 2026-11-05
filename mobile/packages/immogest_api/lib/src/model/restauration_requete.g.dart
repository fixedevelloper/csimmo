// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'restauration_requete.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$RestaurationRequeteCWProxy {
  RestaurationRequete confirmation(String confirmation);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `RestaurationRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// RestaurationRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  RestaurationRequete call({String confirmation});
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfRestaurationRequete.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfRestaurationRequete.copyWith.fieldName(...)`
class _$RestaurationRequeteCWProxyImpl implements _$RestaurationRequeteCWProxy {
  const _$RestaurationRequeteCWProxyImpl(this._value);

  final RestaurationRequete _value;

  @override
  RestaurationRequete confirmation(String confirmation) =>
      this(confirmation: confirmation);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `RestaurationRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// RestaurationRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  RestaurationRequete call({
    Object? confirmation = const $CopyWithPlaceholder(),
  }) {
    return RestaurationRequete(
      confirmation: confirmation == const $CopyWithPlaceholder()
          ? _value.confirmation
          // ignore: cast_nullable_to_non_nullable
          : confirmation as String,
    );
  }
}

extension $RestaurationRequeteCopyWith on RestaurationRequete {
  /// Returns a callable class that can be used as follows: `instanceOfRestaurationRequete.copyWith(...)` or like so:`instanceOfRestaurationRequete.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$RestaurationRequeteCWProxy get copyWith =>
      _$RestaurationRequeteCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

RestaurationRequete _$RestaurationRequeteFromJson(Map<String, dynamic> json) =>
    $checkedCreate('RestaurationRequete', json, ($checkedConvert) {
      $checkKeys(json, requiredKeys: const ['confirmation']);
      final val = RestaurationRequete(
        confirmation: $checkedConvert('confirmation', (v) => v as String),
      );
      return val;
    });

Map<String, dynamic> _$RestaurationRequeteToJson(
  RestaurationRequete instance,
) => <String, dynamic>{'confirmation': instance.confirmation};
