//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'relance_dto.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class RelanceDto {
  /// Returns a new [RelanceDto] instance.
  RelanceDto({

     this.id,

     this.echeanceId,

     this.bailId,

     this.locataireId,

     this.type,

     this.niveau,

     this.dateRelance,

     this.resultat,

     this.canal,

     this.statutEnvoi,

     this.resteDu,

     this.joursRetard,

     this.creeLe,
  });

  @JsonKey(
    
    name: r'id',
    required: false,
    includeIfNull: false,
  )


  final String? id;



  @JsonKey(
    
    name: r'echeanceId',
    required: false,
    includeIfNull: false,
  )


  final String? echeanceId;



  @JsonKey(
    
    name: r'bailId',
    required: false,
    includeIfNull: false,
  )


  final String? bailId;



  @JsonKey(
    
    name: r'locataireId',
    required: false,
    includeIfNull: false,
  )


  final String? locataireId;



  @JsonKey(
    
    name: r'type',
    required: false,
    includeIfNull: false,
  )


  final RelanceDtoTypeEnum? type;



  @JsonKey(
    
    name: r'niveau',
    required: false,
    includeIfNull: false,
  )


  final int? niveau;



  @JsonKey(
    
    name: r'dateRelance',
    required: false,
    includeIfNull: false,
  )


  final DateTime? dateRelance;



  @JsonKey(
    
    name: r'resultat',
    required: false,
    includeIfNull: false,
  )


  final RelanceDtoResultatEnum? resultat;



  @JsonKey(
    
    name: r'canal',
    required: false,
    includeIfNull: false,
  )


  final RelanceDtoCanalEnum? canal;



  @JsonKey(
    
    name: r'statutEnvoi',
    required: false,
    includeIfNull: false,
  )


  final RelanceDtoStatutEnvoiEnum? statutEnvoi;



  @JsonKey(
    
    name: r'resteDu',
    required: false,
    includeIfNull: false,
  )


  final int? resteDu;



  @JsonKey(
    
    name: r'joursRetard',
    required: false,
    includeIfNull: false,
  )


  final int? joursRetard;



  @JsonKey(
    
    name: r'creeLe',
    required: false,
    includeIfNull: false,
  )


  final DateTime? creeLe;





    @override
    bool operator ==(Object other) => identical(this, other) || other is RelanceDto &&
      other.id == id &&
      other.echeanceId == echeanceId &&
      other.bailId == bailId &&
      other.locataireId == locataireId &&
      other.type == type &&
      other.niveau == niveau &&
      other.dateRelance == dateRelance &&
      other.resultat == resultat &&
      other.canal == canal &&
      other.statutEnvoi == statutEnvoi &&
      other.resteDu == resteDu &&
      other.joursRetard == joursRetard &&
      other.creeLe == creeLe;

    @override
    int get hashCode =>
        id.hashCode +
        echeanceId.hashCode +
        bailId.hashCode +
        locataireId.hashCode +
        type.hashCode +
        niveau.hashCode +
        dateRelance.hashCode +
        resultat.hashCode +
        canal.hashCode +
        statutEnvoi.hashCode +
        resteDu.hashCode +
        joursRetard.hashCode +
        creeLe.hashCode;

  factory RelanceDto.fromJson(Map<String, dynamic> json) => _$RelanceDtoFromJson(json);

  Map<String, dynamic> toJson() => _$RelanceDtoToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

enum RelanceDtoTypeEnum {
@JsonValue(r'AUTO')
AUTO(r'AUTO'),
@JsonValue(r'MANUELLE')
MANUELLE(r'MANUELLE');

const RelanceDtoTypeEnum(this.value);

final String value;

@override
String toString() => value;
}


enum RelanceDtoResultatEnum {
@JsonValue(r'EN_FILE')
EN_FILE(r'EN_FILE'),
@JsonValue(r'AUCUN_CANAL')
AUCUN_CANAL(r'AUCUN_CANAL'),
@JsonValue(r'IGNOREE')
IGNOREE(r'IGNOREE');

const RelanceDtoResultatEnum(this.value);

final String value;

@override
String toString() => value;
}


enum RelanceDtoCanalEnum {
@JsonValue(r'SMS')
SMS(r'SMS'),
@JsonValue(r'WHATSAPP')
WHATSAPP(r'WHATSAPP'),
@JsonValue(r'EMAIL')
EMAIL(r'EMAIL'),
@JsonValue(r'PUSH')
PUSH(r'PUSH');

const RelanceDtoCanalEnum(this.value);

final String value;

@override
String toString() => value;
}


enum RelanceDtoStatutEnvoiEnum {
@JsonValue(r'EN_ATTENTE')
EN_ATTENTE(r'EN_ATTENTE'),
@JsonValue(r'ENVOYE')
ENVOYE(r'ENVOYE'),
@JsonValue(r'ECHEC')
ECHEC(r'ECHEC');

const RelanceDtoStatutEnvoiEnum(this.value);

final String value;

@override
String toString() => value;
}


