// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'page_reponse_signalement_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$PageReponseSignalementDtoCWProxy {
  PageReponseSignalementDto content(List<SignalementDto>? content);

  PageReponseSignalementDto page(int? page);

  PageReponseSignalementDto size(int? size);

  PageReponseSignalementDto totalElements(int? totalElements);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PageReponseSignalementDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PageReponseSignalementDto(...).copyWith(id: 12, name: "My name")
  /// ````
  PageReponseSignalementDto call({
    List<SignalementDto>? content,
    int? page,
    int? size,
    int? totalElements,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfPageReponseSignalementDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfPageReponseSignalementDto.copyWith.fieldName(...)`
class _$PageReponseSignalementDtoCWProxyImpl
    implements _$PageReponseSignalementDtoCWProxy {
  const _$PageReponseSignalementDtoCWProxyImpl(this._value);

  final PageReponseSignalementDto _value;

  @override
  PageReponseSignalementDto content(List<SignalementDto>? content) =>
      this(content: content);

  @override
  PageReponseSignalementDto page(int? page) => this(page: page);

  @override
  PageReponseSignalementDto size(int? size) => this(size: size);

  @override
  PageReponseSignalementDto totalElements(int? totalElements) =>
      this(totalElements: totalElements);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PageReponseSignalementDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PageReponseSignalementDto(...).copyWith(id: 12, name: "My name")
  /// ````
  PageReponseSignalementDto call({
    Object? content = const $CopyWithPlaceholder(),
    Object? page = const $CopyWithPlaceholder(),
    Object? size = const $CopyWithPlaceholder(),
    Object? totalElements = const $CopyWithPlaceholder(),
  }) {
    return PageReponseSignalementDto(
      content: content == const $CopyWithPlaceholder()
          ? _value.content
          // ignore: cast_nullable_to_non_nullable
          : content as List<SignalementDto>?,
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

extension $PageReponseSignalementDtoCopyWith on PageReponseSignalementDto {
  /// Returns a callable class that can be used as follows: `instanceOfPageReponseSignalementDto.copyWith(...)` or like so:`instanceOfPageReponseSignalementDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$PageReponseSignalementDtoCWProxy get copyWith =>
      _$PageReponseSignalementDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

PageReponseSignalementDto _$PageReponseSignalementDtoFromJson(
  Map<String, dynamic> json,
) => $checkedCreate('PageReponseSignalementDto', json, ($checkedConvert) {
  final val = PageReponseSignalementDto(
    content: $checkedConvert(
      'content',
      (v) => (v as List<dynamic>?)
          ?.map((e) => SignalementDto.fromJson(e as Map<String, dynamic>))
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

Map<String, dynamic> _$PageReponseSignalementDtoToJson(
  PageReponseSignalementDto instance,
) => <String, dynamic>{
  'content': ?instance.content?.map((e) => e.toJson()).toList(),
  'page': ?instance.page,
  'size': ?instance.size,
  'totalElements': ?instance.totalElements,
};
