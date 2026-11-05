//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'signalement_dto.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class SignalementDto {
  /// Returns a new [SignalementDto] instance.
  SignalementDto({

     this.id,

     this.bailId,

     this.bailNumero,

     this.lotId,

     this.logement,

     this.locataireId,

     this.locataireNom,

     this.description,

     this.statut,

     this.photoFichierId,

     this.noteGestion,

     this.creeLe,
  });

  @JsonKey(
    
    name: r'id',
    required: false,
    includeIfNull: false,
  )


  final String? id;



  @JsonKey(
    
    name: r'bailId',
    required: false,
    includeIfNull: false,
  )


  final String? bailId;



  @JsonKey(
    
    name: r'bailNumero',
    required: false,
    includeIfNull: false,
  )


  final String? bailNumero;



  @JsonKey(
    
    name: r'lotId',
    required: false,
    includeIfNull: false,
  )


  final String? lotId;



  @JsonKey(
    
    name: r'logement',
    required: false,
    includeIfNull: false,
  )


  final String? logement;



  @JsonKey(
    
    name: r'locataireId',
    required: false,
    includeIfNull: false,
  )


  final String? locataireId;



  @JsonKey(
    
    name: r'locataireNom',
    required: false,
    includeIfNull: false,
  )


  final String? locataireNom;



  @JsonKey(
    
    name: r'description',
    required: false,
    includeIfNull: false,
  )


  final String? description;



  @JsonKey(
    
    name: r'statut',
    required: false,
    includeIfNull: false,
  )


  final SignalementDtoStatutEnum? statut;



  @JsonKey(
    
    name: r'photoFichierId',
    required: false,
    includeIfNull: false,
  )


  final String? photoFichierId;



  @JsonKey(
    
    name: r'noteGestion',
    required: false,
    includeIfNull: false,
  )


  final String? noteGestion;



  @JsonKey(
    
    name: r'creeLe',
    required: false,
    includeIfNull: false,
  )


  final DateTime? creeLe;





    @override
    bool operator ==(Object other) => identical(this, other) || other is SignalementDto &&
      other.id == id &&
      other.bailId == bailId &&
      other.bailNumero == bailNumero &&
      other.lotId == lotId &&
      other.logement == logement &&
      other.locataireId == locataireId &&
      other.locataireNom == locataireNom &&
      other.description == description &&
      other.statut == statut &&
      other.photoFichierId == photoFichierId &&
      other.noteGestion == noteGestion &&
      other.creeLe == creeLe;

    @override
    int get hashCode =>
        id.hashCode +
        bailId.hashCode +
        bailNumero.hashCode +
        lotId.hashCode +
        logement.hashCode +
        locataireId.hashCode +
        locataireNom.hashCode +
        description.hashCode +
        statut.hashCode +
        photoFichierId.hashCode +
        noteGestion.hashCode +
        creeLe.hashCode;

  factory SignalementDto.fromJson(Map<String, dynamic> json) => _$SignalementDtoFromJson(json);

  Map<String, dynamic> toJson() => _$SignalementDtoToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

enum SignalementDtoStatutEnum {
@JsonValue(r'NOUVEAU')
NOUVEAU(r'NOUVEAU'),
@JsonValue(r'EN_COURS')
EN_COURS(r'EN_COURS'),
@JsonValue(r'RESOLU')
RESOLU(r'RESOLU'),
@JsonValue(r'REJETE')
REJETE(r'REJETE');

const SignalementDtoStatutEnum(this.value);

final String value;

@override
String toString() => value;
}


