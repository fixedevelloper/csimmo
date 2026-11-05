// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'document_bail_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$DocumentBailDtoCWProxy {
  DocumentBailDto id(String? id);

  DocumentBailDto bailId(String? bailId);

  DocumentBailDto fichierId(String? fichierId);

  DocumentBailDto version(int? version);

  DocumentBailDto genereLe(DateTime? genereLe);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `DocumentBailDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// DocumentBailDto(...).copyWith(id: 12, name: "My name")
  /// ````
  DocumentBailDto call({
    String? id,
    String? bailId,
    String? fichierId,
    int? version,
    DateTime? genereLe,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfDocumentBailDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfDocumentBailDto.copyWith.fieldName(...)`
class _$DocumentBailDtoCWProxyImpl implements _$DocumentBailDtoCWProxy {
  const _$DocumentBailDtoCWProxyImpl(this._value);

  final DocumentBailDto _value;

  @override
  DocumentBailDto id(String? id) => this(id: id);

  @override
  DocumentBailDto bailId(String? bailId) => this(bailId: bailId);

  @override
  DocumentBailDto fichierId(String? fichierId) => this(fichierId: fichierId);

  @override
  DocumentBailDto version(int? version) => this(version: version);

  @override
  DocumentBailDto genereLe(DateTime? genereLe) => this(genereLe: genereLe);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `DocumentBailDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// DocumentBailDto(...).copyWith(id: 12, name: "My name")
  /// ````
  DocumentBailDto call({
    Object? id = const $CopyWithPlaceholder(),
    Object? bailId = const $CopyWithPlaceholder(),
    Object? fichierId = const $CopyWithPlaceholder(),
    Object? version = const $CopyWithPlaceholder(),
    Object? genereLe = const $CopyWithPlaceholder(),
  }) {
    return DocumentBailDto(
      id: id == const $CopyWithPlaceholder()
          ? _value.id
          // ignore: cast_nullable_to_non_nullable
          : id as String?,
      bailId: bailId == const $CopyWithPlaceholder()
          ? _value.bailId
          // ignore: cast_nullable_to_non_nullable
          : bailId as String?,
      fichierId: fichierId == const $CopyWithPlaceholder()
          ? _value.fichierId
          // ignore: cast_nullable_to_non_nullable
          : fichierId as String?,
      version: version == const $CopyWithPlaceholder()
          ? _value.version
          // ignore: cast_nullable_to_non_nullable
          : version as int?,
      genereLe: genereLe == const $CopyWithPlaceholder()
          ? _value.genereLe
          // ignore: cast_nullable_to_non_nullable
          : genereLe as DateTime?,
    );
  }
}

extension $DocumentBailDtoCopyWith on DocumentBailDto {
  /// Returns a callable class that can be used as follows: `instanceOfDocumentBailDto.copyWith(...)` or like so:`instanceOfDocumentBailDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$DocumentBailDtoCWProxy get copyWith => _$DocumentBailDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

DocumentBailDto _$DocumentBailDtoFromJson(Map<String, dynamic> json) =>
    $checkedCreate('DocumentBailDto', json, ($checkedConvert) {
      final val = DocumentBailDto(
        id: $checkedConvert('id', (v) => v as String?),
        bailId: $checkedConvert('bailId', (v) => v as String?),
        fichierId: $checkedConvert('fichierId', (v) => v as String?),
        version: $checkedConvert('version', (v) => (v as num?)?.toInt()),
        genereLe: $checkedConvert(
          'genereLe',
          (v) => v == null ? null : DateTime.parse(v as String),
        ),
      );
      return val;
    });

Map<String, dynamic> _$DocumentBailDtoToJson(DocumentBailDto instance) =>
    <String, dynamic>{
      'id': ?instance.id,
      'bailId': ?instance.bailId,
      'fichierId': ?instance.fichierId,
      'version': ?instance.version,
      'genereLe': ?instance.genereLe?.toIso8601String(),
    };
