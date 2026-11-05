// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'arrieres.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$ArrieresCWProxy {
  Arrieres total(int? total);

  Arrieres parBien(List<LigneArriere>? parBien);

  Arrieres parLocataire(List<LigneArriere>? parLocataire);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `Arrieres(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// Arrieres(...).copyWith(id: 12, name: "My name")
  /// ````
  Arrieres call({
    int? total,
    List<LigneArriere>? parBien,
    List<LigneArriere>? parLocataire,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfArrieres.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfArrieres.copyWith.fieldName(...)`
class _$ArrieresCWProxyImpl implements _$ArrieresCWProxy {
  const _$ArrieresCWProxyImpl(this._value);

  final Arrieres _value;

  @override
  Arrieres total(int? total) => this(total: total);

  @override
  Arrieres parBien(List<LigneArriere>? parBien) => this(parBien: parBien);

  @override
  Arrieres parLocataire(List<LigneArriere>? parLocataire) =>
      this(parLocataire: parLocataire);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `Arrieres(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// Arrieres(...).copyWith(id: 12, name: "My name")
  /// ````
  Arrieres call({
    Object? total = const $CopyWithPlaceholder(),
    Object? parBien = const $CopyWithPlaceholder(),
    Object? parLocataire = const $CopyWithPlaceholder(),
  }) {
    return Arrieres(
      total: total == const $CopyWithPlaceholder()
          ? _value.total
          // ignore: cast_nullable_to_non_nullable
          : total as int?,
      parBien: parBien == const $CopyWithPlaceholder()
          ? _value.parBien
          // ignore: cast_nullable_to_non_nullable
          : parBien as List<LigneArriere>?,
      parLocataire: parLocataire == const $CopyWithPlaceholder()
          ? _value.parLocataire
          // ignore: cast_nullable_to_non_nullable
          : parLocataire as List<LigneArriere>?,
    );
  }
}

extension $ArrieresCopyWith on Arrieres {
  /// Returns a callable class that can be used as follows: `instanceOfArrieres.copyWith(...)` or like so:`instanceOfArrieres.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$ArrieresCWProxy get copyWith => _$ArrieresCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

Arrieres _$ArrieresFromJson(Map<String, dynamic> json) =>
    $checkedCreate('Arrieres', json, ($checkedConvert) {
      final val = Arrieres(
        total: $checkedConvert('total', (v) => (v as num?)?.toInt()),
        parBien: $checkedConvert(
          'parBien',
          (v) => (v as List<dynamic>?)
              ?.map((e) => LigneArriere.fromJson(e as Map<String, dynamic>))
              .toList(),
        ),
        parLocataire: $checkedConvert(
          'parLocataire',
          (v) => (v as List<dynamic>?)
              ?.map((e) => LigneArriere.fromJson(e as Map<String, dynamic>))
              .toList(),
        ),
      );
      return val;
    });

Map<String, dynamic> _$ArrieresToJson(Arrieres instance) => <String, dynamic>{
  'total': ?instance.total,
  'parBien': ?instance.parBien?.map((e) => e.toJson()).toList(),
  'parLocataire': ?instance.parLocataire?.map((e) => e.toJson()).toList(),
};
