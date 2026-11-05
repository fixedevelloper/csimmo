// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'echeance_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$EcheanceDtoCWProxy {
  EcheanceDto id(String? id);

  EcheanceDto bailId(String? bailId);

  EcheanceDto periodeDebut(DateTime? periodeDebut);

  EcheanceDto periodeFin(DateTime? periodeFin);

  EcheanceDto dateEcheance(DateTime? dateEcheance);

  EcheanceDto montant(int? montant);

  EcheanceDto montantPaye(int? montantPaye);

  EcheanceDto reste(int? reste);

  EcheanceDto statut(EcheanceDtoStatutEnum? statut);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `EcheanceDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// EcheanceDto(...).copyWith(id: 12, name: "My name")
  /// ````
  EcheanceDto call({
    String? id,
    String? bailId,
    DateTime? periodeDebut,
    DateTime? periodeFin,
    DateTime? dateEcheance,
    int? montant,
    int? montantPaye,
    int? reste,
    EcheanceDtoStatutEnum? statut,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfEcheanceDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfEcheanceDto.copyWith.fieldName(...)`
class _$EcheanceDtoCWProxyImpl implements _$EcheanceDtoCWProxy {
  const _$EcheanceDtoCWProxyImpl(this._value);

  final EcheanceDto _value;

  @override
  EcheanceDto id(String? id) => this(id: id);

  @override
  EcheanceDto bailId(String? bailId) => this(bailId: bailId);

  @override
  EcheanceDto periodeDebut(DateTime? periodeDebut) =>
      this(periodeDebut: periodeDebut);

  @override
  EcheanceDto periodeFin(DateTime? periodeFin) => this(periodeFin: periodeFin);

  @override
  EcheanceDto dateEcheance(DateTime? dateEcheance) =>
      this(dateEcheance: dateEcheance);

  @override
  EcheanceDto montant(int? montant) => this(montant: montant);

  @override
  EcheanceDto montantPaye(int? montantPaye) => this(montantPaye: montantPaye);

  @override
  EcheanceDto reste(int? reste) => this(reste: reste);

  @override
  EcheanceDto statut(EcheanceDtoStatutEnum? statut) => this(statut: statut);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `EcheanceDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// EcheanceDto(...).copyWith(id: 12, name: "My name")
  /// ````
  EcheanceDto call({
    Object? id = const $CopyWithPlaceholder(),
    Object? bailId = const $CopyWithPlaceholder(),
    Object? periodeDebut = const $CopyWithPlaceholder(),
    Object? periodeFin = const $CopyWithPlaceholder(),
    Object? dateEcheance = const $CopyWithPlaceholder(),
    Object? montant = const $CopyWithPlaceholder(),
    Object? montantPaye = const $CopyWithPlaceholder(),
    Object? reste = const $CopyWithPlaceholder(),
    Object? statut = const $CopyWithPlaceholder(),
  }) {
    return EcheanceDto(
      id: id == const $CopyWithPlaceholder()
          ? _value.id
          // ignore: cast_nullable_to_non_nullable
          : id as String?,
      bailId: bailId == const $CopyWithPlaceholder()
          ? _value.bailId
          // ignore: cast_nullable_to_non_nullable
          : bailId as String?,
      periodeDebut: periodeDebut == const $CopyWithPlaceholder()
          ? _value.periodeDebut
          // ignore: cast_nullable_to_non_nullable
          : periodeDebut as DateTime?,
      periodeFin: periodeFin == const $CopyWithPlaceholder()
          ? _value.periodeFin
          // ignore: cast_nullable_to_non_nullable
          : periodeFin as DateTime?,
      dateEcheance: dateEcheance == const $CopyWithPlaceholder()
          ? _value.dateEcheance
          // ignore: cast_nullable_to_non_nullable
          : dateEcheance as DateTime?,
      montant: montant == const $CopyWithPlaceholder()
          ? _value.montant
          // ignore: cast_nullable_to_non_nullable
          : montant as int?,
      montantPaye: montantPaye == const $CopyWithPlaceholder()
          ? _value.montantPaye
          // ignore: cast_nullable_to_non_nullable
          : montantPaye as int?,
      reste: reste == const $CopyWithPlaceholder()
          ? _value.reste
          // ignore: cast_nullable_to_non_nullable
          : reste as int?,
      statut: statut == const $CopyWithPlaceholder()
          ? _value.statut
          // ignore: cast_nullable_to_non_nullable
          : statut as EcheanceDtoStatutEnum?,
    );
  }
}

extension $EcheanceDtoCopyWith on EcheanceDto {
  /// Returns a callable class that can be used as follows: `instanceOfEcheanceDto.copyWith(...)` or like so:`instanceOfEcheanceDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$EcheanceDtoCWProxy get copyWith => _$EcheanceDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

EcheanceDto _$EcheanceDtoFromJson(Map<String, dynamic> json) => $checkedCreate(
  'EcheanceDto',
  json,
  ($checkedConvert) {
    final val = EcheanceDto(
      id: $checkedConvert('id', (v) => v as String?),
      bailId: $checkedConvert('bailId', (v) => v as String?),
      periodeDebut: $checkedConvert(
        'periodeDebut',
        (v) => v == null ? null : DateTime.parse(v as String),
      ),
      periodeFin: $checkedConvert(
        'periodeFin',
        (v) => v == null ? null : DateTime.parse(v as String),
      ),
      dateEcheance: $checkedConvert(
        'dateEcheance',
        (v) => v == null ? null : DateTime.parse(v as String),
      ),
      montant: $checkedConvert('montant', (v) => (v as num?)?.toInt()),
      montantPaye: $checkedConvert('montantPaye', (v) => (v as num?)?.toInt()),
      reste: $checkedConvert('reste', (v) => (v as num?)?.toInt()),
      statut: $checkedConvert(
        'statut',
        (v) => $enumDecodeNullable(_$EcheanceDtoStatutEnumEnumMap, v),
      ),
    );
    return val;
  },
);

Map<String, dynamic> _$EcheanceDtoToJson(EcheanceDto instance) =>
    <String, dynamic>{
      'id': ?instance.id,
      'bailId': ?instance.bailId,
      'periodeDebut': ?instance.periodeDebut?.toIso8601String(),
      'periodeFin': ?instance.periodeFin?.toIso8601String(),
      'dateEcheance': ?instance.dateEcheance?.toIso8601String(),
      'montant': ?instance.montant,
      'montantPaye': ?instance.montantPaye,
      'reste': ?instance.reste,
      'statut': ?_$EcheanceDtoStatutEnumEnumMap[instance.statut],
    };

const _$EcheanceDtoStatutEnumEnumMap = {
  EcheanceDtoStatutEnum.A_VENIR: 'A_VENIR',
  EcheanceDtoStatutEnum.DUE: 'DUE',
  EcheanceDtoStatutEnum.PARTIELLE: 'PARTIELLE',
  EcheanceDtoStatutEnum.PAYEE: 'PAYEE',
  EcheanceDtoStatutEnum.EN_RETARD: 'EN_RETARD',
  EcheanceDtoStatutEnum.ANNULEE: 'ANNULEE',
};
