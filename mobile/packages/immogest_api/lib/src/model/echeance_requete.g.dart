// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'echeance_requete.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$EcheanceRequeteCWProxy {
  EcheanceRequete echeanceId(String echeanceId);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `EcheanceRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// EcheanceRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  EcheanceRequete call({String echeanceId});
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfEcheanceRequete.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfEcheanceRequete.copyWith.fieldName(...)`
class _$EcheanceRequeteCWProxyImpl implements _$EcheanceRequeteCWProxy {
  const _$EcheanceRequeteCWProxyImpl(this._value);

  final EcheanceRequete _value;

  @override
  EcheanceRequete echeanceId(String echeanceId) => this(echeanceId: echeanceId);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `EcheanceRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// EcheanceRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  EcheanceRequete call({Object? echeanceId = const $CopyWithPlaceholder()}) {
    return EcheanceRequete(
      echeanceId: echeanceId == const $CopyWithPlaceholder()
          ? _value.echeanceId
          // ignore: cast_nullable_to_non_nullable
          : echeanceId as String,
    );
  }
}

extension $EcheanceRequeteCopyWith on EcheanceRequete {
  /// Returns a callable class that can be used as follows: `instanceOfEcheanceRequete.copyWith(...)` or like so:`instanceOfEcheanceRequete.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$EcheanceRequeteCWProxy get copyWith => _$EcheanceRequeteCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

EcheanceRequete _$EcheanceRequeteFromJson(Map<String, dynamic> json) =>
    $checkedCreate('EcheanceRequete', json, ($checkedConvert) {
      $checkKeys(json, requiredKeys: const ['echeanceId']);
      final val = EcheanceRequete(
        echeanceId: $checkedConvert('echeanceId', (v) => v as String),
      );
      return val;
    });

Map<String, dynamic> _$EcheanceRequeteToJson(EcheanceRequete instance) =>
    <String, dynamic>{'echeanceId': instance.echeanceId};
