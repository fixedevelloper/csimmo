// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'mandat_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$MandatDtoCWProxy {
  MandatDto id(String? id);

  MandatDto proprietaireId(String? proprietaireId);

  MandatDto tauxHonorairesBp(int? tauxHonorairesBp);

  MandatDto dateDebut(DateTime? dateDebut);

  MandatDto dateFin(DateTime? dateFin);

  MandatDto notes(String? notes);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `MandatDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// MandatDto(...).copyWith(id: 12, name: "My name")
  /// ````
  MandatDto call({
    String? id,
    String? proprietaireId,
    int? tauxHonorairesBp,
    DateTime? dateDebut,
    DateTime? dateFin,
    String? notes,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfMandatDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfMandatDto.copyWith.fieldName(...)`
class _$MandatDtoCWProxyImpl implements _$MandatDtoCWProxy {
  const _$MandatDtoCWProxyImpl(this._value);

  final MandatDto _value;

  @override
  MandatDto id(String? id) => this(id: id);

  @override
  MandatDto proprietaireId(String? proprietaireId) =>
      this(proprietaireId: proprietaireId);

  @override
  MandatDto tauxHonorairesBp(int? tauxHonorairesBp) =>
      this(tauxHonorairesBp: tauxHonorairesBp);

  @override
  MandatDto dateDebut(DateTime? dateDebut) => this(dateDebut: dateDebut);

  @override
  MandatDto dateFin(DateTime? dateFin) => this(dateFin: dateFin);

  @override
  MandatDto notes(String? notes) => this(notes: notes);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `MandatDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// MandatDto(...).copyWith(id: 12, name: "My name")
  /// ````
  MandatDto call({
    Object? id = const $CopyWithPlaceholder(),
    Object? proprietaireId = const $CopyWithPlaceholder(),
    Object? tauxHonorairesBp = const $CopyWithPlaceholder(),
    Object? dateDebut = const $CopyWithPlaceholder(),
    Object? dateFin = const $CopyWithPlaceholder(),
    Object? notes = const $CopyWithPlaceholder(),
  }) {
    return MandatDto(
      id: id == const $CopyWithPlaceholder()
          ? _value.id
          // ignore: cast_nullable_to_non_nullable
          : id as String?,
      proprietaireId: proprietaireId == const $CopyWithPlaceholder()
          ? _value.proprietaireId
          // ignore: cast_nullable_to_non_nullable
          : proprietaireId as String?,
      tauxHonorairesBp: tauxHonorairesBp == const $CopyWithPlaceholder()
          ? _value.tauxHonorairesBp
          // ignore: cast_nullable_to_non_nullable
          : tauxHonorairesBp as int?,
      dateDebut: dateDebut == const $CopyWithPlaceholder()
          ? _value.dateDebut
          // ignore: cast_nullable_to_non_nullable
          : dateDebut as DateTime?,
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

extension $MandatDtoCopyWith on MandatDto {
  /// Returns a callable class that can be used as follows: `instanceOfMandatDto.copyWith(...)` or like so:`instanceOfMandatDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$MandatDtoCWProxy get copyWith => _$MandatDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

MandatDto _$MandatDtoFromJson(Map<String, dynamic> json) =>
    $checkedCreate('MandatDto', json, ($checkedConvert) {
      final val = MandatDto(
        id: $checkedConvert('id', (v) => v as String?),
        proprietaireId: $checkedConvert('proprietaireId', (v) => v as String?),
        tauxHonorairesBp: $checkedConvert(
          'tauxHonorairesBp',
          (v) => (v as num?)?.toInt(),
        ),
        dateDebut: $checkedConvert(
          'dateDebut',
          (v) => v == null ? null : DateTime.parse(v as String),
        ),
        dateFin: $checkedConvert(
          'dateFin',
          (v) => v == null ? null : DateTime.parse(v as String),
        ),
        notes: $checkedConvert('notes', (v) => v as String?),
      );
      return val;
    });

Map<String, dynamic> _$MandatDtoToJson(MandatDto instance) => <String, dynamic>{
  'id': ?instance.id,
  'proprietaireId': ?instance.proprietaireId,
  'tauxHonorairesBp': ?instance.tauxHonorairesBp,
  'dateDebut': ?instance.dateDebut?.toIso8601String(),
  'dateFin': ?instance.dateFin?.toIso8601String(),
  'notes': ?instance.notes,
};
