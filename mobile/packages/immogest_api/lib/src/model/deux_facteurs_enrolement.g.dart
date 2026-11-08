// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'deux_facteurs_enrolement.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$DeuxFacteursEnrolementCWProxy {
  DeuxFacteursEnrolement secret(String? secret);

  DeuxFacteursEnrolement uri(String? uri);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `DeuxFacteursEnrolement(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// DeuxFacteursEnrolement(...).copyWith(id: 12, name: "My name")
  /// ````
  DeuxFacteursEnrolement call({String? secret, String? uri});
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfDeuxFacteursEnrolement.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfDeuxFacteursEnrolement.copyWith.fieldName(...)`
class _$DeuxFacteursEnrolementCWProxyImpl
    implements _$DeuxFacteursEnrolementCWProxy {
  const _$DeuxFacteursEnrolementCWProxyImpl(this._value);

  final DeuxFacteursEnrolement _value;

  @override
  DeuxFacteursEnrolement secret(String? secret) => this(secret: secret);

  @override
  DeuxFacteursEnrolement uri(String? uri) => this(uri: uri);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `DeuxFacteursEnrolement(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// DeuxFacteursEnrolement(...).copyWith(id: 12, name: "My name")
  /// ````
  DeuxFacteursEnrolement call({
    Object? secret = const $CopyWithPlaceholder(),
    Object? uri = const $CopyWithPlaceholder(),
  }) {
    return DeuxFacteursEnrolement(
      secret: secret == const $CopyWithPlaceholder()
          ? _value.secret
          // ignore: cast_nullable_to_non_nullable
          : secret as String?,
      uri: uri == const $CopyWithPlaceholder()
          ? _value.uri
          // ignore: cast_nullable_to_non_nullable
          : uri as String?,
    );
  }
}

extension $DeuxFacteursEnrolementCopyWith on DeuxFacteursEnrolement {
  /// Returns a callable class that can be used as follows: `instanceOfDeuxFacteursEnrolement.copyWith(...)` or like so:`instanceOfDeuxFacteursEnrolement.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$DeuxFacteursEnrolementCWProxy get copyWith =>
      _$DeuxFacteursEnrolementCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

DeuxFacteursEnrolement _$DeuxFacteursEnrolementFromJson(
  Map<String, dynamic> json,
) => $checkedCreate('DeuxFacteursEnrolement', json, ($checkedConvert) {
  final val = DeuxFacteursEnrolement(
    secret: $checkedConvert('secret', (v) => v as String?),
    uri: $checkedConvert('uri', (v) => v as String?),
  );
  return val;
});

Map<String, dynamic> _$DeuxFacteursEnrolementToJson(
  DeuxFacteursEnrolement instance,
) => <String, dynamic>{'secret': ?instance.secret, 'uri': ?instance.uri};
