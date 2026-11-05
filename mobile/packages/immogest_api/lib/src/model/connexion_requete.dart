//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'connexion_requete.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class ConnexionRequete {
  /// Returns a new [ConnexionRequete] instance.
  ConnexionRequete({

    required  this.identifiant,

    required  this.motDePasse,
  });

  @JsonKey(
    
    name: r'identifiant',
    required: true,
    includeIfNull: false,
  )


  final String identifiant;



  @JsonKey(
    
    name: r'motDePasse',
    required: true,
    includeIfNull: false,
  )


  final String motDePasse;





    @override
    bool operator ==(Object other) => identical(this, other) || other is ConnexionRequete &&
      other.identifiant == identifiant &&
      other.motDePasse == motDePasse;

    @override
    int get hashCode =>
        identifiant.hashCode +
        motDePasse.hashCode;

  factory ConnexionRequete.fromJson(Map<String, dynamic> json) => _$ConnexionRequeteFromJson(json);

  Map<String, dynamic> toJson() => _$ConnexionRequeteToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

