// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'page_reponse_reversement_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$PageReponseReversementDtoCWProxy {
  PageReponseReversementDto content(List<ReversementDto>? content);

  PageReponseReversementDto page(int? page);

  PageReponseReversementDto size(int? size);

  PageReponseReversementDto totalElements(int? totalElements);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PageReponseReversementDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PageReponseReversementDto(...).copyWith(id: 12, name: "My name")
  /// ````
  PageReponseReversementDto call({
    List<ReversementDto>? content,
    int? page,
    int? size,
    int? totalElements,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfPageReponseReversementDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfPageReponseReversementDto.copyWith.fieldName(...)`
class _$PageReponseReversementDtoCWProxyImpl
    implements _$PageReponseReversementDtoCWProxy {
  const _$PageReponseReversementDtoCWProxyImpl(this._value);

  final PageReponseReversementDto _value;

  @override
  PageReponseReversementDto content(List<ReversementDto>? content) =>
      this(content: content);

  @override
  PageReponseReversementDto page(int? page) => this(page: page);

  @override
  PageReponseReversementDto size(int? size) => this(size: size);

  @override
  PageReponseReversementDto totalElements(int? totalElements) =>
      this(totalElements: totalElements);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PageReponseReversementDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PageReponseReversementDto(...).copyWith(id: 12, name: "My name")
  /// ````
  PageReponseReversementDto call({
    Object? content = const $CopyWithPlaceholder(),
    Object? page = const $CopyWithPlaceholder(),
    Object? size = const $CopyWithPlaceholder(),
    Object? totalElements = const $CopyWithPlaceholder(),
  }) {
    return PageReponseReversementDto(
      content: content == const $CopyWithPlaceholder()
          ? _value.content
          // ignore: cast_nullable_to_non_nullable
          : content as List<ReversementDto>?,
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

extension $PageReponseReversementDtoCopyWith on PageReponseReversementDto {
  /// Returns a callable class that can be used as follows: `instanceOfPageReponseReversementDto.copyWith(...)` or like so:`instanceOfPageReponseReversementDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$PageReponseReversementDtoCWProxy get copyWith =>
      _$PageReponseReversementDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

PageReponseReversementDto _$PageReponseReversementDtoFromJson(
  Map<String, dynamic> json,
) => $checkedCreate('PageReponseReversementDto', json, ($checkedConvert) {
  final val = PageReponseReversementDto(
    content: $checkedConvert(
      'content',
      (v) => (v as List<dynamic>?)
          ?.map((e) => ReversementDto.fromJson(e as Map<String, dynamic>))
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

Map<String, dynamic> _$PageReponseReversementDtoToJson(
  PageReponseReversementDto instance,
) => <String, dynamic>{
  'content': ?instance.content?.map((e) => e.toJson()).toList(),
  'page': ?instance.page,
  'size': ?instance.size,
  'totalElements': ?instance.totalElements,
};
