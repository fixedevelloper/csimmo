// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'page_reponse_immeuble_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$PageReponseImmeubleDtoCWProxy {
  PageReponseImmeubleDto content(List<ImmeubleDto>? content);

  PageReponseImmeubleDto page(int? page);

  PageReponseImmeubleDto size(int? size);

  PageReponseImmeubleDto totalElements(int? totalElements);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PageReponseImmeubleDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PageReponseImmeubleDto(...).copyWith(id: 12, name: "My name")
  /// ````
  PageReponseImmeubleDto call({
    List<ImmeubleDto>? content,
    int? page,
    int? size,
    int? totalElements,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfPageReponseImmeubleDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfPageReponseImmeubleDto.copyWith.fieldName(...)`
class _$PageReponseImmeubleDtoCWProxyImpl
    implements _$PageReponseImmeubleDtoCWProxy {
  const _$PageReponseImmeubleDtoCWProxyImpl(this._value);

  final PageReponseImmeubleDto _value;

  @override
  PageReponseImmeubleDto content(List<ImmeubleDto>? content) =>
      this(content: content);

  @override
  PageReponseImmeubleDto page(int? page) => this(page: page);

  @override
  PageReponseImmeubleDto size(int? size) => this(size: size);

  @override
  PageReponseImmeubleDto totalElements(int? totalElements) =>
      this(totalElements: totalElements);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PageReponseImmeubleDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PageReponseImmeubleDto(...).copyWith(id: 12, name: "My name")
  /// ````
  PageReponseImmeubleDto call({
    Object? content = const $CopyWithPlaceholder(),
    Object? page = const $CopyWithPlaceholder(),
    Object? size = const $CopyWithPlaceholder(),
    Object? totalElements = const $CopyWithPlaceholder(),
  }) {
    return PageReponseImmeubleDto(
      content: content == const $CopyWithPlaceholder()
          ? _value.content
          // ignore: cast_nullable_to_non_nullable
          : content as List<ImmeubleDto>?,
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

extension $PageReponseImmeubleDtoCopyWith on PageReponseImmeubleDto {
  /// Returns a callable class that can be used as follows: `instanceOfPageReponseImmeubleDto.copyWith(...)` or like so:`instanceOfPageReponseImmeubleDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$PageReponseImmeubleDtoCWProxy get copyWith =>
      _$PageReponseImmeubleDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

PageReponseImmeubleDto _$PageReponseImmeubleDtoFromJson(
  Map<String, dynamic> json,
) => $checkedCreate('PageReponseImmeubleDto', json, ($checkedConvert) {
  final val = PageReponseImmeubleDto(
    content: $checkedConvert(
      'content',
      (v) => (v as List<dynamic>?)
          ?.map((e) => ImmeubleDto.fromJson(e as Map<String, dynamic>))
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

Map<String, dynamic> _$PageReponseImmeubleDtoToJson(
  PageReponseImmeubleDto instance,
) => <String, dynamic>{
  'content': ?instance.content?.map((e) => e.toJson()).toList(),
  'page': ?instance.page,
  'size': ?instance.size,
  'totalElements': ?instance.totalElements,
};
