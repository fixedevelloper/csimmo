// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'signalement_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$SignalementDtoCWProxy {
  SignalementDto id(String? id);

  SignalementDto bailId(String? bailId);

  SignalementDto bailNumero(String? bailNumero);

  SignalementDto lotId(String? lotId);

  SignalementDto logement(String? logement);

  SignalementDto locataireId(String? locataireId);

  SignalementDto locataireNom(String? locataireNom);

  SignalementDto description(String? description);

  SignalementDto statut(SignalementDtoStatutEnum? statut);

  SignalementDto photoFichierId(String? photoFichierId);

  SignalementDto noteGestion(String? noteGestion);

  SignalementDto creeLe(DateTime? creeLe);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `SignalementDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// SignalementDto(...).copyWith(id: 12, name: "My name")
  /// ````
  SignalementDto call({
    String? id,
    String? bailId,
    String? bailNumero,
    String? lotId,
    String? logement,
    String? locataireId,
    String? locataireNom,
    String? description,
    SignalementDtoStatutEnum? statut,
    String? photoFichierId,
    String? noteGestion,
    DateTime? creeLe,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfSignalementDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfSignalementDto.copyWith.fieldName(...)`
class _$SignalementDtoCWProxyImpl implements _$SignalementDtoCWProxy {
  const _$SignalementDtoCWProxyImpl(this._value);

  final SignalementDto _value;

  @override
  SignalementDto id(String? id) => this(id: id);

  @override
  SignalementDto bailId(String? bailId) => this(bailId: bailId);

  @override
  SignalementDto bailNumero(String? bailNumero) => this(bailNumero: bailNumero);

  @override
  SignalementDto lotId(String? lotId) => this(lotId: lotId);

  @override
  SignalementDto logement(String? logement) => this(logement: logement);

  @override
  SignalementDto locataireId(String? locataireId) =>
      this(locataireId: locataireId);

  @override
  SignalementDto locataireNom(String? locataireNom) =>
      this(locataireNom: locataireNom);

  @override
  SignalementDto description(String? description) =>
      this(description: description);

  @override
  SignalementDto statut(SignalementDtoStatutEnum? statut) =>
      this(statut: statut);

  @override
  SignalementDto photoFichierId(String? photoFichierId) =>
      this(photoFichierId: photoFichierId);

  @override
  SignalementDto noteGestion(String? noteGestion) =>
      this(noteGestion: noteGestion);

  @override
  SignalementDto creeLe(DateTime? creeLe) => this(creeLe: creeLe);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `SignalementDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// SignalementDto(...).copyWith(id: 12, name: "My name")
  /// ````
  SignalementDto call({
    Object? id = const $CopyWithPlaceholder(),
    Object? bailId = const $CopyWithPlaceholder(),
    Object? bailNumero = const $CopyWithPlaceholder(),
    Object? lotId = const $CopyWithPlaceholder(),
    Object? logement = const $CopyWithPlaceholder(),
    Object? locataireId = const $CopyWithPlaceholder(),
    Object? locataireNom = const $CopyWithPlaceholder(),
    Object? description = const $CopyWithPlaceholder(),
    Object? statut = const $CopyWithPlaceholder(),
    Object? photoFichierId = const $CopyWithPlaceholder(),
    Object? noteGestion = const $CopyWithPlaceholder(),
    Object? creeLe = const $CopyWithPlaceholder(),
  }) {
    return SignalementDto(
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
      lotId: lotId == const $CopyWithPlaceholder()
          ? _value.lotId
          // ignore: cast_nullable_to_non_nullable
          : lotId as String?,
      logement: logement == const $CopyWithPlaceholder()
          ? _value.logement
          // ignore: cast_nullable_to_non_nullable
          : logement as String?,
      locataireId: locataireId == const $CopyWithPlaceholder()
          ? _value.locataireId
          // ignore: cast_nullable_to_non_nullable
          : locataireId as String?,
      locataireNom: locataireNom == const $CopyWithPlaceholder()
          ? _value.locataireNom
          // ignore: cast_nullable_to_non_nullable
          : locataireNom as String?,
      description: description == const $CopyWithPlaceholder()
          ? _value.description
          // ignore: cast_nullable_to_non_nullable
          : description as String?,
      statut: statut == const $CopyWithPlaceholder()
          ? _value.statut
          // ignore: cast_nullable_to_non_nullable
          : statut as SignalementDtoStatutEnum?,
      photoFichierId: photoFichierId == const $CopyWithPlaceholder()
          ? _value.photoFichierId
          // ignore: cast_nullable_to_non_nullable
          : photoFichierId as String?,
      noteGestion: noteGestion == const $CopyWithPlaceholder()
          ? _value.noteGestion
          // ignore: cast_nullable_to_non_nullable
          : noteGestion as String?,
      creeLe: creeLe == const $CopyWithPlaceholder()
          ? _value.creeLe
          // ignore: cast_nullable_to_non_nullable
          : creeLe as DateTime?,
    );
  }
}

extension $SignalementDtoCopyWith on SignalementDto {
  /// Returns a callable class that can be used as follows: `instanceOfSignalementDto.copyWith(...)` or like so:`instanceOfSignalementDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$SignalementDtoCWProxy get copyWith => _$SignalementDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

SignalementDto _$SignalementDtoFromJson(Map<String, dynamic> json) =>
    $checkedCreate('SignalementDto', json, ($checkedConvert) {
      final val = SignalementDto(
        id: $checkedConvert('id', (v) => v as String?),
        bailId: $checkedConvert('bailId', (v) => v as String?),
        bailNumero: $checkedConvert('bailNumero', (v) => v as String?),
        lotId: $checkedConvert('lotId', (v) => v as String?),
        logement: $checkedConvert('logement', (v) => v as String?),
        locataireId: $checkedConvert('locataireId', (v) => v as String?),
        locataireNom: $checkedConvert('locataireNom', (v) => v as String?),
        description: $checkedConvert('description', (v) => v as String?),
        statut: $checkedConvert(
          'statut',
          (v) => $enumDecodeNullable(_$SignalementDtoStatutEnumEnumMap, v),
        ),
        photoFichierId: $checkedConvert('photoFichierId', (v) => v as String?),
        noteGestion: $checkedConvert('noteGestion', (v) => v as String?),
        creeLe: $checkedConvert(
          'creeLe',
          (v) => v == null ? null : DateTime.parse(v as String),
        ),
      );
      return val;
    });

Map<String, dynamic> _$SignalementDtoToJson(SignalementDto instance) =>
    <String, dynamic>{
      'id': ?instance.id,
      'bailId': ?instance.bailId,
      'bailNumero': ?instance.bailNumero,
      'lotId': ?instance.lotId,
      'logement': ?instance.logement,
      'locataireId': ?instance.locataireId,
      'locataireNom': ?instance.locataireNom,
      'description': ?instance.description,
      'statut': ?_$SignalementDtoStatutEnumEnumMap[instance.statut],
      'photoFichierId': ?instance.photoFichierId,
      'noteGestion': ?instance.noteGestion,
      'creeLe': ?instance.creeLe?.toIso8601String(),
    };

const _$SignalementDtoStatutEnumEnumMap = {
  SignalementDtoStatutEnum.NOUVEAU: 'NOUVEAU',
  SignalementDtoStatutEnum.EN_COURS: 'EN_COURS',
  SignalementDtoStatutEnum.RESOLU: 'RESOLU',
  SignalementDtoStatutEnum.REJETE: 'REJETE',
};
