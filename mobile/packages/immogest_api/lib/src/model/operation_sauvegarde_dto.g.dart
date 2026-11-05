// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'operation_sauvegarde_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$OperationSauvegardeDtoCWProxy {
  OperationSauvegardeDto operation(String? operation);

  OperationSauvegardeDto statut(String? statut);

  OperationSauvegardeDto id(String? id);

  OperationSauvegardeDto debut(DateTime? debut);

  OperationSauvegardeDto fin(DateTime? fin);

  OperationSauvegardeDto message(String? message);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `OperationSauvegardeDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// OperationSauvegardeDto(...).copyWith(id: 12, name: "My name")
  /// ````
  OperationSauvegardeDto call({
    String? operation,
    String? statut,
    String? id,
    DateTime? debut,
    DateTime? fin,
    String? message,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfOperationSauvegardeDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfOperationSauvegardeDto.copyWith.fieldName(...)`
class _$OperationSauvegardeDtoCWProxyImpl
    implements _$OperationSauvegardeDtoCWProxy {
  const _$OperationSauvegardeDtoCWProxyImpl(this._value);

  final OperationSauvegardeDto _value;

  @override
  OperationSauvegardeDto operation(String? operation) =>
      this(operation: operation);

  @override
  OperationSauvegardeDto statut(String? statut) => this(statut: statut);

  @override
  OperationSauvegardeDto id(String? id) => this(id: id);

  @override
  OperationSauvegardeDto debut(DateTime? debut) => this(debut: debut);

  @override
  OperationSauvegardeDto fin(DateTime? fin) => this(fin: fin);

  @override
  OperationSauvegardeDto message(String? message) => this(message: message);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `OperationSauvegardeDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// OperationSauvegardeDto(...).copyWith(id: 12, name: "My name")
  /// ````
  OperationSauvegardeDto call({
    Object? operation = const $CopyWithPlaceholder(),
    Object? statut = const $CopyWithPlaceholder(),
    Object? id = const $CopyWithPlaceholder(),
    Object? debut = const $CopyWithPlaceholder(),
    Object? fin = const $CopyWithPlaceholder(),
    Object? message = const $CopyWithPlaceholder(),
  }) {
    return OperationSauvegardeDto(
      operation: operation == const $CopyWithPlaceholder()
          ? _value.operation
          // ignore: cast_nullable_to_non_nullable
          : operation as String?,
      statut: statut == const $CopyWithPlaceholder()
          ? _value.statut
          // ignore: cast_nullable_to_non_nullable
          : statut as String?,
      id: id == const $CopyWithPlaceholder()
          ? _value.id
          // ignore: cast_nullable_to_non_nullable
          : id as String?,
      debut: debut == const $CopyWithPlaceholder()
          ? _value.debut
          // ignore: cast_nullable_to_non_nullable
          : debut as DateTime?,
      fin: fin == const $CopyWithPlaceholder()
          ? _value.fin
          // ignore: cast_nullable_to_non_nullable
          : fin as DateTime?,
      message: message == const $CopyWithPlaceholder()
          ? _value.message
          // ignore: cast_nullable_to_non_nullable
          : message as String?,
    );
  }
}

extension $OperationSauvegardeDtoCopyWith on OperationSauvegardeDto {
  /// Returns a callable class that can be used as follows: `instanceOfOperationSauvegardeDto.copyWith(...)` or like so:`instanceOfOperationSauvegardeDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$OperationSauvegardeDtoCWProxy get copyWith =>
      _$OperationSauvegardeDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

OperationSauvegardeDto _$OperationSauvegardeDtoFromJson(
  Map<String, dynamic> json,
) => $checkedCreate('OperationSauvegardeDto', json, ($checkedConvert) {
  final val = OperationSauvegardeDto(
    operation: $checkedConvert('operation', (v) => v as String?),
    statut: $checkedConvert('statut', (v) => v as String?),
    id: $checkedConvert('id', (v) => v as String?),
    debut: $checkedConvert(
      'debut',
      (v) => v == null ? null : DateTime.parse(v as String),
    ),
    fin: $checkedConvert(
      'fin',
      (v) => v == null ? null : DateTime.parse(v as String),
    ),
    message: $checkedConvert('message', (v) => v as String?),
  );
  return val;
});

Map<String, dynamic> _$OperationSauvegardeDtoToJson(
  OperationSauvegardeDto instance,
) => <String, dynamic>{
  'operation': ?instance.operation,
  'statut': ?instance.statut,
  'id': ?instance.id,
  'debut': ?instance.debut?.toIso8601String(),
  'fin': ?instance.fin?.toIso8601String(),
  'message': ?instance.message,
};
