//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:immogest_api/src/model/garant_resume.dart';
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'bail_dto.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class BailDto {
  /// Returns a new [BailDto] instance.
  BailDto({

     this.id,

     this.numero,

     this.lotId,

     this.lotReference,

     this.immeubleNom,

     this.locataireId,

     this.locataireNom,

     this.bailPrecedentId,

     this.statut,

     this.dateDebut,

     this.dateFin,

     this.loyer,

     this.charges,

     this.periodicite,

     this.jourEcheance,

     this.caution,

     this.moisAvance,

     this.preavisMois,

     this.dateNotificationPreavis,

     this.dateSortie,

     this.motifResiliation,

     this.dateFinEffective,

     this.garants,
  });

  @JsonKey(
    
    name: r'id',
    required: false,
    includeIfNull: false,
  )


  final String? id;



  @JsonKey(
    
    name: r'numero',
    required: false,
    includeIfNull: false,
  )


  final String? numero;



  @JsonKey(
    
    name: r'lotId',
    required: false,
    includeIfNull: false,
  )


  final String? lotId;



  @JsonKey(
    
    name: r'lotReference',
    required: false,
    includeIfNull: false,
  )


  final String? lotReference;



  @JsonKey(
    
    name: r'immeubleNom',
    required: false,
    includeIfNull: false,
  )


  final String? immeubleNom;



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
    
    name: r'bailPrecedentId',
    required: false,
    includeIfNull: false,
  )


  final String? bailPrecedentId;



  @JsonKey(
    
    name: r'statut',
    required: false,
    includeIfNull: false,
  )


  final BailDtoStatutEnum? statut;



  @JsonKey(
    
    name: r'dateDebut',
    required: false,
    includeIfNull: false,
  )


  final DateTime? dateDebut;



  @JsonKey(
    
    name: r'dateFin',
    required: false,
    includeIfNull: false,
  )


  final DateTime? dateFin;



  @JsonKey(
    
    name: r'loyer',
    required: false,
    includeIfNull: false,
  )


  final int? loyer;



  @JsonKey(
    
    name: r'charges',
    required: false,
    includeIfNull: false,
  )


  final int? charges;



  @JsonKey(
    
    name: r'periodicite',
    required: false,
    includeIfNull: false,
  )


  final BailDtoPeriodiciteEnum? periodicite;



  @JsonKey(
    
    name: r'jourEcheance',
    required: false,
    includeIfNull: false,
  )


  final int? jourEcheance;



  @JsonKey(
    
    name: r'caution',
    required: false,
    includeIfNull: false,
  )


  final int? caution;



  @JsonKey(
    
    name: r'moisAvance',
    required: false,
    includeIfNull: false,
  )


  final int? moisAvance;



  @JsonKey(
    
    name: r'preavisMois',
    required: false,
    includeIfNull: false,
  )


  final int? preavisMois;



  @JsonKey(
    
    name: r'dateNotificationPreavis',
    required: false,
    includeIfNull: false,
  )


  final DateTime? dateNotificationPreavis;



  @JsonKey(
    
    name: r'dateSortie',
    required: false,
    includeIfNull: false,
  )


  final DateTime? dateSortie;



  @JsonKey(
    
    name: r'motifResiliation',
    required: false,
    includeIfNull: false,
  )


  final String? motifResiliation;



  @JsonKey(
    
    name: r'dateFinEffective',
    required: false,
    includeIfNull: false,
  )


  final DateTime? dateFinEffective;



  @JsonKey(
    
    name: r'garants',
    required: false,
    includeIfNull: false,
  )


  final List<GarantResume>? garants;





    @override
    bool operator ==(Object other) => identical(this, other) || other is BailDto &&
      other.id == id &&
      other.numero == numero &&
      other.lotId == lotId &&
      other.lotReference == lotReference &&
      other.immeubleNom == immeubleNom &&
      other.locataireId == locataireId &&
      other.locataireNom == locataireNom &&
      other.bailPrecedentId == bailPrecedentId &&
      other.statut == statut &&
      other.dateDebut == dateDebut &&
      other.dateFin == dateFin &&
      other.loyer == loyer &&
      other.charges == charges &&
      other.periodicite == periodicite &&
      other.jourEcheance == jourEcheance &&
      other.caution == caution &&
      other.moisAvance == moisAvance &&
      other.preavisMois == preavisMois &&
      other.dateNotificationPreavis == dateNotificationPreavis &&
      other.dateSortie == dateSortie &&
      other.motifResiliation == motifResiliation &&
      other.dateFinEffective == dateFinEffective &&
      other.garants == garants;

    @override
    int get hashCode =>
        id.hashCode +
        numero.hashCode +
        lotId.hashCode +
        lotReference.hashCode +
        immeubleNom.hashCode +
        locataireId.hashCode +
        locataireNom.hashCode +
        bailPrecedentId.hashCode +
        statut.hashCode +
        dateDebut.hashCode +
        dateFin.hashCode +
        loyer.hashCode +
        charges.hashCode +
        periodicite.hashCode +
        jourEcheance.hashCode +
        caution.hashCode +
        moisAvance.hashCode +
        preavisMois.hashCode +
        dateNotificationPreavis.hashCode +
        dateSortie.hashCode +
        motifResiliation.hashCode +
        dateFinEffective.hashCode +
        garants.hashCode;

  factory BailDto.fromJson(Map<String, dynamic> json) => _$BailDtoFromJson(json);

  Map<String, dynamic> toJson() => _$BailDtoToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

enum BailDtoStatutEnum {
@JsonValue(r'BROUILLON')
BROUILLON(r'BROUILLON'),
@JsonValue(r'ACTIF')
ACTIF(r'ACTIF'),
@JsonValue(r'RESILIE')
RESILIE(r'RESILIE'),
@JsonValue(r'TERMINE')
TERMINE(r'TERMINE');

const BailDtoStatutEnum(this.value);

final String value;

@override
String toString() => value;
}


enum BailDtoPeriodiciteEnum {
@JsonValue(r'MENSUELLE')
MENSUELLE(r'MENSUELLE'),
@JsonValue(r'TRIMESTRIELLE')
TRIMESTRIELLE(r'TRIMESTRIELLE'),
@JsonValue(r'ANNUELLE')
ANNUELLE(r'ANNUELLE');

const BailDtoPeriodiciteEnum(this.value);

final String value;

@override
String toString() => value;
}


