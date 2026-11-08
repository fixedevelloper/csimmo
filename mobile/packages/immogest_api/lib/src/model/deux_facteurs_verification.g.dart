// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'deux_facteurs_verification.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$DeuxFacteursVerificationCWProxy {
  DeuxFacteursVerification defi(String defi);

  DeuxFacteursVerification code(String code);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `DeuxFacteursVerification(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// DeuxFacteursVerification(...).copyWith(id: 12, name: "My name")
  /// ````
  DeuxFacteursVerification call({String defi, String code});
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfDeuxFacteursVerification.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfDeuxFacteursVerification.copyWith.fieldName(...)`
class _$DeuxFacteursVerificationCWProxyImpl
    implements _$DeuxFacteursVerificationCWProxy {
  const _$DeuxFacteursVerificationCWProxyImpl(this._value);

  final DeuxFacteursVerification _value;

  @override
  DeuxFacteursVerification defi(String defi) => this(defi: defi);

  @override
  DeuxFacteursVerification code(String code) => this(code: code);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `DeuxFacteursVerification(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// DeuxFacteursVerification(...).copyWith(id: 12, name: "My name")
  /// ````
  DeuxFacteursVerification call({
    Object? defi = const $CopyWithPlaceholder(),
    Object? code = const $CopyWithPlaceholder(),
  }) {
    return DeuxFacteursVerification(
      defi: defi == const $CopyWithPlaceholder()
          ? _value.defi
          // ignore: cast_nullable_to_non_nullable
          : defi as String,
      code: code == const $CopyWithPlaceholder()
          ? _value.code
          // ignore: cast_nullable_to_non_nullable
          : code as String,
    );
  }
}

extension $DeuxFacteursVerificationCopyWith on DeuxFacteursVerification {
  /// Returns a callable class that can be used as follows: `instanceOfDeuxFacteursVerification.copyWith(...)` or like so:`instanceOfDeuxFacteursVerification.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$DeuxFacteursVerificationCWProxy get copyWith =>
      _$DeuxFacteursVerificationCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

DeuxFacteursVerification _$DeuxFacteursVerificationFromJson(
  Map<String, dynamic> json,
) => $checkedCreate('DeuxFacteursVerification', json, ($checkedConvert) {
  $checkKeys(json, requiredKeys: const ['defi', 'code']);
  final val = DeuxFacteursVerification(
    defi: $checkedConvert('defi', (v) => v as String),
    code: $checkedConvert('code', (v) => v as String),
  );
  return val;
});

Map<String, dynamic> _$DeuxFacteursVerificationToJson(
  DeuxFacteursVerification instance,
) => <String, dynamic>{'defi': instance.defi, 'code': instance.code};
