//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'prestataire_requete.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class PrestataireRequete {
  /// Returns a new [PrestataireRequete] instance.
  PrestataireRequete({

    required  this.nom,

     this.metier,

     this.telephone,

     this.email,

     this.adresse,

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





    @override
    bool operator ==(Object other) => identical(this, other) || other is PrestataireRequete &&
      other.nom == nom &&
      other.metier == metier &&
      other.telephone == telephone &&
      other.email == email &&
      other.adresse == adresse &&
      other.utilisateurId == utilisateurId &&
      other.notes == notes;

    @override
    int get hashCode =>
        nom.hashCode +
        metier.hashCode +
        telephone.hashCode +
        email.hashCode +
        adresse.hashCode +
        utilisateurId.hashCode +
        notes.hashCode;

  factory PrestataireRequete.fromJson(Map<String, dynamic> json) => _$PrestataireRequeteFromJson(json);

  Map<String, dynamic> toJson() => _$PrestataireRequeteToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

