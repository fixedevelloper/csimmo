//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:immogest_api/src/model/obstacle_dto.dart';
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'demande_anonymisation_dto.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class DemandeAnonymisationDto {
  /// Returns a new [DemandeAnonymisationDto] instance.
  DemandeAnonymisationDto({

     this.id,

     this.type,

     this.personneId,

     this.statut,

     this.motif,

     this.motifRefus,

     this.recueLe,

     this.recuePar,

     this.traiteeLe,

     this.traiteePar,

     this.obstacles,
  });

  @JsonKey(
    
    name: r'id',
    required: false,
    includeIfNull: false,
  )


  final String? id;



  @JsonKey(
    
    name: r'type',
    required: false,
    includeIfNull: false,
  )


  final DemandeAnonymisationDtoTypeEnum? type;



  @JsonKey(
    
    name: r'personneId',
    required: false,
    includeIfNull: false,
  )


  final String? personneId;



  @JsonKey(
    
    name: r'statut',
    required: false,
    includeIfNull: false,
  )


  final String? statut;



  @JsonKey(
    
    name: r'motif',
    required: false,
    includeIfNull: false,
  )


  final String? motif;



  @JsonKey(
    
    name: r'motifRefus',
    required: false,
    includeIfNull: false,
  )


  final String? motifRefus;



  @JsonKey(
    
    name: r'recueLe',
    required: false,
    includeIfNull: false,
  )


  final DateTime? recueLe;



  @JsonKey(
    
    name: r'recuePar',
    required: false,
    includeIfNull: false,
  )


  final String? recuePar;



  @JsonKey(
    
    name: r'traiteeLe',
    required: false,
    includeIfNull: false,
  )


  final DateTime? traiteeLe;



  @JsonKey(
    
    name: r'traiteePar',
    required: false,
    includeIfNull: false,
  )


  final String? traiteePar;



  @JsonKey(
    
    name: r'obstacles',
    required: false,
    includeIfNull: false,
  )


  final List<ObstacleDto>? obstacles;





    @override
    bool operator ==(Object other) => identical(this, other) || other is DemandeAnonymisationDto &&
      other.id == id &&
      other.type == type &&
      other.personneId == personneId &&
      other.statut == statut &&
      other.motif == motif &&
      other.motifRefus == motifRefus &&
      other.recueLe == recueLe &&
      other.recuePar == recuePar &&
      other.traiteeLe == traiteeLe &&
      other.traiteePar == traiteePar &&
      other.obstacles == obstacles;

    @override
    int get hashCode =>
        id.hashCode +
        type.hashCode +
        personneId.hashCode +
        statut.hashCode +
        motif.hashCode +
        motifRefus.hashCode +
        recueLe.hashCode +
        recuePar.hashCode +
        traiteeLe.hashCode +
        traiteePar.hashCode +
        obstacles.hashCode;

  factory DemandeAnonymisationDto.fromJson(Map<String, dynamic> json) => _$DemandeAnonymisationDtoFromJson(json);

  Map<String, dynamic> toJson() => _$DemandeAnonymisationDtoToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

enum DemandeAnonymisationDtoTypeEnum {
@JsonValue(r'LOCATAIRE')
LOCATAIRE(r'LOCATAIRE'),
@JsonValue(r'GARANT')
GARANT(r'GARANT'),
@JsonValue(r'PROPRIETAIRE')
PROPRIETAIRE(r'PROPRIETAIRE'),
@JsonValue(r'PRESTATAIRE')
PRESTATAIRE(r'PRESTATAIRE');

const DemandeAnonymisationDtoTypeEnum(this.value);

final String value;

@override
String toString() => value;
}


