// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'page_reponse_mise_en_demeure_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$PageReponseMiseEnDemeureDtoCWProxy {
  PageReponseMiseEnDemeureDto content(List<MiseEnDemeureDto>? content);

  PageReponseMiseEnDemeureDto page(int? page);

  PageReponseMiseEnDemeureDto size(int? size);

  PageReponseMiseEnDemeureDto totalElements(int? totalElements);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PageReponseMiseEnDemeureDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PageReponseMiseEnDemeureDto(...).copyWith(id: 12, name: "My name")
  /// ````
  PageReponseMiseEnDemeureDto call({
    List<MiseEnDemeureDto>? content,
    int? page,
    int? size,
    int? totalElements,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfPageReponseMiseEnDemeureDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfPageReponseMiseEnDemeureDto.copyWith.fieldName(...)`
class _$PageReponseMiseEnDemeureDtoCWProxyImpl
    implements _$PageReponseMiseEnDemeureDtoCWProxy {
  const _$PageReponseMiseEnDemeureDtoCWProxyImpl(this._value);

  final PageReponseMiseEnDemeureDto _value;

  @override
  PageReponseMiseEnDemeureDto content(List<MiseEnDemeureDto>? content) =>
      this(content: content);

  @override
  PageReponseMiseEnDemeureDto page(int? page) => this(page: page);

  @override
  PageReponseMiseEnDemeureDto size(int? size) => this(size: size);

  @override
  PageReponseMiseEnDemeureDto totalElements(int? totalElements) =>
      this(totalElements: totalElements);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PageReponseMiseEnDemeureDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PageReponseMiseEnDemeureDto(...).copyWith(id: 12, name: "My name")
  /// ````
  PageReponseMiseEnDemeureDto call({
    Object? content = const $CopyWithPlaceholder(),
    Object? page = const $CopyWithPlaceholder(),
    Object? size = const $CopyWithPlaceholder(),
    Object? totalElements = const $CopyWithPlaceholder(),
  }) {
    return PageReponseMiseEnDemeureDto(
      content: content == const $CopyWithPlaceholder()
          ? _value.content
          // ignore: cast_nullable_to_non_nullable
          : content as List<MiseEnDemeureDto>?,
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

extension $PageReponseMiseEnDemeureDtoCopyWith on PageReponseMiseEnDemeureDto {
  /// Returns a callable class that can be used as follows: `instanceOfPageReponseMiseEnDemeureDto.copyWith(...)` or like so:`instanceOfPageReponseMiseEnDemeureDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$PageReponseMiseEnDemeureDtoCWProxy get copyWith =>
      _$PageReponseMiseEnDemeureDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

PageReponseMiseEnDemeureDto _$PageReponseMiseEnDemeureDtoFromJson(
  Map<String, dynamic> json,
) => $checkedCreate('PageReponseMiseEnDemeureDto', json, ($checkedConvert) {
  final val = PageReponseMiseEnDemeureDto(
    content: $checkedConvert(
      'content',
      (v) => (v as List<dynamic>?)
          ?.map((e) => MiseEnDemeureDto.fromJson(e as Map<String, dynamic>))
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

Map<String, dynamic> _$PageReponseMiseEnDemeureDtoToJson(
  PageReponseMiseEnDemeureDto instance,
) => <String, dynamic>{
  'content': ?instance.content?.map((e) => e.toJson()).toList(),
  'page': ?instance.page,
  'size': ?instance.size,
  'totalElements': ?instance.totalElements,
};
