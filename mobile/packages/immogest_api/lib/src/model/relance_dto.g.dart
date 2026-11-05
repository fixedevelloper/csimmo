// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'relance_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$RelanceDtoCWProxy {
  RelanceDto id(String? id);

  RelanceDto echeanceId(String? echeanceId);

  RelanceDto bailId(String? bailId);

  RelanceDto locataireId(String? locataireId);

  RelanceDto type(RelanceDtoTypeEnum? type);

  RelanceDto niveau(int? niveau);

  RelanceDto dateRelance(DateTime? dateRelance);

  RelanceDto resultat(RelanceDtoResultatEnum? resultat);

  RelanceDto canal(RelanceDtoCanalEnum? canal);

  RelanceDto statutEnvoi(RelanceDtoStatutEnvoiEnum? statutEnvoi);

  RelanceDto resteDu(int? resteDu);

  RelanceDto joursRetard(int? joursRetard);

  RelanceDto creeLe(DateTime? creeLe);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `RelanceDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// RelanceDto(...).copyWith(id: 12, name: "My name")
  /// ````
  RelanceDto call({
    String? id,
    String? echeanceId,
    String? bailId,
    String? locataireId,
    RelanceDtoTypeEnum? type,
    int? niveau,
    DateTime? dateRelance,
    RelanceDtoResultatEnum? resultat,
    RelanceDtoCanalEnum? canal,
    RelanceDtoStatutEnvoiEnum? statutEnvoi,
    int? resteDu,
    int? joursRetard,
    DateTime? creeLe,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfRelanceDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfRelanceDto.copyWith.fieldName(...)`
class _$RelanceDtoCWProxyImpl implements _$RelanceDtoCWProxy {
  const _$RelanceDtoCWProxyImpl(this._value);

  final RelanceDto _value;

  @override
  RelanceDto id(String? id) => this(id: id);

  @override
  RelanceDto echeanceId(String? echeanceId) => this(echeanceId: echeanceId);

  @override
  RelanceDto bailId(String? bailId) => this(bailId: bailId);

  @override
  RelanceDto locataireId(String? locataireId) => this(locataireId: locataireId);

  @override
  RelanceDto type(RelanceDtoTypeEnum? type) => this(type: type);

  @override
  RelanceDto niveau(int? niveau) => this(niveau: niveau);

  @override
  RelanceDto dateRelance(DateTime? dateRelance) =>
      this(dateRelance: dateRelance);

  @override
  RelanceDto resultat(RelanceDtoResultatEnum? resultat) =>
      this(resultat: resultat);

  @override
  RelanceDto canal(RelanceDtoCanalEnum? canal) => this(canal: canal);

  @override
  RelanceDto statutEnvoi(RelanceDtoStatutEnvoiEnum? statutEnvoi) =>
      this(statutEnvoi: statutEnvoi);

  @override
  RelanceDto resteDu(int? resteDu) => this(resteDu: resteDu);

  @override
  RelanceDto joursRetard(int? joursRetard) => this(joursRetard: joursRetard);

