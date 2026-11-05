// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'compteur_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$CompteurDtoCWProxy {
  CompteurDto id(String? id);

  CompteurDto type(CompteurDtoTypeEnum? type);

  CompteurDto numero(String? numero);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `CompteurDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// CompteurDto(...).copyWith(id: 12, name: "My name")
  /// ````
  CompteurDto call({String? id, CompteurDtoTypeEnum? type, String? numero});
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfCompteurDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfCompteurDto.copyWith.fieldName(...)`
class _$CompteurDtoCWProxyImpl implements _$CompteurDtoCWProxy {
  const _$CompteurDtoCWProxyImpl(this._value);

  final CompteurDto _value;

  @override
  CompteurDto id(String? id) => this(id: id);

  @override
  CompteurDto type(CompteurDtoTypeEnum? type) => this(type: type);

  @override
  CompteurDto numero(String? numero) => this(numero: numero);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `CompteurDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// CompteurDto(...).copyWith(id: 12, name: "My name")
  /// ````
  CompteurDto call({
    Object? id = const $CopyWithPlaceholder(),
    Object? type = const $CopyWithPlaceholder(),
    Object? numero = const $CopyWithPlaceholder(),
  }) {
    return CompteurDto(
      id: id == const $CopyWithPlaceholder()
          ? _value.id
          // ignore: cast_nullable_to_non_nullable
          : id as String?,
      type: type == const $CopyWithPlaceholder()
          ? _value.type
          // ignore: cast_nullable_to_non_nullable
          : type as CompteurDtoTypeEnum?,
      numero: numero == const $CopyWithPlaceholder()
          ? _value.numero
          // ignore: cast_nullable_to_non_nullable
          : numero as String?,
    );
  }
}

extension $CompteurDtoCopyWith on CompteurDto {
  /// Returns a callable class that can be used as follows: `instanceOfCompteurDto.copyWith(...)` or like so:`instanceOfCompteurDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$CompteurDtoCWProxy get copyWith => _$CompteurDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

CompteurDto _$CompteurDtoFromJson(Map<String, dynamic> json) =>
    $checkedCreate('CompteurDto', json, ($checkedConvert) {
      final val = CompteurDto(
        id: $checkedConvert('id', (v) => v as String?),
        type: $checkedConvert(
          'type',
          (v) => $enumDecodeNullable(_$CompteurDtoTypeEnumEnumMap, v),
        ),
        numero: $checkedConvert('numero', (v) => v as String?),
      );
      return val;
    });

Map<String, dynamic> _$CompteurDtoToJson(CompteurDto instance) =>
    <String, dynamic>{
      'id': ?instance.id,
      'type': ?_$CompteurDtoTypeEnumEnumMap[instance.type],
      'numero': ?instance.numero,
    };

const _$CompteurDtoTypeEnumEnumMap = {
  CompteurDtoTypeEnum.EAU: 'EAU',
  CompteurDtoTypeEnum.ELECTRICITE: 'ELECTRICITE',
  CompteurDtoTypeEnum.GAZ: 'GAZ',
};
