//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'garant_requete.g.dart';


@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class GarantRequete {
  /// Returns a new [GarantRequete] instance.
  GarantRequete({

    required  this.nom,

     this.lien,

     this.profession,

    required  this.telephone,

     this.adresse,

     this.numeroCni,
  });

  @JsonKey(
    
    name: r'nom',
    required: true,
    includeIfNull: false,
  )


  final String nom;



  @JsonKey(
    
    name: r'lien',
    required: false,
    includeIfNull: false,
  )


  final String? lien;



  @JsonKey(
    
    name: r'profession',
    required: false,
    includeIfNull: false,
  )


  final String? profession;



  @JsonKey(
    
    name: r'telephone',
    required: true,
    includeIfNull: false,
  )


  final String telephone;



  @JsonKey(
    
    name: r'adresse',
    required: false,
    includeIfNull: false,
  )


  final String? adresse;



  @JsonKey(
    
    name: r'numeroCni',
    required: false,
    includeIfNull: false,
  )


  final String? numeroCni;





    @override
    bool operator ==(Object other) => identical(this, other) || other is GarantRequete &&
      other.nom == nom &&
      other.lien == lien &&
      other.profession == profession &&
      other.telephone == telephone &&
      other.adresse == adresse &&
      other.numeroCni == numeroCni;

    @override
    int get hashCode =>
        nom.hashCode +
        lien.hashCode +
        profession.hashCode +
        telephone.hashCode +
        adresse.hashCode +
        numeroCni.hashCode;

  factory GarantRequete.fromJson(Map<String, dynamic> json) => _$GarantRequeteFromJson(json);

  Map<String, dynamic> toJson() => _$GarantRequeteToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }

}

