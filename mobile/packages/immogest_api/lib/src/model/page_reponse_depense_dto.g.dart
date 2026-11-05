// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'page_reponse_depense_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$PageReponseDepenseDtoCWProxy {
  PageReponseDepenseDto content(List<DepenseDto>? content);

  PageReponseDepenseDto page(int? page);

  PageReponseDepenseDto size(int? size);

  PageReponseDepenseDto totalElements(int? totalElements);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PageReponseDepenseDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PageReponseDepenseDto(...).copyWith(id: 12, name: "My name")
  /// ````
  PageReponseDepenseDto call({
    List<DepenseDto>? content,
    int? page,
    int? size,
    int? totalElements,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfPageReponseDepenseDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfPageReponseDepenseDto.copyWith.fieldName(...)`
class _$PageReponseDepenseDtoCWProxyImpl
    implements _$PageReponseDepenseDtoCWProxy {
  const _$PageReponseDepenseDtoCWProxyImpl(this._value);

  final PageReponseDepenseDto _value;

  @override
  PageReponseDepenseDto content(List<DepenseDto>? content) =>
      this(content: content);

  @override
  PageReponseDepenseDto page(int? page) => this(page: page);

  @override
  PageReponseDepenseDto size(int? size) => this(size: size);

  @override
  PageReponseDepenseDto totalElements(int? totalElements) =>
      this(totalElements: totalElements);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PageReponseDepenseDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PageReponseDepenseDto(...).copyWith(id: 12, name: "My name")
  /// ````
  PageReponseDepenseDto call({
    Object? content = const $CopyWithPlaceholder(),
    Object? page = const $CopyWithPlaceholder(),
    Object? size = const $CopyWithPlaceholder(),
    Object? totalElements = const $CopyWithPlaceholder(),
  }) {
    return PageReponseDepenseDto(
      content: content == const $CopyWithPlaceholder()
          ? _value.content
          // ignore: cast_nullable_to_non_nullable
          : content as List<DepenseDto>?,
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

extension $PageReponseDepenseDtoCopyWith on PageReponseDepenseDto {
  /// Returns a callable class that can be used as follows: `instanceOfPageReponseDepenseDto.copyWith(...)` or like so:`instanceOfPageReponseDepenseDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$PageReponseDepenseDtoCWProxy get copyWith =>
      _$PageReponseDepenseDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

PageReponseDepenseDto _$PageReponseDepenseDtoFromJson(
  Map<String, dynamic> json,
) => $checkedCreate('PageReponseDepenseDto', json, ($checkedConvert) {
  final val = PageReponseDepenseDto(
    content: $checkedConvert(
      'content',
      (v) => (v as List<dynamic>?)
          ?.map((e) => DepenseDto.fromJson(e as Map<String, dynamic>))
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

Map<String, dynamic> _$PageReponseDepenseDtoToJson(
  PageReponseDepenseDto instance,
) => <String, dynamic>{
  'content': ?instance.content?.map((e) => e.toJson()).toList(),
  'page': ?instance.page,
  'size': ?instance.size,
  'totalElements': ?instance.totalElements,
};
