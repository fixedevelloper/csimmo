// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'page_reponse_echeance_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$PageReponseEcheanceDtoCWProxy {
  PageReponseEcheanceDto content(List<EcheanceDto>? content);

  PageReponseEcheanceDto page(int? page);

  PageReponseEcheanceDto size(int? size);

  PageReponseEcheanceDto totalElements(int? totalElements);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PageReponseEcheanceDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PageReponseEcheanceDto(...).copyWith(id: 12, name: "My name")
  /// ````
  PageReponseEcheanceDto call({
    List<EcheanceDto>? content,
    int? page,
    int? size,
    int? totalElements,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfPageReponseEcheanceDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfPageReponseEcheanceDto.copyWith.fieldName(...)`
class _$PageReponseEcheanceDtoCWProxyImpl
    implements _$PageReponseEcheanceDtoCWProxy {
  const _$PageReponseEcheanceDtoCWProxyImpl(this._value);

  final PageReponseEcheanceDto _value;

  @override
  PageReponseEcheanceDto content(List<EcheanceDto>? content) =>
      this(content: content);

  @override
  PageReponseEcheanceDto page(int? page) => this(page: page);

  @override
  PageReponseEcheanceDto size(int? size) => this(size: size);

  @override
  PageReponseEcheanceDto totalElements(int? totalElements) =>
      this(totalElements: totalElements);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PageReponseEcheanceDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PageReponseEcheanceDto(...).copyWith(id: 12, name: "My name")
  /// ````
  PageReponseEcheanceDto call({
    Object? content = const $CopyWithPlaceholder(),
    Object? page = const $CopyWithPlaceholder(),
    Object? size = const $CopyWithPlaceholder(),
    Object? totalElements = const $CopyWithPlaceholder(),
  }) {
    return PageReponseEcheanceDto(
      content: content == const $CopyWithPlaceholder()
          ? _value.content
          // ignore: cast_nullable_to_non_nullable
          : content as List<EcheanceDto>?,
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

extension $PageReponseEcheanceDtoCopyWith on PageReponseEcheanceDto {
  /// Returns a callable class that can be used as follows: `instanceOfPageReponseEcheanceDto.copyWith(...)` or like so:`instanceOfPageReponseEcheanceDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$PageReponseEcheanceDtoCWProxy get copyWith =>
      _$PageReponseEcheanceDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

PageReponseEcheanceDto _$PageReponseEcheanceDtoFromJson(
  Map<String, dynamic> json,
) => $checkedCreate('PageReponseEcheanceDto', json, ($checkedConvert) {
  final val = PageReponseEcheanceDto(
    content: $checkedConvert(
      'content',
      (v) => (v as List<dynamic>?)
          ?.map((e) => EcheanceDto.fromJson(e as Map<String, dynamic>))
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

Map<String, dynamic> _$PageReponseEcheanceDtoToJson(
  PageReponseEcheanceDto instance,
) => <String, dynamic>{
  'content': ?instance.content?.map((e) => e.toJson()).toList(),
  'page': ?instance.page,
  'size': ?instance.size,
  'totalElements': ?instance.totalElements,
};
