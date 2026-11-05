// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'page_reponse_arriere_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$PageReponseArriereDtoCWProxy {
  PageReponseArriereDto content(List<ArriereDto>? content);

  PageReponseArriereDto page(int? page);

  PageReponseArriereDto size(int? size);

  PageReponseArriereDto totalElements(int? totalElements);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PageReponseArriereDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PageReponseArriereDto(...).copyWith(id: 12, name: "My name")
  /// ````
  PageReponseArriereDto call({
    List<ArriereDto>? content,
    int? page,
    int? size,
    int? totalElements,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfPageReponseArriereDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfPageReponseArriereDto.copyWith.fieldName(...)`
class _$PageReponseArriereDtoCWProxyImpl
    implements _$PageReponseArriereDtoCWProxy {
  const _$PageReponseArriereDtoCWProxyImpl(this._value);

  final PageReponseArriereDto _value;

  @override
  PageReponseArriereDto content(List<ArriereDto>? content) =>
      this(content: content);

  @override
  PageReponseArriereDto page(int? page) => this(page: page);

  @override
  PageReponseArriereDto size(int? size) => this(size: size);

  @override
  PageReponseArriereDto totalElements(int? totalElements) =>
      this(totalElements: totalElements);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PageReponseArriereDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PageReponseArriereDto(...).copyWith(id: 12, name: "My name")
  /// ````
  PageReponseArriereDto call({
    Object? content = const $CopyWithPlaceholder(),
    Object? page = const $CopyWithPlaceholder(),
    Object? size = const $CopyWithPlaceholder(),
    Object? totalElements = const $CopyWithPlaceholder(),
  }) {
    return PageReponseArriereDto(
      content: content == const $CopyWithPlaceholder()
          ? _value.content
          // ignore: cast_nullable_to_non_nullable
          : content as List<ArriereDto>?,
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

extension $PageReponseArriereDtoCopyWith on PageReponseArriereDto {
  /// Returns a callable class that can be used as follows: `instanceOfPageReponseArriereDto.copyWith(...)` or like so:`instanceOfPageReponseArriereDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$PageReponseArriereDtoCWProxy get copyWith =>
      _$PageReponseArriereDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

PageReponseArriereDto _$PageReponseArriereDtoFromJson(
  Map<String, dynamic> json,
) => $checkedCreate('PageReponseArriereDto', json, ($checkedConvert) {
  final val = PageReponseArriereDto(
    content: $checkedConvert(
      'content',
      (v) => (v as List<dynamic>?)
          ?.map((e) => ArriereDto.fromJson(e as Map<String, dynamic>))
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

Map<String, dynamic> _$PageReponseArriereDtoToJson(
  PageReponseArriereDto instance,
) => <String, dynamic>{
  'content': ?instance.content?.map((e) => e.toJson()).toList(),
  'page': ?instance.page,
  'size': ?instance.size,
  'totalElements': ?instance.totalElements,
};
