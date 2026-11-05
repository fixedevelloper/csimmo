// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'renouvellement_requete.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$RenouvellementRequeteCWProxy {
  RenouvellementRequete dateFin(DateTime? dateFin);

  RenouvellementRequete loyer(int? loyer);

  RenouvellementRequete charges(int? charges);

  RenouvellementRequete caution(int? caution);

  RenouvellementRequete moisAvance(int? moisAvance);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `RenouvellementRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// RenouvellementRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  RenouvellementRequete call({
    DateTime? dateFin,
    int? loyer,
    int? charges,
    int? caution,
    int? moisAvance,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfRenouvellementRequete.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfRenouvellementRequete.copyWith.fieldName(...)`
class _$RenouvellementRequeteCWProxyImpl
    implements _$RenouvellementRequeteCWProxy {
  const _$RenouvellementRequeteCWProxyImpl(this._value);

  final RenouvellementRequete _value;

  @override
  RenouvellementRequete dateFin(DateTime? dateFin) => this(dateFin: dateFin);

  @override
  RenouvellementRequete loyer(int? loyer) => this(loyer: loyer);

  @override
  RenouvellementRequete charges(int? charges) => this(charges: charges);

  @override
  RenouvellementRequete caution(int? caution) => this(caution: caution);

  @override
  RenouvellementRequete moisAvance(int? moisAvance) =>
      this(moisAvance: moisAvance);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `RenouvellementRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// RenouvellementRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  RenouvellementRequete call({
    Object? dateFin = const $CopyWithPlaceholder(),
    Object? loyer = const $CopyWithPlaceholder(),
    Object? charges = const $CopyWithPlaceholder(),
    Object? caution = const $CopyWithPlaceholder(),
    Object? moisAvance = const $CopyWithPlaceholder(),
  }) {
    return RenouvellementRequete(
      dateFin: dateFin == const $CopyWithPlaceholder()
          ? _value.dateFin
          // ignore: cast_nullable_to_non_nullable
          : dateFin as DateTime?,
      loyer: loyer == const $CopyWithPlaceholder()
          ? _value.loyer
          // ignore: cast_nullable_to_non_nullable
          : loyer as int?,
      charges: charges == const $CopyWithPlaceholder()
          ? _value.charges
          // ignore: cast_nullable_to_non_nullable
          : charges as int?,
      caution: caution == const $CopyWithPlaceholder()
          ? _value.caution
          // ignore: cast_nullable_to_non_nullable
          : caution as int?,
      moisAvance: moisAvance == const $CopyWithPlaceholder()
          ? _value.moisAvance
          // ignore: cast_nullable_to_non_nullable
          : moisAvance as int?,
    );
  }
}

extension $RenouvellementRequeteCopyWith on RenouvellementRequete {
  /// Returns a callable class that can be used as follows: `instanceOfRenouvellementRequete.copyWith(...)` or like so:`instanceOfRenouvellementRequete.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$RenouvellementRequeteCWProxy get copyWith =>
      _$RenouvellementRequeteCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

RenouvellementRequete _$RenouvellementRequeteFromJson(
  Map<String, dynamic> json,
) => $checkedCreate('RenouvellementRequete', json, ($checkedConvert) {
  final val = RenouvellementRequete(
    dateFin: $checkedConvert(
      'dateFin',
      (v) => v == null ? null : DateTime.parse(v as String),
    ),
    loyer: $checkedConvert('loyer', (v) => (v as num?)?.toInt()),
    charges: $checkedConvert('charges', (v) => (v as num?)?.toInt()),
    caution: $checkedConvert('caution', (v) => (v as num?)?.toInt()),
    moisAvance: $checkedConvert('moisAvance', (v) => (v as num?)?.toInt()),
  );
  return val;
});

Map<String, dynamic> _$RenouvellementRequeteToJson(
  RenouvellementRequete instance,
) => <String, dynamic>{
  'dateFin': ?instance.dateFin?.toIso8601String(),
  'loyer': ?instance.loyer,
  'charges': ?instance.charges,
  'caution': ?instance.caution,
  'moisAvance': ?instance.moisAvance,
};
