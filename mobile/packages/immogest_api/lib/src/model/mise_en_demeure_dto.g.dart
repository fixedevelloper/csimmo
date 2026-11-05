// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'mise_en_demeure_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$MiseEnDemeureDtoCWProxy {
  MiseEnDemeureDto id(String? id);

  MiseEnDemeureDto numero(String? numero);

  MiseEnDemeureDto bailId(String? bailId);

  MiseEnDemeureDto bailNumero(String? bailNumero);

  MiseEnDemeureDto echeanceId(String? echeanceId);

  MiseEnDemeureDto locataireNom(String? locataireNom);

  MiseEnDemeureDto montantArrieres(int? montantArrieres);

  MiseEnDemeureDto delaiJours(int? delaiJours);

  MiseEnDemeureDto dateEmission(DateTime? dateEmission);

  MiseEnDemeureDto fichierId(String? fichierId);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `MiseEnDemeureDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// MiseEnDemeureDto(...).copyWith(id: 12, name: "My name")
  /// ````
  MiseEnDemeureDto call({
    String? id,
    String? numero,
    String? bailId,
    String? bailNumero,
    String? echeanceId,
    String? locataireNom,
    int? montantArrieres,
    int? delaiJours,
    DateTime? dateEmission,
    String? fichierId,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfMiseEnDemeureDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfMiseEnDemeureDto.copyWith.fieldName(...)`
class _$MiseEnDemeureDtoCWProxyImpl implements _$MiseEnDemeureDtoCWProxy {
  const _$MiseEnDemeureDtoCWProxyImpl(this._value);

  final MiseEnDemeureDto _value;

  @override
  MiseEnDemeureDto id(String? id) => this(id: id);

  @override
  MiseEnDemeureDto numero(String? numero) => this(numero: numero);

  @override
  MiseEnDemeureDto bailId(String? bailId) => this(bailId: bailId);

  @override
  MiseEnDemeureDto bailNumero(String? bailNumero) =>
      this(bailNumero: bailNumero);

  @override
  MiseEnDemeureDto echeanceId(String? echeanceId) =>
      this(echeanceId: echeanceId);

  @override
  MiseEnDemeureDto locataireNom(String? locataireNom) =>
      this(locataireNom: locataireNom);

  @override
  MiseEnDemeureDto montantArrieres(int? montantArrieres) =>
      this(montantArrieres: montantArrieres);

  @override
  MiseEnDemeureDto delaiJours(int? delaiJours) => this(delaiJours: delaiJours);

  @override
  MiseEnDemeureDto dateEmission(DateTime? dateEmission) =>
      this(dateEmission: dateEmission);

  @override
  MiseEnDemeureDto fichierId(String? fichierId) => this(fichierId: fichierId);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `MiseEnDemeureDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// MiseEnDemeureDto(...).copyWith(id: 12, name: "My name")
  /// ````
  MiseEnDemeureDto call({
    Object? id = const $CopyWithPlaceholder(),
    Object? numero = const $CopyWithPlaceholder(),
    Object? bailId = const $CopyWithPlaceholder(),
    Object? bailNumero = const $CopyWithPlaceholder(),
    Object? echeanceId = const $CopyWithPlaceholder(),
    Object? locataireNom = const $CopyWithPlaceholder(),
    Object? montantArrieres = const $CopyWithPlaceholder(),
    Object? delaiJours = const $CopyWithPlaceholder(),
    Object? dateEmission = const $CopyWithPlaceholder(),
    Object? fichierId = const $CopyWithPlaceholder(),
  }) {
    return MiseEnDemeureDto(
      id: id == const $CopyWithPlaceholder()
          ? _value.id
          // ignore: cast_nullable_to_non_nullable
          : id as String?,
      numero: numero == const $CopyWithPlaceholder()
          ? _value.numero
          // ignore: cast_nullable_to_non_nullable
          : numero as String?,
      bailId: bailId == const $CopyWithPlaceholder()
          ? _value.bailId
          // ignore: cast_nullable_to_non_nullable
          : bailId as String?,
      bailNumero: bailNumero == const $CopyWithPlaceholder()
          ? _value.bailNumero
          // ignore: cast_nullable_to_non_nullable
          : bailNumero as String?,
      echeanceId: echeanceId == const $CopyWithPlaceholder()
          ? _value.echeanceId
          // ignore: cast_nullable_to_non_nullable
          : echeanceId as String?,
      locataireNom: locataireNom == const $CopyWithPlaceholder()
          ? _value.locataireNom
          // ignore: cast_nullable_to_non_nullable
          : locataireNom as String?,
      montantArrieres: montantArrieres == const $CopyWithPlaceholder()
          ? _value.montantArrieres
          // ignore: cast_nullable_to_non_nullable
          : montantArrieres as int?,
      delaiJours: delaiJours == const $CopyWithPlaceholder()
          ? _value.delaiJours
          // ignore: cast_nullable_to_non_nullable
          : delaiJours as int?,
      dateEmission: dateEmission == const $CopyWithPlaceholder()
          ? _value.dateEmission
          // ignore: cast_nullable_to_non_nullable
          : dateEmission as DateTime?,
      fichierId: fichierId == const $CopyWithPlaceholder()
          ? _value.fichierId
          // ignore: cast_nullable_to_non_nullable
          : fichierId as String?,
    );
  }
}

extension $MiseEnDemeureDtoCopyWith on MiseEnDemeureDto {
  /// Returns a callable class that can be used as follows: `instanceOfMiseEnDemeureDto.copyWith(...)` or like so:`instanceOfMiseEnDemeureDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$MiseEnDemeureDtoCWProxy get copyWith => _$MiseEnDemeureDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

MiseEnDemeureDto _$MiseEnDemeureDtoFromJson(Map<String, dynamic> json) =>
    $checkedCreate('MiseEnDemeureDto', json, ($checkedConvert) {
      final val = MiseEnDemeureDto(
        id: $checkedConvert('id', (v) => v as String?),
        numero: $checkedConvert('numero', (v) => v as String?),
        bailId: $checkedConvert('bailId', (v) => v as String?),
        bailNumero: $checkedConvert('bailNumero', (v) => v as String?),
        echeanceId: $checkedConvert('echeanceId', (v) => v as String?),
        locataireNom: $checkedConvert('locataireNom', (v) => v as String?),
        montantArrieres: $checkedConvert(
          'montantArrieres',
          (v) => (v as num?)?.toInt(),
        ),
        delaiJours: $checkedConvert('delaiJours', (v) => (v as num?)?.toInt()),
        dateEmission: $checkedConvert(
          'dateEmission',
          (v) => v == null ? null : DateTime.parse(v as String),
        ),
        fichierId: $checkedConvert('fichierId', (v) => v as String?),
      );
      return val;
    });

Map<String, dynamic> _$MiseEnDemeureDtoToJson(MiseEnDemeureDto instance) =>
    <String, dynamic>{
      'id': ?instance.id,
      'numero': ?instance.numero,
      'bailId': ?instance.bailId,
      'bailNumero': ?instance.bailNumero,
      'echeanceId': ?instance.echeanceId,
      'locataireNom': ?instance.locataireNom,
      'montantArrieres': ?instance.montantArrieres,
      'delaiJours': ?instance.delaiJours,
      'dateEmission': ?instance.dateEmission?.toIso8601String(),
      'fichierId': ?instance.fichierId,
    };
