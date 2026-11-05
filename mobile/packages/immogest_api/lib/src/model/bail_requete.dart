//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'bail_requete.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class BailRequete {
  /// Returns a new [BailRequete] instance.
  BailRequete({

    required  this.lotId,

    required  this.locataireId,

     this.garantIds,

    required  this.dateDebut,

     this.dateFin,

    required  this.loyer,

     this.charges,

    required  this.periodicite,

    required  this.jourEcheance,

     this.caution,

     this.moisAvance,

     this.preavisMois,
  });

  @JsonKey(
    
    name: r'lotId',
    required: true,
    includeIfNull: false,
  )


  final String lotId;



  @JsonKey(
    
    name: r'locataireId',
    required: true,
    includeIfNull: false,
  )


  final String locataireId;



  @JsonKey(
    
    name: r'garantIds',
    required: false,
    includeIfNull: false,
  )


  final List<String>? garantIds;



  @JsonKey(
    
    name: r'dateDebut',
    required: true,
    includeIfNull: false,
  )


  final DateTime dateDebut;



  @JsonKey(
    
    name: r'dateFin',
    required: false,
    includeIfNull: false,
  )


  final DateTime? dateFin;



          // minimum: 1
  @JsonKey(
    
    name: r'loyer',
    required: true,
    includeIfNull: false,
  )


  final int loyer;



          // minimum: 0
  @JsonKey(
    
    name: r'charges',
    required: false,
    includeIfNull: false,
  )


  final int? charges;



  @JsonKey(
    
    name: r'periodicite',
    required: true,
    includeIfNull: false,
  )


  final BailRequetePeriodiciteEnum periodicite;



          // minimum: 1
          // maximum: 31
  @JsonKey(
    
    name: r'jourEcheance',
    required: true,
    includeIfNull: false,
  )


  final int jourEcheance;



          // minimum: 0
  @JsonKey(
    
    name: r'caution',
    required: false,
    includeIfNull: false,
  )


  final int? caution;



          // minimum: 0
          // maximum: 24
  @JsonKey(
    
    name: r'moisAvance',
    required: false,
    includeIfNull: false,
  )


  final int? moisAvance;



          // minimum: 0
          // maximum: 12
  @JsonKey(
    
    name: r'preavisMois',
    required: false,
    includeIfNull: false,
  )


  final int? preavisMois;





    @override
    bool operator ==(Object other) => identical(this, other) || other is BailRequete &&
      other.lotId == lotId &&
      other.locataireId == locataireId &&
      other.garantIds == garantIds &&
      other.dateDebut == dateDebut &&
      other.dateFin == dateFin &&
      other.loyer == loyer &&
      other.charges == charges &&
      other.periodicite == periodicite &&
      other.jourEcheance == jourEcheance &&
      other.caution == caution &&
      other.moisAvance == moisAvance &&
      other.preavisMois == preavisMois;

    @override
    int get hashCode =>
        lotId.hashCode +
        locataireId.hashCode +
        garantIds.hashCode +
        dateDebut.hashCode +
        dateFin.hashCode +
        loyer.hashCode +
        charges.hashCode +
        periodicite.hashCode +
        jourEcheance.hashCode +
        caution.hashCode +
        moisAvance.hashCode +
        preavisMois.hashCode;

  factory BailRequete.fromJson(Map<String, dynamic> json) => _$BailRequeteFromJson(json);

  Map<String, dynamic> toJson() => _$BailRequeteToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

enum BailRequetePeriodiciteEnum {
@JsonValue(r'MENSUELLE')
MENSUELLE(r'MENSUELLE'),
@JsonValue(r'TRIMESTRIELLE')
TRIMESTRIELLE(r'TRIMESTRIELLE'),
@JsonValue(r'ANNUELLE')
ANNUELLE(r'ANNUELLE');

const BailRequetePeriodiciteEnum(this.value);

final String value;

@override
String toString() => value;
}


