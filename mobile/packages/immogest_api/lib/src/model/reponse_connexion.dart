//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'reponse_connexion.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class ReponseConnexion {
  /// Returns a new [ReponseConnexion] instance.
  ReponseConnexion({

     this.accessToken,

     this.refreshToken,

     this.expiresIn,

     this.tokenType,

     this.role,

     this.deuxFacteursRequis,
  });

  @JsonKey(
    
    name: r'accessToken',
    required: false,
    includeIfNull: false,
  )


  final String? accessToken;



  @JsonKey(
    
    name: r'refreshToken',
    required: false,
    includeIfNull: false,
  )


  final String? refreshToken;



  @JsonKey(
    
    name: r'expiresIn',
    required: false,
    includeIfNull: false,
  )


  final int? expiresIn;



  @JsonKey(
    
    name: r'tokenType',
    required: false,
    includeIfNull: false,
  )


  final String? tokenType;



  @JsonKey(
    
    name: r'role',
    required: false,
    includeIfNull: false,
  )


  final ReponseConnexionRoleEnum? role;



  @JsonKey(
    
    name: r'deuxFacteursRequis',
    required: false,
    includeIfNull: false,
  )


  final bool? deuxFacteursRequis;





    @override
    bool operator ==(Object other) => identical(this, other) || other is ReponseConnexion &&
      other.accessToken == accessToken &&
      other.refreshToken == refreshToken &&
      other.expiresIn == expiresIn &&
      other.tokenType == tokenType &&
      other.role == role &&
      other.deuxFacteursRequis == deuxFacteursRequis;

    @override
    int get hashCode =>
        accessToken.hashCode +
        refreshToken.hashCode +
        expiresIn.hashCode +
        tokenType.hashCode +
        role.hashCode +
        deuxFacteursRequis.hashCode;

  factory ReponseConnexion.fromJson(Map<String, dynamic> json) => _$ReponseConnexionFromJson(json);

  Map<String, dynamic> toJson() => _$ReponseConnexionToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

enum ReponseConnexionRoleEnum {
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
GESTIONNAIRE_BIENS(r'GESTIONNAIRE_BIENS'),
@JsonValue(r'COMPTABLE')
COMPTABLE(r'COMPTABLE');

const ReponseConnexionRoleEnum(this.value);

final String value;

@override
String toString() => value;
}


