//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'message_dto.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class MessageDto {
  /// Returns a new [MessageDto] instance.
  MessageDto({

     this.id,

     this.canal,

     this.typeMessage,

     this.destinataire,

     this.sujet,

     this.corps,

     this.statut,

     this.tentatives,

     this.prochaineTentative,

     this.derniereErreur,

     this.envoyeLe,

     this.creeLe,
  });

  @JsonKey(
    
    name: r'id',
    required: false,
    includeIfNull: false,
  )


  final String? id;



  @JsonKey(
    
    name: r'canal',
    required: false,
    includeIfNull: false,
  )


  final MessageDtoCanalEnum? canal;



  @JsonKey(
    
    name: r'typeMessage',
    required: false,
    includeIfNull: false,
  )


  final MessageDtoTypeMessageEnum? typeMessage;



  @JsonKey(
    
    name: r'destinataire',
    required: false,
    includeIfNull: false,
  )


  final String? destinataire;



  @JsonKey(
    
    name: r'sujet',
    required: false,
    includeIfNull: false,
  )


  final String? sujet;



  @JsonKey(
    
    name: r'corps',
    required: false,
    includeIfNull: false,
  )


  final String? corps;



  @JsonKey(
    
    name: r'statut',
    required: false,
    includeIfNull: false,
  )


  final MessageDtoStatutEnum? statut;



  @JsonKey(
    
    name: r'tentatives',
    required: false,
    includeIfNull: false,
  )


  final int? tentatives;



  @JsonKey(
    
    name: r'prochaineTentative',
    required: false,
    includeIfNull: false,
  )


  final DateTime? prochaineTentative;



  @JsonKey(
    
    name: r'derniereErreur',
    required: false,
    includeIfNull: false,
  )


  final String? derniereErreur;



  @JsonKey(
    
    name: r'envoyeLe',
    required: false,
    includeIfNull: false,
  )


  final DateTime? envoyeLe;



  @JsonKey(
    
    name: r'creeLe',
    required: false,
    includeIfNull: false,
  )


  final DateTime? creeLe;





    @override
    bool operator ==(Object other) => identical(this, other) || other is MessageDto &&
      other.id == id &&
      other.canal == canal &&
      other.typeMessage == typeMessage &&
      other.destinataire == destinataire &&
      other.sujet == sujet &&
      other.corps == corps &&
      other.statut == statut &&
      other.tentatives == tentatives &&
      other.prochaineTentative == prochaineTentative &&
      other.derniereErreur == derniereErreur &&
      other.envoyeLe == envoyeLe &&
      other.creeLe == creeLe;

    @override
    int get hashCode =>
        id.hashCode +
        canal.hashCode +
        typeMessage.hashCode +
        destinataire.hashCode +
        sujet.hashCode +
        corps.hashCode +
        statut.hashCode +
        tentatives.hashCode +
        prochaineTentative.hashCode +
        derniereErreur.hashCode +
        envoyeLe.hashCode +
        creeLe.hashCode;

  factory MessageDto.fromJson(Map<String, dynamic> json) => _$MessageDtoFromJson(json);

  Map<String, dynamic> toJson() => _$MessageDtoToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

enum MessageDtoCanalEnum {
@JsonValue(r'SMS')
SMS(r'SMS'),
@JsonValue(r'WHATSAPP')
WHATSAPP(r'WHATSAPP'),
@JsonValue(r'EMAIL')
EMAIL(r'EMAIL'),
@JsonValue(r'PUSH')
PUSH(r'PUSH');

const MessageDtoCanalEnum(this.value);

final String value;

@override
String toString() => value;
}


enum MessageDtoTypeMessageEnum {
@JsonValue(r'RECU_ESPECES')
RECU_ESPECES(r'RECU_ESPECES'),
@JsonValue(r'QUITTANCE_EMISE')
QUITTANCE_EMISE(r'QUITTANCE_EMISE'),
@JsonValue(r'RECU_EMIS')
RECU_EMIS(r'RECU_EMIS'),
@JsonValue(r'RAPPEL_ECHEANCE')
RAPPEL_ECHEANCE(r'RAPPEL_ECHEANCE'),
@JsonValue(r'RELANCE_IMPAYE')
RELANCE_IMPAYE(r'RELANCE_IMPAYE'),
@JsonValue(r'MISE_EN_DEMEURE')
MISE_EN_DEMEURE(r'MISE_EN_DEMEURE'),
@JsonValue(r'TEST')
TEST(r'TEST');

const MessageDtoTypeMessageEnum(this.value);

final String value;

@override
String toString() => value;
}


enum MessageDtoStatutEnum {
@JsonValue(r'EN_ATTENTE')
EN_ATTENTE(r'EN_ATTENTE'),
@JsonValue(r'ENVOYE')
ENVOYE(r'ENVOYE'),
@JsonValue(r'ECHEC')
ECHEC(r'ECHEC');

const MessageDtoStatutEnum(this.value);

final String value;

@override
String toString() => value;
}


