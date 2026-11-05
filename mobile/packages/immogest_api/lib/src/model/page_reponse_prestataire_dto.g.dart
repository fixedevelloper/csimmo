// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'page_reponse_prestataire_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$PageReponsePrestataireDtoCWProxy {
  PageReponsePrestataireDto content(List<PrestataireDto>? content);

  PageReponsePrestataireDto page(int? page);

  PageReponsePrestataireDto size(int? size);

  PageReponsePrestataireDto totalElements(int? totalElements);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PageReponsePrestataireDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PageReponsePrestataireDto(...).copyWith(id: 12, name: "My name")
  /// ````
  PageReponsePrestataireDto call({
    List<PrestataireDto>? content,
    int? page,
    int? size,
    int? totalElements,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfPageReponsePrestataireDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfPageReponsePrestataireDto.copyWith.fieldName(...)`
class _$PageReponsePrestataireDtoCWProxyImpl
    implements _$PageReponsePrestataireDtoCWProxy {
  const _$PageReponsePrestataireDtoCWProxyImpl(this._value);

  final PageReponsePrestataireDto _value;

  @override
  PageReponsePrestataireDto content(List<PrestataireDto>? content) =>
      this(content: content);

  @override
  PageReponsePrestataireDto page(int? page) => this(page: page);

  @override
  PageReponsePrestataireDto size(int? size) => this(size: size);

  @override
  PageReponsePrestataireDto totalElements(int? totalElements) =>
      this(totalElements: totalElements);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PageReponsePrestataireDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PageReponsePrestataireDto(...).copyWith(id: 12, name: "My name")
  /// ````
  PageReponsePrestataireDto call({
    Object? content = const $CopyWithPlaceholder(),
    Object? page = const $CopyWithPlaceholder(),
    Object? size = const $CopyWithPlaceholder(),
    Object? totalElements = const $CopyWithPlaceholder(),
  }) {
    return PageReponsePrestataireDto(
      content: content == const $CopyWithPlaceholder()
          ? _value.content
          // ignore: cast_nullable_to_non_nullable
          : content as List<PrestataireDto>?,
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

extension $PageReponsePrestataireDtoCopyWith on PageReponsePrestataireDto {
  /// Returns a callable class that can be used as follows: `instanceOfPageReponsePrestataireDto.copyWith(...)` or like so:`instanceOfPageReponsePrestataireDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$PageReponsePrestataireDtoCWProxy get copyWith =>
      _$PageReponsePrestataireDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

PageReponsePrestataireDto _$PageReponsePrestataireDtoFromJson(
  Map<String, dynamic> json,
) => $checkedCreate('PageReponsePrestataireDto', json, ($checkedConvert) {
  final val = PageReponsePrestataireDto(
    content: $checkedConvert(
      'content',
      (v) => (v as List<dynamic>?)
          ?.map((e) => PrestataireDto.fromJson(e as Map<String, dynamic>))
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

Map<String, dynamic> _$PageReponsePrestataireDtoToJson(
  PageReponsePrestataireDto instance,
) => <String, dynamic>{
  'content': ?instance.content?.map((e) => e.toJson()).toList(),
  'page': ?instance.page,
  'size': ?instance.size,
  'totalElements': ?instance.totalElements,
};
