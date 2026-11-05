// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'push_mobile.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$PushMobileCWProxy {
  PushMobile projectId(String? projectId);

  PushMobile apiKey(String? apiKey);

  PushMobile appId(String? appId);

  PushMobile messagingSenderId(String? messagingSenderId);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PushMobile(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PushMobile(...).copyWith(id: 12, name: "My name")
  /// ````
  PushMobile call({
    String? projectId,
    String? apiKey,
    String? appId,
    String? messagingSenderId,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfPushMobile.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfPushMobile.copyWith.fieldName(...)`
class _$PushMobileCWProxyImpl implements _$PushMobileCWProxy {
  const _$PushMobileCWProxyImpl(this._value);

  final PushMobile _value;

  @override
  PushMobile projectId(String? projectId) => this(projectId: projectId);

  @override
  PushMobile apiKey(String? apiKey) => this(apiKey: apiKey);

  @override
  PushMobile appId(String? appId) => this(appId: appId);

  @override
  PushMobile messagingSenderId(String? messagingSenderId) =>
      this(messagingSenderId: messagingSenderId);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PushMobile(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PushMobile(...).copyWith(id: 12, name: "My name")
  /// ````
  PushMobile call({
    Object? projectId = const $CopyWithPlaceholder(),
    Object? apiKey = const $CopyWithPlaceholder(),
    Object? appId = const $CopyWithPlaceholder(),
    Object? messagingSenderId = const $CopyWithPlaceholder(),
  }) {
    return PushMobile(
      projectId: projectId == const $CopyWithPlaceholder()
          ? _value.projectId
          // ignore: cast_nullable_to_non_nullable
          : projectId as String?,
      apiKey: apiKey == const $CopyWithPlaceholder()
          ? _value.apiKey
          // ignore: cast_nullable_to_non_nullable
          : apiKey as String?,
      appId: appId == const $CopyWithPlaceholder()
          ? _value.appId
          // ignore: cast_nullable_to_non_nullable
          : appId as String?,
      messagingSenderId: messagingSenderId == const $CopyWithPlaceholder()
          ? _value.messagingSenderId
          // ignore: cast_nullable_to_non_nullable
          : messagingSenderId as String?,
    );
  }
}

extension $PushMobileCopyWith on PushMobile {
  /// Returns a callable class that can be used as follows: `instanceOfPushMobile.copyWith(...)` or like so:`instanceOfPushMobile.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$PushMobileCWProxy get copyWith => _$PushMobileCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

PushMobile _$PushMobileFromJson(Map<String, dynamic> json) =>
    $checkedCreate('PushMobile', json, ($checkedConvert) {
      final val = PushMobile(
        projectId: $checkedConvert('projectId', (v) => v as String?),
        apiKey: $checkedConvert('apiKey', (v) => v as String?),
        appId: $checkedConvert('appId', (v) => v as String?),
        messagingSenderId: $checkedConvert(
          'messagingSenderId',
          (v) => v as String?,
        ),
      );
      return val;
    });

Map<String, dynamic> _$PushMobileToJson(PushMobile instance) =>
    <String, dynamic>{
      'projectId': ?instance.projectId,
      'apiKey': ?instance.apiKey,
      'appId': ?instance.appId,
      'messagingSenderId': ?instance.messagingSenderId,
    };
