// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'mandat_requete.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$MandatRequeteCWProxy {
  MandatRequete tauxHonorairesBp(int tauxHonorairesBp);

  MandatRequete dateDebut(DateTime dateDebut);

  MandatRequete dateFin(DateTime? dateFin);

  MandatRequete notes(String? notes);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `MandatRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// MandatRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  MandatRequete call({
    int tauxHonorairesBp,
    DateTime dateDebut,
    DateTime? dateFin,
    String? notes,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfMandatRequete.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfMandatRequete.copyWith.fieldName(...)`
class _$MandatRequeteCWProxyImpl implements _$MandatRequeteCWProxy {
  const _$MandatRequeteCWProxyImpl(this._value);

  final MandatRequete _value;

  @override
  MandatRequete tauxHonorairesBp(int tauxHonorairesBp) =>
      this(tauxHonorairesBp: tauxHonorairesBp);

  @override
  MandatRequete dateDebut(DateTime dateDebut) => this(dateDebut: dateDebut);

  @override
  MandatRequete dateFin(DateTime? dateFin) => this(dateFin: dateFin);

  @override
  MandatRequete notes(String? notes) => this(notes: notes);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `MandatRequete(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// MandatRequete(...).copyWith(id: 12, name: "My name")
  /// ````
  MandatRequete call({
    Object? tauxHonorairesBp = const $CopyWithPlaceholder(),
    Object? dateDebut = const $CopyWithPlaceholder(),
    Object? dateFin = const $CopyWithPlaceholder(),
    Object? notes = const $CopyWithPlaceholder(),
  }) {
    return MandatRequete(
      tauxHonorairesBp: tauxHonorairesBp == const $CopyWithPlaceholder()
          ? _value.tauxHonorairesBp
          // ignore: cast_nullable_to_non_nullable
          : tauxHonorairesBp as int,
      dateDebut: dateDebut == const $CopyWithPlaceholder()
          ? _value.dateDebut
          // ignore: cast_nullable_to_non_nullable
          : dateDebut as DateTime,
      dateFin: dateFin == const $CopyWithPlaceholder()
          ? _value.dateFin
          // ignore: cast_nullable_to_non_nullable
          : dateFin as DateTime?,
      notes: notes == const $CopyWithPlaceholder()
          ? _value.notes
          // ignore: cast_nullable_to_non_nullable
          : notes as String?,
    );
  }
}

extension $MandatRequeteCopyWith on MandatRequete {
  /// Returns a callable class that can be used as follows: `instanceOfMandatRequete.copyWith(...)` or like so:`instanceOfMandatRequete.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$MandatRequeteCWProxy get copyWith => _$MandatRequeteCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

MandatRequete _$MandatRequeteFromJson(Map<String, dynamic> json) =>
    $checkedCreate('MandatRequete', json, ($checkedConvert) {
      $checkKeys(json, requiredKeys: const ['tauxHonorairesBp', 'dateDebut']);
      final val = MandatRequete(
        tauxHonorairesBp: $checkedConvert(
          'tauxHonorairesBp',
          (v) => (v as num).toInt(),
        ),
        dateDebut: $checkedConvert(
          'dateDebut',
          (v) => DateTime.parse(v as String),
        ),
        dateFin: $checkedConvert(
          'dateFin',
          (v) => v == null ? null : DateTime.parse(v as String),
        ),
        notes: $checkedConvert('notes', (v) => v as String?),
      );
      return val;
    });

Map<String, dynamic> _$MandatRequeteToJson(MandatRequete instance) =>
    <String, dynamic>{
      'tauxHonorairesBp': instance.tauxHonorairesBp,
      'dateDebut': instance.dateDebut.toIso8601String(),
      'dateFin': ?instance.dateFin?.toIso8601String(),
      'notes': ?instance.notes,
    };
