// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'code_requete.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$CodeRequeteCWProxy {
  CodeRequete code(String code);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `CodeRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// CodeRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  CodeRequete call({String code});
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfCodeRequete.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfCodeRequete.copyWith.fieldName(...)`
class _$CodeRequeteCWProxyImpl implements _$CodeRequeteCWProxy {
  const _$CodeRequeteCWProxyImpl(this._value);

  final CodeRequete _value;

  @override
  CodeRequete code(String code) => this(code: code);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `CodeRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// CodeRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  CodeRequete call({Object? code = const $CopyWithPlaceholder()}) {
    return CodeRequete(
      code: code == const $CopyWithPlaceholder()
          ? _value.code
          // ignore: cast_nullable_to_non_nullable
          : code as String,
    );
  }
}

extension $CodeRequeteCopyWith on CodeRequete {
  /// Returns a callable class that can be used as follows: `instanceOfCodeRequete.copyWith(...)` or like so:`instanceOfCodeRequete.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$CodeRequeteCWProxy get copyWith => _$CodeRequeteCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

CodeRequete _$CodeRequeteFromJson(Map<String, dynamic> json) => $checkedCreate(
  'CodeRequete',
  json,
  ($checkedConvert) {
    $checkKeys(json, requiredKeys: const ['code']);
    final val = CodeRequete(code: $checkedConvert('code', (v) => v as String));
    return val;
  },
);

Map<String, dynamic> _$CodeRequeteToJson(CodeRequete instance) =>
    <String, dynamic>{'code': instance.code};
