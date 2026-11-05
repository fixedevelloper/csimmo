// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'page_reponse_utilisateur_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$PageReponseUtilisateurDtoCWProxy {
  PageReponseUtilisateurDto content(List<UtilisateurDto>? content);

  PageReponseUtilisateurDto page(int? page);

  PageReponseUtilisateurDto size(int? size);

  PageReponseUtilisateurDto totalElements(int? totalElements);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PageReponseUtilisateurDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PageReponseUtilisateurDto(...).copyWith(id: 12, name: "My name")
  /// ````
  PageReponseUtilisateurDto call({
    List<UtilisateurDto>? content,
    int? page,
    int? size,
    int? totalElements,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfPageReponseUtilisateurDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfPageReponseUtilisateurDto.copyWith.fieldName(...)`
class _$PageReponseUtilisateurDtoCWProxyImpl
    implements _$PageReponseUtilisateurDtoCWProxy {
  const _$PageReponseUtilisateurDtoCWProxyImpl(this._value);

  final PageReponseUtilisateurDto _value;

  @override
  PageReponseUtilisateurDto content(List<UtilisateurDto>? content) =>
      this(content: content);

  @override
  PageReponseUtilisateurDto page(int? page) => this(page: page);

  @override
  PageReponseUtilisateurDto size(int? size) => this(size: size);

  @override
  PageReponseUtilisateurDto totalElements(int? totalElements) =>
      this(totalElements: totalElements);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PageReponseUtilisateurDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PageReponseUtilisateurDto(...).copyWith(id: 12, name: "My name")
  /// ````
  PageReponseUtilisateurDto call({
    Object? content = const $CopyWithPlaceholder(),
    Object? page = const $CopyWithPlaceholder(),
    Object? size = const $CopyWithPlaceholder(),
    Object? totalElements = const $CopyWithPlaceholder(),
  }) {
    return PageReponseUtilisateurDto(
      content: content == const $CopyWithPlaceholder()
          ? _value.content
          // ignore: cast_nullable_to_non_nullable
          : content as List<UtilisateurDto>?,
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

extension $PageReponseUtilisateurDtoCopyWith on PageReponseUtilisateurDto {
  /// Returns a callable class that can be used as follows: `instanceOfPageReponseUtilisateurDto.copyWith(...)` or like so:`instanceOfPageReponseUtilisateurDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$PageReponseUtilisateurDtoCWProxy get copyWith =>
      _$PageReponseUtilisateurDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

PageReponseUtilisateurDto _$PageReponseUtilisateurDtoFromJson(
  Map<String, dynamic> json,
) => $checkedCreate('PageReponseUtilisateurDto', json, ($checkedConvert) {
  final val = PageReponseUtilisateurDto(
    content: $checkedConvert(
      'content',
      (v) => (v as List<dynamic>?)
          ?.map((e) => UtilisateurDto.fromJson(e as Map<String, dynamic>))
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

Map<String, dynamic> _$PageReponseUtilisateurDtoToJson(
  PageReponseUtilisateurDto instance,
) => <String, dynamic>{
  'content': ?instance.content?.map((e) => e.toJson()).toList(),
  'page': ?instance.page,
  'size': ?instance.size,
  'totalElements': ?instance.totalElements,
};
