// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'message_dto.dart';

// **************************************************************************
// CopyWithGenerator
// **************************************************************************

abstract class _$MessageDtoCWProxy {
  MessageDto id(String? id);

  MessageDto canal(MessageDtoCanalEnum? canal);

  MessageDto typeMessage(MessageDtoTypeMessageEnum? typeMessage);

  MessageDto destinataire(String? destinataire);

  MessageDto sujet(String? sujet);

  MessageDto corps(String? corps);

  MessageDto statut(MessageDtoStatutEnum? statut);

  MessageDto tentatives(int? tentatives);

  MessageDto prochaineTentative(DateTime? prochaineTentative);

  MessageDto derniereErreur(String? derniereErreur);

  MessageDto envoyeLe(DateTime? envoyeLe);

  MessageDto creeLe(DateTime? creeLe);

  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `MessageDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// MessageDto(...).copyWith(id: 12, name: "My name")
  /// ````
  MessageDto call({
    String? id,
    MessageDtoCanalEnum? canal,
    MessageDtoTypeMessageEnum? typeMessage,
    String? destinataire,
    String? sujet,
    String? corps,
    MessageDtoStatutEnum? statut,
    int? tentatives,
    DateTime? prochaineTentative,
    String? derniereErreur,
    DateTime? envoyeLe,
    DateTime? creeLe,
  });
}

/// Proxy class for `copyWith` functionality. This is a callable class and can be used as follows: `instanceOfMessageDto.copyWith(...)`. Additionally contains functions for specific fields e.g. `instanceOfMessageDto.copyWith.fieldName(...)`
class _$MessageDtoCWProxyImpl implements _$MessageDtoCWProxy {
  const _$MessageDtoCWProxyImpl(this._value);

  final MessageDto _value;

  @override
  MessageDto id(String? id) => this(id: id);

  @override
  MessageDto canal(MessageDtoCanalEnum? canal) => this(canal: canal);

  @override
  MessageDto typeMessage(MessageDtoTypeMessageEnum? typeMessage) =>
      this(typeMessage: typeMessage);

  @override
  MessageDto destinataire(String? destinataire) =>
      this(destinataire: destinataire);

  @override
  MessageDto sujet(String? sujet) => this(sujet: sujet);

  @override
  MessageDto corps(String? corps) => this(corps: corps);

  @override
  MessageDto statut(MessageDtoStatutEnum? statut) => this(statut: statut);

  @override
  MessageDto tentatives(int? tentatives) => this(tentatives: tentatives);

  @override
  MessageDto prochaineTentative(DateTime? prochaineTentative) =>
      this(prochaineTentative: prochaineTentative);

  @override
  MessageDto derniereErreur(String? derniereErreur) =>
      this(derniereErreur: derniereErreur);

  @override
  MessageDto envoyeLe(DateTime? envoyeLe) => this(envoyeLe: envoyeLe);

  @override
  MessageDto creeLe(DateTime? creeLe) => this(creeLe: creeLe);

  @override
  /// This function **does support** nullification of nullable fields. All `null` values passed to `non-nullable` fields will be ignored. You can also use `MessageDto(...).copyWith.fieldName(...)` to override fields one at a time with nullification support.
  ///
  /// Usage
  /// ```dart
  /// MessageDto(...).copyWith(id: 12, name: "My name")
  /// ````
  MessageDto call({
    Object? id = const $CopyWithPlaceholder(),
    Object? canal = const $CopyWithPlaceholder(),
    Object? typeMessage = const $CopyWithPlaceholder(),
    Object? destinataire = const $CopyWithPlaceholder(),
    Object? sujet = const $CopyWithPlaceholder(),
    Object? corps = const $CopyWithPlaceholder(),
    Object? statut = const $CopyWithPlaceholder(),
    Object? tentatives = const $CopyWithPlaceholder(),
    Object? prochaineTentative = const $CopyWithPlaceholder(),
    Object? derniereErreur = const $CopyWithPlaceholder(),
    Object? envoyeLe = const $CopyWithPlaceholder(),
    Object? creeLe = const $CopyWithPlaceholder(),
  }) {
    return MessageDto(
      id: id == const $CopyWithPlaceholder()
          ? _value.id
          // ignore: cast_nullable_to_non_nullable
          : id as String?,
      canal: canal == const $CopyWithPlaceholder()
          ? _value.canal
          // ignore: cast_nullable_to_non_nullable
          : canal as MessageDtoCanalEnum?,
      typeMessage: typeMessage == const $CopyWithPlaceholder()
          ? _value.typeMessage
          // ignore: cast_nullable_to_non_nullable
          : typeMessage as MessageDtoTypeMessageEnum?,
      destinataire: destinataire == const $CopyWithPlaceholder()
          ? _value.destinataire
          // ignore: cast_nullable_to_non_nullable
          : destinataire as String?,
      sujet: sujet == const $CopyWithPlaceholder()
          ? _value.sujet
          // ignore: cast_nullable_to_non_nullable
          : sujet as String?,
      corps: corps == const $CopyWithPlaceholder()
          ? _value.corps
          // ignore: cast_nullable_to_non_nullable
          : corps as String?,
      statut: statut == const $CopyWithPlaceholder()
          ? _value.statut
          // ignore: cast_nullable_to_non_nullable
          : statut as MessageDtoStatutEnum?,
      tentatives: tentatives == const $CopyWithPlaceholder()
          ? _value.tentatives
          // ignore: cast_nullable_to_non_nullable
          : tentatives as int?,
      prochaineTentative: prochaineTentative == const $CopyWithPlaceholder()
          ? _value.prochaineTentative
          // ignore: cast_nullable_to_non_nullable
          : prochaineTentative as DateTime?,
      derniereErreur: derniereErreur == const $CopyWithPlaceholder()
          ? _value.derniereErreur
          // ignore: cast_nullable_to_non_nullable
          : derniereErreur as String?,
      envoyeLe: envoyeLe == const $CopyWithPlaceholder()
          ? _value.envoyeLe
          // ignore: cast_nullable_to_non_nullable
          : envoyeLe as DateTime?,
      creeLe: creeLe == const $CopyWithPlaceholder()
          ? _value.creeLe
          // ignore: cast_nullable_to_non_nullable
          : creeLe as DateTime?,
    );
  }
}

