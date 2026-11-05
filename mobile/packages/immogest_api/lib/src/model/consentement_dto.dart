//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'consentement_dto.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class ConsentementDto {
  /// Returns a new [ConsentementDto] instance.
  ConsentementDto({

     this.id,

     this.finalite,

     this.accorde,

     this.versionModele,

     this.modeRecueil,

     this.referenceDocument,

     this.recueilliPar,

     this.decideLe,
  });

  @JsonKey(
    
    name: r'id',
    required: false,
    includeIfNull: false,
  )


  final String? id;



  @JsonKey(
    
    name: r'finalite',
    required: false,
    includeIfNull: false,
  )


  final ConsentementDtoFinaliteEnum? finalite;



  @JsonKey(
    
    name: r'accorde',
    required: false,
    includeIfNull: false,
  )


  final bool? accorde;



  @JsonKey(
    
    name: r'versionModele',
    required: false,
    includeIfNull: false,
  )


  final String? versionModele;



  @JsonKey(
    
    name: r'modeRecueil',
    required: false,
    includeIfNull: false,
  )


  final ConsentementDtoModeRecueilEnum? modeRecueil;



  @JsonKey(
    
    name: r'referenceDocument',
    required: false,
    includeIfNull: false,
  )


  final String? referenceDocument;



  @JsonKey(
    
    name: r'recueilliPar',
    required: false,
    includeIfNull: false,
  )


  final String? recueilliPar;



  @JsonKey(
    
    name: r'decideLe',
    required: false,
    includeIfNull: false,
  )


  final DateTime? decideLe;





    @override
    bool operator ==(Object other) => identical(this, other) || other is ConsentementDto &&
      other.id == id &&
      other.finalite == finalite &&
      other.accorde == accorde &&
      other.versionModele == versionModele &&
      other.modeRecueil == modeRecueil &&
      other.referenceDocument == referenceDocument &&
      other.recueilliPar == recueilliPar &&
      other.decideLe == decideLe;

    @override
    int get hashCode =>
        id.hashCode +
        finalite.hashCode +
        accorde.hashCode +
        versionModele.hashCode +
        modeRecueil.hashCode +
        referenceDocument.hashCode +
        recueilliPar.hashCode +
        decideLe.hashCode;

  factory ConsentementDto.fromJson(Map<String, dynamic> json) => _$ConsentementDtoFromJson(json);

  Map<String, dynamic> toJson() => _$ConsentementDtoToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

enum ConsentementDtoFinaliteEnum {
@JsonValue(r'NOTIFICATIONS')
NOTIFICATIONS(r'NOTIFICATIONS'),
@JsonValue(r'PIECES_IDENTITE')
PIECES_IDENTITE(r'PIECES_IDENTITE'),
@JsonValue(r'TRANSMISSION_PROPRIETAIRE')
TRANSMISSION_PROPRIETAIRE(r'TRANSMISSION_PROPRIETAIRE'),
@JsonValue(r'APPLICATION_MOBILE')
APPLICATION_MOBILE(r'APPLICATION_MOBILE');

const ConsentementDtoFinaliteEnum(this.value);

final String value;

@override
String toString() => value;
}


enum ConsentementDtoModeRecueilEnum {
@JsonValue(r'PAPIER')
PAPIER(r'PAPIER'),
@JsonValue(r'ELECTRONIQUE')
ELECTRONIQUE(r'ELECTRONIQUE'),
@JsonValue(r'ORAL')
ORAL(r'ORAL');

const ConsentementDtoModeRecueilEnum(this.value);

final String value;

@override
String toString() => value;
}


