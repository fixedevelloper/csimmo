// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'otp_verification.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$OtpVerificationCWProxy {
  OtpVerification identifiant(String identifiant);

  OtpVerification code(String code);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `OtpVerification(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// OtpVerification(...).copyWith(id: 12, name: "My name")
  /// ````
  OtpVerification call({String identifiant, String code});
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfOtpVerification.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfOtpVerification.copyWith.fieldName(...)`
class _$OtpVerificationCWProxyImpl implements _$OtpVerificationCWProxy {
  const _$OtpVerificationCWProxyImpl(this._value);

  final OtpVerification _value;

  @override
  OtpVerification identifiant(String identifiant) =>
      this(identifiant: identifiant);

  @override
  OtpVerification code(String code) => this(code: code);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `OtpVerification(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// OtpVerification(...).copyWith(id: 12, name: "My name")
  /// ````
  OtpVerification call({
    Object? identifiant = const $CopyWithPlaceholder(),
    Object? code = const $CopyWithPlaceholder(),
  }) {
    return OtpVerification(
      identifiant: identifiant == const $CopyWithPlaceholder()
          ? _value.identifiant
          // ignore: cast_nullable_to_non_nullable
          : identifiant as String,
      code: code == const $CopyWithPlaceholder()
          ? _value.code
          // ignore: cast_nullable_to_non_nullable
          : code as String,
    );
  }
}

extension $OtpVerificationCopyWith on OtpVerification {
  /// Returns a callable class that can be used as follows: `instanceOfOtpVerification.copyWith(...)` or like so:`instanceOfOtpVerification.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$OtpVerificationCWProxy get copyWith => _$OtpVerificationCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

OtpVerification _$OtpVerificationFromJson(Map<String, dynamic> json) =>
    $checkedCreate('OtpVerification', json, ($checkedConvert) {
      $checkKeys(json, requiredKeys: const ['identifiant', 'code']);
      final val = OtpVerification(
        identifiant: $checkedConvert('identifiant', (v) => v as String),
        code: $checkedConvert('code', (v) => v as String),
      );
      return val;
    });

Map<String, dynamic> _$OtpVerificationToJson(OtpVerification instance) =>
    <String, dynamic>{
      'identifiant': instance.identifiant,
      'code': instance.code,
    };
