//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'utilisateur_dto.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class UtilisateurDto {
  /// Returns a new [UtilisateurDto] instance.
  UtilisateurDto({

     this.id,

     this.nom,

     this.telephone,

     this.email,

     this.role,

     this.actif,

     this.deuxFacteursActif,

     this.verrouille,
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
    
    name: r'role',
    required: false,
    includeIfNull: false,
  )


  final UtilisateurDtoRoleEnum? role;



  @JsonKey(
    
    name: r'actif',
    required: false,
    includeIfNull: false,
  )


  final bool? actif;



  @JsonKey(
    
    name: r'deuxFacteursActif',
    required: false,
    includeIfNull: false,
  )


  final bool? deuxFacteursActif;



  @JsonKey(
    
    name: r'verrouille',
    required: false,
    includeIfNull: false,
  )


  final bool? verrouille;





    @override
    bool operator ==(Object other) => identical(this, other) || other is UtilisateurDto &&
      other.id == id &&
      other.nom == nom &&
      other.telephone == telephone &&
      other.email == email &&
      other.role == role &&
      other.actif == actif &&
      other.deuxFacteursActif == deuxFacteursActif &&
      other.verrouille == verrouille;

    @override
    int get hashCode =>
        id.hashCode +
        nom.hashCode +
        telephone.hashCode +
        email.hashCode +
        role.hashCode +
        actif.hashCode +
        deuxFacteursActif.hashCode +
        verrouille.hashCode;

  factory UtilisateurDto.fromJson(Map<String, dynamic> json) => _$UtilisateurDtoFromJson(json);

  Map<String, dynamic> toJson() => _$UtilisateurDtoToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

enum UtilisateurDtoRoleEnum {
@JsonValue(r'ADMIN')
ADMIN(r'ADMIN'),
@JsonValue(r'GESTIONNAIRE')
GESTIONNAIRE(r'GESTIONNAIRE'),
@JsonValue(r'PROPRIETAIRE_TIERS')
PROPRIETAIRE_TIERS(r'PROPRIETAIRE_TIERS'),
@JsonValue(r'LOCATAIRE')
LOCATAIRE(r'LOCATAIRE'),
@JsonValue(r'PRESTATAIRE')
PRESTATAIRE(r'PRESTATAIRE'),
@JsonValue(r'ENCAISSEUR')
ENCAISSEUR(r'ENCAISSEUR'),
@JsonValue(r'GESTIONNAIRE_BIENS')
GESTIONNAIRE_BIENS(r'GESTIONNAIRE_BIENS');

const UtilisateurDtoRoleEnum(this.value);

final String value;

@override
String toString() => value;
}


