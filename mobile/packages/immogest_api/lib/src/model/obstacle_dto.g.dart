// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'obstacle_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$ObstacleDtoCWProxy {
  ObstacleDto code(String? code);

  ObstacleDto message(String? message);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ObstacleDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ObstacleDto(...).copyWith(id: 12, name: "My name")
  /// ````
  ObstacleDto call({String? code, String? message});
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfObstacleDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfObstacleDto.copyWith.fieldName(...)`
class _$ObstacleDtoCWProxyImpl implements _$ObstacleDtoCWProxy {
  const _$ObstacleDtoCWProxyImpl(this._value);

  final ObstacleDto _value;

  @override
  ObstacleDto code(String? code) => this(code: code);

  @override
  ObstacleDto message(String? message) => this(message: message);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ObstacleDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ObstacleDto(...).copyWith(id: 12, name: "My name")
  /// ````
  ObstacleDto call({
    Object? code = const $CopyWithPlaceholder(),
    Object? message = const $CopyWithPlaceholder(),
  }) {
    return ObstacleDto(
      code: code == const $CopyWithPlaceholder()
          ? _value.code
          // ignore: cast_nullable_to_non_nullable
          : code as String?,
      message: message == const $CopyWithPlaceholder()
          ? _value.message
          // ignore: cast_nullable_to_non_nullable
          : message as String?,
    );
  }
}

extension $ObstacleDtoCopyWith on ObstacleDto {
  /// Returns a callable class that can be used as follows: `instanceOfObstacleDto.copyWith(...)` or like so:`instanceOfObstacleDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$ObstacleDtoCWProxy get copyWith => _$ObstacleDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

ObstacleDto _$ObstacleDtoFromJson(Map<String, dynamic> json) =>
    $checkedCreate('ObstacleDto', json, ($checkedConvert) {
      final val = ObstacleDto(
        code: $checkedConvert('code', (v) => v as String?),
        message: $checkedConvert('message', (v) => v as String?),
      );
      return val;
    });

Map<String, dynamic> _$ObstacleDtoToJson(ObstacleDto instance) =>
    <String, dynamic>{'code': ?instance.code, 'message': ?instance.message};
