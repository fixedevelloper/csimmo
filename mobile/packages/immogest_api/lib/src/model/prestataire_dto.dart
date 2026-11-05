//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'prestataire_dto.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class PrestataireDto {
  /// Returns a new [PrestataireDto] instance.
  PrestataireDto({

     this.id,

     this.nom,

     this.metier,

     this.telephone,

     this.email,

     this.adresse,

     this.utilisateurId,

     this.notes,

     this.archive,
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
    
    name: r'metier',
    required: false,
    includeIfNull: false,
  )


  final String? metier;



  @JsonKey(
    
    name: r'telephone',
    required: false,
    includeIfNull: false,
  )


  final String? telephone;



  @JsonKey(
    
    name: r'email',
    required: false,
    includeIfNull: false,
  )


  final String? email;



  @JsonKey(
    
    name: r'adresse',
    required: false,
    includeIfNull: false,
  )


  final String? adresse;



  @JsonKey(
    
    name: r'utilisateurId',
    required: false,
    includeIfNull: false,
  )


  final String? utilisateurId;



  @JsonKey(
    
    name: r'notes',
    required: false,
    includeIfNull: false,
  )


  final String? notes;



  @JsonKey(
    
    name: r'archive',
    required: false,
    includeIfNull: false,
  )


  final bool? archive;





    @override
    bool operator ==(Object other) => identical(this, other) || other is PrestataireDto &&
      other.id == id &&
      other.nom == nom &&
      other.metier == metier &&
      other.telephone == telephone &&
      other.email == email &&
      other.adresse == adresse &&
      other.utilisateurId == utilisateurId &&
      other.notes == notes &&
      other.archive == archive;

    @override
    int get hashCode =>
        id.hashCode +
        nom.hashCode +
        metier.hashCode +
        telephone.hashCode +
        email.hashCode +
        adresse.hashCode +
        utilisateurId.hashCode +
        notes.hashCode +
        archive.hashCode;

  factory PrestataireDto.fromJson(Map<String, dynamic> json) => _$PrestataireDtoFromJson(json);

  Map<String, dynamic> toJson() => _$PrestataireDtoToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

