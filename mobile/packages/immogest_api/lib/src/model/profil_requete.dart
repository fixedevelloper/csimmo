//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'profil_requete.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class ProfilRequete {
  /// Returns a new [ProfilRequete] instance.
  ProfilRequete({

    required  this.nom,

     this.telephone,

     this.email,

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
    
    name: r'motDePasse',
    required: true,
    includeIfNull: false,
  )


  final String motDePasse;





    @override
    bool operator ==(Object other) => identical(this, other) || other is ProfilRequete &&
      other.nom == nom &&
      other.telephone == telephone &&
      other.email == email &&
      other.motDePasse == motDePasse;

    @override
    int get hashCode =>
        nom.hashCode +
        telephone.hashCode +
        email.hashCode +
        motDePasse.hashCode;

  factory ProfilRequete.fromJson(Map<String, dynamic> json) => _$ProfilRequeteFromJson(json);

  Map<String, dynamic> toJson() => _$ProfilRequeteToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

