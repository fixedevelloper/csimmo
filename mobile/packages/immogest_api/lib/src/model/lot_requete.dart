//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:immogest_api/src/model/compteur_requete.dart';
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'lot_requete.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class LotRequete {
  /// Returns a new [LotRequete] instance.
  LotRequete({

     this.immeubleId,

    required  this.reference,

    required  this.type,

     this.surfaceM2,

     this.pieces,

     this.loyerIndicatif,

     this.equipements,

     this.compteurs,
  });

  @JsonKey(
    
    name: r'immeubleId',
    required: false,
    includeIfNull: false,
  )


  final String? immeubleId;



  @JsonKey(
    
    name: r'reference',
    required: true,
    includeIfNull: false,
  )


  final String reference;



  @JsonKey(
    
    name: r'type',
    required: true,
    includeIfNull: false,
  )


  final LotRequeteTypeEnum type;



          // minimum: 0
  @JsonKey(
    
    name: r'surfaceM2',
    required: false,
    includeIfNull: false,
  )


  final num? surfaceM2;



          // minimum: 0
          // maximum: 100
  @JsonKey(
    
    name: r'pieces',
    required: false,
    includeIfNull: false,
  )


  final int? pieces;



          // minimum: 0
  @JsonKey(
    
    name: r'loyerIndicatif',
    required: false,
    includeIfNull: false,
  )


  final int? loyerIndicatif;



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


  final List<CompteurRequete>? compteurs;





    @override
    bool operator ==(Object other) => identical(this, other) || other is LotRequete &&
      other.immeubleId == immeubleId &&
      other.reference == reference &&
      other.type == type &&
      other.surfaceM2 == surfaceM2 &&
      other.pieces == pieces &&
      other.loyerIndicatif == loyerIndicatif &&
      other.equipements == equipements &&
      other.compteurs == compteurs;

    @override
    int get hashCode =>
        immeubleId.hashCode +
        reference.hashCode +
        type.hashCode +
        surfaceM2.hashCode +
        pieces.hashCode +
        loyerIndicatif.hashCode +
        equipements.hashCode +
        compteurs.hashCode;

  factory LotRequete.fromJson(Map<String, dynamic> json) => _$LotRequeteFromJson(json);

  Map<String, dynamic> toJson() => _$LotRequeteToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

enum LotRequeteTypeEnum {
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

const LotRequeteTypeEnum(this.value);

final String value;

@override
String toString() => value;
}


