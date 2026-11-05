// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'calcul_requete.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$CalculRequeteCWProxy {
  CalculRequete mois(String mois);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `CalculRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// CalculRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  CalculRequete call({String mois});
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfCalculRequete.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfCalculRequete.copyWith.fieldName(...)`
class _$CalculRequeteCWProxyImpl implements _$CalculRequeteCWProxy {
  const _$CalculRequeteCWProxyImpl(this._value);

  final CalculRequete _value;

  @override
  CalculRequete mois(String mois) => this(mois: mois);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `CalculRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// CalculRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  CalculRequete call({Object? mois = const $CopyWithPlaceholder()}) {
    return CalculRequete(
      mois: mois == const $CopyWithPlaceholder()
          ? _value.mois
          // ignore: cast_nullable_to_non_nullable
          : mois as String,
    );
  }
}

extension $CalculRequeteCopyWith on CalculRequete {
  /// Returns a callable class that can be used as follows: `instanceOfCalculRequete.copyWith(...)` or like so:`instanceOfCalculRequete.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$CalculRequeteCWProxy get copyWith => _$CalculRequeteCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

CalculRequete _$CalculRequeteFromJson(Map<String, dynamic> json) =>
    $checkedCreate('CalculRequete', json, ($checkedConvert) {
      $checkKeys(json, requiredKeys: const ['mois']);
      final val = CalculRequete(
        mois: $checkedConvert('mois', (v) => v as String),
      );
      return val;
    });

Map<String, dynamic> _$CalculRequeteToJson(CalculRequete instance) =>
    <String, dynamic>{'mois': instance.mois};
