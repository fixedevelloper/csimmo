// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'import_requete.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$ImportRequeteCWProxy {
  ImportRequete contenu(String contenu);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ImportRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ImportRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  ImportRequete call({String contenu});
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfImportRequete.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfImportRequete.copyWith.fieldName(...)`
class _$ImportRequeteCWProxyImpl implements _$ImportRequeteCWProxy {
  const _$ImportRequeteCWProxyImpl(this._value);

  final ImportRequete _value;

  @override
  ImportRequete contenu(String contenu) => this(contenu: contenu);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ImportRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ImportRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  ImportRequete call({Object? contenu = const $CopyWithPlaceholder()}) {
    return ImportRequete(
      contenu: contenu == const $CopyWithPlaceholder()
          ? _value.contenu
          // ignore: cast_nullable_to_non_nullable
          : contenu as String,
    );
  }
}

extension $ImportRequeteCopyWith on ImportRequete {
  /// Returns a callable class that can be used as follows: `instanceOfImportRequete.copyWith(...)` or like so:`instanceOfImportRequete.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$ImportRequeteCWProxy get copyWith => _$ImportRequeteCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

ImportRequete _$ImportRequeteFromJson(Map<String, dynamic> json) =>
    $checkedCreate('ImportRequete', json, ($checkedConvert) {
      $checkKeys(json, requiredKeys: const ['contenu']);
      final val = ImportRequete(
        contenu: $checkedConvert('contenu', (v) => v as String),
      );
      return val;
    });

Map<String, dynamic> _$ImportRequeteToJson(ImportRequete instance) =>
    <String, dynamic>{'contenu': instance.contenu};
