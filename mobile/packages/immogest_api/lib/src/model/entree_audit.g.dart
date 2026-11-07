// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'entree_audit.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$EntreeAuditCWProxy {
  EntreeAudit id(String? id);

  EntreeAudit date(DateTime? date);

  EntreeAudit action(String? action);

  EntreeAudit entite(String? entite);

  EntreeAudit entiteId(String? entiteId);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `EntreeAudit(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// EntreeAudit(...).copyWith(id: 12, name: "My name")
  /// ````
  EntreeAudit call({
    String? id,
    DateTime? date,
    String? action,
    String? entite,
    String? entiteId,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfEntreeAudit.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfEntreeAudit.copyWith.fieldName(...)`
class _$EntreeAuditCWProxyImpl implements _$EntreeAuditCWProxy {
  const _$EntreeAuditCWProxyImpl(this._value);

  final EntreeAudit _value;

  @override
  EntreeAudit id(String? id) => this(id: id);

  @override
  EntreeAudit date(DateTime? date) => this(date: date);

  @override
  EntreeAudit action(String? action) => this(action: action);

  @override
  EntreeAudit entite(String? entite) => this(entite: entite);

  @override
  EntreeAudit entiteId(String? entiteId) => this(entiteId: entiteId);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `EntreeAudit(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// EntreeAudit(...).copyWith(id: 12, name: "My name")
  /// ````
  EntreeAudit call({
    Object? id = const $CopyWithPlaceholder(),
    Object? date = const $CopyWithPlaceholder(),
    Object? action = const $CopyWithPlaceholder(),
    Object? entite = const $CopyWithPlaceholder(),
    Object? entiteId = const $CopyWithPlaceholder(),
  }) {
    return EntreeAudit(
      id: id == const $CopyWithPlaceholder()
          ? _value.id
          // ignore: cast_nullable_to_non_nullable
          : id as String?,
      date: date == const $CopyWithPlaceholder()
          ? _value.date
          // ignore: cast_nullable_to_non_nullable
          : date as DateTime?,
      action: action == const $CopyWithPlaceholder()
          ? _value.action
          // ignore: cast_nullable_to_non_nullable
          : action as String?,
      entite: entite == const $CopyWithPlaceholder()
          ? _value.entite
          // ignore: cast_nullable_to_non_nullable
          : entite as String?,
      entiteId: entiteId == const $CopyWithPlaceholder()
          ? _value.entiteId
          // ignore: cast_nullable_to_non_nullable
          : entiteId as String?,
    );
  }
}

extension $EntreeAuditCopyWith on EntreeAudit {
  /// Returns a callable class that can be used as follows: `instanceOfEntreeAudit.copyWith(...)` or like so:`instanceOfEntreeAudit.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$EntreeAuditCWProxy get copyWith => _$EntreeAuditCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

EntreeAudit _$EntreeAuditFromJson(Map<String, dynamic> json) =>
    $checkedCreate('EntreeAudit', json, ($checkedConvert) {
      final val = EntreeAudit(
        id: $checkedConvert('id', (v) => v as String?),
        date: $checkedConvert(
          'date',
          (v) => v == null ? null : DateTime.parse(v as String),
        ),
        action: $checkedConvert('action', (v) => v as String?),
        entite: $checkedConvert('entite', (v) => v as String?),
        entiteId: $checkedConvert('entiteId', (v) => v as String?),
      );
      return val;
    });

Map<String, dynamic> _$EntreeAuditToJson(EntreeAudit instance) =>
    <String, dynamic>{
      'id': ?instance.id,
      'date': ?instance.date?.toIso8601String(),
      'action': ?instance.action,
      'entite': ?instance.entite,
      'entiteId': ?instance.entiteId,
    };
