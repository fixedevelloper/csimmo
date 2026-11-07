// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'page_reponse_entree_audit.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$PageReponseEntreeAuditCWProxy {
  PageReponseEntreeAudit content(List<EntreeAudit>? content);

  PageReponseEntreeAudit page(int? page);

  PageReponseEntreeAudit size(int? size);

  PageReponseEntreeAudit totalElements(int? totalElements);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PageReponseEntreeAudit(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PageReponseEntreeAudit(...).copyWith(id: 12, name: "My name")
  /// ````
  PageReponseEntreeAudit call({
    List<EntreeAudit>? content,
    int? page,
    int? size,
    int? totalElements,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfPageReponseEntreeAudit.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfPageReponseEntreeAudit.copyWith.fieldName(...)`
class _$PageReponseEntreeAuditCWProxyImpl
    implements _$PageReponseEntreeAuditCWProxy {
  const _$PageReponseEntreeAuditCWProxyImpl(this._value);

  final PageReponseEntreeAudit _value;

  @override
  PageReponseEntreeAudit content(List<EntreeAudit>? content) =>
      this(content: content);

  @override
  PageReponseEntreeAudit page(int? page) => this(page: page);

  @override
  PageReponseEntreeAudit size(int? size) => this(size: size);

  @override
  PageReponseEntreeAudit totalElements(int? totalElements) =>
      this(totalElements: totalElements);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PageReponseEntreeAudit(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PageReponseEntreeAudit(...).copyWith(id: 12, name: "My name")
  /// ````
  PageReponseEntreeAudit call({
    Object? content = const $CopyWithPlaceholder(),
    Object? page = const $CopyWithPlaceholder(),
    Object? size = const $CopyWithPlaceholder(),
    Object? totalElements = const $CopyWithPlaceholder(),
  }) {
    return PageReponseEntreeAudit(
      content: content == const $CopyWithPlaceholder()
          ? _value.content
          // ignore: cast_nullable_to_non_nullable
          : content as List<EntreeAudit>?,
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

extension $PageReponseEntreeAuditCopyWith on PageReponseEntreeAudit {
  /// Returns a callable class that can be used as follows: `instanceOfPageReponseEntreeAudit.copyWith(...)` or like so:`instanceOfPageReponseEntreeAudit.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$PageReponseEntreeAuditCWProxy get copyWith =>
      _$PageReponseEntreeAuditCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

PageReponseEntreeAudit _$PageReponseEntreeAuditFromJson(
  Map<String, dynamic> json,
) => $checkedCreate('PageReponseEntreeAudit', json, ($checkedConvert) {
  final val = PageReponseEntreeAudit(
    content: $checkedConvert(
      'content',
      (v) => (v as List<dynamic>?)
          ?.map((e) => EntreeAudit.fromJson(e as Map<String, dynamic>))
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

Map<String, dynamic> _$PageReponseEntreeAuditToJson(
  PageReponseEntreeAudit instance,
) => <String, dynamic>{
  'content': ?instance.content?.map((e) => e.toJson()).toList(),
  'page': ?instance.page,
  'size': ?instance.size,
  'totalElements': ?instance.totalElements,
};
