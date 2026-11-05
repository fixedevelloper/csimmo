// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'statut_requete.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$StatutRequeteCWProxy {
  StatutRequete statut(StatutRequeteStatutEnum statut);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `StatutRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// StatutRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  StatutRequete call({StatutRequeteStatutEnum statut});
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfStatutRequete.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfStatutRequete.copyWith.fieldName(...)`
class _$StatutRequeteCWProxyImpl implements _$StatutRequeteCWProxy {
  const _$StatutRequeteCWProxyImpl(this._value);

  final StatutRequete _value;

  @override
  StatutRequete statut(StatutRequeteStatutEnum statut) => this(statut: statut);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `StatutRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// StatutRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  StatutRequete call({Object? statut = const $CopyWithPlaceholder()}) {
    return StatutRequete(
      statut: statut == const $CopyWithPlaceholder()
          ? _value.statut
          // ignore: cast_nullable_to_non_nullable
          : statut as StatutRequeteStatutEnum,
    );
  }
}

extension $StatutRequeteCopyWith on StatutRequete {
  /// Returns a callable class that can be used as follows: `instanceOfStatutRequete.copyWith(...)` or like so:`instanceOfStatutRequete.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$StatutRequeteCWProxy get copyWith => _$StatutRequeteCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

StatutRequete _$StatutRequeteFromJson(Map<String, dynamic> json) =>
    $checkedCreate('StatutRequete', json, ($checkedConvert) {
      $checkKeys(json, requiredKeys: const ['statut']);
      final val = StatutRequete(
        statut: $checkedConvert(
          'statut',
          (v) => $enumDecode(_$StatutRequeteStatutEnumEnumMap, v),
        ),
      );
      return val;
    });

Map<String, dynamic> _$StatutRequeteToJson(StatutRequete instance) =>
    <String, dynamic>{
      'statut': _$StatutRequeteStatutEnumEnumMap[instance.statut]!,
    };

const _$StatutRequeteStatutEnumEnumMap = {
  StatutRequeteStatutEnum.LIBRE: 'LIBRE',
  StatutRequeteStatutEnum.OCCUPE: 'OCCUPE',
  StatutRequeteStatutEnum.EN_TRAVAUX: 'EN_TRAVAUX',
};
