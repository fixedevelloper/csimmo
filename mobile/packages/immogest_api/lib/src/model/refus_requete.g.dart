// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'refus_requete.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$RefusRequeteCWProxy {
  RefusRequete motif(String motif);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `RefusRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// RefusRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  RefusRequete call({String motif});
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfRefusRequete.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfRefusRequete.copyWith.fieldName(...)`
class _$RefusRequeteCWProxyImpl implements _$RefusRequeteCWProxy {
  const _$RefusRequeteCWProxyImpl(this._value);

  final RefusRequete _value;

  @override
  RefusRequete motif(String motif) => this(motif: motif);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `RefusRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// RefusRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  RefusRequete call({Object? motif = const $CopyWithPlaceholder()}) {
    return RefusRequete(
      motif: motif == const $CopyWithPlaceholder()
          ? _value.motif
          // ignore: cast_nullable_to_non_nullable
          : motif as String,
    );
  }
}

extension $RefusRequeteCopyWith on RefusRequete {
  /// Returns a callable class that can be used as follows: `instanceOfRefusRequete.copyWith(...)` or like so:`instanceOfRefusRequete.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$RefusRequeteCWProxy get copyWith => _$RefusRequeteCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

RefusRequete _$RefusRequeteFromJson(Map<String, dynamic> json) =>
    $checkedCreate('RefusRequete', json, ($checkedConvert) {
      $checkKeys(json, requiredKeys: const ['motif']);
      final val = RefusRequete(
        motif: $checkedConvert('motif', (v) => v as String),
      );
      return val;
    });

Map<String, dynamic> _$RefusRequeteToJson(RefusRequete instance) =>
    <String, dynamic>{'motif': instance.motif};