extension $MessageDtoCopyWith on MessageDto {
  /// Returns a callable class that can be used as follows: `instanceOfMessageDto.copyWith(...)` or like so:`instanceOfMessageDto.copyWith.fieldName(...)`.
  // ignore: library_private_types_in_public_api
  _$MessageDtoCWProxy get copyWith => _$MessageDtoCWProxyImpl(this);
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

MessageDto _$MessageDtoFromJson(Map<String, dynamic> json) =>
    $checkedCreate('MessageDto', json, ($checkedConvert) {
      final val = MessageDto(
        id: $checkedConvert('id', (v) => v as String?),
        canal: $checkedConvert(
          'canal',
          (v) => $enumDecodeNullable(_$MessageDtoCanalEnumEnumMap, v),
        ),
        typeMessage: $checkedConvert(
          'typeMessage',
          (v) => $enumDecodeNullable(_$MessageDtoTypeMessageEnumEnumMap, v),
        ),
        destinataire: $checkedConvert('destinataire', (v) => v as String?),
        sujet: $checkedConvert('sujet', (v) => v as String?),
        corps: $checkedConvert('corps', (v) => v as String?),
        statut: $checkedConvert(
          'statut',
          (v) => $enumDecodeNullable(_$MessageDtoStatutEnumEnumMap, v),
        ),
        tentatives: $checkedConvert('tentatives', (v) => (v as num?)?.toInt()),
        prochaineTentative: $checkedConvert(
          'prochaineTentative',
          (v) => v == null ? null : DateTime.parse(v as String),
        ),
        derniereErreur: $checkedConvert('derniereErreur', (v) => v as String?),
        envoyeLe: $checkedConvert(
          'envoyeLe',
          (v) => v == null ? null : DateTime.parse(v as String),
        ),
        creeLe: $checkedConvert(
          'creeLe',
          (v) => v == null ? null : DateTime.parse(v as String),
        ),
      );
      return val;
    });

Map<String, dynamic> _$MessageDtoToJson(MessageDto instance) =>
    <String, dynamic>{
      'id': ?instance.id,
      'canal': ?_$MessageDtoCanalEnumEnumMap[instance.canal],
      'typeMessage': ?_$MessageDtoTypeMessageEnumEnumMap[instance.typeMessage],
      'destinataire': ?instance.destinataire,
      'sujet': ?instance.sujet,
      'corps': ?instance.corps,
      'statut': ?_$MessageDtoStatutEnumEnumMap[instance.statut],
      'tentatives': ?instance.tentatives,
      'prochaineTentative': ?instance.prochaineTentative?.toIso8601String(),
      'derniereErreur': ?instance.derniereErreur,
      'envoyeLe': ?instance.envoyeLe?.toIso8601String(),
      'creeLe': ?instance.creeLe?.toIso8601String(),
    };

const _$MessageDtoCanalEnumEnumMap = {
  MessageDtoCanalEnum.SMS: 'SMS',
  MessageDtoCanalEnum.WHATSAPP: 'WHATSAPP',
  MessageDtoCanalEnum.EMAIL: 'EMAIL',
  MessageDtoCanalEnum.PUSH: 'PUSH',
};

const _$MessageDtoTypeMessageEnumEnumMap = {
  MessageDtoTypeMessageEnum.RECU_ESPECES: 'RECU_ESPECES',
  MessageDtoTypeMessageEnum.QUITTANCE_EMISE: 'QUITTANCE_EMISE',
  MessageDtoTypeMessageEnum.RECU_EMIS: 'RECU_EMIS',
  MessageDtoTypeMessageEnum.RAPPEL_ECHEANCE: 'RAPPEL_ECHEANCE',
  MessageDtoTypeMessageEnum.RELANCE_IMPAYE: 'RELANCE_IMPAYE',
  MessageDtoTypeMessageEnum.MISE_EN_DEMEURE: 'MISE_EN_DEMEURE',
  MessageDtoTypeMessageEnum.TEST: 'TEST',
};

const _$MessageDtoStatutEnumEnumMap = {
  MessageDtoStatutEnum.EN_ATTENTE: 'EN_ATTENTE',
  MessageDtoStatutEnum.ENVOYE: 'ENVOYE',
  MessageDtoStatutEnum.ECHEC: 'ECHEC',
};
