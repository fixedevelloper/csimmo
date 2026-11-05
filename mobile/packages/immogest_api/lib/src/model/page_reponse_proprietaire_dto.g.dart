// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'page_reponse_proprietaire_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$PageReponseProprietaireDtoCWProxy {
  PageReponseProprietaireDto content(List<ProprietaireDto>? content);

  PageReponseProprietaireDto page(int? page);

  PageReponseProprietaireDto size(int? size);

  PageReponseProprietaireDto totalElements(int? totalElements);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PageReponseProprietaireDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PageReponseProprietaireDto(...).copyWith(id: 12, name: "My name")
  /// ````
  PageReponseProprietaireDto call({
    List<ProprietaireDto>? content,
    int? page,
    int? size,
    int? totalElements,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfPageReponseProprietaireDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfPageReponseProprietaireDto.copyWith.fieldName(...)`
class _$PageReponseProprietaireDtoCWProxyImpl
    implements _$PageReponseProprietaireDtoCWProxy {
  const _$PageReponseProprietaireDtoCWProxyImpl(this._value);

  final PageReponseProprietaireDto _value;

  @override
  PageReponseProprietaireDto content(List<ProprietaireDto>? content) =>
      this(content: content);

  @override
  PageReponseProprietaireDto page(int? page) => this(page: page);

  @override
  PageReponseProprietaireDto size(int? size) => this(size: size);

  @override
  PageReponseProprietaireDto totalElements(int? totalElements) =>
      this(totalElements: totalElements);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PageReponseProprietaireDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PageReponseProprietaireDto(...).copyWith(id: 12, name: "My name")
  /// ````
  PageReponseProprietaireDto call({
    Object? content = const $CopyWithPlaceholder(),
    Object? page = const $CopyWithPlaceholder(),
    Object? size = const $CopyWithPlaceholder(),
    Object? totalElements = const $CopyWithPlaceholder(),
  }) {
    return PageReponseProprietaireDto(
      content: content == const $CopyWithPlaceholder()
          ? _value.content
          // ignore: cast_nullable_to_non_nullable
          : content as List<ProprietaireDto>?,
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

extension $PageReponseProprietaireDtoCopyWith on PageReponseProprietaireDto {
  /// Returns a callable class that can be used as follows: `instanceOfPageReponseProprietaireDto.copyWith(...)` or like so:`instanceOfPageReponseProprietaireDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$PageReponseProprietaireDtoCWProxy get copyWith =>
      _$PageReponseProprietaireDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

PageReponseProprietaireDto _$PageReponseProprietaireDtoFromJson(
  Map<String, dynamic> json,
) => $checkedCreate('PageReponseProprietaireDto', json, ($checkedConvert) {
  final val = PageReponseProprietaireDto(
    content: $checkedConvert(
      'content',
      (v) => (v as List<dynamic>?)
          ?.map((e) => ProprietaireDto.fromJson(e as Map<String, dynamic>))
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

Map<String, dynamic> _$PageReponseProprietaireDtoToJson(
  PageReponseProprietaireDto instance,
) => <String, dynamic>{
  'content': ?instance.content?.map((e) => e.toJson()).toList(),
  'page': ?instance.page,
  'size': ?instance.size,
  'totalElements': ?instance.totalElements,
};
