//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'traitement_dto.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class TraitementDto {
  /// Returns a new [TraitementDto] instance.
  TraitementDto({

     this.id,

     this.nom,

     this.finalite,

     this.baseLegale,

     this.donnees,

     this.personnes,

     this.destinataires,

     this.conservation,

     this.mesures,
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
    
    name: r'finalite',
    required: false,
    includeIfNull: false,
  )


  final String? finalite;



  @JsonKey(
    
    name: r'baseLegale',
    required: false,
    includeIfNull: false,
  )


  final String? baseLegale;



  @JsonKey(
    
    name: r'donnees',
    required: false,
    includeIfNull: false,
  )


  final List<String>? donnees;



  @JsonKey(
    
    name: r'personnes',
    required: false,
    includeIfNull: false,
  )


  final List<String>? personnes;



  @JsonKey(
    
    name: r'destinataires',
    required: false,
    includeIfNull: false,
  )


  final List<String>? destinataires;



  @JsonKey(
    
    name: r'conservation',
    required: false,
    includeIfNull: false,
  )


  final String? conservation;



  @JsonKey(
    
    name: r'mesures',
    required: false,
    includeIfNull: false,
  )


  final List<String>? mesures;





    @override
    bool operator ==(Object other) => identical(this, other) || other is TraitementDto &&
      other.id == id &&
      other.nom == nom &&
      other.finalite == finalite &&
      other.baseLegale == baseLegale &&
      other.donnees == donnees &&
      other.personnes == personnes &&
      other.destinataires == destinataires &&
      other.conservation == conservation &&
      other.mesures == mesures;

    @override
    int get hashCode =>
        id.hashCode +
        nom.hashCode +
        finalite.hashCode +
        baseLegale.hashCode +
        donnees.hashCode +
        personnes.hashCode +
        destinataires.hashCode +
        conservation.hashCode +
        mesures.hashCode;

  factory TraitementDto.fromJson(Map<String, dynamic> json) => _$TraitementDtoFromJson(json);

  Map<String, dynamic> toJson() => _$TraitementDtoToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

