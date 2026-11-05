//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'locataire_dto.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class LocataireDto {
  /// Returns a new [LocataireDto] instance.
  LocataireDto({

     this.id,

     this.nom,

     this.profession,

     this.telephone,

     this.email,

     this.adresse,

     this.dateNaissance,

     this.numeroCni,

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
    
    name: r'profession',
    required: false,
    includeIfNull: false,
  )


  final String? profession;



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
    
    name: r'dateNaissance',
    required: false,
    includeIfNull: false,
  )


  final DateTime? dateNaissance;



  @JsonKey(
    
    name: r'numeroCni',
    required: false,
    includeIfNull: false,
  )


  final String? numeroCni;



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
    bool operator ==(Object other) => identical(this, other) || other is LocataireDto &&
      other.id == id &&
      other.nom == nom &&
      other.profession == profession &&
      other.telephone == telephone &&
      other.email == email &&
      other.adresse == adresse &&
      other.dateNaissance == dateNaissance &&
      other.numeroCni == numeroCni &&
      other.utilisateurId == utilisateurId &&
      other.notes == notes &&
      other.archive == archive;

    @override
    int get hashCode =>
        id.hashCode +
        nom.hashCode +
        profession.hashCode +
        telephone.hashCode +
        email.hashCode +
        adresse.hashCode +
        dateNaissance.hashCode +
        numeroCni.hashCode +
        utilisateurId.hashCode +
        notes.hashCode +
        archive.hashCode;

  factory LocataireDto.fromJson(Map<String, dynamic> json) => _$LocataireDtoFromJson(json);

  Map<String, dynamic> toJson() => _$LocataireDtoToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

