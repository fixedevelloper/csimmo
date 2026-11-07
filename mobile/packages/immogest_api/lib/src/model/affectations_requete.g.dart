// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'affectations_requete.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$AffectationsRequeteCWProxy {
  AffectationsRequete immeubleIds(List<String> immeubleIds);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `AffectationsRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// AffectationsRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  AffectationsRequete call({List<String> immeubleIds});
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfAffectationsRequete.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfAffectationsRequete.copyWith.fieldName(...)`
class _$AffectationsRequeteCWProxyImpl implements _$AffectationsRequeteCWProxy {
  const _$AffectationsRequeteCWProxyImpl(this._value);

  final AffectationsRequete _value;

  @override
  AffectationsRequete immeubleIds(List<String> immeubleIds) =>
      this(immeubleIds: immeubleIds);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `AffectationsRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// AffectationsRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  AffectationsRequete call({
    Object? immeubleIds = const $CopyWithPlaceholder(),
  }) {
    return AffectationsRequete(
      immeubleIds: immeubleIds == const $CopyWithPlaceholder()
          ? _value.immeubleIds
          // ignore: cast_nullable_to_non_nullable
          : immeubleIds as List<String>,
    );
  }
}

extension $AffectationsRequeteCopyWith on AffectationsRequete {
  /// Returns a callable class that can be used as follows: `instanceOfAffectationsRequete.copyWith(...)` or like so:`instanceOfAffectationsRequete.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$AffectationsRequeteCWProxy get copyWith =>
      _$AffectationsRequeteCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

AffectationsRequete _$AffectationsRequeteFromJson(Map<String, dynamic> json) =>
    $checkedCreate('AffectationsRequete', json, ($checkedConvert) {
      $checkKeys(json, requiredKeys: const ['immeubleIds']);
      final val = AffectationsRequete(
        immeubleIds: $checkedConvert(
          'immeubleIds',
          (v) => (v as List<dynamic>).map((e) => e as String).toList(),
        ),
      );
      return val;
    });

Map<String, dynamic> _$AffectationsRequeteToJson(
  AffectationsRequete instance,
) => <String, dynamic>{'immeubleIds': instance.immeubleIds};
