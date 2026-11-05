// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'page_reponse_lot_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$PageReponseLotDtoCWProxy {
  PageReponseLotDto content(List<LotDto>? content);

  PageReponseLotDto page(int? page);

  PageReponseLotDto size(int? size);

  PageReponseLotDto totalElements(int? totalElements);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PageReponseLotDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PageReponseLotDto(...).copyWith(id: 12, name: "My name")
  /// ````
  PageReponseLotDto call({
    List<LotDto>? content,
    int? page,
    int? size,
    int? totalElements,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfPageReponseLotDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfPageReponseLotDto.copyWith.fieldName(...)`
class _$PageReponseLotDtoCWProxyImpl implements _$PageReponseLotDtoCWProxy {
  const _$PageReponseLotDtoCWProxyImpl(this._value);

  final PageReponseLotDto _value;

  @override
  PageReponseLotDto content(List<LotDto>? content) => this(content: content);

  @override
  PageReponseLotDto page(int? page) => this(page: page);

  @override
  PageReponseLotDto size(int? size) => this(size: size);

  @override
  PageReponseLotDto totalElements(int? totalElements) =>
      this(totalElements: totalElements);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PageReponseLotDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PageReponseLotDto(...).copyWith(id: 12, name: "My name")
  /// ````
  PageReponseLotDto call({
    Object? content = const $CopyWithPlaceholder(),
    Object? page = const $CopyWithPlaceholder(),
    Object? size = const $CopyWithPlaceholder(),
    Object? totalElements = const $CopyWithPlaceholder(),
  }) {
    return PageReponseLotDto(
      content: content == const $CopyWithPlaceholder()
          ? _value.content
          // ignore: cast_nullable_to_non_nullable
          : content as List<LotDto>?,
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

extension $PageReponseLotDtoCopyWith on PageReponseLotDto {
  /// Returns a callable class that can be used as follows: `instanceOfPageReponseLotDto.copyWith(...)` or like so:`instanceOfPageReponseLotDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$PageReponseLotDtoCWProxy get copyWith =>
      _$PageReponseLotDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

PageReponseLotDto _$PageReponseLotDtoFromJson(Map<String, dynamic> json) =>
    $checkedCreate('PageReponseLotDto', json, ($checkedConvert) {
      final val = PageReponseLotDto(
        content: $checkedConvert(
          'content',
          (v) => (v as List<dynamic>?)
              ?.map((e) => LotDto.fromJson(e as Map<String, dynamic>))
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

Map<String, dynamic> _$PageReponseLotDtoToJson(PageReponseLotDto instance) =>
    <String, dynamic>{
      'content': ?instance.content?.map((e) => e.toJson()).toList(),
      'page': ?instance.page,
      'size': ?instance.size,
      'totalElements': ?instance.totalElements,
    };
