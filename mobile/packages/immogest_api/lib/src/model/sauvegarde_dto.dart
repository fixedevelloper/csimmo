//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'sauvegarde_dto.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class SauvegardeDto {
  /// Returns a new [SauvegardeDto] instance.
  SauvegardeDto({

     this.id,

     this.date,

     this.version,

     this.etiquette,

     this.tailleOctets,

     this.objets,

     this.externe,

     this.restaurable,
  });

  @JsonKey(
    
    name: r'id',
    required: false,
    includeIfNull: false,
  )


  final String? id;



  @JsonKey(
    
    name: r'date',
    required: false,
    includeIfNull: false,
  )


  final DateTime? date;



  @JsonKey(
    
    name: r'version',
    required: false,
    includeIfNull: false,
  )


  final String? version;



  @JsonKey(
    
    name: r'etiquette',
    required: false,
    includeIfNull: false,
  )


  final String? etiquette;



  @JsonKey(
    
    name: r'tailleOctets',
    required: false,
    includeIfNull: false,
  )


  final int? tailleOctets;



  @JsonKey(
    
    name: r'objets',
    required: false,
    includeIfNull: false,
  )


  final int? objets;



  @JsonKey(
    
    name: r'externe',
    required: false,
    includeIfNull: false,
  )


  final String? externe;



  @JsonKey(
    
    name: r'restaurable',
    required: false,
    includeIfNull: false,
  )


  final bool? restaurable;





    @override
    bool operator ==(Object other) => identical(this, other) || other is SauvegardeDto &&
      other.id == id &&
      other.date == date &&
      other.version == version &&
      other.etiquette == etiquette &&
      other.tailleOctets == tailleOctets &&
      other.objets == objets &&
      other.externe == externe &&
      other.restaurable == restaurable;

    @override
    int get hashCode =>
        id.hashCode +
        date.hashCode +
        version.hashCode +
        etiquette.hashCode +
        tailleOctets.hashCode +
        objets.hashCode +
        externe.hashCode +
        restaurable.hashCode;

  factory SauvegardeDto.fromJson(Map<String, dynamic> json) => _$SauvegardeDtoFromJson(json);

  Map<String, dynamic> toJson() => _$SauvegardeDtoToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

