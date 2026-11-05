// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'test_requete.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$TestRequeteCWProxy {
  TestRequete destinataire(String destinataire);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `TestRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// TestRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  TestRequete call({String destinataire});
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfTestRequete.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfTestRequete.copyWith.fieldName(...)`
class _$TestRequeteCWProxyImpl implements _$TestRequeteCWProxy {
  const _$TestRequeteCWProxyImpl(this._value);

  final TestRequete _value;

  @override
  TestRequete destinataire(String destinataire) =>
      this(destinataire: destinataire);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `TestRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// TestRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  TestRequete call({Object? destinataire = const $CopyWithPlaceholder()}) {
    return TestRequete(
      destinataire: destinataire == const $CopyWithPlaceholder()
          ? _value.destinataire
          // ignore: cast_nullable_to_non_nullable
          : destinataire as String,
    );
  }
}

extension $TestRequeteCopyWith on TestRequete {
  /// Returns a callable class that can be used as follows: `instanceOfTestRequete.copyWith(...)` or like so:`instanceOfTestRequete.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$TestRequeteCWProxy get copyWith => _$TestRequeteCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

TestRequete _$TestRequeteFromJson(Map<String, dynamic> json) =>
    $checkedCreate('TestRequete', json, ($checkedConvert) {
      $checkKeys(json, requiredKeys: const ['destinataire']);
      final val = TestRequete(
        destinataire: $checkedConvert('destinataire', (v) => v as String),
      );
      return val;
    });

Map<String, dynamic> _$TestRequeteToJson(TestRequete instance) =>
    <String, dynamic>{'destinataire': instance.destinataire};
