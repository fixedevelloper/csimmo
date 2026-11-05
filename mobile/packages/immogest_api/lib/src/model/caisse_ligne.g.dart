// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'caisse_ligne.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$CaisseLigneCWProxy {
  CaisseLigne agentId(String? agentId);

  CaisseLigne agentNom(String? agentNom);

  CaisseLigne total(int? total);

  CaisseLigne operations(int? operations);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `CaisseLigne(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// CaisseLigne(...).copyWith(id: 12, name: "My name")
  /// ````
  CaisseLigne call({
    String? agentId,
    String? agentNom,
    int? total,
    int? operations,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfCaisseLigne.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfCaisseLigne.copyWith.fieldName(...)`
class _$CaisseLigneCWProxyImpl implements _$CaisseLigneCWProxy {
  const _$CaisseLigneCWProxyImpl(this._value);

  final CaisseLigne _value;

  @override
  CaisseLigne agentId(String? agentId) => this(agentId: agentId);

  @override
  CaisseLigne agentNom(String? agentNom) => this(agentNom: agentNom);

  @override
  CaisseLigne total(int? total) => this(total: total);

  @override
  CaisseLigne operations(int? operations) => this(operations: operations);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `CaisseLigne(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// CaisseLigne(...).copyWith(id: 12, name: "My name")
  /// ````
  CaisseLigne call({
    Object? agentId = const $CopyWithPlaceholder(),
    Object? agentNom = const $CopyWithPlaceholder(),
    Object? total = const $CopyWithPlaceholder(),
    Object? operations = const $CopyWithPlaceholder(),
  }) {
    return CaisseLigne(
      agentId: agentId == const $CopyWithPlaceholder()
          ? _value.agentId
          // ignore: cast_nullable_to_non_nullable
          : agentId as String?,
      agentNom: agentNom == const $CopyWithPlaceholder()
          ? _value.agentNom
          // ignore: cast_nullable_to_non_nullable
          : agentNom as String?,
      total: total == const $CopyWithPlaceholder()
          ? _value.total
          // ignore: cast_nullable_to_non_nullable
          : total as int?,
      operations: operations == const $CopyWithPlaceholder()
          ? _value.operations
          // ignore: cast_nullable_to_non_nullable
          : operations as int?,
    );
  }
}

extension $CaisseLigneCopyWith on CaisseLigne {
  /// Returns a callable class that can be used as follows: `instanceOfCaisseLigne.copyWith(...)` or like so:`instanceOfCaisseLigne.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$CaisseLigneCWProxy get copyWith => _$CaisseLigneCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

CaisseLigne _$CaisseLigneFromJson(Map<String, dynamic> json) =>
    $checkedCreate('CaisseLigne', json, ($checkedConvert) {
      final val = CaisseLigne(
        agentId: $checkedConvert('agentId', (v) => v as String?),
        agentNom: $checkedConvert('agentNom', (v) => v as String?),
        total: $checkedConvert('total', (v) => (v as num?)?.toInt()),
        operations: $checkedConvert('operations', (v) => (v as num?)?.toInt()),
      );
      return val;
    });

Map<String, dynamic> _$CaisseLigneToJson(CaisseLigne instance) =>
    <String, dynamic>{
      'agentId': ?instance.agentId,
      'agentNom': ?instance.agentNom,
      'total': ?instance.total,
      'operations': ?instance.operations,
    };
