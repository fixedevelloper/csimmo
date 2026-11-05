//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'proprietaire_requete.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class ProprietaireRequete {
  /// Returns a new [ProprietaireRequete] instance.
  ProprietaireRequete({

    required  this.nom,

     this.telephone,

     this.email,

     this.adresse,

     this.modeReversement,

     this.coordonneesReversement,

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
    
    name: r'modeReversement',
    required: false,
    includeIfNull: false,
  )


  final ProprietaireRequeteModeReversementEnum? modeReversement;



  @JsonKey(
    
    name: r'coordonneesReversement',
    required: false,
    includeIfNull: false,
  )


  final String? coordonneesReversement;



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
    bool operator ==(Object other) => identical(this, other) || other is ProprietaireRequete &&
      other.nom == nom &&
      other.telephone == telephone &&
      other.email == email &&
      other.adresse == adresse &&
      other.modeReversement == modeReversement &&
      other.coordonneesReversement == coordonneesReversement &&
      other.utilisateurId == utilisateurId &&
      other.notes == notes;

    @override
    int get hashCode =>
        nom.hashCode +
        telephone.hashCode +
        email.hashCode +
        adresse.hashCode +
        modeReversement.hashCode +
        coordonneesReversement.hashCode +
        utilisateurId.hashCode +
        notes.hashCode;

  factory ProprietaireRequete.fromJson(Map<String, dynamic> json) => _$ProprietaireRequeteFromJson(json);

  Map<String, dynamic> toJson() => _$ProprietaireRequeteToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

enum ProprietaireRequeteModeReversementEnum {
@JsonValue(r'VIREMENT')
VIREMENT(r'VIREMENT'),
@JsonValue(r'MOBILE_MONEY')
MOBILE_MONEY(r'MOBILE_MONEY'),
@JsonValue(r'ESPECES')
ESPECES(r'ESPECES'),
@JsonValue(r'CHEQUE')
CHEQUE(r'CHEQUE');

const ProprietaireRequeteModeReversementEnum(this.value);

final String value;

@override
String toString() => value;
}


