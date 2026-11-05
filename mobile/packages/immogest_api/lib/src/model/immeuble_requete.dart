//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'immeuble_requete.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class ImmeubleRequete {
  /// Returns a new [ImmeubleRequete] instance.
  ImmeubleRequete({

    required  this.nom,

    required  this.adresse,

     this.quartier,

    required  this.ville,

     this.latitude,

     this.longitude,

     this.description,

     this.proprietaireTiersId,
  });

  @JsonKey(
    
    name: r'nom',
    required: true,
    includeIfNull: false,
  )


  final String nom;



  @JsonKey(
    
    name: r'adresse',
    required: true,
    includeIfNull: false,
  )


  final String adresse;



  @JsonKey(
    
    name: r'quartier',
    required: false,
    includeIfNull: false,
  )


  final String? quartier;



  @JsonKey(
    
    name: r'ville',
    required: true,
    includeIfNull: false,
  )


  final String ville;



          // minimum: -90
          // maximum: 90
  @JsonKey(
    
    name: r'latitude',
    required: false,
    includeIfNull: false,
  )


  final num? latitude;



          // minimum: -180
          // maximum: 180
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





    @override
    bool operator ==(Object other) => identical(this, other) || other is ImmeubleRequete &&
      other.nom == nom &&
      other.adresse == adresse &&
      other.quartier == quartier &&
      other.ville == ville &&
      other.latitude == latitude &&
      other.longitude == longitude &&
      other.description == description &&
      other.proprietaireTiersId == proprietaireTiersId;

    @override
    int get hashCode =>
        nom.hashCode +
        adresse.hashCode +
        quartier.hashCode +
        ville.hashCode +
        latitude.hashCode +
        longitude.hashCode +
        description.hashCode +
        proprietaireTiersId.hashCode;

  factory ImmeubleRequete.fromJson(Map<String, dynamic> json) => _$ImmeubleRequeteFromJson(json);

  Map<String, dynamic> toJson() => _$ImmeubleRequeteToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

