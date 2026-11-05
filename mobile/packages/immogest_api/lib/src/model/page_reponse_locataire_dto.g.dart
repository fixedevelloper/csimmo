// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'page_reponse_locataire_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$PageReponseLocataireDtoCWProxy {
  PageReponseLocataireDto content(List<LocataireDto>? content);

  PageReponseLocataireDto page(int? page);

  PageReponseLocataireDto size(int? size);

  PageReponseLocataireDto totalElements(int? totalElements);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PageReponseLocataireDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PageReponseLocataireDto(...).copyWith(id: 12, name: "My name")
  /// ````
  PageReponseLocataireDto call({
    List<LocataireDto>? content,
    int? page,
    int? size,
    int? totalElements,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfPageReponseLocataireDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfPageReponseLocataireDto.copyWith.fieldName(...)`
class _$PageReponseLocataireDtoCWProxyImpl
    implements _$PageReponseLocataireDtoCWProxy {
  const _$PageReponseLocataireDtoCWProxyImpl(this._value);

  final PageReponseLocataireDto _value;

  @override
  PageReponseLocataireDto content(List<LocataireDto>? content) =>
      this(content: content);

  @override
  PageReponseLocataireDto page(int? page) => this(page: page);

  @override
  PageReponseLocataireDto size(int? size) => this(size: size);

  @override
  PageReponseLocataireDto totalElements(int? totalElements) =>
      this(totalElements: totalElements);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PageReponseLocataireDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PageReponseLocataireDto(...).copyWith(id: 12, name: "My name")
  /// ````
  PageReponseLocataireDto call({
    Object? content = const $CopyWithPlaceholder(),
    Object? page = const $CopyWithPlaceholder(),
    Object? size = const $CopyWithPlaceholder(),
    Object? totalElements = const $CopyWithPlaceholder(),
  }) {
    return PageReponseLocataireDto(
      content: content == const $CopyWithPlaceholder()
          ? _value.content
          // ignore: cast_nullable_to_non_nullable
          : content as List<LocataireDto>?,
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

extension $PageReponseLocataireDtoCopyWith on PageReponseLocataireDto {
  /// Returns a callable class that can be used as follows: `instanceOfPageReponseLocataireDto.copyWith(...)` or like so:`instanceOfPageReponseLocataireDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$PageReponseLocataireDtoCWProxy get copyWith =>
      _$PageReponseLocataireDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

PageReponseLocataireDto _$PageReponseLocataireDtoFromJson(
  Map<String, dynamic> json,
) => $checkedCreate('PageReponseLocataireDto', json, ($checkedConvert) {
  final val = PageReponseLocataireDto(
    content: $checkedConvert(
      'content',
      (v) => (v as List<dynamic>?)
          ?.map((e) => LocataireDto.fromJson(e as Map<String, dynamic>))
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

Map<String, dynamic> _$PageReponseLocataireDtoToJson(
  PageReponseLocataireDto instance,
) => <String, dynamic>{
  'content': ?instance.content?.map((e) => e.toJson()).toList(),
  'page': ?instance.page,
  'size': ?instance.size,
  'totalElements': ?instance.totalElements,
};
