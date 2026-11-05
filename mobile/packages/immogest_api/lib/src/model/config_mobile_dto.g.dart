// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'config_mobile_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$ConfigMobileDtoCWProxy {
  ConfigMobileDto push(PushMobile? push);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ConfigMobileDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ConfigMobileDto(...).copyWith(id: 12, name: "My name")
  /// ````
  ConfigMobileDto call({PushMobile? push});
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfConfigMobileDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfConfigMobileDto.copyWith.fieldName(...)`
class _$ConfigMobileDtoCWProxyImpl implements _$ConfigMobileDtoCWProxy {
  const _$ConfigMobileDtoCWProxyImpl(this._value);

  final ConfigMobileDto _value;

  @override
  ConfigMobileDto push(PushMobile? push) => this(push: push);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ConfigMobileDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ConfigMobileDto(...).copyWith(id: 12, name: "My name")
  /// ````
  ConfigMobileDto call({Object? push = const $CopyWithPlaceholder()}) {
    return ConfigMobileDto(
      push: push == const $CopyWithPlaceholder()
          ? _value.push
          // ignore: cast_nullable_to_non_nullable
          : push as PushMobile?,
    );
  }
}

extension $ConfigMobileDtoCopyWith on ConfigMobileDto {
  /// Returns a callable class that can be used as follows: `instanceOfConfigMobileDto.copyWith(...)` or like so:`instanceOfConfigMobileDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$ConfigMobileDtoCWProxy get copyWith => _$ConfigMobileDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

ConfigMobileDto _$ConfigMobileDtoFromJson(Map<String, dynamic> json) =>
    $checkedCreate('ConfigMobileDto', json, ($checkedConvert) {
      final val = ConfigMobileDto(
        push: $checkedConvert(
          'push',
          (v) =>
              v == null ? null : PushMobile.fromJson(v as Map<String, dynamic>),
        ),
      );
      return val;
    });

Map<String, dynamic> _$ConfigMobileDtoToJson(ConfigMobileDto instance) =>
    <String, dynamic>{'push': ?instance.push?.toJson()};
