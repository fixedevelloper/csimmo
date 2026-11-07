// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'affectations_reponse.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$AffectationsReponseCWProxy {
  AffectationsReponse immeubleIds(List<String>? immeubleIds);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `AffectationsReponse(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// AffectationsReponse(...).copyWith(id: 12, name: "My name")
  /// ````
  AffectationsReponse call({List<String>? immeubleIds});
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfAffectationsReponse.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfAffectationsReponse.copyWith.fieldName(...)`
class _$AffectationsReponseCWProxyImpl implements _$AffectationsReponseCWProxy {
  const _$AffectationsReponseCWProxyImpl(this._value);

  final AffectationsReponse _value;

  @override
  AffectationsReponse immeubleIds(List<String>? immeubleIds) =>
      this(immeubleIds: immeubleIds);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `AffectationsReponse(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// AffectationsReponse(...).copyWith(id: 12, name: "My name")
  /// ````
  AffectationsReponse call({
    Object? immeubleIds = const $CopyWithPlaceholder(),
  }) {
    return AffectationsReponse(
      immeubleIds: immeubleIds == const $CopyWithPlaceholder()
          ? _value.immeubleIds
          // ignore: cast_nullable_to_non_nullable
          : immeubleIds as List<String>?,
    );
  }
}

extension $AffectationsReponseCopyWith on AffectationsReponse {
  /// Returns a callable class that can be used as follows: `instanceOfAffectationsReponse.copyWith(...)` or like so:`instanceOfAffectationsReponse.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$AffectationsReponseCWProxy get copyWith =>
      _$AffectationsReponseCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

AffectationsReponse _$AffectationsReponseFromJson(Map<String, dynamic> json) =>
    $checkedCreate('AffectationsReponse', json, ($checkedConvert) {
      final val = AffectationsReponse(
        immeubleIds: $checkedConvert(
          'immeubleIds',
          (v) => (v as List<dynamic>?)?.map((e) => e as String).toList(),
        ),
      );
      return val;
    });

Map<String, dynamic> _$AffectationsReponseToJson(
  AffectationsReponse instance,
) => <String, dynamic>{'immeubleIds': ?instance.immeubleIds};
