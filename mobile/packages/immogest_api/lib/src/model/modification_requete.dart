//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'modification_requete.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class ModificationRequete {
  /// Returns a new [ModificationRequete] instance.
  ModificationRequete({

    required  this.nom,

     this.telephone,

     this.email,

    required  this.role,

     this.actif,

     this.deuxFacteursActif,
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


  final ModificationRequeteRoleEnum role;



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





    @override
    bool operator ==(Object other) => identical(this, other) || other is ModificationRequete &&
      other.nom == nom &&
      other.telephone == telephone &&
      other.email == email &&
      other.role == role &&
      other.actif == actif &&
      other.deuxFacteursActif == deuxFacteursActif;

    @override
    int get hashCode =>
        nom.hashCode +
        telephone.hashCode +
        email.hashCode +
        role.hashCode +
        actif.hashCode +
        deuxFacteursActif.hashCode;

  factory ModificationRequete.fromJson(Map<String, dynamic> json) => _$ModificationRequeteFromJson(json);

  Map<String, dynamic> toJson() => _$ModificationRequeteToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

enum ModificationRequeteRoleEnum {
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

const ModificationRequeteRoleEnum(this.value);

final String value;

@override
String toString() => value;
}


