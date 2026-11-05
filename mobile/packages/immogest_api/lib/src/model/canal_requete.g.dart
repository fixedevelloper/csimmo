// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'canal_requete.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$CanalRequeteCWProxy {
  CanalRequete actif(bool? actif);

  CanalRequete champs(Map<String, String>? champs);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `CanalRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// CanalRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  CanalRequete call({bool? actif, Map<String, String>? champs});
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfCanalRequete.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfCanalRequete.copyWith.fieldName(...)`
class _$CanalRequeteCWProxyImpl implements _$CanalRequeteCWProxy {
  const _$CanalRequeteCWProxyImpl(this._value);

  final CanalRequete _value;

  @override
  CanalRequete actif(bool? actif) => this(actif: actif);

  @override
  CanalRequete champs(Map<String, String>? champs) => this(champs: champs);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `CanalRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// CanalRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  CanalRequete call({
    Object? actif = const $CopyWithPlaceholder(),
    Object? champs = const $CopyWithPlaceholder(),
  }) {
    return CanalRequete(
      actif: actif == const $CopyWithPlaceholder()
          ? _value.actif
          // ignore: cast_nullable_to_non_nullable
          : actif as bool?,
      champs: champs == const $CopyWithPlaceholder()
          ? _value.champs
          // ignore: cast_nullable_to_non_nullable
          : champs as Map<String, String>?,
    );
  }
}

extension $CanalRequeteCopyWith on CanalRequete {
  /// Returns a callable class that can be used as follows: `instanceOfCanalRequete.copyWith(...)` or like so:`instanceOfCanalRequete.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$CanalRequeteCWProxy get copyWith => _$CanalRequeteCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

CanalRequete _$CanalRequeteFromJson(Map<String, dynamic> json) =>
    $checkedCreate('CanalRequete', json, ($checkedConvert) {
      final val = CanalRequete(
        actif: $checkedConvert('actif', (v) => v as bool?),
        champs: $checkedConvert(
          'champs',
          (v) => (v as Map<String, dynamic>?)?.map(
            (k, e) => MapEntry(k, e as String),
          ),
        ),
      );
      return val;
    });

Map<String, dynamic> _$CanalRequeteToJson(CanalRequete instance) =>
    <String, dynamic>{'actif': ?instance.actif, 'champs': ?instance.champs};
