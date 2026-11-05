// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'erreur_import_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$ErreurImportDtoCWProxy {
  ErreurImportDto feuille(String? feuille);

  ErreurImportDto ligne(int? ligne);

  ErreurImportDto colonne(String? colonne);

  ErreurImportDto code(String? code);

  ErreurImportDto message(String? message);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ErreurImportDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ErreurImportDto(...).copyWith(id: 12, name: "My name")
  /// ````
  ErreurImportDto call({
    String? feuille,
    int? ligne,
    String? colonne,
    String? code,
    String? message,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfErreurImportDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfErreurImportDto.copyWith.fieldName(...)`
class _$ErreurImportDtoCWProxyImpl implements _$ErreurImportDtoCWProxy {
  const _$ErreurImportDtoCWProxyImpl(this._value);

  final ErreurImportDto _value;

  @override
  ErreurImportDto feuille(String? feuille) => this(feuille: feuille);

  @override
  ErreurImportDto ligne(int? ligne) => this(ligne: ligne);

  @override
  ErreurImportDto colonne(String? colonne) => this(colonne: colonne);

  @override
  ErreurImportDto code(String? code) => this(code: code);

  @override
  ErreurImportDto message(String? message) => this(message: message);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `ErreurImportDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// ErreurImportDto(...).copyWith(id: 12, name: "My name")
  /// ````
  ErreurImportDto call({
    Object? feuille = const $CopyWithPlaceholder(),
    Object? ligne = const $CopyWithPlaceholder(),
    Object? colonne = const $CopyWithPlaceholder(),
    Object? code = const $CopyWithPlaceholder(),
    Object? message = const $CopyWithPlaceholder(),
  }) {
    return ErreurImportDto(
      feuille: feuille == const $CopyWithPlaceholder()
          ? _value.feuille
          // ignore: cast_nullable_to_non_nullable
          : feuille as String?,
      ligne: ligne == const $CopyWithPlaceholder()
          ? _value.ligne
          // ignore: cast_nullable_to_non_nullable
          : ligne as int?,
      colonne: colonne == const $CopyWithPlaceholder()
          ? _value.colonne
          // ignore: cast_nullable_to_non_nullable
          : colonne as String?,
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

extension $ErreurImportDtoCopyWith on ErreurImportDto {
  /// Returns a callable class that can be used as follows: `instanceOfErreurImportDto.copyWith(...)` or like so:`instanceOfErreurImportDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$ErreurImportDtoCWProxy get copyWith => _$ErreurImportDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

ErreurImportDto _$ErreurImportDtoFromJson(Map<String, dynamic> json) =>
    $checkedCreate('ErreurImportDto', json, ($checkedConvert) {
      final val = ErreurImportDto(
        feuille: $checkedConvert('feuille', (v) => v as String?),
        ligne: $checkedConvert('ligne', (v) => (v as num?)?.toInt()),
        colonne: $checkedConvert('colonne', (v) => v as String?),
        code: $checkedConvert('code', (v) => v as String?),
        message: $checkedConvert('message', (v) => v as String?),
      );
      return val;
    });

Map<String, dynamic> _$ErreurImportDtoToJson(ErreurImportDto instance) =>
    <String, dynamic>{
      'feuille': ?instance.feuille,
      'ligne': ?instance.ligne,
      'colonne': ?instance.colonne,
      'code': ?instance.code,
      'message': ?instance.message,
    };
