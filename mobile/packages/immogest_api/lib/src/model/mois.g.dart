// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'mois.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$MoisCWProxy {
  Mois mois(DateTime? mois);

  Mois attendu(int? attendu);

  Mois encaisse(int? encaisse);

  Mois tauxBp(int? tauxBp);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `Mois(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// Mois(...).copyWith(id: 12, name: "My name")
  /// ````
  Mois call({DateTime? mois, int? attendu, int? encaisse, int? tauxBp});
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfMois.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfMois.copyWith.fieldName(...)`
class _$MoisCWProxyImpl implements _$MoisCWProxy {
  const _$MoisCWProxyImpl(this._value);

  final Mois _value;

  @override
  Mois mois(DateTime? mois) => this(mois: mois);

  @override
  Mois attendu(int? attendu) => this(attendu: attendu);

  @override
  Mois encaisse(int? encaisse) => this(encaisse: encaisse);

  @override
  Mois tauxBp(int? tauxBp) => this(tauxBp: tauxBp);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `Mois(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// Mois(...).copyWith(id: 12, name: "My name")
  /// ````
  Mois call({
    Object? mois = const $CopyWithPlaceholder(),
    Object? attendu = const $CopyWithPlaceholder(),
    Object? encaisse = const $CopyWithPlaceholder(),
    Object? tauxBp = const $CopyWithPlaceholder(),
  }) {
    return Mois(
      mois: mois == const $CopyWithPlaceholder()
          ? _value.mois
          // ignore: cast_nullable_to_non_nullable
          : mois as DateTime?,
      attendu: attendu == const $CopyWithPlaceholder()
          ? _value.attendu
          // ignore: cast_nullable_to_non_nullable
          : attendu as int?,
      encaisse: encaisse == const $CopyWithPlaceholder()
          ? _value.encaisse
          // ignore: cast_nullable_to_non_nullable
          : encaisse as int?,
      tauxBp: tauxBp == const $CopyWithPlaceholder()
          ? _value.tauxBp
          // ignore: cast_nullable_to_non_nullable
          : tauxBp as int?,
    );
  }
}

extension $MoisCopyWith on Mois {
  /// Returns a callable class that can be used as follows: `instanceOfMois.copyWith(...)` or like so:`instanceOfMois.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$MoisCWProxy get copyWith => _$MoisCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

Mois _$MoisFromJson(Map<String, dynamic> json) =>
    $checkedCreate('Mois', json, ($checkedConvert) {
      final val = Mois(
        mois: $checkedConvert(
          'mois',
          (v) => v == null ? null : DateTime.parse(v as String),
        ),
        attendu: $checkedConvert('attendu', (v) => (v as num?)?.toInt()),
        encaisse: $checkedConvert('encaisse', (v) => (v as num?)?.toInt()),
        tauxBp: $checkedConvert('tauxBp', (v) => (v as num?)?.toInt()),
      );
      return val;
    });

Map<String, dynamic> _$MoisToJson(Mois instance) => <String, dynamic>{
  'mois': ?instance.mois?.toIso8601String(),
  'attendu': ?instance.attendu,
  'encaisse': ?instance.encaisse,
  'tauxBp': ?instance.tauxBp,
};
