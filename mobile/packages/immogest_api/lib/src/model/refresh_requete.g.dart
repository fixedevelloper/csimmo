// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'refresh_requete.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$RefreshRequeteCWProxy {
  RefreshRequete refreshToken(String refreshToken);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `RefreshRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// RefreshRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  RefreshRequete call({String refreshToken});
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfRefreshRequete.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfRefreshRequete.copyWith.fieldName(...)`
class _$RefreshRequeteCWProxyImpl implements _$RefreshRequeteCWProxy {
  const _$RefreshRequeteCWProxyImpl(this._value);

  final RefreshRequete _value;

  @override
  RefreshRequete refreshToken(String refreshToken) =>
      this(refreshToken: refreshToken);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `RefreshRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// RefreshRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  RefreshRequete call({Object? refreshToken = const $CopyWithPlaceholder()}) {
    return RefreshRequete(
      refreshToken: refreshToken == const $CopyWithPlaceholder()
          ? _value.refreshToken
          // ignore: cast_nullable_to_non_nullable
          : refreshToken as String,
    );
  }
}

extension $RefreshRequeteCopyWith on RefreshRequete {
  /// Returns a callable class that can be used as follows: `instanceOfRefreshRequete.copyWith(...)` or like so:`instanceOfRefreshRequete.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$RefreshRequeteCWProxy get copyWith => _$RefreshRequeteCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

RefreshRequete _$RefreshRequeteFromJson(Map<String, dynamic> json) =>
    $checkedCreate('RefreshRequete', json, ($checkedConvert) {
      $checkKeys(json, requiredKeys: const ['refreshToken']);
      final val = RefreshRequete(
        refreshToken: $checkedConvert('refreshToken', (v) => v as String),
      );
      return val;
    });

Map<String, dynamic> _$RefreshRequeteToJson(RefreshRequete instance) =>
    <String, dynamic>{'refreshToken': instance.refreshToken};
