// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'page_reponse_document_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$PageReponseDocumentDtoCWProxy {
  PageReponseDocumentDto content(List<DocumentDto>? content);

  PageReponseDocumentDto page(int? page);

  PageReponseDocumentDto size(int? size);

  PageReponseDocumentDto totalElements(int? totalElements);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PageReponseDocumentDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PageReponseDocumentDto(...).copyWith(id: 12, name: "My name")
  /// ````
  PageReponseDocumentDto call({
    List<DocumentDto>? content,
    int? page,
    int? size,
    int? totalElements,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfPageReponseDocumentDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfPageReponseDocumentDto.copyWith.fieldName(...)`
class _$PageReponseDocumentDtoCWProxyImpl
    implements _$PageReponseDocumentDtoCWProxy {
  const _$PageReponseDocumentDtoCWProxyImpl(this._value);

  final PageReponseDocumentDto _value;

  @override
  PageReponseDocumentDto content(List<DocumentDto>? content) =>
      this(content: content);

  @override
  PageReponseDocumentDto page(int? page) => this(page: page);

  @override
  PageReponseDocumentDto size(int? size) => this(size: size);

  @override
  PageReponseDocumentDto totalElements(int? totalElements) =>
      this(totalElements: totalElements);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PageReponseDocumentDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PageReponseDocumentDto(...).copyWith(id: 12, name: "My name")
  /// ````
  PageReponseDocumentDto call({
    Object? content = const $CopyWithPlaceholder(),
    Object? page = const $CopyWithPlaceholder(),
    Object? size = const $CopyWithPlaceholder(),
    Object? totalElements = const $CopyWithPlaceholder(),
  }) {
    return PageReponseDocumentDto(
      content: content == const $CopyWithPlaceholder()
          ? _value.content
          // ignore: cast_nullable_to_non_nullable
          : content as List<DocumentDto>?,
      page: page == const $CopyWithPlaceholder()
          ? _value.page
          // ignore: cast_nullable_to_non_nullable
          : page as int?,
      size: size == const $CopyWithPlaceholder()
          ? _value.size
          // ignore: cast_nullable_to_non_nullable
          : size as int?,
      totalElements: totalElements == const $CopyWithPlaceholder()
          ? _value.totalElements
          // ignore: cast_nullable_to_non_nullable
          : totalElements as int?,
    );
  }
}

extension $PageReponseDocumentDtoCopyWith on PageReponseDocumentDto {
  /// Returns a callable class that can be used as follows: `instanceOfPageReponseDocumentDto.copyWith(...)` or like so:`instanceOfPageReponseDocumentDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$PageReponseDocumentDtoCWProxy get copyWith =>
      _$PageReponseDocumentDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

PageReponseDocumentDto _$PageReponseDocumentDtoFromJson(
  Map<String, dynamic> json,
) => $checkedCreate('PageReponseDocumentDto', json, ($checkedConvert) {
  final val = PageReponseDocumentDto(
    content: $checkedConvert(
      'content',
      (v) => (v as List<dynamic>?)
          ?.map((e) => DocumentDto.fromJson(e as Map<String, dynamic>))
          .toList(),
    ),
    page: $checkedConvert('page', (v) => (v as num?)?.toInt()),
    size: $checkedConvert('size', (v) => (v as num?)?.toInt()),
    totalElements: $checkedConvert(
      'totalElements',
      (v) => (v as num?)?.toInt(),
    ),
  );
  return val;
});

Map<String, dynamic> _$PageReponseDocumentDtoToJson(
  PageReponseDocumentDto instance,
) => <String, dynamic>{
  'content': ?instance.content?.map((e) => e.toJson()).toList(),
  'page': ?instance.page,
  'size': ?instance.size,
  'totalElements': ?instance.totalElements,
};
