//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:immogest_api/src/model/compteur_dto.dart';
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'lot_dto.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class LotDto {
  /// Returns a new [LotDto] instance.
  LotDto({

     this.id,

     this.immeubleId,

     this.immeubleNom,

     this.ville,

     this.quartier,

     this.reference,

     this.type,

     this.surfaceM2,

     this.pieces,

     this.loyerIndicatif,

     this.statut,

     this.equipements,

     this.compteurs,

     this.archive,
  });

  @JsonKey(
    
    name: r'id',
    required: false,
    includeIfNull: false,
  )


  final String? id;



  @JsonKey(
    
    name: r'immeubleId',
    required: false,
    includeIfNull: false,
  )


  final String? immeubleId;



  @JsonKey(
    
    name: r'immeubleNom',
    required: false,
    includeIfNull: false,
  )


  final String? immeubleNom;



  @JsonKey(
    
    name: r'ville',
    required: false,
    includeIfNull: false,
  )


  final String? ville;



  @JsonKey(
    
    name: r'quartier',
    required: false,
    includeIfNull: false,
  )


  final String? quartier;



  @JsonKey(
    
    name: r'reference',
    required: false,
    includeIfNull: false,
  )


  final String? reference;



  @JsonKey(
    
    name: r'type',
    required: false,
    includeIfNull: false,
  )


  final LotDtoTypeEnum? type;



  @JsonKey(
    
    name: r'surfaceM2',
    required: false,
    includeIfNull: false,
  )


  final num? surfaceM2;



  @JsonKey(
    
    name: r'pieces',
    required: false,
    includeIfNull: false,
  )


  final int? pieces;



  @JsonKey(
    
    name: r'loyerIndicatif',
    required: false,
    includeIfNull: false,
  )


  final int? loyerIndicatif;



  @JsonKey(
    
    name: r'statut',
    required: false,
    includeIfNull: false,
  )


  final LotDtoStatutEnum? statut;



  @JsonKey(
    
    name: r'equipements',
    required: false,
    includeIfNull: false,
  )


  final List<String>? equipements;



  @JsonKey(
    
    name: r'compteurs',
    required: false,
    includeIfNull: false,
  )


  final List<CompteurDto>? compteurs;



  @JsonKey(
    
    name: r'archive',
    required: false,
    includeIfNull: false,
  )


  final bool? archive;





    @override
    bool operator ==(Object other) => identical(this, other) || other is LotDto &&
      other.id == id &&
      other.immeubleId == immeubleId &&
      other.immeubleNom == immeubleNom &&
      other.ville == ville &&
      other.quartier == quartier &&
      other.reference == reference &&
      other.type == type &&
      other.surfaceM2 == surfaceM2 &&
      other.pieces == pieces &&
      other.loyerIndicatif == loyerIndicatif &&
      other.statut == statut &&
      other.equipements == equipements &&
      other.compteurs == compteurs &&
      other.archive == archive;

    @override
    int get hashCode =>
        id.hashCode +
        immeubleId.hashCode +
        immeubleNom.hashCode +
        ville.hashCode +
        quartier.hashCode +
        reference.hashCode +
        type.hashCode +
        surfaceM2.hashCode +
        pieces.hashCode +
        loyerIndicatif.hashCode +
        statut.hashCode +
        equipements.hashCode +
        compteurs.hashCode +
        archive.hashCode;

  factory LotDto.fromJson(Map<String, dynamic> json) => _$LotDtoFromJson(json);

  Map<String, dynamic> toJson() => _$LotDtoToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

enum LotDtoTypeEnum {
@JsonValue(r'APPARTEMENT')
APPARTEMENT(r'APPARTEMENT'),
@JsonValue(r'STUDIO')
STUDIO(r'STUDIO'),
@JsonValue(r'BOUTIQUE')
BOUTIQUE(r'BOUTIQUE'),
@JsonValue(r'BUREAU')
BUREAU(r'BUREAU'),
@JsonValue(r'TERRAIN')
TERRAIN(r'TERRAIN');

const LotDtoTypeEnum(this.value);

final String value;

@override
String toString() => value;
}


enum LotDtoStatutEnum {
@JsonValue(r'LIBRE')
LIBRE(r'LIBRE'),
@JsonValue(r'OCCUPE')
OCCUPE(r'OCCUPE'),
@JsonValue(r'EN_TRAVAUX')
EN_TRAVAUX(r'EN_TRAVAUX');

const LotDtoStatutEnum(this.value);

final String value;

@override
String toString() => value;
}


