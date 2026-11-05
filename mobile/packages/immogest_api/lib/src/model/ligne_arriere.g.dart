// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'ligne_arriere.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$LigneArriereCWProxy {
  LigneArriere id(String? id);

  LigneArriere libelle(String? libelle);

  LigneArriere total(int? total);

  LigneArriere echeances(int? echeances);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `LigneArriere(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// LigneArriere(...).copyWith(id: 12, name: "My name")
  /// ````
  LigneArriere call({String? id, String? libelle, int? total, int? echeances});
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfLigneArriere.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfLigneArriere.copyWith.fieldName(...)`
class _$LigneArriereCWProxyImpl implements _$LigneArriereCWProxy {
  const _$LigneArriereCWProxyImpl(this._value);

  final LigneArriere _value;

  @override
  LigneArriere id(String? id) => this(id: id);

  @override
  LigneArriere libelle(String? libelle) => this(libelle: libelle);

  @override
  LigneArriere total(int? total) => this(total: total);

  @override
  LigneArriere echeances(int? echeances) => this(echeances: echeances);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `LigneArriere(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// LigneArriere(...).copyWith(id: 12, name: "My name")
  /// ````
  LigneArriere call({
    Object? id = const $CopyWithPlaceholder(),
    Object? libelle = const $CopyWithPlaceholder(),
    Object? total = const $CopyWithPlaceholder(),
    Object? echeances = const $CopyWithPlaceholder(),
  }) {
    return LigneArriere(
      id: id == const $CopyWithPlaceholder()
          ? _value.id
          // ignore: cast_nullable_to_non_nullable
          : id as String?,
      libelle: libelle == const $CopyWithPlaceholder()
          ? _value.libelle
          // ignore: cast_nullable_to_non_nullable
          : libelle as String?,
      total: total == const $CopyWithPlaceholder()
          ? _value.total
          // ignore: cast_nullable_to_non_nullable
          : total as int?,
      echeances: echeances == const $CopyWithPlaceholder()
          ? _value.echeances
          // ignore: cast_nullable_to_non_nullable
          : echeances as int?,
    );
  }
}

extension $LigneArriereCopyWith on LigneArriere {
  /// Returns a callable class that can be used as follows: `instanceOfLigneArriere.copyWith(...)` or like so:`instanceOfLigneArriere.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$LigneArriereCWProxy get copyWith => _$LigneArriereCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

LigneArriere _$LigneArriereFromJson(Map<String, dynamic> json) =>
    $checkedCreate('LigneArriere', json, ($checkedConvert) {
      final val = LigneArriere(
        id: $checkedConvert('id', (v) => v as String?),
        libelle: $checkedConvert('libelle', (v) => v as String?),
        total: $checkedConvert('total', (v) => (v as num?)?.toInt()),
        echeances: $checkedConvert('echeances', (v) => (v as num?)?.toInt()),
      );
      return val;
    });

Map<String, dynamic> _$LigneArriereToJson(LigneArriere instance) =>
    <String, dynamic>{
      'id': ?instance.id,
      'libelle': ?instance.libelle,
      'total': ?instance.total,
      'echeances': ?instance.echeances,
    };
