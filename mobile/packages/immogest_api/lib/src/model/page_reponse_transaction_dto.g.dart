// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'page_reponse_transaction_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$PageReponseTransactionDtoCWProxy {
  PageReponseTransactionDto content(List<TransactionDto>? content);

  PageReponseTransactionDto page(int? page);

  PageReponseTransactionDto size(int? size);

  PageReponseTransactionDto totalElements(int? totalElements);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PageReponseTransactionDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PageReponseTransactionDto(...).copyWith(id: 12, name: "My name")
  /// ````
  PageReponseTransactionDto call({
    List<TransactionDto>? content,
    int? page,
    int? size,
    int? totalElements,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfPageReponseTransactionDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfPageReponseTransactionDto.copyWith.fieldName(...)`
class _$PageReponseTransactionDtoCWProxyImpl
    implements _$PageReponseTransactionDtoCWProxy {
  const _$PageReponseTransactionDtoCWProxyImpl(this._value);

  final PageReponseTransactionDto _value;

  @override
  PageReponseTransactionDto content(List<TransactionDto>? content) =>
      this(content: content);

  @override
  PageReponseTransactionDto page(int? page) => this(page: page);

  @override
  PageReponseTransactionDto size(int? size) => this(size: size);

  @override
  PageReponseTransactionDto totalElements(int? totalElements) =>
      this(totalElements: totalElements);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PageReponseTransactionDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PageReponseTransactionDto(...).copyWith(id: 12, name: "My name")
  /// ````
  PageReponseTransactionDto call({
    Object? content = const $CopyWithPlaceholder(),
    Object? page = const $CopyWithPlaceholder(),
    Object? size = const $CopyWithPlaceholder(),
    Object? totalElements = const $CopyWithPlaceholder(),
  }) {
    return PageReponseTransactionDto(
      content: content == const $CopyWithPlaceholder()
          ? _value.content
          // ignore: cast_nullable_to_non_nullable
          : content as List<TransactionDto>?,
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

extension $PageReponseTransactionDtoCopyWith on PageReponseTransactionDto {
  /// Returns a callable class that can be used as follows: `instanceOfPageReponseTransactionDto.copyWith(...)` or like so:`instanceOfPageReponseTransactionDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$PageReponseTransactionDtoCWProxy get copyWith =>
      _$PageReponseTransactionDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

PageReponseTransactionDto _$PageReponseTransactionDtoFromJson(
  Map<String, dynamic> json,
) => $checkedCreate('PageReponseTransactionDto', json, ($checkedConvert) {
  final val = PageReponseTransactionDto(
    content: $checkedConvert(
      'content',
      (v) => (v as List<dynamic>?)
          ?.map((e) => TransactionDto.fromJson(e as Map<String, dynamic>))
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

Map<String, dynamic> _$PageReponseTransactionDtoToJson(
  PageReponseTransactionDto instance,
) => <String, dynamic>{
  'content': ?instance.content?.map((e) => e.toJson()).toList(),
  'page': ?instance.page,
  'size': ?instance.size,
  'totalElements': ?instance.totalElements,
};
