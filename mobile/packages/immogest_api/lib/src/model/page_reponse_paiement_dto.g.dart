// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'page_reponse_paiement_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$PageReponsePaiementDtoCWProxy {
  PageReponsePaiementDto content(List<PaiementDto>? content);

  PageReponsePaiementDto page(int? page);

  PageReponsePaiementDto size(int? size);

  PageReponsePaiementDto totalElements(int? totalElements);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PageReponsePaiementDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PageReponsePaiementDto(...).copyWith(id: 12, name: "My name")
  /// ````
  PageReponsePaiementDto call({
    List<PaiementDto>? content,
    int? page,
    int? size,
    int? totalElements,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfPageReponsePaiementDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfPageReponsePaiementDto.copyWith.fieldName(...)`
class _$PageReponsePaiementDtoCWProxyImpl
    implements _$PageReponsePaiementDtoCWProxy {
  const _$PageReponsePaiementDtoCWProxyImpl(this._value);

  final PageReponsePaiementDto _value;

  @override
  PageReponsePaiementDto content(List<PaiementDto>? content) =>
      this(content: content);

  @override
  PageReponsePaiementDto page(int? page) => this(page: page);

  @override
  PageReponsePaiementDto size(int? size) => this(size: size);

  @override
  PageReponsePaiementDto totalElements(int? totalElements) =>
      this(totalElements: totalElements);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PageReponsePaiementDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PageReponsePaiementDto(...).copyWith(id: 12, name: "My name")
  /// ````
  PageReponsePaiementDto call({
    Object? content = const $CopyWithPlaceholder(),
    Object? page = const $CopyWithPlaceholder(),
    Object? size = const $CopyWithPlaceholder(),
    Object? totalElements = const $CopyWithPlaceholder(),
  }) {
    return PageReponsePaiementDto(
      content: content == const $CopyWithPlaceholder()
          ? _value.content
          // ignore: cast_nullable_to_non_nullable
          : content as List<PaiementDto>?,
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

extension $PageReponsePaiementDtoCopyWith on PageReponsePaiementDto {
  /// Returns a callable class that can be used as follows: `instanceOfPageReponsePaiementDto.copyWith(...)` or like so:`instanceOfPageReponsePaiementDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$PageReponsePaiementDtoCWProxy get copyWith =>
      _$PageReponsePaiementDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

PageReponsePaiementDto _$PageReponsePaiementDtoFromJson(
  Map<String, dynamic> json,
) => $checkedCreate('PageReponsePaiementDto', json, ($checkedConvert) {
  final val = PageReponsePaiementDto(
    content: $checkedConvert(
      'content',
      (v) => (v as List<dynamic>?)
          ?.map((e) => PaiementDto.fromJson(e as Map<String, dynamic>))
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

Map<String, dynamic> _$PageReponsePaiementDtoToJson(
  PageReponsePaiementDto instance,
) => <String, dynamic>{
  'content': ?instance.content?.map((e) => e.toJson()).toList(),
  'page': ?instance.page,
  'size': ?instance.size,
  'totalElements': ?instance.totalElements,
};
