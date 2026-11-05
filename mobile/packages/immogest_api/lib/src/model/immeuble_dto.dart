//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'immeuble_dto.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class ImmeubleDto {
  /// Returns a new [ImmeubleDto] instance.
  ImmeubleDto({

     this.id,

     this.nom,

     this.adresse,

     this.quartier,

     this.ville,

     this.latitude,

     this.longitude,

     this.description,

     this.proprietaireTiersId,

     this.archive,

     this.nombreLots,

     this.lotsLibres,

     this.lotsOccupes,

     this.lotsEnTravaux,
  });

  @JsonKey(
    
    name: r'id',
    required: false,
    includeIfNull: false,
  )


  final String? id;



  @JsonKey(
    
    name: r'nom',
    required: false,
    includeIfNull: false,
  )


  final String? nom;



  @JsonKey(
    
    name: r'adresse',
    required: false,
    includeIfNull: false,
  )


  final String? adresse;



  @JsonKey(
    
    name: r'quartier',
    required: false,
    includeIfNull: false,
  )


  final String? quartier;



  @JsonKey(
    
    name: r'ville',
    required: false,
    includeIfNull: false,
  )


  final String? ville;



  @JsonKey(
    
    name: r'latitude',
    required: false,
    includeIfNull: false,
  )


  final num? latitude;



  @JsonKey(
    
    name: r'longitude',
    required: false,
    includeIfNull: false,
  )


  final num? longitude;



  @JsonKey(
    
    name: r'description',
    required: false,
    includeIfNull: false,
  )


  final String? description;



  @JsonKey(
    
    name: r'proprietaireTiersId',
    required: false,
    includeIfNull: false,
  )


  final String? proprietaireTiersId;



  @JsonKey(
    
    name: r'archive',
    required: false,
    includeIfNull: false,
  )


  final bool? archive;



  @JsonKey(
    
    name: r'nombreLots',
    required: false,
    includeIfNull: false,
  )


  final int? nombreLots;



  @JsonKey(
    
    name: r'lotsLibres',
    required: false,
    includeIfNull: false,
  )


  final int? lotsLibres;



  @JsonKey(
    
    name: r'lotsOccupes',
    required: false,
    includeIfNull: false,
  )


  final int? lotsOccupes;



  @JsonKey(
    
    name: r'lotsEnTravaux',
    required: false,
    includeIfNull: false,
  )


  final int? lotsEnTravaux;





    @override
    bool operator ==(Object other) => identical(this, other) || other is ImmeubleDto &&
      other.id == id &&
      other.nom == nom &&
      other.adresse == adresse &&
      other.quartier == quartier &&
      other.ville == ville &&
      other.latitude == latitude &&
      other.longitude == longitude &&
      other.description == description &&
      other.proprietaireTiersId == proprietaireTiersId &&
      other.archive == archive &&
      other.nombreLots == nombreLots &&
      other.lotsLibres == lotsLibres &&
      other.lotsOccupes == lotsOccupes &&
      other.lotsEnTravaux == lotsEnTravaux;

    @override
    int get hashCode =>
        id.hashCode +
        nom.hashCode +
        adresse.hashCode +
        quartier.hashCode +
        ville.hashCode +
        latitude.hashCode +
        longitude.hashCode +
        description.hashCode +
        proprietaireTiersId.hashCode +
        archive.hashCode +
        nombreLots.hashCode +
        lotsLibres.hashCode +
        lotsOccupes.hashCode +
        lotsEnTravaux.hashCode;

  factory ImmeubleDto.fromJson(Map<String, dynamic> json) => _$ImmeubleDtoFromJson(json);

  Map<String, dynamic> toJson() => _$ImmeubleDtoToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

