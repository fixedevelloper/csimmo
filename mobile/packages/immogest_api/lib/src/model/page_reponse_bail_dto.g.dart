// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'page_reponse_bail_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$PageReponseBailDtoCWProxy {
  PageReponseBailDto content(List<BailDto>? content);

  PageReponseBailDto page(int? page);

  PageReponseBailDto size(int? size);

  PageReponseBailDto totalElements(int? totalElements);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PageReponseBailDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PageReponseBailDto(...).copyWith(id: 12, name: "My name")
  /// ````
  PageReponseBailDto call({
    List<BailDto>? content,
    int? page,
    int? size,
    int? totalElements,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfPageReponseBailDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfPageReponseBailDto.copyWith.fieldName(...)`
class _$PageReponseBailDtoCWProxyImpl implements _$PageReponseBailDtoCWProxy {
  const _$PageReponseBailDtoCWProxyImpl(this._value);

  final PageReponseBailDto _value;

  @override
  PageReponseBailDto content(List<BailDto>? content) => this(content: content);

  @override
  PageReponseBailDto page(int? page) => this(page: page);

  @override
  PageReponseBailDto size(int? size) => this(size: size);

  @override
  PageReponseBailDto totalElements(int? totalElements) =>
      this(totalElements: totalElements);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PageReponseBailDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PageReponseBailDto(...).copyWith(id: 12, name: "My name")
  /// ````
  PageReponseBailDto call({
    Object? content = const $CopyWithPlaceholder(),
    Object? page = const $CopyWithPlaceholder(),
    Object? size = const $CopyWithPlaceholder(),
    Object? totalElements = const $CopyWithPlaceholder(),
  }) {
    return PageReponseBailDto(
      content: content == const $CopyWithPlaceholder()
          ? _value.content
          // ignore: cast_nullable_to_non_nullable
          : content as List<BailDto>?,
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

extension $PageReponseBailDtoCopyWith on PageReponseBailDto {
  /// Returns a callable class that can be used as follows: `instanceOfPageReponseBailDto.copyWith(...)` or like so:`instanceOfPageReponseBailDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$PageReponseBailDtoCWProxy get copyWith =>
      _$PageReponseBailDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

PageReponseBailDto _$PageReponseBailDtoFromJson(Map<String, dynamic> json) =>
    $checkedCreate('PageReponseBailDto', json, ($checkedConvert) {
      final val = PageReponseBailDto(
        content: $checkedConvert(
          'content',
          (v) => (v as List<dynamic>?)
              ?.map((e) => BailDto.fromJson(e as Map<String, dynamic>))
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

Map<String, dynamic> _$PageReponseBailDtoToJson(PageReponseBailDto instance) =>
    <String, dynamic>{
      'content': ?instance.content?.map((e) => e.toJson()).toList(),
      'page': ?instance.page,
      'size': ?instance.size,
      'totalElements': ?instance.totalElements,
    };
