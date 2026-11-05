// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'otp_demande.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$OtpDemandeCWProxy {
  OtpDemande identifiant(String identifiant);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `OtpDemande(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// OtpDemande(...).copyWith(id: 12, name: "My name")
  /// ````
  OtpDemande call({String identifiant});
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfOtpDemande.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfOtpDemande.copyWith.fieldName(...)`
class _$OtpDemandeCWProxyImpl implements _$OtpDemandeCWProxy {
  const _$OtpDemandeCWProxyImpl(this._value);

  final OtpDemande _value;

  @override
  OtpDemande identifiant(String identifiant) => this(identifiant: identifiant);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `OtpDemande(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// OtpDemande(...).copyWith(id: 12, name: "My name")
  /// ````
  OtpDemande call({Object? identifiant = const $CopyWithPlaceholder()}) {
    return OtpDemande(
      identifiant: identifiant == const $CopyWithPlaceholder()
          ? _value.identifiant
          // ignore: cast_nullable_to_non_nullable
          : identifiant as String,
    );
  }
}

extension $OtpDemandeCopyWith on OtpDemande {
  /// Returns a callable class that can be used as follows: `instanceOfOtpDemande.copyWith(...)` or like so:`instanceOfOtpDemande.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$OtpDemandeCWProxy get copyWith => _$OtpDemandeCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

OtpDemande _$OtpDemandeFromJson(Map<String, dynamic> json) =>
    $checkedCreate('OtpDemande', json, ($checkedConvert) {
      $checkKeys(json, requiredKeys: const ['identifiant']);
      final val = OtpDemande(
        identifiant: $checkedConvert('identifiant', (v) => v as String),
      );
      return val;
    });

Map<String, dynamic> _$OtpDemandeToJson(OtpDemande instance) =>
    <String, dynamic>{'identifiant': instance.identifiant};
