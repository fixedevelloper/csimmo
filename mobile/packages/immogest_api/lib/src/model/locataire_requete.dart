//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'locataire_requete.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class LocataireRequete {
  /// Returns a new [LocataireRequete] instance.
  LocataireRequete({

    required  this.nom,

     this.profession,

     this.telephone,

     this.email,

     this.adresse,

     this.dateNaissance,

     this.numeroCni,

     this.utilisateurId,

     this.notes,
  });

  @JsonKey(
    
    name: r'nom',
    required: true,
    includeIfNull: false,
  )


  final String nom;



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





    @override
    bool operator ==(Object other) => identical(this, other) || other is LocataireRequete &&
      other.nom == nom &&
      other.profession == profession &&
      other.telephone == telephone &&
      other.email == email &&
      other.adresse == adresse &&
      other.dateNaissance == dateNaissance &&
      other.numeroCni == numeroCni &&
      other.utilisateurId == utilisateurId &&
      other.notes == notes;

    @override
    int get hashCode =>
        nom.hashCode +
        profession.hashCode +
        telephone.hashCode +
        email.hashCode +
        adresse.hashCode +
        dateNaissance.hashCode +
        numeroCni.hashCode +
        utilisateurId.hashCode +
        notes.hashCode;

  factory LocataireRequete.fromJson(Map<String, dynamic> json) => _$LocataireRequeteFromJson(json);

  Map<String, dynamic> toJson() => _$LocataireRequeteToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

