// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'page_reponse_message_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$PageReponseMessageDtoCWProxy {
  PageReponseMessageDto content(List<MessageDto>? content);

  PageReponseMessageDto page(int? page);

  PageReponseMessageDto size(int? size);

  PageReponseMessageDto totalElements(int? totalElements);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PageReponseMessageDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PageReponseMessageDto(...).copyWith(id: 12, name: "My name")
  /// ````
  PageReponseMessageDto call({
    List<MessageDto>? content,
    int? page,
    int? size,
    int? totalElements,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfPageReponseMessageDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfPageReponseMessageDto.copyWith.fieldName(...)`
class _$PageReponseMessageDtoCWProxyImpl
    implements _$PageReponseMessageDtoCWProxy {
  const _$PageReponseMessageDtoCWProxyImpl(this._value);

  final PageReponseMessageDto _value;

  @override
  PageReponseMessageDto content(List<MessageDto>? content) =>
      this(content: content);

  @override
  PageReponseMessageDto page(int? page) => this(page: page);

  @override
  PageReponseMessageDto size(int? size) => this(size: size);

  @override
  PageReponseMessageDto totalElements(int? totalElements) =>
      this(totalElements: totalElements);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PageReponseMessageDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PageReponseMessageDto(...).copyWith(id: 12, name: "My name")
  /// ````
  PageReponseMessageDto call({
    Object? content = const $CopyWithPlaceholder(),
    Object? page = const $CopyWithPlaceholder(),
    Object? size = const $CopyWithPlaceholder(),
    Object? totalElements = const $CopyWithPlaceholder(),
  }) {
    return PageReponseMessageDto(
      content: content == const $CopyWithPlaceholder()
          ? _value.content
          // ignore: cast_nullable_to_non_nullable
          : content as List<MessageDto>?,
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

extension $PageReponseMessageDtoCopyWith on PageReponseMessageDto {
  /// Returns a callable class that can be used as follows: `instanceOfPageReponseMessageDto.copyWith(...)` or like so:`instanceOfPageReponseMessageDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$PageReponseMessageDtoCWProxy get copyWith =>
      _$PageReponseMessageDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

PageReponseMessageDto _$PageReponseMessageDtoFromJson(
  Map<String, dynamic> json,
) => $checkedCreate('PageReponseMessageDto', json, ($checkedConvert) {
  final val = PageReponseMessageDto(
    content: $checkedConvert(
      'content',
      (v) => (v as List<dynamic>?)
          ?.map((e) => MessageDto.fromJson(e as Map<String, dynamic>))
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

Map<String, dynamic> _$PageReponseMessageDtoToJson(
  PageReponseMessageDto instance,
) => <String, dynamic>{
  'content': ?instance.content?.map((e) => e.toJson()).toList(),
  'page': ?instance.page,
  'size': ?instance.size,
  'totalElements': ?instance.totalElements,
};
