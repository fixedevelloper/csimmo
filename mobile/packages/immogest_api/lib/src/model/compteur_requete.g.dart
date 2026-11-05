// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'compteur_requete.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$CompteurRequeteCWProxy {
  CompteurRequete type(CompteurRequeteTypeEnum type);

  CompteurRequete numero(String numero);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `CompteurRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// CompteurRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  CompteurRequete call({CompteurRequeteTypeEnum type, String numero});
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfCompteurRequete.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfCompteurRequete.copyWith.fieldName(...)`
class _$CompteurRequeteCWProxyImpl implements _$CompteurRequeteCWProxy {
  const _$CompteurRequeteCWProxyImpl(this._value);

  final CompteurRequete _value;

  @override
  CompteurRequete type(CompteurRequeteTypeEnum type) => this(type: type);

  @override
  CompteurRequete numero(String numero) => this(numero: numero);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `CompteurRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// CompteurRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  CompteurRequete call({
    Object? type = const $CopyWithPlaceholder(),
    Object? numero = const $CopyWithPlaceholder(),
  }) {
    return CompteurRequete(
      type: type == const $CopyWithPlaceholder()
          ? _value.type
          // ignore: cast_nullable_to_non_nullable
          : type as CompteurRequeteTypeEnum,
      numero: numero == const $CopyWithPlaceholder()
          ? _value.numero
          // ignore: cast_nullable_to_non_nullable
          : numero as String,
    );
  }
}

extension $CompteurRequeteCopyWith on CompteurRequete {
  /// Returns a callable class that can be used as follows: `instanceOfCompteurRequete.copyWith(...)` or like so:`instanceOfCompteurRequete.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$CompteurRequeteCWProxy get copyWith => _$CompteurRequeteCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

CompteurRequete _$CompteurRequeteFromJson(Map<String, dynamic> json) =>
    $checkedCreate('CompteurRequete', json, ($checkedConvert) {
      $checkKeys(json, requiredKeys: const ['type', 'numero']);
      final val = CompteurRequete(
        type: $checkedConvert(
          'type',
          (v) => $enumDecode(_$CompteurRequeteTypeEnumEnumMap, v),
        ),
        numero: $checkedConvert('numero', (v) => v as String),
      );
      return val;
    });

Map<String, dynamic> _$CompteurRequeteToJson(CompteurRequete instance) =>
    <String, dynamic>{
      'type': _$CompteurRequeteTypeEnumEnumMap[instance.type]!,
      'numero': instance.numero,
    };

const _$CompteurRequeteTypeEnumEnumMap = {
  CompteurRequeteTypeEnum.EAU: 'EAU',
  CompteurRequeteTypeEnum.ELECTRICITE: 'ELECTRICITE',
  CompteurRequeteTypeEnum.GAZ: 'GAZ',
};
