// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'synthese.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$SyntheseCWProxy {
  Synthese occupation(Occupation? occupation);

  Synthese attenduMois(int? attenduMois);

  Synthese encaisseMois(int? encaisseMois);

  Synthese tauxRecouvrementMoisBp(int? tauxRecouvrementMoisBp);

  Synthese arrieresTotal(int? arrieresTotal);

  Synthese echeancesEnRetard(int? echeancesEnRetard);

  Synthese bauxAEcheance(int? bauxAEcheance);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `Synthese(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// Synthese(...).copyWith(id: 12, name: "My name")
  /// ````
  Synthese call({
    Occupation? occupation,
    int? attenduMois,
    int? encaisseMois,
    int? tauxRecouvrementMoisBp,
    int? arrieresTotal,
    int? echeancesEnRetard,
    int? bauxAEcheance,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfSynthese.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfSynthese.copyWith.fieldName(...)`
class _$SyntheseCWProxyImpl implements _$SyntheseCWProxy {
  const _$SyntheseCWProxyImpl(this._value);

  final Synthese _value;

  @override
  Synthese occupation(Occupation? occupation) => this(occupation: occupation);

  @override
  Synthese attenduMois(int? attenduMois) => this(attenduMois: attenduMois);

  @override
  Synthese encaisseMois(int? encaisseMois) => this(encaisseMois: encaisseMois);

  @override
  Synthese tauxRecouvrementMoisBp(int? tauxRecouvrementMoisBp) =>
      this(tauxRecouvrementMoisBp: tauxRecouvrementMoisBp);

  @override
  Synthese arrieresTotal(int? arrieresTotal) =>
      this(arrieresTotal: arrieresTotal);

  @override
  Synthese echeancesEnRetard(int? echeancesEnRetard) =>
      this(echeancesEnRetard: echeancesEnRetard);

  @override
  Synthese bauxAEcheance(int? bauxAEcheance) =>
      this(bauxAEcheance: bauxAEcheance);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `Synthese(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// Synthese(...).copyWith(id: 12, name: "My name")
  /// ````
  Synthese call({
    Object? occupation = const $CopyWithPlaceholder(),
    Object? attenduMois = const $CopyWithPlaceholder(),
    Object? encaisseMois = const $CopyWithPlaceholder(),
    Object? tauxRecouvrementMoisBp = const $CopyWithPlaceholder(),
    Object? arrieresTotal = const $CopyWithPlaceholder(),
    Object? echeancesEnRetard = const $CopyWithPlaceholder(),
    Object? bauxAEcheance = const $CopyWithPlaceholder(),
  }) {
    return Synthese(
      occupation: occupation == const $CopyWithPlaceholder()
          ? _value.occupation
          // ignore: cast_nullable_to_non_nullable
          : occupation as Occupation?,
      attenduMois: attenduMois == const $CopyWithPlaceholder()
          ? _value.attenduMois
          // ignore: cast_nullable_to_non_nullable
          : attenduMois as int?,
      encaisseMois: encaisseMois == const $CopyWithPlaceholder()
          ? _value.encaisseMois
          // ignore: cast_nullable_to_non_nullable
          : encaisseMois as int?,
      tauxRecouvrementMoisBp:
          tauxRecouvrementMoisBp == const $CopyWithPlaceholder()
          ? _value.tauxRecouvrementMoisBp
          // ignore: cast_nullable_to_non_nullable
          : tauxRecouvrementMoisBp as int?,
      arrieresTotal: arrieresTotal == const $CopyWithPlaceholder()
          ? _value.arrieresTotal
          // ignore: cast_nullable_to_non_nullable
          : arrieresTotal as int?,
      echeancesEnRetard: echeancesEnRetard == const $CopyWithPlaceholder()
          ? _value.echeancesEnRetard
          // ignore: cast_nullable_to_non_nullable
          : echeancesEnRetard as int?,
      bauxAEcheance: bauxAEcheance == const $CopyWithPlaceholder()
          ? _value.bauxAEcheance
          // ignore: cast_nullable_to_non_nullable
          : bauxAEcheance as int?,
    );
  }
}

extension $SyntheseCopyWith on Synthese {
  /// Returns a callable class that can be used as follows: `instanceOfSynthese.copyWith(...)` or like so:`instanceOfSynthese.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$SyntheseCWProxy get copyWith => _$SyntheseCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

Synthese _$SyntheseFromJson(
  Map<String, dynamic> json,
) => $checkedCreate('Synthese', json, ($checkedConvert) {
  final val = Synthese(
    occupation: $checkedConvert(
      'occupation',
      (v) => v == null ? null : Occupation.fromJson(v as Map<String, dynamic>),
    ),
    attenduMois: $checkedConvert('attenduMois', (v) => (v as num?)?.toInt()),
    encaisseMois: $checkedConvert('encaisseMois', (v) => (v as num?)?.toInt()),
    tauxRecouvrementMoisBp: $checkedConvert(
      'tauxRecouvrementMoisBp',
      (v) => (v as num?)?.toInt(),
    ),
    arrieresTotal: $checkedConvert(
      'arrieresTotal',
      (v) => (v as num?)?.toInt(),
    ),
    echeancesEnRetard: $checkedConvert(
      'echeancesEnRetard',
      (v) => (v as num?)?.toInt(),
    ),
    bauxAEcheance: $checkedConvert(
      'bauxAEcheance',
      (v) => (v as num?)?.toInt(),
    ),
  );
  return val;
});

Map<String, dynamic> _$SyntheseToJson(Synthese instance) => <String, dynamic>{
  'occupation': ?instance.occupation?.toJson(),
  'attenduMois': ?instance.attenduMois,
  'encaisseMois': ?instance.encaisseMois,
  'tauxRecouvrementMoisBp': ?instance.tauxRecouvrementMoisBp,
  'arrieresTotal': ?instance.arrieresTotal,
  'echeancesEnRetard': ?instance.echeancesEnRetard,
  'bauxAEcheance': ?instance.bauxAEcheance,
};
