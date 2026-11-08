// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'deux_facteurs_defi.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$DeuxFacteursDefiCWProxy {
  DeuxFacteursDefi defi(String defi);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `DeuxFacteursDefi(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// DeuxFacteursDefi(...).copyWith(id: 12, name: "My name")
  /// ````
  DeuxFacteursDefi call({String defi});
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfDeuxFacteursDefi.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfDeuxFacteursDefi.copyWith.fieldName(...)`
class _$DeuxFacteursDefiCWProxyImpl implements _$DeuxFacteursDefiCWProxy {
  const _$DeuxFacteursDefiCWProxyImpl(this._value);

  final DeuxFacteursDefi _value;

  @override
  DeuxFacteursDefi defi(String defi) => this(defi: defi);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `DeuxFacteursDefi(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// DeuxFacteursDefi(...).copyWith(id: 12, name: "My name")
  /// ````
  DeuxFacteursDefi call({Object? defi = const $CopyWithPlaceholder()}) {
    return DeuxFacteursDefi(
      defi: defi == const $CopyWithPlaceholder()
          ? _value.defi
          // ignore: cast_nullable_to_non_nullable
          : defi as String,
    );
  }
}

extension $DeuxFacteursDefiCopyWith on DeuxFacteursDefi {
  /// Returns a callable class that can be used as follows: `instanceOfDeuxFacteursDefi.copyWith(...)` or like so:`instanceOfDeuxFacteursDefi.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$DeuxFacteursDefiCWProxy get copyWith => _$DeuxFacteursDefiCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

DeuxFacteursDefi _$DeuxFacteursDefiFromJson(Map<String, dynamic> json) =>
    $checkedCreate('DeuxFacteursDefi', json, ($checkedConvert) {
      $checkKeys(json, requiredKeys: const ['defi']);
      final val = DeuxFacteursDefi(
        defi: $checkedConvert('defi', (v) => v as String),
      );
      return val;
    });

Map<String, dynamic> _$DeuxFacteursDefiToJson(DeuxFacteursDefi instance) =>
    <String, dynamic>{'defi': instance.defi};
