//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'creation_requete.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class CreationRequete {
  /// Returns a new [CreationRequete] instance.
  CreationRequete({

    required  this.nom,

     this.telephone,

     this.email,

    required  this.role,

     this.deuxFacteursActif,

    required  this.motDePasse,
  });

  @JsonKey(
    
    name: r'nom',
    required: true,
    includeIfNull: false,
  )


  final String nom;



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
    required: true,
    includeIfNull: false,
  )


  final CreationRequeteRoleEnum role;



  @JsonKey(
    
    name: r'deuxFacteursActif',
    required: false,
    includeIfNull: false,
  )


  final bool? deuxFacteursActif;



  @JsonKey(
    
    name: r'motDePasse',
    required: true,
    includeIfNull: false,
  )


  final String motDePasse;





    @override
    bool operator ==(Object other) => identical(this, other) || other is CreationRequete &&
      other.nom == nom &&
      other.telephone == telephone &&
      other.email == email &&
      other.role == role &&
      other.deuxFacteursActif == deuxFacteursActif &&
      other.motDePasse == motDePasse;

    @override
    int get hashCode =>
        nom.hashCode +
        telephone.hashCode +
        email.hashCode +
        role.hashCode +
        deuxFacteursActif.hashCode +
        motDePasse.hashCode;

  factory CreationRequete.fromJson(Map<String, dynamic> json) => _$CreationRequeteFromJson(json);

  Map<String, dynamic> toJson() => _$CreationRequeteToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

enum CreationRequeteRoleEnum {
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
ENCAISSEUR(r'ENCAISSEUR');

const CreationRequeteRoleEnum(this.value);

final String value;

@override
String toString() => value;
}