  @override
  RelanceDto creeLe(DateTime? creeLe) => this(creeLe: creeLe);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `RelanceDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// RelanceDto(...).copyWith(id: 12, name: "My name")
  /// ````
  RelanceDto call({
    Object? id = const $CopyWithPlaceholder(),
    Object? echeanceId = const $CopyWithPlaceholder(),
    Object? bailId = const $CopyWithPlaceholder(),
    Object? locataireId = const $CopyWithPlaceholder(),
    Object? type = const $CopyWithPlaceholder(),
    Object? niveau = const $CopyWithPlaceholder(),
    Object? dateRelance = const $CopyWithPlaceholder(),
    Object? resultat = const $CopyWithPlaceholder(),
    Object? canal = const $CopyWithPlaceholder(),
    Object? statutEnvoi = const $CopyWithPlaceholder(),
    Object? resteDu = const $CopyWithPlaceholder(),
    Object? joursRetard = const $CopyWithPlaceholder(),
    Object? creeLe = const $CopyWithPlaceholder(),
  }) {
    return RelanceDto(
      id: id == const $CopyWithPlaceholder()
          ? _value.id
          // ignore: cast_nullable_to_non_nullable
          : id as String?,
      echeanceId: echeanceId == const $CopyWithPlaceholder()
          ? _value.echeanceId
          // ignore: cast_nullable_to_non_nullable
          : echeanceId as String?,
      bailId: bailId == const $CopyWithPlaceholder()
          ? _value.bailId
          // ignore: cast_nullable_to_non_nullable
          : bailId as String?,
      locataireId: locataireId == const $CopyWithPlaceholder()
          ? _value.locataireId
          // ignore: cast_nullable_to_non_nullable
          : locataireId as String?,
      type: type == const $CopyWithPlaceholder()
          ? _value.type
          // ignore: cast_nullable_to_non_nullable
          : type as RelanceDtoTypeEnum?,
      niveau: niveau == const $CopyWithPlaceholder()
          ? _value.niveau
          // ignore: cast_nullable_to_non_nullable
          : niveau as int?,
      dateRelance: dateRelance == const $CopyWithPlaceholder()
          ? _value.dateRelance
          // ignore: cast_nullable_to_non_nullable
          : dateRelance as DateTime?,
      resultat: resultat == const $CopyWithPlaceholder()
          ? _value.resultat
          // ignore: cast_nullable_to_non_nullable
          : resultat as RelanceDtoResultatEnum?,
      canal: canal == const $CopyWithPlaceholder()
          ? _value.canal
          // ignore: cast_nullable_to_non_nullable
          : canal as RelanceDtoCanalEnum?,
      statutEnvoi: statutEnvoi == const $CopyWithPlaceholder()
          ? _value.statutEnvoi
          // ignore: cast_nullable_to_non_nullable
          : statutEnvoi as RelanceDtoStatutEnvoiEnum?,
      resteDu: resteDu == const $CopyWithPlaceholder()
          ? _value.resteDu
          // ignore: cast_nullable_to_non_nullable
          : resteDu as int?,
      joursRetard: joursRetard == const $CopyWithPlaceholder()
          ? _value.joursRetard
          // ignore: cast_nullable_to_non_nullable
          : joursRetard as int?,
      creeLe: creeLe == const $CopyWithPlaceholder()
          ? _value.creeLe
          // ignore: cast_nullable_to_non_nullable
          : creeLe as DateTime?,
    );
  }
}

extension $RelanceDtoCopyWith on RelanceDto {
  /// Returns a callable class that can be used as follows: `instanceOfRelanceDto.copyWith(...)` or like so:`instanceOfRelanceDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$RelanceDtoCWProxy get copyWith => _$RelanceDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

RelanceDto _$RelanceDtoFromJson(Map<String, dynamic> json) => $checkedCreate(
  'RelanceDto',
  json,
  ($checkedConvert) {
    final val = RelanceDto(
      id: $checkedConvert('id', (v) => v as String?),
      echeanceId: $checkedConvert('echeanceId', (v) => v as String?),
      bailId: $checkedConvert('bailId', (v) => v as String?),
      locataireId: $checkedConvert('locataireId', (v) => v as String?),
      type: $checkedConvert(
        'type',
        (v) => $enumDecodeNullable(_$RelanceDtoTypeEnumEnumMap, v),
      ),
      niveau: $checkedConvert('niveau', (v) => (v as num?)?.toInt()),
      dateRelance: $checkedConvert(
        'dateRelance',
        (v) => v == null ? null : DateTime.parse(v as String),
      ),
      resultat: $checkedConvert(
        'resultat',
        (v) => $enumDecodeNullable(_$RelanceDtoResultatEnumEnumMap, v),
      ),
      canal: $checkedConvert(
        'canal',
        (v) => $enumDecodeNullable(_$RelanceDtoCanalEnumEnumMap, v),
      ),
      statutEnvoi: $checkedConvert(
        'statutEnvoi',
        (v) => $enumDecodeNullable(_$RelanceDtoStatutEnvoiEnumEnumMap, v),
      ),
      resteDu: $checkedConvert('resteDu', (v) => (v as num?)?.toInt()),
      joursRetard: $checkedConvert('joursRetard', (v) => (v as num?)?.toInt()),
      creeLe: $checkedConvert(
        'creeLe',
        (v) => v == null ? null : DateTime.parse(v as String),
      ),
    );
    return val;
  },
);

Map<String, dynamic> _$RelanceDtoToJson(RelanceDto instance) =>
    <String, dynamic>{
      'id': ?instance.id,
      'echeanceId': ?instance.echeanceId,
      'bailId': ?instance.bailId,
      'locataireId': ?instance.locataireId,
      'type': ?_$RelanceDtoTypeEnumEnumMap[instance.type],
      'niveau': ?instance.niveau,
      'dateRelance': ?instance.dateRelance?.toIso8601String(),
      'resultat': ?_$RelanceDtoResultatEnumEnumMap[instance.resultat],
      'canal': ?_$RelanceDtoCanalEnumEnumMap[instance.canal],
      'statutEnvoi': ?_$RelanceDtoStatutEnvoiEnumEnumMap[instance.statutEnvoi],
      'resteDu': ?instance.resteDu,
      'joursRetard': ?instance.joursRetard,
      'creeLe': ?instance.creeLe?.toIso8601String(),
    };

const _$RelanceDtoTypeEnumEnumMap = {
  RelanceDtoTypeEnum.AUTO: 'AUTO',
  RelanceDtoTypeEnum.MANUELLE: 'MANUELLE',
};

const _$RelanceDtoResultatEnumEnumMap = {
  RelanceDtoResultatEnum.EN_FILE: 'EN_FILE',
  RelanceDtoResultatEnum.AUCUN_CANAL: 'AUCUN_CANAL',
  RelanceDtoResultatEnum.IGNOREE: 'IGNOREE',
};

const _$RelanceDtoCanalEnumEnumMap = {
  RelanceDtoCanalEnum.SMS: 'SMS',
  RelanceDtoCanalEnum.WHATSAPP: 'WHATSAPP',
  RelanceDtoCanalEnum.EMAIL: 'EMAIL',
  RelanceDtoCanalEnum.PUSH: 'PUSH',
};

const _$RelanceDtoStatutEnvoiEnumEnumMap = {
  RelanceDtoStatutEnvoiEnum.EN_ATTENTE: 'EN_ATTENTE',
  RelanceDtoStatutEnvoiEnum.ENVOYE: 'ENVOYE',
  RelanceDtoStatutEnvoiEnum.ECHEC: 'ECHEC',
};
