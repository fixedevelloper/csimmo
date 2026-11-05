// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'paiement_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$PaiementDtoCWProxy {
  PaiementDto id(String? id);

  PaiementDto bailId(String? bailId);

  PaiementDto bailNumero(String? bailNumero);

  PaiementDto locataireId(String? locataireId);

  PaiementDto locataireNom(String? locataireNom);

  PaiementDto type(String? type);

  PaiementDto mode(PaiementDtoModeEnum? mode);

  PaiementDto montant(int? montant);

  PaiementDto datePaiement(DateTime? datePaiement);

  PaiementDto reference(String? reference);

  PaiementDto saisiPar(String? saisiPar);

  PaiementDto saisiParNom(String? saisiParNom);

  PaiementDto motif(String? motif);

  PaiementDto contrePasse(bool? contrePasse);

  PaiementDto contrePassationDe(String? contrePassationDe);

  PaiementDto creeLe(DateTime? creeLe);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PaiementDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PaiementDto(...).copyWith(id: 12, name: "My name")
  /// ````
  PaiementDto call({
    String? id,
    String? bailId,
    String? bailNumero,
    String? locataireId,
    String? locataireNom,
    String? type,
    PaiementDtoModeEnum? mode,
    int? montant,
    DateTime? datePaiement,
    String? reference,
    String? saisiPar,
    String? saisiParNom,
    String? motif,
    bool? contrePasse,
    String? contrePassationDe,
    DateTime? creeLe,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfPaiementDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfPaiementDto.copyWith.fieldName(...)`
class _$PaiementDtoCWProxyImpl implements _$PaiementDtoCWProxy {
  const _$PaiementDtoCWProxyImpl(this._value);

  final PaiementDto _value;

  @override
  PaiementDto id(String? id) => this(id: id);

  @override
  PaiementDto bailId(String? bailId) => this(bailId: bailId);

  @override
  PaiementDto bailNumero(String? bailNumero) => this(bailNumero: bailNumero);

  @override
  PaiementDto locataireId(String? locataireId) =>
      this(locataireId: locataireId);

  @override
  PaiementDto locataireNom(String? locataireNom) =>
      this(locataireNom: locataireNom);

  @override
  PaiementDto type(String? type) => this(type: type);

  @override
  PaiementDto mode(PaiementDtoModeEnum? mode) => this(mode: mode);

  @override
  PaiementDto montant(int? montant) => this(montant: montant);

  @override
  PaiementDto datePaiement(DateTime? datePaiement) =>
      this(datePaiement: datePaiement);

  @override
  PaiementDto reference(String? reference) => this(reference: reference);

  @override
  PaiementDto saisiPar(String? saisiPar) => this(saisiPar: saisiPar);

  @override
  PaiementDto saisiParNom(String? saisiParNom) =>
      this(saisiParNom: saisiParNom);

  @override
  PaiementDto motif(String? motif) => this(motif: motif);

  @override
  PaiementDto contrePasse(bool? contrePasse) => this(contrePasse: contrePasse);

  @override
  PaiementDto contrePassationDe(String? contrePassationDe) =>
      this(contrePassationDe: contrePassationDe);

  @override
  PaiementDto creeLe(DateTime? creeLe) => this(creeLe: creeLe);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `PaiementDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// PaiementDto(...).copyWith(id: 12, name: "My name")
  /// ````
  PaiementDto call({
    Object? id = const $CopyWithPlaceholder(),
    Object? bailId = const $CopyWithPlaceholder(),
    Object? bailNumero = const $CopyWithPlaceholder(),
    Object? locataireId = const $CopyWithPlaceholder(),
    Object? locataireNom = const $CopyWithPlaceholder(),
    Object? type = const $CopyWithPlaceholder(),
    Object? mode = const $CopyWithPlaceholder(),
    Object? montant = const $CopyWithPlaceholder(),
    Object? datePaiement = const $CopyWithPlaceholder(),
    Object? reference = const $CopyWithPlaceholder(),
    Object? saisiPar = const $CopyWithPlaceholder(),
    Object? saisiParNom = const $CopyWithPlaceholder(),
    Object? motif = const $CopyWithPlaceholder(),
    Object? contrePasse = const $CopyWithPlaceholder(),
    Object? contrePassationDe = const $CopyWithPlaceholder(),
    Object? creeLe = const $CopyWithPlaceholder(),
  }) {
    return PaiementDto(
      id: id == const $CopyWithPlaceholder()
          ? _value.id
          // ignore: cast_nullable_to_non_nullable
          : id as String?,
      bailId: bailId == const $CopyWithPlaceholder()
          ? _value.bailId
          // ignore: cast_nullable_to_non_nullable
          : bailId as String?,
      bailNumero: bailNumero == const $CopyWithPlaceholder()
          ? _value.bailNumero
          // ignore: cast_nullable_to_non_nullable
          : bailNumero as String?,
      locataireId: locataireId == const $CopyWithPlaceholder()
          ? _value.locataireId
          // ignore: cast_nullable_to_non_nullable
          : locataireId as String?,
      locataireNom: locataireNom == const $CopyWithPlaceholder()
          ? _value.locataireNom
          // ignore: cast_nullable_to_non_nullable
          : locataireNom as String?,
      type: type == const $CopyWithPlaceholder()
          ? _value.type
          // ignore: cast_nullable_to_non_nullable
          : type as String?,
      mode: mode == const $CopyWithPlaceholder()
          ? _value.mode
          // ignore: cast_nullable_to_non_nullable
          : mode as PaiementDtoModeEnum?,
      montant: montant == const $CopyWithPlaceholder()
          ? _value.montant
          // ignore: cast_nullable_to_non_nullable
          : montant as int?,
      datePaiement: datePaiement == const $CopyWithPlaceholder()
          ? _value.datePaiement
          // ignore: cast_nullable_to_non_nullable
          : datePaiement as DateTime?,
      reference: reference == const $CopyWithPlaceholder()
          ? _value.reference
          // ignore: cast_nullable_to_non_nullable
          : reference as String?,
      saisiPar: saisiPar == const $CopyWithPlaceholder()
          ? _value.saisiPar
          // ignore: cast_nullable_to_non_nullable
          : saisiPar as String?,
      saisiParNom: saisiParNom == const $CopyWithPlaceholder()
          ? _value.saisiParNom
          // ignore: cast_nullable_to_non_nullable
          : saisiParNom as String?,
      motif: motif == const $CopyWithPlaceholder()
          ? _value.motif
          // ignore: cast_nullable_to_non_nullable
          : motif as String?,
      contrePasse: contrePasse == const $CopyWithPlaceholder()
          ? _value.contrePasse
          // ignore: cast_nullable_to_non_nullable
          : contrePasse as bool?,
      contrePassationDe: contrePassationDe == const $CopyWithPlaceholder()
          ? _value.contrePassationDe
          // ignore: cast_nullable_to_non_nullable
          : contrePassationDe as String?,
      creeLe: creeLe == const $CopyWithPlaceholder()
          ? _value.creeLe
          // ignore: cast_nullable_to_non_nullable
          : creeLe as DateTime?,
    );
  }
}

extension $PaiementDtoCopyWith on PaiementDto {
  /// Returns a callable class that can be used as follows: `instanceOfPaiementDto.copyWith(...)` or like so:`instanceOfPaiementDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$PaiementDtoCWProxy get copyWith => _$PaiementDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

PaiementDto _$PaiementDtoFromJson(Map<String, dynamic> json) =>
    $checkedCreate('PaiementDto', json, ($checkedConvert) {
      final val = PaiementDto(
        id: $checkedConvert('id', (v) => v as String?),
        bailId: $checkedConvert('bailId', (v) => v as String?),
        bailNumero: $checkedConvert('bailNumero', (v) => v as String?),
        locataireId: $checkedConvert('locataireId', (v) => v as String?),
        locataireNom: $checkedConvert('locataireNom', (v) => v as String?),
        type: $checkedConvert('type', (v) => v as String?),
        mode: $checkedConvert(
          'mode',
          (v) => $enumDecodeNullable(_$PaiementDtoModeEnumEnumMap, v),
        ),
        montant: $checkedConvert('montant', (v) => (v as num?)?.toInt()),
        datePaiement: $checkedConvert(
          'datePaiement',
          (v) => v == null ? null : DateTime.parse(v as String),
        ),
        reference: $checkedConvert('reference', (v) => v as String?),
        saisiPar: $checkedConvert('saisiPar', (v) => v as String?),
        saisiParNom: $checkedConvert('saisiParNom', (v) => v as String?),
        motif: $checkedConvert('motif', (v) => v as String?),
        contrePasse: $checkedConvert('contrePasse', (v) => v as bool?),
        contrePassationDe: $checkedConvert(
          'contrePassationDe',
          (v) => v as String?,
        ),
        creeLe: $checkedConvert(
          'creeLe',
          (v) => v == null ? null : DateTime.parse(v as String),
        ),
      );
      return val;
    });

Map<String, dynamic> _$PaiementDtoToJson(PaiementDto instance) =>
    <String, dynamic>{
      'id': ?instance.id,
      'bailId': ?instance.bailId,
      'bailNumero': ?instance.bailNumero,
      'locataireId': ?instance.locataireId,
      'locataireNom': ?instance.locataireNom,
      'type': ?instance.type,
      'mode': ?_$PaiementDtoModeEnumEnumMap[instance.mode],
      'montant': ?instance.montant,
      'datePaiement': ?instance.datePaiement?.toIso8601String(),
      'reference': ?instance.reference,
      'saisiPar': ?instance.saisiPar,
      'saisiParNom': ?instance.saisiParNom,
      'motif': ?instance.motif,
      'contrePasse': ?instance.contrePasse,
      'contrePassationDe': ?instance.contrePassationDe,
      'creeLe': ?instance.creeLe?.toIso8601String(),
    };

const _$PaiementDtoModeEnumEnumMap = {
  PaiementDtoModeEnum.ESPECES: 'ESPECES',
  PaiementDtoModeEnum.VIREMENT: 'VIREMENT',
  PaiementDtoModeEnum.MTN_MOMO: 'MTN_MOMO',
  PaiementDtoModeEnum.ORANGE_MONEY: 'ORANGE_MONEY',
};
