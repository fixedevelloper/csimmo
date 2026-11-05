// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'page_reponse_relance_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$PageReponseRelanceDtoCWProxy {
  PageReponseRelanceDto content(List<RelanceDto>? content);

  PageReponseRelanceDto page(int? page);

  PageReponseRelanceDto size(int? size);

  PageReponseRelanceDto totalElements(int? totalElements);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PageReponseRelanceDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PageReponseRelanceDto(...).copyWith(id: 12, name: "My name")
  /// ````
  PageReponseRelanceDto call({
    List<RelanceDto>? content,
    int? page,
    int? size,
    int? totalElements,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfPageReponseRelanceDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfPageReponseRelanceDto.copyWith.fieldName(...)`
class _$PageReponseRelanceDtoCWProxyImpl
    implements _$PageReponseRelanceDtoCWProxy {
  const _$PageReponseRelanceDtoCWProxyImpl(this._value);

  final PageReponseRelanceDto _value;

  @override
  PageReponseRelanceDto content(List<RelanceDto>? content) =>
      this(content: content);

  @override
  PageReponseRelanceDto page(int? page) => this(page: page);

  @override
  PageReponseRelanceDto size(int? size) => this(size: size);

  @override
  PageReponseRelanceDto totalElements(int? totalElements) =>
      this(totalElements: totalElements);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PageReponseRelanceDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PageReponseRelanceDto(...).copyWith(id: 12, name: "My name")
  /// ````
  PageReponseRelanceDto call({
    Object? content = const $CopyWithPlaceholder(),
    Object? page = const $CopyWithPlaceholder(),
    Object? size = const $CopyWithPlaceholder(),
    Object? totalElements = const $CopyWithPlaceholder(),
  }) {
    return PageReponseRelanceDto(
      content: content == const $CopyWithPlaceholder()
          ? _value.content
          // ignore: cast_nullable_to_non_nullable
          : content as List<RelanceDto>?,
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

extension $PageReponseRelanceDtoCopyWith on PageReponseRelanceDto {
  /// Returns a callable class that can be used as follows: `instanceOfPageReponseRelanceDto.copyWith(...)` or like so:`instanceOfPageReponseRelanceDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$PageReponseRelanceDtoCWProxy get copyWith =>
      _$PageReponseRelanceDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

PageReponseRelanceDto _$PageReponseRelanceDtoFromJson(
  Map<String, dynamic> json,
) => $checkedCreate('PageReponseRelanceDto', json, ($checkedConvert) {
  final val = PageReponseRelanceDto(
    content: $checkedConvert(
      'content',
      (v) => (v as List<dynamic>?)
          ?.map((e) => RelanceDto.fromJson(e as Map<String, dynamic>))
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

Map<String, dynamic> _$PageReponseRelanceDtoToJson(
  PageReponseRelanceDto instance,
) => <String, dynamic>{
  'content': ?instance.content?.map((e) => e.toJson()).toList(),
  'page': ?instance.page,
  'size': ?instance.size,
  'totalElements': ?instance.totalElements,
};
