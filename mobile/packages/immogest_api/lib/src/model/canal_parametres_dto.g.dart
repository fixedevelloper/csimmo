// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'canal_parametres_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$CanalParametresDtoCWProxy {
  CanalParametresDto canal(CanalParametresDtoCanalEnum? canal);

  CanalParametresDto actif(bool? actif);

  CanalParametresDto configure(bool? configure);

  CanalParametresDto champs(List<ChampDto>? champs);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `CanalParametresDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// CanalParametresDto(...).copyWith(id: 12, name: "My name")
  /// ````
  CanalParametresDto call({
    CanalParametresDtoCanalEnum? canal,
    bool? actif,
    bool? configure,
    List<ChampDto>? champs,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfCanalParametresDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfCanalParametresDto.copyWith.fieldName(...)`
class _$CanalParametresDtoCWProxyImpl implements _$CanalParametresDtoCWProxy {
  const _$CanalParametresDtoCWProxyImpl(this._value);

  final CanalParametresDto _value;

  @override
  CanalParametresDto canal(CanalParametresDtoCanalEnum? canal) =>
      this(canal: canal);

  @override
  CanalParametresDto actif(bool? actif) => this(actif: actif);

  @override
  CanalParametresDto configure(bool? configure) => this(configure: configure);

  @override
  CanalParametresDto champs(List<ChampDto>? champs) => this(champs: champs);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `CanalParametresDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// CanalParametresDto(...).copyWith(id: 12, name: "My name")
  /// ````
  CanalParametresDto call({
    Object? canal = const $CopyWithPlaceholder(),
    Object? actif = const $CopyWithPlaceholder(),
    Object? configure = const $CopyWithPlaceholder(),
    Object? champs = const $CopyWithPlaceholder(),
  }) {
    return CanalParametresDto(
      canal: canal == const $CopyWithPlaceholder()
          ? _value.canal
          // ignore: cast_nullable_to_non_nullable
          : canal as CanalParametresDtoCanalEnum?,
      actif: actif == const $CopyWithPlaceholder()
          ? _value.actif
          // ignore: cast_nullable_to_non_nullable
          : actif as bool?,
      configure: configure == const $CopyWithPlaceholder()
          ? _value.configure
          // ignore: cast_nullable_to_non_nullable
          : configure as bool?,
      champs: champs == const $CopyWithPlaceholder()
          ? _value.champs
          // ignore: cast_nullable_to_non_nullable
          : champs as List<ChampDto>?,
    );
  }
}

extension $CanalParametresDtoCopyWith on CanalParametresDto {
  /// Returns a callable class that can be used as follows: `instanceOfCanalParametresDto.copyWith(...)` or like so:`instanceOfCanalParametresDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$CanalParametresDtoCWProxy get copyWith =>
      _$CanalParametresDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

CanalParametresDto _$CanalParametresDtoFromJson(Map<String, dynamic> json) =>
    $checkedCreate('CanalParametresDto', json, ($checkedConvert) {
      final val = CanalParametresDto(
        canal: $checkedConvert(
          'canal',
          (v) => $enumDecodeNullable(_$CanalParametresDtoCanalEnumEnumMap, v),
        ),
        actif: $checkedConvert('actif', (v) => v as bool?),
        configure: $checkedConvert('configure', (v) => v as bool?),
        champs: $checkedConvert(
          'champs',
          (v) => (v as List<dynamic>?)
              ?.map((e) => ChampDto.fromJson(e as Map<String, dynamic>))
              .toList(),
        ),
      );
      return val;
    });

Map<String, dynamic> _$CanalParametresDtoToJson(CanalParametresDto instance) =>
    <String, dynamic>{
      'canal': ?_$CanalParametresDtoCanalEnumEnumMap[instance.canal],
      'actif': ?instance.actif,
      'configure': ?instance.configure,
      'champs': ?instance.champs?.map((e) => e.toJson()).toList(),
    };

const _$CanalParametresDtoCanalEnumEnumMap = {
  CanalParametresDtoCanalEnum.SMS: 'SMS',
  CanalParametresDtoCanalEnum.WHATSAPP: 'WHATSAPP',
  CanalParametresDtoCanalEnum.EMAIL: 'EMAIL',
  CanalParametresDtoCanalEnum.PUSH: 'PUSH',
};
